package com.dekaisheng.courier.core.webapi.service;

import java.io.File;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import com.dekaisheng.courier.core.webapi.AbsWebapi;
import com.dekaisheng.courier.core.webapi.OnCallback;
import com.dekaisheng.courier.core.webapi.bean.CompletedOrderForm;
import com.dekaisheng.courier.core.webapi.bean.LogisticsDetail;
import com.dekaisheng.courier.core.webapi.bean.ResponseBean;
import com.dekaisheng.courier.core.webapi.bean.UnFinishedOrderForm;
import com.dekaisheng.courier.util.code.EncryptUtils;
import com.dekaisheng.courier.util.log.Log;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.http.HttpHandler;

/**
 * 与订单相关的接口
 * 
 * @author Dorian
 *
 */
public class OrderService extends AbsWebapi{

	/**
	 * 加密一下三个数据
	 * @param token
	 * @param uid
	 * @param order_no
	 * @return
	 */
	private String encodeOrder(String token, String uid, String order_no){
		StringBuilder sb = new StringBuilder();
		sb.append("uid=").append(uid);
		sb.append("&token=").append(token);
		sb.append("&order_no=").append(order_no);
		String params = "";
		try {
			String code = EncryptUtils.encode(sb.toString());
			params = URLEncoder.encode(code, "UTF-8");
		} catch (Exception e) {
			Log.e(TAG, "扫描快递接口加密失败:" + e.getMessage());
		}
		return params;
	}
	
	/**
	 * 派送员扫描订单接口
	 * @param uid
	 * @param token
	 * @param orderNo
	 * @param callback
	 * @return
	 */
	public HttpHandler<String> takeOrder(String uid, String token, 
			String orderNo, final OnCallback callback){
		String params = encodeOrder(token, uid, orderNo);
		String url = this.BASE_URL + "/order/waybill/scanToDeliver?"
				+ this.API_VERSION
				+ "&p=" + params;
		Type beanType = new TypeToken<ResponseBean<Object>>(){}.getType();
		return this.get(url, callback, beanType);
	}

	/**
	 * 查询未完成订单接口
	 * @param token
	 * @param uid
	 * @param mode 路径规划的类型type，{@link }
	 * @param lg 派送员位置经度
	 * @param lt 派送员位置纬度
	 * @param uid
	 * @param callBack
	 * @return
	 */
	public HttpHandler<String> unFinishedOrder(String token, 
			String uid, int mode, LatLng myLoc,final OnCallback callBack){
		String lg, lt;
		if(myLoc == null){
			lg = "";
			lt = "";
		}else{
			lg = myLoc.longitude + "";
			lt = myLoc.latitude + "";
		}
		String url = BASE_URL + "/order/waybill/unDeliverList?"
				+ API_VERSION
				+ "&token=" + token
				+ "&uid=" + uid
				+ "&lg=" + lg
				+ "&lt=" + lt
				+ "&type=" + 1; // 1为未完成
		
		Type beanType = new TypeToken<ResponseBean<List
				<UnFinishedOrderForm>>>() {}.getType();
				return get(url,callBack,beanType);

	}

	/**
	 * 查询已完成订单接口
	 * @param token
	 * @param uid
	 * @param callBack
	 * @return
	 */
	public HttpHandler<String> completedOrder(String token, 
			String uid, int page, final OnCallback callBack){
		String url = BASE_URL + "/order/waybill/finishDeliverList?"
				+ API_VERSION
				+ "&token=" + token
				+ "&uid=" + uid
		        + "&page=" + page;
		Type beanType = new TypeToken<ResponseBean<List
				<CompletedOrderForm>>>() {}.getType();
		return get(url,callBack,beanType);
	}

	/**
	 * 客户签收照片上传接口
	 * @param token
	 * @param uid
	 * @param order_no
	 * @param picture
	 * @param callBack
	 * @return
	 */
	public HttpHandler<String> customsSign(String token, String uid, 
			String order_no, File picture, OnCallback callBack){
		String baseUrl = this.BASE_URL + "/order/waybill/sign";
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		map.put("p", encodeOrder(token, uid, order_no));
		map.put("picture", picture);
		Type beanType = new TypeToken<ResponseBean<Object>>(){}.getType();
		return this.post(baseUrl, map, callBack, beanType);
	}

	/**
	 * 查看物流详情接口
	 * @param token
	 * @param uid
	 * @param orderNo 物流单号
	 * @param callback
	 * @return
	 */
	public HttpHandler<String> logisticsDetail(String token, 
			String uid, String orderNo, OnCallback callback){
		String url = BASE_URL + "/order/waybill/detail?"
				+ this.API_VERSION
				+ "&token=" + token
				+ "&uid=" + uid
				+ "&order_no=" + orderNo;
		Type beanType = new TypeToken<ResponseBean<
				LogisticsDetail>>(){}.getType();
		return get(url,callback,beanType);
	}

	/**
	 * 提交异常订单报告
	 * code:1001:上传成功，1005:系统出错，1007：参数有误，1024：次订单非你派送
	 * @param token
	 * @param uid
	 * @param order_no
	 * @param error_status （1⇒'Lost good',2⇒'Cargo damage/pollution',
	 *                      3⇒'Address error',4⇒'Phone error', 5⇒'No one sign',）
	 * @param error_info
	 * @param other_reason
	 * @param callback
	 * @return
	 */
	public HttpHandler<String> sendAbnormalOrder(String token,
			String uid, String order_no, int error_status,
			String error_info, String other_reason, OnCallback callback){
		String params = encodeOrder(token, uid, order_no);
		HashMap<String,Object> map = new HashMap<String,Object>();
		map.put(this.vTag, this.version);
		map.put("p", params);
		map.put("error_status", error_status);
		map.put("error_info", error_info);
		map.put("other_reason", other_reason);
		String url = this.BASE_URL + "/order/waybill/signAbnormal?";
		Type beanType = new TypeToken<ResponseBean<Object>>(){}.getType();
		return post(url, map, callback, beanType);
	}
	
	/**
	 * 派送员收到分配订单响应。
	 * @param token
	 * @param uid
	 * @param callback
	 * @return
	 */
	public HttpHandler<String> assignmentResponse(String token,
			String uid,OnCallback callback){
		String url = BASE_URL + "/order/assign/msgResponse?"
				+ this.API_VERSION
				+ "&token=" + token
				+ "&uid=" + uid;
		Type beanType = new TypeToken<ResponseBean<Object>>(){}.getType();
		return get(url, callback, beanType);
	}
}










