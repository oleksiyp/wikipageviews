package org.cgrep.matchers;

import org.cgrep.ByteString;
import org.cgrep.util.Utils;

import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

/**
* User: Oleksiy Pylypenko
* Date: 8/4/13
* Time: 7:52 AM
*/
public class HashSetMatcherBuilder implements Utils.LineIt {
    private final Charset charset;
    private final Set<ByteString> set = new HashSet<ByteString>(100000);

    public HashSetMatcherBuilder(Charset charset) {
        this.charset = charset;
    }

    @Override
    public void it(String line) {
        ByteString bs = toByteString(line);
        set.add(bs);
    }

    private ByteString toByteString(String line) {
        return new ByteString(line, charset);
    }

    public SetMatcher build() {
        return new SetMatcher(set);
    }
}
