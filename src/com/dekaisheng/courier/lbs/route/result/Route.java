package com.dekaisheng.courier.lbs.route.result;

import java.io.Serializable;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;

/**
 * 实体类，路径分析的实体类，包括换乘路线
 * （查过一次换乘路线，里面的实体类都包括在这里了，不排除可能还有其他）
 * @author Dorian
 *
 */
public class Route implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 这条路线的矩形方框，外接矩形
	 */
	public Bounds bounds;

	/**
	 * 应该显示google的版权信息
	 */
	public String copyrights;

	/**
	 * 一条完整的Route由多段Leg组成，每一个Leg又由多个Step组成{@link Leg}
	 */
	public List<Leg> legs;

	/**
	 * 整条路线的的概略路线，途经点有可能经过抽样，没有分段那样圆滑，
	 * 只是猜测，反正画线的时候就是用这个
	 */
	public PolyLine overview_polyline;

	/**
	 * 一些文本消息，很多情况是没有值的
	 */
	public String summary;

	/**
	 * 一些警告消息，可能由多条
	 */
	public List<String> warnings;

	/**
	 * 多途经点时，如果做优化，则会输出优化后的途经点顺序（里面的坐标是原来输入的坐标）
	 */
	public List<Integer> waypoint_order;

	/////////////////// 自定义的一些属性和方法  ////////////////////////

	private int time = -1;
	
	private int distance = - 1;
	
	public List<LatLng> getOverview(){
		if(overview_polyline == null){
			return null;
		}
		return overview_polyline.getWayPoint();
	}
	
	/**
	 * 获取整条Route的路程的距离
	 * @return
	 */
	public int getDistance(){
		if(legs == null || distance > 0){
			return distance;
		}
		Leg leg;
		for(int i = 0; i < legs.size(); i++){
			leg = legs.get(i);
			if(leg.distance != null){
				distance += leg.distance.value;
			}
		}
		return distance;
	}
	
	/**
	 * 获取整条Route所需的时间
	 * @return
	 */
	public int getTime(){
		if(legs == null || time > 0){
			return time;
		}
		Leg leg;
		for(int i = 0; i < legs.size(); i++){
			leg = legs.get(i);
			if(leg.duration != null){
				time += leg.duration.value;
			}
		}
		return time;
	}
	
	/**
	 * 格式化距离，遍历所有的Leg，距离求和，再格式化为10.2km、500m的格式
	 * @return
	 */
	public String formatDistance(){
		String tt = null;
		if(legs == null){
			return tt;
		}
		int dis = getDistance();
		if(dis > 1000){
			tt =  String.format("%.1f", dis/1000.0) + "km";
		}else{
			tt = dis + "m";
		}
		return tt;
	}

	/**
	 * 格式化时间，遍历所有Leg，时间求和，再格式化为1.5h、40minute的格式
	 * @return
	 */
	public String formatTime(){
		String tim = null;
		if(legs == null){
			return tim;
		}
		int time = getTime();
		if(time > 3600){
			tim = time / 3600 + "h" + (time % 3600) / 60 + "minute";
		}else{
			tim = (int)(time / 60.0 + 0.5) + "minute";
		}
		return tim;
	}

}










