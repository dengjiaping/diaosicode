package com.itcalf.renhe.context.room;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.itcalf.renhe.R;
import com.itcalf.renhe.RenheApplication;
import com.itcalf.renhe.adapter.NewsWeiboAdapter;
import com.itcalf.renhe.cache.CacheManager;
import com.itcalf.renhe.context.innermsg.SwipeBackActivity;
import com.itcalf.renhe.context.room.RoomNewMsgTask.IRoomBack;
import com.itcalf.renhe.dto.NewMessageBoards;

/**
 * Feature:显示留言列表界面 
 * Description:显示未读留言列表界面
 * 
 * @author wangning
 * 
 */
public class NewMessageBoardActivity extends SwipeBackActivity {
	// 留言列表
	private ListView mWeiboListView;
	// 数据适配器
	private NewsWeiboAdapter mAdapter;
	// 留言显示数据
	private List<Map<String, Object>> mWeiboList = new ArrayList<Map<String, Object>>();
	private List<NewMessageBoards> messageBoardsList = new ArrayList<NewMessageBoards>();
    private SQLiteDatabase db;
    private static final long EXPIRED_TIME = 24 * 60 * 60 * 1000 * 14;
    private RelativeLayout blankLayout;
    private TextView blankTv;
    private ImageView nowifiIv;
    private boolean hasCache = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getTemplate().doInActivity(this, R.layout.news_rooms_msg_list);
	}

	@Override
	protected void onResume() {
		super.onResume();
//		mAdapter.notifyDataSetChanged();
	}

	@Override
	protected void findView() {
		super.findView();
		mWeiboListView = (ListView) findViewById(R.id.weibo_list);
//		findViewById(R.id.editBt).setVisibility(View.INVISIBLE);
		blankLayout = (RelativeLayout)findViewById(R.id.blank_rl);
		blankTv = (TextView)findViewById(R.id.balnk_rl_tv);
		nowifiIv = (ImageView)findViewById(R.id.noreplyiv);
	}

	@Override
	protected void initData() {
		super.initData();
		// 留言列表适配器
		mAdapter = new NewsWeiboAdapter(this, mWeiboList, R.layout.news_weibo_list_item, new String[] { "avatar", "senderUsername", "datetime",
				"replyContent", "sourceContent", "client"}, new int[] { R.id.avatar_img,
				R.id.username_txt, R.id.datetime_txt, R.id.content_txt, R.id.rawcontent_txt, R.id.client_txt}, getRenheApplication().getUserInfo().getEmail(), mWeiboListView);
		mWeiboListView.setAdapter(mAdapter);
		setTextValue(R.id.title_txt, "提醒");
		showDialog(1);
		//打开或创建message.db数据库  
		String email = RenheApplication.getInstance().getUserInfo().getEmail();
//		String dbName;
//		if(email.indexOf("@") != -1){
//			dbName = email.substring(0, email.indexOf("@") - 1);
//		}else{
//			dbName = email;
//		}
//		dbName.replaceAll(".", "_");
//		dbName += ".db";
//        db = openOrCreateDatabase(dbName, Context.MODE_PRIVATE, null);  
		db = CacheManager.getInstance().openDB();
       
//		test();
        if(null == db){
        	 initLoaded();//读取服务器数据
        }else{
        	 //创建unreadmsg表  
            db.execSQL("CREATE TABLE if not exists unreadmsg (_id INTEGER PRIMARY KEY AUTOINCREMENT, notifyObjectId VARCHAR, messageBoardObjectId VARCHAR," +
    							        		"type SMALLINT, sendersid VARCHAR, sendername VARCHAR," +
    							        		"senderuserface VARCHAR, senderreplyContent VARCHAR, sendercreatedDate VARCHAR," +
    							        		"fromSource VARCHAR, sourcecontent VARCHAR, sourceobjectId VARCHAR,firstreaddate VARCHAR)");  
        	loadCache();
        }
	}

	private void filterCacheByResult(List<Map<String, Object>> result){
		for(int i = result.size() - 1; i >= 0 ; i --){
			HashMap<String, Object> map = (HashMap<String, Object>) result.get(i);
			Cursor c2 = db.rawQuery("SELECT * FROM unreadmsg WHERE notifyObjectId = ?",
					new String[] { (String) map.get("notifyObjectId") });
			if (!c2.moveToNext()) {
				mWeiboList.add(0,map);
			}
		}
	}
	@SuppressLint("SimpleDateFormat")
	private void loadCache(){
		List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
        Cursor c = db.rawQuery("SELECT * FROM unreadmsg", null);  
        while (c.moveToNext()) {  
            int _id = c.getInt(c.getColumnIndex("_id"));  
            String unreadObjectId = c.getString(c.getColumnIndex("messageBoardObjectId"));  
            String notifyObjectId = c.getString(c.getColumnIndex("notifyObjectId"));  
            String sourceObjectId = c.getString(c.getColumnIndex("sourceobjectId"));  
            String sourceContent = c.getString(c.getColumnIndex("sourcecontent"));  
            String userface = c.getString(c.getColumnIndex("senderuserface"));  
            String senderSid = c.getString(c.getColumnIndex("sendersid"));  
            String senderUsername = c.getString(c.getColumnIndex("sendername"));  
            String datetime = c.getString(c.getColumnIndex("sendercreatedDate"));  
            String replyContent = c.getString(c.getColumnIndex("senderreplyContent"));  
            String client = c.getString(c.getColumnIndex("fromSource"));  
            int type = c.getInt(c.getColumnIndex("type"));  
            String firstReadDate = c.getString(c.getColumnIndex("firstreaddate"));
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
                try {
					Date date = dateFormat.parse(firstReadDate);
					if(System.currentTimeMillis() - date.getTime() > EXPIRED_TIME){
						//删除数据  
				        db.delete("unreadmsg", "notifyObjectId = ?", new String[]{notifyObjectId});  
				        continue;
					}
				} catch (ParseException e) {
					e.printStackTrace();
				} 
            
            
            Map<String, Object> map = new HashMap<String, Object>();
			map.put("type", type);
			map.put("unreadObjectId", unreadObjectId);
			map.put("notifyObjectId", notifyObjectId);
				map.put("sourceObjectId", sourceObjectId);
				map.put("sourceContent", sourceContent);
				map.put("userface", userface);
				map.put("senderSid", senderSid);
				map.put("senderUsername", senderUsername);
				map.put("datetime", datetime);
				map.put("replyContent", replyContent);
				map.put("client", "来自" + client);
			map.put("avatar", R.drawable.avatar);
			mList.add(0,map);
        }  
        c.close();  
        if(mList.size() > 0){
        	mWeiboList.addAll(mList);
        	hasCache = true;
        }
        mAdapter.notifyDataSetChanged();
        initLoaded();//读取服务器数据
	}
	@SuppressLint("SimpleDateFormat")
	private void saveCache(List<Map<String, Object>> result) {
		if (null != result) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date curDate = new Date(System.currentTimeMillis());//获取当前时间       
			String curDatestr = formatter.format(curDate);
			for (int i = result.size() - 1; i >= 0; i--) {
				HashMap<String, Object> map = (HashMap<String, Object>) result.get(i);
				Cursor c2 = db.rawQuery("SELECT * FROM unreadmsg WHERE notifyObjectId = ?",
						new String[] { (String) map.get("notifyObjectId") });
				if (!c2.moveToNext()) {
					db.execSQL(
							"INSERT INTO unreadmsg VALUES (NULL, ?, ?,?, ?,?, ?,?, ?,?, ?,?,?)",
							new Object[] { map.get("notifyObjectId"), map.get("unreadObjectId"), map.get("type"),
									map.get("senderSid"), map.get("senderUsername"), map.get("userface"),
									map.get("replyContent"), map.get("datetime"), map.get("client"), map.get("sourceContent"),
									map.get("sourceObjectId"), curDatestr });
				}
			}
		}
	}
	/**
	 * 初始化加载服务端数据
	 */
	private void initLoaded() {

		
		new RoomNewMsgTask(this, new IRoomBack() {
			@Override
			public void doPost(List<Map<String, Object>> result, NewMessageBoards msg) {
				removeDialog(1);
				if (null != result) {
					if (!result.isEmpty()) {
						blankLayout.setVisibility(View.GONE);
						filterCacheByResult(result);
//						mWeiboList.addAll(0, result);
						mAdapter.notifyDataSetChanged();
						
					} else {
						if(!hasCache){
							blankLayout.setVisibility(View.VISIBLE);
						}
//						ToastUtil.showToast(NewMessageBoardActivity.this, "暂无新留言!");
					}
				} else {
					if(!hasCache){
						blankLayout.setVisibility(View.VISIBLE);
						nowifiIv.setImageResource(R.drawable.wifi);
						blankTv.setText(getString(R.string.no_net_connected));
					}
//					ToastUtil.showNetworkError(NewMessageBoardActivity.this);
				}
				 if(null != db){
					 saveCache(result);
				 }
			}

			@Override
			public void onPre() {
			}
		}).execute(getRenheApplication().getUserInfo().getAdSId(), getRenheApplication().getUserInfo().getSid());
	}
	private void test(){
		removeDialog(1);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("type", "1");
		map.put("unreadObjectId", "");
		map.put("sourceObjectId", "");
		map.put("avatar", R.drawable.avatar);
		map.put("userface", "");
		map.put("senderSid", "38");
		map.put("senderUsername", "just sender");
		map.put("datetime", "2014-5-14");
		map.put("replyContent", "good![左哼哼]");
		map.put("client", "来自" + "Android客户端");
		map.put("sourceContent", "红红火火[嘻嘻]");
		map.put("sourceReplyNum", 3);
		map.put("favourNumber", 1);
		map.put("isFavour", true);
		mWeiboList.add(map);
		map = new HashMap<String, Object>();
		map.put("type", "2");
		map.put("unreadObjectId", "");
		map.put("sourceObjectId", "");
		map.put("avatar", R.drawable.avatar);
		map.put("userface", "");
		map.put("senderSid", "38");
		map.put("senderUsername", "just sender");
		map.put("datetime", "2014-5-15");
		map.put("replyContent", "这个赞不显示");
		map.put("client", "来自" + "Android客户端");
		map.put("sourceContent", "被赞内容");
		map.put("sourceReplyNum", 3);
		map.put("favourNumber", 1);
		map.put("isFavour", true);
		mWeiboList.add(map);
	}
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 1:
			ProgressDialog findPd = new ProgressDialog(this);
			findPd.setMessage("数据加载中...");
//			findPd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			findPd.setCanceledOnTouchOutside(false);
			return findPd;
		default:
			return null;
		}
	}

	@Override
	protected void initListener() {
		super.initListener();
		// 监听留言列表单击事件
		mWeiboListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (mWeiboList.size() > (position)) {
					String objectId = (String) mWeiboList.get(position).get("sourceObjectId");
					String sid = getRenheApplication().getUserInfo().getSid();
					Bundle bundle = new Bundle();
					bundle.putString("sid", sid);
					bundle.putString("objectId", objectId);
					bundle.putBoolean("isFromNoticeList", true);
					startActivity(TwitterShowMessageBoardActivity.class, bundle);
				}
			}

		});
	}

	@Override
	public void finish() {
		super.finish();
		mWeiboList = new ArrayList<Map<String, Object>>();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(null != messageBoardsList){
			messageBoardsList.clear();
		}
		if(null != mWeiboList){
			mWeiboList.clear();
		}
		if(null != db){
			//关闭当前数据库  
	        db.close();  
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
}
