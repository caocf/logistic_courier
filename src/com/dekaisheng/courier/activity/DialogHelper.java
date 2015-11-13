package com.dekaisheng.courier.activity;

import android.app.DialogFragment;

public class DialogHelper {

	public static DialogFragment getWaitDialog(){
		return new WaitDialog();
	}
	
	public static DialogFragment getSimpleTips(String tips, IDialogListener listener){
		SimpleTipsDialog dialog = new SimpleTipsDialog(tips, listener);
		dialog.setTips(tips);
		dialog.setOkListener(listener);
		return dialog;
	}
	
}
