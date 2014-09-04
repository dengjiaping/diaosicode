package com.itcalf.renhe.context.room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.itcalf.renhe.R;
import com.itcalf.renhe.RenheApplication;
import com.itcalf.renhe.adapter.NWeiboAdapter;
import com.itcalf.renhe.cache.CacheManager;
import com.itcalf.renhe.context.room.RoomTask.IRoomBack;
import com.itcalf.renhe.context.template.BaseActivity;
import com.itcalf.renhe.dto.MessageBoards;
import com.itcalf.renhe.dto.MessageBoards.MessageBoardList;
import com.itcalf.renhe.utils.AsyncImageLoader;
import com.itcalf.renhe.utils.ToastUtil;
import com.itcalf.renhe.view.XListView;
import com.itcalf.renhe.view.XListView.IXListViewListener;

/**
 * Feature: 好友的客厅界面 Desc:好友的客厅界面，显示包括：我的客厅，朋友、同行、同城
 * 
 * @author xp
 * 
 */
public class AttMessageBoardActivity extends BaseActivity implements IXListViewListener {
	//登出标识（两次按下返回退出时的标识字段）
	private boolean logoutFlag;
	// 留言列表
	private XListView mWeiboListView;
	// 更多视图
	//	private View mFooterView;
	// 头部视图
	private View mHeaderView;

