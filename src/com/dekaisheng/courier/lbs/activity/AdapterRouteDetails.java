package com.dekaisheng.courier.lbs.activity;

import java.util.ArrayList;
import java.util.List;

import com.dekaisheng.courier.R;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 路径规划文字详情ListView Adapter
 * @author Dorian
 *
 */
public class AdapterRouteDetails extends BaseAdapter{

	private List<Instruction> datas;
	private Context context;
	private int selectedPosition = -1;// 选中的位置  
	public AdapterRouteDetails(List<Instruction> data, Context c){
		if(data == null){
			data = new ArrayList<Instruction>();
		}
		this.datas = data;
		this.context = c;
	}
	public void setSelectedPosition(int position) {  
        selectedPosition = position;  
    }  
	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public Object getItem(int arg0) {
		return datas.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder hd = null;
		final Instruction i = datas.get(position);
		if(convertView == null){
			hd = new Holder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.listview_item_route_details, null);
			hd.directionImg = (ImageView)convertView.findViewById(
					R.id.listview_item_route_details_direction);
			hd.instructionTxt = (TextView)convertView.findViewById(
					R.id.listview_item_route_details_instructions);
			hd.distanceTxt = (TextView)convertView.findViewById(R.id.listview_item_route_details_distance);
			convertView.setTag(hd);
		}else{
			hd = (Holder) convertView.getTag();
		}
		int match = match(i.direction);
		if(match < 0){
			// 方向为空时，需要匹配字符串来查找
			match = contain(i.instruction);
		}
		if(match < 0){
			// 如果两次都没有找到方向，那么使用默认方向：直走
			hd.directionImg.setImageResource(Direction.STRAIGHT.getResId());
		}else{
			hd.directionImg.setImageResource(match);
		}
		
		if (selectedPosition == position) {  
			hd.instructionTxt.setSelected(true);  
			hd.instructionTxt.setPressed(true);  
        } else {  
        	hd.instructionTxt.setSelected(false);  
        	hd.instructionTxt.setPressed(false);  
        } 
		hd.instructionTxt.setText(Html.fromHtml(i.instruction
				.replace("\u003cdiv", " \u003cb")
				.replace("\u003c/div", "\u003c/b")));
		/*.replace(" style=\"font-size:0.9em\"\u003e", 
					" style=\"font-size:0.9em;padding:0px;margin:0px;\"\u003e ")));*/
		hd.distanceTxt.setText(i.distance);
		return convertView;
	}

	private int match(String value){
		for(Direction d : Direction.values()){
			if(d.getVal().equals(value)){
				return d.getResId();
			}
		}
		return -1;
	}

	private int contain(String instruction){
		if(instruction == null || instruction.equals(""))
			return -1;
		for(Direction d : Direction.values()){
			String[] split = d.getVal().split("-");
			String first = "";
			if(split.length > 0){
				first = split[0];
			}
			if(instruction.contains(first))
				return d.getResId();
		}
		return -1;
	}

	private class Holder{
		ImageView directionImg;
		TextView instructionTxt;
		TextView distanceTxt;
	}
}







