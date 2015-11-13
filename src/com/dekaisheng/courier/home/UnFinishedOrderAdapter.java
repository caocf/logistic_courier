package com.dekaisheng.courier.home;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.dekaisheng.courier.activity.SignForDialog;
import com.dekaisheng.courier.activity.SignForDialog.ISignForListener;
import com.dekaisheng.courier.core.webapi.bean.UnFinishedOrderForm;
import com.dekaisheng.courier.util.DateTimeHelper;
import com.dekaisheng.courier.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 主页未完成订单的列表适配器
 * @author Dorian
 *
 */
public class UnFinishedOrderAdapter extends BaseAdapter{

	public static final String ORDER_EXTRA = "order";
	private Activity context;
	public List<UnFinishedOrderForm> orders;

	public UnFinishedOrderAdapter(Activity context, 
			List<UnFinishedOrderForm> contacts){
		this.context = context;
		if(contacts != null){
			this.orders = contacts;
		}else{
			this.orders = new ArrayList<UnFinishedOrderForm>();
		}
	}

	@Override
	public int getCount() {
		return orders.size();
	}

	@Override
	public Object getItem(int arg0) {
		return orders.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int arg0, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		final UnFinishedOrderForm c = this.orders.get(arg0);
		if(convertView == null || convertView.getTag() == null){
			convertView = LayoutInflater.from(context)
					.inflate(R.layout.listview_item_unfinished_order, null);
			holder = new ViewHolder();
			holder.orderId = (TextView)convertView.findViewById(
					R.id.listview_item_unfinished_order_id);
			holder.distance = (TextView)convertView.findViewById(
					R.id.listview_item_unfinished_order_distance);
			holder.name = (TextView)convertView.findViewById(
					R.id.listview_item_unfinished_order_name);

			holder.address = (TextView)convertView.findViewById(
					R.id.listview_item_unfinished_order_txt_address);
			holder.time = (TextView)convertView.findViewById(
					R.id.listview_item_unfinished_order_txt_time);
			holder.phoneNumber = (TextView)convertView.findViewById(
					R.id.listview_item_unfinished_order_customer_phone);
			holder.signFor = (ImageView)convertView.findViewById(
					R.id.listview_item_unfinished_order_txt_sign);
			holder.location = (ImageView)convertView.findViewById(
					R.id.listview_item_unfinished_order_location);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.orderId.setText(c.order_no);
		holder.distance.setText("500m");
		holder.name.setText(c.recipient_name);
		holder.address.setText(c.address);
		holder.time.setText(c.expected_time);
		holder.phoneNumber.setText(c.recipient_phone_number);
		holder.phoneNumber.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
						+ c.recipient_phone_number));
				if (i.resolveActivity(context.getPackageManager()) == null) {
					return;
				}
				context.startActivity(i);	
			}
		});
		final int id = arg0;
		holder.signFor.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				final SignForDialog sd = new SignForDialog();
				sd.show(context.getFragmentManager(), "");
				SignForDialog.index = id;
				sd.setListener(new ISignForListener(){

					@Override
					public void onTakePhoto() {
						// do nothing
					}

					@Override
					public void onHandWritten() {
						Intent i = new Intent(context,ActivityHandwrittenSignFor.class);
						i.putExtra(ORDER_EXTRA, c);
						context.startActivityForResult(i,ActivityHomeNew.SIGN_HAND_WRITE_REQUEST);
						sd.dismiss();
					}

					@Override
					public void onUnFinished() {
						Intent i = new Intent(context,ActivityAbnormalOrder.class);
						i.putExtra(ORDER_EXTRA, c); // 将点击的未完成订单传过去
						context.startActivityForResult(i,ActivityHomeNew.SIGN_UNABLE_REQUEST);
						sd.dismiss();
					}

				});
			}

		});
		holder.location.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent i = new Intent();
				i.putExtra(ORDER_EXTRA, c);
				context.setResult(Activity.RESULT_OK, i);
				context.finish();
			}

		});
		return convertView;
	}

	public String getLastTime(String expert){
		String result = null;
		try {
			Date ext = DateTimeHelper.convertStrToDate(expert);
			Date c = new Date();
			long last = (ext.getTime() - c.getTime()) / 1000; // 得到秒
			if(last > 0){
				long second = last % 60; // 秒剩余
				long minute = (last / 60) % 60; // 分剩余
				long hour = last / 3600; // 
				result = hour + ":" + minute + ":" + second;
			}else{
				int d = expert.indexOf("-");
				result = expert.substring(d + 1, expert.length());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private class ViewHolder{
		public TextView orderId;
		public TextView distance;
		public ImageView location;
		public TextView name;
		public TextView address;
		public TextView time;
		public TextView phoneNumber;
		public ImageView signFor;
	}

}
