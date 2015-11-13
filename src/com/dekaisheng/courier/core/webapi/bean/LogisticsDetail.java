package com.dekaisheng.courier.core.webapi.bean;

import java.util.List;

/**
 * 运单详情接口（/order/waybill/detail）
 * @author Dorian
 *
 */
public class LogisticsDetail {

	private String order_no;
	private String status;
	private String recipient_name;
	private String e_name;
	private String recipient_phone_number;
	private String address;
	private String deliver_name;
	private String signature_picture;
	private List<LogisticsStep> status_info_list;
		
	public String getSignature_picture() {
		return signature_picture;
	}

	public void setSignature_picture(String signature_picture) {
		this.signature_picture = signature_picture;
	}

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

	public String getRecipient_name() {
		return recipient_name;
	}

	public void setRecipient_name(String recipient_name) {
		this.recipient_name = recipient_name;
	}

	public String getE_name() {
		return e_name;
	}

	public void setE_name(String e_name) {
		this.e_name = e_name;
	}

	public String getRecipient_phone_number() {
		return recipient_phone_number;
	}

	public void setRecipient_phone_number(String recipient_phone_number) {
		this.recipient_phone_number = recipient_phone_number;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDeliver_name() {
		return deliver_name;
	}

	public void setDeliver_name(String deliver_name) {
		this.deliver_name = deliver_name;
	}

	public List<LogisticsStep> getStatus_info_list() {
		return status_info_list;
	}

	public void setStatus_info_list(List<LogisticsStep> status_info_list) {
		this.status_info_list = status_info_list;
	}

	public static class LogisticsStep{
		private String create_time;
		private String info;
		public String getCreate_time() {
			return create_time;
		}
		public void setCreate_time(String create_time) {
			this.create_time = create_time;
		}
		public String getInfo() {
			return info;
		}
		public void setInfo(String info) {
			this.info = info;
		}
		
	}
}
