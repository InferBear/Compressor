package me.masterbear;

import java.io.*;

public class BitWriter {

    int pos = 0;
    byte cur = 0;
    int totalBit = 0;
    final int MOD = 8;
    FileOutputStream fileOutputStream;
    int totalBitOffset;
    final String filename;

    public BitWriter(final String filename) {
        this.filename = filename;
        totalBitOffset = 0;
        try {
            fileOutputStream = new FileOutputStream(filename);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private void writeInt(int val) {
        try {
            fileOutputStream.write(new byte[]{(byte) ((val & (0xFF000000))>>24)});
            fileOutputStream.write(new byte[]{(byte) ((val & (0x00FF0000))>>16)});
            fileOutputStream.write(new byte[]{(byte) ((val & (0x0000FF00))>>8)});
            fileOutputStream.write(new byte[]{(byte) ((val & (0x000000FF)))});
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public void writeFreqHead(int[] fre) {
        int length = fre.length;
        writeInt(length);
        for (int j : fre) {
            writeInt(j);
        }
        totalBitOffset = (1 + fre.length) * 4;
    }

    public void writeBit(int i) {
        if (i != 0 && i != 1) {
            throw new RuntimeException("Argument Error");
        }
        cur = (byte) ((cur << 1) | ((i == 1) ? 1 : 0));
        pos++;
        totalBit++;

        if (pos == MOD) {
            pos = 0;
            try {
                fileOutputStream.write(new byte[]{cur});
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("IO Error");
            }
            cur = 0;
        }
    }

    public void close() {
        try {
            if (pos > 0) {
                while (pos != MOD) {
                    cur = (byte) (cur << 1);
                    pos++;
                }
                fileOutputStream.write(new byte[]{cur});
            }
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeTotalBitPlaceHolder() {
        writeInt(0);
    }

    public void writeTotalBit() {
        try {
            RandomAccessFile r = new RandomAccessFile(filename, "rw");
            r.seek(totalBitOffset);
            r.writeInt(totalBit);
            r.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new Error("Write Total Bit Error");
        }
    }


    public int getTotalBit() {
        return totalBit;
    }
}
