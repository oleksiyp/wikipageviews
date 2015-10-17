package org.cgrep;

public final class GrepBuffer {
    private int grepBufTokenStart;
    private int grepBufPos;
    private final byte[] grepBuf;
    private final int grepBufSize;

    public GrepBuffer(int grepBufSize) {
        this.grepBufSize = grepBufSize;
        this.grepBuf = new byte[grepBufSize];
    }

    @SuppressWarnings("UnusedDeclaration")
    public int getSize() {
        return grepBufSize;
    }

    public byte[] getGrepBuf() {
        return grepBuf;
    }

    public int getPosition() {
        return grepBufPos;
    }

    public boolean hasRemaining() {
        return grepBufPos < grepBuf.length;
    }

    public boolean hasData() {
        return grepBufPos != 0;
    }

    public void put(byte c) {
        grepBuf[grepBufPos++] = c;
    }

    public void clear() {
        grepBufTokenStart = 0;
        grepBufPos = 0;
    }

    public void mark() {
        grepBufTokenStart = grepBufPos;
    }

    public ByteString getMarkedToken() {
        return new ByteString(grepBuf,
                grepBufTokenStart,
                grepBufPos - grepBufTokenStart);
    }
}