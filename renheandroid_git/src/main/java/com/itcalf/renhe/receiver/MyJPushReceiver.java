package com.itcalf.renhe.receiver;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.view.View;
import cn.jpush.android.api.JPushInterface;

import com.itcalf.renhe.R;
import com.itcalf.renhe.RenheApplication;
import com.itcalf.renhe.cache.CacheManager;
import com.itcalf.renhe.context.fragmentMain.MainFragment;
import com.itcalf.renhe.context.fragmentMain.MeunFragment;
import com.itcalf.renhe.context.room.RoomsActivity;
import com.itcalf.renhe.log.Logger;
import com.itcalf.renhe.log.LoggerFactory;
import com.itcalf.renhe.utils.SimpleAsyncImageLoad;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * Title: MyJPushReceiver.java<br>
 * Description: <br>
 * Copyright (c) 人和网版权所有 2013<br>
 * Create DateTime: 2014-04-06 下午3:40:25<br>
 * 
 * @author wangning
 */

public class MyJPushReceiver extends BroadcastReceiver {

	protected final static Logger log = LoggerFactory.getInstance(MyJPushReceiver.class);

	private static final String TAG = "MyJPushReceiver";
	String icon_name;
	String icon_path;
	private SharedPreferences msp;
	private SharedPreferences.Editor mEditor;
	private static final int INNERMSG_PUSH = 1; // 站内信推送
	private static final int MESSAGENUM_PUSH = 2; // 新消息数目推送
	private static final int VIPCHECKPASS_PUSH = 3; // 会员审核通过推送

	@Override
	public void onReceive(final Context context, Intent intent) {
		msp = context.getSharedPreferences("setting_info", 0);
		mEditor = msp.edit();
		final Bundle bundle = intent.getExtras();
		if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
			String jsonExtra = bundle.getString(JPushInterface.EXTRA_EXTRA);
			try {
				JSONObject jsonObject = new JSONObject(jsonExtra);
				int type = jsonObject.getInt("bizType");

				if (type == MESSAGENUM_PUSH) {
					int messageNum = jsonObject.getInt("count");
					Intent intent2 = new Intent(MeunFragment.ICON_ACTION);
					intent2.putExtra("notice_num", messageNum);
					context.sendBroadcast(intent2);
					mEditor.putInt("unreadmsg_num", messageNum);
					mEditor.commit();
				} else if (type == INNERMSG_PUSH || type == VIPCHECKPASS_PUSH) {
					if (RenheApplication.getInstance().getmUserInfo() != null && msp.getBoolean("msgnotify", true)) {
						String imageUrl = jsonObject.getString("notifyImg");
						if (type == INNERMSG_PUSH) {
							if (TextUtils.isEmpty(imageUrl)) {
								showNotify(context, bundle.getString(JPushInterface.EXTRA_MESSAGE),
										bundle.getString(JPushInterface.EXTRA_TITLE), type, null);
							} else {
								//								System.out.println("imageUrl 不= 空");
								//								ImageLoader imageLoader = ImageLoader.getInstance();
								//								try {
								//									imageLoader.loadImage(imageUrl, new AnimateFirstDisplayListener(context,bundle,imageUrl));
								//								} catch (Exception e) {
								//									e.printStackTrace();
								//								}

								SimpleAsyncImageLoad.loadDrawable(null, imageUrl, RenheApplication.getInstance().getUserInfo()
										.getEmail(), 70, 70, context, new SimpleAsyncImageLoad.ImageCallback() {

									@Override
									public void imageLoaded(Drawable imageDrawable, String imageUrl) {
										if (null == imageDrawable) {
											showNotify(context, bundle.getString(JPushInterface.EXTRA_MESSAGE),
													bundle.getString(JPushInterface.EXTRA_TITLE), INNERMSG_PUSH, null);
										} else {
											BitmapDrawable drawable = (BitmapDrawable) imageDrawable;
											showNotify(context, bundle.getString(JPushInterface.EXTRA_MESSAGE),
													bundle.getString(JPushInterface.EXTRA_TITLE), INNERMSG_PUSH,
													drawable.getBitmap());
										}
									}
								});

							}
						} else if (type == VIPCHECKPASS_PUSH) {
							showVipNotify(context, bundle.getString(JPushInterface.EXTRA_MESSAGE),
									bundle.getString(JPushInterface.EXTRA_TITLE), type);
						}
					}
					if (type == INNERMSG_PUSH) {
						SharedPreferences sp = context.getSharedPreferences("setting_info", 0);
						Editor editor = sp.edit();
						int messageNum = jsonObject.getInt("totalUnReadCount");
						editor.putInt("newmsg_unreadmsg_num", messageNum);
						editor.commit();
						Intent intent2 = new Intent(MeunFragment.NEWMSG_ICON_ACTION);
						intent2.putExtra("newmsg_notice_num", messageNum);
						context.sendBroadcast(intent2);

					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

		} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
		} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
			// 在这里可以自己写代码去定义用户点击后的行为

		} else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {

		}
	}

