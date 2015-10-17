package org.cgrep.util;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

/**
 * User: Oleksiy Pylypenko
 * Date: 8/3/13
 * Time: 12:12 PM
 */
public class RandomBytesTool {
    public static final String ABC = " \nabcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final WritableByteChannel out;
    private final long maxLen;
    private Rnd random;

    public RandomBytesTool(WritableByteChannel out, long maxLen) {
        this.out = out;
        this.maxLen = maxLen;
    }

    public static void main(String[] args) throws IOException {
        long seed = args.length == 0 ?
                System.currentTimeMillis() :
                Long.parseLong(args[0]);

        long len = args.length < 2
                        ? Long.MAX_VALUE
                        : new InformationUnit(args[1]).toBytes();

        WritableByteChannel out = new FileOutputStream(FileDescriptor.out).getChannel();

        RandomBytesTool tool = new RandomBytesTool(out, len);
        tool.setRandom(new Rnd(seed));

        try {
            tool.generate();
        } catch (IOException e) {
            out.close();
        }
    }

    private void generate() throws IOException {
        byte []buf = new byte[20 * 1024 * 1024];
        byte []abc = ABC.getBytes("utf-8");
        long outB = 0;
        while (!Thread.interrupted() && outB < maxLen) {
            int need = (int) Math.min(maxLen - outB, buf.length);

            for (int i = 0; i < need; i++) {
                buf[i] = abc[random.nextInt(abc.length)];
            }
            ByteBuffer b = ByteBuffer.wrap(buf, 0, need);
            outB += need;
            out.write(b);
        }
    }

    public void setRandom(Rnd random) {
        this.random = random;
    }
}
