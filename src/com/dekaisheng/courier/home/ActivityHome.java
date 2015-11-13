package com.dekaisheng.courier.home;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.dekaisheng.courier.Config;
import com.dekaisheng.courier.activity.BaseActivity;
import com.dekaisheng.courier.activity.SignForDialog;
import com.dekaisheng.courier.cache.ApiResultCode;
import com.dekaisheng.courier.cache.Cache;
import com.dekaisheng.courier.core.database.dao.UnFinishedOrderDao;
import com.dekaisheng.courier.core.database.entity.PushMessage;
import com.dekaisheng.courier.core.webapi.OnCallback;
import com.dekaisheng.courier.core.webapi.bean.ResponseBean;
import com.dekaisheng.courier.core.webapi.bean.UnFinishedOrderForm;
import com.dekaisheng.courier.core.webapi.bean.User;
import com.dekaisheng.courier.core.webapi.service.OrderService;
import com.dekaisheng.courier.lbs.MyLocationService;
import com.dekaisheng.courier.lbs.route.Mode;
import com.dekaisheng.courier.util.bmp.BitmapFile;
import com.dekaisheng.courier.util.bmp.BitmapHelper;
import com.dekaisheng.courier.util.log.Log;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.dekaisheng.courier.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 未完成订单列表Activity
 * @author Dorian
 *
 */
public class ActivityHome extends BaseActivity implements OnItemClickListener{
	// 新的顶部
	@ViewInject(R.id.veiw_simple_custom_header_goback_2)
	private ImageView gobackImg;
	@ViewInject(R.id.veiw_simple_custom_header_title_2)
	private TextView titleTxt;
	// 新的顶部结束

	
	private PullToRefreshListView p2rListView;

	private ListView listView;

	private UnFinishedOrderAdapter unfinishedAdapter;
	private ArrayList<UnFinishedOrderForm> unfinishedOrder;

	private ArrayList<String> handleOrder;     // 处理了的订单数量
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_home);
		ViewUtils.inject(this);
		startService(new Intent(this.getApplicationContext(), 
				MyLocationService.class));
		initData();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		setResult();
		return true;
	}
	
	@SuppressWarnings("unchecked")
	private void initData(){
		handleOrder = new ArrayList<String>();
		titleTxt.setText(getString(R.string.activity_unfinished_order_list_title));
		unfinishedOrder = new ArrayList<UnFinishedOrderForm>();
		unfinishedAdapter = new UnFinishedOrderAdapter(this,unfinishedOrder);
		p2rListView = (PullToRefreshListView)this.findViewById(R.id.activity_home_p2r_listview);
		p2rListView.setOnRefreshListener(new OnRefreshListener<ListView>(){

			@Override
			public void onRefresh(PullToRefreshBase<ListView> arg0) {
				getUnfinishedOrder();
			}

		});
		p2rListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener(){

			@Override
			public void onLastItemVisible() {
				showToastShort(R.string.home_order_nomore_unfinished_order);
			}

		});
		this.listView = p2rListView.getRefreshableView();
		this.listView.setAdapter(unfinishedAdapter);
		this.listView.setOnItemClickListener(this);
		Object all = getIntent().getSerializableExtra(getString(R.string.extra_unfinished_orders));
		if(all != null && all instanceof ArrayList<?>){
			this.unfinishedOrder.addAll((ArrayList<UnFinishedOrderForm>)all);
			this.unfinishedAdapter.notifyDataSetChanged();
		}
