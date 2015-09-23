package com.undsf.util;

import java.io.*;

/**
 * Created by Arathi on 2015/9/15.
 */
public class StringFileWriter extends OutputStreamWriter {
    private File file;

    public StringFileWriter(String path, String charsetName) throws IOException {
        super(new FileOutputStream(path), charsetName);
        file = new File(path);
    }
}
