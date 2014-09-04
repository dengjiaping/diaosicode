package com.itcalf.renhe.context.more;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;

import com.itcalf.renhe.R;
import com.itcalf.renhe.context.innermsg.SwipeBackActivity;
import com.itcalf.renhe.context.template.ActivityTemplate;
import com.itcalf.renhe.view.SwitchButton;

public class SettingNewMsgActivity extends SwipeBackActivity implements OnCheckedChangeListener {
	private SwitchButton msgNotifyCB;
	private SwitchButton msgSoundCB;
	private SwitchButton msgShakeCB;
	private SwitchButton LedCB;
	private LinearLayout notifySubItemLl;
	SharedPreferences userInfo;
	Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new ActivityTemplate().doInActivity(this, R.layout.setting_newmsg);
	}

	@Override
	protected void findView() {
		super.findView();
		userInfo = getSharedPreferences("setting_info", 0);
		editor = userInfo.edit();
		msgNotifyCB = (SwitchButton) findViewById(R.id.message_notify_cb);
		msgSoundCB = (SwitchButton) findViewById(R.id.sound_cb);
		msgShakeCB = (SwitchButton) findViewById(R.id.warnshake_cb);
		LedCB = (SwitchButton) findViewById(R.id.led_cb);
		notifySubItemLl = (LinearLayout)findViewById(R.id.notify_subitem_ll);
		if (userInfo.getBoolean("msgnotify", true)) {
			msgNotifyCB.setChecked(true);
			notifySubItemLl.setVisibility(View.VISIBLE);
		} else {
			msgNotifyCB.setChecked(false);
			notifySubItemLl.setVisibility(View.GONE);
		}
		if (userInfo.getBoolean("sound", true)) {
			msgSoundCB.setChecked(true);
		} else {
			msgSoundCB.setChecked(false);
		}
		if (userInfo.getBoolean("warnshake", true)) {
			msgShakeCB.setChecked(true);
		} else {
			msgShakeCB.setChecked(false);
		}
		if (userInfo.getBoolean("led", true)) {
			LedCB.setChecked(true);
		} else {
			LedCB.setChecked(false);
		}
	}

	@Override
	protected void initData() {
		super.initData();
		setTextValue(R.id.title_txt, "新消息提醒");
	}

	@Override
	protected void initListener() {
		super.initListener();
		msgNotifyCB.setOnCheckedChangeListener(this);
		msgSoundCB.setOnCheckedChangeListener(this);
		msgShakeCB.setOnCheckedChangeListener(this);
		LedCB.setOnCheckedChangeListener(this);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.message_notify_cb:
			if (isChecked) {
				editor.putBoolean("msgnotify", true);
				notifySubItemLl.setVisibility(View.VISIBLE);
			} else {
				editor.putBoolean("msgnotify", false);
				notifySubItemLl.setVisibility(View.GONE);
			}
			break;
		case R.id.sound_cb:
			if (isChecked) {
				editor.putBoolean("sound", true);
			} else {
				editor.putBoolean("sound", false);
			}
			break;
		case R.id.warnshake_cb:
			if (isChecked) {
				editor.putBoolean("warnshake", true);
			} else {
				editor.putBoolean("warnshake", false);
			}
			break;
		case R.id.led_cb:
			if (isChecked) {
				editor.putBoolean("led", true);
			} else {
				editor.putBoolean("led", false);
			}
			break;
		default:
			break;
		}
		editor.commit();
	}
}
