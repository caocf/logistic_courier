package com.dekaisheng.courier.user;

import com.dekaisheng.courier.activity.BaseActivity;
import com.dekaisheng.courier.core.database.entity.PushMessage;
import com.dekaisheng.courier.R;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ActivityMessageDetail extends BaseActivity {

	@ViewInject(R.id.veiw_simple_custom_header_goback_2)
	private ImageView gobackImg;
	@ViewInject(R.id.veiw_simple_custom_header_title_2)
	private TextView titleTxt;
	@ViewInject(R.id.tv_message_title)
	private TextView tv_message_title;
	@ViewInject(R.id.tv_publish_time)
	private TextView tv_publish_time;
	@ViewInject(R.id.img_head)
	private ImageView img_head;
	@ViewInject(R.id.tv_content)
	private TextView tv_content;

	private PushMessage message;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_message_detail);
		ViewUtils.inject(this);
		initData();
	}

	private void initData() {
		this.titleTxt.setText(this
				.getString(R.string.activity_message_detail_title));
		Intent intent = this.getIntent();
		message = (PushMessage) intent.getSerializableExtra(this
				.getString(R.string.extra_message));
		if (message != null) {
			tv_message_title.setText(message.title);
			tv_publish_time.setText(message.publish_time);
			new BitmapUtils(this).display(img_head, message.image);
			tv_content.setText(message.content);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@OnClick({ R.id.veiw_simple_custom_header_goback_2 })
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.veiw_simple_custom_header_goback_2:
			this.finish();
			break;
		}
	}
}
