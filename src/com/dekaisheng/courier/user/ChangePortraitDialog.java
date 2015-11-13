package com.dekaisheng.courier.user;

import com.dekaisheng.courier.R;

import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioGroup.LayoutParams;
import android.widget.TextView;

public class ChangePortraitDialog extends 
DialogFragment implements OnClickListener{

	private TextView camera;

	private TextView dicm;

	private TextView cancel;
	
	private IOnClickListener listener;
	
	public void setClickListener(IOnClickListener listener){
		this.listener = listener;
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
		/* 设置布局位置为底部:Bottom */
		WindowManager.LayoutParams layoutParams = 
				getDialog().getWindow().getAttributes();
		layoutParams.height = LayoutParams.WRAP_CONTENT;
		layoutParams.width = LayoutParams.MATCH_PARENT;
		layoutParams.gravity = Gravity.BOTTOM;
		getDialog().getWindow().setAttributes(layoutParams);	
		getDialog().getWindow().setWindowAnimations(R.style.dialog_anima_style_portrait); // 设置弹出动画
	}

	@Override
	public View onCreateView(LayoutInflater inflater, 
			ViewGroup container, Bundle savedInstanceState) {
		/* 设置背景透明 */
		getDialog().getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.TRANSPARENT));
		/* 无标题栏 */
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		View view = inflater.inflate(
				R.layout.change_portrait_dialog, container); 
		this.camera = (TextView)view.findViewById(
				R.id.change_portrait_dialog_txt_camera);
		this.dicm = (TextView)view.findViewById(
				R.id.change_portrait_dialog_txt_dicm);
		this.cancel = (TextView)view.findViewById(
				R.id.change_portrait_dialog_txt_cancle);
		this.camera.setOnClickListener(this);
		this.dicm.setOnClickListener(this);
		this.cancel.setOnClickListener(this);
		return view; 
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch(id){
		case R.id.change_portrait_dialog_txt_camera:
			listener.onCamera();
			break;
		case R.id.change_portrait_dialog_txt_dicm:
			listener.onDicm();
			break;
		case R.id.change_portrait_dialog_txt_cancle:
			listener.onCancel();
			break;
		default:break;
		}
	}

	public interface IOnClickListener{
		public void onCamera();
		public void onDicm();
		public void onCancel();
	}
}
