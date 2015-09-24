package com.undsf.util;

import java.io.*;

/**
 * Created by Arathi on 2015/6/18.
 */
public class FileUtil {
    /**
     * 复制文件
     * @param readfile 源文件路径
     * @param writeFile 目标文件路径
     * @return
     */
    public static boolean cp(String readfile,String writeFile) {
        try {
            FileInputStream input = new FileInputStream(readfile);
            FileOutputStream output = new FileOutputStream(writeFile);
            int read = input.read();
            while ( read != -1 ) {
                output.write(read);
                read = input.read();
            }
            input.close();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean cp(File src, File dest){
        return cp(src.getAbsolutePath(), dest.getAbsolutePath());
    }

    public static String getRelativePath(String filePath, String refer) {
        if (filePath.startsWith(refer)){
            int referLength = refer.length();
            return filePath.substring(referLength);
        }
        //TODO 寻找公共子串
        return null;
    }

    public static String getRelativePath(File file, File refer){
        return getRelativePath(file.getAbsolutePath(), refer.getAbsolutePath());
    }
}
