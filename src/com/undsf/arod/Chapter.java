package com.undsf.arod;

import java.util.ArrayList;
import java.util.List;

/**
 * 章
 * Created by Arathi on 2015/9/19.
 */
public class Chapter {
    int id;
    List<String> sections; //节
    String url;

    public Chapter(int id){
        this.id = id;
        sections = new ArrayList<String>();
    }

    public void setUrl(String url){
        this.url = url;
    }

    public String getUrl(){
        return url;
    }

    public int getID(){
        return id;
    }

    public int getLastSectionID(){
        return sections.size();
    }

    public void addSection(String section){
        sections.add(section);
    }

    public String getSection(int sectionID){
        return sections.get(sectionID-1);
    }

    public List<String> getSections(){
        return sections;
    }
}
