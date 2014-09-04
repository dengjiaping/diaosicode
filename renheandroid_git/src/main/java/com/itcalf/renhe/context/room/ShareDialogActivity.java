package com.itcalf.renhe.context.room;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.itcalf.renhe.R;
import com.itcalf.renhe.cache.CacheManager;
import com.itcalf.renhe.cache.ExternalStorageUtil;
import com.itcalf.renhe.context.archives.SoftGridAdapter;
import com.itcalf.renhe.context.innermsg.SendInnerMsgActivity;
import com.itcalf.renhe.context.template.BaseActivity;
import com.itcalf.renhe.dto.Profile;
import com.itcalf.renhe.utils.WeixinUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tencent.connect.auth.QQAuth;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

/**
   * Title: DialogActivity.java<br>
   * Description: <br>
   * Copyright (c) 人和网版权所有 2014    <br>
   * Create DateTime: 2014-3-27 上午9:14:06 <br>
   * @author wangning
   */
public class ShareDialogActivity extends BaseActivity {
	private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
	GridView appGridView;
	PackageManager mPackageManager;
	SoftGridAdapter softGridAdapter;
	List<ResolveInfo> lstImageItem;
	//	private Profile mProfile;
	private String mOtherSid;
	private String userName;
	private String userDesp;
	private String userFaceUrl;
	private String userCompany;
	private String userJob;
	private String userContent;
	private String userSid;
	private String messageId;
	private boolean isFromArchieve = true;

