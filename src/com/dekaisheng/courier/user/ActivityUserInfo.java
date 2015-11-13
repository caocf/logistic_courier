package com.dekaisheng.courier.user;

import java.io.File;
import java.util.Date;

import com.dekaisheng.courier.activity.BaseActivity;
import com.dekaisheng.courier.cache.ApiResultCode;
import com.dekaisheng.courier.cache.Cache;
import com.dekaisheng.courier.cache.FilePath;
import com.dekaisheng.courier.core.database.DBManager;
import com.dekaisheng.courier.core.webapi.OnCallback;
import com.dekaisheng.courier.core.webapi.bean.Portrait;
import com.dekaisheng.courier.core.webapi.bean.ResponseBean;
import com.dekaisheng.courier.core.webapi.bean.User;
import com.dekaisheng.courier.core.webapi.bean.UserDetailInfo;
import com.dekaisheng.courier.core.webapi.service.AcountService;
import com.dekaisheng.courier.user.ChangePortraitDialog.IOnClickListener;
import com.dekaisheng.courier.util.bmp.ImageLoader;
import com.dekaisheng.courier.util.cache.ExternalOverFroyoUtils;
import com.dekaisheng.courier.util.log.Log;
import com.dekaisheng.courier.Config;
import com.dekaisheng.courier.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 用户资料Activity
 * @author Dorian
 *
 */
public class ActivityUserInfo extends BaseActivity{

	@ViewInject(R.id.veiw_simple_header_img_goback)
	private ImageView gobackImg;

	@ViewInject(R.id.veiw_simple_header_title)
	private TextView titleTxt;

	

	@ViewInject(R.id.activity_user_info_portrait)
	private ImageView portraitImg;

	@ViewInject(R.id.activity_user_info_name)
	private TextView userNameTxt;

	@ViewInject(R.id.activity_user_info_gender)
	private TextView genderTxt;

	@ViewInject(R.id.activity_user_info_telephone)
	private TextView telephoneTxt;

	@ViewInject(R.id.activity_user_info_address)
	private TextView addressTxt;

	@ViewInject(R.id.activity_user_info_customer_service_info)
	private TextView serviceInfoTxt;

	@ViewInject(R.id.activity_user_info_login_off)
	private Button loginOff;

	private String photoName = ""; // 拍照的名称
	private String cropName;  // 裁剪后的名称

	private boolean change = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_user_info);
		ViewUtils.inject(this);
		initData();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, 
			int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 0){
			request_0(resultCode, data);
		}else if(requestCode == 1){
			request_1(resultCode, data);
		}else if(requestCode == 2){
			request_2(resultCode, data);
		}
	}

	private void changePortrait(final File f){
		User u = getUser();
		AcountService as = new AcountService();
		as.changPortrait(u.getToken(), u.getUid(), f, new OnCallback(){

			@Override
			public void onStart(){
				showProgressDialog(ActivityUserInfo.this,"submitting");
			}

			@Override
			public void onCompleted(ResponseBean<?> resp) {
				dismissProgressDialog();
				if(resp == null){
					showToastLong(R.string.toast_tips_upload_portrait_failed);
					Log.e(TAG, "上传头像，ResponseBean<Portrait>为Null???");
				}else if(resp.getCode() == ApiResultCode.SUCCESS_1001){
					User u = getUser();
					Portrait por = (Portrait) resp.getData();
					u.setPortrait(por.url);
					try {
						DBManager.getDbUtils().saveOrUpdate(u);
					} catch (DbException e) {
						e.printStackTrace();
					}
					ImageLoader loader = new ImageLoader(ActivityUserInfo.this);
					loader.into(portraitImg, u.getPortrait(), -1, -1);
					change = true; // 设置为true这样返回上一个Activity时就知道头像已经改变，需要重新下载
				}else{
					showToastLong(resp.getMsg());
					Log.e(TAG, "上传头像失败，返回消息为：" + resp.getMsg());
				}
				deleteFile();
			} 

			@Override
			public void onFailure(String msge) {
				dismissProgressDialog();
				deleteFile();
				showToastLong(msge);
				Log.e(TAG, "上传头像失败，返回消息为：" + msge);
			}

			private void deleteFile(){
				// debug时不删除图片
				if(!Config.DEBUG){
					if(f != null){
						f.delete();
					}
					File f2 = new File(photoName);
					if(f2.exists()){
						f2.delete();
					}
				}
			}

		});
	}

	private void request_0(int resultCode, Intent data){
		// 拍照返回
		if(resultCode == RESULT_OK){
			startPhotoZoom(Uri.fromFile(new File(this.photoName)));
		}
	}

	private void request_1(int resultCode, Intent data){
		// 裁剪图片返回
		if(resultCode == RESULT_OK){
			File f = new File(cropName);
			changePortrait(f);
		}
	}

	private void request_2(int resultCode, Intent data){
		// 从DICM里面选择图片返回
		if(resultCode == RESULT_OK){
			Uri uri = data.getData();
			startPhotoZoom(uri);
		}
	}

	@OnClick({R.id.veiw_simple_header_img_goback
		
		, R.id.activity_user_info_portrait
		, R.id.activity_user_info_login_off})
	public void onClick(View v){
		switch(v.getId()){
		case R.id.veiw_simple_header_img_goback:
			goback();
			break;
		case R.id.activity_user_info_portrait:
			changePortrait();
			break;
		case R.id.activity_user_info_login_off:
			logOff();
			break;
		default:break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			goback();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void changePortrait() {

		final ChangePortraitDialog dialog = new ChangePortraitDialog();
		dialog.setClickListener(new IOnClickListener(){

			@Override
			public void onCamera() {
				dialog.dismiss();
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
				intent.addCategory("android.intent.category.DEFAULT");
				File file = ExternalOverFroyoUtils.getDiskCacheDir(
						ActivityUserInfo.this, FilePath.IMAGE);
				if(!file.exists()){
					file.mkdir();
				}
				photoName = new Date().getTime() + ".png";
				File bmp = new File(file.getAbsoluteFile() + File.separator + photoName);
				photoName = bmp.getAbsolutePath(); 
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(bmp)); 
				startActivityForResult(intent, 0); 
			}

			@Override
			public void onDicm() {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT); 
				intent.setType("image/*"); 
				startActivityForResult(intent,2);
				dialog.dismiss();

			}

			@Override
			public void onCancel() {
				dialog.dismiss();
			}

		});
		dialog.show(getFragmentManager(), TAG);
	}

