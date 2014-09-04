package com.itcalf.renhe.context.portal;

import java.util.Date;
import java.util.Set;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.itcalf.renhe.Constants;
import com.itcalf.renhe.R;
import com.itcalf.renhe.RenheApplication;
import com.itcalf.renhe.context.fragmentMain.MainFragment;
import com.itcalf.renhe.context.room.RoomsActivity;
import com.itcalf.renhe.context.template.ActivityTemplate;
import com.itcalf.renhe.context.template.BaseActivity;
import com.itcalf.renhe.dto.UserInfo;
import com.itcalf.renhe.service.RenheService;
import com.itcalf.renhe.utils.NetworkUtil;
import com.itcalf.renhe.utils.RequestDialog;
import com.itcalf.renhe.utils.ToastUtil;

public class RegisterActivity extends BaseActivity {

	private Button mRegisterBt;
	private EditText mMailEt;
//	private EditText mPwdEt;
//	private EditText mRepwdEt;
	private EditText mNameEt;
	private EditText mCompanyEt;
	private EditText mJobEt;
	private String tel = "";
	private String pwd = "";
	private RelativeLayout rootRl;
	private RequestDialog requestDialog;
	//private Dialog mLoadingDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new ActivityTemplate().doInActivity(this, R.layout.portal_register);
	}
	@Override
	protected void findView() {
		super.findView();
		setTextValue(R.id.title_txt, "人和网新用户注册");
		mRegisterBt = (Button) findViewById(R.id.registerBt);
		mMailEt = (EditText) findViewById(R.id.mailEt);
//		mPwdEt = (EditText) findViewById(R.id.pwdEt);
//		mRepwdEt = (EditText) findViewById(R.id.rePwdEt);
		mNameEt = (EditText) findViewById(R.id.nameEt);
		mCompanyEt = (EditText) findViewById(R.id.companyEt);
		mJobEt = (EditText) findViewById(R.id.jobEt);
		mMailEt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		rootRl = (RelativeLayout)findViewById(R.id.rootRl);
		requestDialog = new RequestDialog(this, "正在验证");
	}
	 @Override
	protected void initData() {
		 super.initData();
		 tel = getIntent().getStringExtra("tel");
		 pwd = getIntent().getStringExtra("pwd");
		//mLoadingDialog = new LoadingDialog(this, "登录中...");
	}
    @Override
	protected void initListener() {
    	super.initListener();
		mRegisterBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String email = mMailEt.getText().toString().trim();
//				String pwd = mPwdEt.getText().toString().trim();
//				String rePwd = mRepwdEt.getText().toString().trim();
				String name = mNameEt.getText().toString().trim();
				String company = mCompanyEt.getText().toString().trim();
				String job = mJobEt.getText().toString().trim();

				if (email.length() == 0) {
					ToastUtil.showToast(RegisterActivity.this, getResources()
							.getString(R.string.mailnotnull));
					mMailEt.requestFocus();
					return;
				}
				if (!email
						.matches("^([a-zA-Z0-9_\\.\\-])+\\@(([a-zA-Z0-9\\-])+\\.)+([a-zA-Z0-9]{2,4})+$")) {
					ToastUtil.showToast(RegisterActivity.this, getResources()
							.getString(R.string.mailrule));
					mMailEt.requestFocus();
					return;
				}
//				if (pwd.length() == 0) {
//					ToastUtil.showToast(RegisterActivity.this, getResources()
//							.getString(R.string.passwordnotnull));
//					mPwdEt.requestFocus();
//					return;
//				}
//				if (pwd.length() < 6 || pwd.length() > 16
//						|| !pwd.matches("^\\w+$")) {
//					ToastUtil.showToast(RegisterActivity.this, getResources()
//							.getString(R.string.passwordrule));
//					mPwdEt.requestFocus();
//					return;
//				}
//				if (!rePwd.equals(pwd)) {
//					ToastUtil.showToast(RegisterActivity.this, getResources()
//							.getString(R.string.repasswordrule));
//					mRepwdEt.requestFocus();
//					return;
//				}
				if (name.length() == 0) {
					ToastUtil.showToast(RegisterActivity.this, getResources()
							.getString(R.string.namenotnull));
					mNameEt.requestFocus();
					return;
				}
				if (company.length() == 0) {
					ToastUtil.showToast(RegisterActivity.this, getResources()
							.getString(R.string.comPanynotnull));
					mCompanyEt.requestFocus();
					return;
				}
				if (job.length() == 0) {
					ToastUtil.showToast(RegisterActivity.this, getResources()
							.getString(R.string.jobnotnull));
					mJobEt.requestFocus();
					return;
				}
				if(-1 != NetworkUtil.hasNetworkConnection(RegisterActivity.this)) {
					new RegisterTask().execute(tel, email, name,job, company,pwd);
				}else {
					ToastUtil.showNetworkError(RegisterActivity.this);
				}
			}
		});
	}
	
	class RegisterTask extends AsyncTask<String, Void, UserInfo> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			//mLoadingDialog.show();