	void showNotify(Context context, String title, String content, int type, Bitmap imageBitmap) {
		Notification.Builder mNotificationBuilder = null;
		msp = RenheApplication.getInstance().getSharedPreferences("notify_id", Context.MODE_PRIVATE);
		int notifyId = msp.getInt("notify_id", 0);
		int notifyNum = msp.getInt("notify_num", 1);
		mEditor = msp.edit();
		mEditor.putInt("notify_id", notifyId + 1);
		mEditor.putInt("notify_num", notifyNum + 1);
		mEditor.commit();

		if (type == INNERMSG_PUSH) { //1——站内信推送
			RenheApplication application = RenheApplication.getInstance();
			Intent newIntent = new Intent(context, MainFragment.class);
			newIntent.putExtra("fromNotify", true);
			newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			PendingIntent contentReceiver = PendingIntent.getActivity(context, 0, newIntent, 0);

			if (Build.VERSION.SDK_INT >= 14) {
				mNotificationBuilder = new Notification.Builder(application).setTicker("您有新站内信").setContentTitle(title)
						.setContentText(content).setContentIntent(contentReceiver).setSmallIcon(R.drawable.logo_24x24)
						.setWhen(System.currentTimeMillis());
				mNotificationBuilder.setNumber(notifyNum);
				if (imageBitmap == null) {
					mNotificationBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.icon));
				} else {
					mNotificationBuilder.setLargeIcon(imageBitmap);
				}

				if (null != mNotificationBuilder) {
					Notification n = mNotificationBuilder.getNotification();
					n.flags |= Notification.FLAG_AUTO_CANCEL;
					if (context.getSharedPreferences("setting_info", 0).getBoolean("sound", true)) {
						n.defaults |= Notification.DEFAULT_SOUND;
					}
					if (context.getSharedPreferences("setting_info", 0).getBoolean("led", true)) {
						n.defaults |= Notification.DEFAULT_LIGHTS;
					}
					if (context.getSharedPreferences("setting_info", 0).getBoolean("warnshake", true)) {
						n.defaults |= Notification.DEFAULT_VIBRATE;
					}

					NotificationManager mNotificationManager = (NotificationManager) context
							.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
					mNotificationManager.notify(0, n);// 通知一下才会生效
				}
			} else {

				mNotificationBuilder = new Notification.Builder(application);
				Notification n = mNotificationBuilder.getNotification();
				n.tickerText = "您有新站内信";
				n.setLatestEventInfo(context, title, content, contentReceiver);
				n.icon = R.drawable.logo_24x24;
				if (imageBitmap == null) {
					n.largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon);
				} else {
					n.largeIcon = imageBitmap;
				}
				n.when = System.currentTimeMillis();
				n.number = notifyNum;
				n.flags |= Notification.FLAG_AUTO_CANCEL;
				if (context.getSharedPreferences("setting_info", 0).getBoolean("sound", true)) {
					n.defaults |= Notification.DEFAULT_SOUND;
				}
				if (context.getSharedPreferences("setting_info", 0).getBoolean("led", true)) {
					n.defaults |= Notification.DEFAULT_LIGHTS;
				}
				if (context.getSharedPreferences("setting_info", 0).getBoolean("warnshake", true)) {
					n.defaults |= Notification.DEFAULT_VIBRATE;
				}

