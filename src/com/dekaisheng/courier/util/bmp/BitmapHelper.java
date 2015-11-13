package com.dekaisheng.courier.util.bmp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Bitmap.Config;

/**
 * 图片处理（目前只实现了缩放和质量压缩.....）
 * 1.缩放
 * 2.质量压缩
 * 3.裁剪
 * 4.圆角、圆形
 * @author Dorian
 *
 */
public class BitmapHelper {

	/**
	 * 质量压缩，指定压缩到500k以内，大小最好可以自己配置
	 * @param bmp
	 * @return
	 */
	public static Bitmap qualityCompress(Bitmap bmp){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
		int option = 90;
		while(out.toByteArray().length > 512000){ // 压缩到500k以内
			out.reset();
			bmp.compress(Bitmap.CompressFormat.JPEG, option, out);
			option -= 10;
		}
		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		Bitmap bm = BitmapFactory.decodeStream(in);
		return bm;
	}

	/**
	 * 按比例缩放
	 * @param bmp
	 * @param scale 大于1是缩小，小于1貌似只是返回原图
	 * @return
	 */
	public static Bitmap scaleCompress(Bitmap bmp, int scale){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
//		if(out.toByteArray().length > 1048576){ // 大于1024 *1024即1M时防止oom
//			out.reset(); 
//			bmp.compress(Bitmap.CompressFormat.JPEG, 50, out);
//		}
		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inJustDecodeBounds = false;
		opt.inSampleSize = scale;
		opt.inPreferredConfig = Bitmap.Config.RGB_565; // 每个像素只占2字节，减少一半
		Bitmap bitmap = BitmapFactory.decodeStream(in, null, opt);  
		return bitmap;
	}

	/**
	 * 按比例缩放
	 * @param bmpPath 图片的完整路径
	 * @param scale   大于1是缩小，小于1貌似只是返回原图
	 * @return
	 */
	public static Bitmap scaleCompress(String bmpPath, int scale){
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inJustDecodeBounds = false;
		opt.inSampleSize = scale;
		opt.inPreferredConfig = Bitmap.Config.RGB_565; // 每个像素只占2字节，减少1或2个字节
		Bitmap bmp = BitmapFactory.decodeFile(bmpPath, opt);
		return bmp;
	}
	
	/**
	 * 以较长边为基准，缩放到thelong
	 * @param bmpPath
	 * @param theLong 缩放后的较长边的长度，只是附近，大多数情况下不等于这个长度
	 * @return
	 */
	public static Bitmap scaleToWidth(String bmpPath, float theLong){
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(bmpPath, opt);
		int w = opt.outWidth;
		int h = opt.outHeight;
		int scale = 1;
		if(w > h ){
			scale =(int) (w / theLong + 0.5f);
		}else{
			scale = (int)(h / theLong + 0.5f);
		}
		if(scale < 1){
			scale = 1;
		}
		opt.inJustDecodeBounds = false;
		opt.inSampleSize = scale;
		opt.inPreferredConfig = Bitmap.Config.RGB_565; // 每个像素只占2字节，减少1或2个字节
		Bitmap bmp = BitmapFactory.decodeFile(bmpPath, opt);
		return bmp;
	}
	
	/**
	 * 在原图片的基础上创建新的圆形的图片，注意source还会存在，内部没有销毁.所创建的图片
	 * 是以长宽中较短的一边为直径的圆，圆心为图片的中心点（width/2,height/2），所以，
	 * 图片有可能会被裁剪掉一部分或者很大一部分。最好是正方形的图片，被裁掉的只有四个角
	 * @param source
	 * @return
	 */
	public static Bitmap circleImage(Bitmap source){  
		final Paint paint = new Paint();  
		paint.setAntiAlias(true);  
		Bitmap target = Bitmap.createBitmap(source.getWidth(), 
				source.getHeight(), Config.ARGB_8888);  
		Canvas canvas = new Canvas(target);  
		int radio = source.getHeight();
		if(radio > source.getWidth()){
			radio = source.getWidth();
		}
		canvas.drawCircle(source.getWidth() / 2, 
				source.getHeight() / 2, radio/2, paint);  
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(source, 0, 0, paint);  
		return target;  
	} 
}







