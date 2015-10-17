package org.cgrep.delimiters;

public final class BasicDelimiters implements Delimiters {
    public static final String WHITESPACES = " \t";
    public static final String NEW_LINE = "\n\r";

    @Override
    public boolean isNewLine(int c) {
        return c == '\n' || c == '\r';
    }

    @Override
    public boolean isNotWhitespace(int c) {
        return !(c == ' ' || c == '\t');
    }
}