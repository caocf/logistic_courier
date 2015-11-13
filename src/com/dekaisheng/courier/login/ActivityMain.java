package com.dekaisheng.courier.login;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;
import com.dekaisheng.courier.activity.BaseActivity;
import com.dekaisheng.courier.cache.Cache;
import com.dekaisheng.courier.cache.Constants;
import com.dekaisheng.courier.cache.SharePreferenceHelper;
import com.dekaisheng.courier.core.database.dao.UserDao;
import com.dekaisheng.courier.core.webapi.bean.User;
import com.dekaisheng.courier.home.ActivityHomeNew;
import com.dekaisheng.courier.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

/**
 * 启动页面，程序入口
 * 
 * @author Dorian
 * 
 */
public class ActivityMain extends BaseActivity {

	@ViewInject(R.id.activity_main_splash_logo)
	private ImageView logoImg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!isTaskRoot()) {
			finish();
			return;
		}
		this.setContentView(R.layout.activity_main);
		ViewUtils.inject(this);
		readyGo(); // 如果失败，则下次使用app再注册
		updateApp();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@OnClick(R.id.activity_main_splash_logo)
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.activity_main_splash_logo:
			break;
		default:
			break;
		}
	}

	private void readyGo() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(1500);
					Boolean login = SharePreferenceHelper.getBoolean(
							Constants.LOGIN, false);
					if (login) {
						toMain();
					} else {
						toLogin();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {

				}
			}

		}).start();
	}

	private void toLogin() {
		Intent i = new Intent(ActivityMain.this, ActivityLogin.class);
		startActivity(i);
		finish();
	}

	private void toMain() {
		// 判断：1.安装（到介绍页面，然后到登录页面），2.已登录（直接到主页），3.未登录（到登录页面）
		String uid = SharePreferenceHelper.getString(Constants.UID, "");
		try {
			if (SharePreferenceHelper.getBoolean(Constants.LOGIN, false)) {
				UserDao ud = new UserDao();
				User u = ud.selectById(uid);
				Cache.getInstance().setUser(u);
				Intent i = new Intent(ActivityMain.this, ActivityHomeNew.class);
				startActivity(i);
			} else {
				Intent i = new Intent(ActivityMain.this, ActivityLogin.class);
				startActivity(i);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		finish();
	}

	/**
	 * 自动更新平台
	 */
	private void updateApp() {
		UmengUpdateAgent.setDefault();
		UmengUpdateAgent.setUpdateAutoPopup(false);
		UmengUpdateAgent.setUpdateOnlyWifi(true);
		UmengUpdateAgent.setDeltaUpdate(true); // 增量更新
		UmengUpdateAgent.setRichNotification(true); // 启用高级样式，4.1以上支持，暂停/继续和取消按钮
		UmengUpdateAgent.setUpdateUIStyle(UpdateStatus.STYLE_NOTIFICATION);
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
			@Override
			public void onUpdateReturned(int statusCode,
					final UpdateResponse updateResponse) {
				if (UpdateStatus.Yes == statusCode) {
					// 有更新
					UmengUpdateAgent.showUpdateNotification(ActivityMain.this,
							updateResponse); // 使用通知栏进行提醒
					SharePreferenceHelper.put(Constants.AppHasNew, true);
				}

				if (UpdateStatus.No == statusCode) {
					// 没有更新
					SharePreferenceHelper.put(Constants.AppHasNew, false);
				}
			}
		});
		// 不知为何，点击通知栏无效。只好用静默下载了。
		// UmengUpdateAgent.update(SplashActivity.this);
		// 静默安装
		UmengUpdateAgent.silentUpdate(ActivityMain.this);
	}
}
