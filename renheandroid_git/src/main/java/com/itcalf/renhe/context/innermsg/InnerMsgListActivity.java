package com.itcalf.renhe.context.innermsg;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.itcalf.renhe.Constants;
import com.itcalf.renhe.R;
import com.itcalf.renhe.RenheApplication;
import com.itcalf.renhe.cache.CacheManager;
import com.itcalf.renhe.context.fragmentMain.MeunFragment;
import com.itcalf.renhe.context.fragmentMain.MyDialogFragment;
import com.itcalf.renhe.dto.InBoxInfo;
import com.itcalf.renhe.dto.SendBoxInfo;
import com.itcalf.renhe.utils.HttpUtil;
import com.itcalf.renhe.utils.ToastUtil;
import com.itcalf.renhe.view.XListView;
import com.itcalf.renhe.view.XListView.IXListViewListener;
import com.nostra13.universalimageloader.core.ImageLoader;

public class InnerMsgListActivity extends Fragment implements IXListViewListener {
	//登出标识（两次按下返回退出时的标识字段）
	private boolean logoutFlag;
	private XListView mListView;
	private ImageButton mSendBt;
	private RelativeLayout mBackBt;
	private String mFrom[];
	private int mTo[];
	private SimpleAdapter mSimpleAdapter;
	private List<Map<String, Object>> mData;
	//	private View mFooterView;
	private static final boolean TEST = false;
	private int mStart = 0;
	private int mCount = 20;
	private String minBoxMinObjectId;
	private  String minBoxMaxObjectId;
	private String msendBoxMinObjectId;
	private String msendBoxMaxObjectId;
	private int mType;
	private NotifyNewMessageReceiver newMessageReceiver;
	public static final String NEW_MESSAGE = "renhe.innermessage.notify.newmessage";
	public static final String REFRESH_CACHE = "renhe.innermessage.refresh.cache";
	private boolean isFromNotify = true;
	private Handler mHandler;
	private SharedPreferences msp;
	private Editor editor;
	private RefreshCacheReceiver refreshCacheReceiver;

	private SQLiteDatabase db;
	private String dbName;
	private String tableName;
	private String objIdTableName;
	private Context context;
	private final static int INBOXMSG_REQUEST_DIALOG = 1;
	private final static int SENDBOXMSG_REQUEST_DIALOG = 2;
	private View rootView;
	private Handler mLoadCacheHandler;
	private Runnable loadCacheRun;
	private DialogFragment dialogFragment;
	private String tag = "my_dialog"; 
	private static final int REQUEST_DELAY_TIME = 500;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 Bundle arguments = this.getArguments();
	        if (arguments != null) {
	        	mType = arguments.getInt("value");
	        }
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		if(null == rootView)
		{
			rootView = inflater.inflate(R.layout.innermsg_msglist, null);
		}
		ViewGroup parent = (ViewGroup) rootView.getParent();  
        if (parent != null) {  
            parent.removeView(rootView);  
        }   
		context = (Context)getActivity();
		findView(rootView);
		initData();
		initListener();
		mHandler = new Handler();
		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	protected void findView(View view) {
		mListView = (XListView) view.findViewById(R.id.listView);
		mSendBt = (ImageButton) view.findViewById(R.id.editBt);
		mBackBt = (RelativeLayout) view.findViewById(R.id.backBtRl);
	}

