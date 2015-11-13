package com.dekaisheng.courier.lbs.route;

import com.dekaisheng.courier.lbs.route.result.ResponseBean;
import com.dekaisheng.courier.util.JsonHelper;
import com.dekaisheng.courier.util.StringUtils;
import com.dekaisheng.courier.util.log.Log;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 路径分析接口
 * google direction api请求
 * Usage Limits(免费用户受请求限制2500次/天，2次/秒，途经点最多8个，
 * 付费用户，依据每天的请求收费，10次/秒，最多23个途经点。URL最多大约2000个字符，是URL编码后的字符)
 * The Directions API has the following limits in place:
 * Users of the free API:
 * 2500 directions requests per 24 hour period.
 * Up to 8 waypoints allowed in each request. Waypoints are not available for transit directions.
 * 2 requests per second.
 * Google Maps API for Work customers:
 * Pricing based on volume required directions requests per 24 hour period.
 * 23 waypoints allowed in each request. Waypoints are not available for transit directions.
 * 10 requests per second.
 * Directions API URLs are restricted to approximately 2000 characters, after URL Encoding.
 * As some Directions API URLs may involve many locations along a path, be aware of this limit
 *  when constructing your URLs.The Directions API may only be used in conjunction with 
 *  displaying results on a Google map; using Directions data without displaying a map for 
 *  which directions data was requested is prohibited（禁止）. Additionally, calculation of directions
 *  generates copyrights and warnings which must be displayed to the user in some fashion. 
 *  For complete details on allowed usage, consult the Maps API Terms of Service License Restrictions.
 * @author Dorian
 *
 */
public class RoutePlanner {

	/**
	 * 发起路径分析请求，成功则返回起点到终点的路线，需要多条路线（默认一条）或者其他设置，
	 * 如途经点，路径规划模式（默认驾车）等的在Builder里面设置即可（注意，对于一些请求是
	 * 有限制的，如多个途经点等），更多其他状态请查看RouteResultStatus，
	 * 关于key的问题:没有也会有结果返回，如果带key的话，key不是App Key而是Browser Key，到google console去申请即可
	 * @param params 请求的参数，被封装在Parameter里面，必须的已经在Builder里面处理了，其他的为非必须的
	 * @param routeCallback
	 * @return
	 */
	public HttpHandler<String> route(Parameter params, final OnRouteResultCallback routeCallback){
		StringBuilder sb = new StringBuilder();
		sb.append("https://maps.google.com/maps/api/directions/json?");
		sb.append("origin=").append(params.origin);
		sb.append("&destination=").append(params.destination);
		if(!StringUtils.isEmpty(params.mode)){
			sb.append("&mode=").append(params.mode);
		}
		if(!StringUtils.isEmpty(params.waypoints)){
			if(params.optimize){ // 是否优化途经点的顺序
				sb.append("&waypoints=optimize:true|").append(params.waypoints);
			}else{
				sb.append("&waypoints=").append(params.waypoints);
			}
		}
		if(params.alternatives){
			sb.append("&alternatives=").append(params.alternatives);
		}
		if(!StringUtils.isEmpty(params.avoid)){
			sb.append("&avoid=").append(params.avoid);
		}
		if(!StringUtils.isEmpty(params.language))
			sb.append("&language=").append(params.language);
		if(!StringUtils.isEmpty(params.units)){
			sb.append("&units=").append(params.units);
		}
		if(!StringUtils.isEmpty(params.region)){
			sb.append("&region=").append(params.region);
		}
		if(!StringUtils.isEmpty(params.departure_time)){
			sb.append("&departure_time=").append(params.departure_time);
		}
		if(!StringUtils.isEmpty(params.arrival_time)){
			sb.append("&arrival_time=").append(params.arrival_time);
		}
		if(!StringUtils.isEmpty(params.transit_mode)){
			sb.append("&transit_mode=").append(params.transit_mode);
		}
		if(!StringUtils.isEmpty(params.transit_routing_preference)){
			sb.append("&transit_routing_preference=").append(params.transit_routing_preference);
		}
		if(params.sensor){
			sb.append("&sensor=").append(false);
		}
		if(!StringUtils.isEmpty(params.key)){
			sb.append("&key=").append(params.key);
		}
		HttpUtils httpUtils = new HttpUtils();
		return httpUtils.send(HttpMethod.GET, sb.toString(), new RequestCallBack<String>(){

			@Override
			public void onStart(){
				if(routeCallback != null){
					routeCallback.onStart();
				}
			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				if(routeCallback != null){
					routeCallback.onFailure("Error Message:" + arg0.getMessage() + ";" + arg1);
				}
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				if(routeCallback != null && arg0 != null){
					Log.i("Route", "路径规划结果：" + arg0.result);
					ResponseBean bean = (ResponseBean) new JsonHelper()
							.jsonToBean(arg0.result, ResponseBean.class);
					if(bean == null){
						routeCallback.onFailure("Unknow exception.");
						return;
					}
					routeCallback.onCompleted(bean);
				}
			}

		});
	}

}
