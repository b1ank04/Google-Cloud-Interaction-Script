package com.blank.gcdownloader;

import org.springframework.stereotype.Component;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;

@Component
public class ImageFilter {

    public void filterImages(String zipFileName) throws IOException {
        String filteredZipFilePath = zipFileName.substring(0, zipFileName.indexOf(".")) + "_filtered";
        System.out.println("Filtering...");
        processImagesInZipFile(zipFileName, filteredZipFilePath);
        System.out.println("Complete!");
    }

    private void processImagesInZipFile(String zipFileName, String destDirectory) throws IOException {
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        try (ZipFile zipFile = new ZipFile(new File(zipFileName))) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (!entry.isDirectory() && isImage(entry.getName())) {
                    BufferedImage image = ImageIO.read(zipFile.getInputStream(entry));
                    if (image != null && isImageValid(image, entry)) {
                        String filePath = destDirectory + File.separator + entry.getName();
                        File file = new File(filePath);
                        file.getParentFile().mkdirs();
                        ImageIO.write(image, getFormatName(entry.getName()), new FileOutputStream(file));
                    }
                }
            }
        }
    }

    private boolean isImage(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
        return extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg") || extension.equalsIgnoreCase("png");
    }

    private boolean isImageValid(BufferedImage image, ZipEntry entry) {
        Dimension dimension = new Dimension(image.getWidth(), image.getHeight());
        long sizeBytes = entry.getSize();
        return dimension.width <= 3000 && dimension.width >= 840
                && dimension.height <= 3000 && dimension.height >= 665
                && sizeBytes >= 180 * 1024 && sizeBytes <= 2 * 1024 * 1024;
    }

    private static String getFormatName(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
        return extension.equalsIgnoreCase("jpg") ? "jpeg" : extension;
    }
}
