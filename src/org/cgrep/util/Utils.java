package org.cgrep.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * User: Oleksiy Pylypenko
 * Date: 8/3/13
 * Time: 9:41 AM
 */
public class Utils {
    public interface LineIt {
        void it(String line);
    }

    public static void readLines(File file, LineIt it) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                String trim = line.trim();
                if (trim.isEmpty()) {
                    continue;
                }
                it.it(trim);
            }
        } finally {
            reader.close();
        }
    }
}
