package com.dekaisheng.courier.lbs.route.result;

import java.io.Serializable;
import java.util.List;

/**
 * 换乘路线里面的，应该是表示这一段的路线的显示样式，
 * 但是实际开发中根据自己的需要来画就好了
 * @author Dorian
 *
 */
public class Line implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String color;
	public String short_name;
	public String text_color;
	public List<Agent> agencies;
	public Vehicle vehicle;
}

