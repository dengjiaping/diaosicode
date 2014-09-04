package com.itcalf.renhe.context.room;

import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;

import com.itcalf.renhe.R;
import com.itcalf.renhe.RenheApplication;
import com.itcalf.renhe.cache.CacheManager;
import com.itcalf.renhe.context.fragmentMain.MeunFragment;
import com.itcalf.renhe.dto.Profile;
import com.itcalf.renhe.dto.SearchCity;
import com.itcalf.renhe.dto.UnReadMsgNum;

/**
 * Feature: 我的大厅主界面 Desc:我的大厅主界面
 * 
 * @author xp
 * 
 */
public class RoomsActivity extends TabActivity {
	
	// 我的留言按钮
	private Button mMsgBtn;
	// 好友按钮
	private Button mFriendsBtn;
	// 同行按钮
	private Button mColleaguesBtn;
	// 同城按钮
	private Button mCityBtn;
	// 我的关注按钮
	private Button mFollowBtn;
	private TabHost mTabHost;
	//引导图，安装人和网，第一次进入时提示
	private SharedPreferences			msp;
	private SharedPreferences.Editor	mEditor;
	private ImageView guideIv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		RenheApplication.getInstance().addActivity(this);
		setContentView(com.itcalf.renhe.R.layout.rooms_tab);
		initTab();
		initListener();
		initData();
	}
	private void initTab() {
		mTabHost = getTabHost();
		
		Intent intent = new Intent(this, MessageBoardActivity.class);
		intent.putExtra("type", 1);
		mTabHost.addTab(mTabHost.newTabSpec("msg").setIndicator("msg").setContent(intent));
		
		intent = new Intent(this, MessageBoardActivity.class);
		intent.putExtra("type", 2);
		mTabHost.addTab(mTabHost.newTabSpec("friends").setIndicator("friends").setContent(intent));
		
		intent = new Intent(this, MessageBoardActivity.class);
		intent.putExtra("type", 3);
		mTabHost.addTab(mTabHost.newTabSpec("industry").setIndicator("industry").setContent(intent));
		
		intent = new Intent(this, MessageBoardActivity.class);
		intent.putExtra("type", 4);
		mTabHost.addTab(mTabHost.newTabSpec("city").setIndicator("city").setContent(intent));
		
		intent = new Intent(this, AttMessageBoardActivity.class);
		intent.putExtra("type", 5);
		mTabHost.addTab(mTabHost.newTabSpec("follow").setIndicator("follow").setContent(intent));
		
		//初始化为msg选中
		setupUI();
		mTabHost.setCurrentTab(0);
		setCurrentTab(0);
	}
	private void setupUI() {
//		menu = mySlidingMenu.initSlidingMenu();
		mMsgBtn = (Button) findViewById(com.itcalf.renhe.R.id.msg_btn);
		mFriendsBtn = (Button) findViewById(com.itcalf.renhe.R.id.friends_btn);
		mColleaguesBtn = (Button) findViewById(com.itcalf.renhe.R.id.colleagues_btn);
		mCityBtn = (Button) findViewById(com.itcalf.renhe.R.id.city_btn);
		mFollowBtn = (Button) findViewById(com.itcalf.renhe.R.id.follow_btn);
		msp = getSharedPreferences("first_guide_setting_info", 0);
		mEditor = msp.edit();
		guideIv = (ImageView)findViewById(R.id.guide_iv);
		if(msp.getBoolean("ifNeedGuide", true)){
			guideIv.setVisibility(View.VISIBLE);
//			menu.showMenu();
			startActivity(new Intent(this, GuideActivity.class));
		}
	}

	private void initListener() {
		// 监听我的客厅按钮事件
		mMsgBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mTabHost.setCurrentTab(0);
				setCurrentTab(0);
			}
		});
		// 监听好友按钮事件
		mFriendsBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mTabHost.setCurrentTab(1);
				setCurrentTab(1);
			}
		});
		// 监听同行按钮事件
		mColleaguesBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mTabHost.setCurrentTab(2);
				setCurrentTab(2);
			}
		});
		// 监听同城按钮事件
		mCityBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mTabHost.setCurrentTab(3);
				setCurrentTab(3);
			}
		});
		// 监听我的关注按钮事件
		mFollowBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mTabHost.setCurrentTab(4);
				setCurrentTab(4);
			}
		});
		guideIv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				guideIv.setVisibility(View.GONE);
				mEditor.putBoolean("ifNeedGuide", false);
				mEditor.commit();
			}
		});
	}
	//获取未读消息
	private void initData(){
		new AsyncTask<Void, Void, UnReadMsgNum>() {

			@Override
			protected UnReadMsgNum doInBackground(Void... params) {
				try {
					return ((RenheApplication) RoomsActivity.this.getApplicationContext()).
							getMessageBoardCommand().
							unReadNewMsgNum(RenheApplication.getInstance().getUserInfo().getAdSId(),
									RenheApplication.getInstance().getUserInfo().getSid(),
									RoomsActivity.this);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(UnReadMsgNum result) {
				super.onPostExecute(result);
				if (null != result && 1 == result.getState()) {
					Intent intent = new Intent(MeunFragment.ICON_ACTION);
					intent.putExtra("notice_num", result.getNum());
					RoomsActivity.this.sendBroadcast(intent);
					mEditor.putInt("unreadmsg_num", result.getNum());
					mEditor.commit();
				} else {
//					ToastUtil.showErrorToast(RoomsActivity.this, "连接服务器失败！");
				}
			}
		}.execute();
		//获取用户档案、添加这个请求，主要目的是获取用户的所在地，location
		new ProfileTask().execute(RenheApplication.getInstance().getUserInfo().getSid(), RenheApplication.getInstance().getUserInfo().getSid(), RenheApplication.getInstance()
				.getUserInfo().getAdSId());
	}
	private void setCurrentTab(int index) {
		mMsgBtn.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.icon_livingroom),null,null);
		mFriendsBtn.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.icon_friend_clicked),null,null);
		mColleaguesBtn.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.icon_colleagues_clicked),null,null);
		mCityBtn.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.icon_city_clicked),null,null);
		mFollowBtn.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.icon_follow_clicked),null,null);
		
		mMsgBtn.setTextColor(getResources().getColor(R.color.bottom_item_text_color));
		mFriendsBtn.setTextColor(getResources().getColor(R.color.bottom_item_text_color));
		mColleaguesBtn.setTextColor(getResources().getColor(R.color.bottom_item_text_color));
		mCityBtn.setTextColor(getResources().getColor(R.color.bottom_item_text_color));
		mFollowBtn.setTextColor(getResources().getColor(R.color.bottom_item_text_color));
		switch (index) {
		case 0:
//			mMsgBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_current));
			mMsgBtn.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.icon_livingroom_clicked),null,null);
			mMsgBtn.setTextColor(getResources().getColor(R.color.bottom_item_bcg_darkgray));
			break;
		case 1:
