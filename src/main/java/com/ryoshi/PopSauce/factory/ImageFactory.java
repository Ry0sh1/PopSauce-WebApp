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

public class ImageFactory {

    public static BufferedImage getImage(URL url) throws IOException {
        return ImageIO.read(url);
    }

    public static BufferedImage getImage(String url) throws IOException {
        return ImageIO.read(new URL(url));
    }

    public static BufferedImage getImage(File file) throws IOException {
        return ImageIO.read(file);
    }

    public static File getImageAsFile(URI repositoryRoot){
        return new File(repositoryRoot);
    }

    public static void createImageFile(BufferedImage image, String fileName) throws IOException {
        ImageIO.write(image,"jpg",new File("src/main/resources/pictures/" + fileName + ".jpg"));
    }

    public static void createImageFile(BufferedImage image, URI uri, String fileName) throws IOException {
        ImageIO.write(image,"jpg",new File(uri + "/" + fileName + ".jpg"));
    }

    public static void createImageFile(BufferedImage image, String repositoryRoot, String fileName) throws IOException, URISyntaxException {
        ImageIO.write(image,"jpg",new File(new URI(repositoryRoot) + "/" + fileName + ".jpg"));
    }

    public static byte[] getImageAsBytes(File file) throws IOException {
        return Files.readAllBytes(file.toPath());
    }

    public static byte[] getImageAsBytes(BufferedImage image) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", stream);
        stream.flush();
        stream.close();
        return stream.toByteArray();
    }

    public static boolean isJPG(File file){
        return file.toString().endsWith(".jpg");
    }

}
