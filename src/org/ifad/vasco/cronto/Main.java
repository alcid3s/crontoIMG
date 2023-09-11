package org.ifad.vasco.cronto;

import com.vasco.image.exception.ImageGeneratorSDKException;
import com.vasco.image.generator.ImageGeneratorSDK;
import org.apache.commons.codec.binary.Base64;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;

public class Main {
    public static void main(String[] args) throws IOException, ImageGeneratorSDKException {
        boolean flag = true;
        if (args.length != 1) {
            System.err.println("Usage: cronto <message>");
            flag = false;
        }

        try {
            String message;
            if (flag) {
                message = args[0];
            } else {
                message = "Hello, World!";
            }

            String hexFormat = toHex(message);
            String image64 = generateCronto(hexFormat);
            byte[] image64Bytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(image64);
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(image64Bytes));
            createWindow(img);

        } catch (IOException e) {
            System.err.printf("IO Exception %s\n", e.getMessage());
            System.exit(-1);
        } catch (ImageGeneratorSDKException e) {
            System.err.printf("Onespan Image SDK Error %s\n", e.getErrorMessage());
            System.exit(-1);
        }
    }

    private static String generateCronto(String message) throws ImageGeneratorSDKException, IOException {
        BufferedImage cronto = ImageGeneratorSDK.generateCrontoSign(6, message);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        ImageIO.write(cronto, "png", buf);
        buf.flush();
        byte[] image = buf.toByteArray();
        buf.close();
        return Base64.encodeBase64String(image);
    }
    private static String toHex(String arg) {
        return String.format("%040x", new BigInteger(1, arg.getBytes()));
    }

    private static void createWindow(BufferedImage img) {
        JFrame frame = new JFrame("CrontoSign 7 Image");
        frame.setPreferredSize(new Dimension(225, 250));
        frame.setMaximumSize(new Dimension(225, 250));
        frame.setMinimumSize(new Dimension(225, 250));

        JLabel label = new JLabel();
        label.setIcon(new ImageIcon(img));
        frame.add(label);

        frame.setResizable(false);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}