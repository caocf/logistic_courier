package com.dekaisheng.courier.lbs.geocode;

import java.util.ArrayList;
import java.util.List;

import com.dekaisheng.courier.Config;
import com.dekaisheng.courier.activity.BaseActivity;
import com.dekaisheng.courier.core.webapi.bean.UnFinishedOrderForm;
import com.dekaisheng.courier.home.UnFinishedOrderAdapter;
import com.dekaisheng.courier.lbs.ActivityLocationMap;
import com.dekaisheng.courier.util.log.Log;
import com.dekaisheng.courier.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

/**
 * 地理编码主页
 * @author Dorian
 *
 */
public class ActivityGoogleGeoCode extends BaseActivity implements OnItemClickListener{

	@ViewInject(R.id.activity_googel_geo_code_goback)
	private ImageView gobackImg;

	@ViewInject(R.id.activity_googel_geo_code_input_search)
	private EditText searchInput;

	@ViewInject(R.id.activity_googel_geo_code_img_search)
	private ImageView searchImg;

	@ViewInject(R.id.activity_googel_geo_code_listview)
	private ListView listView;

	private UnFinishedOrderForm form;
	private GeocodeAdapter adapter;
	private ArrayList<Address> address;

	private final int MAX_RESULT = 50;      // 地理编码返回的最大的结果数量
	private final int GEO_CODE_SUCCESS = 0;
	private final int GEO_CODE_FAILED = 1;
	public static final String SELECTED_FORM = "order";
	public static final String ADDRESS_TAG = "address";
	public static final String SELECTED_INDEX_TAG = "index";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_google_geo_code);
		ViewUtils.inject(this);
		initData();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

    @OnClick({R.id.activity_googel_geo_code_goback,
    	R.id.activity_googel_geo_code_img_search})
    public void onClick(View v){
    	switch(v.getId()){
    	case R.id.activity_googel_geo_code_goback:
    		goback();
    		break;
    	case R.id.activity_googel_geo_code_img_search:
    		search();
    		break;
    	}
    }
    
	private void search() {
		geocode(this.searchInput.getText().toString());
	}

	private void goback() {
		this.finish();
	}

	private void initData(){
		address = new ArrayList<Address>();
		adapter = new GeocodeAdapter(this, address);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		form = (UnFinishedOrderForm)getIntent()
				.getSerializableExtra(UnFinishedOrderAdapter.ORDER_EXTRA);
		if(form != null){
			if(form.address != null && !form.address.equals("")){
				geocode(form.address);
				searchInput.setText(form.address);
			}else{
				showToastLong(R.string.toast_tips_geocode_address_null);
				searchInput.setText("");
			}
		}
	}

	private void geocode(String name){
		GeocodeService code = new GeocodeService(this);
		code.code(name, MAX_RESULT, new IGeocodeCallback(){

			@Override
			public void onSuccess(List<Address> result) {
				if(result != null){
					Message msg = new Message();
					msg.what = GEO_CODE_SUCCESS;
					msg.obj = result;
					handler.sendMessage(msg);
				}else{
					Message msg = new Message();
					msg.what = GEO_CODE_FAILED;
					msg.obj = "编码结果集合为空";
					handler.sendMessage(msg);
				}
			}

			@Override
			public void onFailed(String ee) {
				Message msg = new Message();
				msg.what = GEO_CODE_FAILED;
				msg.obj = ee;
				handler.sendMessage(msg);
				Log.e(TAG, "地理编码失败！" + ee);
			}

		});
		showProgressDialog(ActivityGoogleGeoCode.this, "Loading");
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg){
			dismissProgressDialog();
			switch(msg.what){
			case GEO_CODE_SUCCESS:
				handleGeocodeSuccess((List<Address>)msg.obj);
				break;
			case GEO_CODE_FAILED:
				handleGeocodeFailed((String)msg.obj);
				break;
			}
		}

		private void handleGeocodeFailed(String obj) {
			showToastLong(obj);
		}

		private void handleGeocodeSuccess(List<Address> result) {
			address.clear();
			address.addAll(result);
			adapter.notifyDataSetChanged();
			if(Config.DEBUG){
				showToastShort("地理编码成功，返回地址数量为：" + result.size());
				Log.e(TAG, "地理编码成功，返回地址数量为：" + result.size());
			}
		}
	};

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent i = new Intent(this,ActivityLocationMap.class);
		i.putExtra(ADDRESS_TAG, address);
		i.putExtra(SELECTED_INDEX_TAG, arg2);
		i.putExtra(SELECTED_FORM, this.form);
		startActivity(i);
	}

}







