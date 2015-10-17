package org.cgrep;

import org.cgrep.delimiters.BasicDelimiters;
import org.cgrep.delimiters.Delimiters;
import org.cgrep.io.IO;
import org.cgrep.matchers.Matcher;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * User: Oleksiy Pylypenko
 * Date: 8/2/13
 * Time: 9:41 PM
 */
public final class CGrep {
    public static final int DEFAULT_GREP_BUFFER_SIZE = 200 * 1024;
    public static final BasicDelimiters DEFAULT_DELIMITERS = new BasicDelimiters();
    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private final IO io;
    private final Matcher[] matchers;
    private final GrepBuffer buffer;
    private final Delimiters delimiters;

    public CGrep(Delimiters delimiters, GrepBuffer buffer, IO io, Matcher... matchers) {
        this.delimiters = delimiters;
        this.buffer = buffer;
        this.io = io;
        this.matchers = matchers;
    }

    public CGrep(IO io, Matcher ...matchers) {
        this(DEFAULT_DELIMITERS, new GrepBuffer(DEFAULT_GREP_BUFFER_SIZE),
                io, matchers);
    }

    interface State {
        int REST = 0;
        int WHITESPACE = 1;
        int TOKEN = 2;
    }

    private int c;
    private int state;
    private int token;
    private boolean output;
    private long inputLn;
    private long outputLn;

    public void scan() throws IOException {
        resetState();

        automaton();
    }

    private void resetState() {
        c = 0;
        state = State.WHITESPACE;
        buffer.clear();
        token = 0;
        outputLn = 0;
        inputLn = 0;
        output = false;
    }

    private void automaton() throws IOException {
        while ((c = io.readByte()) != -1) {
            if (delimiters.isNewLine(c)) {
                // * -> WHITESPACE
                onNewLine();
            } else if (state == State.REST) {
                // rest is defined whether to output or not
                if (restDataIsEOF()) {
                    break;
                }
            } else if (delimiters.isNotWhitespace(c)) {
                // WHITESPACE -> TOKEN
                processChar();
                outBuf();
            } else if (state == State.TOKEN) {
                // TOKEN -> WHITESPACE
                whiteSpaceAfterToken();
                outBuf();
            } else {
                // WHITESPACE -> WHITESPACE
                outBuf();
            }
        }
    }

    private boolean restDataIsEOF() throws IOException {
        if (output) {
            return restCopy();
        } else {
            return restEmpty();
        }
    }

    private boolean restCopy() throws IOException {
        io.putByte((byte) c);
        while ((c = io.readByte()) != -1) {
            if (delimiters.isNewLine(c)) {
                onNewLine();
                return false;
            }
            io.putByte((byte) c);
        }
        return true;
    }

    private boolean restEmpty() throws IOException {
        while ((c = io.readByte()) != -1) {
            if (delimiters.isNewLine(c)) {
                onNewLine();
                return false;
            }
        }
        return true;
    }


    private void onNewLine() throws IOException {
        state = State.WHITESPACE;

        nextToken();
        token = 0;

        inputLn++;
        if (output) {
            outputLn++;
        }

        out();
        output = false;
    }


    private void processChar() {
        if (state == State.WHITESPACE) {
            buffer.mark();
        }
        state = State.TOKEN;
    }

    private void outBuf() throws IOException {
        if (output) {
            io.putByte((byte) c);
        } else if (buffer.hasRemaining()) {
            buffer.put((byte) c);
        } else {
            buffer.clear();
            state = State.REST;
        }
    }

    private void whiteSpaceAfterToken() throws IOException {
        boolean ok = nextToken();
        state = ok ? State.WHITESPACE : State.REST;
    }

    private void out() throws IOException {
        if (output) {
            io.putByte((byte) c);
        }
    }

    private boolean nextToken() throws IOException {
        if (buffer.hasData()) {
            ByteString tokenStr = buffer.getMarkedToken();

            if (token < matchers.length) {
                Matcher matcher = matchers[token];
                if (matcher.matches(tokenStr)) {
                    if (token == matchers.length - 1) {
                        output = true;
                    }
                    token++;
                } else {
                    buffer.clear();
                    return false;
                }
            }
            if (output) {
                flushBuf();
            }
        }
        return !output;
    }

    private void flushBuf() throws IOException {
        byte[] buf = buffer.getGrepBuf();
        int pos = buffer.getPosition();
        for (int i = 0; i < pos; i++) {
            io.putByte(buf[i]);
        }
        buffer.clear();
    }

    public void report() {
        System.err.println("Input lines: " + inputLn + ", output: " + outputLn);
    }
}
