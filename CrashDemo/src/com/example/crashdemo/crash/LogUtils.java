package com.example.crashdemo.crash;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;

import com.example.crashdemo.MyApplication;
import com.example.crashdemo.R;

public class LogUtils {
	
	public static String DateFormat_24 = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 
	 * @author lilin
	 * @date 2016年9月6日 下午3:06:08
	 * @annotation 创建文件夹
	 */
	public static void createFoler() {
		File f = new File(getLogPath());
		if (!f.exists()) {
			f.mkdirs();
		}
		// 清理5天前的日志
		clearLocalLogs();
	}



	/**
	 * 
	 * @author lilin
	 * @date 2016年9月6日 下午2:45:47
	 * @annotation 清理5天前的日志
	 */
	static void clearLocalLogs() {
		File f = new File(getLogPath());
		File[] files = f.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.exists() && file.isFile()) {
					String FileName 	= file.getName().substring(0,file.getName().indexOf("."));
					String todayName 	= convertDate(new Date(),"yyyy-MM-dd");
					Date fileDate 		= convertString2Date(FileName,"yyyy-MM-dd");
					Date todayDate 		= convertString2Date(todayName,"yyyy-MM-dd");
					if (todayDate.getTime() - fileDate.getTime() >= 86400000 * 5) {
						file.delete();
					}
				}
			}
		}
	}
	
	/**
	 * 
	 * @author lilin
	 * @date 2016年9月6日 下午3:01:42
	 * @annotation 读取日志生成文件夹路径
	 */
	private static String getLogPath(){
		return getRootFilePath()+"/"+MyApplication.getContext().getString(R.string.logpath);
	}

	/**
	 * 
	 * @author lilin
	 * @date 2016年9月6日 下午2:44:31
	 * @annotation 获取外部存储根路径
	 */
	private static String getRootFilePath() {
		String file_dir = "";
		// SD卡是否存在
		boolean isSDCardExist  = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
		// Environment.getExternalStorageDirectory()相当于File file=new File("/sdcard")
		boolean isRootDirExist = Environment.getExternalStorageDirectory().exists();
		if (isSDCardExist && isRootDirExist) {
			file_dir = Environment.getExternalStorageDirectory().getAbsolutePath();
		} else {
			// MyApplication.getInstance().getFilesDir()返回的路劲为/data/data/PACKAGE_NAME/files，其中的包就是我们建立的主Activity所在的包
			file_dir = MyApplication.getContext().getFilesDir().getAbsolutePath();
		}
		return file_dir;
	}
	


	/**
	 * 
	 * @author lilin
	 * @date 2016年9月6日 下午3:06:41
	 * @annotation 将日志记录到本地log文件
	 */
    public static void Log2Storage(String log){
        try{
            String LogPath = getLogPath();
            String logName = convertDate(new Date(), "yyyy-MM-dd")+".log";
            
            PrintWriter pw = new PrintWriter(new BufferedOutputStream(new FileOutputStream(LogPath+"/"+logName, true)));
            String dataTag = "["+convertDate(new Date(), DateFormat_24)+"]";
            pw.write(dataTag);
            pw.write(log);
            pw.write("\r\n");
            pw.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    } 
    
	public static String convertDate(Date date, String format) {
		if (date != null) {
			DateFormat format1 = new SimpleDateFormat(format);
			String s = format1.format(date);
			return s;
		}
		return "";
	}

	public static Date convertString2Date(String str, String formatStr) {
		DateFormat format = new SimpleDateFormat(formatStr);
		Date date = null;
		try {
			date = format.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

}
