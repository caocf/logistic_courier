package com.dekaisheng.courier.home;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.cn.xtouch.tfl.alert.AlertView;
import com.cn.xtouch.tfl.alert.OnItemClickListener;
import com.cn.xtouch.tfl.promotedactionslibrary.PromotedActions;
import com.dekaisheng.courier.AppManager;
import com.dekaisheng.courier.Config;
import com.dekaisheng.courier.MyApplication;
import com.dekaisheng.courier.activity.BaseActivity;
import com.dekaisheng.courier.activity.SignForDialog;
import com.dekaisheng.courier.activity.SignForDialog.ISignForListener;
import com.dekaisheng.courier.cache.ApiResultCode;
import com.dekaisheng.courier.cache.Cache;
import com.dekaisheng.courier.cache.Constants;
import com.dekaisheng.courier.cache.SharePreferenceHelper;
import com.dekaisheng.courier.core.database.dao.PushMessageDao;
import com.dekaisheng.courier.core.database.dao.UnFinishedOrderDao;
import com.dekaisheng.courier.core.database.entity.PushMessage;
import com.dekaisheng.courier.core.webapi.OnCallback;
import com.dekaisheng.courier.core.webapi.bean.ResponseBean;
import com.dekaisheng.courier.core.webapi.bean.UnFinishedOrderForm;
import com.dekaisheng.courier.core.webapi.bean.User;
import com.dekaisheng.courier.core.webapi.service.OrderService;
import com.dekaisheng.courier.core.webapi.service.OtherService;
import com.dekaisheng.courier.lbs.LocationService;
import com.dekaisheng.courier.lbs.MapUtil;
import com.dekaisheng.courier.lbs.MapUtil.AnimateConfig;
import com.dekaisheng.courier.lbs.MyLocationService;
import com.dekaisheng.courier.lbs.MyLocationService.MyBinder;
import com.dekaisheng.courier.lbs.activity.ActivityRoute;
import com.dekaisheng.courier.lbs.route.Mode;
import com.dekaisheng.courier.lbs.route.OnRouteResultCallback;
import com.dekaisheng.courier.lbs.route.Parameter;
import com.dekaisheng.courier.lbs.route.RoutePlanner;
import com.dekaisheng.courier.lbs.route.RouteResultStatus;
import com.dekaisheng.courier.lbs.route.Parameter.Builder;
import com.dekaisheng.courier.user.ActivityMessageCenter;
import com.dekaisheng.courier.util.DisplayUtil;
import com.dekaisheng.courier.util.JsonHelper;
import com.dekaisheng.courier.util.StringUtils;
import com.dekaisheng.courier.util.bmp.BitmapFile;
import com.dekaisheng.courier.util.bmp.BitmapHelper;
import com.dekaisheng.courier.util.log.Log;
import com.dekaisheng.courier.util.log.LoggerConfig;
import com.dekaisheng.courier.util.push.MessageKey;
import com.dekaisheng.courier.view.ViewPagerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.dekaisheng.courier.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class ActivityHomeNew extends BaseActivity implements
		OnMapReadyCallback, OnPageChangeListener, OnItemClickListener {

	private GoogleCloudMessaging gcm;

	@ViewInject(R.id.activity_home_new_txt_order_count)
	private TextView orderCountTxt;

	@ViewInject(R.id.activity_home_new_txt_msg_count)
	private TextView msgCountTxt;

	@ViewInject(R.id.activity_home_new_img_uer)
	private ImageView userImg;

	@ViewInject(R.id.activity_home_new_txt_user_name)
	private TextView userNameTxt;

	@ViewInject(R.id.activity_home_new_viewpager)
	private ViewPager viewPager;

	@ViewInject(R.id.activity_home_new_img_order_list_refresh)
	private ImageView refreshListImg;

	@ViewInject(R.id.activity_home_new_img_order_list)
	private ImageView orderListImg;

	@ViewInject(R.id.activity_home_new_img_location)
	private ImageView myLocationImg;

	@ViewInject(R.id.activity_home_new_drawerlayout)
	private android.support.v4.widget.DrawerLayout drawerLayout;

	// @ViewInject(R.id.fl_orders_menu)
	// private FrameLayout fl_orders_menu;
	@ViewInject(R.id.container)
	private FrameLayout container_menu;
	private MapFragment mapFragment;
	private GoogleMap googleMap;
	private ViewPagerAdapter adapter;
	private ArrayList<View> views;
	private ArrayList<UnFinishedOrderForm> unfinishedOrder = new ArrayList<>();
	private String selectedItem = "";

	private HashMap<String, Marker> markerMap;
	private Marker currentOrderMarker; // 当前选中的包裹的Marker
	private Mode mode = Mode.DRIVING;
	private final int padding = DisplayUtil.dip2px(100);

	private int currentOrder;

	public static final int SIGN_HAND_WRITE_REQUEST = 52; // 手写签收返回
	public static final int SIGN_UNABLE_REQUEST = 51; // 异常订单返回
	public static final int SIGN_TAKE_PHOTO_REQUEST = 50; // 拍照返回
	public static final int ORDER_LIST_REQUEST = 3; // 从订单列表中返回
	public static final int SCAN_REQUEST = 4; // 扫描返回
	public static final int CHANGE_PORTRAIT = 60; // 修改头像返回
	public static final String ORDER_EXTRA = "order";

	private long firstPress = -1; // 按两次退出应用程序
	// private boolean bind = false;
	private AlertView alertView;

	// private MyOrientationListener myOrientationListener;
	/**
	 * 判断是否需要从状态中恢复
	 */
	private boolean isLoadState = false;
	/**
	 * 需要等到googlemap实例化才能添加Marker等操作，所以， 如果google map初始化失败，则所有地图相关的功能都不调用
	 */
	private boolean isGoogleMapEnabled = false;

	/**
	 * 判断地图是否渲染完毕，如果未渲染完毕就移动地图中心到LatLngBound会报错
	 * 
	 */
	private boolean isGoogleMapLoaded = false;

	/**
	 * 判断定位是否显示到中心，因为只会在首次移动到中心，所以需要跟踪
	 */
	// private boolean isMyLocationCentered = false;

	// private Marker deviceLocationMarker;
	public static LatLng deviceLocation;// 设备的位置
	// private int deviceLocationMarkerResId = R.drawable.my_location_blue;
	// private Campass directionListener;
	// private MyLocationService locService;
	private MapUtil mu;
	private ServiceConnection connection = new ServiceConnection() {

		/**
		 * 在绑定的时候设置定位时间和空间间隔更加频繁，解绑时设置为默认，更优化
		 */
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			MyBinder mb = (MyBinder) service;
			mb.getService();
			// locService = mb.getService();
			// locService.setHandler(handler);
			// bind = true;
			Log.i(TAG, "ActivityHome已经绑定LocationService");
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.i(TAG, "ActivityHome已经解绑定LocationService");
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_home_new);
		ViewUtils.inject(this);
		initView();
		initData();
		isLoadState = rollbackState(savedInstanceState);
		initMap();
		// startService(new Intent(this.getApplicationContext(),
		// MyLocationService.class));
		checkRegister();
		openGPSd(this);
		// myOrientationListener = new
		// MyOrientationListener(ActivityHomeNew.this);
		// myOrientationListener
		// .setOnOrientationListener(new OnOrientationListener() {
		// @Override
		// public void OnOrientationChanged(float x) {
		// if (deviceLocationMarker != null) {
		// deviceLocationMarker.setRotation(x);
		// }
		// }
		// });
	}

	/**
	 * 打开gps
	 * 
	 * @param ac
	 */
	private boolean showOpenGps = false;

	public void openGPSd(final Activity ac) {
		LocationManager manager = (LocationManager) MyApplication
				.getApplication().getSystemService(Context.LOCATION_SERVICE);
		if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			return;
		} else {
			alertView = new AlertView("Open GPS",
					getString(R.string.dialog_tips_ask_open_gps), null,
					"Cancel", new String[] { "Ok" }, null, this,
					AlertView.Style.Alert, this).setCancelable(true);
			alertView.show();
			showOpenGps = true;
			// this.showQuestionDialog(R.string.dialog_tips_ask_open_gps,
			// new IDialogListener() {
			//
			// @Override
			// public void onOk() {
			// Intent i = new Intent(
			// Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			// ac.startActivityForResult(i, 0);
			// }
			//
			// @Override
			// public void onCancle() {
			// //
			// }
			//
			// });
		}

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		/**
		 * 保存订单数据和定位数据
		 */
		if (unfinishedOrder.size() < 1)
			return;
		outState.putSerializable("orders", unfinishedOrder);
		outState.putString("item",
				unfinishedOrder.get(viewPager.getCurrentItem()).order_no);
	}

	private boolean rollbackState(Bundle outState) {
		boolean result = false;
		if (outState == null) {
			return result;
		}
		this.selectedItem = outState.getString("item");
		Object obj = outState.getSerializable("orders");
		if (obj instanceof ArrayList) {
			@SuppressWarnings("unchecked")
			ArrayList<UnFinishedOrderForm> uf = (ArrayList<UnFinishedOrderForm>) obj;
			if (uf != null && uf.size() > 0) {
				unfinishedOrder.addAll(uf);
				Log.i(TAG,
						"从onSaveInstanceState中恢复unfinishedOrder成功：" + uf.size());
			}
		} else {
			Log.i(TAG, "从onSaveInstanceState中恢复失败");
		}
		if (unfinishedOrder.size() > 0) {
			result = true;
		}
		return result;
	}

	private void checkRegister() {
		Log.i(TAG, "Check if regist Push ID Begin");
		gcm = GoogleCloudMessaging.getInstance(getBaseContext());
		boolean firstLunch = SharePreferenceHelper.getBoolean(
				Constants.FIRST_LUNCH, true);
		if (firstLunch) {
			register();
		}
	}

	/**
	 * 注册gcm推送ID，注册后在本机记录已经注册，并保存返回来的push id，
	 * 然后需要上传的服务器，注册和保存到服务器只会执行成功一次，以后就不会再执行
	 */
	private void register() {
		new AsyncTask<Object, Object, Object>() {

			private boolean done = false;
			private String token;

			@Override
			protected void onPostExecute(Object result) {
				super.onPostExecute(result);
				if (done) {
					if (token != null && token.length() > 0) {
						SharePreferenceHelper.put(Constants.PUSH_REGISTER_ID,
								token);
						SharePreferenceHelper.put(Constants.FIRST_LUNCH, false);
						savePushId(token);
					}
					Log.i(TAG, "token:" + token);
					if (Config.DEBUG) {
						/**
						 * 上线后是不需要让用户知道后台的具体操作的，如果注册失败，
						 * 则在下次启动ActivityHomeNew时会再次注册.
						 */
						showTipsDialog(token);
					}
				} else {
					if (Config.DEBUG) {
						// 注意这里的token是在catch里面赋值的，是错误信息不是真正的token
						showTipsDialog("注册Push ID 失败！" + token);
					}
					Log.i(TAG, "注册Push ID 失败！" + token);
				}
			}

			@Override
			protected Object doInBackground(final Object... params) {
				Log.i(TAG, "开始注册Push ID");
				try {
					token = gcm.register(getString(R.string.project_number));
					Log.i(TAG, "Register PushID Successfully!");
					done = true;
				} catch (IOException e) {
					token = e.getMessage();
					Log.i(TAG, "Register PushID ERROR:" + e.getMessage());
				}
				return true;
			}
		}.execute(null, null, null);
	}

	private void initData() {
		this.markerMap = new HashMap<String, Marker>();
		User u = getUser();
		if (u != null) {
			String token = SharePreferenceHelper.getString(
					Constants.PUSH_REGISTER_ID, "");
			savePushId(token);
		}
		bindService(new Intent(this, MyLocationService.class), connection,
				Context.BIND_AUTO_CREATE);
		// directionListener = new Campass(this, this.handler);
		// if (!directionListener.intSensor()) {
		// // 如果没有重力传感器和磁场传感器则不用显示箭头图标，改为圆形图标
		// deviceLocationMarkerResId = R.drawable.my_location_blue_circle;
		// if (LoggerConfig.DEBUG) {
		// Log.i(TAG, "您的设备没有重力传感器或者磁场传感器");
		// showTipsDialog("Your device no gravity sensors or magnetic field sensor");
		// }
		// }
	}

	/**
	 * 将gcm的push ID提交到服务器，只提交一次即可。
	 * 
	 * @param regId
	 */
	private void savePushId(final String regId) {
		if (SharePreferenceHelper.getBoolean(Constants.SAVE_PUSH_ID_TO_SERVER,
				false)) {
			Log.i(TAG, "Aleardy push the gcm id to server.");
			return;
		}
		if (StringUtils.isEmpty(regId)) {
			Log.i(TAG, "The gcm push id is null.");
			return;
		}
		OtherService os = new OtherService();
		User u = Cache.getInstance().getUser();
		if (u == null) {
			Log.i(TAG, "Not login yet,so can not save regid to server!");
			return;
		}
		os.savePushId(u.getToken(), u.getUid(), regId, "1", new OnCallback() {

			@Override
			public void onCompleted(ResponseBean<?> rb) {
				if (rb == null) {
					return;
				}
				if (rb.getCode() == ApiResultCode.SUCCESS_1001) {
					SharePreferenceHelper.put(Constants.SAVE_PUSH_ID_TO_SERVER,
							true);
					Log.i(TAG, "Save register id to server successfully!");
				} else {
					Log.i(TAG, "Save register id to server failed!");
				}
			}

			@Override
			public void onFailure(String msg) {
				Log.i(TAG,
						"Can not save register id to server!savePushId() failed");
			}

		});
	}

	@SuppressWarnings("deprecation")
	private void initView() {
		viewPager.setPageMargin(0 - DisplayUtil.dip2px(40));
		views = new ArrayList<View>();
		adapter = new ViewPagerAdapter(views);
		viewPager.setAdapter(adapter);
		viewPager.setOnPageChangeListener(this);
		// 地图上的操作菜单
		PromotedActions promotedActionsLibrary = new PromotedActions();

		promotedActionsLibrary.setup(getApplicationContext(), container_menu);

		promotedActionsLibrary.addItem(
				getResources().getDrawable(R.drawable.selector_location),
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						moveToMyLocation();
					}
				});
		promotedActionsLibrary.addItem(
				getResources().getDrawable(R.drawable.selector_home_reload),
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						refresh();
						mu.animateToBounds(markerMap, padding);
					}
				});
		promotedActionsLibrary
				.addItem(
						getResources().getDrawable(
								R.drawable.selector_home_order_list),
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								orderList();
							}
						});

		promotedActionsLibrary.addMainItem(getResources().getDrawable(
				R.drawable.ic_add));
	}

	/**
	 * 初始化地图。
	 */
	private void initMap() {
		int status = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getBaseContext());
		if (status == ConnectionResult.SUCCESS) {
			mapFragment = (MapFragment) getFragmentManager().findFragmentById(
					R.id.activity_home_new_google_map);
			if (mapFragment != null) {
				mapFragment.getMapAsync(this);
				googleMap = mapFragment.getMap();
			}
		} else {
			isGoogleMapEnabled = false;
			showTipsDialog(R.string.toast_tips_google_service_not_avaliable);
		}
		mu = new MapUtil(googleMap);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		if (googleMap != null) {
			googleMap.setMyLocationEnabled(true);
		}
		checkHaveNewMsg();
		// 是否有新的分配，有的话给提示。
		PushMessage pushMessage = new PushMessage();
		String newPushStr = SharePreferenceHelper.getString(
				Constants.HaveNewPush, "");
		if (!StringUtils.isEmpty(newPushStr)) {
			String[] str = newPushStr.split(",");
			pushMessage.title = str[0];
			pushMessage.message = str[1];
			onReceiveAssignmentResponse(pushMessage);
		}
		super.onResume();
	}

	@Override
	protected void onPause() {
		// myOrientationListener.stop();
		// if (bind) {
		// this.unbindService(connection);
		// bind = false;
		// Log.i(TAG, "unBindService bind = " + bind);
		// }
		if (googleMap != null) {
			googleMap.setMyLocationEnabled(false);
		}
		drawerLayout.closeDrawer(Gravity.LEFT);
		super.onPause();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (event.getRepeatCount() == 0) {
				if (alertView != null && alertView.isShowing()) {
					alertView.dismiss();
					return false;
				}
			}
			long current = new Date().getTime();
			if (current - firstPress < 3000) {
				AppManager.getActivityManager().exit();
				return true;
			} else {
				firstPress = current;
				this.showToastShort(R.string.toast_tips_ask_for_exit);
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case SIGN_TAKE_PHOTO_REQUEST:
			onPhotoSignResult(resultCode, data); // Sign For 拍照返回
			break;
		case SIGN_UNABLE_REQUEST: // Sign For 异常订单返回
			onAbnormalOrderRequest(resultCode, data);
			break;
		case SIGN_HAND_WRITE_REQUEST:
			onHandWrittenRequest(resultCode, data);
			break;
		case ORDER_LIST_REQUEST:
			onOrderListRequest(resultCode, data);
			break;
		// case SCAN_REQUEST:
		// onScanResult(resultCode, data);
		// break;
		default:
			break;
		}
	}

	// private void onScanResult(int resultCode, Intent data) {
	// if (data == null) {
	// return;
	// }
	// List<String> orderNo = data
	// .getStringArrayListExtra(CaptureActivity.ORDER_NO_LIST);
	// if (orderNo != null && orderNo.size() > 0) {
	// /**
	// * 重新拉取未完成订单，由于拉取未完成订单是一次性的，所以， 最好还是为本地的已经做过地理编码的做个缓存，避免二次地理编码
	// * 但是这样就会更复杂，暂时先不考虑
	// */
	// this.getUnfinishedOrder();
	// Log.i(TAG, "扫描结果为：" + orderNo.toString());
	// } else {
	// Log.i(TAG, "扫描结果为0个呀");
	// }
	// }

	private void onOrderListRequest(int resultCode, Intent data) {
		if (data == null) {
			return;
		}
		User u = Cache.getInstance().getUser();
		if (resultCode == RESULT_OK) {
			// 移除地图上已完成的包裹的mark。
			ArrayList<String> handleOrder = data
					.getStringArrayListExtra(getString(R.string.extra_effect_order));
			if (handleOrder != null && handleOrder.size() > 0) {
				for (String str : handleOrder) {
					Marker marker = markerMap.get(str);
					if (marker != null) {
						marker.remove();
						markerMap.remove(str);
					}
				}
				// 重新查询数据库中的数据，刷新pagerView
				UnFinishedOrderDao dao = new UnFinishedOrderDao();
				Selector s = Selector.from(UnFinishedOrderForm.class);
				s.where("uid", "=", u.getUid());
				List<UnFinishedOrderForm> list = dao.select(s);
				if (list != null && list.size() > 0) {
					createViewPagerViews(list, list.get(0).order_no);
				} else {
					// 没有数据，清空pagerview 和 marker.
					// fl_orders_menu.setVisibility(View.GONE);
					views.clear();
					adapter.notifyDataSetChanged();
					clearMarker();
					moveToMyLocation();
				}
			}
			// 下面是如果用户点击了订单，查看地图时，应该跳转到该订单
			UnFinishedOrderForm form = (UnFinishedOrderForm) data
					.getSerializableExtra(UnFinishedOrderAdapter.ORDER_EXTRA);
			if (form == null) {
				return;
			}
			if (unfinishedOrder != null && unfinishedOrder.size() > 0) {
				for (int i = 0; i < unfinishedOrder.size(); i++) {
					if (unfinishedOrder.get(i).order_no.equals(form.order_no)) {
						viewPager.setCurrentItem(i);
						break;
					}
				}
			}
		}
	}

	/**
	 * 客户签收接口
	 * 
	 * @param f
	 *            图片文件（压缩为360px最长边）
	 */
	private void signFor(final File f) {
		final UnFinishedOrderForm form = unfinishedOrder.get(currentOrder);
		if (form == null) {
			return;
		}
		User u = this.getUser();
		if (u == null) {
			return;
		}
		OrderService os = new OrderService();
		os.customsSign(u.getToken(), u.getUid(), form.order_no, f,
				new OnCallback() {

					@Override
					public void onStart() {
						// showWaitDialog();
					}

					@Override
					public void onCompleted(ResponseBean<?> rb) {
						// hideWaitDialog();
						deleteFile();
						if (rb == null) {
							return;
						}
						Log.e(TAG, "上传客户签收图片完成：" + rb.getMsg());
						if (rb.getCode() == ApiResultCode.SUCCESS_1001) {
							UnFinishedOrderDao dao = new UnFinishedOrderDao();
							dao.delete(form);
							showToastShort(R.string.toast_tips_sign_for_successful);
							deleteOrder(currentOrder);
							// hideWaitDialog(); //
							// 前面好像没有关闭掉这个对话框？是不是因为这时候对话框还没有弹出来？
						} else {
							showToastShort(R.string.toast_tips_sign_for_failed);
						}
					}

					@Override
					public void onFailure(String msge) {
						// hideWaitDialog();
						Log.e(TAG, "上传客户签收图片失败：" + msge);
						showToastShort(R.string.toast_tips_sign_for_failed);
						deleteFile();
					}

					private void deleteFile() {
						if (!LoggerConfig.DEBUG) {
							if (f != null) {
								f.delete();
							}
						}
					}
				});
	}

	private void onHandWrittenRequest(int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			String bmpFile = data
					.getStringExtra(ActivityHandwrittenSignFor.SING_FOR_PHOTO);
			Bitmap bmp = BitmapHelper.scaleToWidth(bmpFile, 720f);
			try {
				File f = BitmapFile.saveBitmapToFile(bmp, bmpFile);
				bmp.recycle();
				signFor(f);
			} catch (IOException e) {
				Log.e(TAG, "上传手写签收失败:" + e.getMessage());
				showToastLong(R.string.toast_tips_sign_for_failed);
				e.printStackTrace();
			}
		}
	}

	private void onAbnormalOrderRequest(int resultCode, Intent data) {
		if (RESULT_OK == resultCode) {
			// 提交异常报告成功后，删除记录
			deleteOrder(currentOrder);
		} else {
			Log.e(TAG, "提交异常订单失败");
		}
	}

	private void onPhotoSignResult(int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			showToastLong(R.string.toast_tips_sign_for_failed);
			return;
		}
		File f = null;
		Bitmap bmp = null;
		try {
			f = new File(SignForDialog.photoName);
			bmp = BitmapHelper.scaleToWidth(f.getAbsolutePath(), 360f);
			f = BitmapFile.saveBitmapToFile(bmp, f.getAbsolutePath());
			signFor(f);
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, "onPhotoSignResult" + e.getMessage());
			showToastLong(R.string.toast_tips_sign_for_failed);
		} finally {
			if (bmp != null) {
				bmp.recycle();
			}
		}

	}

	@Override
	public void onMapReady(GoogleMap map) {
		isGoogleMapEnabled = true; // 到了这里，google map才是可用的
		googleMap.setMyLocationEnabled(true);	
		googleMap
				.setOnMyLocationChangeListener(new OnMyLocationChangeListener() {
					@Override
					public void onMyLocationChange(Location location) {
						if (location != null) {
							double latitude = location.getLatitude();
							// Getting longitude of the current location
							double longitude = location.getLongitude();
							deviceLocation = new LatLng(latitude, longitude);
						}
					}

				});

		map.getUiSettings().setAllGesturesEnabled(true);
		map.getUiSettings().setRotateGesturesEnabled(false);
		map.getUiSettings().setMapToolbarEnabled(true); // 路径规划，Place API
		map.getUiSettings().setCompassEnabled(true);
		map.getUiSettings().setIndoorLevelPickerEnabled(true);
		map.getUiSettings().setMyLocationButtonEnabled(false);
		map.getUiSettings().setZoomControlsEnabled(false);
		// map.setInfoWindowAdapter(new CustomsInfoWindow(this));
		map.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker arg0) {
				arg0.hideInfoWindow();
				/**
				 * 点击Marker时显示InfoWindow，InfoWindow的title是收货人姓名和电话，
				 * InfoWindow的content是收货人的地址，左边图标点击则做路径规划（设备所 在位置到Marker位置）
				 */
				String json = arg0.getSnippet();
				UnFinishedOrderForm order = (UnFinishedOrderForm) new JsonHelper()
						.jsonToBean(json, UnFinishedOrderForm.class);
				if (order != null) {
					int index = getIndex(order.order_no);
					if (index > -1) {
						viewPager.setCurrentItem(index);
					}
				}
				return true;
			}

		});
		map.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public void onMapClick(LatLng arg0) {
				/**
				 * 点击地图时，显示ViewPager
				 */
				viewPager.setVisibility(View.VISIBLE);
			}

		});
		map.setOnMapLongClickListener(new OnMapLongClickListener() {

			@Override
			public void onMapLongClick(LatLng arg0) {
				mu.animateToBounds(markerMap, padding);
			}

		});

		map.setOnMapLoadedCallback(new OnMapLoadedCallback() {

			/**
			 * 地图渲染完成后，如果定位、订单、站点等有完成加载的，或者全部完成的则显示到地图中心，
			 * 如果地图先渲染完成，则后面会根据这个直接显示到中心 Called when the map has finished
			 * rendering. This will only be called once. You must request
			 * another callback if you want to be notified again.
			 */
			@Override
			public void onMapLoaded() {
				isGoogleMapLoaded = true;
				if (markerMap.size() > 0) {
					mu.animateToBounds(markerMap, padding);
				}
			}
		});
		User u = getUser();
		if (u == null)
			return;
		mu.addMarker(u.getLocation(), "", "", R.drawable.station_location_red);
		mu.zoomTo(
				u.getLocation(),
				getResources().getInteger(
						R.integer.google_map_default_zoomlevel));
		init();
	}

	/**
	 * 初始化Activity，必须等到知道地图可用/不可用了才初始化，初始化有三种可能：
	 * 1.前面启动过，从保存的数据中恢复(onSaveInstanceState中保存) 2.有本地数据，从本地数据读取 3.访问网络数据
	 */
	private void init() {
		/**
		 * 先判断能否从从之前的状态中恢复数据，如果可以就不要再次调用接口或者访问数据库
		 */
		if (isLoadState) {
			if (selectedItem.equals("")) {
				selectedItem = unfinishedOrder.get(0).order_no;
			}
			createViewPagerViews(unfinishedOrder, selectedItem);
			Log.i(TAG, "从onSaveInstanceState中恢复unfinishedOrder");
		} else {
			/*
			 * 先从本地数据库读取数据，如果有则不再访问服务器，如果没有， 可以访问一次服务器，后面则需要等到有刷新了才访问服务器
			 */
			User u = Cache.getInstance().getUser();
			if (u == null)
				return;
			UnFinishedOrderDao dao = new UnFinishedOrderDao();
			// 区分不同用户！！！
			Selector s = Selector.from(UnFinishedOrderForm.class);
			s.where("uid", "=", u.getUid());
			List<UnFinishedOrderForm> forms = dao.select(s);
			if (forms != null && forms.size() > 0) {
				createViewPagerViews(forms, forms.get(0).order_no);
				Log.i(TAG, "从数据库中恢复unfinishedOrder");
			} else {
				getUnfinishedOrder();
				Log.i(TAG, "从服务器中恢复unfinishedOrder");
			}
		}
	}

	@OnClick({ R.id.activity_home_new_img_order_list,
			R.id.activity_home_new_img_uer, R.id.activity_home_new_img_msg,
			R.id.activity_home_new_img_location,
			R.id.activity_home_new_img_order_list_refresh })
	public void uiClick(View v) {
		switch (v.getId()) {
		case R.id.activity_home_new_img_order_list:
			orderList();
			break;
		case R.id.activity_home_new_img_uer:
			userCenter();
			break;
		case R.id.activity_home_new_img_msg:
			message();
			break;
		// case R.id.activity_home_new_img_scan:
		// scan();
		// break;
		case R.id.activity_home_new_img_order_list_refresh:
			refresh();
			mu.animateToBounds(markerMap, padding);
			break;
		case R.id.activity_home_new_img_location:
			moveToMyLocation();
			break;
		}
	}

	private void refresh() {
		getUnfinishedOrder();
	}

	private void moveToMyLocation() {
	
		// // Getting LocationManager object from System Service
		// LOCATION_SERVICE
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		//
		// // Creating a criteria object to retrieve provider
		Criteria criteria = new Criteria();
		//
		// // Getting the name of the best provider
		String provider = locationManager.getBestProvider(criteria, true);
		//
		// // Getting Current Location
		Location location = locationManager.getLastKnownLocation(provider);
		//
		if (location != null) {
			// // Getting latitude of the current location
			double latitude = location.getLatitude();

			// Getting longitude of the current location
			double longitude = location.getLongitude();

			deviceLocation = new LatLng(latitude, longitude);
		} else {
			location = googleMap.getMyLocation();
			if (location != null) {
				double latitude = location.getLatitude();
				// Getting longitude of the current location
				double longitude = location.getLongitude();
				deviceLocation = new LatLng(latitude, longitude);
			}
		}
		if (deviceLocation != null) {
			mu.animateToCenter(deviceLocation, null);
		}
		else {
			showToastShort("Temporarily unable to locate");
		}
	}

	private void orderList() {
		Intent i = new Intent(this, ActivityHome.class);
		i.putExtra(getString(R.string.extra_unfinished_orders), unfinishedOrder);
		startActivityForResult(i, ORDER_LIST_REQUEST);
	}

	private void userCenter() {
		this.drawerLayout.openDrawer(Gravity.LEFT);
	}

	private void message() {
		Intent i = new Intent(this, ActivityMessageCenter.class);
		startActivity(i);
	}

	// private void scan() {
	// Intent i = new Intent(this, CaptureActivity.class);
	// startActivityForResult(i, SCAN_REQUEST);
	// }

	/**
	 * 获取未完成的订单。
	 */
	private void getUnfinishedOrder() {
		final User u = Cache.getInstance().getUser();
		if (u == null) {
			return;
		}
		OrderService os = new OrderService();
		os.unFinishedOrder(u.getToken(), u.getUid(), Mode.BICYCLING.getMode(),
				deviceLocation, new OnCallback() {

					@Override
					public void onStart() {
						// showWaitDialog();
						showProgressDialog(ActivityHomeNew.this, "refresh");
					}

					@SuppressWarnings("unchecked")
					@Override
					public void onCompleted(ResponseBean<?> rb) {
						// hideWaitDialog();
						dismissProgressDialog();
						if (rb == null) {
							showToastShort("Failed to get unFinshorders");
						}
						switch (rb.getCode()) {
						case ApiResultCode.SUCCESS_1001:
							unfinishedSuccess((List<UnFinishedOrderForm>) rb
									.getData());
							break;
						default:
							unfinishedFailed(rb.getMsg());
							break;
						}
					}

					@Override
					public void onFailure(String msg) {
						// hideWaitDialog();
						dismissProgressDialog();
						unfinishedFailed(msg);
					}

					private void unfinishedSuccess(
							List<UnFinishedOrderForm> list) {
						// 没有拿到数据了。。
						if (list == null || list.size() == 0) {
							// fl_orders_menu.setVisibility(View.GONE);
							// 清空底部显示的订单列表
							views.clear();
							adapter.notifyDataSetChanged();
							clearMarker();
							showToastShort("You don't have to send the parcel");
							return;
						}
						createViewPagerViews(list, list.get(0).order_no);
						/**
						 * 重新拉取数据，直接刷新数据库
						 */
						UnFinishedOrderDao dao = new UnFinishedOrderDao();
						dao.deleteAll();
						Iterator<UnFinishedOrderForm> it = list.iterator();
						UnFinishedOrderForm temp;
						while (it.hasNext()) {
							// 遍历订单，增加uid字段
							temp = it.next();
							temp.uid = u.getUid();
						}
						dao.insert(list);
						// hideWaitDialog();
					}

					private void unfinishedFailed(String obj) {
						showToastShort(obj);
						// hideWaitDialog();
					}

				});
	}

	/**
	 * 清除地图上的包裹mark
	 */
	private void clearMarker() {
		// 将地图上的包裹位置清除。
		if (markerMap.isEmpty()) {
			return;
		}
		Iterator<Map.Entry<String, Marker>> it = markerMap.entrySet()
				.iterator();
		while (it.hasNext()) {
			Map.Entry<String, Marker> entry = it.next();
			Marker marker = entry.getValue();
			// String keyStr = entry.getKey();
			// 站点和人的位置不清除。
			// if (keyStr.equals("station") || keyStr.equals("myLocation")) {
			// continue;
			// }
			marker.remove();
		}
		markerMap.clear();
	}

	/**
	 * 从服务器拉去到订单后，绑定到ViewPager中
	 * 
	 * @param list
	 */
	@SuppressLint("InflateParams")
	private void createViewPagerViews(List<UnFinishedOrderForm> list,
			String orderNo) {
		// fl_orders_menu.setVisibility(View.VISIBLE);
		orderCountTxt.setText(list.size() + "");
		unfinishedOrder.clear();
		unfinishedOrder.addAll(list);
		views.clear();
		clearMarker();
		Iterator<UnFinishedOrderForm> it = list.iterator();
		int index = 0;
		int j = 0;// 遍历list当中，哪一个位置是第一个。
		JsonHelper jh = new JsonHelper();
		while (it.hasNext()) {
			final UnFinishedOrderForm temp = it.next();
			if (temp.order_no.equals(orderNo)) {
				j = index;
			}
			index++;
			View view = getLayoutInflater().inflate(
					R.layout.viewpager_item_home, null);
			TextView name = (TextView) view
					.findViewById(R.id.viewpager_item_home_order_name);
			name.setText(temp.recipient_name);
			ImageView phoneBtn = (ImageView) view.findViewById(R.id.phone_btn);
			ImageView msgBtn = (ImageView) view.findViewById(R.id.msg_btn);
			phoneBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
							+ temp.recipient_phone_number));
					if (i.resolveActivity(getPackageManager()) == null) {
						return;
					}
					startActivity(i);
				}
			});
			msgBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Uri smsToUri = Uri.parse("smsto:"
							+ temp.recipient_phone_number);
					Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
					if (intent.resolveActivity(getPackageManager()) == null) {
						return;
					}
					startActivity(intent);
				}
			});
			TextView address = (TextView) view
					.findViewById(R.id.viewpager_item_home_order_txt_address);
			address.setText(temp.address);
			TextView time = (TextView) view
					.findViewById(R.id.viewpager_tv_time);
			time.setText(temp.expected_time);
			ImageView goRoute = (ImageView) view
					.findViewById(R.id.viewpager_item_home_order_img_go);
			goRoute.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (!isGoogleMapEnabled) {
						return;
					}
					if (deviceLocation == null) {
						showToastShort(R.string.toast_tips_can_not_get_user_location);
						return;
					}
					if (currentOrderMarker == null) {
						showToastShort(R.string.toast_tips_can_not_code_the_pck);
						return;
					}
					route(deviceLocation, currentOrderMarker.getPosition(),
							mode);
				}

			});
			ImageView sign = (ImageView) view
					.findViewById(R.id.viewpager_item_home__order_img_sign);
			sign.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					final SignForDialog sd = new SignForDialog();
					sd.show(getFragmentManager(), "");
					sd.setListener(new ISignForListener() {

						@Override
						public void onTakePhoto() {
							// do nothing
						}

						@Override
						public void onHandWritten() {
							Intent i = new Intent(ActivityHomeNew.this,
									ActivityHandwrittenSignFor.class);
							i.putExtra(ORDER_EXTRA, temp);
							startActivityForResult(i,
									ActivityHomeNew.SIGN_HAND_WRITE_REQUEST);
							sd.dismiss();
						}

						@Override
						public void onUnFinished() {
							Intent i = new Intent(ActivityHomeNew.this,
									ActivityAbnormalOrder.class);
							i.putExtra(ORDER_EXTRA, temp); // 将点击的未完成订单传过去
							startActivityForResult(i,
									ActivityHomeNew.SIGN_UNABLE_REQUEST);
							sd.dismiss();
						}
					});
				}
			});
			views.add(view);
			adapter.notifyDataSetChanged();
			// 添加Mark到地图上。
			String json = jh.toJsonString(temp);
			LatLng ll = new LatLng(temp.lt, temp.lg);
			Marker marker = mu.addTextMarker(ll, R.drawable.package_black, ""
					+ index, temp.order_no, json);
			markerMap.put(temp.order_no, marker);
		}
		viewPager.setCurrentItem(j);
		// 需要等到地图渲染完成才能进行定位到LatLngBound
		if (isGoogleMapLoaded) {
			mu.animateToBounds(markerMap, padding);
		}
		onCurrentOrder(orderNo, false);
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LocationService.MSG_CODE:
				// handleLocation(msg.obj);
				break;
			// case Campass.MSG_CODE_SENSOR:
			// handleDirection(msg.obj);
			// break;
			// default:
			// break;
			}
		}

		// private float oldDir = 0; // 上一次记录到的角度
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

		// private void handleLocation(Object obj) {
		// /*
		// * 接收到派送员位置，在这里，可以设置定位的时间和距离间隔（onBind的时候就应该设置），
		// * 然后转为导航，退出导航模式后再转换为后台模式（时间和距离间隔较大）
		// */
		// if (obj == null || !isGoogleMapEnabled) {
		// return;
		// }
		// Location location = null;
		// if (obj instanceof Location) {
		// location = (Location) obj;
		// deviceLocation = new LatLng(location.getLatitude(),
		// location.getLongitude());
		// if (deviceLocationMarker == null) {
		// deviceLocationMarker = mu.addMarker(deviceLocation,
		// "Location", "My Location",
		// deviceLocationMarkerResId);
		// mu.animateToBounds(markerMap, padding);
		// }
		// deviceLocationMarker.setPosition(deviceLocation);
		// }
		// }
	};

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int position) {
		// 显示哪一个页面就先检查是否已经做了地理编码，是则显示位置，否则进行地理编码并显示位置
		UnFinishedOrderForm form = unfinishedOrder.get(position);
		currentOrder = position;
		onCurrentOrder(form.order_no, true);
	}

	/**
	 * 左右滑动选择订单时，改变地图中心到订单所在的Marker， 并更换Marker的图标为红色图标，再显示InfoWindow。
	 * 同时将上一个选择的Marker的图标换回
	 * 
	 * @param orderNo
	 * @param isPageChange
	 *            如果不是滑动来确定当前marker而是从订单列表过来，有可能当前的marker已经被删除了导致报错。
	 */
	private void onCurrentOrder(String orderNo, boolean isPageChange) {
		if (!isGoogleMapEnabled) {
			return;
		}
		Marker mk = markerMap.get(orderNo);
		if (mk != null) {
			if (isPageChange) {
				if (currentOrderMarker != null) {
					if (!currentOrderMarker.getTitle().equals(mk.getTitle())) {
						currentOrderMarker.setIcon(getBmpDesc(
								R.drawable.package_black,
								currentOrderMarker.getTitle()));
					}
				}
			}
			// 改为红色mark
			CameraPosition cp = googleMap.getCameraPosition();
			AnimateConfig config = new AnimateConfig((int) (cp.zoom + 0.5),
					cp.bearing, cp.tilt);
			mk.setIcon(getBmpDesc(R.drawable.package_pink, mk.getTitle()));
			mu.animateToCenter(mk.getPosition(), config);
			currentOrderMarker = mk;
		}
	}

	/**
	 * 生成一个带文字的可用于显示为Marker的图标的实例
	 * 
	 * @param resId
	 * @param text
	 * @param x
	 * @param y
	 * @return
	 */
	private BitmapDescriptor getBmpDesc(int resId, String text) {
		Bitmap bmp = mu.bmpWithTxt(resId, text);
		return BitmapDescriptorFactory.fromBitmap(bmp);
	}

	/**
	 * 签收或者报告异常订单，成功后都要删除本地的订单
	 * 
	 * @param position
	 */
	private void deleteOrder(int position) {
		unfinishedOrder.remove(position);
		orderCountTxt.setText(unfinishedOrder.size() + "");
		// 将Mark隐藏掉，并从markMap中移除。
		if (currentOrderMarker != null) {
			currentOrderMarker.remove();
			markerMap.remove(currentOrderMarker);
		}
		if (unfinishedOrder.size() > 0) {
			if (position == 0) {
				currentOrder = position;
			} else {
				currentOrder = position - 1;
				// 当前的Mark切换到已删除的前面。
				currentOrderMarker = markerMap.get(unfinishedOrder
						.get(currentOrder).order_no);
			}
		}
		// 页面刷新
		views.remove(position);
		adapter.notifyDataSetChanged();
		viewPager.setCurrentItem(currentOrder);
		// 焦点聚集到下一个。
		if (unfinishedOrder.size() > 0) {
			onCurrentOrder(unfinishedOrder.get(currentOrder).order_no, false);
		}
	}

	@Override
	public void onReceivePushMessage(PushMessage msg) {
		super.onReceivePushMessage(msg);
		if (msg == null) {
			return;
		}
		String key = msg.key;
		if (key == null) {
			return;
		}
		if (key.equals(MessageKey.COMMON_MESSAGE.getVal())) {
			checkHaveNewMsg();
		} else if (key.equals(MessageKey.ASSIGNMENT_RESPONSE.getVal())) {
			onReceiveAssignmentResponse(msg);
		}
	}

	/**
	 * 分配订单通知
	 * 
	 * @param msg
	 */
	private void onReceiveAssignmentResponse(PushMessage msg) {
		alertView = new AlertView(msg.title, msg.message, null, "Cancel",
				new String[] { "Ok" }, null, this, AlertView.Style.Alert, this)
				.setCancelable(true);
		alertView.show();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}

	@Override
	public void onItemClick(Object o, int position) {
		if (position == 0) {
			// 确认
			if (showOpenGps) {
				Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivityForResult(i, 0);
				showOpenGps = false;
			} else {
				assignmentResponse();
			}
		}
		// 当点击alert时，清空数据。
		SharePreferenceHelper.put(Constants.HaveNewPush, "");
	}

	/**
	 * 响应接收分配订单
	 */
	private void assignmentResponse() {
		User u = this.getUser();
		if (u == null) {
			return;
		}
		OrderService os = new OrderService();
		os.assignmentResponse(u.getToken(), u.getUid(), new OnCallback() {

			@Override
			public void onStart() {
				Log.i(TAG, "响应接收开始..");
			}

			@Override
			public void onCompleted(ResponseBean<?> rb) {
				Log.i(TAG, "响应接收完成");
				// 响应就要刷新一下订单。
				refresh();
				alertView.dismiss();
			}

			@Override
			public void onFailure(String msg) {
				Log.e(TAG, "异常订单提交失败：" + msg);
				showToastShort(msg);
				alertView.dismiss();
			}
		});
	}

	/**
	 * 检查是否有新的消息
	 */
	private void checkHaveNewMsg() {
		// 查询是否有新的信息
		boolean haveMessage = PushMessageDao.getInstance().isHaveReadMessage();
		if (haveMessage) {
			msgCountTxt.setVisibility(View.VISIBLE);
		} else {
			msgCountTxt.setVisibility(View.GONE);
		}
	}

	/**
	 * 收到推送消息，此时刚好在当前Activity，则从列表里删除被抢走的订单，并弹框提示。
	 * 
	 * @param msg
	 * 
	 *            private void onReceiveOrderMsg(PushMessage msg) { if (msg ==
	 *            null || unfinishedOrder == null) { return; } for (int i = 0; i
	 *            < unfinishedOrder.size(); i++) { if
	 *            (unfinishedOrder.get(i).order_no.equals(msg.order_no)) {
	 *            deleteOrder(i);
	 *            showTipsDialog(getString(R.string.dialog_tips_order_been_taken
	 *            )); break; } } }
	 */

	/**
	 * 通过订单号查该订单在unfinishedOrder数组中的位置
	 * 
	 * @param orderNo
	 * @return
	 */
	private int getIndex(String orderNo) {
		UnFinishedOrderForm temp;
		for (int i = 0; i < unfinishedOrder.size(); i++) {
			temp = unfinishedOrder.get(i);
			if (temp.order_no.equals(orderNo)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 计算派送员到订单的路径
	 * 
	 * @param start
	 * @param end
	 * @param m
	 */
	private void route(LatLng start, LatLng end, Mode m) {
		if (start == null || end == null) {
			return;
		}
		RoutePlanner planner = new RoutePlanner();
		Builder b = new Builder(start, end);
		b.mode(m);
		Parameter params = b.build();
		planner.route(params, new OnRouteResultCallback() {

			@Override
			public void onStart() {
				showProgressDialog(ActivityHomeNew.this,
						"In the route planning...");
			}

			@Override
			public void onCompleted(
					com.dekaisheng.courier.lbs.route.result.ResponseBean rb) {
				dismissProgressDialog();
				if (RouteResultStatus.OK.getVal().equals(rb.status)) {
					ActivityRoute.routeBean = rb;
					Intent i = new Intent(ActivityHomeNew.this,
							ActivityRoute.class);
					i.putExtra(ActivityRoute.START_LAT, deviceLocation.latitude);
					i.putExtra(ActivityRoute.START_LNG,
							deviceLocation.longitude);
					i.putExtra(ActivityRoute.END_LAT,
							currentOrderMarker.getPosition().latitude);
					i.putExtra(ActivityRoute.END_LNG,
							currentOrderMarker.getPosition().longitude);
					i.putExtra(ActivityRoute.MODE, mode);
					i.putExtra(ActivityRoute.START_ADDRESS,
							getString(R.string.activity_home_new_start_address));
					i.putExtra(ActivityRoute.END_ADDRESS,
							unfinishedOrder.get(currentOrder).address);
					startActivity(i);
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
				if (LoggerConfig.DEBUG) {
					showToastShort(msg);
				} else {
					showToastShort(R.string.toast_tips_route_not_found);
				}
			}
		});
	}

	// Marker mPositionMarker;

	//
	// @Override
	// public void onLocationChanged(Location location) {
	// if (location == null || !isGoogleMapEnabled)
	// return;
	//
	// if (mPositionMarker == null) {
	//
	// mPositionMarker = googleMap.addMarker(new MarkerOptions()
	// .flat(true)
	// .icon(BitmapDescriptorFactory
	// .fromResource(deviceLocationMarkerResId))
	// .anchor(0.5f, 0.5f)
	// .position(
	// new LatLng(location.getLatitude(), location
	// .getLongitude())));
	// }
	// deviceLocation = new LatLng(location.getLatitude(),
	// location.getLongitude());
	// animateMarker(mPositionMarker, location);
	// googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(
	// location.getLatitude(), location.getLongitude())));
	//
	// }
	//
	// public void animateMarker(final Marker marker, final Location location) {
	// final Handler handler = new Handler();
	// final long start = SystemClock.uptimeMillis();
	// final LatLng startLatLng = marker.getPosition();
	// final double startRotation = marker.getRotation();
	// final long duration = 500;
	//
	// final Interpolator interpolator = new LinearInterpolator();
	//
	// handler.post(new Runnable() {
	// @Override
	// public void run() {
	// long elapsed = SystemClock.uptimeMillis() - start;
	// float t = interpolator.getInterpolation((float) elapsed
	// / duration);
	//
	// double lng = t * location.getLongitude() + (1 - t)
	// * startLatLng.longitude;
	// double lat = t * location.getLatitude() + (1 - t)
	// * startLatLng.latitude;
	//
	// float rotation = (float) (t * location.getBearing() + (1 - t)
	// * startRotation);
	//
	// marker.setPosition(new LatLng(lat, lng));
	// marker.setRotation(rotation);
	//
	// if (t < 1.0) {
	// // Post again 16ms later.
	// handler.postDelayed(this, 16);
	// }
	// }
	// });
	// }
}
