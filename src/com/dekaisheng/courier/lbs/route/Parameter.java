package com.dekaisheng.courier.lbs.route;

import java.util.Iterator;
import java.util.List;
import com.google.android.gms.maps.model.LatLng;

/**
 * 路径规划的参数，只能够通过Builder来创建，Builder简化了路径规划参数的输入
 * @author Dorian
 *
 */
public class Parameter {
	
	/**
	 * 起点，可以是地名，也可以是坐标点
	 * The address, place ID or textual latitude/longitude value 
	 * from which you wish to calculate directions. Place IDs must 
	 * be prefixed with place_id:. The place ID may only be specified 
	 * if the request includes an API key or a Google Maps API for 
	 * Work client ID. If you pass an address as a string, the Directions 
	 * service will geocode the string and convert it to a latitude/longitude 
	 * coordinate to calculate directions. This coordinate may be different 
	 * from that returned by the Geocoding API, for example a building entrance 
	 * rather than its center. If you pass coordinates, they will be used 
	 * unchanged to calculate directions. Ensure that no space exists between 
	 * the latitude and longitude values. See the place ID overview.
	 */
	public String origin;
	
	/**
	 * 终点，可以是地名，也可以是坐标点
	 * The address, place ID or textual latitude/longitude value from 
	 * which you wish to calculate directions. Place IDs must be prefixed 
	 * with place_id:. The place ID may only be specified if the request 
	 * includes an API key or a Google Maps API for Work client ID. 
	 * If you pass an address as a string, the Directions service will 
	 * geocode the string and convert it to a latitude/longitude coordinate 
	 * to calculate directions. This coordinate may be different from that 
	 * returned by the Geocoding API, for example a building entrance rather than its center. 
	 * If you pass coordinates, they will be used unchanged to calculate directions. 
	 * Ensure that no space exists between the latitude and longitude values.
	 */
	public String destination;
	
	/**
	 * 路径规划类型
	 *  (defaults to driving) — Specifies the mode of transport to use 
	 *  when calculating directions. Valid values and other request 
	 *  details are specified in Travel Modes.
	 *  1.driving (default) indicates standard driving directions using the road network.
	 *  2.walking requests walking directions via pedestrian paths & sidewalks (where available).
	 *  3.bicycling requests bicycling directions via bicycle paths & preferred streets (where available).
	 *  4.transit requests directions via public transit routes (where available). 
	 *            If you set the mode to transit, you can optionally specify either 
	 *            a departure_time or an arrival_time. If neither time is specified, 
	 *            the departure_time defaults to now (that is, the departure time defaults to the current time). You can also optionally include a transit_mode and/or a transit_routing_preference.
	 */
	public String mode;
	
	/**
	 * 是否多条路线
	 *  If set to true, specifies that the Directions service may provide 
	 *  more than one route alternative in the response. 
	 *  Note that providing route alternatives may increase the response time from the server.
	 */
	public boolean alternatives;
	
	/**
	 * 需要经过的点，可以是地名也可以是坐标点，用"|"分隔
	 * optimize:true：默认是按照你提供的顺序来做路径分析，但是你可以使用这个参数来优化这个顺序
	 * &waypoints=optimize:true|Barossa+Valley,SA|Clare,SA|Connawarra,SA|McLaren+Vale,SA&key=
	 * 
	 * Specifies an array of waypoints. Waypoints alter a route by routing it through 
	 * the specified location(s). A waypoint is specified as either a latitude/longitude coordinate, 
	 * as a place ID, or as an address which will be geocoded. Place IDs must be prefixed with place_id:. 
	 * The place ID may only be specified if the request includes an API key or a Google Maps API for 
	 * Work client ID. Waypoints are only supported for driving, walking and bicycling directions. 
	 * (For more information on waypoints, see Using Waypoints in Routes below.)
	 */
	public String waypoints;
	
	public boolean optimize;
	
	/**
	 * Indicates that the calculated route(s) should avoid the indicated features. 
	 * This parameter supports the following arguments:
	 * 1.tolls——indicates that the calculated route should avoid toll roads/bridges.
	 * 2.highways——indicates that the calculated route should avoid highways.
	 * 3.ferries——indicates that the calculated route should avoid ferries.
	 * 4.indoor——indicates that the calculated route should avoid indoor steps for walking 
	 *        and transit directions. Only requests that include an API key or a 
	 *        Google Maps API for Work client ID will receive indoor steps by default.
	 *        For more information see Route Restrictions below.
	 */
	public String avoid;
	
