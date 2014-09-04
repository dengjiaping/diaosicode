package com.itcalf.renhe.context.archives.edit;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;

import com.itcalf.renhe.Constants;
import com.itcalf.renhe.R;
import com.itcalf.renhe.RenheApplication;
import com.itcalf.renhe.cache.CacheManager;
import com.itcalf.renhe.context.relationship.AdvanceSearchSelectCityMainActivity;
import com.itcalf.renhe.context.relationship.AdvanceSearchSelectForeignCityMainActivity;
import com.itcalf.renhe.dto.Profile;
import com.itcalf.renhe.dto.SearchCity;
import com.itcalf.renhe.dto.UnReadMsgNum;
import com.umeng.analytics.MobclickAgent;

public class SelectCityWithChinaAndForeignActivity extends TabActivity {
	private ImageView mBackLl;
	// 我的留言按钮
	private Button mChinaBtn;
	// 好友按钮
	private Button mForeignBtn;
	private TabHost mTabHost;
	private final static int SELECT_CITY_CHINA = 1;
	private final static int SELECT_CITY_FOREIGN = 2;
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (null != getActionBar()) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
			getActionBar().setDisplayShowHomeEnabled(false);
		}
		setContentView(com.itcalf.renhe.R.layout.select_city_tab);
		setTitle("选择城市");
		initTab();
		initListener();
	}
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("SelectChinaCityMainScreen"); //统计页面
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("SelectChinaCityMainScreen"); // 保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息 
		MobclickAgent.onPause(this);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	@SuppressWarnings("deprecation")
	private void initTab() {
		mTabHost = getTabHost();
		Intent intent = new Intent(this, SelectChinaCityMainActivity.class);
		intent.putExtra("type", SELECT_CITY_CHINA);
		intent.putExtra("isFromArcheveEdit", true);
		mTabHost.addTab(mTabHost.newTabSpec("china").setIndicator("china").setContent(intent));
		
		intent = new Intent(this, SelectForeignCityMainActivity.class);
		intent.putExtra("type", SELECT_CITY_FOREIGN);
		intent.putExtra("isFromArcheveEdit", true);
		mTabHost.addTab(mTabHost.newTabSpec("foreign").setIndicator("foreign").setContent(intent));
		
		//初始化为msg选中
		setupUI();
		mTabHost.setCurrentTab(0);
		setCurrentTab(0);
	}
	private void setupUI() {
		mBackLl = (ImageView)findViewById(R.id.backBt);
		mChinaBtn = (Button) findViewById(com.itcalf.renhe.R.id.guonei_btn);
		mForeignBtn = (Button) findViewById(com.itcalf.renhe.R.id.guoji_btn);
	}

	private void initListener() {
//		mBackLl.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				finish();
//			}
//		});
		mChinaBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mTabHost.setCurrentTab(0);
				setCurrentTab(0);
			}
		});
		mForeignBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mTabHost.setCurrentTab(1);
				setCurrentTab(1);
			}
		});
	}
	private void setCurrentTab(int index) {
		mChinaBtn.setBackgroundResource(R.drawable.guonei_unselect_shape);
		mForeignBtn.setBackgroundResource(R.drawable.guoji_unselect_shape);
		
		mChinaBtn.setTextColor(getResources().getColor(R.color.white));
		mForeignBtn.setTextColor(getResources().getColor(R.color.white));
		switch (index) {
		case 0:
			mChinaBtn.setBackgroundResource(R.drawable.guonei_selected_shape);
			mChinaBtn.setTextColor(getResources().getColor(R.color.top_bacg_red));
			break;
		case 1:
			mForeignBtn.setBackgroundResource(R.drawable.guoji_selected_shape);
			mForeignBtn.setTextColor(getResources().getColor(R.color.top_bacg_red));
			break;
		}
	}

	public static final int REQUEST_CODE_PUBLIC_MSG = 1005;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_CODE_PUBLIC_MSG:
			if (resultCode == RESULT_OK) {
				setCurrentTab(0);
			}
			break;
		}

	}

	/**
	 * 重写finish方法
	 */
	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
	}
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
	}
}
