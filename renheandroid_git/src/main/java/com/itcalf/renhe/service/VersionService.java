package com.itcalf.renhe.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.RemoteViews;

import com.itcalf.renhe.R;
import com.itcalf.renhe.utils.NetworkUtil;
import com.itcalf.renhe.utils.ToastUtil;

/**
 * Feature:人和网版本更新服务
 * Desc:人和网版本更新服务，提供新版本检查/下载功能
 * @author xp
 *
 */
public class VersionService extends Service {

	IVersionUpdate.Stub mVersionCheck;
	//下载块大小
	private final static int DOWNLOAD_FILE_SIZE = 1024 * 10; // 下载块大小：1K

	@Override
	public IBinder onBind(Intent intent) {
		return mVersionCheck;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		if (null == mVersionCheck) {
			mVersionCheck = new IVersionUpdate.Stub() {
				@Override
				public void checkVersionUpdate(final String fileUrl)
						throws RemoteException {
					if(-1 != NetworkUtil.hasNetworkConnection(VersionService.this)) {
						new Thread(new Runnable() {
							@Override
							public void run() {
								try {
									startDownload(fileUrl);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}).start();
					}else {
						ToastUtil.showNetworkError(VersionService.this);
					}
				}
			};
		}
	}

	Notification notification;
	NotificationManager notificationManager;
	int DOWNLOAD_ID = 1;
	int icon;

	/**
	 * 启动新版本下载
	 * @param fileUrl 下载地址
	 */
	private void startDownload(String fileUrl) {
		HttpURLConnection urlConn = null;
		InputStream inputStream = null;
		FileOutputStream fileOutput = null;
		try {
			downloadNotification();
			URL url = new URL(fileUrl);
			urlConn = (HttpURLConnection) url.openConnection();
			File file = new File(Environment.getExternalStorageDirectory(),
					"renhe.apk");
			fileOutput = new FileOutputStream(file);
			inputStream = urlConn.getInputStream();
			byte[] buffer = new byte[DOWNLOAD_FILE_SIZE];
			long length = urlConn.getContentLength();
			long downSize = 0;
			float totalSize = length;
			int percent = 0;
			do {
				int numread = inputStream.read(buffer);
				if (numread == -1) {
					break;
				}
				fileOutput.write(buffer, 0, numread);
				downSize += numread;
				int nowPercent = (int) ((downSize / totalSize) * 100);
				// 如果百分比有变动则更新进度条
				if (nowPercent > percent) {
					percent = nowPercent;
					// 更新
					updateProgressBar(percent);
				}

			} while (true);
			fileOutput.flush();
			// 完成
			updateProgressBar(0);
		} catch (Exception e) {
			// 异常取消
			updateProgressBar(-1);
			e.printStackTrace();
		} finally {
			try {
				fileOutput.close();
				inputStream.close();
				fileOutput = null;
				inputStream = null;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * 下载进度通知
	 */
	private void downloadNotification() {
		String ns = Context.NOTIFICATION_SERVICE;
		notificationManager = (NotificationManager) getSystemService(ns);
		icon = android.R.drawable.stat_sys_download;
		// the text that appears first on the status bar
		String tickerText = "人和网下载中...";
		long time = System.currentTimeMillis();
		notification = new Notification(icon, tickerText, time);
		// the text that needs to change
		Intent notificationIntent = new Intent(Intent.ACTION_VIEW);
		notificationIntent.setType("audio/*");
		notification.contentView = new RemoteViews(getPackageName(),R.layout.versionupdate);
		notification.contentView.setTextViewText(R.id.downloadText, tickerText);
		PendingIntent contentIntent = PendingIntent.getActivity(VersionService.this, 0,
				notificationIntent, 0);
		notification.contentIntent = contentIntent;
		notificationManager.notify(DOWNLOAD_ID, notification);

	}

	/**
	 * 更新进度条状态
	 * @param percent
	 */
	private void updateProgressBar(int percent) {
		switch (percent) {
		case 0:
			notificationManager.cancel(DOWNLOAD_ID);
			openFile(VersionService.this, new File(Environment.getExternalStorageDirectory(),
					"renhe.apk"));
			break;
		case -1:
			notification.flags = Notification.FLAG_AUTO_CANCEL;
			notification.icon = android.R.drawable.stat_notify_error;
			notification.contentView.setImageViewResource(R.id.downloadImg,
					android.R.drawable.stat_sys_warning);
			notification.contentView.setTextViewText(R.id.downloadProgress, "网络异常，停止下载!");
			notification.contentView.setTextColor(R.id.downloadProgress, Color.RED);
			notificationManager.notify(DOWNLOAD_ID, notification);
			break;
		case 100:
			notification.contentView.setProgressBar(R.id.downloadProgress, 100,
					percent, false);
			notification.contentView.setTextViewText(R.id.percetText, percent + "%");
			notification.contentView.setTextViewText(R.id.percetText, "下载完成");
			notificationManager.notify(DOWNLOAD_ID, notification);
			break;
		default:
			notification.contentView.setProgressBar(R.id.downloadProgress, 100,
					percent, false);
			notification.contentView.setTextViewText(R.id.percetText, percent + "%");
			notificationManager.notify(DOWNLOAD_ID, notification);
			break;
		}
	}

	/**
	 * 下载完成后打开文件，更新应用。
	 * @param act
	 * @param f
	 */
	private void openFile(Context act, File f) {
		if (f.isFile()) {
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setAction(android.content.Intent.ACTION_VIEW);
			/* 调用getMIMEType()来取得MimeType */
			String type = getMIMEType(f);
			/* 设置intent的file与MimeType */
			intent.setDataAndType(Uri.fromFile(f), type);
			act.startActivity(intent);
		}
	}

	/* 判断文件MimeType的method */
	private static String getMIMEType(File f) {
		String type = "";
		String fName = f.getName();
		/* 取得扩展名 */
		String end = fName
				.substring(fName.lastIndexOf(".") + 1, fName.length())
				.toLowerCase();
		/* 依扩展名的类型决定MimeType */
		if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
				|| end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
			type = "audio";
		} else if (end.equals("3gp") || end.equals("mp4")) {
			type = "video";
		} else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
				|| end.equals("jpeg") || end.equals("bmp")) {
			type = "image";
		} else if (end.equals("apk")) {
			/* android.permission.INSTALL_PACKAGES */
			type = "application/vnd.android.package-archive";
		} else {
			type = "*";
		}
		/* 如果无法直接打开，就跳出软件列表给用户选择 */
		if (end.equals("apk")) {
		} else {
			type += "/*";
		}
		return type;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (null != mVersionCheck) {
			mVersionCheck = null;
		}
	}

}
