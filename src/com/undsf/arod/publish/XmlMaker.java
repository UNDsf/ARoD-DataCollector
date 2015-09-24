package com.undsf.arod.publish;

import com.sun.org.apache.xml.internal.utils.DOMBuilder;
import com.undsf.arod.Bible;
import com.undsf.arod.Book;
import com.undsf.arod.Chapter;
import com.undsf.arod.Testament;
import com.undsf.arod.collector.IBibleCrawler;
import com.undsf.arod.collector.TextPlainCrawler;
import com.undsf.arod.collector.kyhs.BibleCrawler;
import com.undsf.arod.collector.kyhs.CUVCrawler;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

/**
 * Created by Arathi on 2015/9/22.
 */
public class XmlMaker {
    private Bible bible;
    private Document doc;

    public XmlMaker(){
        init();
    }

    public void init(){
        long startTime = System.currentTimeMillis();
        // BibleCrawler crawler = new CUVCrawler();
        try {
            IBibleCrawler crawler = new TextPlainCrawler("C:\\temp\\ARoD\\bible-cuv.txt");
            crawler.crawl();
            bible = crawler.getBible();
            System.out.println((System.currentTimeMillis() - startTime) + "us");
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public Element createBook(Book book){
        Element bookNode = doc.createElement("book");
        bookNode.setAttribute("name", book.getName());
        for (Chapter chapter : book.getChapters()){
            Element chapterNode = doc.createElement("chapter");
            chapterNode.setAttribute("id", chapter.getID()+"");
            int sectionID = 0;
            for (String section : chapter.getSections()){
                Element sectionNode = doc.createElement("section");
                sectionNode.setAttribute("id", ++sectionID + "");
                //sectionNode.setNodeValue(section);
                sectionNode.setTextContent(section);
                chapterNode.appendChild(sectionNode);
            }
            bookNode.appendChild(chapterNode);
        }
        return bookNode;
    }

    public Element createTestament(Testament testament){
        Element testamentNode = doc.createElement("testament");
        testamentNode.setAttribute("name", testament.getName());
        for (Book book : testament.getBooks()){
            Element bookNode = createBook(book);
            testamentNode.appendChild(bookNode);
        }
        //System.out.println(testamentNode.getChildNodes().getLength());
        return testamentNode;
    }

    public void createBible(){
        try {
            DocumentBuilder domBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            doc = domBuilder.newDocument();
            Element bibleRoot = doc.createElement("bible");
            bibleRoot.setAttribute("version", bible.getVersion());
            doc.appendChild(bibleRoot);

            Element oldTestamentNode = createTestament(bible.getOldTestament());
            bibleRoot.appendChild(oldTestamentNode);

            Element newTestamentNode = createTestament(bible.getNewTestament());
            bibleRoot.appendChild(newTestamentNode);
        }
        catch (ParserConfigurationException e){
            e.printStackTrace();
        }
    }

    public String toString(){
        if (doc == null) return "";
        TransformerFactory tf = TransformerFactory.newInstance();
        try {
            Transformer transformer = tf.newTransformer();
            DOMSource source = new DOMSource(doc);
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            //return source.toString();
            String fileName = "C:\\temp\\ARoD\\output\\bible-cuv.xml";
            OutputStream os = new FileOutputStream(fileName);
            //ByteArrayOutputStream os = new ByteArrayOutputStream();
            PrintWriter pw = new PrintWriter(os);
            StreamResult result = new StreamResult(pw);
            transformer.transform(source, result);
            //System.out.println(os.toString("UTF-8"));
            System.out.println("生成XML文件成功!");
        }
        catch (TransformerConfigurationException e) {
            System.out.println(e.getMessage());
        }
        catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
        catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
        catch (TransformerException e) {
            System.out.println(e.getMessage());
        }
        //catch (UnsupportedEncodingException e){
        //    e.printStackTrace();
        //}
        return "";
    }

    public static void main(String[] args) {
        XmlMaker maker = new XmlMaker();
        maker.createBible();
        String xml = maker.toString();
        System.out.println(xml);
    }
}
