package com.dekaisheng.courier.view;


import com.dekaisheng.courier.R;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
public class CustomProgressDialog extends Dialog {

	public CustomProgressDialog(Context context) {  
        this(context, R.style.CustomProgressDialog);  
    }  
	
	public CustomProgressDialog(Context context, int theme) {  
        super(context, theme);  
        this.setContentView(R.layout.customprogressdialog);  
        this.getWindow().getAttributes().gravity = Gravity.CENTER;  
    }  
}
