package com.dekaisheng.courier.util.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 *  gcm推送广播接收者
 *  
 *  <action android:name="com.google.android.c2dm.intent.RECEIVE" />
 *  <action android:name="com.google.android.c2dm.intent.REGISTRATION" />
 *  <action android:name="com.google.android.c2dm.intent.REGISTER" />
 * @author Dorian
 *
 */
public class PushMessageReceiver extends BroadcastReceiver {

	public void onReceive(Context context, Intent intent) {
		if(intent!=null){
			
			/**
			 * 接收到推送广播后启动一个Service来处理推送消息
			 * 如果程序正在运行，则在对应的Activity里做处理，
			 * 如果程序在后台，则发通知，并保存到数据库
			 * 
			 */
			intent.setClass(context, MessageReceivingService.class);
			context.startService(intent);
		}
	}

}
