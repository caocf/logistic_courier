package com.dekaisheng.courier.core.webapi.service;

import com.dekaisheng.courier.MyApplication;
import com.dekaisheng.courier.core.webapi.AbsWebapi;
import com.dekaisheng.courier.core.webapi.OnCallback;
import com.dekaisheng.courier.core.webapi.bean.OrderHistory;
import com.dekaisheng.courier.core.webapi.bean.Portrait;
import com.dekaisheng.courier.core.webapi.bean.ResponseBean;
import com.dekaisheng.courier.core.webapi.bean.User;
import com.dekaisheng.courier.core.webapi.bean.UserDetailInfo;
import com.dekaisheng.courier.util.InstalltionIdFactory;
import com.dekaisheng.courier.util.code.EncryptUtils;
import com.dekaisheng.courier.util.log.Log;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.http.HttpHandler;

import java.io.File;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * 用户账号接口
 * Created by Dorian on 2015/7/21.
 */
public class AcountService extends AbsWebapi {
	
	private String encodePwd(String phone, String code, String password){
		StringBuilder sb = new StringBuilder();
		sb.append("phone=").append(phone);
		sb.append("&code=").append(code);
		sb.append("&password=").append(password);
		String params = "";
		try {
			String en = EncryptUtils.encode(sb.toString());
			params = URLEncoder.encode(en, "UTF-8");
		} catch (Exception e) {
			Log.e(TAG, "忘记密码递接口加密失败:" + e.getMessage());
		}
		return params;
	}

	/**
	 * 派送员登录接口
	 * @param userName
	 * @param pwd
	 * @param callBack
	 * @return
	 */
	public HttpHandler<?> login(String userName, String pwd,
			final OnCallback callBack){
		StringBuilder sb = new StringBuilder();
		sb.append("username=").append(userName)
		.append("&password=").append(pwd)
		.append("&imei=")
		.append(InstalltionIdFactory.id(MyApplication
				.getApplication().getApplicationContext()));
		String params;
		try{
			String code = EncryptUtils.encode(sb.toString());
			params = URLEncoder.encode(code, "UTF-8");
		}catch(Exception e){
			return null;
		}
		String url = this.BASE_URL + "/user/deliver/login?" 
				+ API_VERSION + "&p=" + params;
		Type beanType = new TypeToken<ResponseBean<User>>() {}.getType();
		return get(url,callBack,beanType);
	}

	/**
	 * 派送员上传地理坐标接口
	 * @param token
	 * @param uid
	 * @param lg
	 * @param lt
	 * @param callBack
	 * @return
	 */
	public HttpHandler<String> sendLocation(String token, String uid,
			double lg, double lt, OnCallback callBack){
		String url = BASE_URL + "/user/station/sendLocation?"
				+ API_VERSION 
				+ "&token=" + token
				+ "&uid=" + uid
				+ "&lg=" + lg
				+ "&lt=" + lt;
		Type beanType = new TypeToken<ResponseBean<Portrait>>() {}.getType();
		return get(url,callBack,beanType);
	}

	/**
	 * 上传用户头像接口
	 * @param token
	 * @param uid
	 * @param file
	 * @param callback
	 * @return
	 */
	public HttpHandler<String> changPortrait(String token, String uid,
			File file, OnCallback callback){
		HashMap<String,Object> map = new HashMap<String,Object>();
		map.put(this.vTag, this.version);
		map.put("token", token);
		map.put("uid", uid);
		map.put("picture", file);
		Type beanType = new TypeToken<ResponseBean<Portrait>>(){}.getType();
		String baseUrl = this.BASE_URL + "/user/station/editPortrait?";
		return this.post(baseUrl, map, callback, beanType);	
	}

	/**
	 * 派送员派送历史记录接口
	 * @param token
	 * @param uid
	 * @param callback
	 * @return
	 */
	public HttpHandler<String> historyRecord(String token, String uid, OnCallback callback){
		String url = this.BASE_URL + "/user/deliver/recodeList?"
				+ this.API_VERSION 
				+ "&token=" + token
				+ "&uid=" + uid;
		Type beanType = new TypeToken<ResponseBean<OrderHistory>>() {}.getType();
		return get(url,callback,beanType);
	}
	
	/**
	 * 配送员忘记密码，获取验证码接口
	 * @param phone 电话号码
	 * @param callback
	 * @return
	 */
	public HttpHandler<String> getCode(String phone, OnCallback callback){
		String url = this.BASE_URL + "/user/deliver/getCodeForPassword?"
				+ this.API_VERSION
				+ "&phone=" + phone;
		Type beanType = new TypeToken<ResponseBean<Object>>(){}.getType();
		return get(url, callback, beanType);
	}
	
	/**
	 * 忘记密码发送邮件
	 * @param phone
	 * @param code
	 * @param callback
	 * @return
	 */
	public HttpHandler<String> forgotPassword(String email, OnCallback callback){
		String url = this.BASE_URL + "/user/deliver/forgetPassword?"
				+ this.API_VERSION
				+ "&email=" + email;
		Type beanType = new TypeToken<ResponseBean<Object>>(){}.getType();
		return get(url, callback, beanType);
	}
	
	/**
	 * 配送员忘记密码，重置密码
	 * @param phone
	 * @param code
	 * @param password
	 * @param callback
	 * @return
	 */
	public HttpHandler<String> resetPassword(String phone, String code, 
			String password, OnCallback callback){
		String url = this.BASE_URL + "/user/deliver/resetPassword?"
				+ this.API_VERSION
				+ "&p=" + encodePwd(phone, code, password);
		Type beanType = new TypeToken<ResponseBean<Object>>(){}.getType();
		return get(url, callback, beanType);
	}

	/**
	 * 获取配送员详细信息接口{@link com.dekaisheng.courier.user.ActivityUserInfo}
	 * @param token
	 * @param uid
	 * @param callback
	 * @return
	 */
	public HttpHandler<String> getUserDetailInfo(String token, String uid, OnCallback callback){
		String url = this.BASE_URL + "/user/deliver/info?"
				+ this.API_VERSION
				+ "&token=" + token
				+ "&uid=" + uid;
		Type beanType = new TypeToken<ResponseBean<UserDetailInfo>>(){}.getType();
		return get(url, callback, beanType);
	}
}










