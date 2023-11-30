package com.company.cybersecurity.services;

import com.company.cybersecurity.config.StorageProperties;
import com.company.cybersecurity.exceptions.StorageException;
import com.company.cybersecurity.exceptions.StorageFileNotFoundException;
import com.company.cybersecurity.utils.OFBUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@Slf4j
public class StorageServiceImpl implements StorageService {

    private final Path decryptedRootLocation;
    private final Path encryptedRootLocation;

    @Autowired
    public StorageServiceImpl(StorageProperties properties) {

        if (properties.getDecryptedFileUpload().trim().isEmpty()) {
            throw new StorageException("File upload location can not be Empty.");
        }

        if (properties.getEncryptedFileUpload().trim().isEmpty()) {
            throw new StorageException("File upload location can not be Empty.");
        }

        this.decryptedRootLocation = Paths.get(properties.getDecryptedFileUpload());
        this.encryptedRootLocation = Paths.get(properties.getEncryptedFileUpload());
    }

    @Override
    public void storeAndEncrypt(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file.");
            }
            Path destinationFile = this.encryptedRootLocation.resolve(
                            Paths.get(Objects.requireNonNull(file.getOriginalFilename())))
                    .normalize().toAbsolutePath();

            if (!destinationFile.getParent().equals(this.encryptedRootLocation.toAbsolutePath())) {
                // This is a security check
                throw new StorageException(
                        "Cannot store file outside current directory.");
            }
//            int extensionIdx = file.getOriginalFilename().toString().lastIndexOf(".");
//            String extension = "." + file.getOriginalFilename().toString().substring(extensionIdx + 1);

            if (System.getProperty("os.name").startsWith("Windows")) {
                OFBUtil.encryptFile(file.getInputStream(), encryptedRootLocation + "\\" + file.getOriginalFilename());
            } else {
                OFBUtil.encryptFile(file.getInputStream(), encryptedRootLocation + "/" + file.getOriginalFilename());
            }

        } catch (IllegalBlockSizeException | IOException | BadPaddingException e) {
            log.info(e.getMessage());
        }
    }

    @Override
    public void storeAndDecrypt(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file.");
            }
            Path destinationFile = this.encryptedRootLocation.resolve(
                            Paths.get(Objects.requireNonNull(file.getOriginalFilename())))
                    .normalize().toAbsolutePath();

            if (!destinationFile.getParent().equals(this.encryptedRootLocation.toAbsolutePath())) {
                // This is a security check
                throw new StorageException(
                        "Cannot store file outside current directory.");
            }
            if (System.getProperty("os.name").startsWith("Windows")) {
                OFBUtil.decryptFile(encryptedRootLocation + "\\" + file.getOriginalFilename(), decryptedRootLocation + "\\" + file.getOriginalFilename());
            } else {
                OFBUtil.decryptFile(encryptedRootLocation + "/" + file.getOriginalFilename(), decryptedRootLocation + "/" + file.getOriginalFilename());
            }

        } catch (IllegalBlockSizeException | IOException | BadPaddingException e) {
            log.info(e.getMessage());
        }
    }

    @Override
    public Stream<Path> loadAllEncrypted() {
        try {
            return Files.walk(this.encryptedRootLocation, 1)
                    .filter(path -> !path.equals(this.encryptedRootLocation))
                    .map(this.encryptedRootLocation::relativize);
        } catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }

    }

    @Override
    public Stream<Path> loadAllDecrypted() {
        try {
            return Files.walk(this.decryptedRootLocation, 1)
                    .filter(path -> !path.equals(this.decryptedRootLocation))
                    .map(this.decryptedRootLocation::relativize);
        } catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }

    }

    @Override
    public Path load(String filename) {
        return encryptedRootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageFileNotFoundException(
                        "Could not read file: " + filename);

            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(encryptedRootLocation.toFile());
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(encryptedRootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }
}