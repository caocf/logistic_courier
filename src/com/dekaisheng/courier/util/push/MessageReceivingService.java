package com.dekaisheng.courier.util.push;

import com.dekaisheng.courier.MyApplication;
import com.dekaisheng.courier.core.database.dao.PushMessageDao;
import com.dekaisheng.courier.core.database.entity.PushMessage;
import com.dekaisheng.courier.core.webapi.bean.User;
import com.dekaisheng.courier.user.ActivityMessageCenter;
import com.dekaisheng.courier.util.JsonHelper;
import com.dekaisheng.courier.util.log.Log;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.dekaisheng.courier.R;
import com.dekaisheng.courier.cache.Cache;
import com.dekaisheng.courier.cache.Constants;
import com.dekaisheng.courier.cache.SharePreferenceHelper;
import com.dekaisheng.courier.home.ActivityHomeNew;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

/**
 * 处理gcm推送消息的Service，接收到广播后启动Service， 执行onStartCommand，处理完消息后自动消失stopSelf()
 * 
 * @author Dorian
 * 
 */
public class MessageReceivingService extends Service {

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		processMessessage(this, intent);
		return super.onStartCommand(intent, flags, startId);
	}

	private void processMessessage(Context context, Intent intent) {
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
		String messageType = gcm.getMessageType(intent);

		if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
			Log.e("Push", "GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR");
		} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
				.equals(messageType)) {
			Log.e("Push", "GoogleCloudMessaging.MESSAGE_TYPE_DELETED");
		} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
				.equals(messageType)) {
			Log.e("Push", "GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE");
			PushMessage msg = saveMessage(intent);
			if (!MyApplication.getApplication()
					.isApplicationBroughtToBackground(context)) {
				/**
				 * 暂时这样处理，app前台运行时，也发送一个Notification，后台运行时也发送到app去处理
				 */
				sendToApp(msg, context);
			}
			else {
				notifyReceiveMsg(msg, context);
			}
		}
		this.stopSelf();
	}

	/**
	 * app后台运行，将会受到一个Notification
	 * 
	 * @param msg
	 * @param context
	 */
	private void notifyReceiveMsg(PushMessage msg, Context context) {
		if (msg == null) {
			return;
		}
		Intent intent = new Intent();
		if (msg.key.equals(MessageKey.ASSIGNMENT_RESPONSE.getVal())) {
			SharePreferenceHelper.put(Constants.HaveNewPush, msg.title+","+msg.message);
			intent.setClass(context, ActivityHomeNew.class);
		} else {
			intent.setClass(context, ActivityMessageCenter.class);
		}
		postNotification(intent, context, msg.message, msg.title, msg.content);
	}

	/**
	 * app前台运行，消息会被发送到当前Activity进行处理
	 * 
	 * @param extras
	 * @param context
	 */
	private void sendToApp(PushMessage msg, Context context) {
		MyApplication.getApplication().getCurrentActivity()
				.onReceivePushMessage(msg);
	}

	private void postNotification(Intent intentAction, Context context,
			String ticker, String title, String content) {
		final NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		final PendingIntent pendingIntent = PendingIntent.getActivity(context,
				0, intentAction, PendingIntent.FLAG_UPDATE_CURRENT);
		@SuppressWarnings("deprecation")
		final Notification notification = new Notification.Builder(context)
				.setSmallIcon(R.drawable.icon).setTicker(ticker)
				.setContentTitle(title).setContentText(content)
				.setContentIntent(pendingIntent).setAutoCancel(true)
				.setDefaults(Notification.DEFAULT_ALL) // 闪灯，震动，音乐
				.setWhen(System.currentTimeMillis()).getNotification();

		mNotificationManager.notify(100111, notification);
	}

	public IBinder onBind(Intent arg0) {
		return null;
	}

	/**
	 * 保存推送消息到本地数据库(有些信息只有Notification，不会保存到本地数据库，如订单被抢)
	 * 接收到推送消息，首先保存到数据库，然后再根据app是否在前台分别做对应的处理
	 * 
	 * @param i
	 * @return
	 */
	private PushMessage saveMessage(Intent i) {
		if (i == null) {
			return null;
		}
		Bundle bd = i.getExtras();
		String key = bd.getString("key");
		if (key == null) {
			return null;
		}
		String title = bd.getString("title");
		String message = bd.getString("message");
		String data = bd.getString("data");
		PushMessage pm = (PushMessage) new JsonHelper().jsonToBean(data,
				PushMessage.class);
		if (pm == null) {
			pm = new PushMessage();
		}
		pm.key = key;
		pm.title = title;
		pm.message = message;
		if (key.equals(MessageKey.ORDER_SCAN_AGAIN.getVal())
				|| key.equals(MessageKey.ASSIGNMENT_RESPONSE.getVal())) {
			return pm; // 如果是订单被抢去的，只是Notification不保存到数据库
		}
		pm.read = -1;
		PushMessageDao dao = new PushMessageDao();
		User u = Cache.getInstance().getUser();
		if (u == null) {
			return pm;
		}
		pm.uid = u.getUid();
		dao.insert(pm);
		return pm;
	}
}
