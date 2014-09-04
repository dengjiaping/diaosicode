package com.itcalf.renhe.context.fragmentMain;

import java.lang.reflect.Field;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.hp.hpl.sparta.xpath.ThisNodeTest;
import com.itcalf.renhe.R;
import com.itcalf.renhe.RenheApplication;
import com.itcalf.renhe.cache.CacheManager;
import com.itcalf.renhe.utils.AsyncImageLoader;
import com.itcalf.renhe.utils.LayoutToDrawable;
import com.itcalf.renhe.utils.ToastUtil;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.umeng.analytics.MobclickAgent;
import com.viewpagerindicator.TabPageIndicator;

public class MainFragment extends SlidingFragmentActivity {
	private TabPageIndicator indicator;
	private static final String[] TITLE = { "我的客厅", "朋友", "同行", "同城", "最受关注" };
	private android.support.v4.app.ActionBarDrawerToggle mDrawerToggle;
	private LayoutToDrawable layoutToDrawable;
	private Drawable mDrawable;
	private View view;
	private Button noticeBt;
	private static TextView titleTv;
	private SharedPreferences msp;
	private SharedPreferences.Editor mEditor;
	private SharedPreferences msp1;
	private SharedPreferences.Editor mEditor1;
	private NoticeReceiver noticeReceiver;
	private boolean isFromNotify = false;
	private boolean logoutFlag = false;
	private Fragment mCurrentFragment;
	private RoomsFragment roomsFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		RenheApplication.getInstance().addActivity(this);
		setOverflowShowingAlways();
		msp = getSharedPreferences("setting_info", 0);
		mEditor = msp.edit();

		//		getSupportActionBar().setIcon(mDrawable);
		//		customActionBarHome(getActionBar());
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		getSupportActionBar().setDisplayShowCustomEnabled(true);
		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		view = LayoutInflater.from(this).inflate(R.layout.custon_actionbar_logo, null);
		noticeBt = (Button) view.findViewById(R.id.title_button);
		titleTv = (TextView) view.findViewById(R.id.title_text);

