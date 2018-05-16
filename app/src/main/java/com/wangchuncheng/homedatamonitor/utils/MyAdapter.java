package com.wangchuncheng.homedatamonitor.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wangchuncheng.homedatamonitor.R;


public class MyAdapter extends BaseAdapter {
    String[] datas; //= new ArrayList<>();
    Context mContext;

    public MyAdapter(Context context) {
        mContext = context;
    }

    public void setDatas(String[] datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return datas == null ? 0 : datas.length;
    }

    @Override
    public Object getItem(int position) {
        return datas == null ? null : datas[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHodler viewHodler = null;
        if (convertView == null) {
            viewHodler = new ViewHodler();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.support_simple_spinner_dropdown_item,null);
            viewHodler.mTextView = (TextView) convertView;
            convertView.setTag(viewHodler);
        } else {
            viewHodler = (ViewHodler) convertView.getTag();
        }
        viewHodler.mTextView.setText(datas[position]);
        return convertView;
    }

    private static class ViewHodler {
        TextView mTextView;
    }
}