	private String mQQAppid = "";
	private QQAuth mQQAuth;
	private int shareType = QQShare.SHARE_TO_QQ_TYPE_DEFAULT;
	private static final String SHARE_URL = "http://www.renhe.cn/messageboard/";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog);
		initData();
		appGridView = (GridView) findViewById(R.id.grid);
		appGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		if (null != getIntent().getSerializableExtra("profile")) {
			//			mProfile = (Profile) getIntent().getSerializableExtra("profile");
		}
		if (null != getIntent().getStringExtra("othersid") && !"".equals(getIntent().getStringExtra("othersid"))) {
			mOtherSid = getIntent().getStringExtra("othersid");
		}
		if (null != getIntent().getStringExtra("userName") && !"".equals(getIntent().getStringExtra("userName"))) {
			userName = getIntent().getStringExtra("userName");
		}
		if (null != getIntent().getStringExtra("userDesp") && !"".equals(getIntent().getStringExtra("userDesp"))) {
			userDesp = getIntent().getStringExtra("userDesp");
		}
		if (null != getIntent().getStringExtra("userFaceUrl") && !"".equals(getIntent().getStringExtra("userFaceUrl"))) {
			userFaceUrl = getIntent().getStringExtra("userFaceUrl");
		}
		if (null != getIntent().getStringExtra("userCompany") && !"".equals(getIntent().getStringExtra("userCompany"))) {
			userCompany = getIntent().getStringExtra("userCompany");
		}
		if (null != getIntent().getStringExtra("userJob") && !"".equals(getIntent().getStringExtra("userJob"))) {
			userJob = getIntent().getStringExtra("userJob");
		}
		if (null != getIntent().getStringExtra("userContent") && !"".equals(getIntent().getStringExtra("userContent"))) {
			userContent = getIntent().getStringExtra("userContent");
		}
		if (null != getIntent().getStringExtra("userSid") && !"".equals(getIntent().getStringExtra("userSid"))) {
			userSid = getIntent().getStringExtra("userSid");
		}
		if (null != getIntent().getStringExtra("messageId") && !"".equals(getIntent().getStringExtra("messageId"))) {
			messageId = getIntent().getStringExtra("messageId");
		}
		if (getIntent().getBooleanExtra("isFromArchieve", false)) {
			isFromArchieve = true;
		}
		mPackageManager = this.getPackageManager();
		lstImageItem = new ArrayList<ResolveInfo>();
		lstImageItem = getShareApps(this, isFromArchieve);
		softGridAdapter = new SoftGridAdapter(this, lstImageItem);
		appGridView.setAdapter(softGridAdapter);
		InputStream is = null;
		try {
			is = getAssets().open("icon_134.png");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		file_put_contents("icon_134.png", is);
		appGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				ResolveInfo resolveInfo = (ResolveInfo) arg0.getItemAtPosition(arg2);
				String itemName = resolveInfo.activityInfo.packageName;
				String itemName1 = resolveInfo.activityInfo.name;
				if (itemName.contains("renhe")) {
					callLetter();
				} else if (itemName.contains("tencent.mm") && itemName1.contains("ShareImgUI")) {//發送到微信好友
					share2Tencent(false);
				} else if (itemName.contains("tencent.mm") && itemName1.contains("ShareToTimeLineUI")) {//發送到微信朋友圈
					share2Tencent(true);
				} else if (itemName.contains("com.tencent.mobileqq")) {
					share2QQ();
				} else {
					String filePath = getWebPath(userFaceUrl);
					Uri uri = null;
					if (null == filePath) {
						//						try {
						//							InputStream is = getAssets().open("icon_108.png");
						//							file_put_contents("icon_108.png", is);
						uri = Uri.parse("file://" + Environment.getExternalStorageDirectory() + File.separator + "Android"
								+ File.separator + "data" + File.separator + "sys_ic" + File.separator + "icon_134.png");
						//						} catch (IOException e) {
						//							e.printStackTrace();
						//						}
					} else {
						filePath = ExternalStorageUtil.getCacheAvatarPath(ShareDialogActivity.this, getRenheApplication()
								.getUserInfo().getEmail())
								+ filePath;
						uri = Uri.parse("file://" + filePath);
					}
					share("http://m.renhe.cn/app/renhe.shtml", uri, itemName);
				}
				ShareDialogActivity.this.finish();
			}

		});
	}

	@Override
	protected void initData() {
		super.initData();
		mQQAppid = "100830477";
		mQQAuth = QQAuth.createInstance(mQQAppid, this.getApplicationContext());
	}

	public File file_put_contents(String file_name, InputStream is) {
		File file = null;
		if (null != is) {
			String path = Environment.getExternalStorageDirectory() + File.separator + "Android" + File.separator + "data"
					+ File.separator + "sys_ic" + File.separator;
			File mfile = new File(path);
			if (!mfile.isDirectory()) {
				mfile.mkdirs();
			}
			file = new File(path, file_name);
			OutputStream os = null;
			try {
				os = new FileOutputStream(file);
				byte buffer[] = new byte[1 * 1024];
				int len = 0;
				while ((len = is.read(buffer)) != -1) {
					os.write(buffer, 0, len);
				}
				os.flush();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					os.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return file;
	}

	private void share(String content, Uri uri, String packageName) {
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		if (uri != null) {
			shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
			shareIntent.setType("image/*");
			//当用户选择短信时使用sms_body取得文字  
			if (isFromArchieve) {
				String sharesid = this.userSid + "";
				String share = this.userName;
				content = "您的好友" + getRenheApplication().getUserInfo().getName() + "分享 人和网 " + "http://r.renhe.cn/" //好友的个人网页
						+ sharesid + share + " 给您";
				shareIntent.putExtra("sms_body", content);
			} else {
				content = "您的好友" + getRenheApplication().getUserInfo().getName() + "分享 人和网 "
						+ "http://m.renhe.cn/app/renhe.shtml 给您";
				shareIntent.putExtra("sms_body", content);
			}
		} else {
			shareIntent.setType("text/plain");
		}
		shareIntent.setPackage(packageName);
		shareIntent.putExtra(Intent.EXTRA_TEXT, content);
		startActivity(shareIntent);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	public List<ResolveInfo> getShareApps(Context context, boolean isFromArchieve) {
		List<ResolveInfo> mApps = new ArrayList<ResolveInfo>();
		Intent intent = new Intent(Intent.ACTION_SEND, null);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setType("image/*");
		PackageManager pManager = context.getPackageManager();
		mApps = pManager.queryIntentActivities(intent, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
		List<ResolveInfo> list = new ArrayList<ResolveInfo>();
		ResolveInfo innerMsgResolve = new ResolveInfo();
		ActivityInfo activityInfo = new ActivityInfo();
		activityInfo.packageName = "com.renhe.cn";
		activityInfo.icon = R.drawable.main_41;
		activityInfo.name = "com.renhe.cn";
		innerMsgResolve.activityInfo = activityInfo;

		for (int i = 0; i < mApps.size(); i++) {
			ResolveInfo resolveInfo = mApps.get(i);
			String packName = resolveInfo.activityInfo.packageName;
			if (packName.contains("tencent") || packName.contains("tencent.WBlog") || packName.contains("sina.weibo")) {
				list.add(0, resolveInfo);
			} else {
				//					list.add(resolveInfo);
			}
		}

		return list;
	}

	private void share2Tencent(boolean is2TimeLine) {
		final String APP_ID = "wx6d03435b4ef6f18d";
		IWXAPI api;
		/**
		 * 注册到微信
		 */
		api = WXAPIFactory.createWXAPI(getApplicationContext(), APP_ID, true);
		api.registerApp(APP_ID);

		if (is2TimeLine) {
			int wxSdkVersion = api.getWXAppSupportAPI();
			if (wxSdkVersion < TIMELINE_SUPPORTED_VERSION) {
				Toast.makeText(ShareDialogActivity.this, "抱歉，您的微信版本暂不支持分享到朋友圈", Toast.LENGTH_LONG).show();
				return;
			}
		}
		/**
		 * 发送url
		 */
		WXWebpageObject webpage = new WXWebpageObject();
		String titleString = "";
		String contentString = "";
		String webUrl = "";
		if (isFromArchieve) {
			String sharesid = this.userSid + "";
			String share = this.userName;
			if (null == sharesid || "".equals(sharesid) || null == share || "".equals(share)) {
				Toast.makeText(this, "分享失败", Toast.LENGTH_SHORT).show();
				return;
			}
			webUrl = SHARE_URL + messageId;
			titleString = this.userName + " " + this.userJob + " " + this.userCompany + "公司 " + "的人和网留言";//好友姓名
			contentString = this.userContent;
		} else {
			titleString = "人和网-人脉成就事业";
			contentString = "通过人和网您可以快速找到各种有价值的商务人脉，并且随时随地和您的人脉保持连接！目前人和网上有1000万高端会员，他们都是您潜在的合作伙伴。";
			webUrl = "http://m.renhe.cn/app/renhe.shtml";
		}
		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = titleString;
		msg.description = contentString;
		webpage.webpageUrl = webUrl;
		Bitmap thumb;//好友头像
		if(isFromArchieve){
			thumb = getUserPic();
		}else{
			thumb = getLogoPic();
		}
		if (thumb != null) {
			msg.thumbData = WeixinUtil.bmpToByteArray(thumb, true);
			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = buildTransaction("webpage");
			req.message = msg;
			req.scene = is2TimeLine ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
			api.sendReq(req);
		} else {
			Toast.makeText(this, "分享失败", Toast.LENGTH_SHORT).show();
		}

	}

	private void share2QQ() {
		String titleString = "";
		String contentString = "";
		String targetUrl = "http://m.renhe.cn/app/renhe.shtml";
		int mExtarFlag = 0x00;
		if (isFromArchieve) {
			String sharesid = this.userSid + "";
			String share = this.userName;
			if (null == sharesid || "".equals(sharesid) || null == share || "".equals(share)) {
				Toast.makeText(this, "分享失败", Toast.LENGTH_SHORT).show();
				return;
			}
			targetUrl = SHARE_URL + messageId;
			titleString = this.userName + " " + this.userJob + " " + this.userCompany + "公司 " + "的人和网留言";//好友姓名
			contentString = this.userContent;
		} else {
			titleString = "人和网-人脉成就事业";
			contentString = "通过人和网您可以快速找到各种有价值的商务人脉，并且随时随地和您的人脉保持连接！目前人和网上有1000万高端会员，他们都是您潜在的合作伙伴。";
		}
		final Bundle params = new Bundle();
		if (shareType != QQShare.SHARE_TO_QQ_TYPE_IMAGE) {
			params.putString(QQShare.SHARE_TO_QQ_TITLE, titleString);
			params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, targetUrl);
			params.putString(QQShare.SHARE_TO_QQ_SUMMARY, contentString);
		}
		if (shareType == QQShare.SHARE_TO_QQ_TYPE_IMAGE) {
			//             params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, imageUrl.getText().toString());
		} else {
			if(isFromArchieve){
				params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, getUserPicPath());
			}else{
				params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, Environment.getExternalStorageDirectory() + File.separator + "Android" + File.separator + "data"
						+ File.separator + "sys_ic" + File.separator + "icon_134.png");
			}
		}
		params.putString(shareType == QQShare.SHARE_TO_QQ_TYPE_IMAGE ? QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL
				: QQShare.SHARE_TO_QQ_IMAGE_URL, getUserPicPath());
		params.putString(QQShare.SHARE_TO_QQ_APP_NAME, getString(R.string.app_name));
		params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, shareType);
		params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, mExtarFlag);
		if (shareType == QQShare.SHARE_TO_QQ_TYPE_AUDIO) {
			//             params.putString(QQShare.SHARE_TO_QQ_AUDIO_URL, mEditTextAudioUrl.getText().toString());
		}
		if ((mExtarFlag & QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN) != 0) {
			//             showToast("在好友选择列表会自动打开分享到qzone的弹窗~~~");
		} else if ((mExtarFlag & QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE) != 0) {
			//             showToast("在好友选择列表隐藏了qzone分享选项~~~");
		}
		doShareToQQ(params);
	}

	/**
	 * 用异步方式启动分享
	 * @param params
	 */
	private void doShareToQQ(final Bundle params) {

		final QQShare mQQShare = new QQShare(this, mQQAuth.getQQToken());
		final Activity activity = ShareDialogActivity.this;
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				mQQShare.shareToQQ(activity, params, new IUiListener() {

					@Override
					public void onCancel() {
						if (shareType != QQShare.SHARE_TO_QQ_TYPE_IMAGE) {
							//                    		Util.toastMessage(activity, "onCancel: ");
						}
					}

					@Override
					public void onComplete(Object response) {
						//                        Util.toastMessage(activity, "onComplete: " + response.toString());
					}

					@Override
					public void onError(UiError e) {
						//                        Util.toastMessage(activity, "onError: " + e.errorMessage, "e");
						Toast.makeText(ShareDialogActivity.this, "分享失败", Toast.LENGTH_SHORT).show();
					}

				});
			}
		}).start();
	}
	private Bitmap getLogoPic() {
		String fileName = getWebPath(this.userFaceUrl);
			//放人和网logo
			Bitmap mlogo1 = BitmapFactory.decodeResource(getResources(), R.drawable.icon_134);
			return mlogo1;
	}
	private Bitmap getUserPic() {
//		String fileName = getWebPath(this.userFaceUrl);
		ImageLoader imageLoader = ImageLoader.getInstance();
		String fileName = null; //使用Universal ImageLoader后的缓存目录
		if(null != this.userFaceUrl){
			fileName = imageLoader.getDiscCache().get(this.userFaceUrl).getPath();
		}
		if (null != fileName && null != CacheManager.getExternalCacheDir(this)) {
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
				Bitmap mlogo = BitmapFactory.decodeResource(getResources(), R.drawable.icon_134);
				return mlogo;
			}
			//目前分享好友也是放人和网应用图标，如果后期想改回好友头像，将上面代码解除注释
//			Bitmap mlogo1 = BitmapFactory.decodeResource(getResources(), R.drawable.icon_134);
//			return mlogo1;
		} else {
			//放人和网logo
			Bitmap mlogo1 = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
			return mlogo1;
		}
	}

	private String getUserPicPath() {
		String path = "";
//		String fileName = getWebPath(this.userFaceUrl);//之前老的图片缓存机制图片目录
		ImageLoader imageLoader = ImageLoader.getInstance();
		String fileName = null; //使用Universal ImageLoader后的缓存目录
		if(null != this.userFaceUrl){
			fileName = imageLoader.getDiscCache().get(this.userFaceUrl).getPath();
		}
		if (null != fileName && null != CacheManager.getExternalCacheDir(this)) {
//			File file = new File(ExternalStorageUtil.getCacheAvatarPath(this, getRenheApplication().getUserInfo().getEmail())
//					+ fileName);
			if(fileName.endsWith(".jpg") || fileName.endsWith(".png")){
				fileName = fileName.substring(0,fileName.length() - 4);
			}
			
			File file = new File(fileName);
			if (null != file && file.isFile()) {
//				path = ExternalStorageUtil.getCacheAvatarPath(this, getRenheApplication().getUserInfo().getEmail()) + fileName;
				path = file.getAbsolutePath();
			} else {
				//放人和网logo
				path = Environment.getExternalStorageDirectory() + File.separator + "Android" + File.separator + "data"
						+ File.separator + "sys_ic" + File.separator + "icon_134.png";
			}
		} else {
			//放人和网logo
			path = Environment.getExternalStorageDirectory() + File.separator + "Android" + File.separator + "data"
					+ File.separator + "sys_ic" + File.separator + "icon_134.png";
		}
		return path;
	}

	private String getWebPath(String webPath) {
		if (null != webPath && !"".equals(webPath)) {
			String id = webPath.substring(webPath.indexOf("/") + 2);
			id = id.replaceAll("/", "_");
			return id;
		}
		return null;
	}

	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}

	private void callLetter() {
		if (isFromArchieve) {
			Bundle bundle = new Bundle();
			bundle.putString("share", this.userName);
			bundle.putString("sharesid", this.userSid);
			startActivity(SendInnerMsgActivity.class, bundle);
		} else {
			Bundle bundle = new Bundle();
			bundle.putString("share", "人和网");
			bundle.putString("sharesid", "");
			startActivity(SendInnerMsgActivity.class, bundle);
		}
	}
}
