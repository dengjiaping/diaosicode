package com.itcalf.renhe.context.room;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.itcalf.renhe.R;
import com.itcalf.renhe.context.template.ActivityTemplate;
import com.itcalf.renhe.context.template.BaseActivity;
import com.itcalf.renhe.view.ProgressWebView;

/**
 * Title: WebViewActivity.java<br>
 * Description: <br>
 * Copyright (c) 人和网版权所有 2014    <br>
 * Create DateTime: 2014-8-15 下午2:02:50 <br>
 * @author wangning
 */
public class WebViewActivityForReport extends BaseActivity {
	private ProgressWebView webView;
	private String userId;
	private String msgId;
	private String msgObjectId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new ActivityTemplate().doInActivity(this, R.layout.webview);
		webView = (ProgressWebView) findViewById(R.id.webView1);
		webView.getSettings().setJavaScriptEnabled(true);//设置使用够执行JS脚本  
		webView.getSettings().setBuiltInZoomControls(true);//设置使支持缩放  
		String url = getIntent().getStringExtra("url");
		setTextValue(R.id.title_txt, "举报");
		userId = getIntent().getStringExtra("sid");
		msgId = getIntent().getStringExtra("entityId");
		msgObjectId = getIntent().getStringExtra("entityObjectId");
		//      webView.getSettings().setDefaultFontSize(5);  
		if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(msgObjectId)) {
			webView.loadUrl("http://m.renhe.cn/spamview.shtml?sid=" + userId + "&type=1&entityId=" + msgId + "&entityObjectId="
					+ msgObjectId);
		} else {
			webView.loadUrl("http://m.renhe.cn/spamview.shtml");
		}
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub  
				view.loadUrl(url);// 使用当前WebView处理跳转  
				return true;//true表示此事件在此处被处理，不需要再广播  
			}

			@Override
			//转向错误时的处理  
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				// TODO Auto-generated method stub  
				Toast.makeText(WebViewActivityForReport.this, "Oh no! " + description, Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	//默认点回退键，会退出Activity，需监听按键操作，使回退在WebView内发生  
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub  
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
			webView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
