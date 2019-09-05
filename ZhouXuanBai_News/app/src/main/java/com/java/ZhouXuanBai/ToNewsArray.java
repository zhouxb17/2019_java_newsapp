package com.java.ZhouXuanBai;

import android.app.Activity;
import android.content.Context;
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

public class ToNewsArray {
    public static String[] publishTime;
    public static String[] title;
    public static String[] content;
    public static String[] url;
    public static String[] newsID;
    public static String[] category;
    public static int size = 0;
    public static void toNewsArray(String s){
        JSONObject job;
        JSONArray jar;
        try{
            job = new JSONObject(s);
            jar = job.getJSONArray("data");
            size = jar.length();
            publishTime = new String[size];
            title = new String[size];
            content = new String[size];
            url = new String[size];
            newsID = new String[size];
            for(int i=0;i<size;i++){
                JSONObject jtemp = (JSONObject)jar.getJSONObject(i);
                publishTime[i] = jtemp.getString("publishTime");
                title[i] = jtemp.getString("titlle");
                content[i] = jtemp.getString("content");
                url[i] = jtemp.getString("url");
                newsID[i] = jtemp.getString("newsID");
                category[i] = jtemp.getString("category");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public static void saveInFile(){
        FileOutputStream out = null;
        BufferedWriter writer = null;
        try{
            for(int i=0;i<size;i++){
                String text = "title: "+title[i]+"\npublishTime: "+publishTime[i]+"\ncontent: "+content[i]+"\nurl: "+url[i]+"\nnewsId: "+newsID[i];
                out = openFileOutput(category[i]+" "+newsID[i]);
                writer = new BufferedWriter(new OutputStreamWriter(out));
                writer.write(text);
            }
        }catch(FileNotFoundException e) {
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            if(writer!=null){
                try{
                    writer.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
