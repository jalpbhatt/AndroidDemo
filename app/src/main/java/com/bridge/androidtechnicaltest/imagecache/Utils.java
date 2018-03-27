package com.bridge.androidtechnicaltest.imagecache;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Utility class for supporting the file operations
 */
public class Utils {

    private static final int BUFFER_SIZE = 1024;

    /**
     *  Copy stream data bytes to output stream
     * @param is input stream object
     * @param os output stream object
     */
    public static void copyStream(InputStream is, OutputStream os) {
        try {
            byte[] bytes = new byte[BUFFER_SIZE];
            for (; ; ) {
                int count = is.read(bytes, 0, BUFFER_SIZE);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
        }
    }
}