//			mFriendsBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_current));
			mFriendsBtn.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.icon_friend),null,null);
			mFriendsBtn.setTextColor(getResources().getColor(R.color.bottom_item_bcg_darkgray));
			break;
		case 2:
//			mColleaguesBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_current));
			mColleaguesBtn.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.icon_colleagues),null,null);
			mColleaguesBtn.setTextColor(getResources().getColor(R.color.bottom_item_bcg_darkgray));
			break;
		case 3:
//			mCityBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_current));
			mCityBtn.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.icon_city),null,null);
			mCityBtn.setTextColor(getResources().getColor(R.color.bottom_item_bcg_darkgray));
			break;
		case 4:
//			mFollowBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_current));
			mFollowBtn.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.icon_follow),null,null);
			mFollowBtn.setTextColor(getResources().getColor(R.color.bottom_item_bcg_darkgray));
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

	@Override
	protected void onStop() {
		super.onPause();
//		menu.showContent();
	}
	/**
	 * 重写finish方法
	 */
	@Override
	public void finish() {
		super.finish();
	}
	class ProfileTask extends AsyncTask<String, Void, Profile> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Profile doInBackground(String... params) {
			try {
				return RenheApplication.getInstance().getProfileCommand().showProfile(params[0], params[1], params[2],
						RenheApplication.getInstance().getApplicationContext());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Profile result) {
			super.onPostExecute(result);
			try {
//				dismissDialog(2);
//				removeDialog(2);
			} catch (Exception e) {
			}
			if (null != result) {
				if (1 == result.getState() && null != result.getUserInfo()) {
					if(result.getUserInfo().getLocation() != null){
						SearchCity searchCity = new SearchCity(-111, result.getUserInfo().getLocation());
						RenheApplication.getInstance().setCurrentCity(searchCity);
						RenheApplication.getInstance().setAccountType(result.getUserInfo().getAccountType());
					}
					CacheManager.getInstance().populateData(RenheApplication.getInstance().getApplicationContext())
					.saveObject(result, RenheApplication.getInstance().getUserInfo().getEmail(), CacheManager.PROFILE);
				}
			} else {
//				ToastUtil.showNetworkError(MyHomeArchivesActivity.this);
			}
		}
	}
}
