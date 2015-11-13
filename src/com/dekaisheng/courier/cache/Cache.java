package com.dekaisheng.courier.cache;

import com.dekaisheng.courier.core.database.dao.UserDao;
import com.dekaisheng.courier.core.webapi.bean.User;
import com.dekaisheng.courier.util.log.Log;

public class Cache {

	private User user;
	private static Cache instance;
	
	private Cache(){
		// 
	}
	
	public synchronized static Cache getInstance(){
		if(instance == null){
			instance = new Cache();
		}
		return instance;
	}

	/**
	 * 获取用户实例，如果为null会从数据库里面读，
	 * 如果数据库里面也没有，则说明未登陆
	 * @return
	 */
	public User getUser() {
		if(user == null){
			UserDao ud = new UserDao();
			String uid = SharePreferenceHelper.getString(Constants.UID, "");
			user = ud.selectById(uid);
		}
		if(user == null){
			Log.e("Cache", "Cache.getUser() user null!!!");
		}
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
