package org.cgrep.io;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public final class ChannelByteIO implements IO {
    private final ReadableByteChannel in;
    private final WritableByteChannel out;
    private final ByteBuffer inBuffer;
    private final ByteBuffer outBuffer;

    public ChannelByteIO(ReadableByteChannel in, WritableByteChannel out) {
        this.in = in;
        this.out = out;

        inBuffer = ByteBuffer.allocateDirect(8 * 1024 * 1024);
        outBuffer = ByteBuffer.allocateDirect(512 * 1024);

        inBuffer.flip();
    }

    public int readByte() throws IOException {
        if (!inBuffer.hasRemaining()) {
            if (fillReadBufferAndIsEOF()) {
                return -1;
            }
        }
        return inBuffer.get();
    }

    private boolean fillReadBufferAndIsEOF() throws IOException {
        int res;

        inBuffer.clear();
        do {
            res = in.read(inBuffer);
        } while (res == 0);

        if (res == -1) {
            return true;
        }

        inBuffer.flip();

        return false;
    }

    public void putByte(byte b) throws IOException {
        flushWriteBuffer();
        outBuffer.put(b);
    }

    private void flushWriteBuffer() throws IOException {
        while (!outBuffer.hasRemaining()) {
            outBuffer.flip();
            out.write(outBuffer);
            outBuffer.compact();
        }
    }

    public static ChannelByteIO stdIO() {
        FileInputStream input = new FileInputStream(FileDescriptor.in);
        FileOutputStream output = new FileOutputStream(FileDescriptor.out);
        FileChannel in = input.getChannel();
        FileChannel out = output.getChannel();
        return new ChannelByteIO(in, out);
    }

    @Override
    public void close() throws IOException {
        out.close();
        in.close();
    }

    public void copy() throws IOException {
        if (in instanceof FileChannel) {
            ((FileChannel)in).
                    transferTo(0, Long.MAX_VALUE, out);
        } else {
            throw new UnsupportedOperationException("no copy implementation for " + in);
        }
    }
}