package com.undsf.arod;

import java.util.ArrayList;
import java.util.List;

/**
 * 圣约书
 * Created by Arathi on 2015/9/19.
 */
public class Testament {
    private String name;
    private List<Book> books;

    public Testament(String name){
        this.name = name;
        books = new ArrayList<Book>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addBook(Book book) {
        books.add(book);
    }

    public List<Book> getBooks(){
        return books;
    }

    public String toString(){
        return name + " (共" + books.size() + "卷)";
    }
}
