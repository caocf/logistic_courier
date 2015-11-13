package com.dekaisheng.courier.home;

import com.dekaisheng.courier.activity.BaseActivity;
import com.dekaisheng.courier.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

/**
 * 订单查询
 * @author Dorian
 *
 */
public class ActivityOrderSearch extends BaseActivity{

	@ViewInject(R.id.veiw_simple_custom_header_goback_2)
	private ImageView gobackBtn;

	@ViewInject(R.id.veiw_simple_custom_header_title_2)
	private TextView titleTxt;

	@ViewInject(R.id.activity_order_search_btn_query)
	private Button queryBtn;

	@ViewInject(R.id.activity_order_search_txt_query)
	private EditText queryTxt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_order_search);
		ViewUtils.inject(this);
		initView();
	}

	private void initView() {
		this.titleTxt.setText(this.getString(R.string.order_query_title));
		this.queryTxt.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_SEARCH){
					query();
					return true;
				}
				return false;
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@OnClick({R.id.veiw_simple_custom_header_goback_2
		,R.id.activity_order_search_btn_query})
	public void onClick(View v){
		switch(v.getId()){
		case R.id.veiw_simple_custom_header_goback_2:
			goback();
			break;
		case R.id.activity_order_search_btn_query:
			query();
			break;
		}
	}

	private void query() {
		if(queryTxt.getText() == null || queryTxt.getText().toString().equals("")){
			showToastShort(R.string.toast_tips_order_search_input_order_number);
			return;
		}
		Intent i = new Intent(this,ActivityLogisticsDetail.class);
		i.putExtra(ActivityLogisticsDetail.ORDER_NO, queryTxt.getText().toString());
		startActivity(i);
	}

	private void goback() {
		this.finish();
	}

}
