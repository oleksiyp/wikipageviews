package org.cgrep.delimiters;

/**
 * User: Oleksiy Pylypenko
 * Date: 8/4/13
 * Time: 7:54 PM
 */
public class ByteArraySetDelimiter implements Delimiters {
    private final ByteArraySet newLine;
    private final ByteArraySet whitespace;

    public ByteArraySetDelimiter(ByteArraySet newLine,
                                 ByteArraySet whitespace) {
        this.newLine = newLine;
        this.whitespace = whitespace;
    }

    @Override
    public boolean isNewLine(int c) {
        return newLine.in((byte) c);
    }

    @Override
    public boolean isNotWhitespace(int c) {
        return !whitespace.in((byte) c);
    }
}
