package com.itcalf.renhe.context.portal;

import java.util.Date;
import java.util.Set;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.itcalf.renhe.Constants;
import com.itcalf.renhe.R;
import com.itcalf.renhe.RenheApplication;
import com.itcalf.renhe.context.fragmentMain.MainFragment;
import com.itcalf.renhe.context.register.AuthActivity;
import com.itcalf.renhe.context.register.NewRegisterActivity;
import com.itcalf.renhe.context.relationship.SearchRelationshipActivity;
import com.itcalf.renhe.context.room.RoomsActivity;
import com.itcalf.renhe.context.room.WebViewActivityForReport;
import com.itcalf.renhe.context.template.ActivityTemplate;
import com.itcalf.renhe.context.template.BaseActivity;
import com.itcalf.renhe.dto.UserInfo;
import com.itcalf.renhe.service.RenheService;
import com.itcalf.renhe.utils.NetworkUtil;
import com.itcalf.renhe.utils.RequestDialog;
import com.itcalf.renhe.utils.ToastUtil;

public class LoginActivity extends BaseActivity {

	private Button mRegisterBt;
	private EditText mEmailEt;
	private EditText mPwdEt;
	private Button mLoginBt;
	private Drawable imgCloseButton;
	// private Dialog mLoadingDialog;
	private boolean mLogout;
	private RequestDialog requestDialog;
	private RelativeLayout loginRl;
	public static final String FINISHLOGINACTIVITY_1 = "com.renhe.finishloginactivity1";
	public static final String FINISHLOGINACTIVITY_2 = "com.renhe.finishloginactivity2";
	private FinishSelfReceiver finishSelfReceiver;
	private TextView findPwdTv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		RenheApplication.getInstance().addActivity(this);
		new ActivityTemplate().doInActivity(this, R.layout.portal_login);
	}

	@Override
	protected void findView() {
		super.findView();
		mRegisterBt = (Button) findViewById(R.id.registerbt);
		mEmailEt = (EditText) findViewById(R.id.mailEt);
		mPwdEt = (EditText) findViewById(R.id.pwdEt);
		mLoginBt = (Button) findViewById(R.id.loginBt);
		mEmailEt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		imgCloseButton = getResources().getDrawable(R.drawable.relationship_input_del);
		loginRl = (RelativeLayout)findViewById(R.id.login_rl);
		findPwdTv = (TextView)findViewById(R.id.text1);
	}

	@Override
	protected void initData() {
		super.initData();
		mLogout = getIntent().getBooleanExtra(Constants.DATA_LOGOUT, false);
		// mLoadingDialog = new LoadingDialog(this, "登录中...");
		// Spanned text = Html.fromHtml("<a>马上注册<a>");
		// mRegisterTv.setText(text);
		if(imgCloseButton != null){
			imgCloseButton.setBounds(0, 0, imgCloseButton.getIntrinsicWidth(), imgCloseButton.getIntrinsicHeight());
		}
		requestDialog = new RequestDialog(this,"登录中");
		finishSelfReceiver = new FinishSelfReceiver();
		IntentFilter intentFilter = new IntentFilter(FINISHLOGINACTIVITY_1);
		intentFilter.addAction(FINISHLOGINACTIVITY_2);
		registerReceiver(finishSelfReceiver, intentFilter);
	}

	@Override
	protected void initListener() {
		super.initListener();
//		mEmailEt.setOnTouchListener(new EditTouchListener(mEmailEt));
		mEmailEt.addTextChangedListener(new TextWatchListener(mEmailEt));
//		mPwdEt.setOnTouchListener(new EditTouchListener(mPwdEt));
		mPwdEt.addTextChangedListener(new TextWatchListener(mPwdEt));
		mRegisterBt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
//				startActivityForResult(NewRegisterActivity.class, REGISTER_CODE);
				startActivity(NewRegisterActivity.class);
			}
		});
		mLoginBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				String email = mEmailEt.getText().toString().trim();
				String pwd = mPwdEt.getText().toString().trim();
				if (email.length() == 0) {
					ToastUtil.showToast(LoginActivity.this, getResources().getString(R.string.mailnotnull));
					return;
				}
				if (!email.matches("^([a-zA-Z0-9_\\.\\-])+\\@(([a-zA-Z0-9\\-])+\\.)+([a-zA-Z0-9]{2,4})+$") && !email.matches("^(13[0-9]|15[0-9]|18[0-9])\\d{8}$")) {
					ToastUtil.showToast(LoginActivity.this, getResources().getString(R.string.mailrule));
					return;
				}
				if (pwd.length() == 0) {
					ToastUtil.showToast(LoginActivity.this, getResources().getString(R.string.passwordnotnull));
					return;
				}
