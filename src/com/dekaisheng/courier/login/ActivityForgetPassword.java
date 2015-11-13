package com.dekaisheng.courier.login;

import com.dekaisheng.courier.activity.BaseActivity;
import com.dekaisheng.courier.core.webapi.OnCallback;
import com.dekaisheng.courier.core.webapi.bean.ResponseBean;
import com.dekaisheng.courier.core.webapi.service.AcountService;
import com.dekaisheng.courier.util.StringUtils;
import com.dekaisheng.courier.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ActivityForgetPassword extends BaseActivity {

	@ViewInject(R.id.activity_forget_pwd_txt_send)
	private EditText et_mail;
	@ViewInject(R.id.activity_forget_pwd_btn_submit)
	private Button submit;
	@ViewInject(R.id.veiw_simple_custom_header_goback_2)
	private Button gobackBtn;
	@ViewInject(R.id.veiw_simple_custom_header_title_2)
	private TextView titleTxt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_forget_password);
		ViewUtils.inject(this);
		initView();
	}

	private void initView() {
		titleTxt.setText(getString(R.string.activity_forget_password_title));
		submit.setEnabled(false);
		et_mail.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (StringUtils.isValidEmail(s.toString())) {
					submit.setEnabled(true);
				} else {
					submit.setEnabled(false);
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@OnClick({ R.id.veiw_simple_custom_header_goback_2,
			R.id.activity_forget_pwd_btn_submit })
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.veiw_simple_custom_header_goback_2:
			goback();
			break;
		case R.id.activity_forget_pwd_btn_submit:
			submit();
			break;
		}
	}

	private void goback() {
		this.finish();
	}

	private void submit() {
		AcountService as = new AcountService();
		as.forgotPassword(et_mail.getText().toString(), new OnCallback() {
			@Override
			public void onStart() {
				showProgressDialog(ActivityForgetPassword.this, "Sending");
			}

			@Override
			public void onCompleted(ResponseBean<?> rb) {
				dismissProgressDialog();
				if (rb != null) {
					showToastShort(rb.getMsg());
				}
			}

			@Override
			public void onFailure(String msg) {
				dismissProgressDialog();
				showTipsDialog(msg);
			}
		});

	}
}
