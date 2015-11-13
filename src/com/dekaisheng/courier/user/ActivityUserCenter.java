package com.dekaisheng.courier.user;

import java.util.ArrayList;
import java.util.List;

import com.dekaisheng.courier.activity.BaseActivity;
import com.dekaisheng.courier.cache.ApiResultCode;
import com.dekaisheng.courier.cache.Cache;
import com.dekaisheng.courier.core.webapi.OnCallback;
import com.dekaisheng.courier.core.webapi.bean.CompletedOrderForm;
import com.dekaisheng.courier.core.webapi.bean.OrderHistory;
import com.dekaisheng.courier.core.webapi.bean.ResponseBean;
import com.dekaisheng.courier.core.webapi.bean.User;
import com.dekaisheng.courier.core.webapi.bean.OrderHistory.YearCount;
import com.dekaisheng.courier.core.webapi.service.AcountService;
import com.dekaisheng.courier.core.webapi.service.OrderService;
import com.dekaisheng.courier.home.CompletedOrderAdapter;
import com.dekaisheng.courier.util.bmp.ImageLoader;
import com.dekaisheng.courier.util.log.Log;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.dekaisheng.courier.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * 用户中心Activity，查看历史记录，历史统计，以及个人信息的入口。
 * @author Dorian
 *
 */
