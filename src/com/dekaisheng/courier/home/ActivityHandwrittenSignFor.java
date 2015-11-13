package com.dekaisheng.courier.home;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import com.dekaisheng.courier.activity.BaseActivity;
import com.dekaisheng.courier.cache.FilePath;
import com.dekaisheng.courier.core.webapi.bean.UnFinishedOrderForm;
import com.dekaisheng.courier.util.DisplayUtil;
import com.dekaisheng.courier.util.bmp.BitmapFile;
import com.dekaisheng.courier.util.cache.ExternalOverFroyoUtils;
import com.dekaisheng.courier.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;

/**
 * 手写签收Activity，一个画图板。
 * 
 * @author Dorian
 * 
 */
public class ActivityHandwrittenSignFor extends BaseActivity {

	@ViewInject(R.id.activity_handwritten_sign_for_img_goback)
	private ImageView gobackImg;

	@ViewInject(R.id.activity_handwritten_sign_for_img_canvas)
	private ImageView img;

	@ViewInject(R.id.activity_handwritten_sign_for_clear)
	private ImageView clearImg;

	@ViewInject(R.id.activity_handwritten_sign_for_ok)
	private ImageView okImg;

	private Canvas canvas;
	private Bitmap bmp;

	private float startX;
	private float startY;
	private boolean init = true;
	private Paint paint;
	private UnFinishedOrderForm form;

	private boolean isWrite = false;// 用于判断是否有书写签名。

	/**
	 * 使用Intent通信时的一个key，用于保存手写签收的图片名字.
	 */
	public static final String SING_FOR_PHOTO = "signFor";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_handwritten_sign_for);
		ViewUtils.inject(this);
		form = (UnFinishedOrderForm) getIntent().getSerializableExtra(
				UnFinishedOrderAdapter.ORDER_EXTRA);
		TextView addressTxt=(TextView)findViewById(R.id.activity_handwritten_signfor_txt_address);
		addressTxt.setText(form.address);
		TextView titleTxt=(TextView)findViewById(R.id.activity_handwritten_sign_for_title);
		titleTxt.setText(form.order_no);
		TextView recipientNameTxt=(TextView)findViewById(R.id.activity_handwritten_signfor_txt_name);
		recipientNameTxt.setText(form.recipient_name);
	}

	private void initData() {		
		ViewTreeObserver vto = img.getViewTreeObserver();
		vto.addOnPreDrawListener(new OnPreDrawListener() {
			@Override
			public boolean onPreDraw() {
				if (init) {
					int h = img.getMeasuredHeight();
					int w = img.getMeasuredWidth();
					bmp = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
					canvas = new Canvas(bmp);
					paint = initPaing();
					canvas.drawColor(Color.argb(255, 222, 222, 222)); // 绘制背景
					img.setImageBitmap(bmp);
					init = false;
				}
				return true;
			}
		});
	}

	private Paint initPaing() {
		paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setStrokeWidth(DisplayUtil.dip2px(5));
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		return paint;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		initData();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			actionDown(event);
			break;
		case MotionEvent.ACTION_MOVE:
			actionMove(event);
			break;
		case MotionEvent.ACTION_UP:
			actionUp(event);
			break;
		}
		return super.onTouchEvent(event);
	}

	private void actionDown(MotionEvent event) {
		if (this.bmp == null || this.bmp.isRecycled()) {
			return;
		}
		this.startX = event.getX();
		this.startY = event.getY();
		this.canvas.drawPoint(this.startX, this.startY, paint);
		this.img.setImageBitmap(bmp);
		this.img.invalidate();
	}

	private void actionMove(MotionEvent event) {
		if (this.bmp == null || this.bmp.isRecycled()) {
			return;
		}
		Path p = new Path();
		p.moveTo(startX, startY);
		p.lineTo(event.getX(), event.getY());
		this.canvas.drawPath(p, paint);
		this.startX = event.getX();
		this.startY = event.getY();
		this.img.setImageBitmap(bmp);
		this.img.invalidate();
		isWrite = true;// 表示已经画了。
	}

	private void actionUp(MotionEvent event) {
		if (this.bmp == null || this.bmp.isRecycled()) {
			return;
		}
	}

	@OnClick({ R.id.activity_handwritten_sign_for_clear,
			R.id.activity_handwritten_sign_for_ok,
			R.id.activity_handwritten_sign_for_img_goback })
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.activity_handwritten_sign_for_clear:
			clear();
			break;
		case R.id.activity_handwritten_sign_for_ok:
			if (isWrite) {
				done();
			}
			break;
		case R.id.activity_handwritten_sign_for_img_goback:
			this.finish();
			break;
		}
	}

	private void clear() {
		canvas.drawColor(Color.argb(255, 222, 222, 222));
		img.setImageBitmap(bmp);
		isWrite=false;
	}

	private void done() {
		this.okImg.setBackgroundColor(Color.argb(200, 144, 160, 204));
		this.okImg.setEnabled(false);
		try {
			File file = ExternalOverFroyoUtils.getDiskCacheDir(this,
					FilePath.IMAGE);
			if (!file.exists()) {
				file.mkdir();
			}
			String photoName = new Date().getTime() + ".png";
			File pf = new File(file.getAbsoluteFile() + File.separator
					+ photoName);
			photoName = pf.getAbsolutePath();
			BitmapFile.saveBitmapToFile(this.bmp, photoName);
			this.bmp.recycle(); // 及时回收Bitmap
			Intent i = new Intent();
			i.putExtra(SING_FOR_PHOTO, photoName);
			setResult(RESULT_OK, i);
			finish();
		} catch (IOException e) {
			showToastLong(R.string.handwritten_save_bmp_failed_tips);
			e.printStackTrace();
			this.okImg.setBackgroundColor(Color.argb(0, 144, 160, 204));
			this.okImg.setEnabled(true);
		}
	}

}
