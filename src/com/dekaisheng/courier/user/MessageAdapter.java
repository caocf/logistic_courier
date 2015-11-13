package com.dekaisheng.courier.user;
import java.util.List;
import com.dekaisheng.courier.core.database.entity.PushMessage;
import com.dekaisheng.courier.util.UIUtils;
import com.dekaisheng.courier.R;
import com.lidroid.xutils.BitmapUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MessageAdapter extends BaseAdapter {

	private Context context;
	public List<PushMessage> orders;
    public BitmapUtils bitmapUtils;
	public MessageAdapter(Context context, List<PushMessage> contacts) {
		this.context = context;
		this.orders = contacts;
		bitmapUtils=new BitmapUtils(context);
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
		if (convertView == null || convertView.getTag() == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.listview_item_message, null);
			holder = new ViewHolder();
			holder.time = (TextView) convertView
					.findViewById(R.id.tv_time);
			holder.title = (TextView) convertView
					.findViewById(R.id.tv_title);
			holder.content = (TextView) convertView
					.findViewById(R.id.tv_content);
			holder.image = (ImageView) convertView
					.findViewById(R.id.img_head);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		PushMessage message = this.orders.get(arg0);
		if (message.read <0) {
			holder.title.setTextColor(UIUtils.getResources().getColor(
					R.color.black));
			holder.content.setTextColor(UIUtils.getResources().getColor(
					R.color.black));
			holder.time.setTextColor(UIUtils.getResources().getColor(
					R.color.black));
		} else {
			holder.title.setTextColor(UIUtils.getResources().getColor(
					R.color.deep_gray_01));
			holder.content.setTextColor(UIUtils.getResources().getColor(
					R.color.deep_gray_01));
			holder.time.setTextColor(UIUtils.getResources().getColor(
					R.color.deep_gray_01));
		}
		
		holder.title.setText(message.title);
		holder.time.setText(message.publish_time);
		holder.content.setText(message.content);
		bitmapUtils.display(holder.image, message.image);
		return convertView;
	}

	private class ViewHolder {
		public TextView title;
		public TextView time;
		public TextView content;
		public ImageView image;
	}
}
