package com.dekaisheng.courier.util;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dekaisheng.courier.MyApplication;
import com.dekaisheng.courier.R;

/**
 * Created by tfl on 2015/7/20.
 */
public class UIUtils {

	public static Context getContext() {
		return MyApplication.getApplication().getApplicationContext();//.getContext();
	}

	public static void showToast(String content) {
		Toast.makeText(getContext(), content, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 从资源文件中获取对象
	 * 
	 * @return
	 */
	public static Resources getResources() {
		return getContext().getResources();
	}

	public static String getString(int id) {
		return getResources().getString(id);
	}

	public static String[] getStringArray(int id) {
		return getResources().getStringArray(id);
	}

	public static Drawable getDrawable(int id) {
		return getResources().getDrawable(id);
	}

	/**
	 * 得到自定义的progressDialog
	 * 
	 * @param context
	 * @param msg
	 * @return
	 */
	@SuppressLint("InflateParams")
	public static Dialog createLoadingDialog(Context context, String msg) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.loading_dialog, null);// 得到加载view
		LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
		// main.xml中的ImageView
		ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
		TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
		// 加载动画
		Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
				context, R.anim.loading_animation);
		// 使用ImageView显示动画
		spaceshipImage.startAnimation(hyperspaceJumpAnimation);
		tipTextView.setText(msg);// 设置加载信息
		Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog
		loadingDialog.setCancelable(false);// 不可以用“返回键”取消
		loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
		return loadingDialog;
	}

	/**
	 * 自定义Dialog
	 * @param context
	 * @param content
	 */
	public static void myAlertDialog(Context context, String content) {
		final AlertDialog myDialog = new AlertDialog.Builder(context).create();
		myDialog.show();
		myDialog.getWindow().setContentView(R.layout.alert_dialog);
		TextView textView = (TextView) myDialog.getWindow().findViewById(
				R.id.alert_content);
		textView.setText(content);
		myDialog.getWindow().findViewById(R.id.ok)
		.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				myDialog.dismiss();
			}
		});
	}
}
