package com.dekaisheng.courier.home;

import java.util.ArrayList;
import java.util.List;

import com.dekaisheng.courier.R;
import com.dekaisheng.courier.activity.BaseActivity;
import com.dekaisheng.courier.cache.ApiResultCode;
import com.dekaisheng.courier.cache.Cache;
import com.dekaisheng.courier.core.webapi.OnCallback;
import com.dekaisheng.courier.core.webapi.bean.CompletedOrderForm;
import com.dekaisheng.courier.core.webapi.bean.ResponseBean;
import com.dekaisheng.courier.core.webapi.bean.User;
import com.dekaisheng.courier.core.webapi.service.OrderService;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ActivityOrderHistory extends BaseActivity{

	@ViewInject(R.id.veiw_simple_header_img_goback)
	private ImageView gobackImg;
	
	@ViewInject(R.id.veiw_simple_header_title)
	private TextView titleText;
	
	
	
	@ViewInject(R.id.activity_order_history_p2r_listview)
	private PullToRefreshListView p2rListView;
	private int currentPage = 1;
	private CompletedOrderAdapter completedAdapter;
	private ArrayList<CompletedOrderForm> completedOrder;
	private boolean isRefresh = false;   // 判断是否是刷新
	private ListView listView;
	private boolean isLoadingOrder = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_order_completed);
		ViewUtils.inject(this);
		initView();
		getCompletedOrder();
	}

	private void initView() {
		this.titleText.setText(getString(R.string.activity_order_history_title));
		completedOrder = new ArrayList<CompletedOrderForm>();
		completedAdapter = new CompletedOrderAdapter(this, completedOrder);
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
					if(currentPage == 1 && !isRefresh)
						showProgress(ActivityOrderHistory.this);
				}

				@SuppressWarnings("unchecked")
				@Override
				public void onCompleted(ResponseBean<?> rb) {
					if(currentPage == 1 && !isRefresh)  
						dismissProgressDialog();
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
					if(currentPage == 1 && !isRefresh) 
						dismissProgressDialog();
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
					if(isRefresh){
						isRefresh = false;
					}
					showToastShort(msg);
				}

			});
		}
	}	
}
