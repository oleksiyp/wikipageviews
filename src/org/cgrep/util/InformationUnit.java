package org.cgrep.util;

/**
 * User: Oleksiy Pylypenko
 * Date: 8/4/13
 * Time: 8:49 AM
 */
public class InformationUnit {
    private long bytes;

    public InformationUnit(final String text) {
        class Matcher {
            final String txt = text.trim().toLowerCase();
            long value;
            boolean matched = false;
            void check(String suf, long multiplier) {
                if (matched) {
                    return;
                }
                if (txt.endsWith(suf)) {
                    String numPart = txt.substring(0, txt.length() - suf.length()).trim();
                    if (numPart.matches("[0-9]+")) {
                        long num = Long.parseLong(numPart.trim());
                        value = num * multiplier;
                        matched = true;
                    }
                }
            }
        }

        Matcher m = new Matcher();
        m.check("k", 1024);
        m.check("m", 1024 * 1024);
        m.check("g", 1024L * 1024 * 1024);
        m.check("t", 1024L * 1024 * 1024 * 1024);

        m.check("kb", 1024);
        m.check("mb", 1024 * 1024);
        m.check("gb", 1024L * 1024 * 1024);
        m.check("tb", 1024L * 1024 * 1024 * 1024);

        m.check("bib", 1);
        m.check("kib", 1024);
        m.check("mib", 1024 * 1024);
        m.check("gib", 1024L * 1024 * 1024);
        m.check("tib", 1024L * 1024 * 1024 * 1024);
        m.check("pib", 1024L * 1024 * 1024 * 1024 * 1024);
        m.check("eib", 1024L * 1024 * 1024 * 1024 * 1024 * 1024);

        m.check("b", 1);
        m.check("", 1);

        if (!m.matched) {
            throw new IllegalArgumentException("bad information unit: '" + text + "'");
        }

        bytes = m.value;
    }

    public long toBytes() {
        return bytes;
    }
}