	protected void initData() {
		msp = context.getSharedPreferences("setting_info", 0);
		editor = msp.edit();
//		mType = getActivity().getIntent().getIntExtra("type", 1);
		if (mType == 1) {
			newMessageReceiver = new NotifyNewMessageReceiver();
			IntentFilter intentFilter = new IntentFilter(NEW_MESSAGE);
			context.registerReceiver(newMessageReceiver, intentFilter);
		}
		if (context.getSharedPreferences("setting_info", 0).getBoolean("fastdrag", false)) {
			mListView.setFastScrollEnabled(true);
		} else {
			mListView.setFastScrollEnabled(false);
		}
		mTo = new int[] { R.id.headImage, R.id.nameTv, R.id.titleTv, R.id.infoTv, R.id.timeTv, R.id.msid, R.id.messageObjectId };
		mFrom = new String[] { "headImage", "nameTv", "titleTv", "infoTv", "timeTv", "msid", "messageObjectId", "read","accountType","isRealName" };
		mData = new ArrayList<Map<String, Object>>();
		if (TEST) {

		} else {
			mSimpleAdapter = new ImageUpdateAdapter(context, mData, R.layout.innermsg_msg_list_item, mFrom, mTo);
			mListView.setAdapter(mSimpleAdapter);
			if (context.getSharedPreferences("setting_info", 0).getBoolean("fastdrag", false)) {
				mListView.setFastScrollEnabled(true);
			}
			//加载缓存数据
//			getActivity().showDialog(INBOXMSG_REQUEST_DIALOG);
			showDialog();
			mLoadCacheHandler = new Handler(new Callback() {

				@Override
				public boolean handleMessage(Message arg0) {
					switch (arg0.what) {
					case 1:
						mSimpleAdapter.notifyDataSetChanged();
						break;
					case 2:
						break;
				 }
					return false;
				}
			});
			loadCacheRun = new Runnable() {

				@Override
				public void run() {
					initDb();
				}
			};
			mLoadCacheHandler.postDelayed(loadCacheRun,REQUEST_DELAY_TIME);//延迟500ms，防止slidemenu滑动卡顿
			
			
		}
		
	}
	private void initDb(){
		db = CacheManager.getInstance().openDB();
        if(null != db){
        	if(mType == 1){
        		tableName = Constants.DbTable.INBOX;
        		objIdTableName = Constants.DbTable.INBOX_OBJECTID;
        		CacheManager.getInstance().createInnerMsgInboxObjIdTable(objIdTableName);
        		CacheManager.getInstance().createInnerMsgInboxTable(tableName);
        		if(CacheManager.getInstance().inOrSendBoxCacheNum(tableName) > 0){
        			Cursor c = db.rawQuery("SELECT * FROM "+objIdTableName, null); 
        			if (c.getCount() > 0) {
        				c.moveToLast();
        				this.minBoxMaxObjectId = c.getString(c.getColumnIndex("maxObjectId"));  
        				this.minBoxMinObjectId = c.getString(c.getColumnIndex("minObjectId"));  
        				if(null != this.minBoxMaxObjectId && !"".equals(this.minBoxMaxObjectId)){
        					loadedCache(mType);
        				}else{
        					db.execSQL("DROP TABLE IF EXISTS "+tableName);
        					CacheManager.getInstance().createInnerMsgInboxTable(tableName);
        					loadInboxInfo("renew", "", "");
        				}
        			}else{
        				db.execSQL("DROP TABLE IF EXISTS "+tableName);
        				CacheManager.getInstance().createInnerMsgInboxTable(tableName);
        				db.execSQL("INSERT INTO "+objIdTableName+" VALUES (?, ?) ",new String[]{"",""});
        				loadInboxInfo("renew", "", "");
        			}
        		}else{
        			db.execSQL("DROP TABLE IF EXISTS "+tableName);
        			db.execSQL("DROP TABLE IF EXISTS "+objIdTableName);
    				CacheManager.getInstance().createInnerMsgInboxTable(tableName);
    				CacheManager.getInstance().createInnerMsgInboxObjIdTable(objIdTableName);
    				db.execSQL("INSERT INTO "+objIdTableName+" VALUES (?, ?) ",new String[]{"",""});
    				loadInboxInfo("renew", "", "");
        		}
        		
        	}else if(mType == 2){
        		tableName = Constants.DbTable.SENDBOX;
        		objIdTableName = Constants.DbTable.SENDNBOX_OBJECTID;
        		CacheManager.getInstance().createInnerMsgSendboxObjIdTable(objIdTableName);
        		CacheManager.getInstance().createInnerMsgSendboxTable(tableName);
        		if(CacheManager.getInstance().inOrSendBoxCacheNum(tableName) > 0){
	        		Cursor c = db.rawQuery("SELECT * FROM "+objIdTableName, null);  
	        		if (c.getCount() > 0) {
	        			c.moveToLast();
	        			this.msendBoxMinObjectId = c.getString(c.getColumnIndex("minObjectId"));
	        			this.msendBoxMaxObjectId = c.getString(c.getColumnIndex("maxObjectId"));
	        			if(null != this.msendBoxMaxObjectId && !"".equals(this.msendBoxMaxObjectId)){
	        				loadedCache(mType);
	        			}else{
	        				db.execSQL("DROP TABLE IF EXISTS "+tableName);
	        				CacheManager.getInstance().createInnerMsgSendboxTable(tableName);
	        				loadSendbodInfo("renew", "", "");
	        			}
	        			
	        		}else{
	        			db.execSQL("DROP TABLE IF EXISTS "+tableName);
	        			CacheManager.getInstance().createInnerMsgSendboxTable(tableName);
	        			db.execSQL("INSERT INTO "+objIdTableName+" VALUES (?, ?) ",new String[]{"",""});
	        			loadSendbodInfo("renew", "", "");
	        		}
        		}else{
        			db.execSQL("DROP TABLE IF EXISTS "+tableName);
        			db.execSQL("DROP TABLE IF EXISTS "+objIdTableName);
        			CacheManager.getInstance().createInnerMsgSendboxTable(tableName);
        			CacheManager.getInstance().createInnerMsgSendboxObjIdTable(objIdTableName);
        			db.execSQL("INSERT INTO "+objIdTableName+" VALUES (?, ?) ",new String[]{"",""});
        			loadSendbodInfo("renew", "", "");
        		}
        	}
        }else{
        	if(mType == 1){
        		loadInboxInfo("renew", "", "");
        	}else if(mType == 2){
        		loadSendbodInfo("renew", "", "");
        	}
        
        }
	}
	/**
	 * 加载缓存数据
	 * 
	 * @param type
	 */
	private void loadedCache(int type) {
		if (type == 1) {
			List<Map<String, Object>> tempList = CacheManager.getInstance().loadInboxCache(tableName);
			if (tempList.size() > 0) {
				mData.addAll(tempList);
				mListView.showFootView();
				mLoadCacheHandler.sendEmptyMessage(1);
				loadInboxInfo("new", "", this.minBoxMaxObjectId);
			}else{
				db.execSQL("DROP TABLE IF EXISTS "+tableName);
				CacheManager.getInstance().createInnerMsgInboxTable(tableName);
				db.execSQL("INSERT INTO "+objIdTableName+" VALUES (?, ?) ",new String[]{"",""});
				loadInboxInfo("renew", "", "");
			}
			
		} else {
			List<Map<String, Object>> tempList = CacheManager.getInstance().loadSendBoxCache(tableName);
			if (tempList.size() > 0) {
				mData.addAll(tempList);
				mListView.showFootView();
				mLoadCacheHandler.sendEmptyMessage(1);
				loadSendbodInfo("new", "", this.msendBoxMaxObjectId);
			}else{
				db.execSQL("DROP TABLE IF EXISTS "+tableName);
				CacheManager.getInstance().createInnerMsgSendboxTable(tableName);
				db.execSQL("INSERT INTO "+objIdTableName+" VALUES (?, ?) ",new String[]{"",""});
				loadSendbodInfo("renew", "", "");
			}
		}
		mListView.showFootView();
	}

