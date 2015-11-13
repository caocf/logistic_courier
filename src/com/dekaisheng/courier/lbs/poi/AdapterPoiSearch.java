package com.dekaisheng.courier.lbs.poi;

import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.dekaisheng.courier.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AdapterPoiSearch extends BaseAdapter{

	public static final String ORDER_EXTRA = "order";
	private Activity context;
	public AutocompletePredictionBuffer address;

	public AdapterPoiSearch(Activity context, AutocompletePredictionBuffer address){
		this.context = context;
		if(address != null){
			this.address = address;
		}
	}

	@Override
	public int getCount() {
		return address.getCount();
	}

	@Override
	public Object getItem(int arg0) {
		return address.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int arg0, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context)
					.inflate(R.layout.listview_item_geocode, null);
			holder.name = (TextView) convertView.findViewById(R.id.listview_item_geocode_txt_address);
			holder.location = (TextView) convertView.findViewById(R.id.listview_item_geocode_txt_location);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		AutocompletePrediction address = this.address.get(arg0);
		holder.name.setText(address.getDescription());
		holder.location.setText(address.getMatchedSubstrings().toString());
		return convertView;
	}

	private class ViewHolder{
		public TextView name;
		public TextView location;
	}

	
}