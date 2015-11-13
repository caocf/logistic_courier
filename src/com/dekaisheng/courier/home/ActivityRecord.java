package com.dekaisheng.courier.home;

import java.util.ArrayList;
import java.util.List;

import com.dekaisheng.courier.R;
import com.dekaisheng.courier.activity.BaseActivity;
import com.dekaisheng.courier.cache.ApiResultCode;
import com.dekaisheng.courier.cache.Cache;
import com.dekaisheng.courier.core.webapi.OnCallback;
import com.dekaisheng.courier.core.webapi.bean.OrderHistory;
import com.dekaisheng.courier.core.webapi.bean.ResponseBean;
import com.dekaisheng.courier.core.webapi.bean.User;
import com.dekaisheng.courier.core.webapi.bean.OrderHistory.YearCount;
import com.dekaisheng.courier.core.webapi.service.AcountService;
import com.dekaisheng.courier.user.RecordAdapter;
import com.dekaisheng.courier.util.log.Log;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

public class ActivityRecord extends BaseActivity{

	@ViewInject(R.id.veiw_simple_header_img_goback)
	private ImageView gobackImg;
	
	@ViewInject(R.id.veiw_simple_header_title)
	private TextView titleText;	
	@ViewInject(R.id.activity_record_listview)
	private ExpandableListView listview;

	private List<YearCount> data;
	private RecordAdapter adapter;

	private boolean isLoadingRecord = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_record);
		ViewUtils.inject(this);
		initView();
		getHistoryRecord();
	}

	private void initView() {
		this.titleText.setText(getString(R.string.activity_record_title));
		data = new ArrayList<YearCount>();
		adapter = new RecordAdapter(this,data);
		listview.setAdapter(adapter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@OnClick({R.id.veiw_simple_header_img_goback})
	public void onClick(View v){
		switch(v.getId()){
		case R.id.veiw_simple_header_img_goback:
			goback();
			break;
		}
	}

	private void goback() {
		this.finish();
	}

	private void getHistoryRecord(){
		if(isLoadingRecord){
			return;
		}
		User u = Cache.getInstance().getUser();
		if(u == null){
			return;
		}
		AcountService as = new AcountService();
		as.historyRecord(u.getToken(), u.getUid(), new OnCallback(){

			@Override
			public void onStart(){
				isLoadingRecord = true;
				showProgress(ActivityRecord.this);
			}
			@Override
			public void onCompleted(ResponseBean<?> rb) {
				dismissProgressDialog();
				if(rb == null){
					Log.i(TAG, "ResponseBean<?> 为null???用户历史记录接口，目测是空数据集时[]使用了对象{}引发的");
					return;
				}
				if(rb.getCode() == ApiResultCode.SUCCESS_1001){
					OrderHistory oh = (OrderHistory) rb.getData();
					if(oh != null){
						data.addAll(oh.record_list);
						adapter.notifyDataSetChanged();
						for (int i=0; i<data.size(); i++) {
							listview.expandGroup(i);
						};
					}
				}else{
					showToastShort(rb.getMsg());
					Log.i(TAG, "用户历史记录接口，不正常返回:" 
					      + ApiResultCode.SUCCESS_1001 + rb.getMsg());
				}
				isLoadingRecord = false;
			}

			@Override
			public void onFailure(String msg) {
				dismissProgressDialog();
				Log.i(TAG, "用户历史记录接口:" + msg);
				isLoadingRecord = false;
			}

		});
	}	
}



