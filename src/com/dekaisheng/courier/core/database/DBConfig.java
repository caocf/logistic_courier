package com.dekaisheng.courier.core.database;

import com.dekaisheng.courier.MyApplication;
import com.dekaisheng.courier.util.log.Log;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.DbUtils.DbUpgradeListener;

/**
 * 数据库配置信息？
 * @author Dorian
 *
 */
public class DBConfig {

    private final String DB_NAME = "oto";
    private final int DB_VERSION = 1;
    private DbUpgradeListener upgradeListener;
    
    public DBConfig(){
    	
    	upgradeListener = new DbUpgradeListener(){

			@Override
			public void onUpgrade(DbUtils arg0, int arg1, int arg2) {
				// 数据库升级比如说增加、删除、修改字段、表等???
				Log.i(DB_NAME, "DbUpgradeListener onUpgrade");
			}
    		
    	};
    }
    
    public DbUtils.DaoConfig getConfig(){
    	DbUtils.DaoConfig config = new DbUtils.DaoConfig(MyApplication.getApplication());
    	config.setDbVersion(DB_VERSION);
    	config.setDbName(DB_NAME);
    	config.setDbUpgradeListener(upgradeListener);
    	return config;
    }

}
