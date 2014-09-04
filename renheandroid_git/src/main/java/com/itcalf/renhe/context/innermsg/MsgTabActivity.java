package com.itcalf.renhe.context.innermsg;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TabHost;

import com.itcalf.renhe.Constants;
import com.itcalf.renhe.R;
import com.itcalf.renhe.RenheApplication;

public class MsgTabActivity extends TabActivity {
	private Button mInBoxBtn;
	private Button mSendBoxBtn;
	private TabHost mTabHost;
	private boolean isFromNotify = false;
	private SharedPreferences msp;
	private SharedPreferences.Editor mEditor;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(com.itcalf.renhe.R.layout.innermsg_tab);
		RenheApplication.getInstance().addActivity(this);
		initTab();
		initListener();
		if(getIntent().getBooleanExtra("fromNotify", false)){
			isFromNotify = true;
			msp = RenheApplication.getInstance().getSharedPreferences("notify_id", Context.MODE_PRIVATE);
			mEditor = msp.edit();
			mEditor.putInt("notify_num", 1);
			mEditor.commit();
//			Intent intent1 = new Intent(InnerMsgListActivity.NEW_MESSAGE);
//			intent1.putExtra("isFromNotify", isFromNotify);
//			sendBroadcast(intent1);
//			RenheApplication.getInstance().resetNotifyNum();
		}
	}
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		if(intent.getBooleanExtra("fromNotify", false)){
			isFromNotify = true;
			mTabHost.setCurrentTab(0);
			setCurrentTab(0);
			Intent intent1 = new Intent(InnerMsgListActivity.NEW_MESSAGE);
			intent1.putExtra("isFromNotify", isFromNotify);
			sendBroadcast(intent1);
			
			msp = RenheApplication.getInstance().getSharedPreferences("notify_id", Context.MODE_PRIVATE);
			mEditor = msp.edit();
			mEditor.putInt("notify_num", 1);
			mEditor.commit();
//			RenheApplication.getInstance().resetNotifyNum();
		}
	}
	private void initTab() {
		mTabHost = getTabHost();
		Intent intent = new Intent(this, InnerMsgListActivity.class);
		intent.putExtra("type", 1);
		mTabHost.addTab(mTabHost.newTabSpec("inbox").setIndicator("inbox")
				.setContent(intent));

		intent = new Intent(this, InnerMsgListActivity.class);
		intent.putExtra("type", 2);
		mTabHost.addTab(mTabHost.newTabSpec("sendbox").setIndicator("sendbox")
				.setContent(intent));
		setupUI();
		mTabHost.setCurrentTab(0);
		setCurrentTab(0);
	}

	private void setupUI() {
//		menu = mySlidingMenu.initSlidingMenu();
		mInBoxBtn = (Button) findViewById(com.itcalf.renhe.R.id.inbox_btn);
		mSendBoxBtn = (Button) findViewById(com.itcalf.renhe.R.id.sendbox_btn);
	}

	private void initListener() {
		mInBoxBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mTabHost.setCurrentTab(0);
				setCurrentTab(0);
			}
		});
		mSendBoxBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mTabHost.setCurrentTab(1);
				setCurrentTab(1);
			}
		});
	}

	private void setCurrentTab(int index) {
//		mInBoxBtn.setBackgroundDrawable(getResources().getDrawable(
//				R.drawable.roomsbottombtn_selected));
		mInBoxBtn.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.icon_inbox), null, null);
		mSendBoxBtn.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.icon_send), null, null);
		
		mInBoxBtn.setTextColor(getResources().getColor(R.color.bottom_item_text_color));
		mSendBoxBtn.setTextColor(getResources().getColor(R.color.bottom_item_text_color));
//		mSendBoxBtn.setBackgroundDrawable(getResources().getDrawable(
//				R.drawable.roomsbottombtn_selected));

		switch (index) {
		case 0:
//			mInBoxBtn.setBackgroundDrawable(getResources().getDrawable(
//					R.drawable.tab_current));
			mInBoxBtn.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.icon_inbox_clicked), null, null);
			mInBoxBtn.setTextColor(getResources().getColor(R.color.bottom_item_bcg_darkgray));
			break;
		case 1:
//			mSendBoxBtn.setBackgroundDrawable(getResources().getDrawable(
//					R.drawable.tab_current));
			mSendBoxBtn.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.icon_send_clicked), null, null);
			mSendBoxBtn.setTextColor(getResources().getColor(R.color.bottom_item_bcg_darkgray));
			break;
		}
	}
	
}
