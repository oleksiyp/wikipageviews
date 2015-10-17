package org.cgrep.io;

import java.io.Closeable;
import java.io.IOException;

/**
 * User: Oleksiy Pylypenko
 * Date: 8/3/13
 * Time: 3:37 PM
 */
public interface IO extends Closeable {
    void putByte(byte b) throws IOException;

    int readByte() throws IOException;
}