//				if (pwd.length() <= 0 || !pwd.matches("^\\w+$")) {
//					ToastUtil.showToast(LoginActivity.this, getResources().getString(R.string.passwordrule));
//					return;
//				}
				if (-1 != NetworkUtil.hasNetworkConnection(LoginActivity.this)) {
					new LoginTask().execute(email, pwd);
				} else {
					ToastUtil.showNetworkError(LoginActivity.this);
				}
			}
		});
		mEmailEt.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				if (arg1) {
					if (mEmailEt.getText().toString().trim().length() > 0) {
						mEmailEt.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.account_icon_aft), null,
								getResources().getDrawable(R.drawable.relationship_input_del), null);
					}else{
						mEmailEt.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.account_icon_pre), null,
								null, null);
					}
				} else {
					if (!TextUtils.isEmpty(mEmailEt.getText().toString().trim())){
						mEmailEt.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.account_icon_aft),null, null,
								null);
					}else{
						mEmailEt.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.account_icon_pre),null, null,
								null);
					}
				}
			}
		});
		mPwdEt.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				if (arg1) {
					if (mPwdEt.getText().toString().trim().length() > 0) {
						mPwdEt.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.pwd_icon_aft), null,
								getResources().getDrawable(R.drawable.relationship_input_del), null);
					}else{
						mPwdEt.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.pwd_icon_pre), null,
								null, null);
					}
				} else {
					if (!TextUtils.isEmpty(mPwdEt.getText().toString().trim())){
						mPwdEt.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.pwd_icon_aft),null, null,
								null);
					}else{
						mPwdEt.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.pwd_icon_pre),null, null,
								null);
					}
				}
			}
		});
		UserInfo userInfo = getRenheApplication().getUserCommand().getLoginUser();
		if (!mLogout) {
//			if (null != userInfo) {
//				mEmailEt.setText(userInfo.getEmail());
//				mPwdEt.setText(userInfo.getPwd());
//				mLoginBt.performClick();
//			}
		} else {
			if (null != userInfo && null != userInfo.getAccountType()) {
				mEmailEt.setText(userInfo.getAccountType());
			}
			mPwdEt.setText("");
		}
		findPwdTv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(LoginActivity.this, WebViewActivityForFindPwd.class);
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			}
		});
	}

	private static final int REGISTER_CODE = 10;
	private static final int COMPLETE_REGISTER_CODE = 5;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REGISTER_CODE:
			if (resultCode == RESULT_OK) {
				if (null != data) {
					UserInfo userInfo = (UserInfo) data.getSerializableExtra("userInfo");
					if (null != userInfo) {
						userInfo.setAccountType(userInfo.getEmail());
						forwardToHall(userInfo);
					}
				}
			}
			break;
		case COMPLETE_REGISTER_CODE:
			if (resultCode == RESULT_OK) {
				finish();
			}
			break;
		}
	}

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

	class LoginTask extends AsyncTask<String, Void, UserInfo> {
		private String userAccount;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
//			showDialog(1);
			requestDialog.addFade(loginRl);
		}

		@Override
		protected UserInfo doInBackground(String... params) {
			UserInfo userInfo = new UserInfo();
			userInfo.setAccountType(params[0]);
			userInfo.setPwd(params[1]);
			this.userAccount = params[0];
			return getRenheApplication().getUserCommand().login(userInfo);
		}

		@Override
		protected void onPostExecute(UserInfo result) {
			super.onPostExecute(result);
//			removeDialog(1);
			if (null != result) {
				if (1 == result.getState()) {
					//设置密码（result中不返回密码）
					result.setPwd(mPwdEt.getText().toString());
					result.setAccountType(userAccount);
					forwardToHall(result);
					//设置JPush推送
					setMyJPush(result);
				}else if(2 == result.getState()){//手机号码登录而且未走完注册流程的，需要跳到完善注册资料的页面（会继续返回mobile字段）
					String mobile = result.getMobile();
					String pwd = mPwdEt.getText().toString();
					Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
					intent.putExtra("tel", mobile);
					intent.putExtra("pwd", pwd);
					startActivityForResult(intent, COMPLETE_REGISTER_CODE);
					overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
				}else if (-1 == result.getState()) {
					ToastUtil.showErrorToast(LoginActivity.this, "用户名或密码错误!");
				} else {
					ToastUtil.showErrorToast(LoginActivity.this, "用户名或密码为空!");
				}
			}
			requestDialog.removeFade(loginRl);
		}

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
			findPd.setMessage("登录中...");
