package com.dekaisheng.courier.user;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.dekaisheng.courier.activity.BaseActivity;
import com.dekaisheng.courier.cache.Cache;
import com.dekaisheng.courier.core.database.dao.PushMessageDao;
import com.dekaisheng.courier.core.database.entity.PushMessage;
import com.dekaisheng.courier.core.webapi.bean.User;
import com.dekaisheng.courier.util.log.Log;
import com.dekaisheng.courier.util.push.MessageKey;
import com.dekaisheng.courier.Config;
import com.dekaisheng.courier.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ActivityMessageCenter extends BaseActivity 
implements OnItemLongClickListener, OnItemClickListener{

	@ViewInject(R.id.veiw_simple_custom_header_goback_2)
	private ImageView gobackBtn;

	@ViewInject(R.id.veiw_simple_custom_header_title_2)
	private TextView titleTxt;

	@ViewInject(R.id.activity_message_center_listview)

	private ListView listview;

	
	@ViewInject(R.id.activity_message_center_no_message)
	private TextView noMessage;

	private List<PushMessage> message;

	private MessageAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_message_center);
		ViewUtils.inject(this);
		initView();
		initData();
	}

	private void initData() {
		User u = Cache.getInstance().getUser();
		if(u == null){
			return;
		}
		PushMessageDao dao = new PushMessageDao();
		/*测试代码*/
		if(Config.DEBUG){
			Log.i(TAG, dao.deleteAll() + "Delete");
			for(int i = 0; i < 10; i++){
				PushMessage msg = new PushMessage();
				msg.content = "One of your package had been taken by your group.So, you don't need to pass that package any more.";
				msg.key = MessageKey.COMMON_MESSAGE.getVal();
				msg.message = "推送消息";
				msg.order_no = "123456789" + i;
				msg.image = "http://fe.topit.me/e/0a/a7/1197888586849a70aeo.jpg";
				msg.publish_time = "2015-09-14 12:00:00";
				msg.title = "package";
				msg.uid = u.getUid();
				if(i % 2 == 0){
					msg.read = -1;
				}else{
					msg.read = 1;
				}
				Log.i(TAG, dao.insert(msg) + "Insert");
			}
		}
		/*测试代码*/
		Selector s = Selector.from(PushMessage.class);
		s.where("uid", "=", u.getUid());
		List<PushMessage> all = dao.select(s);
		if(all != null && all.size() > 0){
			Collections.sort(all); // 先按读和未读排，再按时间排列顺序
			this.message.addAll(all);
			this.adapter.notifyDataSetChanged();
			noMessage.setVisibility(View.GONE);
		}else{
			noMessage.setVisibility(View.VISIBLE);
		}
	}

	private void initView() {
		this.titleTxt.setText(getString(R.string.activity_message_center_title));
		this.message = new ArrayList<PushMessage>();
		this.adapter = new MessageAdapter(this, this.message);
		this.listview.setAdapter(this.adapter);
		this.listview.setOnItemLongClickListener(this);
		this.listview.setOnItemClickListener(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onReceivePushMessage(PushMessage msg) {
		super.onReceivePushMessage(msg);
	}

	@OnClick({R.id.veiw_simple_custom_header_goback_2})
	public void onClick(View v){
		int id = v.getId();
		switch(id){
		case R.id.veiw_simple_custom_header_goback_2:
			goback();
			break;
		}
	}

	private void goback() {
		try{
			this.finish();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 长按，弹出提示框删除消息
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, 
			View arg1, final int arg2, long arg3) {
		//		final SimpleQuestionDialog sqd = new SimpleQuestionDialog(getString(
		//				R.string.dialog_tips_delete_message), new IDialogListener(){
		//
		//			@Override
		//			public void onOk() {
		//				PushMessageDao dao = new PushMessageDao();
		//				PushMessage msg = message.get(arg2);
		//				if(dao.delete(msg)){
		//					message.remove(arg2);
		//					adapter.notifyDataSetChanged();
		//				}
		//			}
		//
		//			@Override
		//			public void onCancle() {
		//
		//			}});
		//		sqd.show(getFragmentManager(), "");
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		PushMessage msg = message.get(arg2);
		msg.read = 1;
		PushMessageDao dao = new PushMessageDao();
		dao.update(msg);
		this.adapter.notifyDataSetChanged();
		Intent i = new Intent(this, ActivityMessageDetail.class);
		i.putExtra(getString(R.string.extra_message), msg);
		startActivity(i);
	}

}