//			showDialog(1);
			requestDialog.addFade(rootRl);
		}

		@Override
		protected UserInfo doInBackground(String... params) {
			UserInfo userInfo = new UserInfo();
			userInfo.setMobile(params[0]);
			userInfo.setEmail(params[1]);
			userInfo.setName(params[2]);
			userInfo.setTitle(params[3]);
			userInfo.setCompany(params[4]);
			userInfo.setPwd(params[5]);
			return RenheApplication.getInstance().getUserCommand().register(userInfo);
		}

		@Override
		protected void onPostExecute(UserInfo result) {
			super.onPostExecute(result);
			//mLoadingDialog.dismiss();
//			removeDialog(1);
			requestDialog.removeFade(rootRl);
			if (null != result) {
				//1. state int 说明：1 注册成功；-1 Email地址已被注册；-2 Email地址格式错误；-3 名字不规范；-4 密码长度不正确；-5 两次密码不相同；
				if(1 == result.getState()) {
					ToastUtil.showToast(RegisterActivity.this, "注册成功!");
					setMyJPush(result);
					Intent intent = new Intent();
					//TODO xin接口添加后设置
					result.setPwd(pwd);
					result.setAccountType(result.getEmail());
//					intent.putExtra("userInfo", result);
//					setResult(RESULT_OK, intent);
					sendBroadcast(new Intent(LoginActivity.FINISHLOGINACTIVITY_1));
					sendBroadcast(new Intent(LoginActivity.FINISHLOGINACTIVITY_2));
					forwardToHall(result);
					finish();
				}else if(-1== result.getState()){
					ToastUtil.showToast(RegisterActivity.this, "Email地址已被注册");
					mMailEt.requestFocus();
				}else if(-2== result.getState()){
					ToastUtil.showToast(RegisterActivity.this, "Email地址格式错误");
					mMailEt.requestFocus();
				}else if(-3== result.getState()){
					ToastUtil.showToast(RegisterActivity.this, "名字不规范");
					mNameEt.requestFocus();
				}else if(-4== result.getState()){
					ToastUtil.showToast(RegisterActivity.this, "email地址有误");
					mMailEt.requestFocus();
				}else if(-5== result.getState()){
					ToastUtil.showToast(RegisterActivity.this, "email地址已被其他人和网会员注册，请尝试更换一个email地址");
					mMailEt.requestFocus();
				}
			} else {
				ToastUtil.showNetworkError(RegisterActivity.this);
			}
		}

	};
	private void forwardToHall(UserInfo userInfo) {
		if (1 == NetworkUtil.hasNetworkConnection(this)) {
			// ToastUtil.showNetworkWIFI(this);
		} else if (0 == NetworkUtil.hasNetworkConnection(this)) {
			// ToastUtil.showNetworkMobile(this);
		} else {
			ToastUtil.showNetworkError(this);
		}
		startService(new Intent(this, RenheService.class));
		userInfo.setRemember(true);
		userInfo.setLogintime(DateFormat.format("yyyy-MM-dd hh:mm:ss", new Date()).toString());
		getRenheApplication().getUserCommand().insertOrUpdate(userInfo);
		getRenheApplication().setUserInfo(userInfo);
		startActivity(MainFragment.class, Intent.FLAG_ACTIVITY_CLEAR_TOP);
	}
	private void setMyJPush(UserInfo userInfo){
		SharedPreferences prefs = getSharedPreferences("setting_info", 0);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(Constants.Prefs.USERNAME, userInfo.getEmail());
		editor.commit();
		//set jpush alias
		JPushInterface.setAlias(getApplicationContext(), String.valueOf(userInfo.getId()), new TagAliasCallback() {
			@Override
			public void gotResult(int arg0, String arg1, Set<String> arg2) {
				Log.i("JPushInterface", "setAlias Result "+ arg0);
			}
		});
		String registrationId = JPushInterface.getRegistrationID(getApplicationContext());
		try {
			JPushSetAliasApi.setAlias(this,userInfo.getId(), registrationId);
		} catch (Exception e) {
			Log.e("SetAlias", "set alias api error after login");
		}
	}
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 1:
			ProgressDialog findPd = new ProgressDialog(this);
//			findPd.setTitle("正在注册账户");
			findPd.setMessage("请稍候...");
//			findPd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			findPd.setCanceledOnTouchOutside(false);
			return findPd;
		default:
			return null;
		}
	}
}
