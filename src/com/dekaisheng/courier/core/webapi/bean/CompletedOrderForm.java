package com.dekaisheng.courier.core.webapi.bean;

/**
 * 已完成订单接口
 * @author Dorian
 *已完成：
{
    "code": "1001",
    "msg": "successful",
    "data": [
        {
            "order_no": "205420150708544",
            "status": "1",
		    "status_info": "normal delivery",
            "e_name": "JD",
            "recipient_name": "alex",
            "recipient_phone_number": "1520124451"
        },
        {
            "order_no": "205420150708545",
            "status": "2",
            "e_name": "JD",
            "recipient_name": "alex",
		"status_info": "address error",
            "recipient_phone_number": "1520124451"
        }
    ]
}

 */
public class CompletedOrderForm {

	private String order_no;
	private String status;
	private String e_name;
	private String recipient_name;
	private String status_info;
	private String recipient_phone_number;
	
	public String getOrder_no() {
		return order_no;
	}
	public void setOrder_no(String order_no) {
		this.order_no = order_no;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getE_name() {
		return e_name;
	}
	public void setE_name(String e_name) {
		this.e_name = e_name;
	}
	public String getRecipient_name() {
		return recipient_name;
	}
	public void setRecipient_name(String recipient_name) {
		this.recipient_name = recipient_name;
	}
	public String getStatus_info() {
		return status_info;
	}
	public void setStatus_info(String status_info) {
		this.status_info = status_info;
	}
	public String getRecipient_phone_number() {
		return recipient_phone_number;
	}
	public void setRecipient_phone_number(String recipient_phone_number) {
		this.recipient_phone_number = recipient_phone_number;
	}
	
}
