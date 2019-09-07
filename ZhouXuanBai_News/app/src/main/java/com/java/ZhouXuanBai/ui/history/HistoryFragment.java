package com.java.ZhouXuanBai.ui.history;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import com.java.ZhouXuanBai.News;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.java.ZhouXuanBai.MainActivity;
import com.java.ZhouXuanBai.MyAdapter;
import com.java.ZhouXuanBai.News;
import com.java.ZhouXuanBai.R;
import com.java.ZhouXuanBai.ui.News.NewsViewModel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class HistoryFragment extends Fragment {

    private HistoryViewModel historyViewModel;
    public static ListView listview;
    public MyAdapter myAdapter;
    public static ArrayList<News> newsArrayList = new ArrayList();
    public View thisView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        historyViewModel =
                ViewModelProviders.of(this).get(HistoryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_history, container, false);

        thisView = root;
        RefreshLayout refreshLayout = (RefreshLayout) root.findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                reload();
                myAdapter.notifyDataSetChanged();
                refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
            }
        });

        listview = root.findViewById(R.id.news_list);
        Context mContext = MainActivity.getContext();
        myAdapter = new MyAdapter(newsArrayList, mContext);
        listview.setAdapter(myAdapter);

        return root;
    }

    public void reload(){
        try{
            newsArrayList.clear();
            File filePath = getContext().getFilesDir();
            File[] files = filePath.listFiles();
            for(File i : files){
                System.out.println("filesaved:    "+i.getName());
                System.out.println(files.length);
                if(i.isFile()){
                    FileReader fr = new FileReader(i);
                    BufferedReader bf = new BufferedReader(fr);
                    String str;
                    String[] vars = new String[7];
                    int n = 0;
                    System.out.println("final");
                    while((str=bf.readLine())!=null){
                        System.out.println(str);
                        if(str.length()!=0){
                            if(str.charAt(0)>96&&str.charAt(0)<123)
                                vars[n++] = str.split(": ")[1];
                            else
                                vars[n-1] += "\n"+str;
                        }
                        else
                            vars[n] += "\n";
                    }
                    News news = News.buildNews(vars[1],vars[0],vars[3],vars[4],vars[6],vars[5],vars[2]);
                    System.out.println(news.title);
                    newsArrayList.add(news);
                }
            }
            MainActivity.HistoryList.addAll(newsArrayList);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static ListView getListView() {
        return listview;
    }

}