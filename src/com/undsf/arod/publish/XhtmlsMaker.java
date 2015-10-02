package com.undsf.arod.publish;

import com.undsf.arod.Bible;
import com.undsf.arod.Book;
import com.undsf.arod.Chapter;
import com.undsf.arod.collector.IBibleCrawler;
import com.undsf.arod.collector.TextPlainCrawler;
import com.undsf.util.Constants;
import com.undsf.util.StringFileReader;
import com.undsf.util.StringFileWriter;
import com.undsf.util.TemplateEngine;

import java.io.IOException;

/**
 * Created by Arathi on 2015/10/2.
 */
public class XhtmlsMaker {
    public static String BookTemplate = null;
    public static String ChapterTemplate = null;

    public TemplateEngine tengine;
    public Bible bible;

    static{
        try{
            BookTemplate = StringFileReader.ReadAll("C:\\temp\\ARoD\\assets\\book_template.xhtml");
            ChapterTemplate = StringFileReader.ReadAll("C:\\temp\\ARoD\\assets\\chapter_template.xhtml");
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public XhtmlsMaker(Bible bible) {
        this.bible = bible;
        this.tengine = new TemplateEngine();
    }

    public String spliceContent(Chapter chapter){
        StringBuffer chapterContent = new StringBuffer();
        boolean firstSection = true;
        int lastSectionID = chapter.getLastSectionID();
        for (int sectionID = 1; sectionID <= lastSectionID; sectionID++){
            String section = chapter.getSection(sectionID);
            chapterContent.append("<sup>");
            chapterContent.append(sectionID);
            chapterContent.append("</sup>");
            chapterContent.append(section);
        }
        return chapterContent.toString();
    }

    public void createBook(String path, Book book, int bookIndex) {
        StringBuffer contents = new StringBuffer();
        for (Chapter chapter : book.getChapters()) {
            tengine.clear();
            tengine.assign("ID", chapter.getID() + "");
            tengine.assign("Content", spliceContent(chapter));
            String chapterHtml = tengine.parse(ChapterTemplate);
            contents.append(chapterHtml);
        }
        tengine.assign("BookName", book.getName());
        tengine.assign("Chapters", contents.toString());
        try {
            String fileName = path + Constants.DIR_SEPARATOR + "Book";
            if (bookIndex<10){
                fileName += "0";
            }
            fileName += bookIndex;
            fileName += ".xhtml";
            System.out.println("正在生成 " + fileName + " ..."); //TODO 移除日志
            StringFileWriter sfw = new StringFileWriter(fileName, "UTF-8");
            sfw.write(tengine.parse(BookTemplate));
            sfw.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void createBooks(String path){
        //TODO 创建目录path
        //遍历书
        int bookIndex = 0;
        for (Book book : bible.getAllBooks()){
            createBook(path, book, ++bookIndex);
        }
    }

    public static void main(String[] args) throws IOException{
        IBibleCrawler crawler = new TextPlainCrawler("C:\\temp\\ARoD\\bible-cuv.txt");
        crawler.crawl();
        Bible bible = crawler.getBible();
        XhtmlsMaker maker = new XhtmlsMaker(bible);
        maker.createBooks("C:\\temp\\ARoD\\output\\xhtml");
    }
}
