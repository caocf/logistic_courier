package com.dekaisheng.courier.lbs.geocode;

public interface ICodeback {
	/**
	 * 成功发起请求，但返回结果可能是一个空集
	 * @param result
	 */
	public void onSuccess(GeocodeBean result);
	
	/**
	 * 发起请求时已经失败
	 * @param msg
	 */
	public void onFailed(String msg);
}
