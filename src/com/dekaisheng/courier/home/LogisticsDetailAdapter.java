package com.dekaisheng.courier.home;

import java.util.ArrayList;
import java.util.List;

import com.dekaisheng.courier.core.webapi.bean.LogisticsDetail.LogisticsStep;
import com.dekaisheng.courier.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class LogisticsDetailAdapter extends BaseAdapter{

	private Context context;
	private List<LogisticsStep> steps;

	public LogisticsDetailAdapter(Context context, List<LogisticsStep> steps){
		this.context = context;
		this.steps = steps;
		if(steps == null){
			this.steps = new ArrayList<LogisticsStep>();
		}
	}

	@Override
	public int getCount() {
		return this.steps.size();
	}

	@Override
	public Object getItem(int position) {
		return this.steps.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder holder = null;
		LogisticsStep step = this.steps.get(position);
		if(view == null){
			view = LayoutInflater.from(context)
					.inflate(R.layout.listview_item_logistics_detail, null);
			holder = new ViewHolder();
			holder.info = (TextView) view.findViewById(
					R.id.listview_item_logistics_detail_address);
			holder.time = (TextView)view.findViewById(
					R.id.listview_item_logistics_detail_time);
			view.setTag(holder);
		}else{
			holder = (ViewHolder) view.getTag();
		}
		if(step != null){
			holder.info.setText(step.getInfo() + "");
			holder.time.setText(step.getCreate_time() + "");
		}
		return view;
	}

	private class ViewHolder{
		public TextView info;
		public TextView time;
	}









}
