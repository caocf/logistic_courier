/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dekaisheng.courier.util.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.dekaisheng.courier.MyApplication;
import com.dekaisheng.courier.util.AndroidVersionCheckUtils;
import com.dekaisheng.courier.util.cache.ExternalOverFroyoUtils;
import com.dekaisheng.courier.util.cache.ExternalUnderFroyoUtils;

/**
 * 
 * @ClassName: FileLogger  
 * @Description: 打印到文件的Log，添加到Loger的集合中即可
 * @date 2015年5月23日 下午11:38:11  
 *
 */
public class FileLogger implements ILogger {

	public static final int VERBOSE = 2;

	public static final int DEBUG = 3;

	public static final int INFO = 4;

	public static final int WARN = 5;

	public static final int ERROR = 6;

	public static final int ASSERT = 7;
	private String mPath;
	private Writer mWriter;

	private static final SimpleDateFormat TIMESTAMP_FMT = new SimpleDateFormat(
			"[yyyy-MM-dd HH:mm:ss] ");
	private String basePath = "";
	private static String LOG_DIR = "log";
	private static String BASE_FILENAME = ".txt";
	private File logDir;

	public FileLogger() {

	}

	public void open() throws IOException {
		if (AndroidVersionCheckUtils.hasFroyo()){
			logDir = ExternalOverFroyoUtils.getDiskCacheDir(MyApplication
					.getApplication().getApplicationContext(), LOG_DIR);
		} else{
			logDir = ExternalUnderFroyoUtils.getDiskCacheDir(MyApplication
					.getApplication().getApplicationContext(), LOG_DIR);
		}
		if (!logDir.exists()){
			logDir.mkdirs();
			try{
				new File(logDir, ".nomedia").createNewFile();
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		basePath = logDir.getAbsolutePath() + "/"+ getCurrentTimeString() + BASE_FILENAME;
		try{
			File file = new File(basePath);
			if (!file.exists()){
				file.createNewFile();
			}
			mPath = file.getAbsolutePath();
//			mWriter = new BufferedWriter(new FileWriter(file), 2048); 
			mWriter = new PrintWriter(new BufferedWriter(new FileWriter(file),2028));
		} finally {
			if (mWriter == null)
				mPath = null;
		}

	}

	public void close() {
		try{
			mPath = null;
			if (mWriter != null)
				mWriter.close();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	private String getCurrentTimeString() {
		Date now = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyyMMddHHmmss");
		return simpleDateFormat.format(now);
	}

	public String getPath() {
		return mPath;
	}

	@Override
	public void d(String tag, String message) {
		// TODO Auto-generated method stub
		println(DEBUG, tag, message);
	}

	@Override
	public void e(String tag, String message) {
		println(ERROR, tag, message);
	}

	@Override
	public void i(String tag, String message) {
		println(INFO, tag, message);
	}

	@Override
	public void v(String tag, String message) {
		println(VERBOSE, tag, message);
	}

	@Override
	public void w(String tag, String message) {
		println(WARN, tag, message);
	}

	@Override
	public void println(int priority, String tag, String message) {
		String printMessage = "";
		switch (priority){
		case VERBOSE:
			printMessage = "[V]|"
					+ tag
					+ "|"
					+ MyApplication.getApplication().getApplicationContext()
					.getPackageName() + "|" + message;
			break;
		case DEBUG:
			printMessage = "[D]|"
					+ tag
					+ "|"
					+ MyApplication.getApplication().getApplicationContext()
					.getPackageName() + "|" + message;
			break;
		case INFO:
			printMessage = "[I]|"
					+ tag
					+ "|"
					+ MyApplication.getApplication().getApplicationContext()
					.getPackageName() + "|" + message;
			break;
		case WARN:
			printMessage = "[W]|"
					+ tag
					+ "|"
					+ MyApplication.getApplication().getApplicationContext()
					.getPackageName() + "|" + message;
			break;
		case ERROR:
			printMessage = "[E]|"
					+ tag
					+ "|"
					+ MyApplication.getApplication().getApplicationContext()
					.getPackageName() + "|" + message;
			break;
		default:
			break;
		}
		println(printMessage);

	}

	public void println(String message) {
		try{
			synchronized (mWriter) {
				mWriter.write(TIMESTAMP_FMT.format(new Date()));
				mWriter.write(message);
				mWriter.write('\n');
				mWriter.flush();
			}
		} catch (Exception e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
