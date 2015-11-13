package com.dekaisheng.courier.login;

import com.dekaisheng.courier.activity.BaseActivity;
import com.dekaisheng.courier.R;

import android.os.Bundle;

/**
 * 进入app的过程界面
 * @author Dorian
 *
 */
public class ActivitySplash extends BaseActivity{


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_splash);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
