package com.dekaisheng.courier.lbs;

import com.dekaisheng.courier.cache.ApiResultCode;
import com.dekaisheng.courier.cache.Cache;
import com.dekaisheng.courier.core.webapi.OnCallback;
import com.dekaisheng.courier.core.webapi.bean.ResponseBean;
import com.dekaisheng.courier.core.webapi.bean.User;
import com.dekaisheng.courier.core.webapi.service.AcountService;
import com.dekaisheng.courier.util.log.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * 新的Google定位接口
 * @author Dorian
 *
 */
public class NewLocationService implements ConnectionCallbacks,
OnConnectionFailedListener, LocationListener{

	private GoogleApiClient mGoogleApiClient;
	private Handler handler;
	public static final int LOC_CHANGE = 10011;
	private final String TAG = "NetworkLocation";

	private static final LocationRequest REQUEST = 
			LocationRequest.create()
			.setInterval(1000 * 300)         // 5 seconds
			.setFastestInterval(5000)    // 
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

	public NewLocationService(Context context){
		mGoogleApiClient = new GoogleApiClient
				.Builder(context)
				.addApi(LocationServices.API)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.build();
	}

	public void connect() {
		if(!mGoogleApiClient.isConnected()){
			mGoogleApiClient.connect();
		}
	}

	public void disconnect() {
		if(mGoogleApiClient.isConnected()){
			mGoogleApiClient.disconnect();
		}
	}

	/**
	 * Implementation of {@link LocationListener}.
	 */
	@Override
	public void onLocationChanged(Location loc) {
		User u = Cache.getInstance().getUser();
		if(u != null){
			AcountService as = new AcountService();
			as.sendLocation(u.getToken(), u.getUid(), 
					loc.getLatitude(), loc.getLongitude(), new OnCallback(){

				@Override
				public void onCompleted(ResponseBean<?> rb) {
					if(rb.getCode() == ApiResultCode.SUCCESS_1001){
						Log.i("上传派送员位置成功：", rb.getCode() + ":" + rb.getMsg());
					}else{
						Log.i("上传派送员位置：", rb.getCode() + ":" + rb.getMsg());
					}
				}

				@Override
				public void onFailure(String msg) {
					Log.i("上传派送员位置失败：", msg);
				}

			});
		}
		if(handler != null){
			Message msg = new Message();
			msg.what = LOC_CHANGE;
			msg.obj = loc;
			this.handler.sendMessage(msg);
		}
	}

	/**
	 * Callback called when connected to GCore. 
	 * Implementation of {@link ConnectionCallbacks}.
	 */
	@Override
	public void onConnected(Bundle connectionHint) {
		Log.i(TAG, "ConnectionResult success");
		LocationServices.FusedLocationApi.requestLocationUpdates(
				mGoogleApiClient,
				REQUEST,
				this);  // LocationListener
	}

	/**
	 * Callback called when disconnected from GCore. 
	 * Implementation of {@link ConnectionCallbacks}.
	 */
	@Override
	public void onConnectionSuspended(int cause) {
		// Do nothing
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		Log.i(TAG, "ConnectionResult failedS");
	}

	public void setHandler(Handler handler){
		this.handler = handler;
	}
}
