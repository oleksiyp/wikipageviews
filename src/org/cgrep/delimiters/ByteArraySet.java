package org.cgrep.delimiters;

/**
* User: Oleksiy Pylypenko
* Date: 8/4/13
* Time: 8:00 PM
*/
public class ByteArraySet {
    private final int[] set;

    public ByteArraySet(String str) {
        byte []bytes = str.getBytes();
        set = new int[255];
        for (byte b : bytes) {
            set[b] = 1;
        }
    }

    public boolean in(byte b) {
        return set[b] != 0;
    }
}
