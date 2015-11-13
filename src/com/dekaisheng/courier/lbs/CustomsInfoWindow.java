package com.dekaisheng.courier.lbs;

import com.dekaisheng.courier.core.webapi.bean.UnFinishedOrderForm;
import com.dekaisheng.courier.util.JsonHelper;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;
import com.dekaisheng.courier.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * 自定义InfoWindow，根据实际需要定义弹窗
 * GoogleMap.setInfoWindowAdapter(new CustomsInfoWindow(context));
 * 即可设置自定义的view
 * @author Dorian
 *
 */
public class CustomsInfoWindow implements InfoWindowAdapter{

	private Context context;
	
	public CustomsInfoWindow(Context context){
		this.context = context;
	}
	
	@SuppressLint("InflateParams")
	@Override
	public View getInfoContents(Marker arg0) {
		View v = LayoutInflater.from(context).inflate(R.layout.custom_info_window
				, null);
		TextView title = (TextView)v.findViewById(R.id.custom_info_window_title);
		TextView content = (TextView)v.findViewById(R.id.custom_info_window_content);
		title.setText(arg0.getTitle());
		content.setText(arg0.getSnippet());
		return v;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getInfoWindow(Marker arg0) {
		View v = LayoutInflater.from(context).inflate(R.layout.custom_info_window
				, null);
		TextView title = (TextView)v.findViewById(R.id.custom_info_window_title);
		TextView content = (TextView)v.findViewById(R.id.custom_info_window_content);

		String json = arg0.getSnippet();
		JsonHelper jh = new JsonHelper();
		UnFinishedOrderForm order = (UnFinishedOrderForm) jh.jsonToBean(json, UnFinishedOrderForm.class);
		/**
		 * 根据需求不同，做出判断
		 */
		if(order != null){
			title.setText(order.recipient_name + "(" + order.recipient_phone_number + ")");
			content.setText(order.order_no);
		}else{
			title.setText(arg0.getTitle());
			content.setText(arg0.getSnippet());
		}
		return v;
	}

}
