package com.itcalf.renhe.context.template;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.itcalf.renhe.R;
import com.itcalf.renhe.RenheApplication;
import com.umeng.analytics.MobclickAgent;

/**
 * 继承Activity，并提供Activity的基本函数。
 * 
 * @author piers.xie
 */
public abstract class BaseActivity extends SherlockActivity {

	/** 系统配置 */
	protected SharedPreferences mSettings;
	protected TextView mTitleTxt;
	private SharedPreferences msp;
	private SharedPreferences.Editor mEditor;
	private float scale;
	public final static String ICON_ACTION = "notice_icon_num";
	public final static String NEWMSG_ICON_ACTION = "newmsg_notice_icon_num";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSettings = getPreferences(2);
		msp = this.getSharedPreferences("setting_info", 0);
		mEditor = msp.edit();
		scale = getResources().getDisplayMetrics().density;
		if (null != getSupportActionBar()) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setDisplayShowHomeEnabled(false);
		}
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	//	@Override
	//	public boolean onPrepareOptionsMenu(Menu menu) {
	//		return super.onPrepareOptionsMenu(menu);
	//	}
	//
	//	@Override
	//	public boolean onCreateOptionsMenu(Menu menu) {
	//		// Inflate the menu items for use in the action bar
	//		MenuInflater inflater = getMenuInflater();
	//		inflater.inflate(R.menu.main, menu);
	//		return super.onCreateOptionsMenu(menu);
	//	}

	//	@Override
	//	public boolean onOptionsItemSelected(MenuItem item) {
	//		switch (item.getItemId()) {
	//		case android.R.id.home:
	//			finish();
	//			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
	//			return true;
	//		case R.id.item_save:
	//			//			Intent intent = new Intent(BaseActivity.this, AdvancedSearchActivity.class);
	//			//			intent.putExtra("isFromMenu", false);
	//			//			startActivity(intent);
	//			//			if(null != BaseActivity.this.getParent()){
	//			//				BaseActivity.this.getParent().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	//			//			}else{
	//			//				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	//			//			}
	//		}
	//		return super.onOptionsItemSelected(item);
	//	}

	/**
	 * 获取模板
	 * 
	 * @return
	 */
	protected ActivityTemplate getTemplate() {
		return new ActivityTemplate();
	}

	/**
	 * 获取组件对象
	 */
	protected void findView() {
		mTitleTxt = (TextView) findViewById(R.id.title_txt);
	};

	/**
	 * 初始化组件数据
	 */
	protected void initData() {
	};

	/**
	 * 初始化组件监听器
	 */
	protected void initListener() {
	};

	/**
	 * 获取当前Application对象
	 * 
	 * @return MaxApplication
	 */
	protected RenheApplication getRenheApplication() {
		return (RenheApplication) getApplicationContext();
	}

	@Override
	protected void onResume() {
		super.onResume();
		//		StatService.onResume(this);
		//友盟统计
		MobclickAgent.onPageStart("SplashScreen"); //统计页面
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		//		StatService.onPause(this);
		//友盟统计
		MobclickAgent.onPageEnd("SplashScreen"); // 保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息 
		MobclickAgent.onPause(this);
	}

	/**
	 * 启动Activity
	 * 
	 * @param clazz
	 */
	protected void startActivity(Class<?> clazz) {
		startActivity(new Intent(this, clazz));
		if (null != this.getParent()) {
			this.getParent().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		} else {
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		}
	}

	/**
	 * 启动Activity,同时销毁自己
	 * 
	 * @param clazz
	 */
	protected void startActivityWithFinish(Class<?> clazz) {
		startActivity(clazz);
		finish();
	}

	/**
	 * 重写finish方法
	 */
	@Override
	public void finish() {
		super.finish();
	}

	/**
	 * 启动Activity
	 * 
	 * @param clazz
	 * @param flag
	 */
	protected void startActivity(Class<?> clazz, int flag) {
		Intent intent = new Intent(this, clazz);
		intent.setFlags(flag);
		startActivity(intent);
		finish();

	}

	protected void startActivity(Class<?> clazz, Bundle bundle, int flag) {
		Intent intent = new Intent(this, clazz);
		intent.setFlags(flag);
		intent.putExtras(bundle);
		startActivity(intent);
		finish();
		if (null != this.getParent()) {
			this.getParent().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		} else {
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		}

	}

	/**
	 * 启动带参数传递的Activity
	 * 
	 * @param clazz
	 * @param bundle
	 */
	protected void startActivity(Class<?> clazz, Bundle bundle) {
		Intent intent = new Intent(this, clazz);
		intent.putExtras(bundle);
		startActivity(intent);
		if (null != this.getParent()) {
			this.getParent().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		} else {
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		}
	}

	/**
	 * 启动回调的Activity
	 * 
	 * @param clazz
	 * @param bundle
	 */
	protected void startActivityForResult(Class<?> clazz, int requestCode) {
		Intent intent = new Intent(this, clazz);
		startActivityForResult(intent, requestCode);
		//		overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_out);
		if (null != this.getParent()) {
			this.getParent().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		} else {
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		}

	}

	/**
	 * 设置文本内容
	 * 
	 * @param resourceId
	 * @param value
	 */
	protected void setTextValue(int resourceId, String value) {
		//		((TextView) findViewById(resourceId)).setText(value);
		setTitle(value);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
	}
}
