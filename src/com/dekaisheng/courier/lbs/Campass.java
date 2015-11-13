package com.dekaisheng.courier.lbs;

import java.util.Date;
import java.util.List;

import com.dekaisheng.courier.util.log.Log;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;

/**
 * 指南针的实现，通过重力感应和磁场感应传感器获取数据，经过计算后得到方向
 * 方向范围值为：-180~180，单位度以正北方向为0度，顺时针为正，逆时针为负，更准
 * 确的理解请参考帮助文档。
 * 1.新建一个Campass对象
 * 2.onCreate()里面调用intSensor()
 * 3.onResume()里调用registerSensor()
 * 4.onPause()里调用unRegisterSensor()
 * 5.在Handler里面处理传感器结果what=MSG_CODE_SENSOR,obj=float[3]，
 *   其中obj[0]就是得到的手机方向与正北方向的夹角
 * @author Administrator
 *
 */
public class Campass {

	public final static int MSG_CODE_SENSOR = 123456;
	
	private final String TAG = "Sensor";
	private SensorManager sensorManager;
	private SensorEventListener sensorListener;
	private Sensor accelerometerSencor; // 加速度传感器
	private Sensor magneticSensor;      // 磁场传感器
	private boolean isRegister = false;     // 是否注册过传感器监听
	private boolean sensorEnable = false;   // 传感器是否可用
	
	private Activity context;
	
	private Handler handler;
	
	public Campass(Activity activity, Handler handler){
		this.context = activity;
		this.handler = handler;
	}

	/**
	 * 移除传感器监听
	 */
	public void unRegisterSensor(){
		if(!sensorEnable || !isRegister) 
			return;
		if(this.sensorManager != null){
			sensorManager.unregisterListener(sensorListener, accelerometerSencor);
			sensorManager.unregisterListener(sensorListener, magneticSensor);
			this.sensorManager = null;
			this.accelerometerSencor = null;
			this.magneticSensor = null;
			this.sensorListener = null;
			isRegister = false;
			Log.i(TAG, "移除传感器监听成功");
		}
	}

	/**
	 * 添加传感器监听
	 */
	public void registerSensor(){
		if(sensorEnable && !isRegister){
			if(this.sensorManager == null){
				this.sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
			}
			this.accelerometerSencor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			this.magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
			this.sensorListener = new SensorChangeListener();

			this.sensorManager.registerListener(sensorListener
					, accelerometerSencor,SensorManager.SENSOR_DELAY_NORMAL);
			this.sensorManager.registerListener(sensorListener, 
					magneticSensor , SensorManager.SENSOR_DELAY_NORMAL);
			isRegister = true;
			Log.i(TAG, "注册传感器监听成功");
		}else{
			Log.i(TAG, "注册传感器监听失败");
		}
	}

	/**
	 * 初始化
	 * @return 如果存在传感器则返回true，不存在则返回false
	 */
	public boolean intSensor(){
		this.sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
		List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
		boolean hasAccelerometer = false;
		boolean hasMagneticField = false;
		for(Sensor s : sensors){
			if(s.getType() == Sensor.TYPE_ACCELEROMETER)
				hasAccelerometer = true;
			if(s.getType() == Sensor.TYPE_MAGNETIC_FIELD)
				hasMagneticField = true;
			Log.i(TAG, "传感器：" + s.getName());
		}
		if(hasAccelerometer && hasMagneticField){
			sensorEnable = true;
		}
		return sensorEnable;
	}

	private class SensorChangeListener implements SensorEventListener{

		private float[] accelerometerValues = new float[3];  
		private float[] magneticFieldValues = new float[3];

		private long latest = 0;
		private long current = 0;
		private long min = 1000;

		@Override
		public void onSensorChanged(SensorEvent sensorEvent) {
			if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)  
				magneticFieldValues = sensorEvent.values;  
			if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER)  
				accelerometerValues = sensorEvent.values; 
			/**
			 * 值得注意：下面计算时间的这段代码如果放在上面之前，则不会得到方向改变的信息，
			 * 永远是0.0，这是为什么呢？？？
			 */
			Date dt = new Date();
			current = dt.getTime();
			if(current - latest < min){
				return;
			}
			latest = current;
			calculateOrientation();  
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {

		}

		private  void calculateOrientation() {
			float[] values = new float[3]; 
			float[] R = new float[9]; 
			SensorManager.getRotationMatrix(R, null, accelerometerValues, magneticFieldValues);          
			SensorManager.getOrientation(R, values); 
			// 要经过一次数据格式的转换，转换为度 
			values[0] = (float) Math.toDegrees(values[0]); 
			Message msg = new Message();
			msg.what = MSG_CODE_SENSOR;
			msg.obj = values;
			handler.sendMessage(msg);
		}
	}
}
