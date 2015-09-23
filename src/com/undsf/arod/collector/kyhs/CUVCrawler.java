package com.undsf.arod.collector.kyhs;

import com.undsf.arod.Bible;
import com.undsf.arod.Book;
import com.undsf.arod.Chapter;
import com.undsf.arod.Testament;
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
public class CUVCrawler extends BibleCrawler {
    public static Log logger = LogFactory.getLog(CUVCrawler.class);

    public static final String VERSION = "/hhb/";
    public static final String INDEX_URL = Constants.BASE_URL + VERSION + "index.htm";
    public static final String CACHE_BASE = "C:\\temp\\ARoD\\hhb\\";

    public CUVCrawler() {
        super();
    }

    @Override
    public void crawl(){
        String cachePath = CACHE_BASE + "index.htm";
        try {
            String html = client.requestWithCache(
                    INDEX_URL,
                    cachePath,
                    client.METHOD_GET,
                    null
            );
            Document indexDoc = Jsoup.parse(html);
            Elements bookElements = indexDoc.select("td[VALIGN=CENTER]");
            Elements bookIndexElements = indexDoc.select("td[ALIGN=LEFT]");
            if (bookElements.size()!=bookIndexElements.size()){
                logger.error("获取到的元素数量不匹配！");
                //TODO 这个值应该为66
                return;
            }
            int bookAmount = bookElements.size();
            bible = new Bible("简体和合本");
            Testament testament = new Testament("旧约全书");
            logger.debug("找到"+bookAmount+"卷");
            for (int index = 0; index < bookAmount; index++){
                Element bookElement = bookElements.get(index);
                Element bookIndexElement = bookIndexElements.get(index);
                Book book = createBook(bookElement, bookIndexElement);
                logger.debug(book);
                //从马太福音开始算新约
                if (book.getName().equals("马太福音")){
                    bible.setOldTestament(testament);
                    testament = new Testament("新约全书");
                }
                testament.addBook(book);
            }
            bible.setNewTestament(testament);
        }
        catch (IOException e){
            logger.error(e.getMessage());
        }
    }

    @Override
    public String getVersion(){
        return VERSION;
    }

    @Override
    public String getCachePath(){
        return CACHE_BASE;
    }

    public static void main(String[] args) {
        BibleCrawler crawler = new CUVCrawler();
        crawler.crawl();
        crawler.printBibleSummary();
    }

}
