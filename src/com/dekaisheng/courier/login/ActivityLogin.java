package com.dekaisheng.courier.login;
import com.dekaisheng.courier.AppManager;
import com.dekaisheng.courier.activity.BaseActivity;
import com.dekaisheng.courier.cache.ApiResultCode;
import com.dekaisheng.courier.cache.Cache;
import com.dekaisheng.courier.cache.Constants;
import com.dekaisheng.courier.cache.SharePreferenceHelper;
import com.dekaisheng.courier.core.database.dao.UserDao;
import com.dekaisheng.courier.core.webapi.OnCallback;
import com.dekaisheng.courier.core.webapi.bean.ResponseBean;
import com.dekaisheng.courier.core.webapi.bean.User;
import com.dekaisheng.courier.core.webapi.service.AcountService;
import com.dekaisheng.courier.home.ActivityHomeNew;
import com.dekaisheng.courier.util.NetUtils;
import com.dekaisheng.courier.util.UIUtils;
import com.dekaisheng.courier.util.log.Log;
import com.dekaisheng.courier.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 登录activity
 * @author Dorian
 *
 */
public class ActivityLogin extends BaseActivity{

	@ViewInject(R.id.activity_login_txt_title)
	private TextView titleLogin;

	@ViewInject(R.id.activity_login_btn_goback)
	private Button gobackBtn;

	@ViewInject(R.id.activity_login_btn_ok)
	private Button okBtn;

	@ViewInject(R.id.activity_login_input_user_name)
	private EditText et_username;

	@ViewInject(R.id.activity_login_input_password)
	private EditText et_password;

	@ViewInject(R.id.activity_home_txt_forgot_pwd)
	private TextView forgotPassword;
	private boolean userisNull = true;
	private boolean pwdisNull = true;
	@ViewInject(R.id.activity_login_btn_ok)
	private Button btn_login;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_login);
		ViewUtils.inject(this);
		initView();
		initData();
	}

	private void initView() {
		et_username.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s.length() > 0) {
					userisNull = false;
					if (!pwdisNull) {
						btn_login.setEnabled(true);
					}
				} else {
					userisNull = true;
					btn_login.setEnabled(false);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		et_password.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s.length() > 0) {
					pwdisNull = false;
					if (!userisNull) {
						btn_login.setEnabled(true);
					}
				} else {
					pwdisNull = true;
					btn_login.setEnabled(false);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void initData() {
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@OnClick({R.id.activity_login_btn_goback, R.id.activity_login_btn_ok
		, R.id.activity_home_txt_forgot_pwd})
	public void onClick(View v){
		int id = v.getId();
		switch(id){
		case R.id.activity_login_btn_goback:
			goback();
			break;
		case R.id.activity_login_btn_ok:
			login();
			break;
		case R.id.activity_home_txt_forgot_pwd:
			forgotPwd();
			break;
		default:break;
		}
	}

	private void goback() {
		this.finish();
	}

	private void login(){
		if (NetUtils.getNetType(UIUtils.getContext()) == NetUtils.UNCONNECTED) {
			UIUtils.showToast(UIUtils.getString(R.string.no_network));
			return;
		}
		String name = et_username.getText().toString();
		String pwd = et_password.getText().toString();
	
		AcountService as = new AcountService();
		as.login(name, pwd, new OnCallback(){

			@Override
			public void onStart(){
				handler.sendEmptyMessage(R.id.web_api_request_start);
			}

			@Override
			public void onCompleted(ResponseBean<?> responseBean) {
				dismissProgressDialog();
				if(responseBean == null){
					return;
				}
				Message msg = new Message();
				msg.obj = responseBean;
				msg.what = R.id.web_api_request_completed;
				handler.sendMessage(msg);
			}

			@Override
			public void onFailure(String msg) {
				dismissProgressDialog();
				Message mesg = new Message();
				mesg.what = R.id.web_api_request_failed;
				mesg.obj = msg;
				handler.sendMessage(mesg);
			}

		});
	}

	private void forgotPwd() {
		Intent i = new Intent(ActivityLogin.this,ActivityForgetPassword.class);
		startActivity(i);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			AppManager.getActivityManager().exit();
			this.goback();
			return true;
		}else{
			return false;
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
			case R.id.web_api_request_start: // onStart
				showProgressDialog(ActivityLogin.this, "Logining");
				break;
			case R.id.web_api_request_completed: // onCompleted
				onCompleted(msg.obj);
				break;
			case R.id.web_api_request_failed: // onFailed
				onFailed((String)msg.obj);
				break;
			default:
				onFailed(getString(R.string.activity_login_login_failed));
				break;
			}
		}

		private void onFailed(String msg) {
			showToastLong(msg);
		}

		private void onCompleted(Object o){
			if(o == null){
				showToastLong(R.string.toast_tips_unknow_problem);
				return;
			}
			ResponseBean<?> responseBean = null;
			if(o.getClass().equals(ResponseBean.class)){
				responseBean = (ResponseBean<?>)o;
			}else{
				showToastLong(R.string.toast_tips_unknow_problem);
				return;
			}
			int code = responseBean.getCode();				
			switch(code){
			case ApiResultCode.SUCCESS_1001:
				success(responseBean);
				break;
			case ApiResultCode.TIME_OUT_1005:
				timeOut(responseBean);
				break;
			case ApiResultCode.WRONG_PWD_1003:
				showToastShort(responseBean.getMsg() + "");
				break;
			case ApiResultCode.ACOUNT_FROZEN_1004:
				showToastShort(responseBean.getMsg() + "");
				break;
			default:
				showToastShort(responseBean.getMsg() + "");
				break;
			}
		}

		private void success(ResponseBean<?> responseBean){
			try {
				User u = (User) responseBean.getData();
				u.setPhone_number(et_username.getText().toString());
				// 保存站点经纬度到数据库！！！
				u.setLatitude(u.getLocation().latitude);
				u.setLongitude(u.getLocation().longitude);
				Cache.getInstance().setUser(u);
				UserDao dao = new UserDao();
				dao.insert(u);
				SharePreferenceHelper.put(Constants.LOGIN, true);
				SharePreferenceHelper.put(Constants.UID, u.getUid());
				SharePreferenceHelper.put(Constants.PASSWORD, 
						et_password.getText().toString()+"");
				Intent i = new Intent(ActivityLogin.this,ActivityHomeNew.class);
				startActivity(i);
			} catch(Exception e){
				showToastShort(R.string.toast_tips_unknow_problem);
				e.printStackTrace();
				Log.e(TAG, "用户登录onSuccess异常，成功返回，但是无法转换为User对象，" 
						+ "请检查后台返回数据格式是否正确");
			}
		}

		private void timeOut(ResponseBean<?> responseBean) {
			showToastLong(responseBean.getMsg());
			Log.e(TAG, "登录失败:time out:" + responseBean.getMsg() );
		}

	};
}