	private static final boolean TEST = false;
	// 数据适配器
	private NWeiboAdapter mAdapter;
	// 留言显示数据
	private List<Map<String, Object>> mWeiboList = new ArrayList<Map<String, Object>>();
	private Long max, min;
	private int mType;
	private Handler mHandler;
	private RefreshListForShieldReceiver refreshListForShieldReceiver;
	public static final String ROOM_REFRESH_AFTER_SHIELD = "room_refresh_after_shield";//屏蔽之后
	private String mSenderId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getTemplate().doInActivity(this, R.layout.rooms_attmsg_list);
		refreshListForShieldReceiver = new RefreshListForShieldReceiver();
		IntentFilter intentFilter2 = new IntentFilter();
		intentFilter2.addAction(ROOM_REFRESH_AFTER_SHIELD);
		registerReceiver(refreshListForShieldReceiver, intentFilter2);
		mHandler = new Handler();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (getSharedPreferences("setting_info", 0).getBoolean("fastdrag", false)) {
			mWeiboListView.setFastScrollEnabled(true);
		}else{
			mWeiboListView.setFastScrollEnabled(false);
		}
	}

	@Override
	protected void findView() {
		super.findView();
		mWeiboListView = (XListView) findViewById(R.id.weibo_list);
		//		mFooterView = LayoutInflater.from(this).inflate(R.layout.room_footerview, null);
		mHeaderView = LayoutInflater.from(this).inflate(R.layout.room_headerview, null);
	}

	@Override
	protected void initData() {
		super.initData();
		mType = getIntent().getIntExtra("type", 1);
		//		mAdapter = new WeiboAdapter(this, mWeiboList, R.layout.weibo_item_list, new String[] { "avatar", "username", "datetime",
		//				"content", "rawcontent", "client", "thumbnailPic1", "forwardThumbnailPic1" }, new int[] {
		//				R.id.avatar_img, R.id.username_txt, R.id.datetime_txt, R.id.content_txt, R.id.rawcontent_txt, R.id.client_txt,
		//				 R.id.thumbnailPic, R.id.forwardThumbnailPic }, getRenheApplication().getUserInfo().getEmail(),
		//				mWeiboListView);
		mAdapter = new NWeiboAdapter(this, mWeiboList, getRenheApplication().getUserInfo().getEmail(), mWeiboListView,mType);
		//		mWeiboListView.addHeaderView(mHeaderView, null, false);
		//		mWeiboListView.addFooterView(mFooterView, null, false);
		mWeiboListView.setAdapter(mAdapter);

		if (TEST) {

		} else {
			
			switch (mType) {
			case 1:
				setTextValue(R.id.title_txt, "我的客厅");
				break;
			case 2:
				setTextValue(R.id.title_txt, "朋友");
				break;
			case 3:
				setTextValue(R.id.title_txt, "同行");
				break;
			case 4:
				setTextValue(R.id.title_txt, "同城");
				break;
			case 5:
				setTextValue(R.id.title_txt, "最受关注");
				break;
			case 6:
				setTextValue(R.id.title_txt, "留言列表");
				//				mEditBt.setBackgroundDrawable(getResources().getDrawable(R.drawable.homebt_selected));
				break;
			}
			showDialog(1);
			loadedCache(mType);
		}
	}

	/**
	 * 初始化加载服务端数据
	 */
	private void initLoaded() {
		new RoomTask(this, new IRoomBack() {
			@Override
			public void doPost(List<Map<String, Object>> result, long max, long min, MessageBoards msg) {
				removeDialog(1);
				if (null != result) {
					if (max > 0) {
						AttMessageBoardActivity.this.max = max;
					}
					if (min > 0) {
						AttMessageBoardActivity.this.min = min;
					}
					if (!result.isEmpty()) {
						//						mWeiboListView.showFootView();
						CacheManager.getInstance().populateData(AttMessageBoardActivity.this)
								.saveObject(msg, getRenheApplication().getUserInfo().getEmail(), mType + "");
						mWeiboList.clear();
						if (result.size() == 20) {
							//							mFooterView.setVisibility(View.VISIBLE);
						}
						mWeiboList.addAll(result);
						mAdapter.notifyDataSetChanged();
					} else {
						//						ToastUtil.showToast(AttMessageBoardActivity.this, "暂无新留言!");
					}
				} else {
					ToastUtil.showNetworkError(AttMessageBoardActivity.this);
				}
				msg = null;
				//				
			}

			@Override
			public void onPre() {
				//				mFooterView.setVisibility(View.GONE);
				//				toggleHeaderView(true);
			}
		}).execute(getRenheApplication().getUserInfo().getAdSId(), getRenheApplication().getUserInfo().getSid(), null, mType,
				"renew", 20, null, null);
	}

	private void loadedCache(int type) {
		MessageBoards result = (MessageBoards) CacheManager.getInstance().populateData(this)
				.getObject(getRenheApplication().getUserInfo().getEmail(), type + "");
		if (null != result) {
			removeDialog(1);
			MessageBoardList[] mbList = result.getMessageBoardList();
			List<Map<String, Object>> weiboList = new ArrayList<Map<String, Object>>();
			if (null != mbList && mbList.length > 0) {
				if (mbList.length < 20) {
					//					mFooterView.setVisibility(View.GONE);
				}
				for (int i = 0; i < mbList.length; i++) {
					MessageBoardList mb = mbList[i];
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("objectId", mb.getObjectId());
					map.put("avatar", R.drawable.avatar);
					if (null != mb.getSenderUserFace()) {
						map.put("userface", mb.getSenderUserFace());
					}
					map.put("sid", mb.getSenderSid());
					map.put("username", mb.getSenderName());
					map.put("datetime", mb.getCreatedDate());

					map.put("thumbnailPic1", R.drawable.none);
					map.put("forwardThumbnailPic1", R.drawable.none);

					map.put("thumbnailPic", mb.getThumbnailPic() == null ? "" : mb.getThumbnailPic());

					map.put("forwardThumbnailPic", mb.getForwardThumbnailPic() == null ? "" : mb.getForwardThumbnailPic());
					map.put("content", Html.fromHtml(mb.getMessageBoardContent()).toString());
					if (null != mb.getForwardMessageBoardContent()) {
						map.put("rawcontent", Html.fromHtml(mb.getForwardMessageBoardContent()).toString());
					}
					map.put("client", "来自" + mb.getFromSource());
					map.put("reply", mb.getReplyNum());
					map.put("favourNumber", mb.getLikedNum());
					map.put("isFavour", mb.isLiked());
					map.put("senderTitle", mb.getSenderTitle());
					map.put("senderCompany", mb.getSenderCompany());
					map.put("senderIndustry", mb.getSenderIndustry());
					map.put("senderLocation", mb.getSenderLocation());
					map.put("accountType", mb.getSenderAccountType());
					map.put("isRealName", mb.isSenderIsRealname());
					
					map.put("isForwardRenhe", mb.isForwardRenhe());// 是否是人和网的转发，是人和网的转发，会返回forwardMemberName、forwardMemberSId、forwardMessageBoardObjectId、forwardMessageBoardId
					map.put("forwardMemberName", mb.getForwardMemberName());//被转发客厅的会员姓名
					map.put("forwardMemberSId", mb.getForwardMemberSId());//被转发客厅的会员sid
					map.put("forwardMessageBoardObjectId", mb.getForwardMessageBoardObjectId());//被转发客厅的objectId
					map.put("forwardMessageBoardId", mb.getForwardMessageBoardId());// 被转发客厅的id
					weiboList.add(map);
				}
				max = result.getMaxCreatedDate();
				min = result.getMinCreatedDate();
				mWeiboList.addAll(weiboList);
				mAdapter.notifyDataSetChanged();
			}
			mbList = null;
		} else {
			initLoaded();
		}
		result = null;
	}

	private void toggleHeaderView(boolean isShow) {
		if (isShow) {
			mHeaderView.findViewById(R.id.waitPb).setVisibility(View.VISIBLE);
			((TextView) mHeaderView.findViewById(R.id.titleTv)).setText("加载中...");
		} else {
			mHeaderView.findViewById(R.id.waitPb).setVisibility(View.GONE);
			((TextView) mHeaderView.findViewById(R.id.titleTv)).setText("点击刷新");
		}
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
		mWeiboListView.setPullLoadEnable(false);
		mWeiboListView.setXListViewListener(this);
		// 监听留言列表单击事件
		mWeiboListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				position -= 1;
				if (mWeiboList.size() > (position)) {
					String objectId = (String) mWeiboList.get(position).get("objectId");
					String sid = (String) mWeiboList.get(position).get("sid");
					Bundle bundle = new Bundle();
					bundle.putString("sid", sid);
					bundle.putString("objectId", objectId);
					startActivity(TwitterShowMessageBoardActivity.class, bundle);
				}
			}

		});
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
			if (logoutFlag) {
				AsyncImageLoader.getInstance().clearCache();
				//				finish();
				if (getSharedPreferences("setting_info", 0).getBoolean("clearcache", false)) {
					CacheManager.getInstance().populateData(this).clearCache(getRenheApplication().getUserInfo().getEmail());
				}
				//关闭通知栏消息
				((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(10001);
				RenheApplication.getInstance().exit();
			} else {
				ToastUtil.showToast(this, "请再点击一次退出程序!");
				logoutFlag = true;
				handler.postDelayed(run, 2000);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event); // 最后，一定要做完以后返回
		// true，或者在弹出菜单后返回true，其他键返回super，让其他键默认
	}

	private void onLoad() {
		mWeiboListView.stopRefresh();
		mWeiboListView.stopLoadMore();
		mWeiboListView.setRefreshTime("刚刚");
	}

	@Override
	public void onRefresh() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {

				// 执行留言列表异步加载
				new RoomTask(AttMessageBoardActivity.this, new IRoomBack() {
					@Override
					public void doPost(List<Map<String, Object>> result, long max, long min, MessageBoards msg) {
						if (null != result) {
							if(!TextUtils.isEmpty(mSenderId)){
								refreshListForShield(mSenderId, mType+"");
							}
							if (max > 0) {
								AttMessageBoardActivity.this.max = max;
							}
							if (!result.isEmpty()) {
								//								ToastUtil.showToast(AttMessageBoardActivity.this, "加载成功!");
								mWeiboList.clear();
								CacheManager.getInstance().populateData(AttMessageBoardActivity.this)
										.saveObject(msg, getRenheApplication().getUserInfo().getEmail(), mType + "");
								mWeiboList.addAll(result);
								mAdapter.notifyDataSetChanged();
							} else {
								//								ToastUtil.showToast(AttMessageBoardActivity.this, "暂无新留言!");
							}
							
						} else {
							ToastUtil.showNetworkError(AttMessageBoardActivity.this);
						}
						msg = null;
						onLoad();
					}

					@Override
					public void onPre() {
					}
				}).execute(getRenheApplication().getUserInfo().getAdSId(), getRenheApplication().getUserInfo().getSid(), null,
						mType, "renew", 20, null, AttMessageBoardActivity.this.max);

			}
		}, 2000);
	}

	//最受关注，返回数量是固定值，不需要加载更多功能
	@Override
	public void onLoadMore() {
		//		mHandler.postDelayed(new Runnable() {
		//			@Override
		//			public void run() {
		//
		//				new RoomTask(AttMessageBoardActivity.this, new IRoomBack() {
		//					@Override
		//					public void doPost(List<Map<String, Object>> result, long max, long min, MessageBoards msg) {
		//						if (null != result) {
		//							if (!result.isEmpty()) {
		//								if (min > 0) {
		//									AttMessageBoardActivity.this.min = min;
		//								}
		//								mWeiboList.addAll(result);
		//								mAdapter.notifyDataSetChanged();
		//							}
		//						} else {
		//							ToastUtil.showNetworkError(AttMessageBoardActivity.this);
		//						}
		//						msg = null;
		//						onLoad();
		//					}
		//
		//					@Override
		//					public void onPre() {
		//					}
		//				}).execute(getRenheApplication().getUserInfo().getAdSId(), getRenheApplication().getUserInfo().getSid(), null,
		//						mType, "more", 20, min, null);
		//			
		//			}
		//		}, 2000);
	}
	@SuppressWarnings("unused")
	private void refreshListForShield(String senderSid, String type) {
		//屏蔽你
		CacheManager.getInstance().populateData(AttMessageBoardActivity.this)
				.deleteObject(getRenheApplication().getUserInfo().getEmail(), type + "");
		for (int k = 0; k < mWeiboList.size(); k++) {
			if (mWeiboList.get(k).get("sid").equals(senderSid)) {
				mWeiboList.remove(k);
			}
		}

//		mAdapter.notifyDataSetChanged();
	}
	class RefreshListForShieldReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			String senderSid = arg1.getStringExtra("senderSid");
			String type = arg1.getStringExtra("type");
			mSenderId = senderSid;
			if(!TextUtils.isEmpty(senderSid)){
				if(!TextUtils.isEmpty(type)){
					refreshListForShield(senderSid, type+"");
				}else{
					refreshListForShield(senderSid, mType+"");
				}
			}
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (null != refreshListForShieldReceiver) {
			unregisterReceiver(refreshListForShieldReceiver);
		}
	}
}
