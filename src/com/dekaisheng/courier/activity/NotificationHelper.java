package com.dekaisheng.courier.activity;

import com.dekaisheng.courier.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class NotificationHelper {
	
	public void postNotification(Intent intentAction, Context context, 
			String ticker, String title, String content, int number){
		NotificationManager mNotificationManager = (NotificationManager) 
				context.getSystemService(Context.NOTIFICATION_SERVICE);

		PendingIntent pendingIntent = PendingIntent.getActivity(context, 
				0, intentAction, Notification.DEFAULT_LIGHTS 
				| Notification.FLAG_AUTO_CANCEL);
		@SuppressWarnings("deprecation")
		Notification notification = new Notification.Builder(context)
				.setSmallIcon(R.drawable.notification_ico)
				.setTicker(ticker)
				.setContentTitle(title)
				.setContentText(content)
				.setContentIntent(pendingIntent)
				.setAutoCancel(true)
				.setNumber(number)
				.setWhen(System.currentTimeMillis())
				.getNotification();

		mNotificationManager.notify(R.string.notification_number, notification);
	}

}
