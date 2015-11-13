package com.dekaisheng.courier;

import com.dekaisheng.courier.activity.IDialogListener;
import com.dekaisheng.courier.activity.SimpleTipsDialog;
import com.dekaisheng.courier.cache.Cache;
import com.dekaisheng.courier.cache.Constants;
import com.dekaisheng.courier.cache.SharePreferenceHelper;
import com.dekaisheng.courier.core.database.dao.UnFinishedOrderDao;
import com.dekaisheng.courier.core.database.dao.UserDao;
import com.dekaisheng.courier.core.database.entity.PushMessage;
import com.dekaisheng.courier.lbs.MyLocationService;
import com.dekaisheng.courier.login.ActivityLogin;
import com.dekaisheng.courier.util.log.Log;
import com.dekaisheng.courier.util.networkstate.NetworkType;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.Toast;

public abstract class MyActivity extends Activity{

	protected String TAG = "BasicActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		notifyApplicationOnActivityCreating();
		super.onCreate(savedInstanceState);
		try{
			notifyApplicationOnActivityCreated();	
			notifyApplicationOnActivityfterCreated();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		notifyApplicationOnActivityDestory();
	}

	@Override
	public void onBackPressed() { 
		back();
		super.onBackPressed();
	}

	/**
	 * 
	 * @Title: back  
	 * @Description: 告知application，activity按下了返回键，  
	 * @param       
	 * @return void  
	 * @throws
	 */
	protected void back() {
		MyApplication.getApplication().activityBack();
	}

	private void notifyApplicationOnActivityDestory() {
		MyApplication.getApplication().onActivityDestory(this);
	}

	private void notifyApplicationOnActivityfterCreated() {
		MyApplication.getApplication().onAfterActivityCreated(this);
	}

	private void notifyApplicationOnActivityCreated() {
		MyApplication.getApplication().onActivityCreated(this);
	}

	private void notifyApplicationOnActivityCreating() {
		MyApplication.getApplication().onActivityCreating(this);
	}

	/**
	 * 
	 * @Title: onConnected  
	 * @Description: 网络状态变为可用时调用，设置isNetworkValuable，记录网络状态 
	 * @param @param netType      
	 * @return void  
	 * @throws
	 */
	public void onConnected(NetworkType netType){
		Log.i(TAG,"onConnected BasicActivity");
	}

	/**
	 * 
	 * @Title: onDisConnected  
	 * @Description: 网络状态变为不可用时调用，设置isNetworkValuable，记录网络状态
	 * @param       
	 * @return void  
	 * @throws
	 */
	public void onDisConnected(){
		Log.i(TAG,"onDisConnected BasicActivity");
	}

	/**
	 *
	 * @Title: showToastLong
	 * @Description: 土司通知
	 * @param @param msg
	 * @return void
	 * @throws
	 */
	public void showToastLong(String msg){
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}

	/**
	 *
	 * @Title: showToastShort
	 * @Description: 土司通知
	 * @param @param msg
	 * @return void
	 * @throws
	 */
	public void showToastShort(String msg){
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	/**
	 *
	 * @Title: showToastLong
	 * @Description: 土司通知
	 * @param @param resId String资源的ID
	 * @return void
	 * @throws
	 */
	public void showToastLong(int resId){
		try{
			String msg = this.getResources().getString(resId);
			showToastLong(msg);
		}catch(Resources.NotFoundException e){
			Log.e(TAG, "Toast Can Not Found Resourse!" + e.getMessage());
		}
	}

	/**
	 *
	 * @Title: showToastShort
	 * @Description: 土司通知
	 * @param @param resId
	 * @return void
	 * @throws
	 */
	public void showToastShort(int resId){
		try{
			String msg = this.getResources().getString(resId);
			showToastShort(msg);
		}catch(Resources.NotFoundException e){
			Log.e(TAG,"Toast Can Not Found Resourse!" + e.getMessage());
		}
	}

	public void showTipsDialog(String tips, IDialogListener listener){
		if(isFinishing()){
			return;
		}
		if(!MyApplication.getApplication().isActivityRunning(this)){
			return;
		}
		SimpleTipsDialog std = new SimpleTipsDialog(tips, listener);
		std.show(getFragmentManager(), "");
	}

	public void showTipsDialog(String tips){
		this.showTipsDialog(tips, null);
	}

	public void showTipsDialog(int resID){
		String tips = this.getString(resID);
		this.showTipsDialog(tips);
	}
	
	public void showTipsDialog(int resID, IDialogListener listener){
		String tips = this.getString(resID);
		this.showTipsDialog(tips, listener);
	}

	/**
	 * 弹出问题框，必须要选择确定或者取消才能消失？还是触摸外部就消失？暂时触摸外部就消失
	 * @param question
	 * @param listener
	 */
//	public void showQuestionDialog(String question, IDialogListener listener){
//		if(isFinishing()){
//			return;
//		}
//		if(!MyApplication.getApplication().isActivityRunning(this)){
//			return;
//		}
//		SimpleQuestionDialog sqd = new SimpleQuestionDialog(question, listener);
//		sqd.show(getFragmentManager(), "");
//	}
	
//	public void showQuestionDialog(int resId, IDialogListener listener){
//		String str = this.getString(resId);
//		this.showQuestionDialog(str, listener);
//	}
	
	/**
	 * 接收处理Push通知，当app位于前台时，收到推送消息应该就近原则先处理消息（一般也是
	 * 发个Notification，然后跳到MessageCenter，但是有时候也需要在当前页面显示消息数量等）
	 * @param i
	 */
	public void onReceivePushMessage(PushMessage i){
		if(i != null){
			Log.i(TAG, i.key + i.message);
		}
	}

	/**
	 * 退出登录
	 * 1.删除用户的未完成订单
	 * 2.删除用户
	 * 3.停止定位服务
	 * 4.删除Cache对象的User实例
	 * 5.销毁所有activity实例并启动登陆页面
	 */
	public void logOff(){
		UnFinishedOrderDao orderDao = new UnFinishedOrderDao();
		orderDao.deleteAll();
		UserDao ud = new UserDao();
		ud.deleteAll();
		// 删除用户
		stopService(new Intent(this.getApplicationContext(), MyLocationService.class));
		SharePreferenceHelper.put(Constants.LOGIN, false);
		SharePreferenceHelper.put(Constants.PASSWORD, "");
		Cache.getInstance().setUser(null);
		AppManager.getActivityManager().exit();
		Intent i = new Intent(this, ActivityLogin.class);
		startActivity(i);
	}

}







