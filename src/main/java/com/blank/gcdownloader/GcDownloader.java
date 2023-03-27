package com.blank.gcdownloader;


import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;


import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class GcDownloader {
    public void downloadBucket(String projectName, String bucketName) throws IOException {
        String zipFilePath = "pharmacies.zip";
        try (InputStream credentialsFile = getClass().getClassLoader().getResourceAsStream("credentials.json")) {
            Credentials credentials = GoogleCredentials
                    .fromStream(Objects.requireNonNull(credentialsFile));
            Storage storage = StorageOptions.newBuilder().setProjectId(projectName).setCredentials(credentials).build().getService();
            Iterable<Blob> blobs = storage.list(bucketName).iterateAll();
            System.out.println("Downloading...");
            try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFilePath))) {
                for (Blob blob : blobs) {
                    String blobName = blob.getName();
                    byte[] blobContent = blob.getContent();
                    ZipEntry zipEntry = new ZipEntry(blobName);
                    zipOutputStream.putNextEntry(zipEntry);
                    zipOutputStream.write(blobContent);
                    zipOutputStream.closeEntry();
                }
                System.out.println("Complete!");
            }
        }
    }
}
