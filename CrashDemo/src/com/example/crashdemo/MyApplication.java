package com.example.crashdemo;

import android.app.Application;
import android.content.Context;

import com.example.crashdemo.crash.CrashHandler;
import com.pgyersdk.crash.PgyCrashManager;

/**
 * @author lilin
 * @date 2016年9月6日 上午9:28:28
 * @annotation
 */
public class MyApplication extends Application {

	private static Context mContext;
	
	@Override
	public void onCreate() {
		super.onCreate();
		mContext = getApplicationContext();
		// 异常处理，不需要处理时注释掉这两句即可！
		CrashHandler crashHandler = CrashHandler.getInstance();
		// 注册crashHandler
		crashHandler.init(this);
		
		// 注册蒲公英（上传错误日志）
		PgyCrashManager.register(this);
	}

	public static Context getContext(){
		return mContext;
	}
	
}
