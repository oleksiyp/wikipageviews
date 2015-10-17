package org.cgrep.delimiters;

/**
 * User: Oleksiy Pylypenko
 * Date: 8/4/13
 * Time: 11:28 AM
 */
public interface Delimiters {
    boolean isNewLine(int c);

    boolean isNotWhitespace(int c);
}
