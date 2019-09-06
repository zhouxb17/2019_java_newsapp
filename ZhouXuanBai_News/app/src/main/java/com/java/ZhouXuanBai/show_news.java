//package com.java.ZhouXuanBai;
//
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
//import java.io.FileReader;
//import java.io.IOException;
//
//public class show_news extends AppCompatActivity {
//    private TextView textView10;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_show_news);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        textView10 = findViewById(R.id.textView10);
//        textView10.setMovementMethod(ScrollingMovementMethod.getInstance());
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
//    private Handler mHandler = new Handler(){
//        public void handleMessage(android.os.Message msg){
//            textView10.setText((String)msg.obj);
//        }
//    };
//
//    class DataThread extends Thread{
//        @Override
//        public void run() {
//            while(true)
//            {
//                try{
//                    BufferedReader in = new BufferedReader(new FileReader("/sdcard/log"));
//                    String line;
//                    StringBuilder sb = new StringBuilder();
//                    while((line = in.readLine()) != null) {
//                        sb.append(line+'\n');
//                    }
//                    mHandler.sendMessage(mHandler.obtainMessage(0, sb.toString()));
//                    in.close();
//                    Thread.sleep(2000);
//                } catch(IOException e){
//                    e.printStackTrace();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//}


