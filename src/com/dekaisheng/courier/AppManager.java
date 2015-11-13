package com.dekaisheng.courier;

import java.util.Iterator;
import java.util.Stack;

import com.dekaisheng.courier.util.log.Log;

import android.app.Activity;

public class AppManager{

	private static AppManager instance;
	private Stack<Activity> activities;

	private AppManager(){
		this.activities = new Stack<Activity>();
	}

	public static AppManager getActivityManager(){
		if(instance == null){
			instance = new AppManager();
		}
		return instance;
	}

	public void add(Activity activity) {
		if(this.activities == null){
			this.activities = new Stack<Activity>();
		}
		this.activities.add(activity);
	}

	public void remove(Activity activity) {
		this.activities.remove(activity);
	}

	/**
	 * 移除某一类别的Activity实例
	 * @param classObj
	 */
	public void remove(Class<?> classObj) {
		Activity a = null;
		int j = this.activities.size();
		for(int i = 0; i < j; i++){
			a = this.activities.get(i);
			if(a.getClass().equals(classObj)){
				this.activities.remove(a);
				j = this.activities.size();
			}
		}
		a = null;
	}

	public Activity getCurrentActivity() {
		Activity a = this.activities.lastElement();
		return a;
	}

	/**
	 * 跳转到指定类型的Activity，如果没有则finish所有Activity
	 * 没有测试过，
	 * @param classObj
	 */
	public void toActivity(Class<?> classObj) {
		Activity ac = null;
		Iterator<Activity> it = this.activities.iterator();
		while(it.hasNext()){
			ac = this.activities.pop(); // 栈顶
			if(ac.getClass().equals(classObj)){
				break;
			}else{
				finish(ac);
			}
		}
	}

	/**
	 * 
	 */
	public void toMainActivity() {
		//		Activity a = null;
		//		int j = this.activities.size();
		//		for(int i = 0; i < j; i++){
		//			a = this.activities.get(i);
		//			if(a != null){
		//				a.finish();
		//			}
		//		}
		//		this.activities.clear();
	}

	/**
	 * finish所有Activity，和finishAll一样的，有重复的嫌疑
	 */
	public void exit() {
		try{
			finishAll();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * finish指定的Activity实例
	 * @param activity
	 */
	public void finish(Activity activity) {
		if(activity != null){
			try{
				activity.finish();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	/**
	 * finish所有的Activity并清空activity栈
	 */
	public void finishAll() {
		Activity a = null;
		Iterator<Activity> it = this.activities.iterator();
		while(it.hasNext()){
			a = it.next();
			finish(a);
		}
		this.activities.clear();
	}

	/**
	 * finish当前栈顶Activity
	 */
	public void finish() {
		try{
			Activity a = this.activities.pop();
			this.finish(a);
		}catch(Exception e){
			Log.e("finish", "NoSuchElementException if this vector is empty:" + e.getMessage());
		}
	}

}
