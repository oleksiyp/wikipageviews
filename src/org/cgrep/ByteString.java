package org.cgrep;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
* User: Oleksiy Pylypenko
* Date: 8/3/13
* Time: 3:03 PM
*/
public final class ByteString {
    private final byte[] bytes;
    private final int off;
    private final int len;

    public ByteString(byte[] bytes, int off, int len) {
        this.bytes = bytes;
        this.off = off;
        this.len = len;
    }

    public ByteString(String str, Charset charset) {
        byte []arr = str.getBytes(charset);
        off = 0;
        bytes = arr;
        len = arr.length;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ByteString that = (ByteString) o;

        if (len != that.len)
        {
            return false;
        }

        for (int i = 0, a = off, b = that.off; i < len; i++, a++, b++) {
            if (bytes[a] != that.bytes[b]) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        if (bytes == null)
            return 0;

        int result = 1;
        int end = off + len;
        for (int i = off; i < end; i++) {
            result = 31 * result + bytes[i];
        }

        return result ^ len;
    }

    @SuppressWarnings("UnusedDeclaration")
    public int length() {
        return len;
    }

    @Override
    public String toString() {
        try {
            return new String(bytes, off, len, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return Arrays.toString(Arrays.copyOfRange(bytes, off, off + len));
        }
    }
}
