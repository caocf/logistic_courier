package com.dekaisheng.courier.util.log;

import java.io.IOException;

/**
 * 
 * @ClassName: ILogger  
 * @Description: 一个日志接口，会有多种实现，记录日志的时候会每个实现都记录，如Log，文件等  
 * @date 2015年4月12日 上午9:52:07  
 *
 */
public interface ILogger {

	void v(String tag, String msg);
	
	void d(String tag, String msg);
	
	void i(String tag, String msg);
	
	void w(String tag, String msg);
	
	void e(String tag, String msg);
	
	void open() throws IOException;
	
	void close();
	
	void println(int priority, String tag, String message);
}
