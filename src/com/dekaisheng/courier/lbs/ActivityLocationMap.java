package com.dekaisheng.courier.lbs;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import com.dekaisheng.courier.activity.BaseActivity;
import com.dekaisheng.courier.core.webapi.bean.UnFinishedOrderForm;
import com.dekaisheng.courier.lbs.GoogleLocationService.MyBinder;
import com.dekaisheng.courier.lbs.geocode.ActivityGoogleGeoCode;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.dekaisheng.courier.R;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class ActivityLocationMap extends BaseActivity 
implements OnMapReadyCallback, OnMarkerClickListener, OnMarkerDragListener{

	private GoogleMap googleMap; 
	private boolean bind = false;
	public static final int LOC_CHANGE = 0;
	private List<Address> addresses;
	private int selected;
	private UnFinishedOrderForm form;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_location_map);
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

        if(status == ConnectionResult.SUCCESS){ 
    		MapFragment mapFragment = (MapFragment) getFragmentManager()
    				.findFragmentById(R.id.activity_location_map_google_map);
    		mapFragment.getMapAsync(this);	 
        }else{
        	// 弹框，说Google Service不可用，不会有地图了
			showToastLong("Google Service is not avaliable");
        }
	}

	@SuppressWarnings("unchecked")
	private void initData() {
		selected = getIntent().getIntExtra(ActivityGoogleGeoCode.SELECTED_INDEX_TAG, -1);
		addresses = (ArrayList<Address>)getIntent()
				.getSerializableExtra(ActivityGoogleGeoCode.ADDRESS_TAG);
		form = (UnFinishedOrderForm)getIntent()
				.getSerializableExtra(ActivityGoogleGeoCode.SELECTED_FORM);
	}

	private ServiceConnection connection = new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			MyBinder mb = (MyBinder)service;
			GoogleLocationService sv = mb.getService();
			sv.setHandler(handler);
			bind = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}

	};

	@Override
	protected void onDestroy() {
		if(bind){
			this.unbindService(connection);
			bind = false;
		}
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(bind == false){
//			bind = this.bindService(new Intent(this,
//					GoogleLocationService.class), connection, 
//					Context.BIND_AUTO_CREATE);
		}
	}

	@Override
	protected void onPause() {	
		if(bind){
			this.unbindService(connection);
			bind = false;
		}
		super.onPause();
	}

	@Override
	public void onMapReady(GoogleMap map) {
		/**
		 * map不会为null，取得map后就可已对map进行初始化，比如说设置地图
		 * 类型，设置地图中心，比例尺、缩放按钮等
		 */
		initData(); 
		this.googleMap = map;
		map.getUiSettings().setAllGesturesEnabled(true);
		map.getUiSettings().setMapToolbarEnabled(true);
		map.getUiSettings().setCompassEnabled(true);
		map.getUiSettings().setIndoorLevelPickerEnabled(true);
		map.getUiSettings().setMyLocationButtonEnabled(true);
		map.getUiSettings().setZoomControlsEnabled(true);
		map.setInfoWindowAdapter(new CustomsInfoWindow(this));

		if(this.selected > -1 && this.addresses != null 
				&& this.addresses.size() > this.selected){
			Iterator<Address> it = this.addresses.iterator();
			Address a = null;
			int i = 0;
			while(it.hasNext()){
				a = it.next();
				LatLng ll = new LatLng(a.getLatitude() , a.getLongitude());
				MapUtil mu = new MapUtil(googleMap);
				mu.addMarker(ll, form.recipient_name + "(" 
						+ form.recipient_phone_number + ")", 
						a.getAddressLine(0) + ","+ a.getAddressLine(1)+ "," + a.getAddressLine(2));
				if(i == this.selected){
					mu.animateToCenter(ll, null);
				}
				i++;			
			}
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
			case LOC_CHANGE:
				handleLocation((Location)msg.obj);
				break;
			default:break;
			}
		}

		private void handleLocation(Location obj) {
			if(obj == null || googleMap == null){
				return;
			}
			//			googleMap.clear(); // 需要重绘，所有覆盖物都要重新添加
			LatLng ll = new LatLng(obj.getLatitude()
					, obj.getLongitude());
			MapUtil mu = new MapUtil(googleMap);
			mu.addMarker(ll, "Marker","");
			//			mu.moveToCenter(ll);
		}

	};


	@Override
	public boolean onMarkerClick(Marker arg0) {
		return false;
	}

	@Override
	public void onMarkerDrag(Marker arg0) {

	}

	@Override
	public void onMarkerDragEnd(Marker arg0) {

	}

	@Override
	public void onMarkerDragStart(Marker arg0) {

	}


}
