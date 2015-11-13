package com.dekaisheng.courier.core.webapi;

import com.dekaisheng.courier.Config;
import com.dekaisheng.courier.MyApplication;
import com.dekaisheng.courier.activity.IDialogListener;
import com.dekaisheng.courier.cache.ApiResultCode;
import com.dekaisheng.courier.core.webapi.bean.ResponseBean;
import com.dekaisheng.courier.util.JsonHelper;
import com.dekaisheng.courier.util.log.Log;
import com.dekaisheng.courier.R;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Dorian on 2015/7/21.
 */
public class AbsWebapi {

	// api版本号，以后应该会升级，升级后原版本同样可用，新版本只需要更改strings.xml中的版本号即可
	protected String API_VERSION;
	protected String version;
	protected final String vTag = "v";
	protected final String TAG = "webapi";
	protected String BASE_URL;
	private final int connectTimeOut = 15000; // 连接超时
	private final int soTimeOut = 30000;      // 读取时间超时

	public AbsWebapi(){
		version = MyApplication.getApplication()
				.getApplicationContext().getString(R.string.api_version);
		API_VERSION = vTag + "=" + version; 
		if(Config.DEBUG){
			BASE_URL = "http://192.168.1.122:80"; // 开发时使用
		}else{
			BASE_URL = "http://api.mshede.com";// 上线时使用
		}
	}

	protected HttpHandler<String> get(String url, 
			final OnCallback callBack,final Type beanType){
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.configCurrentHttpCacheExpiry(10);
		httpUtils.configTimeout(connectTimeOut);
		httpUtils.configSoTimeout(soTimeOut);
		Log.i(TAG, "Request URL:" + url);
		return httpUtils.send(HttpRequest.HttpMethod.GET,url
				,new MyRequestCallBack(callBack, beanType));
	}

	/**
	 * 只支持File和String类型的参数，不支持InputStream，
	 * 所以在上存文件时需要注意。
	 * @param baseUrl
	 * @param params
	 * @param callBack
	 * @param beanType
	 * @return
	 */
	protected HttpHandler<String> post(String baseUrl
			, HashMap<String,Object> params
			, final OnCallback callBack, final Type beanType){
		RequestParams param = new RequestParams();
		Iterator<String> i = params.keySet().iterator();
		String key;
		Object value;
		while(i.hasNext()){
			key = i.next();
			value = params.get(key);
			if(value instanceof String){
				param.addBodyParameter(key,(String)value);
			}else if(value instanceof File){
				param.addBodyParameter(key, (File)value);
			}
		}
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.configCurrentHttpCacheExpiry(10);
		httpUtils.configTimeout(connectTimeOut);
		httpUtils.configSoTimeout(soTimeOut);
		return httpUtils.send(HttpRequest.HttpMethod.POST, baseUrl
				,param ,new MyRequestCallBack(callBack, beanType));
	}

	private class MyRequestCallBack extends RequestCallBack<String>{

		private OnCallback callBack;
		private Type beanType;

		public MyRequestCallBack(OnCallback callBack, Type bt){
			this.callBack = callBack;
			this.beanType = bt;
		}

		@Override
		public void onStart(){
			if(callBack != null) {
				callBack.onStart();
			}
		}

		@Override
		public void onLoading(long total, long current, boolean isUploading) {
			if(callBack != null){
				callBack.onLoading(total,current,isUploading);
			}
		}

		@Override
		public void onFailure(HttpException arg0, String arg1) {
			if(arg0.getExceptionCode() == ApiResultCode.UN_AUTHORIZED){
				/**
				 *  用户已在另一台设备登录，本设备退出登录，回到登录页面
				 */
				MyApplication.getApplication().getCurrentActivity().showTipsDialog(
						MyApplication.getApplication().getCurrentActivity()
						.getString(R.string.dialog_tips_login_in_other_device), 
						new IDialogListener(){

							@Override
							public void onOk() {
								MyApplication.getApplication().getCurrentActivity().logOff();
							}

							@Override
							public void onCancle() {
								Log.i(TAG, "webapiResult:这个请求被取消");
							}

						});
			}else{
				if(callBack != null){
					callBack.onFailure(arg1);
				}
			}
		}

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			if(callBack != null) {
				if(responseInfo == null){
					callBack.onFailure("UnKnow problem.");
					Log.e(TAG, "失败，位于onSuccess()??ResponseInfo为空值!简直impossible");
					return;
				}
				String result = responseInfo.result;
				Log.i(TAG, "webapiResult:" + result);
				ResponseBean<?> rb = new JsonHelper().jsonToBean(result, beanType);
				if(rb == null){
					callBack.onFailure("UnKnow problem.");
				}else{ 
					// 确保传到上层的实例不为空，避免更多的检验代码和莫名的NullPointerException
					callBack.onCompleted(rb);
				}
			}
		}

	}
}
