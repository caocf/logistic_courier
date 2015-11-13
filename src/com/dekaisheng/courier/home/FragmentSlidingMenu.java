package com.dekaisheng.courier.home;

import com.dekaisheng.courier.R;
import com.dekaisheng.courier.activity.HelpActivity;
import com.dekaisheng.courier.cache.Cache;
import com.dekaisheng.courier.core.webapi.bean.User;
import com.dekaisheng.courier.user.ActivityUserInfo;
import com.dekaisheng.courier.util.bmp.ImageLoader;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * ActivityHomeNew的侧滑菜单Fragment 1.用户信息 2.订单查询 3.历史记录统计 4.历史记录列表
 * 
 * @author Dorian
 * 
 */
public class FragmentSlidingMenu extends Fragment implements OnClickListener {

	private ImageView portraitImg;
	private TextView userNameTxt;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		User u = Cache.getInstance().getUser();
		View v = inflater.inflate(R.layout.fragment_slidingmenu, container,
				false);
		v.findViewById(R.id.fragment_slidingmenu_layout_about_me)
				.setOnClickListener(this);
		v.findViewById(R.id.fragment_slidingmenu_layout_search)
				.setOnClickListener(this);
		v.findViewById(R.id.fragment_slidingmenu_layout_record)
				.setOnClickListener(this);
		v.findViewById(R.id.fragment_slidingmenu_layout_order_completed)
				.setOnClickListener(this);
		v.findViewById(R.id.fragment_slidingmenu_layout_help)
		.setOnClickListener(this);
		portraitImg = (ImageView) v
				.findViewById(R.id.fragment_slidingmenu_user_icon);
		userNameTxt = (TextView) v
				.findViewById(R.id.fragment_slidingmenu_user_name);
		ImageLoader loader = new ImageLoader(getActivity());
		if (u != null) {
			loader.into(portraitImg, u.getPortrait(), -1, -1);
			userNameTxt.setText(u.getUsername());
		} else {
			userNameTxt.setText("");
		}
		return v;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fragment_slidingmenu_layout_about_me:
			user();
			break;
		case R.id.fragment_slidingmenu_layout_search:
			search();
			break;
		case R.id.fragment_slidingmenu_layout_record:
			record();
			break;
		case R.id.fragment_slidingmenu_layout_order_completed:
			completedOrder();
			break;
		case R.id.fragment_slidingmenu_layout_help:
			help();
			break;
		}
	}

	private void help() {
		Intent i = new Intent(getActivity(), HelpActivity.class);
		startActivity(i);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case ActivityHomeNew.CHANGE_PORTRAIT:
			updatePortrait(resultCode, data);
			break;
		default:
			break;
		}
	}

	private void user() {
		Intent i = new Intent(getActivity(), ActivityUserInfo.class);
		startActivityForResult(i, ActivityHomeNew.CHANGE_PORTRAIT);
	}

	private void completedOrder() {
		Intent i = new Intent(getActivity(), ActivityOrderHistory.class);
		startActivity(i);
	}

	private void record() {
		Intent i = new Intent(getActivity(), ActivityRecord.class);
		startActivity(i);
	}

	private void search() {
		Intent i = new Intent(getActivity(), ActivityOrderSearch.class);
		startActivity(i);
	}

	private void updatePortrait(int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			User u = Cache.getInstance().getUser();
			if (u != null) {
				ImageLoader loader = new ImageLoader(this.getActivity());
				loader.into(portraitImg, u.getPortrait(), -1, -1);
			}
		}
	}

}
