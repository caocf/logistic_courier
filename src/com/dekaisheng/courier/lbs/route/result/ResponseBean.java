package com.dekaisheng.courier.lbs.route.result;

import java.io.Serializable;
import java.util.List;

public class ResponseBean implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 1.OK indicates the response contains a valid result.
	 * 2.NOT_FOUND indicates at least one of the locations specified in the request's origin, destination, or waypoints could not be geocoded.
	 * 3.ZERO_RESULTS indicates no route could be found between the origin and destination.
	 * 4.MAX_WAYPOINTS_EXCEEDED indicates that too many waypointss were provided in the request The maximum allowed waypoints is 8, plus the origin, and destination. ( Google Maps API for Work customers may contain requests with up to 23 waypoints.)
	 * 5.INVALID_REQUEST indicates that the provided request was invalid. Common causes of this status include an invalid parameter or parameter value.
	 * 6.OVER_QUERY_LIMIT indicates the service has received too many requests from your application within the allowed time period.
	 * 7.REQUEST_DENIED indicates that the service denied use of the directions service by your application.
	 * 8.UNKNOWN_ERROR indicates a directions request could not be processed due to a server error. The request may succeed if you try again.
	 */
	public String status;
	
	/**
	 * 起点和终点的编码点
	 */
	public List<CodePoint> geocoded_waypoints;
	
	/**
	 * 路径规划返回的主要内容
	 */
	public List<Route> routes;
	
	public String error_message;

}






