package com.dekaisheng.courier.core.database.dao;

import java.util.List;
import com.dekaisheng.courier.core.database.entity.PushMessage;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.util.LogUtils;

/**
 * 推送通知的DAO
 * @author Dorian
 *
 */
public class PushMessageDao extends AbsDatabaseDao<PushMessage>{
	private static final PushMessageDao messageDao = new PushMessageDao();

	public static PushMessageDao getInstance() {
		return messageDao;
	}
	@Override
	public List<PushMessage> select(String sql) {
		return null;
	}

	@Override
	public List<PushMessage> select(String sqlstr, String[] args) {
		return null;
	}
	
	/**
	 * 查询未读消息
	 * 
	 * @return
	 */
	public boolean isHaveReadMessage() {
		List<PushMessage> messages = null;
		try {
			messages = dbUtils.findAll(Selector.from(PushMessage.class).where("read",
					"=", "-1"));
			if (messages!=null && messages.size() > 0) {
				return true;
			}
		} catch (DbException e) {
			LogUtils.e("查询未读消息失败");
		}
		return false;
	}

}
