package org.cgrep.util;

/**
 * User: Oleksiy Pylypenko
 * Date: 8/3/13
 * Time: 9:40 PM
 */
public class Rnd {
    private long seed;

    private final static long multiplier = 0x5DEECE66DL;
    private final static long addend = 0xBL;
    private final static long mask = (1L << 48) - 1;

    public Rnd(long seed) {
        this.seed = (seed ^ multiplier) & mask;
    }

    private int next31() {
        seed = (seed * multiplier + addend) & mask;
        return (int)(seed >>> 17);
    }

    public int nextInt(int n) {
        if ((n & -n) == n)  // i.e., n is a power of 2
            return (int)((n * (long) next31()) >> 31);

        int bits, val;
        do {
            bits = next31();
            val = bits % n;
        } while (bits - val + (n-1) < 0);
        return val;
    }
}
