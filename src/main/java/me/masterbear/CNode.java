package me.masterbear;

public class CNode {

    final public byte val;
    final int freq;
    final boolean isLeaf;

    public final CNode[] ch = new CNode[2];

    public CNode(byte val, int fre, boolean isLeaf) {
        this.val = val;
        this.freq = fre;
        this.isLeaf = isLeaf;
    }

    public int getFreq() {
        return freq;
    }

    public boolean isLeaf() {
        return isLeaf;
    }
}
