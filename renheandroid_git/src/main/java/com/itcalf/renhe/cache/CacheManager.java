package com.itcalf.renhe.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.itcalf.renhe.Constants;
import com.itcalf.renhe.R;
import com.itcalf.renhe.RenheApplication;
import com.itcalf.renhe.dto.InBoxInfo;
import com.itcalf.renhe.dto.InBoxInfo.MessageList;
import com.itcalf.renhe.dto.SearchHistoryItem;
import com.itcalf.renhe.dto.SendBoxInfo;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.L;

/**
 * 缓存管理器
 * 
 * @author xp
 * 
 */
public class CacheManager {
	private static final String UNIVERSAL_IMAGELOADER_CACHE = Environment.getExternalStorageDirectory().getAbsolutePath()
			+ File.separator + "Android" + File.separator + "data" + File.separator
			+ RenheApplication.getInstance().getPackageName() + File.separator + "cache";
	private static final String TAG = "CacheManager";
	private static final String DBNAME = "newrenhemsgdb";
	private static CacheManager cache;
	private Context ct;
	public static final String PROFILE = "10001";
	private SQLiteDatabase db;
	private static final long EXPIRED_TIME = 48 * 60 * 60 * 1000;
	private static final long SEARCH_HISTORY_EXPIRED_TIME = 7 * 24 * 60 * 60 * 1000;//搜索历史过期时间是7天
	private static final String SEARCHDBNAME = "searchdb";
	private static final String SEARCHTABLE = "advancesearch";
	private SQLiteDatabase searchdb;
	private static final int SEARCH_HISTORY_MAX_COUNT = 10;//搜索历史最多显示10条,显示最新的10条

	private static final String ROOM_CACHE_FOLDER_SUFFIX = "_renhe_room_cache";

	public static CacheManager getInstance() {
		if (null == cache) {
			cache = new CacheManager();
		}
		return cache;
	}

	public CacheManager populateData(Context ct) {
		this.ct = ct;
		return cache;
	}

	/**
	 * 缓存对象
	 * 
	 * @param obj
	 * @param cacheFile
	 * @return
	 */
	public boolean saveObject(Object obj, String email, String cacheFile) {
		File suspend_f = new File(ExternalStorageUtil.getCacheDataPath(ct, email), cacheFile + ROOM_CACHE_FOLDER_SUFFIX);
		return saveSeri(obj, suspend_f);

	}

	/**
	 * 删除缓存对象
	 * @param obj
	 * @param email
	 * @param cacheFile
	 * @return wangning
	 */
	public boolean deleteObject(String email, String cacheFile) {
		File path = new File(ExternalStorageUtil.getCacheDataPath(ct, email), cacheFile + ROOM_CACHE_FOLDER_SUFFIX);
		if (path.exists()) {
			path.delete();
			return true;
		}
		return false;
	}

	private static boolean saveSeri(Object obj, File suspend_f) {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		boolean keep = true;
		try {
			fos = new FileOutputStream(suspend_f);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(obj);
			oos.flush();
		} catch (Exception e) {
			keep = false;
			Log.e(TAG, "保存到本地时Error");
		} finally {
			try {
				if (oos != null)
					oos.close();
				if (fos != null)
					fos.close();
				if (keep == false)
					suspend_f.delete();
				oos = null;
				fos = null;
				suspend_f = null;
			} catch (Exception e) {
				Log.e(TAG, "保存到本地时关闭流Error");
			}
		}
		return keep;
	}

	/**
	 * 读取缓存对象
	 * 
	 * @param path
	 * @return
	 */
	public Object getObject(String email, String path) {
		File suspend_f = new File(ExternalStorageUtil.getCacheDataPath(ct, email), path + ROOM_CACHE_FOLDER_SUFFIX);
		if (ifExpired(email, path + ROOM_CACHE_FOLDER_SUFFIX)) {
			if (isNetworkConnected(ct)) {
				return null;
			}
		}
		return getSeri(suspend_f);
	}

