package com.example.crashdemo.crash;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

/**
 * 
 * @author lilin
 * @date 2016年9月6日 上午10:52:35
 * @annotation 异常处理类
 */
public class CrashHandler implements UncaughtExceptionHandler {

	public static String TAG = "andli";
	
	private Context mContext;
	//CrashHandler实例  
    private static CrashHandler instance;  
    //系统默认的UncaughtException处理类  
    private Thread.UncaughtExceptionHandler mDefaultHandler;  
    //用来存储设备信息和异常信息  
    private Map<String, String> infos = new HashMap<String, String>();  
    
	/** 保证只有一个CrashHandler实例 */  
    private CrashHandler() {  
    }  
    
    /** 获取CrashHandler实例 ,单例模式 */  
    public static CrashHandler getInstance() {  
        if (instance == null)  
            instance = new CrashHandler();  
        return instance;  
    }  
    
    /** 初始化 */ 
    public void init(Context context) {  
        mContext = context;  
        
        // 初始化日志文件夹
        LogUtils.createFoler();
		
        //获取系统默认的UncaughtException处理器  
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();  
        //设置该CrashHandler为程序的默认处理器      
        Thread.setDefaultUncaughtExceptionHandler(this);  
    }  
    
    @Override  
    public void uncaughtException(Thread thread, Throwable ex) {  
        if (!handleException(ex) && mDefaultHandler != null) {  
            //如果用户没有处理则让系统默认的异常处理器来处理  
            mDefaultHandler.uncaughtException(thread, ex);  
        } else {  
            try {  
                Thread.sleep(3000);  
            } catch (InterruptedException e) {  
                Log.e(TAG, "error : ", e);  
            }  
            //退出程序      
            android.os.Process.killProcess(android.os.Process.myPid());  
            System.exit(1);  
        }  
    }  
    /** 
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 
     * @param ex 
     * @return true:如果处理了该异常信息;否则返回false. 
     */  
    private boolean handleException(Throwable ex) {  
        if (ex == null) {  
            return false;  
        }  
        //收集设备参数信息  
        collectDeviceInfo(mContext);  
        // 使用Toast来显示异常信息  
        new Thread() {  
            @Override  
            public void run() {  
                Looper.prepare();  
                Toast.makeText(mContext, "很抱歉,程序出现异常,即将退出!", Toast.LENGTH_SHORT).show();  
                Looper.loop();  
            }  
        }.start();  
        // 保存日志文件       
        saveCatchInfo2File(ex);  
        return true;  
    } 
    
	/**
	 * 
	 * @author lilin
	 * @date 2016年9月6日 上午11:32:45
	 * @annotation 收集设备参数信息
	 */
    public void collectDeviceInfo(Context ctx) {  
        try {  
            PackageManager pm = ctx.getPackageManager();  
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);  
            if (pi != null) {  
                String versionName = pi.versionName == null ? "null" : pi.versionName;  
                String versionCode = pi.versionCode + "";  
                infos.put("versionName", versionName);  
                infos.put("versionCode", versionCode);  
            }  
        } catch (NameNotFoundException e) {  
            Log.e(TAG, "an error occured when collect package info", e);  
        }  
        Field[] fields = Build.class.getDeclaredFields();  
        for (Field field : fields) {  
            try {  
                field.setAccessible(true);  
                infos.put(field.getName(), field.get(null).toString());  
                Log.d(TAG, field.getName() + " : " + field.get(null));  
            } catch (Exception e) {  
                Log.e(TAG, "an error occured when collect crash info", e);  
            }  
        }  
    }  

	/**
	 * @author lilin
	 * @date 2016年9月6日 上午11:14:46
	 * @annotation 保存崩溃信息到SDCard
	 */
	private void saveCatchInfo2File(Throwable ex) {
		StringBuffer sb = new StringBuffer();  
        for (Map.Entry<String, String> entry : infos.entrySet()) {  
            String key = entry.getKey();  
            String value = entry.getValue();  
            sb.append(key + "=" + value + "\n");  
        }  
        Writer writer = new StringWriter();  
        PrintWriter printWriter = new PrintWriter(writer);  
        ex.printStackTrace(printWriter);  
        Throwable cause = ex.getCause();  
        while (cause != null) {  
            cause.printStackTrace(printWriter);  
            cause = cause.getCause();  
        }  
        printWriter.close();  
        String result = writer.toString();  
        sb.append(result);  
        // 写入SDCard log文件
        LogUtils.Log2Storage(sb.toString()); 
	}
	
}