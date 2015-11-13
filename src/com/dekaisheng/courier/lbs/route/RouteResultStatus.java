package com.dekaisheng.courier.lbs.route;

/**
 * google Direction API status值及其意义：
 * 1.OK indicates the response contains a valid result.
 * 2.NOT_FOUND indicates at least one of the locations specified in the request's origin, destination, or waypoints could not be geocoded.
 * 3.ZERO_RESULTS indicates no route could be found between the origin and destination.
 * 4.MAX_WAYPOINTS_EXCEEDED indicates that too many waypointss were provided in the request The maximum allowed waypoints is 8, plus the origin, and destination. ( Google Maps API for Work customers may contain requests with up to 23 waypoints.)
 * 5.INVALID_REQUEST indicates that the provided request was invalid. Common causes of this status include an invalid parameter or parameter value.
 * 6.OVER_QUERY_LIMIT indicates the service has received too many requests from your application within the allowed time period.
 * 7.REQUEST_DENIED indicates that the service denied use of the directions service by your application.
 * 8.UNKNOWN_ERROR indicates a directions request could not be processed due to a server error. The request may succeed if you try again.
 */
public enum RouteResultStatus {

	OK("OK"),
	NOT_FOUND("NOT_FOUND"),
	ZERO_RESULTS("ZERO_RESULTS"), 
	MAX_WAYPOINTS_EXCEEDED("MAX_WAYPOINTS_EXCEEDED"),
	INVALID_REQUEST("INVALID_REQUEST"),
	OVER_QUERY_LIMIT("OVER_QUERY_LIMIT"), 
	REQUEST_DENIED("REQUEST_DENIED"),
	UNKNOWN_ERROR("UNKNOWN_ERROR");
	
	private String value;
	
	public String getVal(){
		return value;
	}
	private RouteResultStatus(String val){
		this.value = val;
	}
}