//	private void message() {
//		Intent i = new Intent(this,ActivityMessageCenter.class);
//		startActivity(i);
//	}

	private void initData() {
		User u = Cache.getInstance().getUser();
		if(u != null){
			AcountService as = new AcountService();
			as.getUserDetailInfo(u.getToken(), u.getUid(), new OnCallback(){

				@Override 
				public void onStart(){
					showProgress(ActivityUserInfo.this);
				}

				@Override
				public void onCompleted(ResponseBean<?> rb) {
				dismissProgressDialog();
					if(rb == null){
						showToastShort(R.string.toast_tips_unknow_problem);
					}
					if(rb.getCode() != ApiResultCode.SUCCESS_1001){
						showToastShort(rb.getMsg());
					}else{
						Object obj = rb.getData();
						if(obj instanceof UserDetailInfo){
							UserDetailInfo ui = (UserDetailInfo)obj;
							userNameTxt.setText(ui.username);
							telephoneTxt.setText(ui.phone_number + "");
							addressTxt.setText(ui.address + "");
							serviceInfoTxt.setText(ui.customer_service_info + "");
							ImageLoader loader = new ImageLoader(ActivityUserInfo.this);
							loader.into(portraitImg, ui.portrait, -1, -1);
							/*设置用户性别*/
							String gender = getString(R.string.gender_unknow);
							if(ui.gender.equals("1")){
								gender = getString(R.string.gender_men);
							}else if(ui.gender.equals("2")){
								gender = getString(R.string.gender_women);
							}
							genderTxt.setText(gender);
						}else{
							showToastShort(R.string.toast_tips_unknow_problem);
						}
					}
				}

				@Override
				public void onFailure(String msg) {
					dismissProgressDialog();
					showToastShort(msg);
				}

			});
		}
	}

	private void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// crop为true是设置在开启的intent中设置显示的view可以剪裁
		intent.putExtra("crop", "true");

		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);

		// outputX,outputY 是剪裁图片的宽高
		intent.putExtra("outputX", 300);
		intent.putExtra("outputY", 300);
		intent.putExtra("scale", true);
		intent.putExtra("circleCrop", "");
		intent.putExtra("return-data", false);
		intent.putExtra("noFaceDetection", true);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());

		File file = ExternalOverFroyoUtils.getDiskCacheDir(
				ActivityUserInfo.this, FilePath.IMAGE);
		if(!file.exists()){
			file.mkdir();
		}
		this.cropName = file.getAbsolutePath() + 
				File.separator + new Date().getTime() + ".png";
		intent.putExtra("output", Uri.fromFile(new File(cropName))); 
		//		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(cropName))); 
		startActivityForResult(intent, 1);
	}

	private void goback(){
		Intent i = new Intent();
		if(change){
			setResult(RESULT_OK, i);
		}else{
			setResult(RESULT_CANCELED, i);
		}
		finish();
	}
}
