package com.undsf.util;

import java.io.*;

/**
 * Created by Arathi on 2015/9/19.
 */
public class StringFileReader extends InputStreamReader {
    private File file = null;

    public StringFileReader(String path) throws IOException{
        super(new FileInputStream(path));
        file = new File(path);
    }

    public StringFileReader(String path, String charsetName) throws IOException {
        super(new FileInputStream(path), charsetName);
        file = new File(path);
    }

    public boolean exists() {
        if (file == null) return false;
        return file.exists();
    }

    public String readAll() throws IOException {
        Long fileLength = file.length();
        char[] contentArray = new char[fileLength.intValue()];
        int readLength = read(contentArray, 0, contentArray.length);
        String content = new String(contentArray, 0, readLength);
        return content;
    }
}
