package com.dekaisheng.courier.lbs;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.dekaisheng.courier.lbs.route.result.Route;
import com.dekaisheng.courier.util.DisplayUtil;
import com.dekaisheng.courier.util.log.Log;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.ui.IconGenerator;
import com.dekaisheng.courier.MyApplication;
import com.dekaisheng.courier.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;

/**
 * 地图实用工具，添加Marker，改变地图中心（添加其他覆盖物等，还没写）
 * 
 * @author Dorian
 * 
 */
public class MapUtil {

	private GoogleMap map;
	private AnimateConfig aConfig;

	public MapUtil(GoogleMap map) {
		this.map = map;
		aConfig = new AnimateConfig();
	}

	/**
	 * 动画形式将地图中心移动到指定点
	 * 
	 * @param latlng
	 *            指定的中心
	 */
	public void animateToCenter(LatLng latlng, AnimateConfig acon) {
		if (latlng == null) {
			return;
		}
		AnimateConfig ani = null;
		if (acon != null) {
			ani = acon;
		} else {
			ani = aConfig;
		}
		CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(latlng).zoom(ani.zoomLevel).bearing(ani.bearing) // Sets
																			// the
																			// orientation
																			// of
																			// the
																			// camera
																			// to
																			// east
				.tilt(ani.tilt) // Sets the tilt(倾斜) of the camera to 30 degrees
				.build();
		CameraUpdate update = CameraUpdateFactory
				.newCameraPosition(cameraPosition);
		map.animateCamera(update);
	}

	/**
	 * 
	 * @param location
	 * @param acon
	 *            为null则使用默认的配置
	 */
	public void animateToCenter(Location location, AnimateConfig acon) {
		if (location == null) {
			return;
		}
		if (acon == null) {
			acon = this.aConfig;
		}
		LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
		animateToCenter(ll, acon);
	}

	/**
	 * 
	 * @param lat
	 * @param lng
	 * @param acon
	 *            为null则使用默认的配置
	 */
	public void animateToCenter(double lat, double lng, AnimateConfig acon) {
		if (acon == null) {
			acon = this.aConfig;
		}
		LatLng ll = new LatLng(lat, lng);
		animateToCenter(ll, acon);
	}

	/**
	 * 添加一个Marker，参数最全，自行配置
	 * 
	 * @param ll
	 *            坐标
	 * @param title
	 *            标题
	 * @param snippet
	 *            描述小段文字
	 * @param resId
	 *            Marker图标
	 * @param flat
	 *            是否贴着地面（贴着则跟着地面转动）
	 * @param draggable
	 *            能否拖动
	 * @param alpha
	 *            透明度
	 * @return
	 */
	public Marker addMarker(LatLng ll, String title, String snippet, int resId,
			boolean flat, boolean draggable, float alpha, float rotation) {
		if (ll == null || map == null)
			return null;
		MarkerOptions mo = new MarkerOptions();
		mo.position(ll).title(title).snippet(snippet).alpha(alpha)
				.draggable(draggable).flat(flat).rotation(rotation)
				.icon(BitmapDescriptorFactory.fromResource(resId));
		return map.addMarker(mo);
	}

	public Marker addMarker(LatLng ll, String title, String snippet,
			Bitmap bmp, boolean flat, boolean draggable, float alpha) {
		if (ll == null || map == null)
			return null;
		MarkerOptions mo = new MarkerOptions();
		mo.position(ll).title(title).snippet(snippet).alpha(alpha)
				.draggable(draggable).flat(flat)
				.icon(BitmapDescriptorFactory.fromBitmap(bmp));
		return map.addMarker(mo);
	}

	public Marker addMarker(LatLng ll, String txt, String title,
			String snippet, Context c) {
		if (ll == null)
			return null;
		MarkerOptions mo = new MarkerOptions();
		mo.position(ll)
				.title(title)
				.snippet(snippet)
				.alpha(255)
				.draggable(true)
				.flat(false)
				.icon(BitmapDescriptorFactory.fromBitmap(new IconGenerator(c)
						.makeIcon(txt)));
		return map.addMarker(mo);
	}

