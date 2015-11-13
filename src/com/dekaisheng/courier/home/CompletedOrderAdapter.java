package com.dekaisheng.courier.home;

import java.util.ArrayList;
import java.util.List;

import com.dekaisheng.courier.core.webapi.bean.CompletedOrderForm;
import com.dekaisheng.courier.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("InflateParams")
public class CompletedOrderAdapter extends BaseAdapter implements OnClickListener{

	private Context context;
	public List<CompletedOrderForm> orders;

	public CompletedOrderAdapter(Context context, 
			List<CompletedOrderForm> contacts){
		this.context = context;
		if(contacts != null){
			this.orders = contacts;
		}else{
			this.orders = new ArrayList<CompletedOrderForm>();
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

	@Override
	public View getView(int arg0, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		final CompletedOrderForm c = this.orders.get(arg0);
		if(convertView == null || convertView.getTag() == null){
			convertView = LayoutInflater.from(context)
					.inflate(R.layout.listview_item_completed_order, null);
			holder = new ViewHolder();
			holder.orderId = (TextView)convertView.findViewById(R.id.listview_item_completed_order_id);
			holder.status = (TextView)convertView.findViewById(R.id.listview_item_completed_order_status);
			holder.name = (TextView)convertView.findViewById(R.id.listview_item_completed_order_name);
			holder.phoneNumber = (TextView)convertView.findViewById(R.id.listview_item_completed_order_phone);
			holder.pck = (TextView)convertView.findViewById(R.id.listview_item_completed_order_package);
			holder.go = (ImageView)convertView.findViewById(R.id.listview_item_completed_order_detail);
			holder.click = (LinearLayout)convertView.findViewById(R.id.listview_item_completed_order);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}

		holder.orderId.setText(c.getOrder_no());
		holder.status.setText(c.getStatus_info());
		holder.name.setText(c.getRecipient_name());
		holder.pck.setText(c.getE_name());
		holder.phoneNumber.setText(c.getRecipient_phone_number());
		holder.click.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent i = new Intent(context,ActivityLogisticsDetail.class);
				i.putExtra(ActivityLogisticsDetail.ORDER_NO, c.getOrder_no());
				context.startActivity(i);
			}

		});

//		holder.go.setOnClickListener(new OnClickListener(){
//
//			@Override
//			public void onClick(View v) {
//				Intent i = new Intent(context,ActivityLogisticsDetail.class);
//				i.putExtra(ActivityLogisticsDetail.ORDER_NO, c.getOrder_no());
//				context.startActivity(i);
//			}
//
//		});
		return convertView;
	}

	private class ViewHolder{
		public TextView orderId;
		public TextView status;
		public TextView name;
		public TextView phoneNumber;
		public TextView pck;
		@SuppressWarnings("unused")
		public ImageView go;
		public LinearLayout click;

	}

	@Override
	public void onClick(View v) {

	}
}
