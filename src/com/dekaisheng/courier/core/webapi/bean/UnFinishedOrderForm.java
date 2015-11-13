package com.dekaisheng.courier.core.webapi.bean;

import java.io.Serializable;

import com.lidroid.xutils.db.annotation.Id;

/**
 * 未完成订单接口实体类
 * @author Dorian
 */
public class UnFinishedOrderForm implements Serializable{

	private static final long serialVersionUID = 1L;

	public String uid; // 用于区分不同用户的订单
	
	@Id
	public String order_no;
	
	public String address;
	public String e_name;
	public String expected_time; // 期望到达的时间
	public String recipient_name;
	public String recipient_phone_number;
	public double lt; // 纬度值
	public double lg; // 经度值
	
//	public boolean equals(Object obj){
//		if(!(obj instanceof UnFinishedOrderForm)){
//			return false;
//		}
//		UnFinishedOrderForm temp = (UnFinishedOrderForm)obj;
//		if(temp.order_no == null || this.order_no == null){
//			return false;
//		}
//		if(temp.order_no.equals(this.order_no)){
//			return true;
//		}
//		return false;
//	}
}
