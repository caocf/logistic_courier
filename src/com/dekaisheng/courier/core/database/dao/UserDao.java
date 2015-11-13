package com.dekaisheng.courier.core.database.dao;

import java.util.List;

import com.dekaisheng.courier.core.webapi.bean.User;

/**
 * 
 * @author Administrator
 *
 */
public class UserDao extends AbsDatabaseDao<User>{

	@Override
	public List<User> select(String sql) {
		return null;
	}

	@Override
	public List<User> select(String sqlstr, String[] args) {
		return null;
	}

}
