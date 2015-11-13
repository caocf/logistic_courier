package com.dekaisheng.courier.user;

import com.dekaisheng.courier.util.DisplayUtil;
import com.dekaisheng.courier.R;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

/**
 * 用户中心的弹出菜单，使用自定义的PopupWindow
 * @author Dorian
 *
 */
public class MyPopupMenu extends PopupWindow implements OnClickListener{

	private View conentView;

	public MyPopupMenu(final Activity context, OnClickListener listener) {  
		LayoutInflater inflater = LayoutInflater.from(context); 
		conentView = inflater.inflate(R.layout.popup_window_user_center, null);   

		this.setContentView(conentView);  
		this.setWidth(DisplayUtil.dip2px(160)); 
		this.setHeight(LayoutParams.WRAP_CONTENT);   
		/**
		 * 要实现触摸外部区域隐藏popupWin必须同时设置outSideTouchable和BackgroundDrawable
		 */
		this.setOutsideTouchable(true);
		// 刷新状态  
		this.update();  
		// 实例化一个ColorDrawable颜色为半透明  
		ColorDrawable dw = new ColorDrawable(0000000000);  
		//		 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作  
		this.setBackgroundDrawable(dw);  
		// mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);  
		// 设置SelectPicPopupWindow弹出窗体动画效果  
		// this.setAnimationStyle(R.style.AnimationPreview);  
		LinearLayout addTaskLayout = (LinearLayout) conentView  
				.findViewById(R.id.popup_win_layout_msg);  
		LinearLayout teamMemberLayout = (LinearLayout) conentView  
				.findViewById(R.id.popup_win_layout_logoff);  
		addTaskLayout.setOnClickListener(listener);  
		teamMemberLayout.setOnClickListener(listener);  
	}  

	/** 
	 * 显示popupWindow 
	 *  
	 * @param parent 
	 */  
	public void showPopupWindow(View parent) {  
		if (!this.isShowing()) {  
			// 以下拉方式显示popupwindow  
			this.showAsDropDown(parent, 0, DisplayUtil.dip2px(5));  
		} else {  
			this.dismiss();  
		}  
	}

	@Override
	public void onClick(View v) {

	}

}  

