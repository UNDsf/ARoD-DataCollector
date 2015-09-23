package com.undsf.util;

import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;

import java.io.File;

/**
 * Created by Arathi on 2015/6/21.
 */
public class FileName {
    protected String path;
    protected String name;
    protected String extName;

    public FileName(String full){
        int index;
        //查找最后一个分隔符
        index = full.lastIndexOf(Constants.DIR_SEPARATOR);
        if (index>=0){
            path = full.substring(0, index);
        }
        String fileName = full.substring(index+1);
        index = fileName.lastIndexOf(".");
        extName = fileName.substring(index+1);
        name = fileName.substring(0, index);
    }

    public String getFullName(){
        StringBuffer fullName = new StringBuffer(path);
        if (path.endsWith(Constants.DIR_SEPARATOR)==false){
            fullName.append(Constants.DIR_SEPARATOR);
        }
        fullName.append(getFileName());
        return fullName.toString();
    }

    public File getFile(){
        String fullName = getFullName();
        File file = new File(fullName);
        return file;
    }

    public String getFileName(){
        StringBuffer fileName = new StringBuffer();
        fileName.append(name);
        if (extName!=null && extName.length()>0) {
            fileName.append(".");
            fileName.append(extName);
        }
        return fileName.toString();
    }

    public String getExtName(){
        return extName;
    }

    public String getName(){
        return name;
    }

    public String getPath(){
        return path;
    }
}
