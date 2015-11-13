package com.dekaisheng.courier.lbs.activity;

import com.dekaisheng.courier.lbs.route.result.PolyLine;

/**
 * 路径规划中的每一步的指令，包含行走方向和文字提示以及距离和途径点
 * 每一个方向都有一个图标与之配对
 * @author Dorian
 *
 */
public class Instruction {

	public String direction;
	public String instruction;
	public String distance;
	public PolyLine line;

	public Instruction(String direction, 
			String instruction, String distance, PolyLine line){
		this.direction = direction;
		this.instruction = instruction;
		this.distance = distance;
		this.line = line;
	}

}