	public boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	private static Object getSeri(File suspend_f) {
		Object obj = null;
		FileInputStream fis = null;
		ObjectInputStream is = null;
		try {
			fis = new FileInputStream(suspend_f);
			is = new ObjectInputStream(fis);
			obj = is.readObject();
		} catch (Exception e) {
			Log.e(TAG, "读取本地Cache时关闭流Error");
		} finally {
			try {
				if (fis != null)
					fis.close();
				if (is != null)
					is.close();
				fis = null;
				is = null;
				suspend_f = null;
			} catch (Exception e) {
				Log.e(TAG, "读取本地Cache时关闭流Error");
			}
		}
		return obj;
	}

	/**
	 * 清除缓存目录
	 * 
	 * @param context
	 */
	public void clearCache(String email) {
		//		File[] dir = ct.getCacheDir().listFiles();
		//		if (dir != null) {
		//			for (File f : dir) {
		//				f.delete();
		//			}
		//		}
		//		delAllFile(ExternalStorageUtil.getCacheRootDir(ct, email));
		String path = Environment.getExternalStorageDirectory() + File.separator + "Android" + File.separator + "data"
				+ File.separator + email + File.separator + "cache" + File.separator;
		delFolder(path);

		delFolder(DEFAULT_IMAGECACHE_FOLDER);
		delFolder(UNIVERSAL_IMAGELOADER_CACHE);
		//打开或创建message.db数据库  
		//		String memail = RenheApplication.getInstance().getUserInfo().getEmail();
		//		String dbName;
		//		if(memail.indexOf("@") != -1){
		//			dbName = memail.substring(0, memail.indexOf("@") - 1);
		//		}else{
		//			dbName = memail;
		//		}
		//		dbName.replaceAll(".", "_");
		//		dbName += ".db";
		ct.deleteDatabase(DBNAME);
	}

