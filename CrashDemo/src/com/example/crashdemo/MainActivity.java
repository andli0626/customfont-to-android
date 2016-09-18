package com.example.crashdemo;


import java.io.IOException;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	TextView txtView;
	TextView txtView2;
	WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		txtView = (TextView)findViewById(R.id.txt);
		txtView2 = (TextView)findViewById(R.id.txt2);
		webView = (WebView)findViewById(R.id.webview);
		
		
		/*
         * 必须事先在assets底下创建一fonts文件夹 并放入要使用的字体文件(.ttf)
         * 并提供相对路径给creatFromAsset()来创建Typeface对象
         */
        Typeface fontFace = Typeface.createFromAsset(getAssets(),"fonts/dfgb_k.ttf"); 	// 华康楷体
        // 字体文件必须是true type font的格式(ttf)；
        // 当使用外部字体却又发现字体没有变化的时候(以 Droid Sans代替)，通常是因为
        // 这个字体android没有支持,而非你的程序发生了错误
        txtView.setTypeface(fontFace);
        
        fontFace = Typeface.createFromAsset(getAssets(),"fonts/dfgb_s.ttf"); 			// 华康宋体
        txtView2.setTypeface(fontFace);
        
        // 加载URL
        webView.loadUrl("http://1.testmyhtml.applinzi.com/test.html");

        // 加载本地HTML
//        webView.loadUrl(" file:///android_asset/test.html ");  // 自定义字体
//        webView.loadUrl(" file:///android_asset/test2.html "); // 系统默认字体
        
		webView.setWebViewClient(new WebViewClient() {
			
			// 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
			public boolean shouldOverrideUrlLoading(WebView view, String url) { 
				view.loadUrl(url);
				return true;
			}
			
			@Override
			public WebResourceResponse shouldInterceptRequest(WebView view,String url) {
				WebResourceResponse response = super.shouldInterceptRequest(view, url);
				Log.i("andli", "shouldInterceptRequest");
				Log.i("andli", "load intercept request:" + url);
				if (url != null && url.contains("dfgb_k.ttf")) {
					String assertPath = "fonts/dfgb_k.ttf";
					try {
						response = new WebResourceResponse("application/x-font-ttf", "UTF8", getAssets().open(assertPath));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				return response;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				Log.i("andli","onPageFinished");
				view.loadUrl("javascript:!function(){" +  
                        "s=document.createElement('style');s.innerHTML="  
                        + "\"@font-face{font-family:myhyqh;src:url('****/fonts/dfgb_k.ttf');}*{font-family:myhyqh !important;}\";"  
                        + "document.getElementsByTagName('head')[0].appendChild(s);" +  
                        "document.getElementsByTagName('body')[0].style.fontFamily = \"myhyqh\";}()");  
			    super.onPageFinished(view, url);  
			}
			
		});
	}
	

}
