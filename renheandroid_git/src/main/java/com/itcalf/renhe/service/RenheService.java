package com.itcalf.renhe.service;

import java.util.HashMap;
import java.util.Map;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.IBinder;

import com.itcalf.renhe.Constants;
import com.itcalf.renhe.R;
import com.itcalf.renhe.RenheApplication;
import com.itcalf.renhe.command.IContactCommand;
import com.itcalf.renhe.context.fragmentMain.MeunFragment;
import com.itcalf.renhe.dto.ContactList;
import com.itcalf.renhe.dto.FollowState;
import com.itcalf.renhe.dto.NewInnerMessage;
import com.itcalf.renhe.dto.UserInfo;
import com.itcalf.renhe.utils.HttpUtil;
import com.itcalf.renhe.utils.NetworkUtil;

/**
 * Feature:人和网主服务
 * Desc:人和网主服务，提供联系人同步、站内信轮询提醒消息
 * @author xp
 *
 */
public class RenheService extends Service {

	private RenheApplication renheApplication;
	// private String mLastMessageObjectId = "";
	private Thread mThread;
	private boolean isContinue = true;
	private UserInfo mUserInfo;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		renheApplication = (RenheApplication) getApplication();
		mUserInfo = ((RenheApplication) getApplication())
				.getUserInfo();
		//联系人同步
		if (null != mUserInfo) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					IContactCommand contactCommand = null;
					ContactList clist = null;
					try {
						if(-1 != NetworkUtil.hasNetworkConnection(RenheService.this)) {
							contactCommand = ((RenheApplication) getApplication()).getContactCommand();
							clist = contactCommand.getContactList(
									mUserInfo.getSid(), mUserInfo.getSid(),
									mUserInfo.getAdSId());
							if (1 == clist.getState()) {
								if (null != clist.getMemberList()
										&& clist.getMemberList().length > 0) {
									contactCommand.saveContactList(clist.getMemberList(), mUserInfo.getEmail());
								}
							}
							
							////////////////////////////
							
							Map<String, Object> reqParams = new HashMap<String, Object>();
							reqParams.put("sid", mUserInfo.getSid());
							reqParams.put("adSId", mUserInfo.getAdSId());
							try {
								FollowState mb = (FollowState) HttpUtil.doHttpRequest(
										Constants.Http.CHECK_FOLLOWRENHE, reqParams,
										FollowState.class,RenheService.this);
								if(mb!=null && mb.getState()==1)
									renheApplication.setFollowState(mb);
							} catch (Exception e) {
								e.printStackTrace();
							}
							///////////////////
						}else {
							//ToastUtil.showNetworkError(RenheService.this);
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						clist = null;
						contactCommand = null;
						mUserInfo = null;
					}
				}
			}).start();
		
			//检查最新站内信
			mThread = new Thread(new Runnable() {
				@Override
				public void run() {
						Map<String, Object> reqParams = new HashMap<String, Object>();
						reqParams.put("sid", renheApplication.getUserInfo().getSid());
//						reqParams.put("minObjectId",getSharedPreferences(getPackageName(), 0).getString("InnerMsgMessageObjectId", ""));
						reqParams.put("adSId", renheApplication.getUserInfo().getAdSId());
						NewInnerMessage mb = null;
						try {
							if(-1 != NetworkUtil.hasNetworkConnection(RenheService.this)) {
								mb = (NewInnerMessage) HttpUtil.doHttpRequest(Constants.Http.INNERMSG_CHECKUNREADMESSAGE, reqParams, NewInnerMessage.class,RenheService.this);
								if (mb != null && mb.getState() == 1) {
									SharedPreferences msp = getSharedPreferences("setting_info", 0);
									Editor editor = msp.edit();
										editor.putInt("newmsg_unreadmsg_num",mb.getCount());
										Intent intent = new Intent(MeunFragment.NEWMSG_ICON_ACTION);
										intent.putExtra("newmsg_notice_num", mb.getCount());
										RenheService.this.sendBroadcast(intent);
										editor.commit();
										editor = null;
//									editor.putString("InnerMsgMessageObjectId",mb.getMaxMessageObjectId());
									
								}
							}else {
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							reqParams = null;
							mb = null;
						}
					}
			});
			mThread.start();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		isContinue = false;
	}
	
	/**
	 * 新站内信提醒
	 * @param title 提醒标题
	 * @param message 提醒消息内容
	 * @param tone	是否提示声音
	 * @param intent 准备数据
	 * @param notiId 提示ID
	 */
	private void sendNotifaction(String title, String message, boolean tone,
			Intent intent, int notiId) {

		Notification notification = new Notification(R.drawable.icon, // Icon to
				"人和网", // Text
				System.currentTimeMillis() // When to display - i.e. now
		);

		PendingIntent pi = PendingIntent.getActivity(this, notiId, intent, 0);

		// notification.defaults |= Notification.DEFAULT_ALL ;
		if (getSharedPreferences("setting_info", 0)
				.getBoolean("warnshake", true)) {
			notification.defaults |= Notification.DEFAULT_VIBRATE;
		}
		if (getSharedPreferences("setting_info", 0)
				.getBoolean("led", true)) {
			notification.defaults |= Notification.DEFAULT_LIGHTS;
		}
		if (tone) {
			if (getSharedPreferences("setting_info", 0)
					.getBoolean("msgnotify", true)) {
				notification.defaults |= Notification.DEFAULT_SOUND;
			}
		}
		notification.flags = Notification.FLAG_SHOW_LIGHTS
				| Notification.FLAG_AUTO_CANCEL;

		// Add to the Notification
		notification.setLatestEventInfo(getApplicationContext(), title, // Title
				// of
				// detail
				// view
				message, // Text on detail view
				pi);
		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(notiId, notification); // ID_HELLO_WORLD is a
	}

}
