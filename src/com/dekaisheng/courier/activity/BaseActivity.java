package com.dekaisheng.courier.activity;
import com.dekaisheng.courier.MyActivity;
import com.dekaisheng.courier.cache.Cache;
import com.dekaisheng.courier.core.webapi.bean.User;
import com.dekaisheng.courier.util.log.Log;
import com.dekaisheng.courier.view.CustomProgressDialog;
import com.dekaisheng.courier.view.ProcessProgressDialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

public class BaseActivity extends MyActivity {

	//protected DialogFragment waitDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, this.getLocalClassName() + " Activity onCreate");
	}

	@Override
	protected void onPause() {
	//	hideWaitDialog();
		super.onPause();
		Log.i(TAG, this.getLocalClassName() + "Activity onPause");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, this.getLocalClassName() + "Activity onResume");
	}

//	@Override
//	protected void onSaveInstanceState(Bundle outState) {
//		// Activity被kill前，先将waitDialog置null，这样就不会再被调用导致crash
//		try{
//			hideWaitDialog();
//		}catch(Exception e){
//			e.printStackTrace();
//		}finally{
//			this.waitDialog = null;
//		}
//		super.onSaveInstanceState(outState);
//		Log.i(TAG, this.getLocalClassName() + "Activity onSaveInstanceState");
//	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i(TAG, this.getLocalClassName() + "Activity onDestroy");
	}

//	protected synchronized void showWaitDialog() {
//		if(this.waitDialog == null){
//			this.waitDialog = new WaitDialog();
//		}
//		if(!this.waitDialog.isAdded()){
//			this.waitDialog.show(getFragmentManager(), "");
//			Log.i(TAG, "wait dialog dismiss isHidden=" + this.waitDialog.isHidden());
//			Log.i(TAG, "wait dialog dismiss isAdded=" + this.waitDialog.isAdded());
//		}
//	}

//	protected synchronized void hideWaitDialog() {
//		if (waitDialog != null) {
//			if(MyApplication.getApplication().isActivityRunning(this)){
//				try{
//					this.waitDialog.dismiss();
//					Log.i(TAG, "wait dialog dismiss isHidden=" + this.waitDialog.isHidden());
//					Log.i(TAG, "wait dialog dismiss isAdded=" + this.waitDialog.isAdded());
//				}catch(Exception e){
//					Log.e(TAG, "关闭对话框失败：" + e.getMessage());
//				}
//			}
//		}
//	}

	private Dialog dialog;
	
	/**
	 * 整体加载
	 * 
	 * @param context
	 */
	protected void showProgress(Context context) {
		if (dialog != null) {
			dialog.cancel();
		}
		dialog = new CustomProgressDialog(context);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}
	
	/**
	 * 处理类加载
	 * 
	 * @param context
	 * @param msg
	 */
	protected void showProgressDialog(Context context, String msg) {
		if (dialog != null) {
			dialog.cancel();
		}
		dialog = new ProcessProgressDialog(context, msg);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}
	
	/**
	 * 关闭dialog
	 */
	protected void dismissProgressDialog() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
			dialog = null;
		}
	}
	
	
	/**
	 * 获取用户，如果获取不到则回到登录页面
	 * @return
	 */
	protected User getUser(){
		User u = Cache.getInstance().getUser();
		if(u == null){
			logOff();
			return null;
		}
		return u;
	}

}
