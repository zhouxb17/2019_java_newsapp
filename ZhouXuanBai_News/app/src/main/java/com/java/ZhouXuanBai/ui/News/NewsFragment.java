package com.java.ZhouXuanBai.ui.News;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.java.ZhouXuanBai.MainActivity;
import com.java.ZhouXuanBai.R;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

public class NewsFragment extends Fragment {

    private NewsViewModel newsViewModel;
    public static ListView listview;
    public ArrayAdapter<String> arrayAdapter;
    public List<String> titleList = new ArrayList();

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
        RefreshLayout refreshLayout = (RefreshLayout)root.findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                MainActivity.refresh();
                arrayAdapter.notifyDataSetChanged();
                refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                MainActivity.getMore();
                arrayAdapter.notifyDataSetChanged();
                refreshlayout.finishLoadMore(2000/*,false*/);//传入false表示加载失败
            }
        });
        listview = root.findViewById(R.id.news_list);
        Context mContext = MainActivity.getContext();
        arrayAdapter = new ArrayAdapter<String>(mContext, R.layout.item_list , MainActivity.titleList);
        listview.setAdapter(arrayAdapter);

//        listview.setOnScrollListener(new AbsListView.OnScrollListener() {
//
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem,
//                                 int visibleItemCount, int totalItemCount) {
//                if(firstVisibleItem + visibleItemCount == totalItemCount && !isUpdating){
//                    if(totalItemCount<totalCount){ //防止最后一次取数据进入死循环。
//                        Toast.makeText(ListViewActivity.this, "正在取第"+(++currentPage)+"的数据", Toast.LENGTH_LONG).show();
//                        AsyncUpdateDatasTask asyncUpdateWeiBoDatasTask = new AsyncUpdateDatasTask();
//                        asyncUpdateWeiBoDatasTask.execute();
//                    }
//                    System.out.println("begin update-------------");
//                }
//            }
//        });

        return root;
    }


    public static ListView getListView()
    {
        return listview;
    }
}
