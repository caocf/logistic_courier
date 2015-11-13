package com.dekaisheng.courier.util.networkstate;

import java.util.ArrayList;

import com.dekaisheng.courier.util.networkstate.NetWorkUtil;
import com.dekaisheng.courier.util.networkstate.NetworkStateObserver;
import com.dekaisheng.courier.util.networkstate.NetworkType;

import android.util.Log;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

public class NetworkStateReceiver extends BroadcastReceiver{

	private final String TAG = "NetworkStateReceiver";
	private static NetworkStateReceiver receiver;
	private ArrayList<NetworkStateObserver> observers;
	private boolean isNetworkValueable = false;
	private NetworkType networkType;

	private NetworkStateReceiver(){

	}

	public static NetworkStateReceiver getInstance(){
		if(receiver == null){
			receiver = new NetworkStateReceiver();
		}
		return receiver;
	}

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		String action = arg1.getAction();
		if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			if (!NetWorkUtil.isNetworkAvailable(arg0)){
				isNetworkValueable = false;
			} else{
				networkType = NetWorkUtil.getAPNType(arg0);
				isNetworkValueable = true;
			}
			notifyObserver();
		}
	}

	/**
	 * 
	 * @Title: registerObserver  
	 * @Description: 注册一个观察者  
	 * @param @param observer      
	 * @return void  
	 * @throws
	 */
	public void registerObserver(NetworkStateObserver observer){
		if(observers == null){
			observers = new ArrayList<NetworkStateObserver>();
		}
		observers.add(observer);
	}

	/**
	 * 
	 * @Title: removeObserver  
	 * @Description: 移除一个观察者  
	 * @param @param observer      
	 * @return void  
	 * @throws
	 */
	public void removeObserver(NetworkStateObserver observer){
		if(this.observers != null){
			observers.remove(observer);
		}
	}

	/**
	 * 
	 * @Title: registerBroadcast  
	 * @Description:  添加网络状态侦听
	 * @param @param context      
	 * @return void  
	 * @throws
	 */
	public void registerNetworkStateReceiver(Context context){
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

		//		IntentFilter filter = new IntentFilter();
		//		filter.addAction(TA_ANDROID_NET_CHANGE_ACTION);
		//		filter.addAction(ANDROID_NET_CHANGE_ACTION);
		context.getApplicationContext().registerReceiver(getInstance(), filter);
	}

	/**
	 * 
	 * @Title: unRegisterBroadcast  
	 * @Description: 移除网络状态侦听 
	 * @param @param context      
	 * @return void  
	 * @throws
	 */
	public void unRegisterNetworkStateReceiver(Context context){
		if (receiver != null){
			try{
				context.getApplicationContext().unregisterReceiver(receiver);
			} catch (Exception e){
				Log.d(TAG, e.getMessage());
			}
		}
	}

	private void notifyObserver(){
		NetworkStateObserver obs = null;
		for(int i = 0; i < this.observers.size(); i++){
			obs = this.observers.get(i);
			if(this.isNetworkValueable){
				try{
					obs.onConnected(networkType);
					Log.i(TAG, "执行完onConnected");
				}catch(Exception e){
					Log.e(TAG, "观察者代码有问题！"+e.getMessage());
				}
			}else{
				try{
					obs.onDisconnected();
					Log.i(TAG, "执行完onDisConnected");
				}catch(Exception e){
					Log.e(TAG, "观察者代码有问题！"+e.getMessage());
				}
			}
		}
	}

	public boolean isNetworkValuebale(){
		return this.isNetworkValueable;
	}

	public NetworkType getNetworkType(){
		return this.networkType;
	}
}
