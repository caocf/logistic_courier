package com.dekaisheng.courier.home;

import com.dekaisheng.courier.activity.BaseActivity;
import com.dekaisheng.courier.cache.ApiResultCode;
import com.dekaisheng.courier.core.webapi.OnCallback;
import com.dekaisheng.courier.core.webapi.bean.ResponseBean;
import com.dekaisheng.courier.core.webapi.bean.UnFinishedOrderForm;
import com.dekaisheng.courier.core.webapi.bean.User;
import com.dekaisheng.courier.core.webapi.service.OrderService;
import com.dekaisheng.courier.util.log.Log;
import com.dekaisheng.courier.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

/**
 * 异常订单Activity，填写并报告后台异常原因
 * ResultCode == RESULT_OK时，才是提交成功
 * @author Dorian
 *
 */
public class ActivityAbnormalOrder extends BaseActivity{

	@ViewInject(R.id.veiw_simple_custom_header_goback_2)
	private ImageView gobackImg;

	@ViewInject(R.id.veiw_simple_custom_header_title_2)
	private TextView titleTxt;

	@ViewInject(R.id.activity_abnormal_order_radio_lost)
	private RadioButton lostRadio;

	@ViewInject(R.id.activity_abnormal_order_cargo)
	private RadioButton cargoRadio;

	@ViewInject(R.id.activity_abnormal_order_address_error)
	private RadioButton addressErrorRadio;

	@ViewInject(R.id.activity_abnormal_order_phone_error)
	private RadioButton phoneErrorRadio;

	@ViewInject(R.id.activity_abnormal_order_no_one_sign)
	private RadioButton noSignRadio;

	@ViewInject(R.id.activity_abnormal_order_input_reason)
	private EditText reasonInput;

	@ViewInject(R.id.activity_abnormal_order_btn_ok)
	private Button okBtn;
	
	@ViewInject(R.id.activity_abnormal_order_radiogroup)
	private RadioGroup radioGroup;

	private String errorInfo;

	private UnFinishedOrderForm uo; // 所点击的订单

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_abnormal_order);
		ViewUtils.inject(this);
		initView();
		initData();
	}

	private void initData() {
		Object oo = getIntent().getSerializableExtra(UnFinishedOrderAdapter.ORDER_EXTRA);
		if(oo != null && oo instanceof UnFinishedOrderForm){
			uo = (UnFinishedOrderForm)oo; 
			titleTxt.setText(uo.order_no + "");
		}

	}

	private void initView() {
		this.reasonInput.setOnEditorActionListener(new OnEditorActionListener(){

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_DONE){
					sendAbnormalOrder();
					return true;
				}
				return false;
			}
			
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@OnClick({R.id.activity_abnormal_order_btn_ok,
		R.id.veiw_simple_custom_header_goback_2})
	public void onClick(View v){
		int id = v.getId();
		switch(id){
		case R.id.veiw_simple_custom_header_goback_2:
			goback(RESULT_CANCELED);
			break;
		case R.id.activity_abnormal_order_btn_ok:
			sendAbnormalOrder();
			break;
		default:break;
		}
	}

	private void goback(int resultCode) {
		Intent intent=new Intent();   
		setResult(resultCode, intent);  
		finish(); 
	}

	private void sendAbnormalOrder() {
		if(uo == null){
			return;
		}
		int error_status = getReason();
		if(error_status == -1 && this.reasonInput.getText().toString().equals("")){
			showToastShort(R.string.abnormal_order_send_no_reason_tips);
			return;
		}
		User u = this.getUser();
		if(u == null){
			return;
		}
		OrderService os = new OrderService();
		os.sendAbnormalOrder(u.getToken(), u.getUid(), uo.order_no, 
				error_status, this.errorInfo + "", 
				reasonInput.getText().toString() + "", new OnCallback(){

			@Override
			public void onStart(){
				showProgressDialog(ActivityAbnormalOrder.this,"submitting");
			}
			
			@Override
			public void onCompleted(ResponseBean<?> rb) {
				dismissProgressDialog();
				if(rb == null){
					showToastShort(R.string.abnormal_order_send_error);
				}else if(rb.getCode() == ApiResultCode.SUCCESS_1001){
					showToastLong(rb.getMsg());
					goback(RESULT_OK);
				}else if(rb.getCode() == ApiResultCode.TIME_OUT_1005){
					showToastShort(R.string.abnormal_order_send_error + rb.getMsg());
				}else if(rb.getCode() == ApiResultCode.WRONG_PARAMS_1007){
					showToastShort(R.string.abnormal_order_send_error + rb.getMsg());
				}else if(rb.getCode() == ApiResultCode.NOT_YOUR_ORDER_1024){
					showToastShort(R.string.abnormal_order_send_error + rb.getMsg());
				}
			}

			@Override
			public void onFailure(String msg) {
				Log.e(TAG, "异常订单提交失败：" + msg);
				dismissProgressDialog();
				showToastShort(msg);
			}

		});
	}

	private int getReason(){
		int id = this.radioGroup.getCheckedRadioButtonId();
		int reason = -1;
		switch(id){
		case R.id.activity_abnormal_order_radio_lost:
			reason = 1;
			this.errorInfo = getString(R.string.activity_abnormal_order_radio_lost);
			break;
		case R.id.activity_abnormal_order_cargo:
			reason = 2;
			this.errorInfo = getString(R.string.activity_abnormal_order_radio_cargo);
			break;
		case R.id.activity_abnormal_order_address_error:
			reason = 3;
			this.errorInfo = getString(R.string.activity_abnormal_order_address_error);
			break;
		case R.id.activity_abnormal_order_phone_error:
			reason = 4;
			this.errorInfo = getString(R.string.activity_abnormal_order_phone_error);
			break;
		case R.id.activity_abnormal_order_no_one_sign:
			reason = 5;
			this.errorInfo = getString(R.string.activity_abnormal_order_no_one_sign);
			break;
		default:break;
		}
		return reason;
	}

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			goback(RESULT_CANCELED);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
}
