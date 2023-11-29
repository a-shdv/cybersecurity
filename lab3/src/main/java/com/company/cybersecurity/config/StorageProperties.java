package com.company.cybersecurity.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("storage")
public class StorageProperties {

    @Value("${decryptedFileUpload.path}")
    private String decryptedFileUpload;

    @Value("${encryptedFileUpload.path}")
    private String encryptedFileUpload;

    public String getDecryptedFileUpload() {
        return decryptedFileUpload;
    }

    public void setDecryptedFileUpload(String decryptedFileUpload) {
        this.decryptedFileUpload = decryptedFileUpload;
    }

    public String getEncryptedFileUpload() {
        return encryptedFileUpload;
    }

    public void setEncryptedFileUpload(String encryptedFileUpload) {
        this.encryptedFileUpload = encryptedFileUpload;
    }
}
