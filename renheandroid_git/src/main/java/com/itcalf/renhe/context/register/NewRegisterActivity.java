package com.itcalf.renhe.context.register;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.itcalf.renhe.R;
import com.itcalf.renhe.context.portal.ClauseActivity;
import com.itcalf.renhe.context.portal.OldRegisterActivity;
import com.itcalf.renhe.context.template.ActivityTemplate;
import com.itcalf.renhe.context.template.BaseActivity;
import com.itcalf.renhe.utils.DialogUtil;
import com.itcalf.renhe.utils.DialogUtil.MyDialogClickListener;
import com.itcalf.renhe.utils.RequestDialog;

/**
 * Title: NewRegisterActivity.java<br>
 * Description: <br>
 * Copyright (c) 人和网版权所有 2014    <br>
 * Create DateTime: 2014-6-9 下午1:14:36 <br>
 * @author wangning
 */
public class NewRegisterActivity extends BaseActivity {
	private TextView areaTv;
	private EditText areaHeadTelNumET;
	private EditText userTelET;
	private EditText mPwdEt;
	private Button registerBt;
	private TextView clauseTv;
	private String telNum = "";
	private String pwd = "";
	private DialogUtil dialogUtil;
	private String contentString = "";
	private RelativeLayout rootRl;
	private RequestDialog requestDialog;
	public static final String FINISHLOGINACTIVITY_1 = "com.renhe.finishloginactivity1";
	public static final String FINISHLOGINACTIVITY_2 = "com.renhe.finishloginactivity2";
	private FinishSelfReceiver finishSelfReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new ActivityTemplate().doInActivity(this, R.layout.user_register);
	}

	@Override
	protected void findView() {
		super.findView();
		setTextValue(R.id.title_txt, "人和网新用户注册");
		areaTv = (TextView) findViewById(R.id.register_area);
		areaHeadTelNumET = (EditText) findViewById(R.id.register_area_tel_headnum);
		userTelET = (EditText) findViewById(R.id.register_user_telnum);
		mPwdEt = (EditText) findViewById(R.id.pwdEt);
		registerBt = (Button) findViewById(R.id.registerBt);
		clauseTv = (TextView) findViewById(R.id.clauseTv);
		rootRl = (RelativeLayout)findViewById(R.id.registerRl);
		requestDialog = new RequestDialog(this, "正在校验");
	}
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem registerItem = menu.findItem(R.id.item_save);
		registerItem.setVisible(true);
		registerItem.setTitle("邮箱注册");
		return super.onPrepareOptionsMenu(menu);
	}
	@Override
	protected void initData() {
		super.initData();
		areaTv.setText("中国");
		areaHeadTelNumET.setText("+86");
		dialogUtil = new DialogUtil(this, new MyDialogClickListener() {

			@Override
			public void onclick(int id) {
				if (id == DialogUtil.SURE_BUTTON) {
					new NewRegisterTask(NewRegisterActivity.this){
						public void doPre() {
							requestDialog.addFade(rootRl);
						};
						public void doPost(com.itcalf.renhe.dto.MessageBoardOperation result) {
							requestDialog.removeFade(rootRl);
							if (result == null) {
								dialogUtil.createDialog(NewRegisterActivity.this, "网络异常", "", "确定", "连接服务器失败！");
//								ToastUtil.showErrorToast(NewRegisterActivity.this, "连接服务器失败！");
							} else if (result.getState() == 1) {
//								ToastUtil.showToast(NewRegisterActivity.this, "短信验证码发送成功");
								Intent intent = new Intent(NewRegisterActivity.this, AuthActivity.class);
								intent.putExtra("telnum", areaHeadTelNumET.getText().toString().trim() + " " + telNum);
								intent.putExtra("tel", telNum);
								intent.putExtra("pwd", pwd);
								intent.putExtra("deviceinfo", getDeviceInfo());
								startActivity(intent);
								overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
							}else if(result.getState() == 2){
//								ToastUtil.showToast(NewRegisterActivity.this, "您的手机号码  "+telNum+" 以前注册过，输入验证码后，人和网密码为你刚才设置的密码");
								Intent intent = new Intent(NewRegisterActivity.this, AuthActivity.class);
								intent.putExtra("telnum", areaHeadTelNumET.getText().toString().trim() + " " + telNum);
								intent.putExtra("tel", telNum);
								intent.putExtra("pwd", pwd);
								intent.putExtra("deviceinfo", getDeviceInfo());
								intent.putExtra("warn", getString(R.string.warn));
								startActivity(intent);
								overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
							}else if (result.getState() == -1) {
//								ToastUtil.showErrorToast(NewRegisterActivity.this, "手机号码不能为空");
								dialogUtil.createDialog(NewRegisterActivity.this, "手机号码错误", "", "确定", "手机号码不能为空");
							} else if (result.getState() == -2) {
//								ToastUtil.showErrorToast(NewRegisterActivity.this, "密码不能为空");
								dialogUtil.createDialog(NewRegisterActivity.this, "密码错误", "", "确定", "密码不能为空");
							} else if (result.getState() == -3) {
//								ToastUtil.showErrorToast(NewRegisterActivity.this, "设备唯一id信息不能为空");
								dialogUtil.createDialog(NewRegisterActivity.this, "验证失败", "", "确定", "数据异常，请退出重试");
							} else if (result.getState() == -4) {
//								ToastUtil.showErrorToast(NewRegisterActivity.this, "手机号码格式有误，目前进支持大陆地区用户注册");
								dialogUtil.createDialog(NewRegisterActivity.this, "手机号码错误", "", "确定", "手机号码格式有误，目前进支持大陆地区用户注册");
							} else if (result.getState() == -5) {
//								ToastUtil.showErrorToast(NewRegisterActivity.this, "手机号码已被注册，请尝试换一个手机号码注册");
								dialogUtil.createDialog(NewRegisterActivity.this, "手机号码错误", "", "确定", "手机号码已被注册，请尝试换一个手机号码注册");
							} else if (result.getState() == -6) {
//								ToastUtil.showErrorToast(NewRegisterActivity.this, "短信验证码发送过于频繁，请1分钟后重试");
								dialogUtil.createDialog(NewRegisterActivity.this, "警告", "", "确定", "短信验证码发送过于频繁，请1分钟后重试");
							} else if (result.getState() == -7) {
//								ToastUtil.showErrorToast(NewRegisterActivity.this, "短信验证码发送过于频繁，请1小时后重试");
								dialogUtil.createDialog(NewRegisterActivity.this, "警告", "", "确定", "短信验证码发送过于频繁，请1小时后重试");
							} else if (result.getState() == -8) {
//								ToastUtil.showErrorToast(NewRegisterActivity.this, "短信验证码发送过于频繁，请1天后重试");
								dialogUtil.createDialog(NewRegisterActivity.this, "警告", "", "确定", "短信验证码发送过于频繁，请1天后重试");
							} 
						};
					}.execute(telNum, pwd, getDeviceInfo());
					
				}
			}
		});
		finishSelfReceiver = new FinishSelfReceiver();
		IntentFilter intentFilter = new IntentFilter(FINISHLOGINACTIVITY_2);
		intentFilter.addAction(FINISHLOGINACTIVITY_2);
		registerReceiver(finishSelfReceiver, intentFilter);
	}
	/**
	 * 设备的唯一id，用于做短信验证码的发送数量控制
	 * @return
	 */
	private String getDeviceInfo(){
		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getDeviceId();
	}
	@Override
	protected void initListener() {
		super.initListener();
		registerBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				telNum = userTelET.getText().toString().trim();
				pwd = mPwdEt.getText().toString().trim();
				if (telNum.length() == 0) {
//					ToastUtil.showToast(NewRegisterActivity.this, getResources().getString(R.string.phonenotnull));
					dialogUtil.createDialog(NewRegisterActivity.this, "手机号码错误", "", "确定", getResources().getString(R.string.phonenotnull));
					userTelET.requestFocus();
					return;
				}
				if (telNum.length() > 0 && !telNum.matches("^(13[0-9]|15[0-9]|18[0-9])\\d{8}$")) {
//					ToastUtil.showToast(NewRegisterActivity.this, getResources().getString(R.string.phonerule));
					dialogUtil.createDialog(NewRegisterActivity.this, "手机号码错误", "", "确定", getResources().getString(R.string.phonerule));
					userTelET.requestFocus();
					return;
				}
				if (pwd.length() == 0) {
//					ToastUtil.showToast(NewRegisterActivity.this, getResources().getString(R.string.passwordnotnull));
					dialogUtil.createDialog(NewRegisterActivity.this, "密码错误", "", "确定", getResources().getString(R.string.passwordnotnull));
					mPwdEt.requestFocus();
					return;
				}
				if (pwd.length() < 6 || pwd.length() > 16 || !pwd.matches("^\\w+$")) {
//					ToastUtil.showToast(NewRegisterActivity.this, getResources().getString(R.string.passwordrule));
					dialogUtil.createDialog(NewRegisterActivity.this, "密码错误", "", "确定", getResources().getString(R.string.passwordrule));
					mPwdEt.requestFocus();
					return;
				}
				//TODO check phone is registered
				
				contentString = getString(R.string.new_register_dialog_content) + "\n"
						+ areaHeadTelNumET.getText().toString().trim() + " " + telNum;
				dialogUtil.createDialog(NewRegisterActivity.this, getString(R.string.new_register_dialog_title),
						getString(R.string.new_register_dialog_button_cancle),
						getString(R.string.new_register_dialog_button_sure), contentString);
			}
		});
		registerBt.setClickable(false);
		clauseTv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(NewRegisterActivity.this, ClauseActivity.class));
			}
		});
		userTelET.addTextChangedListener(new MTextWacher());
		mPwdEt.addTextChangedListener(new MTextWacher());
		userTelET.setOnEditorActionListener(new MOnEditorActionListener());
		mPwdEt.setOnEditorActionListener(new MOnEditorActionListener());
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.item_save:
			startActivity(OldRegisterActivity.class);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	class MTextWacher implements TextWatcher {

		@Override
		public void afterTextChanged(Editable arg0) {

		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

		}

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			if (userTelET.getText().toString().trim().length() > 0 && mPwdEt.getText().toString().trim().length() > 0
					) {
				registerBt.setBackgroundResource(R.drawable.red_bt_selected);
				registerBt.setClickable(true);
			} else {
				registerBt.setBackgroundResource(R.drawable.auth_retry_bt_p_shape);
				registerBt.setClickable(false);
			}

		}

	}

	class MOnEditorActionListener implements OnEditorActionListener {

		@Override
		public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
			if (arg1 == EditorInfo.IME_ACTION_DONE) {
				registerBt.performClick();
			}
			return true;
		}

	}
	class FinishSelfReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			if(null != arg1.getAction() && (arg1.getAction().equals(FINISHLOGINACTIVITY_2) || arg1.getAction().equals(FINISHLOGINACTIVITY_1))){
				finish();
			}
		}
		
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(null != finishSelfReceiver){
			unregisterReceiver(finishSelfReceiver);
		}
	}
}
