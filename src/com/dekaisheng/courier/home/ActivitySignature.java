package com.dekaisheng.courier.home;

import com.dekaisheng.courier.activity.BaseActivity;
import com.dekaisheng.courier.util.log.Log;
import com.dekaisheng.courier.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.webkit.WebSettings.LayoutAlgorithm;

/**
 * 查看签收图片，使用一个WebView来加载图片
 * @author Dorian
 *
 */
public class ActivitySignature extends BaseActivity{

	@ViewInject(R.id.activity_signature_webview)
	private WebView webview;

	@ViewInject(R.id.veiw_simple_custom_header_goback_2)
	private ImageView gobackImg;

	@ViewInject(R.id.veiw_simple_custom_header_title_2)
	private TextView titleTxt;

	private String url;

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_signature);
		ViewUtils.inject(this);
		initView();
		webviewSetting();
		initWebview();
	}

	private void initView() {
		this.titleTxt.setText(getString(R.string.activity_signature_title));
	}

	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
	private void webviewSetting(){
		this.webview.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NORMAL); 
		this.webview.getSettings().setJavaScriptEnabled(true);
		this.webview.getSettings().setDefaultTextEncodingName("utf-8");
		this.webview.getSettings().setUseWideViewPort(true);
		this.webview.getSettings().setSupportZoom(true);          
		this.webview.getSettings().setLoadWithOverviewMode(true); // 缩放到屏幕（宽度）显示全部
		this.webview.getSettings().setBuiltInZoomControls(true);  // 显示缩放按钮
		this.webview.setHorizontalScrollBarEnabled(false);
		this.webview.setBackgroundColor(0);
	}

	private void initWebview() {
		this.url = getIntent().getStringExtra(getString(R.string.extra_url));  
		Log.i(TAG, "签收图片证明：" + url);
		this.webview.loadData("<html>" 
				+"<head>"+
				"<style type='text/css'>" +
				"body{width:100%;height:100%;text-align:center;} " + 
				"img{position:relative;top:300px;margin:auto;} " +
				"</style>" +
				"</head>" + 
				"<body><img src='" + url + "'/></body>" + 
				"</html>" ,"text/html",  "UTF-8");
	}

	@OnClick({R.id.veiw_simple_custom_header_goback_2})
	public void onClick(View v){
		switch(v.getId()){
		case R.id.veiw_simple_custom_header_goback_2:
			this.finish();
			break;
		default:break;
		}
	}
}
