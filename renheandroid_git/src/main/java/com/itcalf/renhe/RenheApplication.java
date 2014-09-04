package com.itcalf.renhe;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import cn.jpush.android.api.JPushInterface;

import com.itcalf.renhe.command.IContactCommand;
import com.itcalf.renhe.command.IMessageBoardCommand;
import com.itcalf.renhe.command.IPhoneCommand;
import com.itcalf.renhe.command.IProfileCommand;
import com.itcalf.renhe.command.IUserCommand;
import com.itcalf.renhe.command.impl.ContactCommandImpl;
import com.itcalf.renhe.command.impl.MessageBoardCommandImpl;
import com.itcalf.renhe.command.impl.PhoneCommandImpl;
import com.itcalf.renhe.command.impl.ProfileCommandImpl;
import com.itcalf.renhe.command.impl.UserCommandImpl;
import com.itcalf.renhe.dto.FollowState;
import com.itcalf.renhe.dto.SearchCity;
import com.itcalf.renhe.dto.UserInfo;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.umeng.analytics.MobclickAgent;

public class RenheApplication extends Application {

	public static final String PREFS_NAME = "PrefsFile";
	// 该变量为站内信删除后加入id,回退列表时回调，动态删除
	public static List<String> INNER_DLETE_MSG = new ArrayList<String>();
	private UserInfo mUserInfo;
	private IUserCommand mUser;
	private IMessageBoardCommand mMessageBoard;
	private IProfileCommand mProfile;
	private IContactCommand mContact;
	private IPhoneCommand mPhone;
	public static int FLAG_NOTIFICATION = 15434;
	private FollowState mFollowState;//人和网关注状态：服务中初始化，每次点击更多的时候后台更新
	public static RenheApplication renheApplication;
	public long enterRoomTime = 0;
	public long enterFriendTime = 0;
	public long enterColleagueTime = 0;
	public long enterCityTime = 0;
	public long enterFollowTime = 0;

	private static final String LOG_DIR = Environment.getExternalStorageDirectory() + File.separator + "renhe_android_log";
	private static final String LOG_NAME = "renhe_android_log.TXT";

	private int accountType = 0;

	@Override
	public void onCreate() {
		super.onCreate();
		if (renheApplication == null) {
			renheApplication = this;
		}
		JPushInterface.setDebugMode(false);
		JPushInterface.init(this);
//		if (!Constants.renhe_log) {
//			Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);
//		}
		MobclickAgent.openActivityDurationTrack(false);
		initImageLoader(getApplicationContext());
	}
	

	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you may tune some of them,
		// or you can create default configuration by
		//  ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory().diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO).writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}

