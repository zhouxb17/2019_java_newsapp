package com.java.ZhouXuanBai;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import org.json.*;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ToNewsArray {
    public String[] publishTime;
    public String[] title;
    public String[] content;
    public String[] url;
    public String[] newsID;
    public String[] category;
    public String[] publisher;
    public int size = 0;
    public void toNewsArray(String s){
        JSONObject job;
        JSONArray jar;
        JSONArray jar1;
        try{
            job = new JSONObject(s);
            jar = job.getJSONArray("data");
            size = jar.length();
            publishTime = new String[size];
            title = new String[size];
            content = new String[size];
            url = new String[size];
            newsID = new String[size];
            category = new String[size];
            publisher = new String[size];
            for(int i=0;i<size;i++){
                JSONObject jtemp = (JSONObject)jar.getJSONObject(i);
                publishTime[i] = jtemp.getString("publishTime");
                title[i] = jtemp.getString("title");
                content[i] = jtemp.getString("content");
                url[i] = jtemp.getString("url");
                newsID[i] = jtemp.getString("newsID");
                category[i] = jtemp.getString("category");
                publisher[i] = jtemp.getString("publisher");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public ArrayList<String> getInfo() {
        ArrayList<String> al = new ArrayList<String>();
        for (int i = 0; i < size; i++)
            al.add(title[i]);
        return al;
    }
}
