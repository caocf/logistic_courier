package com.dekaisheng.courier.util.push;

/**
 * 推送消息的类型，配送员端暂时只考虑前面两种
 * 1.一般消息：common_message
 * 2.订单被别个扫描带走：order_scan_again
 * 3.分配订单响应
 * 4.异常订单通知
 * @author Dorian
 *
 */
public enum MessageKey {

	COMMON_MESSAGE("common_message"),
	ORDER_SCAN_AGAIN("order_scan_again"),
	ASSIGNMENT_RESPONSE("assignment_response"),
	ABNORMAL_ORDER("abnormal_order");
	
	private String value;
	
	public String getVal(){
		return this.value;
	}
	
	private MessageKey(String val){
		this.value = val;
	}
}
