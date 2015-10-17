package org.cgrep.matchers;

import org.cgrep.ByteString;

import java.util.HashSet;
import java.util.Set;

public final class SetMatcher implements Matcher {
    private final Set<ByteString> byteStrings;

    public SetMatcher(Set<ByteString> byteStrings) {
        this.byteStrings = new HashSet<ByteString>(byteStrings);
    }

    public boolean matches(ByteString token) {
        return byteStrings.contains(token);
    }

    public String toString() {
        return "set of " + byteStrings.size() + " unique words";
    }
}