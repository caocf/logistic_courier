package com.dekaisheng.courier.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import com.dekaisheng.courier.core.webapi.bean.OrderHistory.MonthCount;
import com.dekaisheng.courier.core.webapi.bean.OrderHistory.YearCount;
import com.dekaisheng.courier.R;

import android.annotation.SuppressLint;
import android.content.Context;

public class RecordAdapter extends BaseExpandableListAdapter{

	private Context context;

	private List<YearCount> data;

	public RecordAdapter(Context c, List<YearCount> data){
		this.context = c;
		this.data = data;
	}

	@Override
	public int getGroupCount() {
		if(data == null){
			return 0;
		}
		return data.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return data.get(groupPosition).list.size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return data.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return data.get(groupPosition).list.get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return data.get(groupPosition).year;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		YearCount y = data.get(groupPosition);
		GroupHolder holder = null;
		if(convertView == null){
			holder = new GroupHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.view_record_group_item, null);
			holder.layout = (LinearLayout)convertView.findViewById(R.id.view_record_group_item);
			holder.year = (TextView) convertView.findViewById(R.id.view_record_group_item_txt_year);
			holder.arrow = (ImageView) convertView.findViewById(R.id.view_record_group_item_img_arrow);
			holder.top = (LinearLayout) convertView.findViewById(R.id.view_record_group_item_top);
			convertView.setTag(holder);
		}else{
			holder = (GroupHolder) convertView.getTag();
		}
		
		if (isExpanded) {
			holder.arrow.setImageResource(R.drawable.arrow_up_gray);
		} else {
			holder.arrow.setImageResource(R.drawable.arrow_down_gray);
		}
		if(groupPosition == 0){
			holder.top.setVisibility(View.GONE);
		}else{
			holder.top.setVisibility(View.VISIBLE);
		}
		holder.year.setText("In " + y.year + " (" + y.total + ")");
		return convertView;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {
		YearCount y = data.get(groupPosition);
		MonthCount mc = y.list.get(childPosition);
		ChildHolder holder = null;
		if(convertView == null){
			holder = new ChildHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.view_record_child_item, null);
			holder.month = (TextView)convertView.findViewById(R.id.view_record_child_item_txt_month);
			holder.radio = (ProgressBar)convertView.findViewById(R.id.view_record_child_item_txt_radio);
			holder.count = (TextView)convertView.findViewById(R.id.view_record_child_item_txt_count);
			convertView.setTag(holder);
		}else{
			holder = (ChildHolder) convertView.getTag();
		}
		holder.month.setText(mc.month + "");
		holder.count.setText(mc.count + "");
		holder.radio.setProgress(mc.count * 100 / y.total);
//		Log.i("View", "W=" + holder.radio.getWidth()+"");
//		Log.i("View", "H=" + holder.radio.getHeight()+"");
//		Log.i("View", "W=" + DisplayUtil.getScreenWidth());
//		Log.i("View", "H=" + DisplayUtil.getScreenHeight());
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}
	
	private class GroupHolder{

		@SuppressWarnings("unused")
		public LinearLayout layout;
		
		public LinearLayout top;

		public TextView year;

		public ImageView arrow;

	}

	private class ChildHolder{
		
		public TextView month;
		
		public ProgressBar radio;
		
		public TextView count;
		
	}
}
