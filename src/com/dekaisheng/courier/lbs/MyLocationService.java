package com.dekaisheng.courier.lbs;

import com.dekaisheng.courier.util.log.Log;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;

import android.os.IBinder;

/**
 * 项目中用到的定位服务
 * 一个Service组件，启动该Service后将会持续定位，可以绑定到Activity，
 * 如有地图的Activity，然后在地图上显示定位信息，根据具体需求，只需要在绑
 * 定的时候传入一个Handler即可，Message.what = MSG_CODE = 1008611
 * @author Dorian
 *
 */
public class MyLocationService extends Service{

	private Binder binder = new MyBinder();
	private LocationService locService;
	private boolean started = false;
	private final long mapLocMinTime = 1000;// 位于地图界面时的定位间隔(通过绑定服务的形式获取服务的)，当地图在显示时候，1s定位一次。
	private final long backgroundLocMinTime = 1000*60*3; // 后台运行时的定位间隔设置3分钟。

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("LocationService", "LocationService onCreate()");
		locService = new LocationService(this.getApplicationContext());
	}

	@Override
	public void onDestroy(){
		Log.i("LocationService", "LocationService onDestroy()");
		locService.stop();
		started = false;
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("LocationService", "LocationService onStartCommand()");
		if(!started){
			locService.start();
			started = true;
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.i("LocationService", "LocationService onUnBind()");
		locService.setMinTime(backgroundLocMinTime);
		locService.setHandler(null);
		return super.onUnbind(intent);
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.i("LocationService", "LocationService onBind()");
		if(!started){
			locService.start();
		}
		locService.setMinTime(mapLocMinTime); //mapLocMinTime;
		return binder;
	}

	public class MyBinder extends Binder{
		public MyLocationService getService(){
			return MyLocationService.this;
		}
	}

	/**
	 * 提供给调用者的方法，在绑定Service时可以设置Handler来与Activity交互，
	 * 如在Handler里面处理返回来的Location
	 * @param handler
	 */
//	public void setHandler(Handler handler){
//		locService.setHandler(handler);
//	}

	public void setMinDistance(float min){
		locService.minDistance = min;
	}

	public void setMinTime(long min){
		locService.setMinTime(min);
	}

	//	/**
	//	 * 提供给调用者的方法，获取到LocationService实例，以便对其进行某些属性设置
	//	 * @return
	//	 */
	//	public LocationService getLocationService(){
	//		return this.locService;
	//	}

}
