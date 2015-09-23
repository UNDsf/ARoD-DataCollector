package com.undsf.arod;

import java.util.ArrayList;
import java.util.List;

/**
 * 书
 * Created by Arathi on 2015/9/19.
 */
public class Book {
    private String name;
    private List<Chapter> chapters;
    private String url;

    public Book(String name){
        this.name = name;
        chapters = new ArrayList<Chapter>();
    }

    public String getName(){
        return name;
    }

    public void addChapter(Chapter chapter){
        chapters.add(chapter);
    }

    public String toString(){
        return name + " (计" + chapters.size() + "章)";
    }

    public List<Chapter> getChapters(){
        return chapters;
    }
}
