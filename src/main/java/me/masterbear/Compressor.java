package me.masterbear;


import java.io.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

public class Compressor {

    final static int TABLE_LENGTH = 256;
    final static int OFFSET = 128;
    final String filename;
    final HashMap<Byte, String> encodeTable = new HashMap<>();
    final HashMap<String, Byte> decodeTable = new HashMap<>();
    int[] fre = new int[256];
    int totalOriginByte;
    int totalCompressedBit;

    CNode root;

    public Compressor(String file) {
        filename = file;
    }

    private void init() {
        genFreTable();
        generateTree();
        processTree();
    }

    BufferedInputStream getReader(String filename) {
        FileInputStream f = null;
        try {
            f = new FileInputStream(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return new BufferedInputStream(f);
    }


    void genFreTable() {
        BufferedInputStream r = getReader(filename);
        byte[] buf = new byte[1024];
        int sz;
        int total = 0;
        try {
            while ((sz = r.read(buf)) > 0) {
                for (int i = 0; i < sz; i++) {
                    fre[buf[i] + OFFSET]++;
                }
                total += sz;
            }
            r.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("IO Error");
            System.exit(-1);
        }
        totalOriginByte = total;
        System.out.println("Total:" + total + " bytes");
    }

    private void generateTree() {
        PriorityQueue<CNode> q = new PriorityQueue<>(Comparator.comparing(CNode::getFreq));
        for (int i = 0; i < TABLE_LENGTH; i++) {
            if (fre[i] == 0) continue;
            q.add(new CNode((byte) (i - OFFSET), fre[i], true));
        }

        while (q.size() != 1) {
            CNode left = q.poll();
            CNode right = q.poll();
            assert left != null;
            assert right != null;
            CNode t = new CNode((byte) 0, left.freq + right.freq, false);
            t.ch[0] = left;
            t.ch[1] = right;
            q.add(t);
        }
        root = q.peek();
    }

    void processTree() {
        dfs(root, "");
        for (byte b : encodeTable.keySet()) {
            decodeTable.put(encodeTable.get(b), b);
        }
    }

    void dfs(CNode root, String cur) {
        if (root == null) {
            return;
        }
        if (root.isLeaf) {
            encodeTable.put(root.val, cur);
            //return;
        }

        dfs(root.ch[0], cur + "0");
        dfs(root.ch[1], cur + "1");
    }

    public void compress(String to) {
        init();
        BufferedInputStream r = getReader(filename);
        BitWriter bitWriter = new BitWriter(to);
        bitWriter.writeFreqHead(fre);
        bitWriter.writeTotalBitPlaceHolder(); // write 0 first
        byte[] buf = new byte[1024];
        int sz;
        try {
            while ((sz = r.read(buf)) > 0) {
                for (int i = 0; i < sz; i++) {
                    String e = encodeTable.get(buf[i]);
                    assert e != null && e.length() != 0;
                    for (int j = 0; j < e.length(); j++) {
                        if (e.charAt(j) == '0') {
                            bitWriter.writeBit(0);
                        } else {
                            bitWriter.writeBit(1);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("IO Error");
            System.exit(-1);
        }
        bitWriter.close();
        totalCompressedBit = bitWriter.getTotalBit();
        System.out.println("Compressed bit: " + bitWriter.getTotalBit());
        bitWriter.writeTotalBit();
    }
}

