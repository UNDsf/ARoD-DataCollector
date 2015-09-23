package com.undsf.arod.publish;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubReader;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by Arathi on 2015/9/22.
 */
public class EpubMaker {
    public static void main(String[] args) {
        //Book epubBook = new Book();
        try {
            EpubReader epubReader = new EpubReader();
            Book book = epubReader.readEpub(new FileInputStream("D:\\Calibre\\John Schember\\Calibre Quick Start Guide (1)\\Calibre Quick Start Guide - John Schember.epub"));
            //Resource resource;
            System.out.println(book);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
