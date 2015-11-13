package com.dekaisheng.courier.cache;

import com.dekaisheng.courier.MyApplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 简单类型数据缓存SharePreference
 * 
 * @author Dorian
 *
 */
public final class SharePreferenceHelper {

	private final static String setting = "setting";
	
	public static void put(String key,Object value){
		SharedPreferences sp = MyApplication.getApplication()
				.getSharedPreferences(setting, Context.MODE_MULTI_PROCESS);
		Editor ed = sp.edit();
		if(value instanceof Boolean){
			ed.putBoolean(key, (Boolean) value);
		}else if(value instanceof Float){
			ed.putFloat(key, (Float) value);
		}else if(value instanceof Integer){
			ed.putInt(key, (Integer) value);
		}else if(value instanceof Long){
			ed.putLong(key, (Long) value);
		}else if(value instanceof String){
			ed.putString(key, (String) value);
		}
		ed.commit();
	}

	public static Boolean getBoolean(String key, Boolean defValue){
		SharedPreferences sp = MyApplication.getApplication()
				.getSharedPreferences(setting, 0);
		return sp.getBoolean(key, defValue);
	}
	
	public static float getFloat(String key, float defValue){
		SharedPreferences sp = MyApplication.getApplication()
				.getSharedPreferences(setting, 0);
		return sp.getFloat(key, defValue);
	}
	
	public static int getInt(String key, int defValue){
		SharedPreferences sp = MyApplication.getApplication()
				.getSharedPreferences(setting, 0);
		return sp.getInt(key, defValue);
	}
	
	public static Long getLong(String key, Long defValue){
		SharedPreferences sp = MyApplication.getApplication()
				.getSharedPreferences(setting, 0);
		return sp.getLong(key, defValue);
	}
	
	public static String getString(String key, String defValue){
		SharedPreferences sp = MyApplication.getApplication()
				.getSharedPreferences(setting, 0);
		return sp.getString(key, defValue);
	}
}
