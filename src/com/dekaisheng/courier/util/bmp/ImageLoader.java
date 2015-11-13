package com.dekaisheng.courier.util.bmp;

import com.dekaisheng.courier.R;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

/**
 * 加载图片到ImageView的工具类，需要设置圆角的，在调用前设置circle=true即可，
 * 默认circle=true，所以不要圆角的需要设置为false，因为默认为true就不需要更改
 * 项目的代码。
 * @author Dorian
 *
 */
public class ImageLoader {
	
	private BitmapUtils bitmapUtils;
	
	/**
	 * 是否裁剪为圆形
	 */
	private boolean circle = true;
	
	public ImageLoader(Context c){
		bitmapUtils = new BitmapUtils(c);
		bitmapUtils.configDefaultLoadFailedImage(R.drawable.default_portrait);
		bitmapUtils.configDefaultLoadingImage(R.drawable.default_portrait);
	}

	/**
	 * 设置加载过程中显示的临时图片
	 * @param loadingResID Drawable ID
	 * @return
	 */
	public ImageLoader loadingImage(int loadingResID){
		this.bitmapUtils.configDefaultLoadFailedImage(loadingResID);
		return this;
	}
	
	/**
	 * 设置加载失败显示的图片
	 * @param failedID 图片是drawable的ID
	 * @return
	 */
	public ImageLoader failedImage(int failedID){
		this.bitmapUtils.configDefaultLoadFailedImage(failedID);
		return this;
	}
	
	/**
	 * 设置是否显示圆形图片，注意，一旦设置为圆形图片，则会在内存里面新建一个Bitmap实例
	 * @param circle true则显示为圆形，false则为原来样子，默认为true
	 * @return
	 */
	public ImageLoader circle(boolean circle){
		this.circle = circle;
		return this;
	}
	
	/**
	 * 加载图片到指定的ImageView
	 * @param img     显示图片的ImageView容器
	 * @param url     图片的地址，可以是http地址也可以是uri地址或本地文件地址
	 * @param width   默认图片的最大尺寸，如果设置为<1则不设置默认最大尺寸参数 
	 * @param height  默认图片的最大尺寸，如果设置为<1则不设置默认最大尺寸参数 
	 */
	public void into(ImageView img,String url, Integer width, Integer height){
		if(width != null && width > 0 && height != null && height > 0){
			bitmapUtils.configDefaultBitmapMaxSize(width, height);
		}
		bitmapUtils.display(img, url, new DefaultBitmapLoadCallBack<ImageView>(){

			@Override
			public void onLoadCompleted(ImageView container, String uri, Bitmap bitmap, 
					BitmapDisplayConfig config, BitmapLoadFrom from) {
				if(circle){
					/* 原图是不能recycle的 */
					bitmap = BitmapHelper.circleImage(bitmap); 
				}
				super.onLoadCompleted(container, uri, bitmap, config, from);
			}

			@Override
			public void onLoadFailed(ImageView container, String uri, Drawable drawable) {
				super.onLoadFailed(container, uri, drawable);
			}
			
		});
	}
	
}
