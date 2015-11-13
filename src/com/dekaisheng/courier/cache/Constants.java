package com.dekaisheng.courier.cache;

public final class Constants {

	/*判断是否已登录*/
	public static final String LOGIN = "login";
	
	/*用户的uid*/
	public static final String UID = "uid";
	
	/*密码*/
	public static final String PASSWORD = "password";
	
	/*gcm注册得到的id*/
	public static final String PUSH_REGISTER_ID = "pushID";
	
	/*是否已经上传了gcm的id*/
	public static final String SAVE_PUSH_ID_TO_SERVER = "save_push_id";
	
	/*判断是否已经注册了gcm的push id注册过后将会推送到后台服务器*/
	public static final String FIRST_LUNCH = "firstLunch";
	
	public final static String AppHasNew = "AppHasNew";
	
	public final static String HaveNewPush = "HaveNewPush";
	
//	/*判断是否默认开启GPS，是则在程序启动时开启gps，否则不进行任何操作*/
//	public static final String GPS_ENABLED = "gpsEnable";
	
//	public static final String USER_NAME = "username";
//	public static final String TOKEN = "token";
//	public static final String PASSWORD = "password";
//	public static final String PORTRAIT = "portrait";
	
}
