package com.steganography.src;

public class App {
    public static void main(String[] args) {
        Steganography stegano = new Steganography();

        String message = "Hi there, i hope you are always happy."+"\n";
        String image_path = "C:\\Users\\User\\Pictures\\cover.png";  // path of the image
        String output_path = "C:\\Users\\User\\Pictures\\result.png"; // path to generate the steganography image

        stegano.insert(message, image_path, output_path);   // making a steganography image
        System.out.println(stegano.deInsert(output_path));  // read a text in the steganography image
    }
}
