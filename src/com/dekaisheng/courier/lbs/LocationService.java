package com.dekaisheng.courier.lbs;

import com.dekaisheng.courier.MyApplication;
import com.dekaisheng.courier.cache.ApiResultCode;
import com.dekaisheng.courier.cache.Cache;
import com.dekaisheng.courier.core.webapi.OnCallback;
import com.dekaisheng.courier.core.webapi.bean.ResponseBean;
import com.dekaisheng.courier.core.webapi.bean.User;
import com.dekaisheng.courier.core.webapi.service.AcountService;
import com.dekaisheng.courier.util.log.Log;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;

/**
 * 
 * 定位服务
 * 
 * @author Dorian
 *
 */
public class LocationService {

	private final String TAG = "Location";
	private LocationManager manager;
	private Handler handler;
	public String bestProvider;
	public long minTime =1000;    // 默认1s 定位一次。
	public float minDistance = 0;    // 定时发送一次位置信息，不管是否变化
	public static final int MSG_CODE = 1008611;

	private boolean isGpsRemove = true;
	private boolean isNetworkRemove = true;
	private boolean isListening = false;
	private Location oldLocation;
	private User user ;
	private AcountService as ;
	public LocationService(Context context){
		manager = (LocationManager)MyApplication.getApplication()
				.getSystemService(Context.LOCATION_SERVICE);
		bestProvider =getProvider(); // gps
		user = Cache.getInstance().getUser();
		as = new AcountService();
		Log.i(TAG, bestProvider);
	}

	public void stop(){
		if(manager != null && isListening){
			removeNetwork();
			removeGps();
			isListening = false;
			manager = null;
		}
	}

	public void start(){
		if(!isListening){
			addNetwork();
			addGps();
			isListening = true;
		}
	}

	protected final LocationListener gpsLocListener = new LocationListener() {

		@Override
		public void onLocationChanged(Location loc) {
			if(loc == null) 
				loc=manager.getLastKnownLocation(bestProvider);
			if(isBetterLocation(oldLocation, loc)){
				oldLocation = loc;
				handlerLocation(loc);
				removeNetwork();
			}
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.i(TAG, "onStatusChanged:" + provider + ":" + status);
			if(provider.equals(LocationManager.GPS_PROVIDER)){
				if(status == LocationProvider.OUT_OF_SERVICE){
					addNetwork();
				}else if(status == LocationProvider.AVAILABLE){
					// provider变为可用
				}else if(status == LocationProvider.TEMPORARILY_UNAVAILABLE){
					// 临时不可用
					addNetwork();
				}
			}
		}

		@Override
		public void onProviderEnabled(String provider) {
			Log.i(TAG, "onProviderEnabled:" + provider);
		}

		@Override
		public void onProviderDisabled(String provider) {
			Log.i(TAG, "onProviderDisabled:" + provider);
			addNetwork();
		} 

	}; 

	protected final LocationListener networkLocListener = new LocationListener(){

		@Override
		public void onLocationChanged(Location location) {
			handlerLocation(location);
		}

		@Override
		public void onProviderDisabled(String provider) {
			Log.i(TAG, "networkLocListener onProviderDisabled:" + provider);
		}

		@Override
		public void onProviderEnabled(String provider) {
			Log.i(TAG, "networkLocListener onProviderEnabled:" + provider);
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.i(TAG, "networkLocListener onStatusChanged:" + provider + ":" + status);
		}

	};

	private void handlerLocation(Location loc){
		/**
		 * Activity绑定Service时可以通过传入Handler来接收定位信息
		 */
		if(loc == null){
			loc=manager.getLastKnownLocation(bestProvider);
		}
		Log.i(TAG, "Location:" + loc.getProvider() + ":(" + loc.getLatitude() + "," + loc.getLongitude() + ")");
		if(handler != null){
			Message msg = new Message();
			msg.what = MSG_CODE;
			msg.obj = loc;
			handler.sendMessage(msg);
		}
		uploadLocation(loc);
	}

