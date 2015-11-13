package com.dekaisheng.courier.home;

import java.util.ArrayList;
import java.util.List;

import com.dekaisheng.courier.activity.BaseActivity;
import com.dekaisheng.courier.activity.IDialogListener;
import com.dekaisheng.courier.cache.ApiResultCode;
import com.dekaisheng.courier.cache.Cache;
import com.dekaisheng.courier.core.webapi.OnCallback;
import com.dekaisheng.courier.core.webapi.bean.LogisticsDetail;
import com.dekaisheng.courier.core.webapi.bean.ResponseBean;
import com.dekaisheng.courier.core.webapi.bean.User;
import com.dekaisheng.courier.core.webapi.bean.LogisticsDetail.LogisticsStep;
import com.dekaisheng.courier.core.webapi.service.OrderService;
import com.dekaisheng.courier.util.StringUtils;
import com.dekaisheng.courier.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 物流详情Activity
 * @author Dorian
 *
 */
public class ActivityLogisticsDetail extends BaseActivity{

	/* 订单号？物流单号？ */
	public static final String ORDER_NO = "order_no";

	@ViewInject(R.id.activity_logistics_detail_lay_container)
	private LinearLayout containerLay;
	@ViewInject(R.id.activity_logistics_detail_lay_container2)
	private LinearLayout container2Lay;

	@ViewInject(R.id.veiw_simple_header_img_goback)
	private ImageView gobackImg;

	@ViewInject(R.id.veiw_simple_header_title)
	private TextView titleTxt;

	

	@ViewInject(R.id.activity_logistics_detail_customer_name)
	private TextView customersName;
	@ViewInject(R.id.activity_logistics_detail_customer_phone)
	private TextView customersPhone;
	@ViewInject(R.id.activity_logistics_detail_customer_address)
	private TextView customersAddress;
	@ViewInject(R.id.activity_logistics_detail_txt_platform)
	private TextView orderPlatform;
	@ViewInject(R.id.activity_logistics_detail_txt_user)
	private TextView userName;
	@ViewInject(R.id.activity_logistics_detail_layout_signature)
	private LinearLayout signatureLayout;

	@ViewInject(R.id.activity_logistics_detail_listview)
	private ListView listview;
	private LogisticsDetailAdapter adapter;
	private List<LogisticsStep> steps;
	private LogisticsDetail logistics;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_logistics_detail);
		ViewUtils.inject(this);
		initView();
		initData();
	}

	private void initData() {
		Intent i = this.getIntent();
		String order = i.getStringExtra(ORDER_NO);
		if(order == null || order.equals("")){
			showTipsDialog(R.string.dialog_tips_order_no_null, new IDialogListener() {

				@Override
				public void onOk() {
					finish();
				}

				@Override
				public void onCancle() {
					// 没有取消按钮o
				}
			});
			return ;
		}else{
			getLogisticsDetail(order);
		}
	}

	private void getLogisticsDetail(String order){
		User u = Cache.getInstance().getUser();
		if(u == null){
			showToastShort(R.string.toast_tips_not_login_yet);
			this.logOff();
			return;
		}
		OrderService os = new OrderService();
		os.logisticsDetail(u.getToken(), u.getUid(), order, new OnCallback(){

			@Override
			public void onStart(){
				showProgress(ActivityLogisticsDetail.this);
			}
			
			@Override
			public void onCompleted(ResponseBean<?> obj) {
				dismissProgressDialog();
				if(obj == null){
					return;
				}
				if(obj.getCode() == ApiResultCode.SUCCESS_1001){
					if(obj.getData() instanceof LogisticsDetail){
						logistics = (LogisticsDetail) obj.getData();
						container2Lay.setVisibility(View.VISIBLE);
						containerLay.setVisibility(View.VISIBLE);
						customersName.setText(logistics.getRecipient_name());
						customersPhone.setText("(" + logistics.getRecipient_phone_number() + ")");
						customersAddress.setText(logistics.getAddress());
						userName.setText(logistics.getDeliver_name());
						orderPlatform.setText(logistics.getE_name());
						steps.addAll(logistics.getStatus_info_list());
						adapter.notifyDataSetChanged();
					}
				}else{
					finish();
					showToastShort(obj.getMsg());
				}
			}

			@Override
			public void onFailure(String failed) {
				dismissProgressDialog();
				showToastShort(failed);
			}
		});
	}
	
	private void initView() {
		this.titleTxt.setText(this.getString(R.string.activity_logistics_detail_title));
		
		this.steps = new ArrayList<LogisticsStep>();
		this.adapter = new LogisticsDetailAdapter(this,this.steps);
		this.listview.setAdapter(adapter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@OnClick({R.id.veiw_simple_header_img_goback
	
		,R.id.activity_logistics_detail_layout_signature})
	public void onClick(View v){
		int id = v.getId();
		switch(id){
		case R.id.veiw_simple_header_img_goback:
			this.finish();
			break;
		case R.id.activity_logistics_detail_layout_signature:
			signature();
			break;
		default:break;
		}
	}

	private void signature() {
		String url = this.logistics.getSignature_picture();
		if(StringUtils.isEmpty(url)){
			showToastShort(R.string.toast_tips_no_signature_picture);
		}
		Intent i = new Intent(this,ActivitySignature.class);
		i.putExtra(getString(R.string.extra_url), url);
		startActivity(i);
	}

}
