package com.company.cybersecurity.services;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {

    void init();

    void storeAndEncrypt(MultipartFile file);
    void storeAndDecrypt(MultipartFile file);

    Stream<Path> loadAllEncrypted();
    Stream<Path> loadAllDecrypted();

    Path load(String filename);

    Resource loadAsResource(String filename);

    void deleteAll();

}