package com.dekaisheng.courier.lbs.route.result;

import java.io.Serializable;
import java.util.List;

/**
 * 地理编码得到的点
 * @author Dorian
 *
 */
public class CodePoint implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String geocoder_status;
	public String place_id;
	public List<String> types;
}
