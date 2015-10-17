package org.cgrep.util;

import org.cgrep.io.ChannelByteIO;

import java.io.IOException;

/**
 * User: Oleksiy Pylypenko
 * Date: 8/4/13
 * Time: 8:52 PM
 */
public class CopyTool {
    public static void main(String[] args) throws IOException {
        ChannelByteIO io = ChannelByteIO.stdIO();
        try {
            io.copy();
        } finally {
            io.close();
        }

    }
}
