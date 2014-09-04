  package com.itcalf.renhe.context.register;

import android.content.Context;
import android.content.Intent;
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

import com.itcalf.renhe.R;
import com.itcalf.renhe.context.portal.ClauseActivity;
import com.itcalf.renhe.context.portal.RegisterActivity;
import com.itcalf.renhe.context.template.ActivityTemplate;
import com.itcalf.renhe.context.template.BaseActivity;
import com.itcalf.renhe.utils.DialogUtil;
import com.itcalf.renhe.utils.RequestDialog;
import com.itcalf.renhe.utils.ToastUtil;
import com.itcalf.renhe.utils.DialogUtil.MyDialogClickListener;
  /**
   * Title: NewRegisterActivity.java<br>
   * Description: <br>
   * Copyright (c) 人和网版权所有 2014    <br>
   * Create DateTime: 2014-6-11 下午1:14:36 <br>
   * @author wangning
   */
public class BindPhoneActivity extends BaseActivity {
	private TextView areaTv;
	private EditText areaHeadTelNumET;
	private EditText userTelET;
	private Button registerBt;
	private TextView clauseTv;
	private String telNum = "";
	private DialogUtil dialogUtil;
	private String contentString = "";
	private EditText mPwdEt;
	private RelativeLayout rootRl;
	private RequestDialog requestDialog;
	private RelativeLayout pwdRl;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new ActivityTemplate().doInActivity(this, R.layout.user_register);
	}
	@Override
	protected void findView() {
		super.findView();
		setTextValue(R.id.title_txt, "绑定手机号码");
		areaTv = (TextView)findViewById(R.id.register_area);
		areaHeadTelNumET = (EditText)findViewById(R.id.register_area_tel_headnum);
		userTelET = (EditText)findViewById(R.id.register_user_telnum);
		registerBt = (Button)findViewById(R.id.registerBt);
		clauseTv = (TextView)findViewById(R.id.clauseTv);
		mPwdEt = (EditText) findViewById(R.id.pwdEt);
		mPwdEt.setVisibility(View.GONE);
		rootRl = (RelativeLayout)findViewById(R.id.registerRl);
		requestDialog = new RequestDialog(this, "正在校验");
		pwdRl = (RelativeLayout)findViewById(R.id.pwRl);
		pwdRl.setVisibility(View.GONE);
	}
	@Override
	protected void initData() {
		super.initData();
		areaTv.setText("中国");
		areaHeadTelNumET.setText("+86");
		dialogUtil = new DialogUtil(this,new MyDialogClickListener() {

			@Override
			public void onclick(int id) {
				if (id == DialogUtil.SURE_BUTTON) {
					new BindPhoneTask(BindPhoneActivity.this){
						public void doPre() {
							requestDialog.addFade(rootRl);
						};
						public void doPost(com.itcalf.renhe.dto.MessageBoardOperation result) {
							requestDialog.removeFade(rootRl);
							if (result == null) {
//								ToastUtil.showErrorToast(BindPhoneActivity.this, "连接服务器失败！");
								dialogUtil.createDialog(BindPhoneActivity.this, "网络异常", "", "确定", "连接服务器失败！");
								
							} else if (result.getState() == 1) {
//								ToastUtil.showToast(NewRegisterActivity.this, "短信验证码发送成功");
								Intent intent = new Intent(BindPhoneActivity.this, BindPhoneAuthActivity.class);
								intent.putExtra("telnum", areaHeadTelNumET.getText().toString().trim() + " " + telNum);
								intent.putExtra("tel", telNum);
								intent.putExtra("deviceinfo", getDeviceInfo());
								startActivityForResult(intent, 1);
								overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
							}else if (result.getState() == -1) {
//								ToastUtil.showErrorToast(BindPhoneActivity.this, "权限不足");
								dialogUtil.createDialog(BindPhoneActivity.this, "验证失败", "", "确定", "发生未知错误");
								
							} else if (result.getState() == -2) {
//								ToastUtil.showErrorToast(BindPhoneActivity.this, "发生未知错误");
								dialogUtil.createDialog(BindPhoneActivity.this, "数据异常", "", "确定", "发生未知错误");
								
							} else if (result.getState() == -3) {
//								ToastUtil.showErrorToast(BindPhoneActivity.this, "手机号码不能为空");
								dialogUtil.createDialog(BindPhoneActivity.this, "手机号码错误", "", "确定", "手机号码不能为空");
							} else if (result.getState() == -4) {
//								ToastUtil.showErrorToast(BindPhoneActivity.this, "设备唯一id不能为空");
							} else if (result.getState() == -5) {
//								ToastUtil.showErrorToast(BindPhoneActivity.this, "手机号码有误，目前仅支持中国大陆地区的手机号码");
								dialogUtil.createDialog(BindPhoneActivity.this, "手机号码错误", "", "确定", "手机号码有误，目前仅支持中国大陆地区的手机号码");
								
							} else if (result.getState() == -6) {
//								ToastUtil.showErrorToast(BindPhoneActivity.this, "手机号码已被其他人和网会员绑定");
								dialogUtil.createDialog(BindPhoneActivity.this, "手机号码错误", "", "确定", "手机号码已被其他人和网会员绑定");
								
							} else if (result.getState() == -7) {
//								ToastUtil.showErrorToast(BindPhoneActivity.this, "短信验证码发送过于频繁，请1分钟后重试");
								dialogUtil.createDialog(BindPhoneActivity.this, "警告", "", "确定", "短信验证码发送过于频繁，请1分钟后重试");
								
							} else if (result.getState() == -8) {
//								ToastUtil.showErrorToast(BindPhoneActivity.this, "短信验证码发送过于频繁，请1小时后重试");
								dialogUtil.createDialog(BindPhoneActivity.this, "警告", "", "确定", "短信验证码发送过于频繁，请1小时后重试");
								
							}else if (result.getState() == -9) {
//								ToastUtil.showErrorToast(BindPhoneActivity.this, "短信验证码发送过于频繁，请1天后重试");
								dialogUtil.createDialog(BindPhoneActivity.this, "警告", "", "确定", "短信验证码发送过于频繁，请1天后重试");
								
							} 
						};
					}.execute(getRenheApplication().getUserInfo().getSid(), getRenheApplication().getUserInfo().getAdSId(), telNum, getDeviceInfo());
					
				}
			}
		});
	}
	@Override
	protected void initListener() {
		super.initListener();
		registerBt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				telNum = userTelET.getText().toString().trim();
				if (telNum.length() == 0) {
//					ToastUtil.showToast(BindPhoneActivity.this, getResources()
//							.getString(R.string.phonenotnull));
					dialogUtil.createDialog(BindPhoneActivity.this, "手机号码错误", "", "确定", getResources().getString(R.string.phonenotnull));
					userTelET.requestFocus();
					return;
				}
				if (telNum.length()>0 && !telNum.matches("^(13[0-9]|15[0-9]|18[0-9])\\d{8}$")) {
//					ToastUtil.showToast(BindPhoneActivity.this, getResources()
//							.getString(R.string.phonerule));
					dialogUtil.createDialog(BindPhoneActivity.this, "手机号码错误", "", "确定", getResources().getString(R.string.phonerule));
					userTelET.requestFocus();
					return;
				}
				//TODO check phone is registered
				
				contentString = getString(R.string.new_register_dialog_content)+"\n"+areaHeadTelNumET.getText().toString().trim()+" "+telNum;
				dialogUtil.createDialog(BindPhoneActivity.this,getString(R.string.new_register_dialog_title),
						getString(R.string.new_register_dialog_button_cancle),getString(R.string.new_register_dialog_button_sure),contentString);
			}
		});
		registerBt.setClickable(false);
		clauseTv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(BindPhoneActivity.this, ClauseActivity.class));
			}
		});
		userTelET.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(userTelET.getText().toString().trim().length() > 0){
					registerBt.setBackgroundResource(R.drawable.red_bt_selected);
					registerBt.setClickable(true);
				}else{
					registerBt.setBackgroundResource(R.drawable.auth_retry_bt_p_shape);
					registerBt.setClickable(false);
				}
			}

			@Override
			public void afterTextChanged(Editable arg0) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
		});
		userTelET.setOnEditorActionListener(new TextView.OnEditorActionListener() {  
			@Override
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
				if(arg1 == EditorInfo.IME_ACTION_DONE){
					registerBt.performClick();
				}
				return true;
			}  
		});
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK && requestCode == 1){
			setResult(RESULT_OK);
			finish();
		}
	}
}

