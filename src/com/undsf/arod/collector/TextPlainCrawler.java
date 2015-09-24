package com.undsf.arod.collector;

import com.undsf.arod.Bible;
import com.undsf.arod.Book;
import com.undsf.arod.Chapter;
import com.undsf.arod.Testament;
import com.undsf.util.StringFileReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Arathi on 2015/09/24.
 */
public class TextPlainCrawler implements IBibleCrawler {
    public static final Pattern BookPattern = Pattern.compile("^\\d+\\.\\s*(.*)\\s*$");
    public static final Pattern ChapterPattern = Pattern.compile("^\\d+\\.(\\d+)\\.\\s*第(\\d+)章\\s*$");
    public static final Pattern SectionIDPattern = Pattern.compile("(\\d+)[^\\d]");

    public static Log logger = LogFactory.getLog(TextPlainCrawler.class);

    private String[] lines;
    private Bible bible;

    public TextPlainCrawler(String path) throws IOException{
        StringFileReader sfr = new StringFileReader(path, "UTF-8");
        String content = sfr.readAll();
        this.lines = content.split("\r\n");
    }

    public Chapter createChapter(String bookName, int id, StringBuffer content){
        if (content == null || content.length()==0){
            return null;
        }
        Chapter chapter = new Chapter(id);
        //int firstSectionIDofChapter = 0;
        boolean firstSection = true;
        int lastSectionID = 0;
        int startAt = 0;
        int endAt = 0;
        int cursor = 0;
        Matcher sidMacher = SectionIDPattern.matcher(content);
        while (sidMacher.find(cursor)){
            String sectionIDstr = sidMacher.group(1);
            int sectionID = Integer.parseInt(sectionIDstr);
            //检查sectionID是否为合理的节号
            if (lastSectionID+1 == sectionID) {
                if (lastSectionID>0) {
                    endAt = sidMacher.start();
                    String sectionContent = content.substring(startAt, endAt);
                    chapter.addSection(sectionContent);
                }
                startAt = sidMacher.start() + sectionIDstr.length();
                lastSectionID = sectionID;
            }
            else{
                logger.warn("[" + bookName + " " + id + ":" + lastSectionID + "] 上一节的节号为"+lastSectionID+"，新获取到的数值"+sectionIDstr+"不为下一节号！");
            }
            cursor = sidMacher.end();
        }
        String sectionContent = content.substring(startAt);
        //System.out.println("<sup>" + lastSectionID + "</sup>" + sectionContent);
        chapter.addSection(sectionContent);
        return chapter;
    }

    @Override
    public void crawl() {
        bible = new Bible("合和本");
        Testament ot = new Testament("旧约");
        Testament nt = new Testament("新约");
        Testament testament = ot;
        StringBuffer chapterContent = new StringBuffer();
        int lineNo = 0;
        int lastChapterID = 0;
        Book book = null;
        for (String line : lines){
            lineNo++;
            Matcher bookMatcher = BookPattern.matcher(line);
            Matcher chapterMatcher = ChapterPattern.matcher(line);
            if (chapterMatcher.find()){
                if (book != null) {
                    Chapter chapter = createChapter(book.getName(), lastChapterID, chapterContent);
                    chapterContent.delete(0, chapterContent.length());
                    logger.debug("[CHAPTER] " + line);
                    //if (chapter == null) chapterID--;
                    if (chapter != null) book.addChapter(chapter);
                }
                lastChapterID = Integer.parseInt(chapterMatcher.group(1));
                continue;
            }
            if (bookMatcher.find()){
                if (book!=null) {
                    Chapter chapter = createChapter(book.getName(), lastChapterID, chapterContent);
                    chapterContent.delete(0, chapterContent.length());
                    //if (chapter == null) chapterID--;
                    if (chapter != null) book.addChapter(chapter);
                    testament.addBook(book);
                }
                logger.debug("[BOOK] " + line);
                String bookName = bookMatcher.group(1);
                if (bookName.equals("马太福音")) {
                    testament = nt;
                }
                book = new Book(bookName);
                continue;
            }
            chapterContent.append(line);
        }
        testament.addBook(book);
        bible.setOldTestament(ot);
        bible.setNewTestament(nt);
    }

    public static void main(String[] args) {
        try {
            TextPlainCrawler crawler = new TextPlainCrawler("C:\\temp\\ARoD\\bible-cuv.txt");
            crawler.crawl();

        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public Bible getBible() {
        return bible;
    }
}
