package com.itcalf.renhe.context.more;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.itcalf.renhe.Constants;
import com.itcalf.renhe.R;
import com.itcalf.renhe.RenheApplication;
import com.itcalf.renhe.cache.CacheManager;
import com.itcalf.renhe.context.archives.FollowTask;
import com.itcalf.renhe.context.portal.JPushSetAliasApi;
import com.itcalf.renhe.context.portal.LoginActivity;
import com.itcalf.renhe.context.template.ActivityTemplate;
import com.itcalf.renhe.context.template.BaseActivity;
import com.itcalf.renhe.dto.FollowState;
import com.itcalf.renhe.dto.UserInfo;
import com.itcalf.renhe.dto.Version;
import com.itcalf.renhe.service.IVersionUpdate;
import com.itcalf.renhe.service.IVersionUpdate.Stub;
import com.itcalf.renhe.service.RenheService;
import com.itcalf.renhe.service.VersionService;
import com.itcalf.renhe.utils.AsyncImageLoader;
import com.itcalf.renhe.utils.HttpUtil;
import com.itcalf.renhe.utils.RequestDialog;
import com.itcalf.renhe.utils.ToastUtil;

public class MySettingActivity extends BaseActivity implements OnClickListener {
	private Button newMsgBt;
	private Button toAllBt;
	private Button gprsBt;
	private Button updateBt;
	private Button feedBackBt;
	private Button aboutBt;
	private Button attentionBt;
	private Button exitBt;

	private AlertDialog mAlertDialog;
	private LinearLayout closeRH;
	private LinearLayout closeRHLogin;