	/**
	 * 添加一个一个Marker，可拖动，不随地图转动，完全不透明
	 * 
	 * @param ll
	 * @param title
	 * @param snippet
	 * @param resId
	 * @return
	 */
	public Marker addMarker(LatLng ll, String title, String snippet, int resId) {
		return addMarker(ll, title, snippet, resId, false, true, 1.0f, 0.0f);
	}

	/**
	 * 添加一个蓝色图标的Marker，可拖动，不随地图转动，完全不透明
	 * 
	 * @param ll
	 * @param title
	 * @param snippet
	 * @return
	 */
	public Marker addMarker(LatLng ll, String title, String snippet) {
		return addMarker(ll, title, snippet, R.drawable.location_blue_50x74);
	}

	/**
	 * 将一个Marker添加到地图，因为GoogleMap没有直接添加Marker的接口 所以在刷新地图的时候只能重新调用接口绘制一次Marker，
	 * 
	 * @param marker
	 * @param resId
	 * @return
	 */
	public void addMarker(Marker marker, int resId) {
		if (marker == null) {
			return;
		}
		LatLng ll = marker.getPosition();
		String title = marker.getTitle();
		String snept = marker.getSnippet();
		boolean flat = marker.isFlat();
		boolean draggable = marker.isDraggable();
		float alpha = marker.getAlpha();
		addMarker(ll, title, snept, resId, flat, draggable, alpha, 0.0f);
	}

	/**
	 * 
	 * @param markerMap
	 * @param resId
	 */
	public void addMarkers(HashMap<String, Marker> markerMap, int resId) {
		if (markerMap == null) {
			return;
		}
		for (String key : markerMap.keySet()) {
			addMarker(markerMap.get(key), resId);
		}
	}

	public void clearMarkers(HashMap<String, Marker> markerMap) {
		if (markerMap == null) {
			return;
		}
		for (String key : markerMap.keySet()) {
			markerMap.get(key).remove();
		}
	}

	/**
	 * 改变地图中心动画参数配置
	 * 
	 * @author Dorian
	 * 
	 */
	public static class AnimateConfig {

		public int zoomLevel;
		public float bearing;
		public float tilt;

		public AnimateConfig() {
			this.zoomLevel = 17;
			this.bearing = 0f;
			this.tilt = 0f;
		}

		public AnimateConfig(int zoomLevel, float bearing, float tilt) {
			this.zoomLevel = zoomLevel;
			this.bearing = bearing;
			this.tilt = tilt;
		}
	}

	/**
	 * 添加一条线，由一组有序的点相连
	 * 
	 * @param points
	 *            途经点
	 * @param color
	 *            线的颜色
	 * @param width
	 *            宽度
	 * @return
	 */
	public Polyline addPolyline(List<LatLng> points, int color, float width) {
		if (points == null || points.size() < 2) {
			return null;
		}
		PolylineOptions po = new PolylineOptions();
		Iterator<LatLng> it = points.iterator();
		while (it.hasNext()) {
			po.add(it.next());
		}
		po.color(color).width(width);
		return map.addPolyline(po);
	}

	/**
	 * 添加一条线，由Google Direction Api返回的Route提供有序的点
	 * 
	 * @param route
	 * @param color
	 * @return
	 */
	public Polyline addPolyline(Route route, int color, float width) {
		if (route == null || route.overview_polyline == null) {
			return null;
		}
		return addPolyline(route.overview_polyline.getWayPoint(), color, width);
	}

	/**
	 * 获取一组点的矩形区域，返回格式为安照：东南西北的顺序。 如果传入来的点数少于2则返回一个null;
	 * 求中心点坐标只需要分别对应相加求和，再取平均就可以了
	 * 
	 * @param points
	 * @return
	 */
	public double[] getBounds(List<LatLng> points) {
		if (points == null || points.size() < 1) {
			return null;
		} else if (points.size() == 1) {
			// 只有一点的情况下，以该点为中心向外扩张0.005度形成一个边长为0.01度的矩形
			LatLng temp = points.get(0);
			return new double[] { temp.longitude + 0.005,
					temp.latitude - 0.005, temp.longitude - 0.05,
					temp.latitude + 0.05 };
		}
		LatLng temp = points.get(0);
		double[] bounds = new double[] { temp.longitude, temp.latitude,
				temp.longitude, temp.latitude };
		for (int i = 1; i < points.size(); i++) {
			temp = points.get(i);
			if (temp.longitude > bounds[0]) {
				bounds[0] = temp.longitude;
			} else if (temp.longitude < bounds[2]) {
				bounds[2] = temp.longitude;
			}
			if (temp.latitude > bounds[3]) {
				bounds[3] = temp.latitude;
			} else if (temp.latitude < bounds[1]) {
				bounds[1] = temp.latitude;
			}
		}
		return bounds;
	}

