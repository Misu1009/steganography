package com.steganography.src;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class Steganography {
    public void insert(String cipherText, String imagePath, String outputImagePath){
        BufferedImage image = null;
        File file = null;

        try {
            file = new File(imagePath);
            image = ImageIO.read(file);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }

        int width = image.getWidth();
        int height = image.getHeight();
        int maxMessageLength = (width * height)/8;
        byte[] messageBytes = cipherText.getBytes();                     // convert message to byte
        if (messageBytes.length > maxMessageLength) {                    // calculate the limit message
            System.out.println("Overcapacity");
            return;
        }

        int messageIndex = 0, bitindex = 7, flag=0;;
        for (int y = 0; y < height; y++){
            for (int x = 0; x < width; x++){
                int pixel = image.getRGB(x, y);                                     // get ARGB -> Alpha, RGB
                int opacity = (pixel >> 24) & 0xff;                                 // make opacity full(255)
                int red = (pixel >> 16) & 0xff, green = (pixel >> 8) & 0xff;        // separate RGB
                int blue = pixel & 0xff;

                blue = (blue & 0xfe) | ((messageBytes[messageIndex] >> bitindex) & 0x01);// insert bit to the last blue's bit
                bitindex--;

                int newPixel = (opacity<<24) | (red << 16) | (green << 8) | blue;       // join the RGB color
                image.setRGB(x, y, newPixel);

                if(bitindex == -1){
                    bitindex = 7;
                    messageIndex++;
                }

                if(messageIndex == (messageBytes.length)){
                    flag =1;
                    break;
                }
            }
            if(flag==1)break;
        }
        try {                                               // Making an output image
            file = new File(outputImagePath);
            ImageIO.write(image, "png", file);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public String deInsert(String imageRead){
        String imagePath = imageRead;
        BufferedImage image = null;
        File file = null;
        try {
            file = new File(imagePath);
            image = ImageIO.read(file);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }

        int width = image.getWidth();
        int height = image.getHeight();
        int flag = 0, bitIndex = 0;         // index to keep track of the current bit being extracted
        byte messageByte =0;

        ArrayList<Byte> messageBytesList = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = image.getRGB(x, y);
                int blue = pixel & 0xff;                        // get the blue component of the pixel

                messageByte |= (blue & 0x01) << (7 - bitIndex); // extract the least significant bit and add it to the byte array
                if(messageByte==10){
                    flag = 1;
                    break;
                }
                if(bitIndex==7){
                    messageBytesList.add(messageByte);
                    bitIndex=0;
                    messageByte=0;
                    continue;
                }
                bitIndex++; // move to the next bit
            }
            if(flag==1) break;
        }
        byte[] messageBytes = new byte[messageBytesList.size()];
        for(int i=0; i<messageBytesList.size(); i++) messageBytes[i] = messageBytesList.get(i);
        String extractedMessage = new String(messageBytes);
        return extractedMessage;
    }
}
