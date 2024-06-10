package com.ryoshi.PopSauce.factory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ImageFactory {

    public static BufferedImage getImage(final URL url) throws IOException {
        return ImageIO.read(url);
    }

    public static BufferedImage getImage(final String url) throws IOException {
        return ImageIO.read(new URL(url));
    }

    public static BufferedImage getImage(final File file) throws IOException {
        return ImageIO.read(file);
    }

    public static File getImageAsFile(final URI repositoryRoot){
        return new File(repositoryRoot);
    }

    public static void createImageFile(final BufferedImage image, final String fileName) throws IOException {
        ImageIO.write(image,"jpg",new File("src/main/resources/pictures/" + fileName + ".jpg"));
    }

    public static void createImageFile(final BufferedImage image, URI uri, String fileName) throws IOException {
        ImageIO.write(image,"jpg",new File(uri + "/" + fileName + ".jpg"));
    }

    public static void createImageFile(final BufferedImage image, final String repositoryRoot, final String fileName) throws IOException, URISyntaxException {
        ImageIO.write(image,"jpg",new File(new URI(repositoryRoot) + "/" + fileName + ".jpg"));
    }

    public static byte[] getImageAsBytes(final File file) throws IOException {
        return Files.readAllBytes(file.toPath());
    }

    public static byte[] getImageAsBytes(final BufferedImage image) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", stream);
        stream.flush();
        stream.close();
        return stream.toByteArray();
    }

    public static boolean isJPG(final File file){
        return file.toString().endsWith(".jpg");
    }

    public static boolean isJPG(final String filePath){
        return filePath.endsWith(".jpg");
    }

    public static List<File> getFilesInFolder(final File folder) {
        List<File> pathOfFiles = new ArrayList<>();
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                pathOfFiles.addAll(getFilesInFolder(fileEntry));
            } else {
                pathOfFiles.add(fileEntry);
            }
        }
        return pathOfFiles;
    }

    public static void convertAllFilesToJPG(){
        try {
            final File folder = new File("src/main/resources/pictures");
            List<File> allFiles = getFilesInFolder(folder);
            for (File file : allFiles) {
                if (!ImageFactory.isJPG(file)) {
                    createImageFile(getImage(file), file.getName());
                    file.delete();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
