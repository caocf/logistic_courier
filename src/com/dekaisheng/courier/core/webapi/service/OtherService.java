package com.dekaisheng.courier.core.webapi.service;

import java.lang.reflect.Type;

import com.dekaisheng.courier.core.webapi.AbsWebapi;
import com.dekaisheng.courier.core.webapi.OnCallback;
import com.dekaisheng.courier.core.webapi.bean.ResponseBean;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.http.HttpHandler;

/**
 * 一些分类不明确的接口
 * @author Dorian
 *
 */
public class OtherService extends AbsWebapi{

	/**
	 * 提交推送服务注册id接口
	 * @param token
	 * @param uid
	 * @param regId    得到的推送注册码
	 * @param platform (1:aws-gcm,2:aws-baidu)
	 * @param callBack
	 * @return
	 */
	public HttpHandler<String> savePushId(String token, String uid, 
			String regId, String platform, OnCallback callBack){
		String url = BASE_URL + "/user/station/savePushRegisterId?"
				+ API_VERSION
				+ "&token=" + token
				+ "&uid=" + uid
				+ "&reg_id=" + regId
				+ "&reg_platform=" + platform;
		Type beanType = new TypeToken<ResponseBean<Object>>() {}.getType();
		return get(url,callBack,beanType);
	}

}
