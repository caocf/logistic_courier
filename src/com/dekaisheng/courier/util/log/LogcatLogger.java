package com.dekaisheng.courier.util.log;

import java.io.IOException;

import com.dekaisheng.courier.util.log.ILogger;

import android.util.Log;

/**
 * 
 * @ClassName: LogcatLogger  
 * @Description: 打印到Logcat中的ILogger实现，此外还有打印到文件的等 
 * @date 2015年4月12日 上午10:03:04  
 *
 */
public class LogcatLogger implements ILogger {

	@Override
	public void v(String tag, String msg) {
		Log.v(tag, msg);
	}

	@Override
	public void d(String tag, String msg) {
		Log.d(tag, msg);
	}

	@Override
	public void i(String tag, String msg) {
		Log.i(tag, msg);
	}

	@Override
	public void w(String tag, String msg) {
		Log.w(tag, msg);
	}

	@Override
	public void e(String tag, String msg) {
		Log.e(tag, msg);
	}

	@Override
	public void open() throws IOException {
		// 
	}

	@Override
	public void close() {
		// 
	}

	@Override
	public void println(int priority, String tag, String msg) {
		Log.println(priority, tag, msg);
	}

}
