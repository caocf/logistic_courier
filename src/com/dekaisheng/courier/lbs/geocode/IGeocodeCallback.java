package com.dekaisheng.courier.lbs.geocode;

import java.util.List;

import android.location.Address;

/**
 * 地理编码回调接口
 * @author Dorian
 *
 */
public interface IGeocodeCallback {
	
	/**
	 * 成功发起请求，但返回结果可能是一个空集
	 * @param result
	 */
	public void onSuccess(List<Address> result);
	
	/**
	 * 发起请求时已经失败
	 * @param msg
	 */
	public void onFailed(String msg);
}