	/**
	     * 捕获错误信息的handler
	     */
	private UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler() {

		@Override
		public void uncaughtException(Thread thread, Throwable ex) {
			String info = null;
			ByteArrayOutputStream baos = null;
			PrintStream printStream = null;
			try {
				baos = new ByteArrayOutputStream();
				printStream = new PrintStream(baos);
				ex.printStackTrace(printStream);
				byte[] data = baos.toByteArray();
				info = new String(data);
				data = null;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (printStream != null) {
						printStream.close();
					}
					if (baos != null) {
						baos.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			writeErrorLog(info);
			//			Intent intent = new Intent(getApplicationContext(), CollapseActivity.class);
			//			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			//			startActivity(intent);
		}
	};

	/**
	     * 向文件中写入错误信息
	     * 
	     * @param info
		     */

	protected void writeErrorLog(String info) {
		File dir = new File(LOG_DIR);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File file = new File(dir, getCurrentDateString() + LOG_NAME);
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(file, true);
			fileOutputStream.write(info.getBytes());
			fileOutputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	     * 获取当前日期
	     * 
	 * @return
	 */
	private static String getCurrentDateString() {
		String result = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm", Locale.getDefault());
		Date nowDate = new Date();
		result = sdf.format(nowDate);
		result = result.replaceAll(" ", "_");
		return result.trim();
	}

	public static RenheApplication getInstance() {
		return renheApplication;
	}

	public UserInfo getUserInfo() {
		if (null == mUserInfo) {
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
			mUserInfo = new UserInfo();
			mUserInfo.setAdSId(settings.getString("adSId", ""));
			mUserInfo.setCompany(settings.getString("company", ""));
			mUserInfo.setEmail(settings.getString("email", ""));
			mUserInfo.setId(settings.getLong("id", 0));
			mUserInfo.setName(settings.getString("name", ""));
			mUserInfo.setSid(settings.getString("sid", ""));
			mUserInfo.setTitle(settings.getString("title", ""));
			mUserInfo.setUserface(settings.getString("userface", ""));
			settings = null;
		}
		return mUserInfo;
	}

	public void setUserInfo(UserInfo mUserInfo) {
		//		this.mUserInfo = mUserInfo;
		setmUserInfo(mUserInfo);
		if (null != mUserInfo) {
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString("name", mUserInfo.getName());
			editor.putString("adSId", mUserInfo.getAdSId());
			editor.putString("company", mUserInfo.getCompany());
			editor.putString("email", mUserInfo.getEmail());
			editor.putString("sid", mUserInfo.getSid());
			editor.putString("title", mUserInfo.getTitle());
			editor.putString("userface", mUserInfo.getUserface());
			editor.putLong("id", mUserInfo.getId());
			editor.commit();
			editor = null;
			settings = null;
		} else {
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.clear();
			editor.commit();
			editor = null;
			settings = null;
		}
	}

	public UserInfo getmUserInfo() {
		return mUserInfo;
	}

	public SearchCity currentCity;

	public SearchCity getCurrentCity() {
		return currentCity;
	}

	public void setCurrentCity(SearchCity currentCity) {
		this.currentCity = currentCity;
	}

	public int getAccountType() {
		return accountType;
	}

	public void setAccountType(int accountType) {
		this.accountType = accountType;
	}

	public void setmUserInfo(UserInfo mUserInfo) {
		this.mUserInfo = mUserInfo;
	}

	public FollowState getFollowState() {

		if (null == mFollowState) {
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
			mFollowState = new FollowState();
			mFollowState.setState(settings.getInt("follow", 1));
			mFollowState.setFollowState(settings.getInt("followState", 1));
			settings = null;
		}
		return mFollowState;
	}

	public void setFollowState(FollowState mFollowState) {
		this.mFollowState = mFollowState;
		if (null != mFollowState) {
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putInt("follow", mFollowState.getState());
			editor.putInt("followState", mFollowState.getFollowState());
			editor.commit();
			editor = null;
			settings = null;
		} else {
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.clear();
			editor.commit();
			editor = null;
			settings = null;
		}
	}

	public IUserCommand getUserCommand() {
		if (null == mUser) {
			mUser = new UserCommandImpl(this);
		}
		return mUser;
	}

	public IMessageBoardCommand getMessageBoardCommand() {
		if (null == mMessageBoard) {
			mMessageBoard = new MessageBoardCommandImpl();
		}
		return mMessageBoard;
	}

	public IProfileCommand getProfileCommand() {
		if (null == mProfile) {
			mProfile = new ProfileCommandImpl();
		}
		return mProfile;
	}

	public IContactCommand getContactCommand() {
		if (null == mContact) {
			mContact = new ContactCommandImpl(this);
		}
		return mContact;
	}

	public IPhoneCommand getPhoneCommand() {
		if (null == mPhone) {
			mPhone = new PhoneCommandImpl();
		}
		return mPhone;
	}

	private List<Activity> activityList = new LinkedList<Activity>();

	//添加Activity 到容器中
	public void addActivity(Activity activity) {
		activityList.add(activity);
	}

	//遍历所有Activity 并finish
	public void clearActivity() {
		for (Activity mactivity : activityList) {
			mactivity.finish();
		}
	}

	public void exit() {
		//		CacheManager.IMAGE_CACHE.clear();
		for (Activity activity : activityList) {
			activity.finish();
		}
		//如果想让应用退出后，不再接受推送通知，就解除下面的注释，因为他会杀死进程，监听推送的广播将失效
//		System.exit(0);
	}

}
