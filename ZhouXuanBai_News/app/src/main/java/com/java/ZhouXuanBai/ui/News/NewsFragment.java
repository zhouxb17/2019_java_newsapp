package com.java.ZhouXuanBai.ui.News;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.java.ZhouXuanBai.MainActivity;
import com.java.ZhouXuanBai.MyAdapter;
import com.java.ZhouXuanBai.News;
import com.java.ZhouXuanBai.R;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

public class NewsFragment extends Fragment {

    private NewsViewModel newsViewModel;
    public static ListView listview;
    public MyAdapter myAdapter;
    public ArrayList<News> newsArrayList = new ArrayList();
    public View thisView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        newsViewModel =
                ViewModelProviders.of(this).get(NewsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_news, container, false);
//        final TextView textView = root.findViewById(R.id.text_news);
//        newsViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
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

    //    listview.setOnItemClickListener((ListView.OnItemClickListener) this);






        return root;
    }

//    @Override
//    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
//        String location = "位置："+listview.getItemIdAtPosition(position);
//        String l = "   内容，"+listview.getItemAtPosition(position);
//        Context mContext = MainActivity.getContext();
//        Toast.makeText(mContext, location+l, Toast.LENGTH_SHORT).show();
//    }

    public static ListView getListView() {
        return listview;
    }


//    public void newsclicked(View view)
//    {
//
//    }

}
