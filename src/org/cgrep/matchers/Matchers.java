package org.cgrep.matchers;

import org.cgrep.ByteString;
import org.cgrep.util.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

/**
 * User: Oleksiy Pylypenko
 * Date: 8/4/13
 * Time: 10:18 AM
 */
public class Matchers {
    private static final MatchEverything MATCH_EVERYTHING = new MatchEverything();

    public static final String EVERYTHING_STR = "-";
    public static final String FILE_PREFIX = "-f";

    public static Matcher fromExpression(Charset charset, String expr) throws IOException {
        expr = expr.trim();
        HashSetMatcherBuilder builder = new HashSetMatcherBuilder(charset);
        for (String val : expr.split("\\|")) {
            val = val.trim();
            if (val.equals(EVERYTHING_STR)) {
                return everything();
            } else if (val.startsWith(FILE_PREFIX)) {
                String filename = val.substring(FILE_PREFIX.length());
                File file = new File(filename);
                Utils.readLines(file, builder);
            } else {
                builder.it(val);
            }
        }
        return builder.build();
    }


    public static SetMatcher set(Charset charset, String ...lines) {
        Set<ByteString> set = new HashSet<ByteString>(lines.length);
        for (String line : lines) {
            set.add(new ByteString(line, charset));
        }
        return new SetMatcher(set);
    }

    @SuppressWarnings("UnusedDeclaration")
    public static SetMatcher set(Charset charset, File file)
            throws IOException {
        HashSetMatcherBuilder builder = new HashSetMatcherBuilder(charset);
        Utils.readLines(file, builder);
        return builder.build();
    }

    public static Matcher everything() {
        return MATCH_EVERYTHING;
    }

    private static class MatchEverything implements Matcher {
        @Override
        public boolean matches(ByteString token) {
            return true;
        }

        @Override
        public String toString() {
            return "everything";
        }
    }
}
