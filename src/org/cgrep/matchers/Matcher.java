package org.cgrep.matchers;

import org.cgrep.ByteString;

/**
 * User: Oleksiy Pylypenko
 * Date: 8/4/13
 * Time: 7:51 AM
 */
public interface Matcher {
    boolean matches(ByteString token);
}
