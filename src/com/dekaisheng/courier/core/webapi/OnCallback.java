package com.dekaisheng.courier.core.webapi;

import com.dekaisheng.courier.core.webapi.bean.ResponseBean;

/**
 * Created by Dorian on 2015/7/21.
 */
public abstract class OnCallback{

	public void onStart(){
		
	}
	
    public void onLoading(long total, long current, boolean isUploading) {
	}

    public abstract void onCompleted(ResponseBean<?> rb);

    public abstract void onFailure(String msg);
}