	/**
	 * Specifies the unit system to use when displaying results. 
	 * Valid values are specified in Unit Systems below.
	 * 1.metric——specifies usage of the metric system. Textual distances are returned using kilometers and meters.
	 * 2.imperial——specifies usage of the Imperial (English) system. Textual distances are returned using miles and feet.
	 */
	public String units;
	
	/**
	 * 返回结果的语言设置
	 * Specifies the language in which to return results. See the list of supported domain languages. 
	 * Note that we often update supported languages so this list may not be exhaustive. 
	 * If language is not supplied, the service will attempt to use the native language of the domain 
	 * from which the request is sent.
	 * 1.zh-CN	Chinese (Simplified)
	 * 2.zh-TW	Chinese (Traditional)
	 * 3.en	English
	 */
	public String language;
	
	/**
	 * Specifies the region code, specified as a ccTLD ("top-level domain") two-character value. 
	 * (For more information see Region Biasing below.)
	 */
	public String region;
	
	/**
	 * 换乘工具选择
	 * Specifies one or more preferred modes of transit. This parameter may only be specified 
	 * for transit directions, and only if the request includes an API key or a Google Maps 
	 * API for Work client ID. The parameter supports the following arguments:
	 * 1.bus indicates that the calculated route should prefer travel by bus.
	 * 2.subway indicates that the calculated route should prefer travel by subway.
	 * 3.train indicates that the calculated route should prefer travel by train.
	 * 4.tram indicates that the calculated route should prefer travel by tram(电车) and light rail(铁路).
	 * 5.rail indicates that the calculated route should prefer travel by train, 
	 *        tram, light rail, and subway. This is equivalent to transit_mode=train|tram|subway.
	 */
	public String transit_mode;
	
	/**
	 * 最少步行，最少换乘选项
	 * Specifies preferences for transit routes. Using this parameter, 
	 * you can bias the options returned, rather than accepting the default 
	 * best route chosen by the API. This parameter may only be specified for 
	 * transit directions, and only if the request includes an API key or a 
	 * Google Maps API for Work client ID. The parameter supports the following arguments:
	 * 1.less_walking indicates that the calculated route should prefer limited amounts of walking.
	 * 2.fewer_transfers indicates that the calculated route should prefer a limited number of transfers.
	 */
	public String transit_routing_preference;
	
	/**
	 * 到达时间，最多只能在到达时间和开始时间之间选一个，不能两个同时定义
	 * Specifies the desired time of arrival for transit directions, in seconds since midnight, 
	 * January 1, 1970 UTC. You can specify either departure_time or arrival_time, but not both. 
	 * Note that arrival_time must be specified as an integer.
	 */
	public String arrival_time;
	
	/**
	 * 出发时间(从基准时间算起，毫秒为单位)
	 * Specifies the desired time of departure. You can specify the time as an integer in seconds 
	 * since midnight, January 1, 1970 UTC. Alternatively, you can specify a value of now, 
	 * which sets the departure time to the current time (correct to the nearest second). 
	 * The departure time may be specified in two cases:
	 * 1.For transit directions: You can optionally specify one of departure_time or arrival_time. 
	 *   If neither time is specified, the departure_time defaults to now (that is, the departure 
	 *   time defaults to the current time).
	 * 2.For driving directions: Google Maps API for Work customers can specify the departure_time 
	 *   to receive trip duration considering current traffic conditions. The departure_time must be 
	 *   set to within a few minutes of the current time.
	 */
	public String departure_time;

	
	public boolean sensor;
	
	/**
	 * 不是google Android APP Key，而是浏览器的key，使用app key无法访问的
	 */
	public String key;
	
	private Parameter(Builder builder){
		this.alternatives = builder.alternatives;
		this.arrival_time = builder.arrival_time;
		this.avoid = builder.avoid;
		this.departure_time = builder.departure_time;
		this.destination = builder.destination;
		this.language = builder.language;
		this.mode = builder.mode;
		this.origin = builder.origin;
		this.region = builder.region;
		this.transit_mode = builder.transit_mode;
		this.transit_routing_preference = builder.transit_routing_preference;
		this.units = builder.units;
		this.waypoints = builder.waypoints;
		this.optimize = builder.optimize;
		this.sensor = builder.sensor;
		this.key = builder.key;
	}
	
