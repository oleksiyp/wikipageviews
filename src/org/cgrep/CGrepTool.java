package org.cgrep;

import org.cgrep.delimiters.BasicDelimiters;
import org.cgrep.delimiters.ByteArraySet;
import org.cgrep.delimiters.ByteArraySetDelimiter;
import org.cgrep.delimiters.Delimiters;
import org.cgrep.io.ChannelByteIO;
import org.cgrep.io.IO;
import org.cgrep.matchers.Matcher;
import org.cgrep.matchers.Matchers;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Oleksiy Pylypenko
 * Date: 8/4/13
 * Time: 1:25 PM
 */
public class CGrepTool {

    private static void usage(String message) {
        System.out.println("Column grep tool");
        System.out.println("Error: " + message);
        System.out.println("Usage: cgrep column1Matcher column2Matcher ... columnNMatcher");
        System.out.println(" columnKMatcher is applied to K-th column and could be:");
        System.out.println("  - string '" + Matchers.EVERYTHING_STR + "' to match everything");
        System.out.println("  - string 'keyword1|keyword2|" + Matchers.FILE_PREFIX +
                "path3' to match union of separate keywords and files");
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            usage("Specify at least one argument");
            System.exit(1);

        }

        Config cfg = new Config();
        cfg.parse(args);
        if (cfg.matchers.length == 0) {
            usage("Specify at least one matcher");
        }

        GrepBuffer buffer = new GrepBuffer(cfg.grepBufSize);
        IO io = ChannelByteIO.stdIO();
        CGrep cg = new CGrep(cfg.delimiters,
                buffer,
                io,
                cfg.matchers);
        try {
            cg.scan();
            if (Options.verbose) {
                cg.report();
            }
        } catch (IOException e) {
            System.err.println(e);
            e.printStackTrace();
        } finally {
            io.close();
        }
    }

    private static class Config {
        String nlDelim = BasicDelimiters.NEW_LINE;
        String whDelim = BasicDelimiters.WHITESPACES;

        Delimiters delimiters = CGrep.DEFAULT_DELIMITERS;
        int grepBufSize = CGrep.DEFAULT_GREP_BUFFER_SIZE;
        Charset charset = CGrep.DEFAULT_CHARSET;
        Matcher[] matchers;

        private void parse(String[] args) throws IOException {
            List<String> matcherArgs = new ArrayList<String>(args.length);
            for (String arg : args) {
                arg = arg.trim();
                if (arg.equals("-v")) {
                    Options.verbose = true;
                } else if (arg.startsWith("-d")) {
                    nlDelimitersArg(arg);
                } else if (arg.startsWith("-d")) {
                    delimitersArg(arg);
                } else if (arg.startsWith("-s")) {
                    grepBufSizeFromArg(arg);
                } else if (arg.startsWith("-c")) {
                    charsetFromArg(arg);
                } else {
                    matcherArgs.add(arg);
                }
            }


            buildMatchers(matcherArgs);
        }

        private void delimitersArg(String arg) {
            whDelim = arg.substring(2).trim();
            buildDelimiters();
        }

        private void nlDelimitersArg(String arg) {
            nlDelim = arg.substring(2).trim();
            buildDelimiters();
        }

        private void buildDelimiters() {
            if (whDelim.equals(BasicDelimiters.WHITESPACES)
                    && nlDelim.equals(BasicDelimiters.NEW_LINE)) {
                delimiters = new BasicDelimiters();
            } else {
                delimiters = new ByteArraySetDelimiter(
                        new ByteArraySet(whDelim),
                        new ByteArraySet(nlDelim));
            }
        }

        private void grepBufSizeFromArg(String arg) {
            try {
                grepBufSize = Integer.parseInt(arg.substring(2).trim());
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException(arg, ex);
            }
        }

        private void charsetFromArg(String arg) {
            String val = arg.substring(2).trim();
            try {
                charset = Charset.forName(val);
            } catch (UnsupportedCharsetException ex) {
                throw new IllegalArgumentException(arg, ex);
            }
        }

        private void buildMatchers(List<String> matcherArgs) throws IOException {
            Matcher[] matchers = new Matcher[matcherArgs.size()];

            int i = 0;
            for (String arg : matcherArgs) {
                matchers[i] = Matchers.fromExpression(charset, arg);
                if (Options.verbose) {
                    System.err.println("COLUMN" + (i+1) + " matches " + matchers[i]);
                }
                i++;
            }

            this.matchers = matchers;
        }
    }

}
