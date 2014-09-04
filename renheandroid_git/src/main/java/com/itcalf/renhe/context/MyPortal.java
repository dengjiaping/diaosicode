package com.itcalf.renhe.context;

import java.util.Date;
import java.util.Set;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.itcalf.renhe.Constants;
import com.itcalf.renhe.R;
import com.itcalf.renhe.RenheApplication;
import com.itcalf.renhe.context.fragmentMain.MainFragment;
import com.itcalf.renhe.context.portal.JPushSetAliasApi;
import com.itcalf.renhe.context.portal.LoginActivity;
import com.itcalf.renhe.context.room.RoomsActivity;
import com.itcalf.renhe.context.template.BaseActivity;
import com.itcalf.renhe.dto.UserInfo;
import com.itcalf.renhe.dto.Version;
import com.itcalf.renhe.service.IVersionUpdate;
import com.itcalf.renhe.service.IVersionUpdate.Stub;
import com.itcalf.renhe.service.RenheService;
import com.itcalf.renhe.service.VersionService;
import com.itcalf.renhe.utils.NetworkUtil;
import com.itcalf.renhe.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * Feature:系统默认入口界面 Desc:检查系统版本，提示更新下载，默认显示人和网宣传图片
 * 
 * @author xp
 * 
 */
public class MyPortal extends BaseActivity {

	// 绑定版本下载更新IPC
	private IVersionUpdate.Stub versionUpdate;
	// 版本文字
//	private TextView mVersionText;
	private TextView mcopyRightText;
	// 绑定服务连接
	private ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			versionUpdate = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			versionUpdate = (Stub) service;
			// 异步线程去检查版本更新
			new AsyncTask<Void, Void, Version>() {

				@Override
				protected Version doInBackground(Void... params) {
					try {
						return ((RenheApplication) getApplication()).getPhoneCommand().getLastedVersion(MyPortal.this);
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				}

				@Override
				protected void onPostExecute(final Version result) {
					super.onPostExecute(result);
					if (null != result && 1 == result.getState()) {
						String ver = "";
						try {
							ver = MyPortal.this.getPackageManager().getPackageInfo(MyPortal.this.getPackageName(), 0).versionName;
						} catch (NameNotFoundException e1) {
							e1.printStackTrace();
						}
						if(null != result.getVersion()){
							// 比较系统版本，如果有新版本提示是否更新
							if (result.getVersion().compareToIgnoreCase(ver) > 0) {
								new AlertDialog.Builder(MyPortal.this).setTitle("软件升级")
								.setMessage("发现新版本" + result.getVersion() + ",是否立即更新?")
								.setNegativeButton("取消", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
//											startActivity(LoginActivity.class);
										MyPortal.this.userAutoLogin();
//											finish();
									}
								}).setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										try {
											versionUpdate.checkVersionUpdate(result.getNewVersionDownloadUrl());
										} catch (RemoteException e) {
											e.printStackTrace();
										}
										// startActivity(LoginActivity.class);
										// finish();
									}
								}).create().show();
							} else {
								MyPortal.this.userAutoLogin();
//							finish();
							}
						}else{
							MyPortal.this.userAutoLogin();
						}
					} else {
						ToastUtil.showErrorToast(MyPortal.this, "连接服务器失败！");
					}
				}
			}.execute();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myportal);
		RenheApplication.getInstance().addActivity(this);
		//友盟统计
		MobclickAgent.updateOnlineConfig(this);
		
		//判断是否已登录
//		userAutoLogin();
		// getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		startService(new Intent(this, VersionService.class));
//		mVersionText = (TextView) findViewById(R.id.versionText);
		String ver = "";
		try {
			ver = MyPortal.this.getPackageManager().getPackageInfo(MyPortal.this.getPackageName(), 0).versionName;
//			mVersionText.setText("人和网Android " + ver + "版");
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		}
	}
	public void userAutoLogin(){
		UserInfo userInfo = getRenheApplication().getUserCommand().getLoginUser();
		String email = "";
		String pwd = "";
		if (null != userInfo) {
			email = userInfo.getAccountType();
			pwd = userInfo.getPwd();
			if(null != email && null != pwd){
				if (-1 != NetworkUtil.hasNetworkConnection(this)) {
					new LoginTask(pwd).execute(email, pwd);
				} else {
					ToastUtil.showNetworkError(this);
				}
			}else{
				Intent intent = new Intent();
				intent.setClass(this, LoginActivity.class);
				intent.putExtra(Constants.DATA_LOGOUT, true);
				startActivity(intent);
				finish();
			}
		}else{
			Intent intent = new Intent();
			intent.setClass(this, LoginActivity.class);
			intent.putExtra(Constants.DATA_LOGOUT, true);
			startActivity(intent);
			finish();
		}
		
	
	}
	class LoginTask extends AsyncTask<String, Void, UserInfo> {
		private String pwd;
		private String accountType;
		public LoginTask(String pwd) {
			this.pwd = pwd;
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showDialog(1);
		}

		@Override
		protected UserInfo doInBackground(String... params) {
			UserInfo userInfo = new UserInfo();
			userInfo.setAccountType(params[0]);
			userInfo.setPwd(params[1]);
			accountType = params[0];
			return getRenheApplication().getUserCommand().login(userInfo);
		}

		@Override
		protected void onPostExecute(UserInfo result) {
			super.onPostExecute(result);
			removeDialog(1);
			if (null != result) {
				if (1 == result.getState()) {
					//设置密码（result中不返回密码）
					result.setPwd(this.pwd);
					result.setAccountType(accountType);
					forwardToHall(result);
					//设置JPush推送
					setMyJPush(result);
				} else{
					Intent intent = new Intent();
					intent.setClass(MyPortal.this, LoginActivity.class);
					intent.putExtra(Constants.DATA_LOGOUT, true);
					startActivity(intent);
					finish();
				}
			}
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
		finish();
	}
	@Override
	protected void onResume() {
		// 绑定版本检查服务
		bindService(new Intent(this, VersionService.class), mServiceConnection, BIND_AUTO_CREATE);
		JPushInterface.onResume(this);
		super.onResume();
	}

	@Override
	protected void onPause() {
		// 解除版本检查服务绑定
		unbindService(mServiceConnection);
		JPushInterface.onPause(this);
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mServiceConnection = null;
	}

}
