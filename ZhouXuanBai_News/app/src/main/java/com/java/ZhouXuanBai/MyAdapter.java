package com.java.ZhouXuanBai;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MyAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<News> newsArrayList;

    public MyAdapter() {}

    public MyAdapter(ArrayList<News> newsArrayList, Context mContext) {
        this.newsArrayList = newsArrayList;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return newsArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list,parent,false);
            holder = new ViewHolder();
            holder.text_Date = (TextView) convertView.findViewById(R.id.textView_Date);
            holder.text_Title = (TextView) convertView.findViewById(R.id.textView_Title);
            holder.text_Publisher = (TextView) convertView.findViewById(R.id.textView_Publisher);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.text_Date.setText(newsArrayList.get(position).publishTime);
        holder.text_Title.setText(newsArrayList.get(position).title);
        holder.text_Publisher.setText(newsArrayList.get(position).publisher);
        return convertView;
    }

    private class ViewHolder{
        TextView text_Date;
        TextView text_Title;
        TextView text_Publisher;
    }

}