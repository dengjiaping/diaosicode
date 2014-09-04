package com.itcalf.renhe.context.portal;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;

import com.itcalf.renhe.R;
import com.itcalf.renhe.context.template.ActivityTemplate;
import com.itcalf.renhe.context.template.BaseActivity;

public class ClauseActivity extends BaseActivity {
	private Button mBackBt;
	//private TextView mInfoTv;
	private WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.portal_clause);
		setTitle("人和网服务条款");
		findView();
		initData();
		initListener();
	}

	protected void findView() {
		mBackBt = (Button) findViewById(R.id.backBt);
		//mInfoTv = (TextView) findViewById(R.id.infoTv);
		webView=(WebView)findViewById(R.id.webview);
	}

	protected void initData() {
		//webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY) ;
		
		switch (getWindowManager().getDefaultDisplay().getWidth()) {
		case 480:
			webView.loadUrl("file:///android_asset/local_800.html");
			break;
		case 320:
			webView.loadUrl("file:///android_asset/local_480.html");
			break;
		case 240:
			webView.loadUrl("file:///android_asset/local_320.html");
			break;
		default:
			webView.loadUrl("file:///android_asset/local.html");
			break;
		}
		
	}

	protected void initListener() {
	}
}
