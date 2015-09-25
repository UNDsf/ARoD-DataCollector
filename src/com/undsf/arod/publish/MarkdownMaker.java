package com.undsf.arod.publish;

import com.undsf.arod.Bible;
import com.undsf.arod.Book;
import com.undsf.arod.Chapter;
import com.undsf.arod.Testament;
import com.undsf.arod.collector.IBibleCrawler;
import com.undsf.arod.collector.TextPlainCrawler;
import com.undsf.util.StringFileWriter;

import java.io.IOException;

/**
 * Created by Arathi on 2015/9/24.
 */
public class MarkdownMaker {
    private Bible bible;
    public static final String LINE_BREAK = "\r\n";

    public MarkdownMaker(String path){
        try {
            IBibleCrawler crawler = new TextPlainCrawler(path);
            crawler.crawl();
            bible = crawler.getBible();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private void genChapter(StringBuffer buffer, Chapter chapter){
        buffer.append("####");
        buffer.append(chapter.getID());
        buffer.append(LINE_BREAK);

        int sectionID = 0;
        for (String section : chapter.getSections()){
            buffer.append("<sup>");
            buffer.append(++sectionID);
            buffer.append("</sup>");
            buffer.append(section);
            buffer.append("  ");
            buffer.append(LINE_BREAK);
        }
    }

    private void genBook(StringBuffer buffer, Book book){
        buffer.append("###");
        buffer.append(book.getName());
        buffer.append(LINE_BREAK);

        for (Chapter chap : book.getChapters()){
            genChapter(buffer, chap);
        }
    }

    private void genTestament(StringBuffer buffer, Testament testament){
        buffer.append("##");
        buffer.append(testament.getName());
        buffer.append(LINE_BREAK);

        for (Book book : testament.getBooks()){
            genBook(buffer, book);
        }
    }

    public String getMarkdown(){
        StringBuffer buffer = new StringBuffer();

        buffer.append("#");
        buffer.append(bible.getVersion());
        buffer.append(LINE_BREAK);

        genTestament(buffer, bible.getOldTestament() );
        genTestament( buffer, bible.getNewTestament() );

        return buffer.toString();
    }

    public static void main(String[] args) {
        MarkdownMaker mm = new MarkdownMaker("C:\\temp\\ARoD\\bible-cuv.txt");
        String markdown = mm.getMarkdown();
        //System.out.println(markdown);
        try {
            StringFileWriter sfr = new StringFileWriter("C:\\temp\\ARoD\\output\\bible-cuv.md", "UTF-8");
            sfr.write(markdown);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
