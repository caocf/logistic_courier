package com.dekaisheng.courier.core.database.dao;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import com.dekaisheng.courier.core.database.DBManager;
import com.dekaisheng.courier.util.log.Log;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;

/**
 * 
 * @author Dorian
 *
 * @param <T> 具体的实体类
 */
public abstract class AbsDatabaseDao<T> {

	protected final String tag = "Database";
	protected DbUtils dbUtils;
	protected Class<?> beanType;

	public AbsDatabaseDao(){
		this.dbUtils = DBManager.getDbUtils();
		// 获取泛型T的Class对象
		try{
			beanType = (Class<?>) ((ParameterizedType) getClass()
					.getGenericSuperclass()).getActualTypeArguments()[0];
		}catch(Exception e){
			e.printStackTrace();
			Log.e(tag, "获取不到泛型T的class对象" + e.getMessage());
		}
	}

	/**
	 * 插入一条数据记录，成功返回true，失败返回false
	 * @param o
	 * @return
	 */
	public boolean insert(T o){
		boolean result = false;
		try {
			this.dbUtils.saveOrUpdate(o);
			result = true;
		} catch (DbException e) {
			e.printStackTrace();
			Log.e(tag, "Database Error:" + e.getMessage());
		} catch (Exception e){
			Log.e(tag, "DbError" + e.getMessage());
		}
		return result;
	}

	/**
	 * 以事物的方式插入一组数据记录，成功返回true，失败返回false
	 * @param data
	 * @return
	 */
	public boolean insert(List<T> data){
		boolean result = false;
		try {
			this.dbUtils.saveOrUpdateAll(data);
			result = true;
		} catch (DbException e) {
			e.printStackTrace();
			Log.e(tag, "Database Error:" + e.getMessage());
		} catch (Exception e){
			Log.e(tag, "DbError" + e.getMessage());
		}
		return result;
	}

	/**
	 * 删除一条数据记录
	 * @param o
	 * @return
	 */
	public boolean delete(T o){
		boolean result = false;
		try {
			this.dbUtils.delete(o);
			result = true;
		} catch (DbException e) {
			Log.e(tag, "Database Error:" + e.getMessage());
			e.printStackTrace();
		} catch (Exception e){
			Log.e(tag, "DbError" + e.getMessage());
		}
		return result;
	}

	/**
	 * 以事物的形式删除一组记录
	 * @param all
	 * @return
	 */
	public boolean delete(List<T> all){
		boolean result = false;
		try {
			this.dbUtils.delete(all);
			result = true;
		} catch (DbException e) {
			Log.e(tag, "Database Error:" + e.getMessage());
			e.printStackTrace();
		} catch (Exception e){
			Log.e(tag, "DbError" + e.getMessage());
		}
		return result;
	}

	/**
	 * 删除表里面的所有数据记录
	 * @return
	 */
	public boolean deleteAll(){
		boolean result = false;
		try {
			this.dbUtils.deleteAll(beanType);
			result = true;
		} catch (DbException e) {
			Log.e(tag, "Database Error:" + e.getMessage());
			e.printStackTrace();
		} catch (Exception e){
			Log.e(tag, "DbError" + e.getMessage());
		}
		return result;
	}

	/**
	 * 删除指定id的记录
	 * @param id
	 * @return
	 */
	public boolean deleteById(Object id){
		boolean result = false;
		try {
			this.dbUtils.deleteById(beanType, id);
			result = true;
		} catch (DbException e) {
			Log.e(tag, "Database Error:" + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			Log.e(tag, "DbError" + e.getMessage());
		}
		return result;
	}

	/**
	 * 按条件删除
	 * @param builder
	 * @return
	 */
	public boolean delete(WhereBuilder builder){
		boolean result = false;
		try {
			this.dbUtils.delete(beanType, builder);
			result = true;
		} catch (DbException e) {
			e.printStackTrace();
			Log.e(tag, "Database Error:" + e.getMessage());
		} catch (Exception e) {
			Log.e(tag, "DbError" + e.getMessage());
		}
		return result;
	}

	/**
	 * 更新一条记录
	 * @param o
	 * @return
	 */
	public boolean update(T o){
		boolean result = false;
		try {
			this.dbUtils.saveOrUpdate(o);
			result = true;
		} catch (DbException e) {
			e.printStackTrace();
			Log.e(tag, "Database Error:" + e.getMessage());
		} catch (Exception e) {
			Log.e(tag, "DbError" + e.getMessage());
		}
		return result;
	}

	/**
	 * 更新一组数据
	 * @param all
	 * @return
	 */
	public boolean update(List<T> all){
		boolean result = false;
		try {
			this.dbUtils.saveOrUpdate(all);
			result = true;
		} catch (DbException e) {
			e.printStackTrace();
			Log.e(tag, "Database Error:" + e.getMessage());
		} catch (Exception e){
			Log.e(tag, "DbError" + e.getMessage());
		}
		return result;
	}

	/**
	 * 根据id查找一条数据
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public T selectById(String id){
		T obj = null;
		try {
			obj = (T) this.dbUtils.findById(beanType, id);
		} catch (DbException e) {
			e.printStackTrace();
			Log.e(tag, "Database Error:" + e.getMessage());
		} catch(Exception e){
			Log.e(tag, "DbError" + e.getMessage());
		}
		return obj;
	}

	/**
	 * 按条件查找
	 * @param selector
	 * @return
	 */
	public List<T> select(Selector selector){
		List<T> obj = null;
		try {
			obj = this.dbUtils.findAll(selector);
		} catch (DbException e) {
			e.printStackTrace();
			Log.e(tag, "Database Error:" + e.getMessage());
		} catch (Exception e){
			Log.e(tag, "DbError" + e.getMessage());
		}
		return obj;
	}

	/**
	 * 查询表里面的所有记录
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<T> selectAll(){
		List<T> obj = null;
		try {
			obj = (List<T>) this.dbUtils.findAll(beanType);
		} catch (DbException e) {
			e.printStackTrace();
			Log.e(tag, "Database Error:" + e.getMessage());
		} catch (Exception e){
			Log.e(tag, "DbError" + e.getMessage());
		}
		return obj;
	}

	/**
	 * 执行没有返回的sql语句，未测试！！！
	 * execNonQuery返回类型为void，不知道是否执行了，需要看看源码
	 * @param sql
	 * @return 操作是否成功执行，true成功，false失败
	 */
	public boolean exceSql(String sql){
		boolean result = false;
		try {
			this.dbUtils.execNonQuery(sql);
			result = true;
		} catch (DbException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 如果上面的查询还不能满足需求，则可以自定义sql语句进行查询，当然是需要自己实现了
	 * @param sql
	 * @return
	 */
	public abstract List<T> select(String sql);

	/**
	 * @see #select(String)
	 * @param sqlstr
	 * @param args
	 * @return
	 */
	public abstract List<T> select(String sqlstr,String[] args);

}






