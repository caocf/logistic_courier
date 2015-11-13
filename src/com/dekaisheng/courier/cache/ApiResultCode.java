package com.dekaisheng.courier.cache;

/**
 * 接口请求返回的状态码code
 * 
 * @author Dorian
 *
 */
public class ApiResultCode {

	public static final int SUCCESS_1001 = 1001;  // 成功
	public static final int NOT_FOUND_1002 = 1002;// 目标资源不存在
	public static final int WRONG_PWD_1003 = 1003;// 密码错误
	public static final int ACOUNT_FROZEN_1004 = 1004;//用户账号已被冻结
	public static final int TIME_OUT_1005 = 1005; // 响应超时
	public static final int NOT_LOGIN_1006 = 1006;// 用户未登录
	public static final int WRONG_PARAMS_1007 = 1007;//参数有误
	public static final int HAD_SCAN_1023 = 1023; // 运单已经站点确认过
	public static final int NOT_YOUR_ORDER_1024 = 1024; // 不是你的订单
	public static final int UN_AUTHORIZED = 401; // 设备在另外一个设备登录
	public static final int ORDER_HAD_BEEN_SIGN_1026 = 1026; // Order had been sign
	
}
