package com.yanghuabin.reader;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.yanghuabin.reader.ChapterInfo;
import com.yanghuabin.reader.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by 18016 on 2018/2/23.
 */

public class ChapterAdapter extends ArrayAdapter {
    private int resourceId;

    public ChapterAdapter(Context context, int viewResourceId, ArrayList<String> objects){
        super(context,viewResourceId,objects);
        resourceId = viewResourceId;
    }

    public class ViewHolder{
        private TextView chapternames;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view;
        //ChapterInfo chapternames =  getItem(position);
        String chapss = (String) getItem(position);
        ViewHolder holder;
        if (convertView == null){
            holder = new ViewHolder();
            view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            holder.chapternames = (TextView) view.findViewById(R.id.chapter_title);
            view.setTag(holder);
        }else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        holder.chapternames.setText(chapss);
        return view;

    }
}