	/**
	 * 删除文件夹
	 * 
	 * @param filePathAndName
	 *            String 文件夹路径及名称 如c:/fqf
	 * @param fileContent
	 *            String
	 * @return boolean
	 */
	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // 删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // 删除空文件夹
		} catch (Exception e) {
			Log.e(TAG, "删除文件夹Error");
			e.printStackTrace();
		}

	}

	/**
	 * 删除文件夹里面的所有文件
	 * 
	 * @param path
	 *            String 文件夹路径 如 c:/fqf
	 */
	public static void delAllFile(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return;
		}
		if (!file.isDirectory()) {
			return;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
				delFolder(path + "/" + tempList[i]);// 再删除空文件夹
			}
		}
	}

	/**
	 * 判断缓存文件是否过期 48小时
	 */
	public boolean ifExpired(String email, String path) {
		boolean expired = true;
		File suspend_f = new File(ExternalStorageUtil.getCacheDataPath(ct, email), path);
		long expiredTime = System.currentTimeMillis() - suspend_f.lastModified();
		if (expiredTime < 48 * 60 * 60 * 1000) { //缓存过期时间是48小时
			expired = false;
		}
		return expired;
	}

	//建立数据库
	public SQLiteDatabase openDB() {

		//		String email = RenheApplication.getInstance().getUserInfo().getEmail();
		//		if (email.indexOf("@") != -1) {
		//			dbName = email.substring(0, email.indexOf("@") - 1);
		//		} else {
		//			dbName = email;
		//		}
		//		dbName.replaceAll(".", "_");
		//		dbName += ".db";
		try {
			db = RenheApplication.getInstance().openOrCreateDatabase(DBNAME, Context.MODE_PRIVATE, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return db;
	}

	//建立存储max、min objectid的表
	public void createInnerMsgInboxObjIdTable(String objIdTableName) {
		if (null != db && db.isOpen()) {
			db.execSQL("CREATE TABLE if not exists " + objIdTableName + " (minObjectId VARCHAR, " + "maxObjectId VARCHAR)");
		}
	}

	public void createInnerMsgSendboxObjIdTable(String objIdTableName) {
		if (null != db && db.isOpen()) {
			db.execSQL("CREATE TABLE if not exists " + objIdTableName + " (minObjectId VARCHAR, " + "maxObjectId VARCHAR)");
		}
	}

	//创建收件箱表 
	public void createInnerMsgInboxTable(String tableName) {
		if (null != db && db.isOpen()) {
			db.execSQL("CREATE TABLE if not exists "
					+ tableName
					+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT, msid VARCHAR, "
					+ "messageObjectId VARCHAR, subject VARCHAR, content VARCHAR, "
					+ "createdDate ,read SMALLINT, sid VARCHAR, name VARCHAR, userface VARCHAR,firstreaddate VARCHAR,createTime LONG,"
					+ "accountType SMALLINT,isRealName TINYINT(1))");
		}
	}

	//创建发件箱表 
	public void createInnerMsgSendboxTable(String tableName) {
		if (null != db && db.isOpen()) {
			db.execSQL("CREATE TABLE if not exists " + tableName + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, msid VARCHAR, "
					+ "messageObjectId VARCHAR, subject VARCHAR, content VARCHAR, "
					+ "createdDate ,sid VARCHAR, name VARCHAR, userface VARCHAR,firstreaddate VARCHAR,createTime LONG,"
					+ "accountType SMALLINT,isRealName TINYINT(1))");
		}
	}

	public void saveInboxMoreCache(String tableName, InBoxInfo result) {
		if (null != db && db.isOpen()) {
			if (null != result) {
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date curDate = new Date(System.currentTimeMillis());//获取当前时间       
				String curDatestr = formatter.format(curDate);
				for (int i = 0; i < result.getMessageList().length; i++) {
					MessageList msglist = result.getMessageList()[i];
					Cursor c2 = db.rawQuery("SELECT * FROM " + tableName + " WHERE messageObjectId = ?",
							new String[] { msglist.getMessageObjectId() });
					if (!c2.moveToNext()) {
						db.execSQL(
								"INSERT INTO " + tableName + " VALUES (NULL, ?, ?,?, ?,?, ?,?, ?,?,?,?,?,?)",
								new Object[] { msglist.getMsid(), msglist.getMessageObjectId(), msglist.getSubject(),
										msglist.getContent(), msglist.getCreatedDate(), msglist.getRead(),
										msglist.getSenderInfo().getSid(), msglist.getSenderInfo().getName(),
										msglist.getSenderInfo().getUserface(), curDatestr, msglist.getCreatedTime(),
										msglist.getSenderInfo().getAccountType(), msglist.getSenderInfo().isRealname() });
					}
					c2.close();
				}
			}
		}
	}

	/**
	 * @deprecated
	 * @param tableName
	 * @param result
	 */
	public void saveInboxNewCache(String tableName, InBoxInfo result) {
		if (null != db && db.isOpen()) {
			if (null != result) {
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date curDate = new Date(System.currentTimeMillis());//获取当前时间       
				String curDatestr = formatter.format(curDate);
				for (int i = result.getMessageList().length - 1; i >= 0; i--) {
					MessageList msglist = result.getMessageList()[i];
					Cursor c2 = db.rawQuery("SELECT * FROM " + tableName + " WHERE messageObjectId = ?",
							new String[] { msglist.getMessageObjectId() });
					if (!c2.moveToNext()) {
						Cursor c3 = db.rawQuery("SELECT * FROM " + tableName, null);
						if (c3.moveToLast()) {
							db.execSQL("update " + tableName + " set _id = _id + 1 WHERE _id = ?",
									new Integer[] { c3.getInt(c3.getColumnIndex("_id")) });
							while (c3.moveToPrevious()) {
								db.execSQL("update " + tableName + " set _id = _id + 1 WHERE _id = ?",
										new Integer[] { c3.getInt(c3.getColumnIndex("_id")) });
							}
						}
						db.execSQL(
								"INSERT INTO " + tableName + " VALUES (0, ?, ?,?, ?,?, ?,?, ?,?,?)",
								new Object[] { msglist.getMsid(), msglist.getMessageObjectId(), msglist.getSubject(),
										msglist.getContent(), msglist.getCreatedDate(), msglist.getRead(),
										msglist.getSenderInfo().getSid(), msglist.getSenderInfo().getName(),
										msglist.getSenderInfo().getUserface(), curDatestr });
					}
					c2.close();
				}
			}
		}
	}

	public void saveSendboxMoreCache(String tableName, SendBoxInfo result) {
		if (null != db && db.isOpen()) {
			if (null != result) {
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date curDate = new Date(System.currentTimeMillis());//获取当前时间       
				String curDatestr = formatter.format(curDate);
				for (int i = 0; i < result.getMessageList().length; i++) {
					SendBoxInfo.MessageList msglist = result.getMessageList()[i];
					Cursor c2 = db.rawQuery("SELECT * FROM " + tableName + " WHERE messageObjectId = ?",
							new String[] { msglist.getMessageObjectId() });
					if (!c2.moveToNext()) {
						db.execSQL(
								"INSERT INTO " + tableName + " VALUES (NULL, ?, ?,?, ?,?, ?,?, ?,?,?,?,?)",
								new Object[] { msglist.getMsid(), msglist.getMessageObjectId(), msglist.getSubject(),
										msglist.getContent(), msglist.getCreatedDate(), msglist.getReceiverInfo().getSid(),
										msglist.getReceiverInfo().getName(), msglist.getReceiverInfo().getUserface(), curDatestr,
										msglist.getCreatedTime(), msglist.getReceiverInfo().getAccountType(),
										msglist.getReceiverInfo().isRealname() });
					}
					c2.close();
				}
			}
		}
	}

	/**
	 * @deprecated
	 * @param tableName
	 * @param result
	 */
	public void saveSendboxNewCache(String tableName, SendBoxInfo result) {
		if (null != db && db.isOpen()) {
			if (null != result) {
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date curDate = new Date(System.currentTimeMillis());//获取当前时间       
				String curDatestr = formatter.format(curDate);
				for (int i = result.getMessageList().length - 1; i >= 0; i--) {
					SendBoxInfo.MessageList msglist = result.getMessageList()[i];
					Cursor c2 = db.rawQuery("SELECT * FROM " + tableName + " WHERE messageObjectId = ?",
							new String[] { msglist.getMessageObjectId() });
					if (!c2.moveToNext()) {
						db.execSQL("update " + tableName + " set _id = _id + 1");
						db.execSQL(
								"INSERT INTO " + tableName + " VALUES (0, ?, ?,?, ?,?, ?,?, ?,?)",
								new Object[] { msglist.getMsid(), msglist.getMessageObjectId(), msglist.getSubject(),
										msglist.getContent(), msglist.getCreatedDate(), msglist.getReceiverInfo().getSid(),
										msglist.getReceiverInfo().getName(), msglist.getReceiverInfo().getUserface(), curDatestr });
					}
					c2.close();
				}
			}
		}
	}

	public int inOrSendBoxCacheNum(String tableName) {
		int count = 0;
		Cursor c = db.rawQuery("SELECT * FROM " + tableName, null);
		count = c.getCount();
		c.close();
		return count;
	}

	public List<Map<String, Object>> loadInboxCache(String tableName) {
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		if (null != db && db.isOpen()) {
			Cursor c = db.rawQuery("SELECT * FROM " + tableName + " ORDER BY createTime DESC ", null);
			while (c.moveToNext()) {
				int _id = c.getInt(c.getColumnIndex("_id"));
				String msid = c.getString(c.getColumnIndex("msid"));
				String messageObjectId = c.getString(c.getColumnIndex("messageObjectId"));
				String subject = c.getString(c.getColumnIndex("subject"));
				String content = c.getString(c.getColumnIndex("content"));
				String createdDate = c.getString(c.getColumnIndex("createdDate"));
				int read = c.getInt(c.getColumnIndex("read"));
				String sid = c.getString(c.getColumnIndex("sid"));
				String name = c.getString(c.getColumnIndex("name"));
				String userface = c.getString(c.getColumnIndex("userface"));
				String firstReadDate = c.getString(c.getColumnIndex("firstreaddate"));
				int accountType = c.getInt(c.getColumnIndex("accountType"));
				int isRealNameInt = c.getInt(c.getColumnIndex("isRealName"));
				boolean isRealName = false;
				if (isRealNameInt == 1) {
					isRealName = true;
				} else if (isRealNameInt == 0) {
					isRealName = false;
				}
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				try {
					Date date = dateFormat.parse(firstReadDate);
					if (System.currentTimeMillis() - date.getTime() > EXPIRED_TIME) {
						//删除数据  
						db.delete(tableName, "messageObjectId = ?", new String[] { messageObjectId });
						continue;
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
				String[] mFrom = new String[] { "headImage", "nameTv", "titleTv", "infoTv", "timeTv", "msid", "messageObjectId",
						"read", "accountType", "isRealName" };
				Map<String, Object> map = new HashMap<String, Object>();
				map.put(mFrom[0], R.drawable.avatar);
				map.put("avator_path", userface);
				map.put(mFrom[1], name);
				map.put(mFrom[2], subject);

				map.put(mFrom[3], Html.fromHtml(content));
				map.put(mFrom[4], createdDate);
				map.put(mFrom[5], msid);
				map.put(mFrom[6], messageObjectId);
				map.put(mFrom[7], read);
				map.put(mFrom[8], accountType);
				map.put(mFrom[9], isRealName);
				mList.add(map);
			}
			c.close();
			if (mList.size() == 0) {//读取缓存失败的容错，清楚存储最大、最小objectID表
				db.execSQL("DROP TABLE IF EXISTS " + Constants.DbTable.INBOX_OBJECTID);
				CacheManager.getInstance().createInnerMsgInboxObjIdTable(Constants.DbTable.INBOX_OBJECTID);
			}
		}
		return mList;
	}

	public List<Map<String, Object>> loadSendBoxCache(String tableName) {
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
		if (null != db && db.isOpen()) {
			Cursor c = db.rawQuery("SELECT * FROM " + tableName + " ORDER BY createTime DESC", null);
			while (c.moveToNext()) {
				int _id = c.getInt(c.getColumnIndex("_id"));
				String msid = c.getString(c.getColumnIndex("msid"));
				String messageObjectId = c.getString(c.getColumnIndex("messageObjectId"));
				String subject = c.getString(c.getColumnIndex("subject"));
				String content = c.getString(c.getColumnIndex("content"));
				String createdDate = c.getString(c.getColumnIndex("createdDate"));
				String sid = c.getString(c.getColumnIndex("sid"));
				String name = c.getString(c.getColumnIndex("name"));
				String userface = c.getString(c.getColumnIndex("userface"));
				String firstReadDate = c.getString(c.getColumnIndex("firstreaddate"));
				int accountType = c.getInt(c.getColumnIndex("accountType"));
				int isRealNameInt = c.getInt(c.getColumnIndex("isRealName"));
				boolean isRealName = false;
				if (isRealNameInt == 1) {
					isRealName = true;
				} else if (isRealNameInt == 0) {
					isRealName = false;
				}
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				try {
					Date date = dateFormat.parse(firstReadDate);
					if (System.currentTimeMillis() - date.getTime() > EXPIRED_TIME) {
						//删除数据  
						db.delete(tableName, "messageObjectId = ?", new String[] { messageObjectId });
						continue;
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
				String[] mFrom = new String[] { "headImage", "nameTv", "titleTv", "infoTv", "timeTv", "msid", "messageObjectId",
						"read", "accountType", "isRealName" };
				Map<String, Object> map = new HashMap<String, Object>();
				map.put(mFrom[0], R.drawable.avatar);
				map.put("avator_path", userface);
				map.put(mFrom[1], name);
				map.put(mFrom[2], subject);

				map.put(mFrom[3], Html.fromHtml(content));
				map.put(mFrom[4], createdDate);
				map.put(mFrom[5], msid);
				map.put(mFrom[6], messageObjectId);
				map.put(mFrom[8], accountType);
				map.put(mFrom[9], isRealName);
				mList.add(map);
			}
			c.close();
			if (mList.size() == 0) {//读取缓存失败的容错，清楚存储最大、最小objectID表
				db.execSQL("DROP TABLE IF EXISTS " + Constants.DbTable.SENDNBOX_OBJECTID);
				CacheManager.getInstance().createInnerMsgSendboxObjIdTable(Constants.DbTable.SENDNBOX_OBJECTID);
			}
		}
		return mList;
	}

	public void updateMessage(String tableName, String messageObjectId) {
		if (null != db && db.isOpen()) {
			if (isTableExist(tableName)) {
				Cursor c = db.rawQuery("SELECT * FROM " + tableName + " WHERE messageObjectId = ?",
						new String[] { messageObjectId });
				if (c.getCount() > 0) {
					db.execSQL("update " + tableName + " set read = 1 WHERE messageObjectId = ?",
							new String[] { messageObjectId });
				}
				c.close();
			}
		}
	}

	//判断表是否存在
	/**
	 * 判断某张表是否存在
	 * @param tabName 表名
	 * @return
	 */
	public boolean isTableExist(String tableName) {
		boolean result = false;
		if (null != db && db.isOpen()) {
			if (tableName == null) {
				return false;
			}
			Cursor cursor = null;
			try {
				String sql = "select count(*) as c from Sqlite_master  where type ='table' and name ='" + tableName.trim() + "' ";
				cursor = db.rawQuery(sql, null);
				if (cursor.moveToNext()) {
					int count = cursor.getInt(0);
					if (count > 0) {
						result = true;
					}
				}

			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return result;
	}

	public String[] loadObjectId(String tableName) {

		String[] s = new String[2];
		if (null != db && db.isOpen()) {
			Cursor c = db.rawQuery("SELECT * FROM " + tableName, null);
			while (c.moveToNext()) {
				s[0] = c.getString(c.getColumnIndex("maxObjectId"));
				s[1] = c.getString(c.getColumnIndex("minObjectId"));
			}
			c.close();
		}
		return s;
	}

	public static final String DEFAULT_IMAGECACHE_FOLDER = new StringBuilder()
			.append(Environment.getExternalStorageDirectory().getAbsolutePath()).append(File.separator).append("AndroidSystm")
			.append(File.separator).append("AndroidRenhe").append(File.separator).append("ImageCache").toString();

	/** image cache **/
	//	public static final ImageCache IMAGE_CACHE = new ImageCache();
	//	public static final int ANIMATION_DURATION = 2000;
	//	static {
	//		OnImageCallbackListener imageCallBack = new OnImageCallbackListener() {
	//
	//			private static final long serialVersionUID = 1L;
	//
	//			// callback function before get image, run on ui thread
	//			@Override
	//			public void onPreGet(String imageUrl, View view) {
	//				// Log.e(TAG_CACHE, "pre get image");
	//			}
	//
	//			// callback function after get image successfully, run on ui thread
	//			@Override
	//			public void onGetSuccess(String imageUrl, Bitmap loadedImage, View view, boolean isInCache) {
	//				// can be another view child, like textView and so on
	//				//				if (view != null && loadedImage != null) {
	//				//                    ImageView imageView = (ImageView)view;
	//				//                    imageView.setImageBitmap(loadedImage);
	//				//                    // first time show with animation
	//				//                    if (!isInCache) {
	//				//                        imageView.startAnimation(getInAlphaAnimation(2000));
	//				//                    }
	//				//
	//				//                    // auto set height accroding to rate between height and weight
	//				//                    LayoutParams imageParams = (LayoutParams)imageView.getLayoutParams();
	//				//                    imageParams.height = imageParams.width * loadedImage.getHeight() / loadedImage.getWidth();
	//				//                    imageView.setScaleType(ScaleType.FIT_XY);
	//				//                }
	//				if (view != null && loadedImage != null && view instanceof ImageView) {
	//					ImageView imageView = (ImageView) view;
	//					// add tag judge, avoid listView cache and so on
	//					String imageUrlTag = (String) imageView.getTag();
	//					if (ObjectUtils.isEquals(imageUrlTag, imageUrl)) {
	//						imageView.setImageBitmap(loadedImage);
	//						if (!isInCache) {
	//							imageView.startAnimation(getInAlphaAnimation(ANIMATION_DURATION));
	//						}
	//					}
	//					//				        LayoutParams imageParams = (LayoutParams)imageView.getLayoutParams();
	//					//		                 imageParams.height = imageParams.width * loadedImage.getHeight() / loadedImage.getWidth();
	//					imageView.setScaleType(ScaleType.FIT_XY);
	//				} else {
	//				}
	//
	//			}
	//
	//			// callback function after get image failed, run on ui thread
	//			@Override
	//			public void onGetFailed(String imageUrl, Bitmap loadedImage, View view, FailedReason failedReason) {
	//				if (Constants.renhe_log) {
	//					Log.e("ImageCache", new StringBuilder(128).append("get image ").append(imageUrl).append(" error").toString());
	//				}
	//			}
	//
	//			@Override
	//			public void onGetNotInCache(final String imageUrl, final View view) {
	//				Runnable runnable = new Runnable() {
	//
	//					@Override
	//					public void run() {
	//						ImageView imageView = (ImageView) view;
	//						// add tag judge, avoid listView cache and so on
	//						String imageUrlTag = (String) imageView.getTag();
	//						if (ObjectUtils.isEquals(imageUrlTag, imageUrl)) {
	//							FileNameRule fileNameRule = new FileNameRuleImageUrl();
	//							String savePath = DEFAULT_IMAGECACHE_FOLDER + File.separator + fileNameRule.getFileName(imageUrl);
	//							if (savePath.endsWith(".jpg") || savePath.endsWith(".png") || savePath.endsWith(".bmp")) {
	//								savePath = savePath.substring(0, savePath.length() - 4);
	//							}
	//							Bitmap loadedImage = CacheManager.getInstance().getDiskBitmap(savePath);
	//							if (null != loadedImage) {
	//								imageView.setImageBitmap(loadedImage);
	//								//								CacheObject<Bitmap> cacheBit = IMAGE_CACHE.get(imageUrl);
	//								//								if (cacheBit == null) {
	//								//								imageView.clearAnimation();
	//								//									imageView.startAnimation(getInAlphaAnimation(ANIMATION_DURATION));
	//								//								}
	//							} else {
	//							}
	//							//                    }
	//						}
	//						imageView.setScaleType(ScaleType.FIT_XY);
	//					}
	//				};
	//				Handler handler = new Handler();
	//				handler.postDelayed(runnable, 2000);
	//
	//			}
	//		};
	//		IMAGE_CACHE.setOnImageCallbackListener(imageCallBack);
	//		IMAGE_CACHE.setOpenWaitingQueue(false);
	//	}

	public Bitmap getDiskBitmap(String pathString) {
		Bitmap bitmap = null;
		try {
			File file = new File(pathString);
			if (file.exists()) {
				bitmap = BitmapFactory.decodeFile(pathString);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bitmap;
	}

	public static AlphaAnimation getInAlphaAnimation(long durationMillis) {
		AlphaAnimation inAlphaAnimation = new AlphaAnimation(0, 1);
		inAlphaAnimation.setDuration(durationMillis);
		return inAlphaAnimation;
	}

	public static DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.avatar)
			.showImageForEmptyUri(R.drawable.avatar).showImageOnFail(R.drawable.avatar).cacheInMemory(true).cacheOnDisk(true)
			.imageScaleType(ImageScaleType.EXACTLY_STRETCHED).considerExifParams(true)
			//	.displayer(new RoundedBitmapDisplayer(20))
			.build();
	public static AnimateFirstDisplayListener animateFirstDisplayListener = new AnimateFirstDisplayListener();

	public static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				//				imageView.setImageBitmap(loadedImage);
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 1000);
					displayedImages.add(imageUri);
				}
			}
		}

		@Override
		public void onLoadingStarted(String imageUri, View view) {
			super.onLoadingStarted(imageUri, view);
		}

		@Override
		public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
			super.onLoadingFailed(imageUri, view, failReason);
			if (null != view) {
				ImageView imageView = (ImageView) view;
				imageView.setImageResource(R.drawable.avatar);
			}

		}

		@Override
		public void onLoadingCancelled(String imageUri, View view) {
			super.onLoadingCancelled(imageUri, view);
			if (null != view) {
				ImageView imageView = (ImageView) view;
				imageView.setImageResource(R.drawable.avatar);
			}
		}

	}

	// 创建搜索历史数据库
	public SQLiteDatabase openSearchDB() {
		try {
			searchdb = ct.openOrCreateDatabase(SEARCHDBNAME, Context.MODE_PRIVATE, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return searchdb;
	}

	// 建立查询记录的表
	public void createSearchListoryTable() {
		if (null != searchdb && searchdb.isOpen()) {
			searchdb.execSQL("CREATE TABLE if not exists "
					+ SEARCHTABLE
					+ " (keyword VARCHAR, area VARCHAR,areacode int,industry VARCHAR,industrycode int,company VARCHAR,job VARCHAR,createTime LONG)");
		}
	}

	public void saveSearchListoryItem(String keyword, String area, int areacode, String industry, int industrycode,
			String company, String job) {
		boolean isExist = false;
		try {
			if (null != searchdb && searchdb.isOpen()) {
				// SimpleDateFormat formatter = new SimpleDateFormat(
				// "yyyy-MM-dd HH:mm:ss");
				long curDate = System.currentTimeMillis();// 获取当前时间
				Cursor c2 = searchdb.rawQuery("SELECT * FROM " + SEARCHTABLE, null);
				while (c2.moveToNext()) {
					StringBuffer sb = new StringBuffer();
					sb.append(keyword + area + industry + company + job);

					StringBuffer mStringBuffer = new StringBuffer();
					mStringBuffer.append(c2.getString(c2.getColumnIndex("keyword")));
					mStringBuffer.append(c2.getString(c2.getColumnIndex("area")));
					mStringBuffer.append(c2.getString(c2.getColumnIndex("industry")));
					mStringBuffer.append(c2.getString(c2.getColumnIndex("company")));
					mStringBuffer.append(c2.getString(c2.getColumnIndex("job")));

					if (sb.toString().equals(mStringBuffer.toString())) {
						isExist = true;
						break;
					}
				}
				if (!isExist) {
					if (c2.getCount() >= SEARCH_HISTORY_MAX_COUNT) {
						int count = c2.getCount() - SEARCH_HISTORY_MAX_COUNT;
						if (null != searchdb && searchdb.isOpen()) {
							Cursor c3 = searchdb.rawQuery("SELECT * FROM " + SEARCHTABLE + " ORDER BY createTime ASC ", null);
							while (count >= 0 && c3.moveToNext()) {
								searchdb.delete(SEARCHTABLE, "createTime = ?",
										new String[] { c3.getLong(c3.getColumnIndex("createTime")) + "" });
								count--;
							}
							c3.close();
						}
					}
					insertSearchItem(keyword, area, areacode, industry, industrycode, company, job, curDate);
				}
				c2.close();
			}
		} catch (Exception e) {
		}
	}

	public void insertSearchItem(String keyword, String area, int areacode, String industry, int industrycode, String company,
			String job, long createTime) {
		if (null != searchdb && searchdb.isOpen()) {
			searchdb.execSQL("INSERT INTO " + SEARCHTABLE + " VALUES (?,?,?, ?,?, ?,?, ?)", new Object[] { keyword, area,
					areacode, industry, industrycode, company, job, createTime });
		}
	}

	public List<SearchHistoryItem> querySearchHistory() {
		List<SearchHistoryItem> list = new ArrayList<SearchHistoryItem>();
		if (null != searchdb && searchdb.isOpen()) {
			Cursor c2 = searchdb.rawQuery("SELECT * FROM " + SEARCHTABLE + " ORDER BY createTime DESC ", null);
			while (c2.moveToNext()) {
				long curDate = c2.getLong(c2.getColumnIndex("createTime"));// 获取当前时间
				//				if (System.currentTimeMillis() - curDate < SEARCH_HISTORY_EXPIRED_TIME) {
				String mkeyword = c2.getString(c2.getColumnIndex("keyword"));
				String marea = c2.getString(c2.getColumnIndex("area"));
				int mareaCode = c2.getInt(c2.getColumnIndex("areacode"));
				String mindustry = c2.getString(c2.getColumnIndex("industry"));
				int mindustryCode = c2.getInt(c2.getColumnIndex("industrycode"));
				String mcompany = c2.getString(c2.getColumnIndex("company"));
				String mjob = c2.getString(c2.getColumnIndex("job"));

				SearchHistoryItem searchHistoryItem = new SearchHistoryItem(mkeyword, marea, mindustry, mareaCode, mindustryCode,
						mcompany, mjob, curDate);
				list.add(searchHistoryItem);
				//				} 
				//				else {
				//					searchdb.delete(SEARCHTABLE, "createTime = ?", new String[] { curDate + "" });
				//				}
			}
			c2.close();
		}
		return list;
	}

	public static File getExternalCacheDir(Context context) {
		File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
		File appCacheDir = new File(new File(dataDir, context.getPackageName()), "cache");
		if (!appCacheDir.exists()) {
			if (!appCacheDir.mkdirs()) {
				L.w("Unable to create external cache directory");
				return null;
			}
			try {
				new File(appCacheDir, ".nomedia").createNewFile();
			} catch (IOException e) {
				L.i("Can't create \".nomedia\" file in application external cache directory");
			}
		}
		return appCacheDir;
	}

	public void clearSearchHistory() {
		if (null != searchdb && searchdb.isOpen()) {
			searchdb.execSQL("DROP TABLE IF EXISTS " + SEARCHTABLE);
		}
	}
}
