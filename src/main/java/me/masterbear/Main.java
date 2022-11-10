package me.masterbear;

public class Main {

    public static void main(String[] args) {
        final String from = "bible.txt";
        final String to = "bible_copy.txt";
        Compressor compressor = new Compressor(from);
        compressor.compress("zip.dat");
        DeCompressor deCompressor = new DeCompressor("zip.dat");
        deCompressor.decompress(to);
    }
}
