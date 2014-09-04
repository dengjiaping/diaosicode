package com.itcalf.renhe.context.more;

import java.util.HashMap;
import java.util.Map;

import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.itcalf.renhe.Constants;
import com.itcalf.renhe.R;
import com.itcalf.renhe.RenheApplication;
import com.itcalf.renhe.cache.CacheManager;
import com.itcalf.renhe.context.archives.DialogActivity;
import com.itcalf.renhe.context.archives.FollowTask;
import com.itcalf.renhe.context.innermsg.SendInnerMsgActivity;
import com.itcalf.renhe.context.settings.SettingsPreferences;
import com.itcalf.renhe.context.template.ActivityTemplate;
import com.itcalf.renhe.context.template.BaseActivity;
import com.itcalf.renhe.dto.FollowState;
import com.itcalf.renhe.utils.AsyncImageLoader;
import com.itcalf.renhe.utils.HttpUtil;
import com.itcalf.renhe.utils.ToastUtil;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;

public class SettingMainActivity extends BaseActivity {
	private final String APP_ID = "wx6d03435b4ef6f18d"; 
	private IWXAPI api;
	private Button mSettingBt;
	private Button mAttentionBt;
	private Button mFeedbackBt;
	private Button mAboutBt;
	private Button mShareBt;
	//登出标识（两次按下返回退出时的标识字段）
	private boolean logoutFlag;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new ActivityTemplate().doInActivity(this, R.layout.more_settingmain);
		/**
		 * 注册到微信
		 */
		api = WXAPIFactory.createWXAPI(getApplicationContext(),APP_ID,true);  
        api.registerApp(APP_ID);
	}

	@Override
	protected void findView() {
		super.findView();
//		menu = mySlidingMenu.initSlidingMenu();
		mSettingBt = (Button) findViewById(R.id.settingBt);
		mAttentionBt = (Button) findViewById(R.id.attentionBt);
		mFeedbackBt = (Button) findViewById(R.id.feedbackBt);
		mAboutBt = (Button) findViewById(R.id.aboutBt);
		mShareBt = (Button)findViewById(R.id.shareBt);
		
	}

	@Override
	protected void initData() {
		super.initData();
		setTextValue(R.id.title_txt, "更多");
		if (getRenheApplication().getFollowState() != null && getRenheApplication().getFollowState().getState() == 1) {
			switch (getRenheApplication().getFollowState().getFollowState()) {
			case 1: // 1 已关注过人和网 2. 未关注过人和网
				mAttentionBt.setText("已关注人和网");
				break;
			case 2:
				mAttentionBt.setText("关注人和网");
				break;
			default:
				break;
			}
		}
		new AttentionTask().execute((String)null);
	}

	@Override
	protected void initListener() {
		super.initListener();
//		registerForContextMenu(mShareBt);
		mFeedbackBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(FeedBackActivity.class);
			}
		});
		mAboutBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(AboutRenheActivity.class);
			}
		});
		mSettingBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(SettingsPreferences.class);
			}
		});
		mAttentionBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (getRenheApplication().getFollowState() != null && getRenheApplication().getFollowState().getState() == 1) {
					switch (getRenheApplication().getFollowState().getFollowState()) {
					case 1: // 1 已关注过人和网 2. 未关注过人和网
						ToastUtil.showToast(SettingMainActivity.this, "已关注人和网");
						break;
					case 2:
						new FollowTask(SettingMainActivity.this) {
							@Override
							public void doPost(Integer result) {
								removeDialog(1);
								if (result == 1) {
									new AttentionTask().execute((String)null);
									ToastUtil.showToast(SettingMainActivity.this, "关注人和网成功");
									mAttentionBt.setText("已关注人和网");

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
		});
		mShareBt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
//				mShareBt.performLongClick();
				Intent intent = new Intent();
				intent.setClass(SettingMainActivity.this, DialogActivity.class);
				SettingMainActivity.this.startActivity(intent);
				overridePendingTransition(R.anim.push_bottom_in, R.anim.push_bottom_out);
			}
		});
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderIcon(android.R.drawable.ic_menu_more);
		menu.setHeaderTitle("请选择");
		menu.add(0, 0, 0, "通过站内信分享");
		menu.add(0, 1, 0, "通过手机短信分享");
		menu.add(0, 2, 0, "通过email分享");
		menu.add(0, 3, 0, "发送给微信好友");
		// menu.add(1, 3, 0, R.id.follow_btn);
	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			callLetter();
			break;
		case 1:
				Uri uri1 = Uri.parse("smsto:");
				Intent it = new Intent(Intent.ACTION_SENDTO, uri1);
				it.putExtra("sms_body", "您的好友" + getRenheApplication().getUserInfo().getName() + "分享 人和网"
						+"http://m.renhe.cn/app/renhe.shtml"
						+ "给您");
				startActivity(it);
			break;
		case 2:
				Uri uri = Uri.parse("mailto:");
				Intent email = new Intent(Intent.ACTION_SENDTO, uri);
				email.putExtra(Intent.EXTRA_SUBJECT, "您的好友" + getRenheApplication().getUserInfo().getName() + "分享 人和网"
						+"http://m.renhe.cn/app/renhe.shtml" + "给您");
				startActivity(email);
			break;
		case 3://发送到微信好友
			/**
			 * 发送url
			 */
			WXWebpageObject webpage = new WXWebpageObject();
			
			webpage.webpageUrl = "http://www.renhe.cn";
			
			WXMediaMessage msg = new WXMediaMessage(webpage);
			msg.title = "人和网";//好友姓名
			msg.description = "全国最大的商务社交平台";//好友介绍
			Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.icon);//好友头像
			msg.thumbData = com.itcalf.renhe.utils.WeixinUtil.bmpToByteArray(thumb, true);
			
			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = buildTransaction("webpage");
			req.message = msg;
			req.scene = SendMessageToWX.Req.WXSceneSession;
			api.sendReq(req);
		
		default:
			break;
		}
		return super.onContextItemSelected(item);
	}
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
	private void callLetter() {
			Bundle bundle = new Bundle();
			bundle.putString("share", "人和网");
			bundle.putString("sharesid", "");
			startActivity(SendInnerMsgActivity.class, bundle);
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
						FollowState.class, SettingMainActivity.this);
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
						mAttentionBt.setText("已关注人和网");
						break;
					case 2:
						mAttentionBt.setText("关注人和网");
						break;
					default:
						break;
					}
				}
			}
		}

	}
	Handler handler = new Handler();
	Runnable run = new Runnable() {
		@Override
		public void run() {
			logoutFlag = false;
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(logoutFlag) {
				AsyncImageLoader.getInstance().clearCache();
//				finish();
				if(getSharedPreferences("setting_info", 0)
						.getBoolean("clearcache", false)) {
					CacheManager.getInstance().populateData(this)
						.clearCache(getRenheApplication().getUserInfo().getEmail());
				}
				//关闭通知栏消息
				((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(10001);
				RenheApplication.getInstance().exit();
			}else {
				ToastUtil.showToast(this, "请再点击一次退出程序!");
				logoutFlag = true;
				handler.postDelayed(run, 2000);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event); // 最后，一定要做完以后返回
		// true，或者在弹出菜单后返回true，其他键返回super，让其他键默认
	}
}
