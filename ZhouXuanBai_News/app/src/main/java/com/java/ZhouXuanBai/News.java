package com.java.ZhouXuanBai;

import java.io.Serializable;

public class News implements Serializable {
    public String publishTime;
    public String title;
    public String content;
    public String url;
    public String newsID;
    public String category;
    public String publisher;
    News(String a,String b,String c,String d,String e,String f,String g){
        publishTime = a;
        title = b;
        content = c;
        url = d;
        newsID = e;
        category = f;
        publisher = g;
    }
}
