package com.yanghuabin.reader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class NovelsAdapter extends BaseAdapter {

	private Context context;
	private List<NovelInfo> novels;
	
	public NovelsAdapter(Context context, List<NovelInfo> novels) {
		this.context = context;
		this.novels = novels;
	}
	
	public void setDataChangeLinstener(List<NovelInfo> novels){
		this.novels = novels;
		this.notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return novels.size();
	}

	@Override
	public Object getItem(int position) {
		return novels.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder holder;
		
		if(view == null){
			view = LayoutInflater.from(context).inflate(R.layout.novel_item, null);
			holder = new ViewHolder();
			
			holder.name= (TextView) view.findViewById(R.id.tv_novel_name);
			holder.author = (TextView) view.findViewById(R.id.tv_novel_author);
			holder.type = (TextView) view.findViewById(R.id.tv_novel_type);
			holder.updateTime = (TextView) view.findViewById(R.id.tv_novel_updateTime);
			holder.size = (TextView) view.findViewById(R.id.tv_novel_size);
			holder.newChapter = (TextView) view.findViewById(R.id.tv_novel_newChapter);
			
			view.setTag(holder);
		}else{
			holder = (ViewHolder) view.getTag();
		}
		
		NovelInfo novel = (NovelInfo) getItem(position);
		holder.name.setText(novel.name);
		holder.author.setText(novel.author);
		holder.type.setText(novel.typeName);
		holder.updateTime.setText(novel.updateTime);
		holder.size.setText(novel.size);
		holder.newChapter.setText(novel.newChapter);
		
		return view;
	}
	
	static class ViewHolder{
		TextView name;
		TextView author;
		TextView type;
		TextView updateTime;
		TextView size;
		TextView newChapter;
	}
	
}
