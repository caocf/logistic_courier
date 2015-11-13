package com.dekaisheng.courier.lbs.route;

import com.dekaisheng.courier.lbs.route.result.ResponseBean;

/**
 * 
 * Route回调接口，因为是基于XUtils的，所以可以在回调里面直接操作UI。
 * @author Dorian
 *
 */
public abstract class OnRouteResultCallback {
	
	public void onStart(){

	}

	public void onLoading(long total, long current, boolean isUploading) {
	}

	public abstract void onCompleted(ResponseBean rb);

	public abstract void onFailure(String msg);
}