		ActionBar.LayoutParams params = new ActionBar.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT,
				Gravity.LEFT);
		getSupportActionBar().setCustomView(view, params);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			//			view.findViewById(R.id.home).setBackgroundResource(android.R.attr.actionBarItemBackground);
		}
		view.findViewById(R.id.home).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				toggle();
			}
		});

		setSlidingActionBarEnabled(true);
		initSlidingMenu();
		if (getIntent().getBooleanExtra("fromNotify", false)) {
			msp1 = RenheApplication.getInstance().getSharedPreferences("notify_id", Context.MODE_PRIVATE);
			mEditor1 = msp1.edit();
			mEditor1.putInt("notify_num", 1);
			mEditor1.commit();
			//			getSupportFragmentManager().beginTransaction().replace(R.id.rel, new InnerMsgsFragment(),"InnerMsgsFragment").commit();
			Intent intent = new Intent(MeunFragment.INIT_CURRENTFRAGMENT_ACTION);
			intent.putExtra("currentfragment", "InnerMsgsFragment");
			sendBroadcast(intent);
		}

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		if (intent.getBooleanExtra("fromNotify", false)) {
			msp1 = RenheApplication.getInstance().getSharedPreferences("notify_id", Context.MODE_PRIVATE);
			mEditor1 = msp1.edit();
			mEditor1.putInt("notify_num", 1);
			mEditor1.commit();
			//			getSupportFragmentManager().beginTransaction().replace(R.id.rel, new InnerMsgsFragment()).commit();
			Intent intent1 = new Intent(MeunFragment.INIT_CURRENTFRAGMENT_ACTION);
			intent1.putExtra("currentfragment", "InnerMsgsFragment");
			sendBroadcast(intent1);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	public static void switchTitle(String text) {
		if (!TextUtils.isEmpty(text)) {
			titleTv.setText(text);
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		int noticeNum = msp.getInt("unreadmsg_num", 0);
		//站内信数目提醒
		int newMsgNoticeNum = msp.getInt("newmsg_unreadmsg_num", 0);
		int sumNum = noticeNum + newMsgNoticeNum;
		if (sumNum > 0) {
			noticeBt.setVisibility(View.VISIBLE);
			noticeBt.setText(sumNum + "");
		} else {
			noticeBt.setVisibility(View.GONE);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// toggle���ǳ����Զ��ж��Ǵ򿪻��ǹر�
			toggle();
			// getSlidingMenu().showMenu();// show menu
			// getSlidingMenu().showContent();//show content
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	//	@Override
	//	public Dialog onCreateDialog(int id) {
	//		switch (id) {
	//		case 1:
	//			ProgressDialog findPd = new ProgressDialog(this);
	//			findPd.setMessage("数据加载中...");
	//			//			findPd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	//			findPd.setCanceledOnTouchOutside(false);
	//			return findPd;
	//		default:
	//			return null;
	//		}
	//	}
	private void setOverflowShowingAlways() {
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			menuKeyField.setAccessible(true);
			menuKeyField.setBoolean(config, false);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initSlidingMenu() {
		// TODO Auto-generated method stub
		SlidingMenu mSlidingMenu = getSlidingMenu();
		setBehindContentView(R.layout.content_frame);
		MeunFragment menuFragment = new MeunFragment(getSupportFragmentManager(), mSlidingMenu, this);

		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.replace(R.id.content_frame, menuFragment, "MeunFragment");
		transaction.commit();
		mSlidingMenu.setMode(SlidingMenu.LEFT);// 
		mSlidingMenu.setTouchModeAbove(SlidingMenu.LEFT);
		mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		mSlidingMenu.setFadeEnabled(true);
		mSlidingMenu.setFadeDegree(0.5f);

		mSlidingMenu.setShadowDrawable(R.drawable.shadow);
		mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
		//		mSlidingMenu.setBehindOffset((int) (width * 0.15));//设置SlidingMenu菜单的宽度  
		//		mSlidingMenu.setFadeDegree(0.35f);
		mSlidingMenu.setMenu(R.layout.content_frame);
		//注册消息提醒广播
		noticeReceiver = new NoticeReceiver();
		IntentFilter intentFilter = new IntentFilter(MeunFragment.ICON_ACTION);
		intentFilter.addAction(MeunFragment.NEWMSG_ICON_ACTION);
		registerReceiver(noticeReceiver, intentFilter);
	}

	class NoticeReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(MeunFragment.ICON_ACTION) || intent.getAction().equals(MeunFragment.NEWMSG_ICON_ACTION)) {
				getWindow().invalidatePanelMenu(Window.FEATURE_OPTIONS_PANEL);
			}

		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (null != noticeReceiver) {
			unregisterReceiver(noticeReceiver);
			noticeReceiver = null;
		}
	}

	Handler handler = new Handler();
	Runnable run = new Runnable() {
		@Override
		public void run() {
			logoutFlag = false;
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			try {
				//			if (logoutFlag) {
				//				AsyncImageLoader.getInstance().clearCache();
				//				if (getSharedPreferences("setting_info", 0).getBoolean("clearcache", false)) {
				//					CacheManager.getInstance().populateData(this).clearCache(RenheApplication.getInstance().getUserInfo().getEmail());
				//				}
				//				RenheApplication.getInstance().exit();
				//			} else {
				//				ToastUtil.showToast(this, "请再点击一次退出程序!");
				//				logoutFlag = true;
				//				handler.postDelayed(run, 2000);
				//			}
				MeunFragment mFragment;
				Fragment menuFragment = getSupportFragmentManager().findFragmentByTag("MeunFragment");
				if (menuFragment != null && menuFragment instanceof MeunFragment) {
					mFragment = (MeunFragment) menuFragment;
					mFragment.myOnKeyDown(keyCode);
				}
			} catch (Exception e) {
			}
			return true;
		}
		return super.onKeyDown(keyCode, event); // 最后，一定要做完以后返回
	}
}
