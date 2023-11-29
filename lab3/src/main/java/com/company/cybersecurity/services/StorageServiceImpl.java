package com.company.cybersecurity.services;

import com.company.cybersecurity.config.StorageProperties;
import com.company.cybersecurity.exceptions.StorageException;
import com.company.cybersecurity.exceptions.StorageFileNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Service
public class StorageServiceImpl implements StorageService {

    private final Path decryptedRootLocation;
    private final Path encryptedRoolLocation;

    @Autowired
    public StorageServiceImpl(StorageProperties properties) {

        if (properties.getDecryptedFileUpload().trim().length() == 0) {
            throw new StorageException("File upload location can not be Empty.");
        }

        if (properties.getEncryptedFileUpload().trim().length() == 0) {
            throw new StorageException("File upload location can not be Empty.");
        }

        this.decryptedRootLocation = Paths.get(properties.getDecryptedFileUpload());
        this.encryptedRoolLocation = Paths.get(properties.getEncryptedFileUpload());
    }

    @Override
    public void storeAndEncrypt(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file.");
            }
            Path destinationFile = this.encryptedRoolLocation.resolve(
                            Paths.get(file.getOriginalFilename()))
                    .normalize().toAbsolutePath();

            if (!destinationFile.getParent().equals(this.encryptedRoolLocation.toAbsolutePath())) {
                // This is a security check
                throw new StorageException(
                        "Cannot store file outside current directory.");
            }
//            try (InputStream inputStream = file.getInputStream()) {
//                Files.copy(inputStream, destinationFile,
//                        StandardCopyOption.REPLACE_EXISTING);
//                String fileHash = OFBUtil.hashFile(String.valueOf(destinationFile));
//
//                // Rename the file with the hash
//                int extension = destinationFile.toString().lastIndexOf(".");
//                Path newDestinationFile = Paths.get(destinationFile.getParent().toString(), fileHash + "." + destinationFile.toString().substring(extension + 1));
//                Files.move(destinationFile, newDestinationFile, StandardCopyOption.REPLACE_EXISTING);
//            } catch (Exception ex) {
//                ex.getMessage();
//            }
        } catch (Exception ex) {
            ex.getMessage();
        }
    }

    @Override
    public void storeAndDecrypt(MultipartFile file) {

    }
// catch (NoSuchAlgorithmException e) {
//                throw new RuntimeException(e);
//            }
//        } catch (IOException e) {
//            throw new StorageException("Failed to store file.", e);
//        }


    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.encryptedRoolLocation, 1)
                    .filter(path -> !path.equals(this.encryptedRoolLocation))
                    .map(this.encryptedRoolLocation::relativize);
        } catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }

    }

    @Override
    public Path load(String filename) {
        return encryptedRoolLocation.resolve(filename);
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
        FileSystemUtils.deleteRecursively(encryptedRoolLocation.toFile());
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(encryptedRoolLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }
}