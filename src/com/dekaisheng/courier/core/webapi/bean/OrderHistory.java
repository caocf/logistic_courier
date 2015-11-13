package com.dekaisheng.courier.core.webapi.bean;

import java.util.List;

/**
 * 配送员历史记录接口Bean
 * 
 * @author Dorian
 *
 */
public class OrderHistory {

	public List<YearCount> record_list;
	
	public int total;
	
	public static class YearCount {
		public int year;
		public int total;
		public List<MonthCount> list;

	}
	
	public static class MonthCount{
		public String month;
		public int count;
	}
}
