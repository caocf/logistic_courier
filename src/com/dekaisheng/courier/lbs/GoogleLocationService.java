package com.dekaisheng.courier.lbs;

import com.dekaisheng.courier.util.log.Log;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

/**
 * 这是新的Google定位接口，项目中未用到
 * 一个Service组件，启动该Service后将会持续定位，可以绑定到Activity，
 * 如有地图的Activity，然后在地图上显示定位信息，根据具体需求，只需要在绑
 * 定的时候传入一个Handler即可，Message.what = LOC_CHANGE = 10011
 * @author Dorian
 *
 */
public class GoogleLocationService extends Service{

	private Binder binder = new MyBinder();
	private NewLocationService nloc;
	
	@Override
	public void onCreate() {
		super.onCreate();
		nloc = new NewLocationService(this.getApplicationContext());
	}
	
	@Override
	public void onDestroy(){
		Log.i("LocationService", "LocationService onDestroy()");
		nloc.disconnect();
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("LocationService", "LocationService onStartCommand()");
		nloc.connect();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.i("LocationService", "LocationService onUnBind()");
		return super.onUnbind(intent);
	}

	@Override
	public IBinder onBind(Intent intent) {
		nloc.connect();
		return binder;
	}
	
	public void setHandler(Handler handler){
		nloc.setHandler(handler);
	}

	public class MyBinder extends Binder{
		public GoogleLocationService getService(){
			return GoogleLocationService.this;
		}
	}
}