	/**
	 * 获取给点坐标点数组的外接矩形，
	 * 
	 * @param points
	 * @return
	 */
	public LatLngBounds getLatLngBounds(List<LatLng> points) {
		double[] bounds = this.getBounds(points);
		if (bounds == null) {
			return null;
		}
		LatLng southwest = new LatLng(bounds[1], bounds[2]);
		LatLng northeast = new LatLng(bounds[3], bounds[0]);
		LatLngBounds llbs = new LatLngBounds(southwest, northeast);
		return llbs;
	}

	/**
	 * 地图显示区域移动到指定的矩形
	 * 
	 * @param bounds
	 * @param padding
	 *            外接矩形与地图容器间的距离单位px
	 */
	public void animateToBounds(LatLngBounds bounds, int padding) {
		if (bounds == null) {
			return;
		}
		if (padding < 0) {
			padding = 0;
		}
		CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds,
				padding);
		map.animateCamera(update);
	}

	/**
	 * 地图显示区域移动到指定的一组坐标点的外接矩形范围
	 * 
	 * @param points
	 * @param padding
	 *            外接矩形与地图容器间的距离单位px
	 */
	public void animateToBounds(List<LatLng> points, int padding) {
		LatLngBounds bounds = getLatLngBounds(points);
		this.animateToBounds(bounds, padding);
	}

	public void animateToBounds(HashMap<String, Marker> map, int padding) {
		if (map == null) {
			return;
		}
		List<LatLng> points = new ArrayList<LatLng>();
		Marker mk;
		for (String key : map.keySet()) {
			mk = map.get(key);
			if (mk == null) {
				continue;
			}
			if (mk.getPosition() != null) {
				points.add(mk.getPosition());
			}
		}
		animateToBounds(points, padding);
	}

	public void zoomTo(LatLng center, float zoomlevel) {
		CameraUpdate update = CameraUpdateFactory.newLatLngZoom(center,
				zoomlevel);
		map.moveCamera(update);
	}

	private Bitmap bmpWithTxt(Bitmap bmp, String text) {
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setStrokeWidth(1.5f);
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setTextSize(DisplayUtil.sp2px(9));

		Canvas canvas = new Canvas(bmp);
		// 根据数字位数求偏移量，这样得到文字居中
		float x = 21f;
		try {
			int no = Integer.parseInt(text);
			if (no < 10) {
				x = DisplayUtil.dip2px(11);
			} else if (no < 100) {
				x = DisplayUtil.dip2px(8.5f);
			} else {
				paint.setTextSize(DisplayUtil.sp2px(8));
				x = DisplayUtil.dip2px(7f);
			}
		} catch (Exception e) {
			Log.e("bmpWithTxt", e.getMessage() + "");
		}
		canvas.drawText(text, x, DisplayUtil.dip2px(13), paint);
		return bmp;
	}

	public Bitmap bmpWithTxt(int resId, String text) {
		Resources res = MyApplication.getApplication().getBaseContext()
				.getResources();
		Bitmap bmp = BitmapFactory.decodeResource(res, resId).copy(
				Bitmap.Config.ARGB_8888, true);
		;
		return bmpWithTxt(bmp, text);
	}

	/**
	 * title为要画在Marker的文本，cotent为UnFinishedOrderForm的JSON字符串
	 * 
	 * @param loc
	 * @param resId
	 * @param text
	 * @param title
	 * @param content
	 * @return
	 */
	public Marker addTextMarker(LatLng loc, int resId, String text,
			String title, String content) {
		Bitmap bmp = bmpWithTxt(resId, text);
		// Marker的title设置为要绘制的文本，方便下次绘制，订单号在content中提取
		return addMarker(loc, text, content, bmp, true, true, 255);
	}

	/**
	 * 只是单一插值，未能实现
	 * 
	 * @param map
	 * @param latLngOrig
	 * @param latLngDest
	 * @param color
	 */
	public void createDashedLine(GoogleMap map, LatLng latLngOrig,
			LatLng latLngDest, int color) {
		double difLat = latLngDest.latitude - latLngOrig.latitude;
		double difLng = latLngDest.longitude - latLngOrig.longitude;

		double zoom = map.getCameraPosition().zoom;

		double divLat = difLat / (zoom * 2);
		double divLng = difLng / (zoom * 2);

		LatLng tmpLatOri = latLngOrig;

		for (int i = 0; i < (zoom * 2); i++) {

			tmpLatOri = new LatLng(tmpLatOri.latitude + divLat,
					tmpLatOri.longitude + divLng);
		}
	}

	public void drawDashedPolyLine(List<LatLng> list, int color, float width) {
		/* Boolean to control drawing alternate lines */
		boolean added = false;
		for (int i = 0; i < list.size() - 1; i++) {
			/* Get distance between current and next point */
			double distance = getConvertedDistance(list.get(i), list.get(i + 1));

			/* If distance is less than 0.002 kms */
			if (distance < 0.030) {
				if (!added) {
					map.addPolyline(new PolylineOptions().add(list.get(i))
							.add(list.get(i + 1)).width(width).color(color));
					added = true;
				} else {/* Skip this piece */
					added = false;
				}
			} else {
				/* Get how many divisions to make of this line */
				int countOfDivisions = (int) ((distance / 0.030));
				/* Get difference to add per lat/lng */
				double latdiff = (list.get(i + 1).latitude - list.get(i).latitude)
						/ countOfDivisions;
				double lngdiff = (list.get(i + 1).longitude - list.get(i).longitude)
						/ countOfDivisions;
				/*
				 * Last known indicates start point of polyline. Initialized to
				 * ith point
				 */
				LatLng lastKnowLatLng = new LatLng(list.get(i).latitude,
						list.get(i).longitude);
				for (int j = 0; j < countOfDivisions; j++) {

					/* Next point is point + diff */
					LatLng nextLatLng = new LatLng(lastKnowLatLng.latitude
							+ latdiff, lastKnowLatLng.longitude + lngdiff);
					if (!added) {
						map.addPolyline(new PolylineOptions()
								.add(lastKnowLatLng).add(nextLatLng)
								.width(width).color(color));
						added = true;
					} else {
						added = false;
					}
					lastKnowLatLng = nextLatLng;
				}
			}
		}
	}

	private double getConvertedDistance(LatLng latlng1, LatLng latlng2) {
		double distance = DistanceUtil.distance(latlng1.latitude,
				latlng1.longitude, latlng2.latitude, latlng2.longitude);
		BigDecimal bd = new BigDecimal(distance);
		BigDecimal res = bd.setScale(3, RoundingMode.DOWN);
		return res.doubleValue();
	}

	public static class DistanceUtil {

		public static double distance(double lat1, double lon1, double lat2,
				double lon2) {

			if ((lat1 == lat2) && (lon1 == lon2)) {
				return 0;
			} else
				return distance(lat1, lon1, lat2, lon2, 'K');
		}

		public static double distance(double lat1, double lon1, double lat2,
				double lon2, char unit) {
			double theta = lon1 - lon2;
			double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
					+ Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
					* Math.cos(deg2rad(theta));
			dist = Math.acos(dist);
			dist = rad2deg(dist);
			dist = dist * 60 * 1.1515;
			if (unit == 'K') {
				dist = dist * 1.609344;
			} else if (unit == 'N') {
				dist = dist * 0.8684;
			}
			return (dist);
		}

		private static double deg2rad(double deg) {
			return (deg * Math.PI / 180.0);
		}

		private static double rad2deg(double rad) {
			return (rad * 180.0 / Math.PI);
		}
	}

}
