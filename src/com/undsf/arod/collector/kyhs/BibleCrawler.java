package com.undsf.arod.collector.kyhs;

import com.undsf.arod.Bible;
import com.undsf.arod.Book;
import com.undsf.arod.Chapter;
import com.undsf.arod.Testament;
import com.undsf.arod.collector.ICrawlable;
import com.undsf.util.UndHttpClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by Arathi on 2015/9/19.
 */
public abstract class BibleCrawler implements ICrawlable {
    public static Log logger = LogFactory.getLog(BibleCrawler.class);

    protected UndHttpClient client;
    protected Bible bible;

    public BibleCrawler(){
        client = UndHttpClient.getInstance();
    }

    @Override
    public abstract void crawl();
    public abstract String getVersion();
    public abstract String getCachePath();

    public void printTestamentSummary(Testament testament){
        System.out.println("  " + testament);
        for (Book book : testament.getBooks()){
            System.out.println("    " + book);
            for (Chapter chapter : book.getChapters()){
                int chapterID = chapter.getID();
                int sectionID = chapter.getLastSectionID();
                System.out.println("      " + chapterID + ":" + sectionID + " " + chapter.getSection(chapter.getLastSectionID()));
            }
        }
    }

    public void printBibleSummary(){
        System.out.println(bible);
        Testament ot = bible.getOldTestament();
        if (ot!=null) {
            printTestamentSummary(ot);
        }
        Testament nt = bible.getNewTestament();
        if (nt!=null) {
            printTestamentSummary(nt);
        }
    }

    public Book createBook(Element bookElement, Element bookIndexElement){
        Book book = new Book(bookElement.text().trim());
        Elements chapterUrlElements = bookIndexElement.select("a");
        for (Element link : chapterUrlElements){
            String chapterPage = link.attr("href");
            int chapterID = Integer.parseInt(link.text());
            Chapter chapter = createChapter(chapterID, chapterPage);
            book.addChapter(chapter);
        }
        return book;
    }

    public Chapter createChapter(int id, String page){
        Chapter chapter = new Chapter(id);
        chapter.setUrl(Constants.BASE_URL + getVersion() + page);
        String cache = getCachePath() + page;
        try {
            String html = client.requestWithCache(
                    chapter.getUrl(),
                    cache,
                    client.METHOD_GET,
                    null
            );
            Document chapterDoc = Jsoup.parse(html);
            //取出内容
            Elements tables = chapterDoc.select("table");
            int tableIndexOfMainBody = 1;
            if (tables.size()==1) {
                tableIndexOfMainBody = 0;
            }
            Element table = chapterDoc.select("table").get(tableIndexOfMainBody);
            Elements sectionElements = table.select("td[class=v]");
            logger.debug(sectionElements.size());
            for (Element tdIndex : sectionElements) {
                Element tdContent = tdIndex.nextElementSibling();
                String section = tdContent.text();
                logger.debug(section);
                chapter.addSection(section);
            }
        }
        catch (IOException e){
            logger.error(e.getMessage());
        }
        catch (IndexOutOfBoundsException e){
            logger.error(e.getMessage());
        }
        return chapter;
    }

    public Bible getBible(){
        return bible;
    }

}
