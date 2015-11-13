package com.dekaisheng.courier.lbs.activity;

import com.dekaisheng.courier.R;

/**
 * 路径规划时关键点的方向指示枚举(个人理解是这样的，准确理解请参考google官方文档
 * (貌似没有这方面的文档......参考StackOverFlow))
 * turn-sharp-left   左后方急转
 * turn-sharp-right  右后方急转
 * uturn-right       右后方转
 * uturn-left        左后方转
 * turn-slight-right 右前方转
 * turn-slight-left  
 * roundabout-left   大转盘左转
 * roundabout-right  大转盘右转
 * turn-left   左转（直）
 * turn-right
 * ramp-right
 * ramp-left
 * fork-right
 * fork-left
 * keep-left
 * keep-right
 * straight
 * ferry-train
 * ferry
 * merge
 * @author Dorian
 * 方向的枚举：
 * {@link http://stackoverflow.com/questions/17941812/google-directions-api}
 * 地图上如何添加方向图标：
 * {@link http://stackoverflow.com/questions/18202724/google-map-route-direction-show-by-arrows-in-android-v2}
 */
public enum Direction {
	TURN_SHARP_LEFT("turn-sharp-left", R.drawable.turn_sharp_left),
	TURN_SHARP_RIGHT("turn-sharp-right", R.drawable.turn_sharp_right),
	UTURN_LEFT("uturn-left", R.drawable.uturn_left),
	UTURN_RIGHT("uturn-right", R.drawable.uturn_right),
	TRUN_SLIGHT_LEFT("turn-slight-left", R.drawable.turn_slight_left),
	TURN_SLIGHT_RIGHT("turn-slight-right", R.drawable.turn_slight_right),
	ROUNDABOUT_LEFT("roundabout-left", R.drawable.round_about_left),
	ROUNDABOUT_RIGHT("roundabout-right", R.drawable.round_about_right),
	TURN_LEFT("turn-left", R.drawable.turn_left),
	TURN_RIGHT("turn-right", R.drawable.turn_right),
	RAMP_LEFT("ramp-left", R.drawable.ramp_left),
	RAMP_RIGHT("ramp-right", R.drawable.ramp_right),
	FORK_LEFT("fork-left", R.drawable.fork_left),
	FORK_RIGHT("fork-right", R.drawable.fork_right),
	KEEP_LEFT("keep-left", R.drawable.keep_left),
	KEEP_RIGHT("keep-right", R.drawable.keep_right),
	FERRY_TRAIN("ferry-train", R.drawable.ferry_train),
	FERRY("ferry", R.drawable.ferry),
	MERGE("merge", R.drawable.merge),
	STRAIGHT("straight", R.drawable.straight);
	
	private String value;
	
	private int resId; // 对应的图标ID
	
	public int getResId(){
		return this.resId;
	}
	
	public String getVal(){
		return value;
	}
	
	private Direction(String direction, int resId){
		this.value = direction;
		this.resId = resId;
	}
	
}
