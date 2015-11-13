package com.dekaisheng.courier.xzing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.SurfaceHolder.Callback;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.dekaisheng.courier.R;
import com.dekaisheng.courier.activity.BaseActivity;
import com.dekaisheng.courier.cache.ApiResultCode;
import com.dekaisheng.courier.cache.Cache;
import com.dekaisheng.courier.core.webapi.OnCallback;
import com.dekaisheng.courier.core.webapi.bean.ResponseBean;
import com.dekaisheng.courier.core.webapi.bean.User;
import com.dekaisheng.courier.core.webapi.service.OrderService;
import com.dekaisheng.courier.util.StringUtils;
import com.dekaisheng.courier.util.UIUtils;
import com.dekaisheng.courier.util.log.Log;
import com.dekaisheng.courier.xzing.camera.CameraManager;
import com.dekaisheng.courier.xzing.decoding.CaptureActivityHandler;
import com.dekaisheng.courier.xzing.decoding.InactivityTimer;
import com.dekaisheng.courier.xzing.view.ViewfinderView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class CaptureActivity extends BaseActivity implements Callback {

	@ViewInject(R.id.viewfinder_view)
	private ViewfinderView viewfinderView;
	private CaptureActivityHandler handler;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private boolean vibrate;
	private static final float BEEP_VOLUME = 0.10f;
	@ViewInject(R.id.preview_view)
	private SurfaceView surfaceView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	@ViewInject(R.id.listView)
	private ListView mListView;
	@ViewInject(R.id.tv_processing)
	private TextView processingTxt;
	private ArrayAdapter<String> arrayAdapter;
	private ArrayList<String> arrayList;
	@ViewInject(R.id.tv_title)
	private TextView title;
	@ViewInject(R.id.tv_order_count)
	private TextView orderCountTxt;
	@ViewInject(R.id.right_btn)
	private ImageView rightBtn;
	public static final String ORDER_NO_LIST = "order";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_capture);
		init();
	}

	protected void init() {
		ViewUtils.inject(this);
		rightBtn.setImageDrawable(UIUtils
				.getDrawable(R.drawable.edit_btn_selector));
		CameraManager.init(getApplication());
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);

		arrayList = new ArrayList<String>();
		arrayAdapter = new ArrayAdapter<String>(
				CaptureActivity.this,
				R.layout.scan_package_order_item,
				R.id.tv_order_no, arrayList);
		mListView.setAdapter(arrayAdapter);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			goback();
			this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}


	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats,
					characterSet);
		}
	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();
	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	/**
	 * 处理扫描结果
	 * 
	 * @param result
	 * @param barcode
	 */
	public void handleDecode(Result result, Bitmap barcode) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		String resultString = result.getText();
		if(resultString == null || resultString.equals("")){
			scanOver();
		}else{
			if(!checkCodeExit(resultString)){
				takeOrder(resultString);
			}else{
				showToastShort(R.string.toast_tips_order_already_exist);
			}
			scanOver();
		}
	}

	/**
	 * 检查订单是否已经扫描过，在列表中，防止扫描过快，多次提交（缓存问题）
	 * @param code
	 * @return true表示已经扫描并成功，false反之。
	 */
	private boolean checkCodeExit(String code){
		Iterator<String> it = this.arrayList.iterator();
		String temp;
		boolean check = false;
		while(it.hasNext()){
			temp = it.next();
			if(temp.equals(code)){
				check = true;
				break;
			}
		}
		return check;
	}

	/**
	 * 扫描后提交订单号到后台，获取订单派送权
	 */
	private void takeOrder(final String ordeNo) {
		if(StringUtils.isEmpty(ordeNo)){
			return;
		}
		User user = Cache.getInstance().getUser();
		if(user == null){
			scanOver();
			return;
		}
		OrderService os = new OrderService();
		os.takeOrder(user.getUid()
				, user.getToken(), ordeNo, new OnCallback(){

			@Override
			public void onStart(){
				Log.i(TAG, "扫描订单onStart，确定不要等待框？");
			}

			@Override
			public void onCompleted(ResponseBean<?> rb) {
				if(rb == null){
					return;
				}
				switch(rb.getCode()){
				case ApiResultCode.SUCCESS_1001:
					success(ordeNo);
					break;
				case ApiResultCode.TIME_OUT_1005:
					timeOut(rb.getMsg());
					break;
				case ApiResultCode.NOT_FOUND_1002:
					orderNotExit(rb.getMsg());
					break;
				case ApiResultCode.HAD_SCAN_1023:
					hadScan(rb.getMsg());
					break;
				case ApiResultCode.ORDER_HAD_BEEN_SIGN_1026:
					showToastShort(rb.getMsg() + "");
					break;
				default:
					showToastShort(rb.getMsg() + "");
					break;
				}
			}

			/**
			 * 设置为同步方法，必须等到处理完成一个返回扫描结果才处理下一个，防止多次添加到列表
			 * @param orderNo
			 */
			private synchronized void success(String orderNo) {
				Log.i("订单号", ordeNo);
				title.setText(orderNo);
				if(!checkCodeExit(ordeNo)){
					arrayList.add(orderNo);
				}else{
					showToastShort(R.string.toast_tips_order_already_exist);
				}
				arrayAdapter.notifyDataSetChanged();
			}

			private void timeOut(String msg) {
				Log.i("TimeOut", msg);
				showToastShort(msg);
			}

			private void orderNotExit(String msg) {
				Log.i("NotExit", msg);
				showToastShort(msg);
			}

			private void hadScan(String msg) {
				Log.i("HadScan", msg);
				showToastShort(msg);
			}

			@Override
			public void onFailure(String msg) {
				Log.i("scan onFailure", msg);
				showToastShort(msg + "");
			}

		});
	}

	/**
	 * 扫描完成
	 */
	private void scanOver() {
		if (handler != null) {
			SystemClock.sleep(1000);
			Message msg = new Message();
			msg.what = R.id.restart_preview;
			handler.sendMessage(msg);
		}
		processingTxt.setVisibility(View.GONE);
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		hasSurface = false;
	}

	@OnClick({ R.id.back_btn, R.id.right_btn })
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.back_btn:
			goback();
			break;
		case R.id.right_btn:
			myAlertDialog();
			break;
		}
	}

	/**
	 * 设置返回，将成功扫描并提交到服务器的订单的订单号返回给调用的Activity
	 */
	private void goback(){
		Intent i = new Intent();
		i.putExtra(ORDER_NO_LIST, this.arrayList);
		this.setResult(RESULT_OK, i);
		finish();
	}

	private AlertDialog myDialog = null;

	private void myAlertDialog() {
		myDialog = new AlertDialog.Builder(CaptureActivity.this).create();
		myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		myDialog.show();
		myDialog.getWindow().setContentView(R.layout.alert_input_dialog);

		myDialog.getWindow().clearFlags(
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		myDialog.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		final EditText editText = (EditText) myDialog.getWindow().findViewById(
				R.id.et_code);
		editText.setOnEditorActionListener(new OnEditorActionListener(){

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_NEXT){
					String codeString = editText.getText().toString();
					if (StringUtils.isEmpty(codeString)) {
						showToastShort(R.string.toast_tips_scan_order_code_null);
						return true;
					}
					takeOrder(codeString);
					myDialog.dismiss();
					return true;
				}
				return false;
			}
			
		});

		myDialog.getWindow().findViewById(R.id.ok)
		.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String codeString = editText.getText().toString();
				if (StringUtils.isEmpty(codeString)) {
					showToastShort(R.string.toast_tips_scan_order_code_null);
					return;
				}
				takeOrder(codeString);
				myDialog.dismiss();
			}
		});
		myDialog.getWindow().findViewById(R.id.cancel)
		.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				myDialog.dismiss();
			}
		});
	}
}
