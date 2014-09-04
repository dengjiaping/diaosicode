package com.itcalf.renhe.context.register;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.view.MenuItem;
import com.itcalf.renhe.R;
import com.itcalf.renhe.context.template.ActivityTemplate;
import com.itcalf.renhe.context.template.BaseActivity;
import com.itcalf.renhe.utils.CustomDialogUtilOfJustText;
import com.itcalf.renhe.utils.CustomDialogUtilOfJustText.CustomDialogClickListener;
import com.itcalf.renhe.utils.DialogUtil;
import com.itcalf.renhe.utils.DialogUtil.MyDialogClickListener;
import com.itcalf.renhe.utils.RequestDialog;

/**
 * Title: BindPhoneAuthActivity.java<br>
 * Description: <br>
 * Copyright (c) 人和网版权所有 2014    <br>
 * Create DateTime: 2014-6-9 下午1:14:36 <br>
 * @author wangning
 */
public class BindPhoneAuthActivity extends BaseActivity {
	private RelativeLayout authLl;
	private TextView telTv;
	private EditText authET;
	private Button sureBt;
	private Button retryBt;
	private String telNum = "";
	private String tel = "";
	private String authCode = "";
	private final static int RETRYTIME = 120;
	private int count = RETRYTIME; //120s倒计时
	private Timer timer;
	private MyTimerTask myTimerTask;
	private RequestDialog requestDialog;
	private DialogUtil dialogUtil;
	private TextView warnTv;
	private String warnString = "";
	private String deviceInfo = "";
	private TextView notReceiveCode;
	private CustomDialogUtilOfJustText customDialogUtilOfJustText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new ActivityTemplate().doInActivity(this, R.layout.user_auth);
	}

	@Override
	protected void findView() {
		super.findView();
		setTextValue(R.id.title_txt, "填写验证码");
		authLl = (RelativeLayout) findViewById(R.id.auth_ll);
		telTv = (TextView) findViewById(R.id.auth_tel_tv);
		authET = (EditText) findViewById(R.id.auth_code_et);
		sureBt = (Button) findViewById(R.id.sureBt);
		retryBt = (Button) findViewById(R.id.retryBt);
		warnTv = (TextView) findViewById(R.id.auth_warn);
		notReceiveCode = (TextView) findViewById(R.id.code_tv);
		//		SpannableString desc = new SpannableString(descTv.getText().toString().trim());
		//		desc.setSpan(new BackgroundColorSpan(android.R.color.transparent), 5, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		//		desc.setSpan(new ForegroundColorSpan(R.color.black), 5, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		//		desc.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 5, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		//		descTv.setText(desc);

	}

	@Override
	protected void initData() {
		super.initData();
		telNum = getIntent().getStringExtra("telnum");
		tel = getIntent().getStringExtra("tel");
		warnString = getIntent().getStringExtra("warn");
		deviceInfo = getIntent().getStringExtra("deviceinfo");
		if (null != warnString && !"".equals(warnString)) {
			//			warnTv.setVisibility(View.VISIBLE);
			warnTv.setText(warnString);
		} else {
			//			warnTv.setVisibility(View.GONE);
		}
		telTv.setText(telNum);
		timer = new Timer();
		myTimerTask = new MyTimerTask();
		timer.schedule(myTimerTask, 500, 1000);
		requestDialog = new RequestDialog(this, "验证中");
		dialogUtil = new DialogUtil(this, new MyDialogClickListener() {

			@Override
			public void onclick(int id) {
				if (id == DialogUtil.SURE_BUTTON) {
					finish();
					overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
				}
			}
		});
		customDialogUtilOfJustText = new CustomDialogUtilOfJustText(this, new CustomDialogClickListener() {

			@Override
			public void onclick(int id) {
				if (id == CustomDialogUtilOfJustText.SURE_BUTTON) {
					new BindPhoneTask(BindPhoneAuthActivity.this) {
						public void doPre() {
						};

						public void doPost(com.itcalf.renhe.dto.MessageBoardOperation result) {
							if (result == null) {
//								ToastUtil.showErrorToast(BindPhoneAuthActivity.this, "连接服务器失败！");
								dialogUtil.createDialog(BindPhoneAuthActivity.this, "网络异常", "", "确定", "连接服务器失败！");
								
								return;
							} else if (result.getState() == 1) {
								//								ToastUtil.showToast(NewRegisterActivity.this, "短信验证码发送成功");
							} else if (result.getState() == -1) {
								//								ToastUtil.showErrorToast(BindPhoneActivity.this, "权限不足");
								dialogUtil.createDialog(BindPhoneAuthActivity.this, "验证失败", "", "确定", "发生未知错误，请返回前一步重新输入");
								
								return;
							} else if (result.getState() == -2) {
//								ToastUtil.showErrorToast(BindPhoneAuthActivity.this, "发生未知错误");
								dialogUtil.createDialog(BindPhoneAuthActivity.this, "验证失败", "", "确定", "发生未知错误，请退出重试");
								
								return;
							} else if (result.getState() == -3) {
								//								ToastUtil.showErrorToast(BindPhoneAuthActivity.this, "手机号码不能为空");
								dialogUtil.createDialog(BindPhoneAuthActivity.this, "手机号码错误", "", "确定", "手机号码不能为空，请返回前一步重新输入");
								
								return;
							} else if (result.getState() == -4) {
								//								ToastUtil.showErrorToast(BindPhoneActivity.this, "设备唯一id不能为空");
								dialogUtil.createDialog(BindPhoneAuthActivity.this, "验证失败", "", "确定", "数据异常，请退出重试");
								
								return;
							} else if (result.getState() == -5) {
//								ToastUtil.showErrorToast(BindPhoneAuthActivity.this, "手机号码有误，目前仅支持中国大陆地区的手机号码");
								dialogUtil.createDialog(BindPhoneAuthActivity.this, "手机号码错误", "", "确定", "手机号码格式有误，目前进支持大陆地区用户注册");
								
								return;
							} else if (result.getState() == -6) {
//								ToastUtil.showErrorToast(BindPhoneAuthActivity.this, "手机号码已被其他人和网会员绑定");
								dialogUtil.createDialog(BindPhoneAuthActivity.this, "手机号码错误", "", "确定", "手机号码已被其他人和网会员绑定");
								
								return;
							} else if (result.getState() == -7) {
//								ToastUtil.showErrorToast(BindPhoneAuthActivity.this, "短信验证码发送过于频繁，请1分钟后重试");
								dialogUtil.createDialog(BindPhoneAuthActivity.this, "警告", "", "确定", "短信验证码发送过于频繁，请1分钟后重试");
								
								return;
							} else if (result.getState() == -8) {
//								ToastUtil.showErrorToast(BindPhoneAuthActivity.this, "短信验证码发送过于频繁，请1小时后重试");
								dialogUtil.createDialog(BindPhoneAuthActivity.this, "警告", "", "确定", "短信验证码发送过于频繁，请1小时后重试");
								
								return;
							} else if (result.getState() == -9) {
//								ToastUtil.showErrorToast(BindPhoneAuthActivity.this, "短信验证码发送过于频繁，请1天后重试");
								dialogUtil.createDialog(BindPhoneAuthActivity.this, "警告", "", "确定", "短信验证码发送过于频繁，请1天后重试");
								
								return;
							}
							count = RETRYTIME;
							timer = new Timer();
							myTimerTask = new MyTimerTask();
							timer.schedule(myTimerTask, 1000, 1000);
						};
					}.execute(getRenheApplication().getUserInfo().getSid(), getRenheApplication().getUserInfo().getAdSId(), tel,
							deviceInfo);

				}
			}
		});
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case android.R.id.home:
			dialogUtil.createDialog(BindPhoneAuthActivity.this, "", getString(R.string.auth_dialog_cancle),
					getString(R.string.auth_dialog_sure), getString(R.string.auth_click_back));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	protected void initListener() {
		super.initListener();
		sureBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (!TextUtils.isEmpty(authET.getText().toString().trim())) {
					sureBt.setClickable(false);
					requestDialog.addFade(authLl);
					checkAuthCode(authET.getText().toString().trim());
				}
			}
		});
		authET.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (!TextUtils.isEmpty(authET.getText().toString().trim())) {
					activateButton(sureBt);
				} else {
					nagativeButton(sureBt);
				}
			}

			@Override
			public void afterTextChanged(Editable arg0) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
		});
		authET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
				if (arg1 == EditorInfo.IME_ACTION_DONE) {
					sureBt.performClick();
				}
				return true;
			}
		});
		notReceiveCode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				customDialogUtilOfJustText.createDialog(BindPhoneAuthActivity.this,
						getString(R.string.auth_code_receive_one_more_time));
			}
		});
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {
				if (count > 0) {
					//					nagativeButton(retryBt);
					notReceiveCode.setText("接收短信大约需要" + count-- + "秒钟");
					notReceiveCode.setClickable(false);
					notReceiveCode.setTextColor(getResources().getColor(R.color.black));
				} else {
					if (null != timer) {
						timer.cancel();
						timer = null;
					}
					if (null != myTimerTask) {
						myTimerTask.cancel();
						myTimerTask = null;
					}
					notReceiveCode.setText("收不到验证码？");
					notReceiveCode.setClickable(true);
					notReceiveCode.setTextColor(getResources().getColor(R.color.auth_desc));
					//					activateButton(retryBt);
				}
			}
		};
	};

	class MyTimerTask extends TimerTask {

		@Override
		public void run() {
			Message message = new Message();
			message.what = 1;
			handler.sendMessage(message);
		}

	}

	private void activateButton(Button button) {
		if (button.getId() == R.id.sureBt) {
			button.setBackgroundResource(R.drawable.red_bt_selected);
		}
		if (button.getId() == R.id.retryBt) {
			button.setBackgroundResource(R.drawable.auth_retry_bt_aft_shape);
		}
		button.setClickable(true);
	}

	private void nagativeButton(Button button) {
		button.setClickable(false);
		if (button.getId() == R.id.sureBt) {
			button.setBackgroundResource(R.drawable.auth_retry_bt_p_shape);
		}
		if (button.getId() == R.id.retryBt) {
			button.setBackgroundResource(R.drawable.auth_retry_bt_pre_shape);
		}
	}

	private void checkAuthCode(String code) {
		if (null != code && !"".equals(code)) {
			//如果验证成功
			new BindCheckCodeTask(BindPhoneAuthActivity.this) {
				public void doPre() {
				};

				public void doPost(com.itcalf.renhe.dto.MessageBoardOperation result) {
					requestDialog.removeFade(authLl);
					if (result == null) {
//						ToastUtil.showErrorToast(BindPhoneAuthActivity.this, "连接服务器失败！");
						dialogUtil.createDialog(BindPhoneAuthActivity.this, "网络异常", "", "确定", "连接服务器失败！");
						
					} else if (result.getState() == 1) {
						//						ToastUtil.showToast(BindPhoneAuthActivity.this, "短信验证码验证成功");
						setResult(RESULT_OK);
						finish();
					} else if (result.getState() == -1) {
						//						ToastUtil.showErrorToast(BindPhoneAuthActivity.this, "权限不足");
						dialogUtil.createDialog(BindPhoneAuthActivity.this, "验证失败", "", "确定", "发生未知错误，请返回前一步重新输入");
						
					} else if (result.getState() == -2) {
//						ToastUtil.showErrorToast(BindPhoneAuthActivity.this, "发生未知错误");
						dialogUtil.createDialog(BindPhoneAuthActivity.this, "验证失败", "", "确定", "发生未知错误");
						
					} else if (result.getState() == -3) {
//						ToastUtil.showErrorToast(BindPhoneAuthActivity.this, "手机号码不能为空");
						dialogUtil.createDialog(BindPhoneAuthActivity.this, "手机号码错误", "", "确定", "手机号码不能为空，请返回前一步重新输入");
						
					} else if (result.getState() == -4) {
//						ToastUtil.showErrorToast(BindPhoneAuthActivity.this, "验证码不能为空");
						dialogUtil.createDialog(BindPhoneAuthActivity.this, "验证码错误", "", "确定", "验证码不能为空");
						
					} else if (result.getState() == -5) {
//						ToastUtil.showErrorToast(BindPhoneAuthActivity.this, "此手机号码没有申请过绑定验证码");
						dialogUtil.createDialog(BindPhoneAuthActivity.this, "验证失败", "", "确定", "数据异常，请返回前一步重新输入");
						
					} else if (result.getState() == -6) {
//						ToastUtil.showErrorToast(BindPhoneAuthActivity.this, "短信验证码已过期，请重新申请发送绑定手机号码请求");
						dialogUtil.createDialog(BindPhoneAuthActivity.this, "验证码错误", "", "确定", "短信验证码已过期，请重新申请发送绑定手机号码请求");
						
					} else if (result.getState() == -7) {
//						ToastUtil.showErrorToast(BindPhoneAuthActivity.this, "短信验证码有误，请重新申请验证");
						dialogUtil.createDialog(BindPhoneAuthActivity.this, "验证码错误", "", "确定", "短信验证码有误，请重新申请验证");
						
					}
					activateButton(sureBt);
				};
			}.execute(getRenheApplication().getUserInfo().getSid(), getRenheApplication().getUserInfo().getAdSId(), tel, code);
		}else{
			dialogUtil.createDialog(BindPhoneAuthActivity.this, "验证码错误", "", "确定", "验证码不能为空");
		}
	}

	@Override
	public void onBackPressed() {
		dialogUtil.createDialog(BindPhoneAuthActivity.this, "", getString(R.string.auth_dialog_cancle),
				getString(R.string.auth_dialog_sure), getString(R.string.auth_click_back));
		return;
	}
}