	private void loadInboxInfo(String type, String s1, String s2) {
		new MsgInBoxLoadTask() {

			@Override
			void doPre() {
//				getActivity().showDialog(INBOXMSG_REQUEST_DIALOG);
				showDialog();
			}

			@Override
			void doPost() {
//				getActivity().removeDialog(INBOXMSG_REQUEST_DIALOG);
				removeDialog();
				mLoadCacheHandler.sendEmptyMessage(1);
			}
		}.execute(type, s1, s2);
	}

	private void loadSendbodInfo(String type, String s1, String s2) {
		new MsgSendBoxLoadTask() {

			@Override
			void doPre() {
//				getActivity().showDialog(SENDBOXMSG_REQUEST_DIALOG);
				showDialog();
				//				mFooterView.setVisibility(View.INVISIBLE);
			}

			@Override
			void doPost() {
//				getActivity().removeDialog(SENDBOXMSG_REQUEST_DIALOG);
				removeDialog();
				mLoadCacheHandler.sendEmptyMessage(1);
			}
		}.execute(type, s1, s2);
	}

	protected void initListener() {
		mListView.setPullLoadEnable(true);
		mListView.setXListViewListener(this);
		refreshCacheReceiver = new RefreshCacheReceiver();
		IntentFilter intentFilter = new IntentFilter(REFRESH_CACHE);
		context.registerReceiver(refreshCacheReceiver, intentFilter);

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				position -= 1;
				if (mType == 1) {
					if (mData.size() > position) {
						Intent intent = new Intent();
						intent.putExtra("msid", mData.get(position).get(mFrom[5]).toString());
						intent.putExtra("messageObjectId", mData.get(position).get(mFrom[6]).toString());
						intent.putExtra("type", 1);
						intent.setClass(context, LookatMsgActivity.class);
						startActivityForResult(intent, 0);
						getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
						//更新已读状态
						Map<String, Object> map = mData.get(position);
						Object readObject = map.get("read");
						if (readObject != null) {
							String read = readObject.toString();
							if (read.equals("0")) {//0代表未读
								map.put("read", "1");
								mData.set(position, map);
								int currentNum = msp.getInt("newmsg_unreadmsg_num", 1);
								if (currentNum < 1) {
									currentNum = 1;
								}
								//更改左侧菜单未读站内信图标
								editor.putInt("newmsg_unreadmsg_num", currentNum - 1);
								Intent intent2 = new Intent(MeunFragment.NEWMSG_ICON_ACTION);
								intent2.putExtra("newmsg_notice_num", currentNum - 1);
								context.sendBroadcast(intent2);
								editor.commit();
							}
						}
						//更新缓存
						refreshCache(map.get(mFrom[6]).toString());
					}

				} else {
					if (mData.size() > position) {
						Intent intent = new Intent();
						intent.putExtra("msid", mData.get(position).get(mFrom[5]).toString());
						intent.putExtra("messageObjectId", mData.get(position).get(mFrom[6]).toString());
						intent.putExtra("type", 2);
						intent.setClass(context, LookatMsgActivity.class);
						startActivityForResult(intent, 0);
						getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
					}
				}

			}
		});
	}

	private void refreshCache(String messageObjectId) {
		CacheManager.getInstance().updateMessage(tableName, messageObjectId);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		boolean isChange = false;
		loop: for (String str : RenheApplication.INNER_DLETE_MSG) {
			for (int i = mData.size() - 1; i >= 0; i--) {
				if ((mData.get(i).get(mFrom[5]) + "#" + mData.get(i).get(mFrom[6])).equals(str)) {
					mData.remove(i);
//					CacheManager.getInstance().populateData(this)
//							.deleteObject(getRenheApplication().getUserInfo().getEmail(), "innerMsg" + mType);
					db.execSQL("DROP TABLE IF EXISTS " + tableName);
					isChange = true;
					continue loop;
				}
			}

		}
		if (isChange) {
			mSimpleAdapter.notifyDataSetChanged();
		}
	}

	abstract class MsgInBoxLoadTask extends AsyncTask<String, Void, InBoxInfo> {
		
		private boolean isNew = false;
		private boolean isMore = false;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			doPre();
		}

		abstract void doPre();

		@Override
		protected InBoxInfo doInBackground(String... params) {
			Map<String, Object> reqParams = new HashMap<String, Object>();
			reqParams.put("sid", RenheApplication.getInstance().getUserInfo().getSid());
			if (params[0].equals("more")) {
				isMore = true;
			}
			if (params[0].equals("new")) {
				isNew = true;
			} else {
				isNew = false;
			}
			reqParams.put("type", params[0]);
			if (null != params[1]) {
				reqParams.put("minObjectId", params[1]);
			}
			reqParams.put("maxObjectId", params[2]);
			reqParams.put("count", 20);
			reqParams.put("adSId", RenheApplication.getInstance().getUserInfo().getAdSId());
			try {
				InBoxInfo mb = (InBoxInfo) HttpUtil.doHttpRequest(Constants.Http.INNERMSG_INBOX, reqParams, InBoxInfo.class,
						context);
				return mb;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(InBoxInfo result) {
			super.onPostExecute(result);
			if (null != result) {
				if (1 == result.getState()) {
					if (result.getMaxObjectId() != null) {
						minBoxMaxObjectId = result.getMaxObjectId();
					}
					if (result.getMinObjectId() != null) {
						minBoxMinObjectId = result.getMinObjectId();
					}
					if(null != db){
						if(db.isOpen()){
							db.execSQL(
									"UPDATE "+objIdTableName+" SET minObjectId = ? ,maxObjectId = ?",
									new String[] {minBoxMinObjectId,minBoxMaxObjectId});
						}
					}
					if (null != result.getMessageList() && result.getMessageList().length > 0) {
						mListView.showFootView();
						if (isNew && result.isAboveMaxCount()) {
							mData.clear();
//							CacheManager.getInstance().populateData(InnerMsgListActivity.this)
//									.deleteObject(getRenheApplication().getUserInfo().getEmail(), "innerMsg" + mType + "");
							if(null != db){
								if(db.isOpen()){
									db.execSQL("DROP TABLE IF EXISTS " + tableName);
									CacheManager.getInstance().createInnerMsgInboxTable(tableName);
								}
							}
						}
						if (!isNew) {
							boolean flag = false;
							for(int i = 0; i < result.getMessageList().length; i++){
								InBoxInfo.MessageList messageList = result.getMessageList()[i];
								for(int j = 0; j < i; j++){
									if(result.getMessageList()[j].getMessageObjectId().equals(result.getMessageList()[i].getMessageObjectId())){
										flag = true;
										break;
									}
								}
								if(!flag){
									final Map<String, Object> map = new LinkedHashMap<String, Object>();
									map.put(mFrom[0], R.drawable.avatar);
									map.put("avator_path", messageList.getSenderInfo().getUserface());
									map.put(mFrom[1], messageList.getSenderInfo().getName());
									map.put(mFrom[2], messageList.getSubject());
									map.put(mFrom[3], Html.fromHtml(messageList.getContent()));
									map.put(mFrom[4], messageList.getCreatedDate());
									map.put(mFrom[5], messageList.getMsid());
									map.put(mFrom[6], messageList.getMessageObjectId());
									map.put(mFrom[7], messageList.getRead());
									map.put(mFrom[8], messageList.getSenderInfo().getAccountType());
									map.put(mFrom[9], messageList.getSenderInfo().isRealname());
									mData.add(map);
								}
							}
//							for (InBoxInfo.MessageList messageList : result.getMessageList()) {
//
//								final Map<String, Object> map = new LinkedHashMap<String, Object>();
//								map.put(mFrom[0], R.drawable.avatar);
//								map.put("avator_path", messageList.getSenderInfo().getUserface());
//								map.put(mFrom[1], messageList.getSenderInfo().getName());
//								map.put(mFrom[2], messageList.getSubject());
//
//								map.put(mFrom[3], Html.fromHtml(messageList.getContent()));
//								map.put(mFrom[4], messageList.getCreatedDate());
//								map.put(mFrom[5], messageList.getMsid());
//								map.put(mFrom[6], messageList.getMessageObjectId());
//								map.put(mFrom[7], messageList.getRead());
//								map.put(mFrom[8], messageList.getSenderInfo().getAccountType());
//								map.put(mFrom[9], messageList.getSenderInfo().isRealname());
//								mData.add(map);
//							}
						} else {
							boolean flag = false;
							for (int i = result.getMessageList().length - 1; i >= 0; i--) {
								InBoxInfo.MessageList messageList = result.getMessageList()[i];
								for(int j = result.getMessageList().length - 1; j > i; j--){
									if(result.getMessageList()[j].getMessageObjectId().equals(result.getMessageList()[i].getMessageObjectId())){
										flag = true;
										break;
									}
								}
								if(!flag){
									final Map<String, Object> map = new LinkedHashMap<String, Object>();
									map.put(mFrom[0], R.drawable.avatar);
									map.put("avator_path", messageList.getSenderInfo().getUserface());
									map.put(mFrom[1], messageList.getSenderInfo().getName());
									map.put(mFrom[2], messageList.getSubject());
									
									map.put(mFrom[3], Html.fromHtml(messageList.getContent()));
									map.put(mFrom[4], messageList.getCreatedDate());
									map.put(mFrom[5], messageList.getMsid());
									map.put(mFrom[6], messageList.getMessageObjectId());
									map.put(mFrom[7], messageList.getRead());
									map.put(mFrom[8], messageList.getSenderInfo().getAccountType());
									map.put(mFrom[9], messageList.getSenderInfo().isRealname());
									mData.add(0, map);
								}
							}
						}
						//判断缓存是否已过期，是 则将新刷新的对象写入文件，否 则将文件原有对象和新刷新对象一并写入文件
//								if (isMore) {
//									CacheManager.getInstance().saveInboxMoreCache(tableName, result);
//								} else {
//									CacheManager.getInstance().saveInboxNewCache(tableName, result);
//								}
						if(null != db){
							CacheManager.getInstance().saveInboxMoreCache(tableName, result);
						}
					} else {
						if (isMore) {
							//							mFooterView.setVisibility(View.GONE);
							Toast.makeText(context, "已经到底了", Toast.LENGTH_SHORT).show();
						}
					}
				}
			} else {
				//				ToastUtil.showNetworkError(InnerMsgListActivity.this);
				mStart = (mStart -= mCount) == 0 ? 0 : mStart;
				if (isMore) {
					//					mFooterView.setVisibility(View.GONE);
					Toast.makeText(context, "已经到底了", Toast.LENGTH_SHORT).show();
				}
			}
			//遍历缓存，检查是否有未读邮件，防止在别的设备以及读了某邮件，在该设备上仍受到push，导致始终有未读通知
//			if(null != db){
//				List<Map<String, Object>> cacheList = CacheManager.getInstance().loadInboxCache(tableName);
//				int count = 0;
//				for(Map<String, Object> map : cacheList){
//					if(null != map.get("read")){
//						int read = (Integer)map.get("read");
//						if(read == 0){
//							count += 1;
//						}
//					}
//				}
//				Intent intent2 = new Intent(MySlidingMenu.NEWMSG_ICON_ACTION);
//				intent2.putExtra("newmsg_notice_num", count);
//				sendBroadcast(intent2);
//			}
			doPost();
		}

		abstract void doPost();
	}

	abstract class MsgSendBoxLoadTask extends AsyncTask<String, Void, SendBoxInfo> {
		private boolean isNew = false;
		private boolean isSendMore = false;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			doPre();
		}

		abstract void doPre();

		@Override
		protected SendBoxInfo doInBackground(String... params) {
			Map<String, Object> reqParams = new HashMap<String, Object>();
			reqParams.put("sid", RenheApplication.getInstance().getUserInfo().getSid());
			if (params[0].equals("more")) {
				isSendMore = true;
			}
			if (params[0].equals("new")) {
				isNew = true;
			} else {
				isNew = false;
			}
			reqParams.put("type", params[0]);
			if (null != params[1]) {
				reqParams.put("minObjectId", params[1]);
			}
			reqParams.put("maxObjectId", params[2]);
			reqParams.put("count", 20);
			reqParams.put("adSId", RenheApplication.getInstance().getUserInfo().getAdSId());
			try {
				SendBoxInfo mb = (SendBoxInfo) HttpUtil.doHttpRequest(Constants.Http.INNERMSG_SENDBOX, reqParams,
						SendBoxInfo.class, context);
				return mb;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(SendBoxInfo result) {
			super.onPostExecute(result);
			if (null != result) {
				if (1 == result.getState()) {
					if (result.getMaxObjectId() != null) {
						msendBoxMaxObjectId = result.getMaxObjectId();
					}
					if (result.getMinObjectId() != null) {
						msendBoxMinObjectId = result.getMinObjectId();
					}
					if(null != db){
						if(db.isOpen()){
							db.execSQL(
									"INSERT INTO "+objIdTableName+" VALUES (?, ?)",
									new Object[] {msendBoxMinObjectId,msendBoxMaxObjectId});
						}
					}
					if (null != result.getMessageList() && result.getMessageList().length > 0) {
						mListView.showFootView();
						if (isNew && result.isAboveMaxCount()) {
							mData.clear();
//							CacheManager.getInstance().populateData(InnerMsgListActivity.this)
//									.deleteObject(getRenheApplication().getUserInfo().getEmail(), "innerMsg" + mType + "");
							if(null != db){
								if(db.isOpen()){
									db.execSQL("DROP TABLE IF EXISTS " + tableName);
									CacheManager.getInstance().createInnerMsgSendboxTable(tableName);
								}
							}
						}
						if (!isNew) {
							boolean flag = false;
							for(int i = 0; i < result.getMessageList().length; i++){
								SendBoxInfo.MessageList messageList = result.getMessageList()[i];
								for(int j = 0; j < i; j++){
									if(result.getMessageList()[j].getMessageObjectId().equals(result.getMessageList()[i].getMessageObjectId())){
										flag = true;
										break;
									}
								}
								if(!flag){
									final Map<String, Object> map = new LinkedHashMap<String, Object>();
									map.put(mFrom[0], R.drawable.avatar);
									map.put("avator_path", messageList.getReceiverInfo().getUserface());
									map.put(mFrom[1], "发送至: " + messageList.getReceiverInfo().getName());
									map.put(mFrom[2], messageList.getSubject());
									
									map.put(mFrom[3], Html.fromHtml(messageList.getContent()));
									map.put(mFrom[4], messageList.getCreatedDate());
									map.put(mFrom[5], messageList.getMsid());
									map.put(mFrom[6], messageList.getMessageObjectId());
									map.put(mFrom[8], messageList.getReceiverInfo().getAccountType());
									map.put(mFrom[9], messageList.getReceiverInfo().isRealname());
									mData.add(map);
								}
							}
//							for (SendBoxInfo.MessageList messageList : result.getMessageList()) {
//								final Map<String, Object> map = new LinkedHashMap<String, Object>();
//								map.put(mFrom[0], R.drawable.avatar);
//								map.put("avator_path", messageList.getReceiverInfo().getUserface());
//								map.put(mFrom[1], "发送至: " + messageList.getReceiverInfo().getName());
//								map.put(mFrom[2], messageList.getSubject());
//
//								map.put(mFrom[3], Html.fromHtml(messageList.getContent()));
//								map.put(mFrom[4], messageList.getCreatedDate());
//								map.put(mFrom[5], messageList.getMsid());
//								map.put(mFrom[6], messageList.getMessageObjectId());
//								map.put(mFrom[8], messageList.getReceiverInfo().getAccountType());
//								map.put(mFrom[9], messageList.getReceiverInfo().isRealname());
//								mData.add(map);
//							}
						} else {
							boolean flag = false;
							for (int i = result.getMessageList().length - 1; i >= 0; i--) {
								SendBoxInfo.MessageList messageList = result.getMessageList()[i];
								for(int j = result.getMessageList().length - 1; j > i; j--){
									if(result.getMessageList()[j].getMessageObjectId().equals(result.getMessageList()[i].getMessageObjectId())){
										flag = true;
										break;
									}
								}
								if(!flag){
									final Map<String, Object> map = new LinkedHashMap<String, Object>();
									map.put(mFrom[0], R.drawable.avatar);
									map.put("avator_path", messageList.getReceiverInfo().getUserface());
									map.put(mFrom[1], "发送至: " + messageList.getReceiverInfo().getName());
									map.put(mFrom[2], messageList.getSubject());
									
									map.put(mFrom[3], Html.fromHtml(messageList.getContent()));
									map.put(mFrom[4], messageList.getCreatedDate());
									map.put(mFrom[5], messageList.getMsid());
									map.put(mFrom[6], messageList.getMessageObjectId());
									map.put(mFrom[8], messageList.getReceiverInfo().getAccountType());
									map.put(mFrom[9], messageList.getReceiverInfo().isRealname());
									mData.add(0, map);
								}
							}
						}
						
						//判断缓存是否已过期，是 则将新刷新的对象写入文件，否 则将文件原有对象和新刷新对象一并写入文件
//								if (isSendMore) {
//									CacheManager.getInstance().saveSendboxMoreCache(tableName, result);
//								} else {
//									CacheManager.getInstance().saveSendboxNewCache(tableName, result);
//								}
						if(null != db){
							CacheManager.getInstance().saveSendboxMoreCache(tableName, result);
						}
//						
					} else {
						if (isSendMore) {
							//							mFooterView.setVisibility(View.GONE);
						}
					}
				}
			} else {
				ToastUtil.showNetworkError(context);
				mStart = (mStart -= mCount) == 0 ? 0 : mStart;
				if (isSendMore) {
					//					mFooterView.setVisibility(View.GONE);
				}
			}
			doPost();
		}

		abstract void doPost();
	}
//	private void filterCacheByResult(InBoxInfo result,){
//		for(int i = 0; i < result.getMessageList().length; i ++){
//			MessageList msList = result.getMessageList()[i];
//			Cursor c2 = db.rawQuery("SELECT * FROM "+tableName+" WHERE messageObjectId = ?",
//					new String[] {msList.getMessageObjectId()});
//			if (!c2.moveToNext()) {
//				final Map<String, Object> map = new LinkedHashMap<String, Object>();
//				map.put(mFrom[0], R.drawable.avatar);
//				map.put("avator_path", msList.getSenderInfo().getUserface());
//				map.put(mFrom[1], msList.getSenderInfo().getName());
//				map.put(mFrom[2], msList.getSubject());
//
//				map.put(mFrom[3], Html.fromHtml(msList.getContent()));
//				map.put(mFrom[4], msList.getCreatedDate());
//				map.put(mFrom[5], msList.getMsid());
//				map.put(mFrom[6], msList.getMessageObjectId());
//				map.put(mFrom[7], msList.getRead());
//				mData.add(map);
//			}
//		}
//	}
	class ImageUpdateAdapter extends SimpleAdapter {

		public ImageUpdateAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
			super(context, data, resource, from, to);
		}

		@SuppressLint("SimpleDateFormat")
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			convertView = super.getView(position, convertView, parent);
			if (convertView != null && position < mData.size()) {
				String picPath = (String) mData.get(position).get("avator_path");
				final ImageView imageView = (ImageView) convertView.findViewById(mTo[0]);
				Map<String, Object> map = mData.get(position);
				Object readObject = map.get("read");
				TextView titleTv = (TextView) convertView.findViewById(mTo[2]);
				TextView timeTv = (TextView) convertView.findViewById(mTo[4]);
				ImageView vipIv = (ImageView) convertView.findViewById(R.id.vipImage);
				ImageView realNameIv = (ImageView) convertView.findViewById(R.id.realnameImage);
				Object accountObject  = map.get("accountType");
				Object realNameObject = map.get("isRealName");
				int accountType = 0;
				boolean isRealName = false;
				if(null != accountObject){
					accountType = (Integer)map.get("accountType");;//账号vip等级类型：0：普通会员；1：VIP会员；2：黄金会员；3：铂金会员
				}
				if(null != realNameObject){
					isRealName= (Boolean)map.get("isRealName");//是否是实名认证的会员
				}
				switch (accountType) {
				case 0:
					vipIv.setVisibility(View.GONE);
					break;
				case 1:
					vipIv.setVisibility(View.VISIBLE);
					vipIv.setImageResource(R.drawable.vip_1);
					break;
				case 2:
					vipIv.setVisibility(View.VISIBLE);
					vipIv.setImageResource(R.drawable.vip_2);
					break;
				case 3:
					vipIv.setVisibility(View.VISIBLE);
					vipIv.setImageResource(R.drawable.vip_3);
					break;

				default:
					break;
				}
				if(isRealName && accountType <= 0){
					realNameIv.setVisibility(View.VISIBLE);
					realNameIv.setImageResource(R.drawable.realname);
				}else{
					realNameIv.setVisibility(View.GONE);
				}
				if (readObject != null) {
					String read = readObject.toString();
					if (read.equals("0")) {//0代表未读
						titleTv.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.readed), null,
								null, null);
					} else {
						titleTv.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
					}
				} else {
					titleTv.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
				}

				timeTv = (TextView) convertView.findViewById(R.id.timeTv);
				String time = map.get("timeTv").toString();
				DateFormat format1 = new SimpleDateFormat("MM-dd HH:mm");
				try {
					Date date = format1.parse(time);
					SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.readable_date_md_hm));
					time = dateFormat.format(date);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				timeTv.setText(time);

