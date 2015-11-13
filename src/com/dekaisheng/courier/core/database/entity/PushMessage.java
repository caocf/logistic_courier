package com.dekaisheng.courier.core.database.entity;

import java.io.Serializable;


import com.dekaisheng.courier.util.StringUtils;


/**
 * 记录推送消息
 * 
 * @author Dorian
 * 
 */
public class PushMessage implements Serializable, Comparable<PushMessage> {

	private static final long serialVersionUID = 1L;

	public int id;
	// 前台添加的字段，标示是否已经阅读，小于0为未读
	public int read = -1;
	public String uid;
	// 真正的CommonMessage
	public String title;

	public String key;          // key值决定是什么类型的消息
	public String message;      // 显示在Notification中的Content

	public String portrait;     // 发送者头像
	public String publisher;    // 发布者
	public String publish_time; // 发布时间
	public String content;      // 内容
    public String image;
	// 订单被抢的字段
	public String order_no;

	/**
	 * 用于排序，原则是未读的在前面，已读的放在后面，未读和已读的再按照
	 */
	@Override
	public int compareTo(PushMessage another) {
		if(another == null){
			return -1;
		}
		if(read > 0 && another.read < 0){
			return 1;
		}else if(read < 0 && another.read > 0){
			return -1;
		}else if(read < 0 && another.read < 0){ // 两个都是未读消息时，比较日期，
			compareTo(another.publish_time);
		}else if(read > 0 && another.read > 0){
			compareTo(another.publish_time);
		}
		return -1;
	}
	

	private int compareTo(String time) {
		if (!StringUtils.isEmpty(publish_time)) {
			int result = publish_time.compareTo(time);
			if (result > 0) {
				return -1;
			} else if (result == 0) {
				return 0;
			} else {
				return 1;
			}
		}
		return 0;
	}
}
