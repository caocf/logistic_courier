package com.dekaisheng.courier.view;
import com.dekaisheng.courier.R;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;

public class ProcessProgressDialog extends Dialog {

	public ProcessProgressDialog(Context context, String strMessage) {
		this(context, R.style.CustomProgressDialog, strMessage);
	}

	public ProcessProgressDialog(Context context, int theme, String strMessage) {
		super(context, theme);
		this.setContentView(R.layout.process_progressdialog);
		this.getWindow().getAttributes().gravity = Gravity.CENTER;
		TextView tvMsg = (TextView) this.findViewById(R.id.id_tv_loadingmsg);
		if (tvMsg != null) {
			tvMsg.setText(strMessage);
		}
	}
}
