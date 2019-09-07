package com.java.ZhouXuanBai.ui.history;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

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

import java.util.ArrayList;

public class HistoryFragment extends Fragment {

    private HistoryViewModel historyViewModel;
    public static ListView listview;
    public MyAdapter myAdapter;
    public ArrayList<News> newsArrayList = new ArrayList();
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
                MainActivity.refresh();
                myAdapter.notifyDataSetChanged();
                refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                MainActivity.getMore();
                myAdapter.notifyDataSetChanged();
                refreshlayout.finishLoadMore(2000/*,false*/);//传入false表示加载失败
            }
        });
        listview = root.findViewById(R.id.news_list);
        Context mContext = MainActivity.getContext();
        myAdapter = new MyAdapter(MainActivity.newsArrayList, mContext);
        listview.setAdapter(myAdapter);

        return root;
    }

    public static ListView getListView() {
        return listview;
    }

}