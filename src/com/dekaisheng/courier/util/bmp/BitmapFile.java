package com.dekaisheng.courier.util.bmp;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

public class BitmapFile {

	/**
	 * 
	 * @param bmp  
	 * @param name 要保存bitmap的绝对路径
	 * @return
	 * @throws IOException
	 */
	public static File saveBitmapToFile(Bitmap bmp,String name) throws IOException{
		BufferedOutputStream os = null; 
		File file = null;
		try {  
			file = new File(name);     
			file.createNewFile();  
			os = new BufferedOutputStream(new FileOutputStream(file));  
			bmp.compress(Bitmap.CompressFormat.PNG, 100, os);  
		} catch(IOException e){  
			throw e;
		}finally {
			if (os != null) {  
				try {  
					os.close();  
				} catch (IOException e) {  
					Log.e("Cache", e.getMessage());  
				}  
			}  
		}  
		return file;
	}

	/**
	 * 从文件中读取图片到bitmap
	 * @param name bitmap的绝对路径
	 * @return
	 */
	public static Bitmap bmpFromFile(String name){
		Bitmap bitmap = null;  
		File file = new File(name);   
		if(file.exists()){  
			InputStream is = null;
			BitmapFactory.Options opt = new BitmapFactory.Options();  
			opt.inPreferredConfig = Bitmap.Config.RGB_565;  
			try {
				is = new FileInputStream(file);
			} catch (FileNotFoundException e) { 
				e.printStackTrace();
			}
			bitmap = BitmapFactory.decodeStream(is,null,opt);    
		}  
		return bitmap;  
	}

	public static Bitmap bmpFromDrawable(Context context, int resId){  
		BitmapFactory.Options opt = new BitmapFactory.Options();  
		opt.inPreferredConfig = Bitmap.Config.RGB_565;   
		InputStream is = context.getResources().openRawResource(resId);  
		return BitmapFactory.decodeStream(is,null,opt);  
	}

	public static Bitmap decodeUriAsBitmap(Uri uri, Context c){
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(
					c.getContentResolver().openInputStream(uri));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}

}
