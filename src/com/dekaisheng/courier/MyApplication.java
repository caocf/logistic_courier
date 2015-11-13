package com.dekaisheng.courier;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.List;

import com.dekaisheng.courier.core.database.DBManager;
import com.dekaisheng.courier.util.log.FileLogger;
import com.dekaisheng.courier.util.log.Log;
import com.dekaisheng.courier.util.networkstate.NetWorkUtil;
import com.dekaisheng.courier.util.networkstate.NetworkStateObserver;
import com.dekaisheng.courier.util.networkstate.NetworkStateReceiver;
import com.dekaisheng.courier.util.networkstate.NetworkType;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
@SuppressWarnings("deprecation")
public class MyApplication extends Application {

	private final String TAG = "Application";
	private static MyApplication application;
	private UncaughtExceptionHandler uncaughtExceptionHandler;

	private NetworkStateObserver networkStateChangeObserver;

	private boolean isNetworkAvailable = false;

	protected NetworkType networkType = NetworkType.noneNet;

	private MyActivity currentActivity;

	private AppManager manager;

	@Override
	public void onCreate() {
		doBeforeCreate();
		super.onCreate();
		doOnCreate();
		doAfterOnCreate();
		DBManager.initDB();
	}


	@Override
	public void onTerminate() { 
		Log.i(TAG,"Application Terminate!");
		NetworkStateReceiver.getInstance().unRegisterNetworkStateReceiver(application);
		super.onTerminate();
	}

	private UncaughtExceptionHandler getUncaughtExceptionHandler(){
		if(this.uncaughtExceptionHandler == null){
			this.uncaughtExceptionHandler = CrashExceptionHandler.getInstance(this);
		}
		return this.uncaughtExceptionHandler;
	}

	private void doBeforeCreate() {
		Log.i(TAG,"Application Before Create!");
	}

	private void doOnCreate() {
		Log.i(TAG,"Application On Create!");
		application = this;
		Thread.setDefaultUncaughtExceptionHandler(this.getUncaughtExceptionHandler());
		networkStateChangeObserver = new NetworkStateObserver(){

			@Override
			public void onConnected(NetworkType netType){
				super.onConnected(netType);
				MyApplication.this.onConnected(netType);
			}

			@Override
			public void onDisconnected(){
				super.onDisconnected();
				MyApplication.this.onDisConnected();
			}
		};
		NetworkStateReceiver.getInstance().registerNetworkStateReceiver(application);
		NetworkStateReceiver.getInstance().registerObserver(networkStateChangeObserver);
		this.isNetworkAvailable = NetWorkUtil.isNetworkAvailable(this);
		this.networkType = NetWorkUtil.getAPNType(this);
		this.manager = AppManager.getActivityManager();
	}

	/**
	 * 	
	 * @Title: doAfterOnCreate  
	 * @Description: Application执行完onCreate后调用，
	 *               如引入第三方的SDK如百度、高德地图SDK等需要初始化，
	 *               则在这里进行初始化
	 * @param       
	 * @return void  
	 * @throws
	 */
	private void doAfterOnCreate() {
		Log.i(TAG,"Application After Created!");
		Log.addLogger(new FileLogger());
	}

	/**
	 * 
	 * @Title: onConnected  
	 * @Description: 网络变为可用时，通知当前的activity 
	 * @param @param netType      
	 * @return void  
	 * @throws
	 */
	protected void onConnected(NetworkType netType){
		this.isNetworkAvailable = true;
		this.networkType = netType;
		if(this.currentActivity != null){
			this.currentActivity.onConnected(netType);
		}
	}

	/**
	 * 
	 * @Title: onDisConnected  
	 * @Description: 网络变为不可用时，通知当前的activity 
	 * @param       
	 * @return void  
	 * @throws
	 */
	protected void onDisConnected(){
		this.isNetworkAvailable = false;
		if(this.currentActivity != null){
			this.currentActivity.onDisConnected();
		}
	}

	/**
	 * 
	 * @Title: onActivityCreating  
	 * @Description: BasicActivity在onCreated时，调用该方法告知application    
	 * @param @param activity      
	 * @return void  
	 * @throws
	 */
	public void onActivityCreating(MyActivity activity){

	}

	/**
	 * 
	 * @Title: onActivityCreated  
	 * @Description: BasicActivity在onCreated时，调用该方法告知application     
	 * @param @param activity      
	 * @return void  
	 * @throws
	 */
	public void onActivityCreated(Activity activity){
		manager.add(activity);
	}

	/**
	 * 
	 * @Title: onAfterActivityCreated  
	 * @Description: BasicActivity在onCreated的最后，调用该方法告知application  
	 * @param @param activity      
	 * @return void  
	 * @throws
	 */
	public void onAfterActivityCreated(MyActivity activity){
		this.currentActivity = activity;
	}

	/**
	 * 
	 * @Title: onActivityDestory  
	 * @Description:  Activity调用onDestory时通过调用该方法告知application，
	 *                那么问题来了，BaseActivity中定义了返回键事件，销毁了activity
	 *                remove被调用了，现在还需要调用么？
	 * @param @param activity      
	 * @return void  
	 * @throws
	 */
	public void onActivityDestory(Activity activity){
		manager.remove(activity);
	}

	/**
	 * 
	 * @Title: getApplication  
	 * @Description: 返回LibraryApplication的唯一实例，Activity可以在相应生命周期
	 *               通过该实例进行相应的调度，告知Application自己的状态，Application
	 *               进行相应的处理
	 * @return MyApplication
	 * @throws
	 */
	public static MyApplication getApplication(){
		return application;
	}

	/**
	 * 
	 * @Title: getIsNetworkAvaliable  
	 * @Description: 检测网络是否可用
	 * @param @return      
	 * @return boolean  
	 * @throws
	 */
	public boolean getIsNetworkAvaliable(){
		return this.isNetworkAvailable;
	}

	public NetworkType getNetworkType(){
		return this.networkType;
	}

	/**
	 * 
	 * @Title: activityBack  
	 * @Description: 当前activity按下了返回键时，应该退栈 
	 * @param       
	 * @return void  
	 * @throws
	 */
	public void activityBack(){
		// 当前activity按下了返回键时，应该退栈
		manager.finish();
	}

	public MyActivity getCurrentActivity(){
		return this.currentActivity;
	}

	/**
	 * 
	 * @param context
	 * @return true:后台运行；false:前台运行
	 */
	public boolean isApplicationBroughtToBackground(final Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		
		List<RunningTaskInfo> tasks = am.getRunningTasks(1);
		if (!tasks.isEmpty()) {
			ComponentName topActivity = tasks.get(0).topActivity;
			if (!topActivity.getPackageName().equals(context.getPackageName())) {
				return true;
			}
		}
		return false;

	}

	/**
	 * 判断一个Activity是否在前台运行
	 * @param activity
	 * @return
	 */
	public boolean isActivityRunning(Activity activity){
		ActivityManager am = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasks = am.getRunningTasks(1);
		if (!tasks.isEmpty()) {
			ComponentName topActivity = tasks.get(0).topActivity;
			// String className = topActivity.getClassName();//topActivity.getClassName()
			// String localName = activity.getLocalClassName();//topActivity.getClassName()
			if(topActivity.getClassName().contains(activity.getLocalClassName())){
				return true;
			}
		}
		return false;
	}
}
