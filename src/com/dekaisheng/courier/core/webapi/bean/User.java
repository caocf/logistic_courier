package com.dekaisheng.courier.core.webapi.bean;

import com.google.android.gms.maps.model.LatLng;
import com.lidroid.xutils.db.annotation.Id;

/**
 * 登录接口返回实体
 * 
 * Created by Dorian on 2015/7/22.
 * 
 * {
    "code": 1001,
    "msg": "success",
    "data": {
        "token": "430173a7c0ede61b7225509dfb80a9b9",//登陆token
        "uid": "232342342325",//用户id
        "username": "alemeng",//用户名
        "portrait": "http://www.xtouchdevice.com/images/logo.png", //头像
        "gender": "1",//性别(0:未知,1:男,2:女)
        "phone_number": "15601777609",//手机号码
        "email": "mshede@gmail.com",//email
        "address": "1"//住址    
	 }
}

 */

public class User {

    private String token;
    @Id
    private String uid;
    private String username;
    private String portrait;
    private String gender; /*0未知，1男，2女*/
    private String phone_number;
    private String email;
    private String address;
    /**
     *  api返回是一个LatLng对象，但是不能存到数据库，所以还是要保存经纬度，
     *  然后再重新创建LatLng对象，注意在存数据库时需要先给longitude和latitude赋值！
     */
    private LatLng location; // 站点位置
    private double longitude;
    private double latitude;

    public User(){

    }


	public LatLng getLocation() {
		if(location == null){
			// 确保location不为空
			location = new LatLng(latitude, longitude);
		}
		return location;
	}

	public void setLocation(LatLng location) {
		this.location = location;
	}

	public String getToken() {
		return token;
	}


	public void setToken(String token) {
		this.token = token;
	}


	public String getUid() {
		return uid;
	}


	public void setUid(String uid) {
		this.uid = uid;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getPortrait() {
		return portrait;
	}


	public void setPortrait(String portrait) {
		this.portrait = portrait;
	}


	public String getGender() {
		return gender;
	}


	public void setGender(String gender) {
		this.gender = gender;
	}


	public String getPhone_number() {
		return phone_number;
	}


	public void setPhone_number(String phone_number) {
		this.phone_number = phone_number;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}



	public double getLongitude() {
		return longitude;
	}


	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}


	public double getLatitude() {
		return latitude;
	}


	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

}