	public static class Builder{
		
		private String key;
		private String origin;
		private String destination;
		private String mode;
		private boolean alternatives;
		private String waypoints;
		private boolean optimize;
		private String avoid;
		private String units;
		private String language;
		private String region;
		private String transit_mode;
		private String transit_routing_preference;
		private String arrival_time;
		private String departure_time;
		private boolean sensor = false;
		
		public Parameter build(){
			return new Parameter(this);
		}

		/**
		 * 保证起点和终点不能为空
		 * @param origin
		 * @param destination
		 */
		public Builder(LatLng origin, LatLng destination){
			this.origin = origin.latitude + "," + origin.longitude;
			this.destination = destination.latitude + "," + destination.longitude;
			this.mode = Mode.DRIVING.getVal(); // 默认是驾车
			this.alternatives = false; // 默认一条路线
			//this.key = MyApplication.getApplication().getString(R.string.google_browser_key);

		}
		
		public Builder(String origin, String destination){
			this.origin = origin;
			this.destination = destination;
			this.mode = Mode.DRIVING.getVal(); // 默认是驾车
			this.alternatives = false;
			//this.key = MyApplication.getApplication().getString(R.string.google_browser_key);

		}
		
		public Builder mode(Mode m){
			this.mode = m.getVal();
			return this;
		}
		
		/**
		 * 途经点坐标点集合
		 * @param points
		 * @return
		 */
		public Builder waypoints(List<LatLng> points){
			if(points == null || points.size() < 1){
				this.waypoints = "";
				return this;
			}
			Iterator<LatLng> it = points.iterator();
			LatLng t = null;
			StringBuilder sb = new StringBuilder();
			while(it.hasNext()){
				t = it.next();
				sb.append(t.latitude).append(",").append(t.longitude).append("|");
			}
			String temp = sb.toString();
			int index = temp.lastIndexOf("|");
			this.waypoints = temp.substring(0, index);
			return this;
		}
		
		/**
		 * 途经点名称集合
		 * @param names
		 * @return
		 */
		public Builder waypointNames(List<String> names){
			if(names == null || names.size() < 1){
				this.waypoints = "";
				return this;
			}
			Iterator<String> it = names.iterator();
			StringBuilder sb = new StringBuilder();
			while(it.hasNext()){
				sb.append(it.next()).append("|");
			}
			String temp = sb.toString();
			int index = temp.lastIndexOf("|");
			this.waypoints = temp.substring(0, index);
			return this;
		}
		
		/**
		 * 有多个途经点时，是否优化途经点的顺序
		 * @param optimize
		 * @return
		 */
		public Builder optimize(boolean optimize){
			this.optimize = optimize;
			return this;
		}
		
		/**
		 * 是否有多条路径
		 * @param val
		 * @return
		 */
		public Builder alterNatives(Boolean val){
			this.alternatives = val;
			return this;
		}
		
		public Builder avoid(Avoid... avoid){
			for(int i = 0; i < avoid.length; i++){
				this.avoid += avoid[i].getVal() + "|";
			}
			int index = this.avoid.lastIndexOf("|");
			this.avoid = this.avoid.substring(0, index);
			return this;
		}
		
		public Builder language(String language){
			this.language = language;
			return this;
		}
	
		public Builder transitMode(TransitMode... mode){
			for(int i = 0; i < mode.length; i++){
				this.transit_mode += mode[i].getVal() + "|";
			}
			int index = this.transit_mode.lastIndexOf("|");
			this.transit_mode = this.transit_mode.substring(0, index);
			return this;
		}
		
		public Builder transitRoutingPreference(TransitRoutingPreference preference){
			this.transit_routing_preference = preference.getVal();
			return this;
		}
		
		public Builder arrivalTime(String arrival){
			this.arrival_time = arrival;
			return this;
		}
	
		public Builder departureTime(String departure){
			this.departure_time = departure;
			return this;
		}

		public Builder unit(Unit unit){
			this.units = unit.getVal();
			return this;
		}
		
		public Builder region(String region){
			this.region = region;
			return this;
		}

		public Builder sensor(boolean sensor){
			this.sensor = sensor;
			return this;
		}
	
		public Builder key(String key){
			this.key = key;
			return this;
		}
	}
}
