//				imageView.setTag(picPath + position);
				if (picPath != null) {
//					SimpleAsyncImageLoad.loadDrawable(null, picPath, getRenheApplication().getUserInfo().getEmail(), 70, 70,
//							InnerMsgListActivity.this, new SimpleAsyncImageLoad.ImageCallback() {
//
//								@Override
//								public void imageLoaded(Drawable imageDrawable, String imageUrl) {
//									ImageView imageViewByTag = (ImageView) mListView.findViewWithTag(imageUrl + position);
//									if (imageViewByTag != null) {
//										imageViewByTag.setImageDrawable(imageDrawable);
//									}
//								}
//
//							});
					//方法2
//					imageView.setTag(picPath);
//					CacheManager.IMAGE_CACHE.initData(InnerMsgListActivity.this, "renhe_imagecache");
//					CacheManager.IMAGE_CACHE.setContext(InnerMsgListActivity.this);
//					CacheManager.IMAGE_CACHE.setCacheFolder(CacheManager.DEFAULT_IMAGECACHE_FOLDER);
//					if (!CacheManager.IMAGE_CACHE.get(picPath, imageView)) {
//						((ImageView)imageView).setImageDrawable(InnerMsgListActivity.this.getResources().getDrawable(R.drawable.avatar));
//					}
					//方法3
					ImageLoader imageLoader = ImageLoader.getInstance();		
					try {
						imageLoader.displayImage(picPath, (ImageView)imageView, CacheManager.options,  CacheManager.animateFirstDisplayListener);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if(position == 0){
						this.notifyDataSetChanged();
					}
				}

			}
			return convertView;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(null != db){
			//关闭当前数据库  
	        db.close();  
		}
		if (null != newMessageReceiver) {
			context.unregisterReceiver(newMessageReceiver);
			newMessageReceiver = null;
		}
		if (null != refreshCacheReceiver) {
			context.unregisterReceiver(refreshCacheReceiver);
			refreshCacheReceiver = null;
		}
	}

	class NotifyNewMessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			isFromNotify = arg1.getBooleanExtra("isFromNotify", true);
			loadInboxInfo("new", "", minBoxMaxObjectId);
		}

	}

	Handler handler = new Handler();
	Runnable run = new Runnable() {
		@Override
		public void run() {
			logoutFlag = false;
		}
	};

	private void onLoad() {
		mListView.stopRefresh();
		mListView.stopLoadMore();
		mListView.setRefreshTime("刚刚");
	}

	@Override
	public void onRefresh() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {

				if (mType == 1) {
					if (minBoxMaxObjectId != null) {
						new MsgInBoxLoadTask() {

							@Override
							void doPre() {
							}

							@Override
							void doPost() {
								onLoad();
							}
						}.execute("new", null, minBoxMaxObjectId);
					}
				} else {
					if (msendBoxMaxObjectId != null) {
						new MsgSendBoxLoadTask() {

							@Override
							void doPre() {
							}

							@Override
							void doPost() {
								onLoad();
							}
						}.execute("new", null, msendBoxMaxObjectId);
					}
				}

			}
		}, 2000);
	}

	@Override
	public void onLoadMore() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {

				if (mType == 1) {
					if (minBoxMaxObjectId != null) {
						new MsgInBoxLoadTask() {

							@Override
							void doPre() {
							}

							@Override
							void doPost() {
								onLoad();
							}
						}.execute("more", minBoxMinObjectId, minBoxMaxObjectId);
					}
				} else {
					if (msendBoxMaxObjectId != null) {
						new MsgSendBoxLoadTask() {

							@Override
							void doPre() {
							}

							@Override
							void doPost() {
								onLoad();
							}
						}.execute("more", msendBoxMinObjectId, msendBoxMaxObjectId);
					}
				}

			}
		}, 2000);
	}

	class RefreshCacheReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			if (arg1.getAction().equals(REFRESH_CACHE)) {
				String mObjId = arg1.getStringExtra("messageObjectid");
				if(null != mObjId && !"".equals(mObjId)){
					refreshCache(mObjId);
					if (null != mData && mData.size() > 0) {
						for (Map<String, Object> mResult : mData) {
							if(mResult.get("messageObjectId").equals(mObjId)){
								if(mResult.get("read") != null && (Integer)(mResult.get("read")) == 0){
									mResult.put("read", 1);
								}
								break;
							}
						}
					}
				}
				mSimpleAdapter.notifyDataSetChanged();
			}
		}

	}
	private void showDialog(){
		if(null == dialogFragment){
			dialogFragment = MyDialogFragment.newInstance("数据加载中...");  
		}
		if(!dialogFragment.isAdded()){
			dialogFragment.show(getFragmentManager(), tag);
		}
	}
	private void removeDialog(){
		if( null != dialogFragment && dialogFragment.isAdded() && !dialogFragment.isDetached() && !dialogFragment.isHidden()){
			dialogFragment.dismiss();
		}
	}
}
