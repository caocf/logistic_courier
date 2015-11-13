package com.dekaisheng.courier.core.database;

import android.database.sqlite.SQLiteDatabase;
import com.lidroid.xutils.DbUtils.DbUpgradeListener;
import com.dekaisheng.courier.core.database.entity.PushMessage;
import com.dekaisheng.courier.core.webapi.bean.UnFinishedOrderForm;
import com.dekaisheng.courier.core.webapi.bean.User;
import com.dekaisheng.courier.util.log.Log;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;

/**
 * Created by Dorian
 */
public class DBManager {

	private final static String TAG = "database";
	/**
	 * 初始化数据库
	 */
    public static void initDB(){
    	DbUtils.DaoConfig config = new DBConfig().getConfig();
    	DbUtils instance = DbUtils.create(config);  	
        SQLiteDatabase database = instance.getDatabase();
        int oldVersion = database.getVersion();
        int newVersion = config.getDbVersion();
        if (newVersion > oldVersion) {
            DbUpgradeListener upgradeListener = config.getDbUpgradeListener();
            if (upgradeListener != null) {
                upgradeListener.onUpgrade(instance, oldVersion, newVersion);
            } else {
                try {
                    instance.dropDb();
                    Log.i(TAG, "DbUtils dropDb");
                } catch (DbException e) {
                    Log.e(e.getMessage(), e);
                    Log.i(TAG, "DbUtils dropDb DbException");
                }
            }
            database.setVersion(newVersion);
        }else{
        	try {
        		// 统一创建数据表
				instance.createTableIfNotExist(User.class);
				instance.createTableIfNotExist(PushMessage.class);
				instance.createTableIfNotExist(UnFinishedOrderForm.class);
                Log.i(TAG, "DbUtils createTableIfNotExist");
			} catch (DbException e) {
				e.printStackTrace();
                Log.i(TAG, "DbUtils createTableIfNotExist DbException");
			}
        }
    }
    
    public static DbUtils getDbUtils(){
    	DbUtils.DaoConfig config = new DBConfig().getConfig();
    	return DbUtils.create(config);
    }

}
