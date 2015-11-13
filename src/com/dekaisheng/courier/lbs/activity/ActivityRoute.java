package com.dekaisheng.courier.lbs.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dekaisheng.courier.R;
import com.dekaisheng.courier.activity.BaseActivity;
import com.dekaisheng.courier.lbs.MapUtil;
import com.dekaisheng.courier.lbs.route.Mode;
import com.dekaisheng.courier.lbs.route.OnRouteResultCallback;
import com.dekaisheng.courier.lbs.route.Parameter;
import com.dekaisheng.courier.lbs.route.Parameter.Builder;
import com.dekaisheng.courier.lbs.route.RoutePlanner;
import com.dekaisheng.courier.lbs.route.RouteResultStatus;
import com.dekaisheng.courier.lbs.route.result.Leg;
import com.dekaisheng.courier.lbs.route.result.PolyLine;
import com.dekaisheng.courier.lbs.route.result.ResponseBean;
import com.dekaisheng.courier.lbs.route.result.Route;
import com.dekaisheng.courier.lbs.route.result.Step;
import com.dekaisheng.courier.util.DisplayUtil;
import com.dekaisheng.courier.util.log.LoggerConfig;
import com.dekaisheng.courier.view.SlideBottomPanel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 路径规划主页，四种类型（换乘，驾车，骑自行车，步行），暂时提供两种即可（骑自行车、驾车） 可以继续查看文字详情，导航功能不提供。 1.所有订单
 * 2.选择订单 3.个人位置 4.地图范围，所有订单的外接矩形 5.路径规划，个人位置到选择订单 6.路径详情 7.路线style
 * 
 * @author Dorian
 * 
 */
