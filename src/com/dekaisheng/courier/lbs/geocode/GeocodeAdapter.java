package com.dekaisheng.courier.lbs.geocode;

import java.util.ArrayList;
import java.util.List;

import com.dekaisheng.courier.R;
import android.app.Activity;
import android.location.Address;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GeocodeAdapter extends BaseAdapter{

	public static final String ORDER_EXTRA = "order";
	private Activity context;
	public List<Address> address;

	public GeocodeAdapter(Activity context, List<Address> address){
		this.context = context;
		if(address != null){
			this.address = address;
		}else{
			this.address = new ArrayList<Address>();
		}
	}

	@Override
	public int getCount() {
		return address.size();
	}

	@Override
	public Object getItem(int arg0) {
		return address.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

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
		Address address = this.address.get(arg0);
		holder.name.setText(address.getAddressLine(1) + "-" + address.getAddressLine(0));
		holder.location.setText("(" + address.getLatitude() + "," + address.getLongitude() + ")");
		return convertView;
	}

	private class ViewHolder{
		public TextView name;
		public TextView location;
	}

	
}