	private SharedPreferences msp;
	private SharedPreferences.Editor mEditor;
	// 版本更新IPC
	private IVersionUpdate.Stub versionUpdate;
	private RequestDialog requestDialog;
	private RelativeLayout rootRl;
	// 版本更新服务连接
	private ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			versionUpdate = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			versionUpdate = (Stub) service;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		RenheApplication.getInstance().addActivity(this);
		new ActivityTemplate().doInActivity(this, R.layout.settingmain);
		// 绑定服务
		bindService(new Intent(this, VersionService.class), mServiceConnection, BIND_AUTO_CREATE);

	}

	@Override
	protected void findView() {
		super.findView();
		newMsgBt = (Button) findViewById(R.id.newmsgBt);
		toAllBt = (Button) findViewById(R.id.toallBt);
		gprsBt = (Button) findViewById(R.id.gprsBt);
		updateBt = (Button) findViewById(R.id.updateBt);
		feedBackBt = (Button) findViewById(R.id.feedbackBt);
		aboutBt = (Button) findViewById(R.id.aboutBt);
		attentionBt = (Button) findViewById(R.id.attentionBt);
		exitBt = (Button) findViewById(R.id.exitBt);
		rootRl = (RelativeLayout) findViewById(R.id.rootrl);
	}

	@Override
	protected void initData() {
		super.initData();
		setTextValue(R.id.title_txt, "设置");
		if (getRenheApplication().getFollowState() != null && getRenheApplication().getFollowState().getState() == 1) {
			switch (getRenheApplication().getFollowState().getFollowState()) {
			case 1: // 1 已关注过人和网 2. 未关注过人和网
				attentionBt.setText("已关注人和网");
				break;
			case 2:
				attentionBt.setText("关注人和网");
				break;
			default:
				break;
			}
		}
		new AttentionTask().execute((String) null);
		msp = this.getSharedPreferences("setting_info", 0);
		mEditor = msp.edit();
		requestDialog = new RequestDialog(this, "正在注销");
	}

	@Override
	protected void initListener() {
		super.initListener();
		newMsgBt.setOnClickListener(this);
		toAllBt.setOnClickListener(this);
		gprsBt.setOnClickListener(this);
		updateBt.setOnClickListener(this);
		aboutBt.setOnClickListener(this);
		attentionBt.setOnClickListener(this);
		feedBackBt.setOnClickListener(this);
		exitBt.setOnClickListener(this);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 1:
			ProgressDialog findPd = new ProgressDialog(this);
			findPd.setTitle("提交请求中");
			findPd.setMessage("请稍候...");
			//			findPd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			findPd.setCanceledOnTouchOutside(false);
			return findPd;
		default:
			return null;
		}
	}

	class AttentionTask extends AsyncTask<String, Void, FollowState> {

		@Override
		protected FollowState doInBackground(String... params) {
			Map<String, Object> reqParams = new HashMap<String, Object>();
			reqParams.put("sid", getRenheApplication().getUserInfo().getSid());
			reqParams.put("adSId", getRenheApplication().getUserInfo().getAdSId());
			try {
				FollowState mb = (FollowState) HttpUtil.doHttpRequest(Constants.Http.CHECK_FOLLOWRENHE, reqParams,
						FollowState.class, MySettingActivity.this);
				return mb;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(FollowState result) {
			super.onPostExecute(result);
			if (result != null && result.getState() == 1) {
				getRenheApplication().setFollowState(result);
				if (getRenheApplication().getFollowState() != null && getRenheApplication().getFollowState().getState() == 1) {
					switch (getRenheApplication().getFollowState().getFollowState()) {
					case 1: // 1 已关注过人和网 2. 未关注过人和网
						attentionBt.setText("已关注人和网");
						break;
					case 2:
						attentionBt.setText("关注人和网");
						break;
					default:
						break;
					}
				}
			}
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.newmsgBt:
			startActivity(SettingNewMsgActivity.class);
			break;
		case R.id.toallBt:
			startActivity(SettingAllActivity.class);
			break;
		case R.id.gprsBt:
			startActivity(SettingGPRSActivity.class);
			break;
		case R.id.updateBt:
			update();
			break;
		case R.id.feedbackBt:
			startActivity(FeedBackActivity.class);
			break;
		case R.id.aboutBt:
			startActivity(AboutRenheActivity.class);
			break;
		case R.id.attentionBt:
			attention();
			break;
		case R.id.exitBt:
//			createDialog(this);
			closeLogin();
			break;
		default:
			break;
		}
	}

	private void attention() {

		if (getRenheApplication().getFollowState() != null && getRenheApplication().getFollowState().getState() == 1) {
			switch (getRenheApplication().getFollowState().getFollowState()) {
			case 1: // 1 已关注过人和网 2. 未关注过人和网
				ToastUtil.showToast(MySettingActivity.this, "已关注人和网");
				break;
			case 2:
				new FollowTask(MySettingActivity.this) {
					@Override
					public void doPost(Integer result) {
						removeDialog(1);
						if (result == 1) {
							new AttentionTask().execute((String) null);
							ToastUtil.showToast(MySettingActivity.this, "关注人和网成功");
							attentionBt.setText("已关注人和网");

						}
					}

					@Override
					public void doPre() {
						showDialog(1);
					}
				}.execute(Constants.Http.MESSAGEBOARD_ADDFOLLOW,
						"ff2e8a06dd18d07495e8831a1d9556ac58d176cc7e7ee789150c5d61492919a3");
				break;
			default:
				break;
			}
		}

	}

	private void update() {
		if (null != versionUpdate) {
			new AsyncTask<Void, Void, Version>() {
				@Override
				protected Version doInBackground(Void... params) {
					try {
						return ((RenheApplication) getApplication()).getPhoneCommand().getLastedVersion(MySettingActivity.this);
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
							ver = MySettingActivity.this.getPackageManager().getPackageInfo(
									MySettingActivity.this.getPackageName(), 0).versionName;
						} catch (NameNotFoundException e1) {
							e1.printStackTrace();
						}
						// 检查软件版本，提示下载更新
						if (result.getVersion().compareToIgnoreCase(ver) > 0) {
							new AlertDialog.Builder(MySettingActivity.this).setTitle("版本更新")
									.setMessage("发现新版本" + result.getVersion() + ",是否立即更新?")
									.setNegativeButton("取消", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
										}
									}).setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											try {
												versionUpdate.checkVersionUpdate(result.getNewVersionDownloadUrl());
											} catch (RemoteException e) {
												e.printStackTrace();
											}
										}
									}).create().show();
						} else {
							ToastUtil.showToast(MySettingActivity.this, "暂无新版本!");
						}
					}
				}

			}.execute();
		}
	}

	public void createDialog(Context context) {
		RelativeLayout view = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.exit_dialog, null);

		Builder mDialog = new AlertDialog.Builder(context);
		//		mDialog.setView(view,0,0,0,0);
		closeRH = (LinearLayout) view.findViewById(R.id.closeRH);
		closeRHLogin = (LinearLayout) view.findViewById(R.id.closeRH_login);
		mAlertDialog = mDialog.create();
		mAlertDialog.setView(view, 0, 0, 0, 0);
		mAlertDialog.setCanceledOnTouchOutside(true);
		mAlertDialog.show();
		closeRH.setOnClickListener(new ButtonListener());
		closeRHLogin.setOnClickListener(new ButtonListener());
	}

	class ButtonListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			if(null != mAlertDialog){
				mAlertDialog.dismiss();
			}
			switch (v.getId()) {
			case R.id.closeRH:
				//清空图片内存缓存
				AsyncImageLoader.getInstance().clearCache();
				//				stopService(new Intent(this, RenheService.class));
				//退出时是否清除用户数据
				if (getSharedPreferences("setting_info", 0).getBoolean("clearcache", false)) {
					CacheManager.getInstance().populateData(MySettingActivity.this)
							.clearCache(getRenheApplication().getUserInfo().getEmail());
				}
				//关闭通知栏消息
				((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(10001);
				RenheApplication.getInstance().exit();
				break;
			case R.id.closeRH_login:
				closeLogin();

				break;
			default:
				break;
			}
		}
	}

	private void closeLogin(){
		requestDialog.addFade(rootRl);
		final Runnable runnable2 = new Runnable() {

			@Override
			public void run() {
				RenheApplication.getInstance().exit();
			}
		};
		final Handler handler = new Handler(new Callback() {

			@Override
			public boolean handleMessage(Message arg0) {
				if (arg0.what == 10) {
					Bundle bundle = new Bundle();
					bundle.putBoolean(Constants.DATA_LOGOUT, true);
					startActivity(LoginActivity.class, bundle, Intent.FLAG_ACTIVITY_CLEAR_TOP);
					//						RenheApplication.getInstance().exit();
//					new Thread(runnable2).start();
					requestDialog.removeFade(rootRl);
				}
				return false;
			}
		});
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				//清空图片内存缓存
				AsyncImageLoader.getInstance().clearCache();
				CacheManager.getInstance().populateData(MySettingActivity.this)
						.clearCache(getRenheApplication().getUserInfo().getEmail());
				//清空shareprefrence数据
				mEditor.clear();
				mEditor.commit();
				//删除登录的用户信息
				getRenheApplication().getUserCommand().delUser(getRenheApplication().getUserInfo().getEmail());
				getRenheApplication().setUserInfo(null);
				//删除jpush设置
				delMyJPush(RenheApplication.getInstance().getUserInfo());
				//关闭通知服务
				stopService(new Intent(MySettingActivity.this, RenheService.class));
				//						stopService(new Intent(this, VersionService.class));
				((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(1000);
				((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(0);
				SharedPreferences mmsp = RenheApplication.getInstance().getSharedPreferences("notify_id", Context.MODE_PRIVATE);
				SharedPreferences.Editor mmEditor = mmsp.edit();
				mmEditor.putInt("notify_num", 1);
				mmEditor.commit();
				
				RenheApplication.getInstance().clearActivity();
				Message msg = new Message();
				msg.what = 10;
				handler.sendMessageDelayed(msg, 2000);
			}
		};
		
		handler.post(runnable);
	}
	private void delMyJPush(UserInfo userInfo) {
		// 清除缓存的login token
		RenheApplication.getInstance().setUserInfo(null);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(Constants.Prefs.HAD_REGIST_JPUSH, false);
		editor.commit();

		if (userInfo != null) {
			// unset jpush alias
			JPushInterface.setAlias(this, "", new TagAliasCallback() {
				@Override
				public void gotResult(int arg0, String arg1, Set<String> arg2) {
					Log.i("SetAlias", "unset alias result=" + arg0);
				}
			});
			String registrationId = JPushInterface.getRegistrationID(this);
			try {
				JPushSetAliasApi.delAlias(this, userInfo.getId(), registrationId);
			} catch (Exception e) {
				Log.e("SetAlias", "unset alias error after logout");
			}
		}
	}
}