@SuppressLint("InflateParams")
public class ActivityRoute extends BaseActivity implements OnMapReadyCallback,
		OnItemClickListener {

	/**
	 * 上一个Activity传入来的RouteBean，现在的实现是上一个Activity查到Route后再
	 * 跳转到这个Activity，所以能来到这里，说明这个RouteBean不为空（先检查）
	 */
	public static ResponseBean routeBean; //
	public static LatLng start; // 如果起点终点相同则不需要再查询一次，暂时不这样做
	public static LatLng end;
	private Mode mode;
	private String startAddress;
	private String endAddress;

	public static final String START_LAT = "startLat";
	public static final String START_LNG = "startLng";
	public static final String END_LAT = "endLat";
	public static final String END_LNG = "endLng";
	public static final String MODE = "mode";
	public static final String START_ADDRESS = "startAddress";
	public static final String END_ADDRESS = "endAddress";

	private final int startResID = R.drawable.location_blue_50x74;// my_location_blue;
	private final int endResID = R.drawable.location_red_50x74;

	private HashMap<String, Marker> markerMap;
	private int padding = DisplayUtil.dip2px(50);
	private MapFragment mapFragment;
	private GoogleMap googleMap;
	private float oldZoom = -1;
	private boolean isGoogleEnabled;
	private Marker startMarker;
	private Marker endMarker;
	private Polyline currentRoute;
	private Polyline currentRouteBg;
	@ViewInject(R.id.activity_route_actionbar_layout)
	private FrameLayout actionBarLayout;
	@ViewInject(R.id.activity_route_img_goback)
	private ImageView gobackImg;
	@ViewInject(R.id.activity_route_img_bus)
	private ImageView busImg;
	@ViewInject(R.id.activity_route_img_car)
	private ImageView carImg;
	@ViewInject(R.id.activity_route_img_bycycle)
	private ImageView bycycleImg;
	@ViewInject(R.id.activity_route_txt_distance)
	private TextView distanceTxt;
	//private MyLocationService locService;
	// private Marker deviceLocationMarker;
	// private int deviceLocationResId = R.drawable.my_location_blue;
	//private boolean bind = false;
	@ViewInject(R.id.sbv)
	private SlideBottomPanel sbv;
	// private Campass campass;
	private Polyline selectedStep;
	private Polyline selectedStepBg;

	TranslateAnimation animationUp;
	TranslateAnimation animationDown;
	private ArrayList<Instruction> data;
	private AdapterRouteDetails adapter;
	private View header;
	private TextView startTxt;
	private View footer;
	private TextView endTxt;
	private MapUtil mu;

//	private ServiceConnection connection = new ServiceConnection() {
//
//		/**
//		 * 在绑定的时候设置定位时间和空间间隔更加频繁，解绑时设置为默认，更优化
//		 */
//		@Override
//		public void onServiceConnected(ComponentName name, IBinder service) {
//			MyBinder mb = (MyBinder) service;
//			locService = mb.getService();
//			// locService.setHandler(handler);
//			bind = true;
//			Log.i(TAG, "ActivityHome已经绑定LocationService");
//		}
//
//		@Override
//		public void onServiceDisconnected(ComponentName name) {
//			Log.i(TAG, "ActivityHome已经解绑定LocationService");
//		}
//
//	};

	// @SuppressLint("HandlerLeak")
	// private Handler handler = new Handler() {
	//
	// private float oldDir = 0; // 上一次记录到的角度
	//
	// @Override
	// public void handleMessage(Message msg) {
	// switch (msg.what) {
	// case LocationService.MSG_CODE:
	// handleLocation(msg.obj);
	// break;
	// case Campass.MSG_CODE_SENSOR:
	// handleDirection(msg.obj);
	// default:
	// break;
	// }
	// }
	//
	// private void handleDirection(Object obj) {
	// if (obj == null) {
	// Log.i(TAG, "传感器传来参数未null");
	// return;
	// }
	// float[] value = (float[]) obj;
	// if (value.length > 0) {
	// if (deviceLocationMarker != null) {
	// float dis = oldDir - value[0];
	// if (dis > 5 || dis < -5) {
	// deviceLocationMarker.setRotation(value[0]);
	// oldDir = value[0];
	// Log.i(TAG, "成功改变方向:" + value[0]);
	// } else {
	// Log.i(TAG, "传感器结果太接近(2):" + oldDir + "-" + value[0]);
	// }
	// } else {
	// Log.i(TAG, "deviceLocationMarker 为空");
	// }
	// }
	// }
	//
	// private void handleLocation(Object obj) {
	// /*
	// * 接收到派送员位置，在这里，可以设置定位的时间和距离间隔（onBind的时候就应该设置），
	// * 然后转为导航，退出导航模式后再转换为后台模式（时间和距离间隔较大）
	// */
	// if (obj == null || googleMap == null) {
	// return;
	// }
	// Location loc = null;
	// if (obj instanceof Location) {
	// loc = (Location) obj;
	// getDeviceLocation(new LatLng(loc.getLatitude(),
	// loc.getLongitude()));
	// }
	// }
	// };

	public void onBackPressed() {
		if (sbv.isPanelShowing()) {
			sbv.hide();
			return;
		}
		super.onBackPressed();
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_route);
		ViewUtils.inject(this);
		//initMap();
		initData();
		initView();
	}

	private void initData() {
		markerMap = new HashMap<String, Marker>();
		Intent i = getIntent();
		double slat = i.getDoubleExtra(START_LAT, -10000);
		double slng = i.getDoubleExtra(START_LNG, -10000);
		double elat = i.getDoubleExtra(END_LAT, -10000);
		double elng = i.getDoubleExtra(END_LNG, -10000);
		startAddress = i.getStringExtra(START_ADDRESS);
		endAddress = i.getStringExtra(END_ADDRESS);
		if (slat < -90 || slng < -360 || elat < -90 || elng < -360) {
			start = null;
			end = null;
		} else {
			start = new LatLng(slat, slng);
			end = new LatLng(elat, elng);
		}
		this.mode = (Mode) i.getSerializableExtra(MODE);
	}

	@Override
	protected void onResume() {
		// this.campass.registerSensor(); // 注册传感器监听
		initMap();
//		if (bind == false) {
//			bind = this.bindService(new Intent(this, MyLocationService.class),
//					connection, Context.BIND_AUTO_CREATE);
//			Log.i(TAG, "BindService:" + bind);
//		}
		super.onResume();
	}

	/**
	 * 获取设备位置。
	 */
	// private void getDeviceLocation(LatLng latLng) {
	// if (deviceLocationMarker == null) {
	// deviceLocationMarker = mu.addMarker(latLng, "", "",
	// deviceLocationResId);
	// } else {
	// deviceLocationMarker.setPosition(latLng);
	// }
	// }

	@Override
	protected void onPause() {
		// this.campass.unRegisterSensor();
//		if (bind) {
//			this.unbindService(connection);
//			bind = false;
//			Log.i(TAG, "unBindService bind = " + bind);
//		}
		googleMap.setMyLocationEnabled(false);
		super.onPause();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@OnClick(value = { R.id.activity_route_img_goback,
			R.id.activity_route_img_bus, R.id.activity_route_img_car,
			R.id.activity_route_img_bycycle, R.id.activity_route_txt_distance })
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.activity_route_img_goback:
			finish();
			break;
		case R.id.activity_route_img_bus:
			route(Mode.TRANSIT);
			break;
		case R.id.activity_route_img_car:
			route(Mode.DRIVING);
			break;
		case R.id.activity_route_img_bycycle:
			route(Mode.BICYCLING);
			break;
		case R.id.activity_route_txt_distance:
//			if (sbv.isPanelShowing()) {
//				sbv.hide();
//			} else {
//				sbv.displayPanel();
//			}
			break;
		}
	}

	@Override
	public void onMapReady(GoogleMap map) {
		this.googleMap = map;
		googleMap.setMyLocationEnabled(true);
		mu = new MapUtil(googleMap);
		this.startMarker = mu.addMarker(start, "", "", startResID);
		this.endMarker = mu.addMarker(end, "", "", endResID);
		markerMap.put("start", startMarker);
		markerMap.put("end", endMarker);
		mu.zoomTo(
				start,
				getResources().getInteger(
						R.integer.google_map_default_zoomlevel));

		map.getUiSettings().setAllGesturesEnabled(true);
		map.getUiSettings().setRotateGesturesEnabled(true);
		map.getUiSettings().setMapToolbarEnabled(true); // 路径规划，Place API
		map.getUiSettings().setCompassEnabled(true);
		map.getUiSettings().setIndoorLevelPickerEnabled(true);
		map.getUiSettings().setZoomControlsEnabled(false);
		map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
			@Override
			public void onMapLoaded() {
				isGoogleEnabled = true;
				if (routeBean != null) {
					drawRouteResult(routeBean);
				} else {
					// 不会执行
					route(mode);
				}
			}
		});
		map.setOnCameraChangeListener(new OnCameraChangeListener() {

			@Override
			public void onCameraChange(CameraPosition arg0) {
				if (oldZoom != arg0.zoom) {

				}
			}

		});
		map.setOnMapLongClickListener(new OnMapLongClickListener() {

			@Override
			public void onMapLongClick(LatLng arg0) {
				if (currentRouteBg != null) {
					mu.animateToBounds(currentRouteBg.getPoints(),
							DisplayUtil.dip2px(padding));
				}
			}
		});
		// getDeviceLocation(ActivityHomeNew.deviceLocation);
	}

	private void initMap() {
		int status = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getBaseContext());
		if (status == ConnectionResult.SUCCESS) {
			mapFragment = (MapFragment) getFragmentManager().findFragmentById(
					R.id.activity_route_google_map);
			mapFragment.getMapAsync(this);
		} else {
			isGoogleEnabled = false;
			showTipsDialog(R.string.toast_tips_google_service_not_avaliable);
		}
	}

	private void route(Mode m) {
		if (!isGoogleEnabled || start == null || end == null) {
			// 起点和终点为空，应该不会出现，一般上一个Activity会处理，这里就不再处理
			return;
		}
		RoutePlanner planner = new RoutePlanner();
		Builder b = new Builder(start, end);
		b.mode(m);
		Parameter params = b.build();
		planner.route(params, new OnRouteResultCallback() {

			@Override
			public void onStart() {
				showProgressDialog(ActivityRoute.this, "Loading");
			}

			@Override
			public void onCompleted(ResponseBean rb) {
				dismissProgressDialog();
				if (RouteResultStatus.OK.getVal().equals(rb.status)) {
					routeBean = rb;
					drawRouteResult(rb);
				} else if (RouteResultStatus.NOT_FOUND.getVal().endsWith(
						rb.status)) {
					showToastShort(R.string.toast_tips_route_not_found);
				} else if (RouteResultStatus.ZERO_RESULTS.getVal().endsWith(
						rb.status)) {
					showToastShort(R.string.toast_tips_route_zero_result);
				} else if (RouteResultStatus.MAX_WAYPOINTS_EXCEEDED.getVal()
						.equals(rb.status)) {
					showToastShort(R.string.toast_tips_route_max_waypoints_exceeded);
				} else if (RouteResultStatus.INVALID_REQUEST.getVal().endsWith(
						rb.status)) {
					showToastShort(R.string.toast_tips_route_invalid_request);
				} else if (RouteResultStatus.OVER_QUERY_LIMIT.getVal()
						.endsWith(rb.status)) {
					showToastShort(R.string.toast_tips_route_over_query_limit);
				} else if (RouteResultStatus.REQUEST_DENIED.getVal().endsWith(
						rb.status)) {
					showToastShort(R.string.toast_tips_route_request_denied);
				} else if (RouteResultStatus.UNKNOWN_ERROR.getVal().endsWith(
						rb.status)) {
					showToastShort(R.string.toast_tips_route_unknow_error);
				} else {
					// 略有多余，如果google返回了除上面8中status之外的status，则提示not found
					showToastShort(R.string.toast_tips_route_not_found);
				}
			}

			@Override
			public void onFailure(String msg) {
				dismissProgressDialog();
				if (LoggerConfig.DEBUG)
					showToastShort(msg);
				else
					showToastShort(R.string.toast_tips_route_not_found);
			}

		});
	}

	private void drawRoute(Route route) {
		if (currentRoute != null) {
			currentRoute.remove();
		}
		if (currentRouteBg != null) {
			currentRouteBg.remove();
		}
		List<LatLng> points = route.overview_polyline.getWayPoint();
		this.currentRouteBg = mu.addPolyline(points,
				Color.argb(205, 44, 77, 200), DisplayUtil.dip2px(6f));
		this.currentRoute = mu.addPolyline(points,
				Color.argb(205, 66, 145, 88), DisplayUtil.dip2px(3f));
		mu.animateToBounds(points, DisplayUtil.dip2px(padding));
		dismissProgressDialog();
	}

	private void drawRouteResult(ResponseBean rb) {
		if (rb == null || rb.routes == null || rb.routes.size() < 1) {
			return;
		}
		Route r = rb.routes.get(0);// 只有一条路线。
		if (r != null) {
			this.distanceTxt
					.setText(r.formatDistance() + "  " + r.formatTime());
			drawRoute(r);
			getInstruction(routeBean.routes.get(0));
		}
	}

	private void getInstruction(Route route) {
		if (route == null) {
			return;
		}
		for (int i = 0; i < route.legs.size(); i++) {
			Leg leg = route.legs.get(i);
			for (int j = 0; j < leg.steps.size(); j++) {
				Step step = leg.steps.get(j);
				if (step.steps == null) {
					add(step);
				} else {
					Step in;
					for (int k = 0; k < step.steps.size(); k++) {
						in = step.steps.get(k);
						add(in);
					}
				}
			}
		}
		this.adapter.notifyDataSetChanged();
	}

	private void add(Step step) {
		Instruction in = new Instruction(step.maneuver, step.html_instructions,
				step.distance.text, step.polyline);
		this.data.add(in);
	}

	private void initView() {
		// 添加footer和header，header是My Location，footer是包裹的位置
		header = LayoutInflater.from(this).inflate(
				R.layout.listview_route_detail_header, null);
		footer = LayoutInflater.from(this).inflate(
				R.layout.listview_route_detail_footer, null);
		startTxt = (TextView) header
				.findViewById(R.id.listview_route_detail_header_txt_start);
		endTxt = (TextView) footer
				.findViewById(R.id.listview_route_detail_bottom_txt_end);
		startTxt.setText("" + startAddress);
		endTxt.setText(endAddress);
		ListView listView = (ListView) findViewById(R.id.activity_route_listview);
		listView.addFooterView(footer);
		listView.addHeaderView(header);
		listView.setOnItemClickListener(this);
		data = new ArrayList<Instruction>();
		adapter = new AdapterRouteDetails(data, this);
		listView.setAdapter(adapter);
		// 指南针
		// this.campass = new Campass(this, this.handler);
		// if (!campass.intSensor()) {
		// deviceLocationResId = R.drawable.my_location_blue_circle;
		// if (LoggerConfig.DEBUG) {
		// Log.i(TAG, "您的设备没有重力传感器或者磁场传感器");
		// showTipsDialog("您的设备没有重力传感器或者磁场传感器");
		// }
		// }
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if (arg2 == 0) {
			// 移动到起点
			mu.animateToCenter(start, null);
		} else if (arg2 > data.size()) {
			// 移动到终点
			mu.animateToCenter(end, null);
		} else {
			PolyLine pl = this.data.get(arg2 - 1).line;
			if (pl != null) {
				if (this.selectedStep != null) {
					this.selectedStep.remove();
					this.selectedStepBg.remove();
				}
				this.selectedStep = mu.addPolyline(pl.getWayPoint(),
						Color.argb(205, 44, 255, 20), DisplayUtil.dip2px(8f));
				this.selectedStepBg = mu.addPolyline(pl.getWayPoint(),
						Color.argb(205, 66, 255, 88), DisplayUtil.dip2px(4f));
				mu.animateToBounds(selectedStep.getPoints(), padding);
			}
		}
		adapter.setSelectedPosition(arg2 - 1);
		adapter.notifyDataSetInvalidated();
	}
}
