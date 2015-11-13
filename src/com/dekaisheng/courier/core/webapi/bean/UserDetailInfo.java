package com.dekaisheng.courier.core.webapi.bean;

/**
 * 用户详细信息接口
 * {
    "code": 1001,
    "msg": "successful",
    "data": {
        "username": "alex",
        "portrait": "http://img.o2olg.com/fff.jpg",
        "gender": "1", //性别(0:未知,1:男,2:女)
        "phone_number": "11213213213",
        "address": "shenzhen baoan qu",
        "customer_service_info":"ssdfwer 12321321"
    }
}
 * @author Dorian
 *
 */
public class UserDetailInfo {

	public String username;
	public String portrait;
	public String gender;
	public String phone_number;
	public String address;
	
	/**
	 * Detailed Ino 底部要显示的文字
	 */
	public String customer_service_info;
	
}