//			findPd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			findPd.setCanceledOnTouchOutside(false);
			return findPd;
		default:
			return null;
		}
	}
	class TextWatchListener implements TextWatcher{
		EditText et;
		public TextWatchListener(EditText et){
			this.et = et;
		}
		@Override
		public void afterTextChanged(Editable s) {
			
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if(et.getId() == R.id.mailEt){
				if (!TextUtils.isEmpty(mEmailEt.getText().toString().trim())){
					mEmailEt.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.account_icon_aft),null, getResources().getDrawable(R.drawable.relationship_input_del),
							null);
				}else{
					mEmailEt.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.account_icon_pre),null, null,
							null);
				}
			}
			if(et.getId() == R.id.pwdEt){
				if (!TextUtils.isEmpty(mPwdEt.getText().toString().trim())){
					mPwdEt.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.pwd_icon_aft),null, getResources().getDrawable(R.drawable.relationship_input_del),
							null);
				}else{
					mPwdEt.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.pwd_icon_pre),null, null,
							null);
				}
			}
//			if (!TextUtils.isEmpty(mEmailEt.getText().toString().trim()) && !TextUtils.isEmpty(mPwdEt.getText().toString().trim())) {
//				mLoginBt.setBackgroundResource(R.drawable.login_bt_selected);
//				mLoginBt.setClickable(true);
//			} else {
//				mLoginBt.setBackgroundResource(R.drawable.loginbt_nomal_shape);
//				mLoginBt.setClickable(false);
//			}
		
		}
		
	}
	class EditTouchListener implements OnTouchListener{
		EditText et;
		public EditTouchListener(EditText et){
			this.et = et;
		}
		@Override
		public boolean onTouch(View v, MotionEvent event) {

			switch (event.getAction()) {
			/** 手指离开的事件 */
			case MotionEvent.ACTION_UP:
				if(et.getCompoundDrawables()[2] != null){
					/** 手指抬起时候的坐标 **/
					int curX = (int) event.getX();
					if (curX > v.getWidth() - v.getPaddingRight() - imgCloseButton.getIntrinsicWidth() && !TextUtils.isEmpty(et.getText().toString())) {
						et.setText("");
						int cacheInputType = et.getInputType();
						et.onTouchEvent(event);
						et.setInputType(cacheInputType);
						
						return true;
					}
				}
				if(v.getId() == R.id.mailEt){
					if(mEmailEt.getText().toString().trim().length() > 0){
						mEmailEt.setCompoundDrawablesWithIntrinsicBounds(null, null,
								getResources().getDrawable(R.drawable.relationship_input_del), null);
					}
					mPwdEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
				}else if(v.getId() == R.id.pwdEt){
					if(mPwdEt.getText().toString().trim().length() > 0){
						mPwdEt.setCompoundDrawablesWithIntrinsicBounds(null, null,
								getResources().getDrawable(R.drawable.relationship_input_del), null);
					}
					mEmailEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
				}
				break;
			}
			return false;
		
		}
	}
	class FinishSelfReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			if(null != arg1.getAction() && (arg1.getAction().equals(FINISHLOGINACTIVITY_1) || arg1.getAction().equals(FINISHLOGINACTIVITY_2))){
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