//		}else{
//			getUnfinishedOrder(); // 如果从首页传过来的数据为null则执行一次查询
//		}
	}

	/**
	 * 查询未完成订单
	 */
	private void getUnfinishedOrder(){
		final User u = Cache.getInstance().getUser();
		if(u != null){
			OrderService os = new OrderService();
			os.unFinishedOrder(u.getToken(), u.getUid(), Mode.BICYCLING.getMode(), 
					ActivityHomeNew.deviceLocation,new OnCallback(){

				@Override
				public void onCompleted(ResponseBean<?> rb) {
					if(rb == null){
						return;
					}
					switch(rb.getCode()){
					case ApiResultCode.SUCCESS_1001:
						unfinishedSuccess(rb.getData());
						break;
					default:
						unfinishedFailed(rb.getMsg());
						break;
					}
				}

				@Override
				public void onFailure(String msg) {
					unfinishedFailed(msg);
				}

				private void unfinishedSuccess(Object obj) {
					try{
						@SuppressWarnings("unchecked")
						List<UnFinishedOrderForm> list = (List<UnFinishedOrderForm>)obj;
						unfinishedOrder.clear();
						unfinishedOrder.addAll(list);
						unfinishedAdapter.notifyDataSetChanged();
						p2rListView.onRefreshComplete();
						
						// 数据保存到数据库
						UnFinishedOrderDao dao = new UnFinishedOrderDao();
						dao.deleteAll(); // 查询后先清空数据库，连其他账号的也清空了。
						Iterator<UnFinishedOrderForm> it = list.iterator();
						UnFinishedOrderForm temp;
						while(it.hasNext()){
							// 遍历订单，增加uid字段
							temp = it.next();
							temp.uid = u.getUid();
						}
						dao.insert(list);
					}catch(Exception e){
						e.printStackTrace();
						p2rListView.onRefreshComplete();
					}
				}

				private void unfinishedFailed(String obj) {
					showToastShort(obj);
					p2rListView.onRefreshComplete();
				}

			});
		}
	}

	/**
	 * 客户签收接口
	 * @param f 图片文件（压缩为360最长边）
	 */
	private void signFor(final File f){
		UnFinishedOrderForm form = unfinishedOrder.get(SignForDialog.index);
		if(form == null){
			return;
		}
		User u = this.getUser();
		if(u != null){
			OrderService os = new OrderService();
			os.customsSign(u.getToken(), u.getUid(), form.order_no, 
					f, new OnCallback(){

				@Override
				public void onStart(){
					showProgressDialog(ActivityHome.this,"Processing");
				}
				
				@Override
				public void onCompleted(ResponseBean<?> rb) {
					dismissProgressDialog();
					deleteFile();
					if(rb.getCode() == ApiResultCode.SUCCESS_1001){
						showToastShort(R.string.toast_tips_signfor_successfully);
						deleteOrder();
					}else{
						showToastShort(R.string.toast_tips_signfor_failed);
						Log.e(TAG,"上传客户签收图片失败：" + rb.getMsg());
					}
				}

				@Override
				public void onFailure(String msge) {
					dismissProgressDialog();
					Log.e(TAG,"上传客户签收图片失败：" + msge);
					showToastShort(msge);
					deleteFile();
				}

				private void deleteFile(){
					if(!Config.DEBUG){
						if(f != null){
							f.delete();
						}	
					}
				}
			});
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode){
		case ActivityHomeNew.SIGN_TAKE_PHOTO_REQUEST:
			onPhotoSignResult(resultCode, data); // Sign For 拍照返回
			break;
		case ActivityHomeNew.SIGN_UNABLE_REQUEST: // Sign For 异常订单返回
			onAbnormalOrderRequest(resultCode, data);
			break;
		case ActivityHomeNew.SIGN_HAND_WRITE_REQUEST:
			onHandWrittenRequest(resultCode, data);
			break;
		default:break;
		}
	}

	private void onHandWrittenRequest(int resultCode, Intent data) {
		if(resultCode == RESULT_OK){
			String bmpFile = data.getStringExtra(ActivityHandwrittenSignFor.SING_FOR_PHOTO);
			Bitmap bmp = BitmapHelper.scaleToWidth(bmpFile,720f);
			try {
				File f = BitmapFile.saveBitmapToFile(bmp, bmpFile);
				bmp.recycle();
				signFor(f);
			} catch (IOException e) {
				Log.e(TAG, "上传手写签收失败:" + e.getMessage());
				e.printStackTrace();
			}
		}else{

		}
	}

	private void onAbnormalOrderRequest(int resultCode, Intent data) {
		if(RESULT_OK == resultCode){
			// 提交异常报告成功后，删除记录
			deleteOrder();
		}else{
			// ???
		}
	}

	private void onPhotoSignResult(int resultCode, Intent data) {
		if(resultCode != RESULT_OK){
			showToastLong(R.string.toast_tips_sign_for_failed);
			return;
		}
		File f = null;
		Bitmap bmp = null;
		try {
			f = new File(SignForDialog.photoName);
			bmp = BitmapHelper.scaleToWidth(f.getAbsolutePath(),360f);
			f = BitmapFile.saveBitmapToFile(bmp, f.getAbsolutePath());
			signFor(f);
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if(bmp != null){
				bmp.recycle();
			}
		}

	}

	@OnClick({R.id.veiw_simple_custom_header_goback_2})
	public void onClick(View v){
		int id = v.getId();
		switch(id){
		case R.id.veiw_simple_custom_header_goback_2:
			setResult();
			break;
		}
	}

//	private void scan() {
//		Intent i = new Intent(this,CaptureActivity.class);
//		startActivity(i);
//	}

	/**
	 * 处理推送通知
	 */
	@Override
	public void onReceivePushMessage(PushMessage msg) {
		super.onReceivePushMessage(msg);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		
	}

	private void deleteOrder(){
		UnFinishedOrderForm form = unfinishedOrder.remove(SignForDialog.index);
		handleOrder.add(form.order_no);
		unfinishedAdapter.notifyDataSetChanged();
		// 删除本地数据库的订单
		UnFinishedOrderDao dao = new UnFinishedOrderDao();
		dao.delete(form);
	}
	
	private void setResult(){
		Intent i = new Intent();
		i.putExtra(getString(R.string.extra_effect_order), handleOrder);
		setResult(Activity.RESULT_OK, i);
	    finish();
	}
}
