package com.dekaisheng.courier.lbs;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
@SuppressWarnings("deprecation")
public class MyOrientationListener implements SensorEventListener {

	// 用来获取Seosor
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private Context mcontext;
	private float lastX;
	private OnOrientationListener mOnOrientationListener;

	public MyOrientationListener(Context context) {
		mcontext = context;
	}

	// 开启监听的方法

	public void start() {
		mSensorManager = (SensorManager) mcontext
				.getSystemService(Context.SENSOR_SERVICE);
		if (mSensorManager != null) {
			mSensor = mSensorManager
					.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		}
		if (mSensor != null) {
			// 注册监听
			mSensorManager.registerListener(this, mSensor,
					SensorManager.SENSOR_DELAY_UI);
		}
	}

	// 停止监听的方法
	public void stop() {
		mSensorManager.unregisterListener(this);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// 拿到方向传感器 if(event.sensor.getType()==Sensor.TYPE_ORIENTATION){
		float x = event.values[SensorManager.DATA_X];
		// 如果移动超过一个精度则转向
		if (Math.abs(x - lastX) > 1.0) {
			if (mOnOrientationListener != null) {
				mOnOrientationListener.OnOrientationChanged(x);
			}
		}
		lastX = x;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	// 定义回调接口
	public interface OnOrientationListener {
		// 传递改变的监听的x方向的值
		void OnOrientationChanged(float x);
	}

	public void setOnOrientationListener(
			OnOrientationListener mOnOrientationListener) {
		this.mOnOrientationListener = mOnOrientationListener;
	}
}