public class ActivityUserCenter extends BaseActivity 
implements  OnClickListener{

	private ImageView gobackImg;
	private ImageView moreImg;
	private TextView userNameTxt;
	private boolean show = false;

	@ViewInject(R.id.activity_user_center_layout_user)
	private LinearLayout nameLayout;
	@ViewInject(R.id.activity_user_center_img_portrait)
	private ImageView portraitImg;
	@ViewInject(R.id.activity_user_center_txt_name)
	private TextView userNameTxt2;
	@ViewInject(R.id.activity_user_center_img_go_right)
	private ImageView goRightImg;
	@ViewInject(R.id.activity_user_center_listview)
	private ExpandableListView listview;
	@ViewInject(R.id.activity_user_center_txt_my_record)
	private TextView myRecordTxt;

	@ViewInject(R.id.activity_user_center_txt_history_record)
	private TextView recordHistoryTxt;

	private List<YearCount> data;
	private RecordAdapter adapter;

	@ViewInject(R.id.activity_user_center_p2r_listview)
	private PullToRefreshListView p2rListView;

	private int currentPage = 1;
	private CompletedOrderAdapter completedAdapter;
	private ArrayList<CompletedOrderForm> completedOrder;
	private boolean isRefresh = false;   // 判断是否是刷新
	private ListView listView;
	private MyPopupMenu menuPop;
	private boolean isLoadingOrder = false;
	private boolean isLoadingRecord = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_user_center);
		ViewUtils.inject(this);
		initView();
		initData();
	}

	private void initData() {
		completedOrder = new ArrayList<CompletedOrderForm>();
		completedAdapter = new CompletedOrderAdapter(this, completedOrder);
		p2rListView = (PullToRefreshListView)this.findViewById(R.id.activity_user_center_p2r_listview);
		p2rListView.setOnRefreshListener(new OnRefreshListener<ListView>(){

			@Override
			public void onRefresh(PullToRefreshBase<ListView> arg0) {
				currentPage = 1;
				isRefresh = true;
				getCompletedOrder();
			}

		});
		p2rListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener(){

			@Override
			public void onLastItemVisible() {
				getCompletedOrder();
			}

		});
		this.listView = p2rListView.getRefreshableView();
		this.listView.setAdapter(completedAdapter);
		getHistoryRecord();
	}

	private void initView() {
		ActionBar actionBar = getActionBar();
		actionBar.setCustomView(R.layout.view_user_center_header);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayShowCustomEnabled(true);
		this.moreImg = (ImageView)actionBar.getCustomView()
				.findViewById(R.id.veiw_user_center_img_more);
		this.moreImg.setOnClickListener(this);
		this.gobackImg = (ImageView)actionBar.getCustomView()
				.findViewById(R.id.veiw_user_center_img_goback);
		this.gobackImg.setOnClickListener(this);
		this.userNameTxt = (TextView)actionBar.getCustomView()
				.findViewById(R.id.view_user_center_header_user_name);

		data = new ArrayList<YearCount>();
		adapter = new RecordAdapter(this,data);
		listview.setAdapter(adapter);
		this.menuPop = new MyPopupMenu(this, new OnClickListener(){

			@Override
			public void onClick(View v) {
				switch(v.getId()){
				case R.id.popup_win_layout_msg:
					message();
					break;
				case R.id.popup_win_layout_logoff:
					logOff();
					break;
				}
			}

		});
		this.menuPop.setOnDismissListener(new PopupWindow.OnDismissListener() {

			@Override
			public void onDismiss() {
				moreImg.setBackgroundResource(R.drawable.menu_more_white);
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		/**
		 * 在onResume里面设置头像，因为后面有重新选择
		 */
		User u = Cache.getInstance().getUser();
		if(u != null){
			userNameTxt.setText(u.getUsername());
			userNameTxt2.setText(u.getUsername());
			ImageLoader loader = new ImageLoader(this);
			loader.into(portraitImg, u.getPortrait(), -1, -1);
		}
	}

	private void message() {
		this.menuPop.dismiss();
		Intent i = new Intent(this,ActivityMessageCenter.class);
		startActivity(i);
	}

	@OnClick({R.id.activity_user_center_layout_user,
		R.id.activity_user_center_txt_my_record,
		R.id.activity_user_center_txt_history_record})
	public void uiClick(View v){
		switch(v.getId()){
		case R.id.activity_user_center_layout_user:
			userInfo();
			break;
		case R.id.activity_user_center_txt_my_record:
			yearCount();
			break;
		case R.id.activity_user_center_txt_history_record:
			completedOrder();
			break;
		default:break;
		}
	}

	private void completedOrder() {
		this.p2rListView.setVisibility(View.VISIBLE);
		this.listview.setVisibility(View.GONE);
		this.myRecordTxt.setBackgroundResource(R.color.c_ddf1f1f1);
		this.recordHistoryTxt.setBackgroundResource(R.color.white);
		if(completedOrder.size() == 0){
			getCompletedOrder();
		}
	}

	private void yearCount() {
		this.p2rListView.setVisibility(View.GONE);
		this.listview.setVisibility(View.VISIBLE);
		this.myRecordTxt.setBackgroundResource(R.color.white);
		this.recordHistoryTxt.setBackgroundResource(R.color.c_ddf1f1f1);
		if(data.size() < 1){
			getHistoryRecord();
		}
	}

	private void userInfo() {
		Intent i = new Intent(this,ActivityUserInfo.class);
		startActivity(i);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch(id){
		case R.id.veiw_user_center_img_more:
			more();
			break;
		case R.id.veiw_user_center_img_goback:
			goback();
			break;
		default:break;
		}
	}

	private void goback() {
		this.finish();
	}

	private void more() {
		if(show){
			menuPop.dismiss();
			show = false;
		}else{
			show = true;
			menuPop.showAsDropDown(findViewById(R.id.view_user_center_header_menu_position));
			this.moreImg.setBackgroundResource(R.drawable.menu_more_blue);
		}
	}

	private void getCompletedOrder(){
		if(isLoadingOrder){
			return;
		}
		User u = Cache.getInstance().getUser();
		if(u != null){
			OrderService os = new OrderService();
			os.completedOrder(u.getToken(), u.getUid(), currentPage,new OnCallback(){

				@Override
				public void onStart(){
					isLoadingOrder = true;
				}

				@SuppressWarnings("unchecked")
				@Override
				public void onCompleted(ResponseBean<?> rb) {
					if(rb == null){
						return;
					}
					switch(rb.getCode()){
					case ApiResultCode.SUCCESS_1001:
						completedSuccess((List<CompletedOrderForm>)rb.getData());
						break;
					default:
						completedFailed(rb.getMsg());
						break;
					}
					isLoadingOrder = false;
				}

				@Override
				public void onFailure(String m) {
					completedFailed(m);
					isLoadingOrder = false;
				}

				private void completedSuccess(List<CompletedOrderForm> data) {
					p2rListView.onRefreshComplete();
					if(data == null || data.size() < 1){
						showToastShort(R.string.home_order_nomore_completed_order);
						return;
					}
					if(isRefresh){
						completedOrder.clear();
						isRefresh = false;
					}
					completedOrder.addAll(data);
					completedAdapter.notifyDataSetChanged();
					currentPage++;
				}

				private void completedFailed(String msg) {
					p2rListView.onRefreshComplete();
					showToastShort(msg);
				}

			});
		}
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
			}
			@Override
			public void onCompleted(ResponseBean<?> rb) {
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
						myRecordTxt.setText(getString(
								R.string.activity_user_center_txt_record) 
								+ " (" + oh.total +")");
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
				Log.i(TAG, "用户历史记录接口:" + msg);
				isLoadingRecord = false;
			}

		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(this.show){
				this.menuPop.dismiss();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

}
