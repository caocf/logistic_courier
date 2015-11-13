package com.dekaisheng.courier.lbs.poi;

import com.dekaisheng.courier.activity.BaseActivity;
import com.dekaisheng.courier.core.webapi.bean.UnFinishedOrderForm;
import com.dekaisheng.courier.home.UnFinishedOrderAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.dekaisheng.courier.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

public class ActivityPoiSearch extends BaseActivity 
       implements ConnectionCallbacks, OnConnectionFailedListener{

	@ViewInject(R.id.activity_poi_search_goback)
	private ImageView gobackImg;

	@ViewInject(R.id.activity_poi_search_input_search)
	private EditText searchInput;

	@ViewInject(R.id.activity_poi_search_img_search)
	private ImageView searchImg;

	@ViewInject(R.id.activity_poi_search_listview)
	private ListView listView;
	
	private UnFinishedOrderForm form;

	private GooglePoiService gpService;
	private boolean connected = false;
	private AdapterPoiSearch adapter;
	private final int POI_SEARCH_RESULT = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_poi_search);
		ViewUtils.inject(this);
		initData();
		gpService = new GooglePoiService(this, this, this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		gpService.start();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		gpService.stop();
	}
	
	@OnClick({R.id.activity_poi_search_goback,
		R.id.activity_poi_search_img_search})
	public void onClick(View v){
		switch(v.getId()){
		case R.id.activity_poi_search_goback:
			goback();
			break;
		case R.id.activity_poi_search_img_search:
			search();
			break;
		}
	}

	private void initData(){
		form = (UnFinishedOrderForm)getIntent()
				.getSerializableExtra(UnFinishedOrderAdapter.ORDER_EXTRA);
		if(form != null){
			if(form.address != null && !form.address.equals("")){
				searchInput.setText(form.address);
			}else{
				showToastLong("address can not be null!");
				searchInput.setText("null");
			}
		}
	}

	private void search() {
		if(this.connected){
			// 需要传入派送员的地理位置，用户地址，然后根据派送员当前位置设置一个三公里的经纬度范围
			LatLng lt = new LatLng(22.20, 113.90);
			LatLng br = new LatLng(22.70, 114.10);
			LatLngBounds bounds = new LatLngBounds(lt, br);
			gpService.search(searchInput.getText().toString(), 
					 bounds, null, new IPoiListener() {
				
				@Override
				public void onPoiSearchCallback(AutocompletePredictionBuffer buffer) {
					Message msg = new Message();
					msg.what = POI_SEARCH_RESULT;
					msg.obj = buffer;
					handler.sendMessage(msg);
				}
			});
		}
	}

	private void goback() {
		this.finish();
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		connected = false;
	}

	@Override
	public void onConnected(Bundle arg0) {
		connected = true;
		search();
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		
	}

	private Handler handler = new Handler(){
		
		@Override
		public void handleMessage(Message msg){

			switch(msg.what){
			case POI_SEARCH_RESULT:
				handlePoi((AutocompletePredictionBuffer)msg.obj);
				break;
			}
		}

		private void handlePoi(AutocompletePredictionBuffer buffer) {
			if(buffer != null && buffer.getCount() > 0){
				String desc = buffer.get(0).getDescription();
				String dd = buffer.get(0).getPlaceId();
				showToastLong(desc + dd);
				adapter = new AdapterPoiSearch(ActivityPoiSearch.this,buffer);
				listView.setAdapter(adapter);
			}else{
				showToastLong("找不到");
			}
		}
	};
}











