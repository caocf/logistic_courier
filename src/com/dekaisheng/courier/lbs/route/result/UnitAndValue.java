package com.dekaisheng.courier.lbs.route.result;

import java.io.Serializable;

/**
 * 单位与值，time_zone是为时间添加的，表示一个度量
 * @author Dorian
 *
 */
public class UnitAndValue implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int value;
	public String text;
	public String time_zone;
}

