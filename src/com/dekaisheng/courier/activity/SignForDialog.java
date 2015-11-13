package com.dekaisheng.courier.activity;

import java.io.File;
import java.util.Date;

import com.dekaisheng.courier.cache.FilePath;
import com.dekaisheng.courier.home.ActivityHomeNew;
import com.dekaisheng.courier.util.cache.ExternalOverFroyoUtils;
import com.dekaisheng.courier.R;

import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.RadioGroup.LayoutParams;

public class SignForDialog extends DialogFragment implements OnClickListener{

	/*一次弹框对应一个文件*/
	public static String photoName;
	
	private ISignForListener listener;

	/**
	 * 用于保存被点击的Item索引，一次弹框，对应处理一个订单，也合理
	 */
	public static int index;

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
		layoutParams.width = LayoutParams.WRAP_CONTENT;
		layoutParams.gravity = Gravity.CENTER;
		getDialog().getWindow().setAttributes(layoutParams);	
		getDialog().getWindow().setWindowAnimations(R.style.dialog_anima_style_portrait); // 设置弹出动画
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		View view = inflater.inflate(R.layout.sign_for_dialog, container); 
		view.findViewById(R.id.sign_for_dialog_layout_hand_write).setOnClickListener(this);
		view.findViewById(R.id.sign_for_dialog_layout_photo).setOnClickListener(this);
		view.findViewById(R.id.sign_for_dialog_txt_not_complete).setOnClickListener(this);
		return view; 
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch(id){
		case R.id.sign_for_dialog_layout_hand_write:
			handWritten();
			break;
		case R.id.sign_for_dialog_layout_photo:
			photo();
			break;
		case R.id.sign_for_dialog_txt_not_complete:
			unFinished();
			break;
		default:break;
		}
	}

	public void setListener(ISignForListener listener){
		this.listener = listener;
	}

	private void handWritten() {
		if(this.listener != null){
			listener.onHandWritten();
		}
		this.dismiss();
	}

	private void photo() {
		if(this.listener != null){
			listener.onTakePhoto();
		}
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
		intent.addCategory("android.intent.category.DEFAULT");
		File file = ExternalOverFroyoUtils.getDiskCacheDir(
				this.getActivity(), FilePath.IMAGE);
		if(!file.exists()){
			file.mkdir();
		}
		photoName = new Date().getTime() + ".png";
		File bmp = new File(file.getAbsoluteFile() + File.separator + photoName);
		photoName = bmp.getAbsolutePath(); 
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(bmp)); 
		this.getActivity().startActivityForResult(intent, ActivityHomeNew.SIGN_TAKE_PHOTO_REQUEST); 
		this.dismiss();
	}

	private void unFinished() {
		if(this.listener != null){
			listener.onUnFinished();
		}
	}

	public interface ISignForListener{
		/**
		 * 用户签收，拍照上传证明
		 */
		public void onTakePhoto();
		
		/**
		 * 用户签收，手写（手机上写）上传证明
		 */
		public void onHandWritten();
		
		/**
		 * 无法送达的，
		 */
		public void onUnFinished();
	}
}
