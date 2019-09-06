//package com.java.ZhouXuanBai;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//
//import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import com.google.android.material.snackbar.Snackbar;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//
//import android.os.Handler;
//import android.text.method.*;
//import android.view.View;
//import android.widget.TextView;
//
//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.FileOutputStream;
//import java.io.FileReader;
//import java.io.IOException;
//import java.io.OutputStreamWriter;
//
//public class show_news extends AppCompatActivity {
//    private TextView textView2;
//    private TextView textView3;
//    private TextView textView4;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_show_news);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        textView2 = findViewById(R.id.textView2);
//        textView2.setMovementMethod(ScrollingMovementMethod.getInstance());
//        textView3 = findViewById(R.id.textView3);
//        textView3.setMovementMethod(ScrollingMovementMethod.getInstance());
//        textView4 = findViewById(R.id.textView4);
//        textView4.setMovementMethod(ScrollingMovementMethod.getInstance());
//
//        //News news = (News) getIntent.getExtras();
//        News news;
//        String filename = news.category+"_"+news.newsID+".txt";
//        this.WriteSysFile(show_news.this,filename);
//        this.ShowSomething(news);
//
//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//    }
//
//    public void WriteSysFile(Context context,String filename){
//        try{
//            FileOutputStream fos = context.openFileOutput(filename,Context.MODE_APPEND);
//            OutputStreamWriter osw = new OutputStreamWriter(fos,"UTF-8");
//            osw.write("something");//把新闻的内容写入/data/data/包名/files
//            osw.flush();
//            fos.flush();
//            osw.close();
//            fos.close();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//
//    public void ShowSomething(News news){
//        textView2.setText(news.title);
//        textView3.setText(news.publisher+" "+news.publishTime);
//        textView4.setText(news.content);
//    }
//}
//
//
