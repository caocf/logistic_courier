package com.dekaisheng.courier.core.database.dao;

import java.util.List;

import com.dekaisheng.courier.core.webapi.bean.UnFinishedOrderForm;

/**
 * 未完成订单数据表操作
 * @author Dorian
 *
 */
public class UnFinishedOrderDao extends AbsDatabaseDao<UnFinishedOrderForm>{

	@Override
	public List<UnFinishedOrderForm> select(String sql) {
		return null;
	}

	@Override
	public List<UnFinishedOrderForm> select(String sqlstr, String[] args) {
		return null;
	}

}
