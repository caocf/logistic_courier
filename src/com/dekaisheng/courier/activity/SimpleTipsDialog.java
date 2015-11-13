package com.dekaisheng.courier.activity;

import com.dekaisheng.courier.R;

import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

/**
 * 简单提示框，显示一段文本，IDialogListener可以为null
 * @author Dorian
 *
 */
public class SimpleTipsDialog extends DialogFragment implements OnClickListener{

	private TextView tipsTxt;
	private Button okBtn;
	private IDialogListener okListener;
	private String tips;
	
	public SimpleTipsDialog(String tips, IDialogListener okListener){
		this.okListener = okListener;
		this.tips = tips;
	}
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		View view = inflater.inflate(R.layout.simple_tips_dialog, container); 
		this.tipsTxt = (TextView)view.findViewById(R.id.tips_dialog_info);
		this.okBtn = (Button)view.findViewById(R.id.tips_dialog_ok);
		this.okBtn.setOnClickListener(this);
		this.tipsTxt.setText(tips + " ");
		return view; 
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if(id == R.id.tips_dialog_ok){
			if(this.okListener != null){
				this.okListener.onOk();
			}
			this.dismiss();
		}
	}

	public void setOkListener(IDialogListener okListener){
		this.okListener = okListener;
	}
	
	public void setTips(String tips){
		this.tips = tips;
	}
	
}
