package com.dekaisheng.courier.lbs.geocode;

import java.io.IOException;
import java.util.List;

import com.dekaisheng.courier.core.webapi.bean.UnFinishedOrderForm;
import com.dekaisheng.courier.util.ThreadExecutor;
import com.dekaisheng.courier.util.log.Log;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

/**
 * 地理编码服务
 * 地理编码：地址转化到地理坐标
 * 反地理编码：地理坐标转化为地址
 * 
 * @author Dorian
 *
 */
public class GeocodeService {

	private Geocoder geocoder;

	public GeocodeService(Context context){
		geocoder = new Geocoder(context);
	}

	/**
	 * 地理编码
	 * @param address   地址
	 * @param maxResult 返回的最大结果数
	 * @param callback 
	 * @return
	 */
	public void code(final String address, final int maxResult, 
			final IGeocodeCallback callback){
		ThreadExecutor.defaultInstance().doTask(new Runnable(){

			@Override
			public void run() {
				try {
					List<Address> result = geocoder
							.getFromLocationName(address, maxResult);
					if(callback != null){
						if(result != null){
							callback.onSuccess(result);
						}else{
							callback.onFailed("查询不到合适的编码！");
							Log.e("Geocode", "Geocode Failed:" + "查询不到地理编码");
						}
					}
				}catch (IOException e) {
					if(callback != null){
						callback.onFailed("Geocode Error:" + e.getMessage());
					}
					e.printStackTrace();
					Log.e("Geocode", "Geocode Failed:" + e.getMessage());
				}
			}
		});
		
	}

	/**
	 * 在一个矩形范围内进行地理编码
	 * @param address             地址或者地名都可以
	 * @param maxResult           最大结果数量
	 * @param lowerLeftLatitude   低纬度
	 * @param lowerLeftLongitude  低经度
	 * @param upperRightLatitude  高纬度
	 * @param upperRightLongitude 高经度
	 * @param callback
	 */
	public void code(final String address, final int maxResult, 
			final double lowerLeftLatitude, final double lowerLeftLongitude,
			final double upperRightLatitude, final double upperRightLongitude,
			final IGeocodeCallback callback){
		ThreadExecutor.defaultInstance().doTask(new Runnable(){

			@Override
			public void run() {
				try {
					List<Address> result = geocoder
							.getFromLocationName(address, maxResult, lowerLeftLatitude, 
									lowerLeftLongitude, upperRightLatitude, upperRightLongitude);
					if(callback != null){
						if(result != null){
							callback.onSuccess(result);
						}else{
							callback.onFailed("查询不到合适的编码！");
							Log.e("Geocode", "Geocode Failed:" + "查询不到地理编码");
						}
					}
				}catch (IOException e) {
					if(callback != null){
						callback.onFailed("Geocode Error:" + e.getMessage());
					}
					e.printStackTrace();
					Log.e("Geocode", "Geocode Failed:" + e.getMessage());
				}
			}
		});
		
	}
	
	/**
	 * 反地理编码
	 * @param lat 经度
	 * @param lng 纬度
	 * @param maxResult 返回结果的最大数量
	 * @param callback
	 * @return
	 */
	public void decode(final double lat, final double lng, 
			final int maxResult, final IGeocodeCallback callback){
		ThreadExecutor.defaultInstance().doTask(new Runnable(){

			@Override
			public void run() {
				List<Address> result = null;
				try {
					result = geocoder.getFromLocation(lat, lng, maxResult);
					if(callback != null){
						if(result != null){
							callback.onSuccess(result);
						}else{
							callback.onFailed("反地理编码失败，找不到这个编码！");
						}
					}
				} catch (IOException e) {
					if(callback != null){
						callback.onFailed("decode Error:" + e.getMessage());
					}
					e.printStackTrace();
					Log.e("Decode", "Failed:" + e.getMessage());
				}
			}
			
		});
	}

	///////////////////////// 定制的地理编码  ////////////////////////////////////////
	public void code(final UnFinishedOrderForm order, final int maxResult, 
			final ICodeback callback){
		ThreadExecutor.defaultInstance().doTask(new Runnable(){

			@Override
			public void run() {
				try {
					List<Address> result = geocoder.getFromLocationName(order.address, maxResult);
					if(callback != null){
						if(result != null){
							callback.onSuccess(new GeocodeBean(result, order));
						}else{
							callback.onFailed("Geocode Failed, no position match");
							Log.e("Geocode", "Geocode Failed:" + "查询不到地理编码");
						}
					}
				}catch (IOException e) {
					if(callback != null){
						callback.onFailed("Geocode Error:" + e.getMessage());
					}
					e.printStackTrace();
					Log.e("Geocode", "Geocode Failed:" + e.getMessage());
				}
			}
		});
		
	}
	
	public void code(final UnFinishedOrderForm order, final int maxResult, 
			final double lowerLeftLatitude, final double lowerLeftLongitude,
			final double upperRightLatitude, final double upperRightLongitude,
			final ICodeback callback){
		ThreadExecutor.defaultInstance().doTask(new Runnable(){

			@Override
			public void run() {
				try {
					List<Address> result = geocoder
							.getFromLocationName(order.address, maxResult, lowerLeftLatitude, 
									lowerLeftLongitude, upperRightLatitude, upperRightLongitude);
					if(callback != null){
						if(result != null){
							callback.onSuccess(new GeocodeBean(result, order));
						}else{
							callback.onFailed("Geocode Failed, no position match");
							Log.e("Geocode", "Geocode Failed:" + "查询不到地理编码");
						}
					}
				}catch (IOException e) {
					if(callback != null){
						callback.onFailed("Geocode Error:" + e.getMessage());
					}
					e.printStackTrace();
					Log.e("Geocode", "Geocode Failed:" + e.getMessage());
				}
			}
		});
		
	}
}
