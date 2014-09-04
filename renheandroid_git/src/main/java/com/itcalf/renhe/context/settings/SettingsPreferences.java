package com.itcalf.renhe.context.settings;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.itcalf.renhe.R;
import com.itcalf.renhe.RenheApplication;
import com.itcalf.renhe.cache.CacheManager;
import com.itcalf.renhe.dto.Version;
import com.itcalf.renhe.service.IVersionUpdate;
import com.itcalf.renhe.service.IVersionUpdate.Stub;
import com.itcalf.renhe.service.VersionService;
import com.itcalf.renhe.utils.AsyncImageLoader;
import com.itcalf.renhe.utils.ToastUtil;

/**
 * Feature:系统设置界面 
 * Description:系统设置界面
 * 
 * @author xp
 * 
 */
public class SettingsPreferences extends PreferenceActivity {
	Button overButton;
	// 版本更新IPC
	private IVersionUpdate.Stub versionUpdate;
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
		addPreferencesFromResource(R.xml.renhepref);
		setContentView(R.layout.preferences_layout);
		overButton = (Button)findViewById(R.id.backBt);
		overButton.setOnClickListener(new ButtonListener());
		// 绑定服务
		bindService(new Intent(this, VersionService.class), mServiceConnection, BIND_AUTO_CREATE);
	}
	class ButtonListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			finish();
		}
		
	}
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
		if (preference.getKey().equals("update")) {
			if (null != versionUpdate) {
				new AsyncTask<Void, Void, Version>() {
					@Override
					protected Version doInBackground(Void... params) {
						try {
							return ((RenheApplication) getApplication()).getPhoneCommand().getLastedVersion(
									SettingsPreferences.this);
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
								ver = SettingsPreferences.this.getPackageManager().getPackageInfo(
										SettingsPreferences.this.getPackageName(), 0).versionName;
							} catch (NameNotFoundException e1) {
								e1.printStackTrace();
							}
							// 检查软件版本，提示下载更新
							if (result.getVersion().compareToIgnoreCase(ver) > 0) {
								new AlertDialog.Builder(SettingsPreferences.this).setTitle("版本更新")
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
								ToastUtil.showToast(SettingsPreferences.this, "暂无新版本!");
							}
						}
					}

				}.execute();
			}
		} else if (preference.getKey().equals("about")) {
			
		} else if (preference.getKey().equals("clearcache")) {
			AsyncImageLoader.getInstance().clearCache();
			CacheManager.getInstance().populateData(this)
					.clearCache(((RenheApplication) getApplicationContext()).getUserInfo().getEmail());
			ToastUtil.showToast(this, "清除成功!");
		}
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}

	@Override
	protected void onDestroy() {
		// 解除版本更新服务绑定
		unbindService(mServiceConnection);
		mServiceConnection = null;
		super.onDestroy();
	}
}