				NotificationManager mNotificationManager = (NotificationManager) context
						.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
				mNotificationManager.notify(0, n);// 通知一下才会生效
			}

		}

	}

	void showVipNotify(Context context, String title, String content, int type) {
		title = "人和网";
		Notification.Builder mNotificationBuilder = null;
		if (type == VIPCHECKPASS_PUSH) {//3——会员通过审核通过推送
			Intent i = new Intent(context, MainFragment.class); //自定义打开的界面
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			PendingIntent contentReceiver = PendingIntent.getActivity(context, 0, i, 0);
			if (Build.VERSION.SDK_INT >= 14) {
				mNotificationBuilder = new Notification.Builder(context).setTicker("您已通过人和网会员审核").setContentTitle(title)
						.setContentText(content).setContentIntent(contentReceiver)
						.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.icon))
						.setSmallIcon(R.drawable.logo_24x24).setWhen(System.currentTimeMillis());
				if (null != mNotificationBuilder) {
					Notification n = mNotificationBuilder.getNotification();
					n.flags |= Notification.FLAG_AUTO_CANCEL;
					if (context.getSharedPreferences("setting_info", 0).getBoolean("sound", true)) {
						n.defaults |= Notification.DEFAULT_SOUND;
					}
					if (context.getSharedPreferences("setting_info", 0).getBoolean("led", true)) {
						n.defaults |= Notification.DEFAULT_LIGHTS;
					}
					if (context.getSharedPreferences("setting_info", 0).getBoolean("warnshake", true)) {
						n.defaults |= Notification.DEFAULT_VIBRATE;
					}
					NotificationManager mNotificationManager = (NotificationManager) context
							.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
					mNotificationManager.notify(1000, n);// 通知一下才会生效
				}
			} else {
				mNotificationBuilder = new Notification.Builder(context);
				Notification n = mNotificationBuilder.getNotification();
				n.tickerText = "您已通过人和网会员审核";
				n.setLatestEventInfo(context, title, content, contentReceiver);
				n.icon = R.drawable.logo_24x24;
				n.largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon);
				n.when = System.currentTimeMillis();
				n.flags |= Notification.FLAG_AUTO_CANCEL;
				if (context.getSharedPreferences("setting_info", 0).getBoolean("sound", true)) {
					n.defaults |= Notification.DEFAULT_SOUND;
				}
				if (context.getSharedPreferences("setting_info", 0).getBoolean("led", true)) {
					n.defaults |= Notification.DEFAULT_LIGHTS;
				}
				if (context.getSharedPreferences("setting_info", 0).getBoolean("warnshake", true)) {
					n.defaults |= Notification.DEFAULT_VIBRATE;
				}

				NotificationManager mNotificationManager = (NotificationManager) context
						.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
				mNotificationManager.notify(1000, n);// 通知一下才会生效
			}
		}
	}

	public class AnimateFirstDisplayListener extends SimpleImageLoadingListener {
		private Bundle bundle;
		private Context context;
		private String imageUrl;

		public AnimateFirstDisplayListener(Context context, Bundle bundle, String url) {
			this.bundle = bundle;
			this.context = context;
			this.imageUrl = url;
		}

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			System.out.println("onLoadingComplete");
			//			Bitmap userImage = getUserPic(context, imageUrl);
			showNotify(context, bundle.getString(JPushInterface.EXTRA_MESSAGE), bundle.getString(JPushInterface.EXTRA_TITLE),
					INNERMSG_PUSH, loadedImage);
		}

		@Override
		public void onLoadingStarted(String imageUri, View view) {
			super.onLoadingStarted(imageUri, view);

		}

		@Override
		public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
			super.onLoadingFailed(imageUri, view, failReason);
			System.out.println("onLoadingFailed");
			showNotify(context, bundle.getString(JPushInterface.EXTRA_MESSAGE), bundle.getString(JPushInterface.EXTRA_TITLE),
					INNERMSG_PUSH, null);
		}

		@Override
		public void onLoadingCancelled(String imageUri, View view) {
			super.onLoadingCancelled(imageUri, view);
			System.out.println("onLoadingCancelled");
			showNotify(context, bundle.getString(JPushInterface.EXTRA_MESSAGE), bundle.getString(JPushInterface.EXTRA_TITLE),
					INNERMSG_PUSH, null);
		}

	}

	private Bitmap getUserPic(Context context, String userFaceUrl) {
		//		String fileName = getWebPath(this.userFaceUrl);
		ImageLoader imageLoader = ImageLoader.getInstance();
		String fileName = null; //使用Universal ImageLoader后的缓存目录
		if (null != userFaceUrl) {
			fileName = imageLoader.getDiscCache().get(userFaceUrl).getPath();
		}
		if (null != fileName && null != CacheManager.getExternalCacheDir(context)) {
			//			File file = new File(ExternalStorageUtil.getCacheAvatarPath(this, getRenheApplication().getUserInfo().getEmail())
			//					+ fileName);
			File file = new File(fileName);
			if (null != file && file.isFile()) {
				//				Bitmap mbitmap = BitmapFactory.decodeFile(ExternalStorageUtil.getCacheAvatarPath(this, getRenheApplication()
				//						.getUserInfo().getEmail())
				//						+ fileName);
				Bitmap mbitmap = BitmapFactory.decodeFile(fileName);
				return mbitmap;
			} else {
				//放人和网logo
				Bitmap mlogo = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon);
				return mlogo;
			}
			//目前分享好友也是放人和网应用图标，如果后期想改回好友头像，将上面代码解除注释
			//			Bitmap mlogo1 = BitmapFactory.decodeResource(getResources(), R.drawable.icon_134);
			//			return mlogo1;
		} else {
			//放人和网logo
			Bitmap mlogo1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon);
			return mlogo1;
		}
	}
}
