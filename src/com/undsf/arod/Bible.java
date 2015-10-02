package com.undsf.arod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 圣经
 * Created by Arathi on 2015/9/19.
 */
public class Bible {
    private String version;

    private Testament oldTestament;
    private Testament newTestament;
    private List<Book> allBooks;
    private Map<String, Integer> bookNameIndex;

    public Bible(String version){
        this.version = version;
        allBooks = new ArrayList<Book>();
    }

    public String getSection(String book, int chapter, int section){
        //TODO
        return "";
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Testament getOldTestament() {
        return oldTestament;
    }

    public void setOldTestament(Testament testament) {
        this.oldTestament = testament;
        this.allBooks.addAll(testament.getBooks());
    }

    public Testament getNewTestament() {
        return newTestament;
    }

    public void setNewTestament(Testament testament) {
        this.newTestament = testament;
        this.allBooks.addAll(testament.getBooks());
    }

    public List<Book> getAllBooks(){
        return allBooks;
    }

    public String toString(){
        return version+" (共" + allBooks.size() + "卷)";
    }
}
