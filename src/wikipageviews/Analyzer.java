package wikipageviews;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPInputStream;

public class Analyzer {
    private String prefix;

    public Analyzer(String prefix) {
        this.prefix = prefix;
    }

    public static void main(String[] args) throws IOException {
        File file = new File(args[0]);
        String[] lst = file.list();
        if (lst == null) {
            return;
        }
        Arrays.sort(lst);
        for (String prefix : new String[]{"ru", "en", "uk", "pl"}) {
            System.out.println(prefix);
            Analyzer an = new Analyzer(prefix);
            for (String st : lst) {
                if (!st.endsWith(".gz")) {
                    continue;
                }
                System.out.print(st);
                System.out.print(' ');
                an.readLines(new File(st));
            }
        }
    }

    private void readLines(File file) throws IOException {
        byte buf[] = new byte[1024 * 100];
        ByteBuffer line = ByteBuffer.wrap(buf);
        int off = 0, len;
        try (FileInputStream fin = new FileInputStream(file);
                    GZIPInputStream in = new GZIPInputStream(fin)) {
            int cnt = 0;
            while ((len = in.read(buf, off, buf.length - off)) > 0) {
                int l = len + off;
                off = 0;
                for (int i = 0; i < l; i++) {
                    if (buf[i] == '\n') {
                        if (i <= 3) {
                            off = i + 1;
                            continue;
                        }
                        line.limit(i);
                        line.position(off);
                        lineRead(line);
                        cnt++;
                        off = i + 1;
                    }
                }
                System.arraycopy(buf, off, buf, 0, l - off);
                off = l - off;
            }
            ggg();
        }
    }

    Set<String> memory = new HashSet<>();
    private void ggg() {
        Collections.sort(q);
        for (int i = 0; i < 1 && i < q.size(); i++) {
            Item item = q.get(i);
            String d = item.value;
            String d1 = decode(d, "UTF-8");
            String d2 = decode(d, "CP1251");
            String d3 = decode(d, "KOI8");
            String d4 = decode(d, "ASCII");
            String s = winEncoding(new String[] { d1, d2, d3, d4 });

            System.out.println(s + " " + item.views);
            memory.add(d);
        }
        q.clear();
    }

    private String winEncoding(String[] strs) {
        Arrays.sort(strs, new Comparator<String>() {
            int cntLetters(String s) {
                boolean[]flg = new boolean[2000];
                int l = 0;
                for (char c : s.toCharArray()) {
                    c = Character.toLowerCase(c);
                    boolean let = false;
                    if (c >= 'a' && c <= 'z') {
                        let = true;
                    } else if (c >= 'а' && c <= 'я') {
                        let = true;
                    }
                    if (let) {
                        l++;
                        if (!flg[c]) {
                            l++;
                        }
                        flg[c] = true;
                    }

                }
                return l;
            }

            @Override public int compare(String o1, String o2) {
                int l = cntLetters(o1);
                int ll = cntLetters(o2);
                return -Integer.compare(l, ll);
            }
        });
        return strs[0];
    }

    private String decode(String d, String param) {
        try {
            d = URLDecoder.decode(d, param);
        } catch (Exception e) {
        }
        return d;
    }

    List<Item> q = new ArrayList<>();

    private void lineRead(ByteBuffer line) {
        int tok1End = skipToken(line, 0);
        int tok2Start = skipSpace(line, tok1End);
        int tok2End = skipToken(line, tok2Start);
        if (tok2End == tok2Start) {
            return;
        }
        int tok3Start = skipSpace(line, tok2End);
        int tok3End = skipToken(line, tok3Start);
        if (tok3Start == tok3End) {
            return;
        }
        String pref = getStr(line, 0, tok1End);
        if (!pref.startsWith(prefix)) {
            return;
        }
        long num = 0;
        for (int i = tok3Start; i < tok3End; i++) {
            num *= 10;
            byte c = line.get(line.position() + i);
            if (!Character.isDigit(c)) {
                break;
            }
            num += c - '0';

        }
        if (num < 50) {
            return;
        }
        String str = getStr(line, tok2Start, tok2End);
        if (memory.contains(str)) {
            return;
        }

        Item e = new Item();
        e.views = num;
        e.value = str;
        q.add(e);
    }

    private String getStr(ByteBuffer line, int tok2Start, int tok2End) {
        String str = null;
        try {
            str = new String(line.array(), line.position() + tok2Start, tok2End - tok2Start, "ASCII");
        } catch (UnsupportedEncodingException e) {
        }
        return str;
    }

    private int skipSpace(ByteBuffer line, int i) {
        int p = line.position();
        for (; i <= line.limit() - p; i++) {
            if (i == line.limit() - p) {
                break;
            }
            if (line.get(p + i) != ' ') {
                break;
            }
        }
        return i;
    }

    private int skipToken(ByteBuffer line, int i) {
        int p = line.position();
        for (; i <= line.limit() - p; i++) {
            if (i == line.limit() - p) {
                break;
            }
            byte c = line.get(p + i);
            if (c == ' ') {
                break;
            }
        }
        return i;
    }

    private static class Item implements Comparable<Item> {
        public String value;
        public long views;

        @Override public int compareTo(Item o) {
            return -Long.compare(views, o.views);
        }
    }
}
