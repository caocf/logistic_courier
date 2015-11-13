package com.dekaisheng.courier.activity;

import com.dekaisheng.courier.R;

import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

/**
 * 等待对话框
 * @author Dorian
 *
 */
public class WaitDialog extends DialogFragment{

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		View view = inflater.inflate(R.layout.wait_dialog, container); 
		ImageView img = (ImageView) view.findViewById(R.id.wait_dialog_img);
		Animation rotateAnimaLow = AnimationUtils.loadAnimation(getActivity(),
				R.anim.anima_wait_dialog);
		img.startAnimation(rotateAnimaLow);
		return view; 
	}

}