	/**
	 * 上传位置到服务端
	 * @param loc
	 * @param u
	 */
	private void uploadLocation(Location loc) {
		if(user== null){
			// 说明用户没有登录，可以直接返回
			return ;
		}
		as.sendLocation(user.getToken(), user.getUid(), 
				 loc.getLongitude(), loc.getLatitude(), new OnCallback(){

			@Override
			public void onCompleted(ResponseBean<?> rb) {
				if(rb == null){
					Log.i(TAG, "上传派送员位置失败 onCompleted ResponseBean为null");
					return;
				}
				if(rb.getCode() == ApiResultCode.SUCCESS_1001){
					Log.i(TAG, "上传派送员位置" + rb.getCode() + ":" + rb.getMsg());
				}else{
					Log.i(TAG, rb.getCode() + ":" + rb.getMsg());
				}
			}

			@Override
			public void onFailure(String msg) {
				/**
				 * 由于是需要实时位置，所以，上传失败就不管了
				 */
				Log.i(TAG, "上传派送员位置失败：" + msg);
			}
		});
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	private Boolean isBetterLocation(Location oldLocation,Location newLocation){
		if(oldLocation == null){
			return true;
		}
		int twoMinus = 120000;
		long timeDelta = oldLocation.getTime() - newLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > twoMinus;
		boolean isSignificantlyOlder = timeDelta < twoMinus;
		boolean isNewer = timeDelta > 0;
		if(isSignificantlyNewer){
			return true;
		}
		else if(isSignificantlyOlder){
			return false;
		}
		float accuracyDelta = oldLocation.getAccuracy() - newLocation.getAccuracy();

		if(accuracyDelta < 0){
			return true;
		}
		else if(isNewer && !(accuracyDelta > 0)){
			return true;
		}
		else if(isNewer && !(accuracyDelta > 200.0) 
				&& isSameProvider(oldLocation.getProvider(),newLocation.getProvider())){
			return true;
		}
		return false;
	}

	private boolean isSameProvider(String oldProvider,String newProvider){
		if(oldProvider == null){
			return newProvider == null;
		}
		return oldProvider.equals(newProvider);
	}

	private String getProvider(){
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);// 不查询海拔？
		criteria.setBearingRequired(false); // 不查询方位角？
		criteria.setCostAllowed(false);     // 不要钱
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		return this.manager.getBestProvider(criteria, true);
	}

	private void removeNetwork(){
		if(!isNetworkRemove){
			manager.removeUpdates(networkLocListener);
			isNetworkRemove = true;
			Log.i(TAG, " remove network listener");
		}
	}

	private void removeGps(){
		if(!isGpsRemove){
			manager.removeUpdates(gpsLocListener);
			isGpsRemove = true;
			Log.i(TAG, " remove gps listener");
		}
	}

	private void addNetwork(){
		if(isNetworkRemove){
			manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 
					minTime, minDistance, networkLocListener);
			isNetworkRemove = false;
			Log.i(TAG, " add network listener");
		}
	}

	private void addGps(){
		if(isGpsRemove){
			manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, 
					minDistance, gpsLocListener);
			isGpsRemove = false;
			Log.i(TAG, " add gps listener");
		}
	}

	/**
	 * 设置更新位置的最短时间，由于时间间隔发生改变，
	 * 所以需要先移除之前的Listener，然后再重新添加Listener
	 * @param minTime
	 */
	public void setMinTime(long minTime){
		this.minTime = minTime;
		if(!isGpsRemove){
			removeGps();
			addGps();
		}
		if(!isNetworkRemove){
			removeNetwork();
			addNetwork();
		}
	}

	public void openGPSd(Activity ac){
		if(manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
				|| manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
			return;
		}else{
			Intent i = new Intent(Settings.ACTION_SECURITY_SETTINGS);
			ac.startActivityForResult(i, 0);
		}
	}

}

















