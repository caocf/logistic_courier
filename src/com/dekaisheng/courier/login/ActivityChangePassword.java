package com.dekaisheng.courier.login;

import com.dekaisheng.courier.MyApplication;
import com.dekaisheng.courier.activity.BaseActivity;
import com.dekaisheng.courier.activity.IDialogListener;
import com.dekaisheng.courier.cache.ApiResultCode;
import com.dekaisheng.courier.core.webapi.OnCallback;
import com.dekaisheng.courier.core.webapi.bean.ResponseBean;
import com.dekaisheng.courier.core.webapi.service.AcountService;
import com.dekaisheng.courier.util.StringUtils;
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
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

/**
 * 配送员修改密码Activity
 * @author Dorian
 *
 */
public class ActivityChangePassword extends BaseActivity{

	@ViewInject(R.id.veiw_simple_custom_header_goback_2)
	private Button gobackBtn;
	
	@ViewInject(R.id.veiw_simple_custom_header_title_2)
	private TextView titleTxt;
	
	@ViewInject(R.id.activity_change_pwd_txt_new_pwd)
	private EditText newPwdTxt;
	
	@ViewInject(R.id.activity_change_pwd_txt_retype)
	private EditText retypePwdTxt;
	
	@ViewInject(R.id.activity_change_pwd_confirm)
	private Button confirmBtn;

	private String phone;
	private String code;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_change_password);
		ViewUtils.inject(this);
		initView();
		initData();
	}

	private void initData() {
		Intent i = this.getIntent();
		this.phone = i.getStringExtra(this.getString(R.string.extra_phone));
		this.code = i.getStringExtra(this.getString(R.string.extra_code));
	}

	private void initView() {
		this.titleTxt.setText(this.getString(R.string.change_password_title));
		this.newPwdTxt.setOnEditorActionListener(new OnEditorActionListener(){

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_DONE){
					confirm();
					return true;
				}
				return false;
			}
			
		});
		this.retypePwdTxt.setOnEditorActionListener(new OnEditorActionListener(){

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_DONE){
					confirm();
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

	@OnClick({R.id.veiw_simple_custom_header_goback_2
		, R.id.activity_change_pwd_confirm})
	public void onClick(View v){
		int id = v.getId();
		switch(id){
		case R.id.veiw_simple_custom_header_goback_2:
			goback();
			break;
		case R.id.activity_change_pwd_confirm:
			confirm();
			break;
		default:break;
		}
	}

	private void goback() {
		this.finish();
	}

	private void confirm() {
		String password = newPwdTxt.getText().toString();
		String rPassword = retypePwdTxt.getText().toString();
		if(StringUtils.isEmpty(password)){
			showToastShort(R.string.toast_tips_no_pwd_input);
			return;
		}else if(StringUtils.isEmpty(rPassword)){
			showToastShort(R.string.toast_tips_input_pwd_again);
			return;
		}else if(!password.equals(rPassword)){
			showToastShort(R.string.toast_tips_retype_pwd_wrong);
			return;
		}
		AcountService as = new AcountService();
		as.resetPassword(phone, code, password, new OnCallback(){

			@Override
			public void onCompleted(ResponseBean<?> rb) {
				if(rb == null) return;
				if(rb.getCode() == ApiResultCode.SUCCESS_1001){					
					// 修改密码成功，弹框，点确定回到登录界面
					showTipsDialog(rb.getMsg(), new IDialogListener(){

						@Override
						public void onOk() {
							finish();
						}

						@Override
						public void onCancle() {

						}

					});
				}else{

				}
			}

			@Override
			public void onFailure(String msg) {
				if(MyApplication.getApplication().getIsNetworkAvaliable()){
					showTipsDialog(R.string.dialog_tips_unknow_error);
				}else{
					showTipsDialog(getString(R.string.dialog_tips_network_error) + ":" + msg);
				}
			}

		});
	}

}





















