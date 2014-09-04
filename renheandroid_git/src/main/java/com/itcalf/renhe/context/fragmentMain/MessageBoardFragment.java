package com.itcalf.renhe.context.fragmentMain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itcalf.renhe.R;
import com.itcalf.renhe.RenheApplication;
import com.itcalf.renhe.adapter.NWeiboAdapter;
import com.itcalf.renhe.adapter.NWeiboAdapter.ViewHolder;
import com.itcalf.renhe.cache.CacheManager;
import com.itcalf.renhe.context.room.RoomTask;
import com.itcalf.renhe.context.room.RoomTask.IRoomBack;
import com.itcalf.renhe.context.room.TwitterShowMessageBoardActivity;
import com.itcalf.renhe.dto.MessageBoards;
import com.itcalf.renhe.dto.MessageBoards.MessageBoardList;
import com.itcalf.renhe.utils.ToastUtil;
import com.itcalf.renhe.view.TextViewFixTouchConsume;
import com.itcalf.renhe.view.XListView;
import com.itcalf.renhe.view.XListView.IXListViewListener;
import com.umeng.analytics.MobclickAgent;

/**
 * Feature:显示留言列表界面 
 * Description:显示留言列表界面，包括：我的客厅，朋友、同行、同城
 * 
 * @author xp
 * 
 */
public class MessageBoardFragment extends Fragment implements IXListViewListener {
	//登出标识（两次按下返回退出时的标识字段）
	private boolean logoutFlag;
	// 留言列表
	private XListView mWeiboListView;
	//新消息通知视图
	private RelativeLayout newMsgNoticeRl;
	private TextView newMsgNumTv;
	private SharedPreferences msp;
	private SharedPreferences.Editor mEditor;
	//	private NoticeRlReceiver noticeRlReceiver;
	private final static String ICON_ACTION = "notice_icon_num";
	// 更多视图
	//	private View mFooterView;
	// private View mHeaderView;
	// 测试数据
	private static final boolean TEST = false;
	// 数据适配器
	private NWeiboAdapter mAdapter;
	//	private WeiboAdapter mAdapter;
	// 留言显示数据
	private List<Map<String, Object>> mWeiboList = new ArrayList<Map<String, Object>>();
	// 获取更新、更多数据的最大、最小值
	private long max, min;
	// 查看类型（eg：我的客厅，朋友、同行、同城）
	private int mType;
	private String mViewSid;
	private List<MessageBoards> messageBoardsList = new ArrayList<MessageBoards>();
	private ChangeItemStateReceiver changeItemStateReceiver;
	public static final String ROOM_ITEM_STATE_ACTION_STRING = "room.item.statechanged_favour";
	public static final String ROOM_ITEM_STATE_ACTION_STRING_CHANGE = "room.item.statechanged_favour_change";
	public static final String ROOM_ITEM_STATE_ACTION_STRING_REPLY = "room.item.statechanged_reply";
	public static final String ROOM_ITEM_STATE_ACTION_STRING_REPLY_CHANGE = "room.item.statechanged_reply_change";
	public static final String ROOM_REFRESH_AFTER_SHIELD = "room_refresh_after_shield";//屏蔽之后
	private Handler mHandler;
	private AnimationDrawable animationDrawable;
	private RefreshListForShieldReceiver refreshListForShieldReceiver;
	private String mSenderId;
	
	private Context context;
	private View rootView;
	private Handler mLoadCacheHandler;
	private Runnable loadCacheRun;
	private DialogFragment dialogFragment;
	private String tag = "my_dialog"; 
	private static final int REQUEST_DELAY_TIME = 800;
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
			rootView = inflater.inflate(R.layout.rooms_msg_list, null);
		}
		ViewGroup parent = (ViewGroup) rootView.getParent();  
        if (parent != null) {  
            parent.removeView(rootView);  
        }   
		context = (Context)getActivity();
		findView(rootView);
		initData();
		initListener();
		changeItemStateReceiver = new ChangeItemStateReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ROOM_ITEM_STATE_ACTION_STRING);
		intentFilter.addAction(ROOM_ITEM_STATE_ACTION_STRING_REPLY);
		intentFilter.addAction(ROOM_ITEM_STATE_ACTION_STRING_REPLY_CHANGE);
		intentFilter.addAction(ROOM_ITEM_STATE_ACTION_STRING_CHANGE);
		getActivity().registerReceiver(changeItemStateReceiver, intentFilter);
		refreshListForShieldReceiver = new RefreshListForShieldReceiver();
		IntentFilter intentFilter2 = new IntentFilter();
		intentFilter2.addAction(ROOM_REFRESH_AFTER_SHIELD);
		getActivity().registerReceiver(refreshListForShieldReceiver, intentFilter2);
		mHandler = new Handler();
		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("MessageboardScreen"); //统计页面
		if (getActivity().getSharedPreferences("setting_info", 0).getBoolean("fastdrag", false)) {
			mWeiboListView.setFastScrollEnabled(true);
		} else {
			mWeiboListView.setFastScrollEnabled(false);
		}
	}
	protected void findView(View view) {
		mWeiboListView = (XListView) view.findViewById(R.id.weibo_list);
		newMsgNoticeRl = (RelativeLayout) view.findViewById(R.id.newmsg_notify_ll);
		newMsgNumTv = (TextView) view.findViewById(R.id.newmsg_notify_num);
		msp = getActivity().getSharedPreferences("setting_info", 0);
		int noticeNum = msp.getInt("unreadmsg_num", 0);
		if (noticeNum > 0) {
			//newMsgNoticeRl.setVisibility(View.VISIBLE);
			newMsgNumTv.setText(noticeNum + "条新消息");
		}
		
	}

	protected void initData() {
//		mType = getActivity().getIntent().getIntExtra("type", 1);
		String name = getActivity().getIntent().getStringExtra("friendName");
		// 留言列表适配器
		mAdapter = new NWeiboAdapter(getActivity(), mWeiboList, RenheApplication.getInstance().getUserInfo().getEmail(), mWeiboListView,mType);
		//		mAdapter = new WeiboAdapter(this, mWeiboList, R.layout.weibo_item_list, new String[] { "avatar", "username", "datetime",
		//				"content", "rawcontent", "client", "thumbnailPic1", "forwardThumbnailPic1" }, new int[] { R.id.avatar_img,
		//				R.id.username_txt, R.id.datetime_txt, R.id.content_txt, R.id.rawcontent_txt, R.id.client_txt, R.id.thumbnailPic,
		//				R.id.forwardThumbnailPic }, getRenheApplication().getUserInfo().getEmail(), mWeiboListView);
		mWeiboListView.setAdapter(mAdapter);
		if(null != mWeiboList){
			mWeiboList.clear();
			mAdapter.notifyDataSetChanged();
		}
//		if (TEST) {
//		} else {
//			mViewSid = getActivity().getIntent().getStringExtra("viewSid");
//			
//			switch (mType) {
//			case 1:
//				setTextValue(R.id.title_txt, "我的客厅");
//				break;
//			case 2:
//				setTextValue(R.id.title_txt, "朋友");
//				break;
//			case 3:
//				setTextValue(R.id.title_txt, "同行");
//				break;
//			case 4:
//				setTextValue(R.id.title_txt, "同城");
//				break;
//			case 5:
//				setTextValue(R.id.title_txt, "最受关注");
//				break;
//			case 6:
//				if(!TextUtils.isEmpty(name)){
//					setTextValue(R.id.title_txt, name+"的留言");
//				}else{
//					setTextValue(R.id.title_txt, "留言列表");
//				}
//				mEditBt.setVisibility(View.INVISIBLE);
//				LayoutParams params = (RelativeLayout.LayoutParams) searchIB.getLayoutParams();
//				params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//				searchIB.setLayoutParams(params); //使layout更新
//				break;
//			}
			// 查看自己的客厅，先加载缓存数据
			if (null == mViewSid) {
//				getActivity().showDialog(1);
				showDialog();
				mLoadCacheHandler = new Handler(new Callback() {

					@Override
					public boolean handleMessage(Message arg0) {
						switch (arg0.what) {
						case 1:
							mAdapter.notifyDataSetChanged();
//							getActivity().removeDialog(1);
							removeDialog();
							break;
						case 2:
							initLoaded("renew", null, null);
							break;
					 }
						return false;
					}
				});
				loadCacheRun = new Runnable() {

					@Override
					public void run() {
						loadedCache(mType);
						
					}
				};
				mLoadCacheHandler.postDelayed(loadCacheRun,REQUEST_DELAY_TIME);//延迟500ms，防止slidemenu滑动卡顿
				
				
			} else {
//				getActivity().showDialog(1);
				showDialog();
				initLoaded("renew", null, null);
			}
//		}
	}

	/**
	 * 初始化加载服务端数据
	 */
	private void initLoaded(final String type, Object min, Object max) {
		new RoomTask(getActivity(), new IRoomBack() {
			FragmentActivity contextActivity = getActivity();
			@Override
			public void doPost(List<Map<String, Object>> result, long max, long min, MessageBoards msg) {
//				getActivity().removeDialog(1);
				removeDialog();
				if (null != result) {
					if (max > 0) {
						MessageBoardFragment.this.max = max;
					}
					if (min > 0) {
						MessageBoardFragment.this.min = min;
					}
					if (!result.isEmpty()) {
						mWeiboListView.showFootView();
						if (messageBoardsList == null) {
							messageBoardsList = new ArrayList<MessageBoards>();
						}
						messageBoardsList.add(0, msg);
						if (type.equals("new")) {
							if (msg.isAboveMaxCount()) {//Boolean 是否已经超过最大返回的数量(在请求的参数type为new时有效，若为true，则需要客户端清空本地之前的缓存数据，同时更新maxCreatedDate和minCreatedDate
								mWeiboList.clear();
								CacheManager.getInstance().populateData(contextActivity)
										.deleteObject(RenheApplication.getInstance().getUserInfo().getEmail(), mType + "");
							} else {
								refreshCacheAndListview(result, type);//如果旧的留言有新的回复，将其置顶，同时删除缓存文件、listview 里同objectId的item
							}
						}
						CacheManager.getInstance().populateData(contextActivity)
								.saveObject(messageBoardsList, RenheApplication.getInstance().getUserInfo().getEmail(), mType + "");
						mWeiboList.addAll(0, result);
						mAdapter.notifyDataSetChanged();
					} else {
						//						ToastUtil.showToast(MessageBoardActivity.this, "暂无新留言!");
					}
				} else {
					ToastUtil.showNetworkError(contextActivity);
				}
				msg = null;
			}

			@Override
			public void onPre() {
				//				mFooterView.setVisibility(View.GONE);
			}
		}).execute(RenheApplication.getInstance().getUserInfo().getAdSId(), RenheApplication.getInstance().getUserInfo().getSid(), mViewSid, mType,
				type, 20, min, max);
	}

	@SuppressWarnings("unchecked")
	private void refreshCacheAndListview(List<Map<String, Object>> result, String type) {
		for (int i = 0; i < result.size(); i++) {
			messageBoardsList = (List<MessageBoards>) CacheManager.getInstance().populateData(getActivity())
					.getObject(RenheApplication.getInstance().getUserInfo().getEmail(), type + "");
			if (null != messageBoardsList && !messageBoardsList.isEmpty()) {
				for (MessageBoards mresult : messageBoardsList) {
					MessageBoardList[] mbList = mresult.getMessageBoardList();
					if (null != mbList && mbList.length > 0) {
						for (int b = 0; b < mbList.length; b++) {
							MessageBoardList mb = mbList[b];
							if (mb.getObjectId().equals(result.get(i).get("Id"))) {
								//								mbList.remove(b);
							}
						}

					}
				}
			}
			for (int k = 0; k < mWeiboList.size(); k++) {
				if (mWeiboList.get(k).get("objectId").equals(result.get(i).get("objectId"))) {
					mWeiboList.remove(k);
				}
			}
		}

	}

	@SuppressWarnings("unused")
	private void refreshListForShield(String senderSid, String type) {
		//屏蔽你
		CacheManager.getInstance().populateData(getActivity())
				.deleteObject(RenheApplication.getInstance().getUserInfo().getEmail(), type + "");
		for (int k = 0; k < mWeiboList.size(); k++) {
			if (mWeiboList.get(k).get("sid").equals(senderSid)) {
				mWeiboList.remove(k);
			}
		}

//		mAdapter.notifyDataSetChanged();
	}

	/**
	 * 加载缓存数据
	 * 
	 * @param type
	 */
	@SuppressWarnings("unchecked")
	private void loadedCache(int type) {
		messageBoardsList = (List<MessageBoards>) CacheManager.getInstance().populateData(getActivity())
				.getObject(RenheApplication.getInstance().getUserInfo().getEmail(), type+"");
		if (messageBoardsList != null && !messageBoardsList.isEmpty()) {
			mWeiboListView.showFootView();
			
			for (MessageBoards result : messageBoardsList) {
				MessageBoardList[] mbList = result.getMessageBoardList();
				List<Map<String, Object>> weiboList = new ArrayList<Map<String, Object>>();
				if (null != mbList && mbList.length > 0) {
					for (int i = 0; i < mbList.length; i++) {
						MessageBoardList mb = mbList[i];
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("Id", mb.getId());
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
						map.put("bmiddlePic", mb.getBmiddlePic() == null ? "" : mb.getBmiddlePic());
						map.put("forwardBmiddlePic", mb.getForwardBmiddlePic() == null ? "" : mb.getForwardBmiddlePic());
						map.put("content", Html.fromHtml(mb.getMessageBoardContent()).toString());
						if (null != mb.getForwardMessageBoardContent()) {
							map.put("rawcontent", Html.fromHtml(mb.getForwardMessageBoardContent()).toString());
							map.put("forwardMessageMember", mb.getForwardMessageBoardAtMembers());
							//							map.put("forwardMessageMemberid", "dd5e630df24ba0ee");
						}
						map.put("messageBoardMember", mb.getAtMembers());
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
				}
				mWeiboList.addAll(weiboList);
				mbList = null;
				result = null;
			}
			MessageBoardFragment.this.max = messageBoardsList.get(0).getMaxCreatedDate();
			MessageBoardFragment.this.min = messageBoardsList.get(messageBoardsList.size() - 1).getMinCreatedDate();
			
			mLoadCacheHandler.sendMessage(handler.obtainMessage(1, null));
			if (type == 1) {
				if (RenheApplication.renheApplication.enterRoomTime == 0) {
					RenheApplication.renheApplication.enterRoomTime = System.currentTimeMillis();
					initLoaded("new", null, MessageBoardFragment.this.max);
				} else if ((System.currentTimeMillis() - RenheApplication.renheApplication.enterRoomTime) > 6 * 60 * 60 * 1000) {
					initLoaded("new", null, MessageBoardFragment.this.max);
					RenheApplication.renheApplication.enterRoomTime = System.currentTimeMillis();
				}
			} else if (type == 2) {
				if (RenheApplication.renheApplication.enterFriendTime == 0) {
					RenheApplication.renheApplication.enterFriendTime = System.currentTimeMillis();
					initLoaded("new", null, MessageBoardFragment.this.max);
				} else if ((System.currentTimeMillis() - RenheApplication.renheApplication.enterFriendTime) > 6 * 60 * 60 * 1000) {
					initLoaded("new", null, MessageBoardFragment.this.max);
					RenheApplication.renheApplication.enterFriendTime = System.currentTimeMillis();
				}
			} else if (type == 3) {
				if (RenheApplication.renheApplication.enterColleagueTime == 0) {
					RenheApplication.renheApplication.enterColleagueTime = System.currentTimeMillis();
					initLoaded("new", null, MessageBoardFragment.this.max);
				} else if ((System.currentTimeMillis() - RenheApplication.renheApplication.enterColleagueTime) > 6 * 60 * 60 * 1000) {
					initLoaded("new", null, MessageBoardFragment.this.max);
					RenheApplication.renheApplication.enterColleagueTime = System.currentTimeMillis();
				}
			} else if (type == 4) {
				if (RenheApplication.renheApplication.enterCityTime == 0) {
					RenheApplication.renheApplication.enterCityTime = System.currentTimeMillis();
					initLoaded("new", null, MessageBoardFragment.this.max);
				} else if ((System.currentTimeMillis() - RenheApplication.renheApplication.enterCityTime) > 6 * 60 * 60 * 1000) {
					initLoaded("new", null, MessageBoardFragment.this.max);
					RenheApplication.renheApplication.enterCityTime = System.currentTimeMillis();
				}
			} else if (type == 5) {
				if (RenheApplication.renheApplication.enterFollowTime == 0) {
					RenheApplication.renheApplication.enterFollowTime = System.currentTimeMillis();
					initLoaded("new", null, MessageBoardFragment.this.max);
				} else if ((System.currentTimeMillis() - RenheApplication.renheApplication.enterFollowTime) > 6 * 60 * 60 * 1000) {
					initLoaded("new", null, MessageBoardFragment.this.max);
					RenheApplication.renheApplication.enterFollowTime = System.currentTimeMillis();
				}
			}

		} else {
			mLoadCacheHandler.sendMessage(handler.obtainMessage(2, null));
		}

	}


	protected void initListener() {
		mWeiboListView.setPullLoadEnable(true);
		mWeiboListView.setXListViewListener(this);

//		mEditBt.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if (mType == 6) {
//				} else {
//					startActivityForResult(AddMessageBoardActivity.class, RoomsActivity.REQUEST_CODE_PUBLIC_MSG);
//				}
//			}
//		});
		// 监听留言列表单击事件
		mWeiboListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				position -= 1;//添加HeaderView之后导致OnItemClickListener的position移位 
				if (mWeiboList.size() > (position)) {
					String objectId = (String) mWeiboList.get(position).get("objectId");
					String sid = (String) mWeiboList.get(position).get("sid");
					boolean isFavour = (Boolean) mWeiboList.get(position).get("isFavour");
					int favourNumber = (Integer) mWeiboList.get(position).get("favourNumber");
					int replyNumber = (Integer) mWeiboList.get(position).get("reply");
					Bundle bundle = new Bundle();
					bundle.putString("sid", sid);
					bundle.putString("objectId", objectId);
					bundle.putBoolean("isFavour", isFavour);
					bundle.putInt("favourNumber", favourNumber);
					bundle.putInt("replyNum", replyNumber);
					Intent intent = new Intent(context, TwitterShowMessageBoardActivity.class);
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}

		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 2:
			if (resultCode == getActivity().RESULT_OK) {
				String objectId = data.getStringExtra("objectId");
				if (null != objectId && !"".equals(objectId)) {
					for (int i = 0; i < mWeiboList.size(); i++) {
						if (null != mWeiboList.get(i).get("objectId")
								&& mWeiboList.get(i).get("objectId").toString().equals(objectId)) {
							int reply = 0;
							if (null != mWeiboList.get(i).get("reply")) {
								reply = (Integer) mWeiboList.get(i).get("reply");
							}
							mWeiboList.get(i).put("reply", reply);
							updateView(i, 1, reply + 1, false);
							break;
						}
					}
					//					mAdapter.notifyDataSetChanged();
				}

				//更新缓存
				messageBoardsList = (List<MessageBoards>) CacheManager.getInstance().populateData(context)
						.getObject(RenheApplication.getInstance().getUserInfo().getEmail(), mType + "");
				if (messageBoardsList != null && !messageBoardsList.isEmpty()) {
					for (int k = 0; k < messageBoardsList.size(); k++) {
						MessageBoards messageBoards = messageBoardsList.get(k);
						MessageBoardList[] mbList = messageBoards.getMessageBoardList();
						if (null != mbList && mbList.length > 0) {
							for (int i = 0; i < mbList.length; i++) {
								MessageBoardList mb = mbList[i];
								if (mb.getObjectId().equals(objectId)) {
									mb.setReplyNum((Integer) mWeiboList.get(i).get("reply"));
									for (int j = 0; j < mbList.length; j++) {
										if (j == i) {
											mbList[j] = mb;
											break;
										}
									}
									messageBoards.setMessageBoardList(mbList);
									messageBoardsList.set(k, messageBoards);
									CacheManager
											.getInstance()
											.populateData(context)
											.saveObject(messageBoardsList, RenheApplication.getInstance().getUserInfo().getEmail(),
													mType + "");
									break;
								}
							}
						}
					}
				}

			}
			break;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (null != changeItemStateReceiver) {
			context.unregisterReceiver(changeItemStateReceiver);
		}
		if (null != refreshListForShieldReceiver) {
			context.unregisterReceiver(refreshListForShieldReceiver);
		}
		if (null != messageBoardsList) {
			messageBoardsList.clear();
		}
		if (null != mWeiboList) {
			mWeiboList.clear();
		}
	}

	class ChangeItemStateReceiver extends BroadcastReceiver {

		@SuppressWarnings("unchecked")
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ROOM_ITEM_STATE_ACTION_STRING)) {
				String objectId = intent.getStringExtra("objectId");
				int favourNumber = intent.getIntExtra("favourNumber", 0);
				boolean isFavour = intent.getBooleanExtra("isFavour", false);
				boolean isNeedUpdateView = intent.getBooleanExtra("isNeedUpdateView", true);
				//更新listview
				for (int i = 0; i < mWeiboList.size(); i++) {
					Map<String, Object> map = mWeiboList.get(i);
					if (map.get("objectId").toString().equals(objectId)) {
						map.put("isFavour", isFavour);
						map.put("favourNumber", favourNumber);
						mWeiboList.set(i, map);
						if (isNeedUpdateView) {
							updateView(i, 2, favourNumber, isFavour);
						}
						break;
					}
				}
				//				mAdapter.notifyDataSetChanged();

				//更新缓存
				messageBoardsList = (List<MessageBoards>) CacheManager.getInstance().populateData(context)
						.getObject(RenheApplication.getInstance().getUserInfo().getEmail(), mType + "");
				if (messageBoardsList != null && !messageBoardsList.isEmpty()) {
					for (int k = 0; k < messageBoardsList.size(); k++) {
						MessageBoards messageBoards = messageBoardsList.get(k);
						MessageBoardList[] mbList = messageBoards.getMessageBoardList();
						if (null != mbList && mbList.length > 0) {
							for (int i = 0; i < mbList.length; i++) {
								MessageBoardList mb = mbList[i];
								if (mb.getObjectId().equals(objectId)) {
									mb.setLiked(isFavour);
									mb.setLikedNum(favourNumber);
									for (int j = 0; j < mbList.length; j++) {
										if (j == i) {
											mbList[j] = mb;
											break;
										}
									}
									messageBoards.setMessageBoardList(mbList);
									messageBoardsList.set(k, messageBoards);
									CacheManager
											.getInstance()
											.populateData(context)
											.saveObject(messageBoardsList, RenheApplication.getInstance().getUserInfo().getEmail(),
													mType + "");
									break;
								}
							}
						}
					}
				}
			} else if (intent.getAction().equals(ROOM_ITEM_STATE_ACTION_STRING_REPLY)) {

				String objectId = intent.getStringExtra("objectId");
				if (null != objectId && !"".equals(objectId)) {
					for (int i = 0; i < mWeiboList.size(); i++) {
						if (null != mWeiboList.get(i).get("objectId")
								&& mWeiboList.get(i).get("objectId").toString().equals(objectId)) {
							int reply = 0;
							if (null != mWeiboList.get(i).get("reply")) {
								reply = (Integer) mWeiboList.get(i).get("reply");
							}
							mWeiboList.get(i).put("reply", reply + 1);
							updateView(i, 1, reply + 1, false);
							break;
						}
					}
					//					mAdapter.notifyDataSetChanged();
					//					updateView();
				}

				//更新缓存
				messageBoardsList = (List<MessageBoards>) CacheManager.getInstance().populateData(context)
						.getObject(RenheApplication.getInstance().getUserInfo().getEmail(), mType + "");
				if (messageBoardsList != null && !messageBoardsList.isEmpty()) {
					for (int k = 0; k < messageBoardsList.size(); k++) {
						MessageBoards messageBoards = messageBoardsList.get(k);
						MessageBoardList[] mbList = messageBoards.getMessageBoardList();
						if (null != mbList && mbList.length > 0) {
							for (int i = 0; i < mbList.length; i++) {
								MessageBoardList mb = mbList[i];
								if (mb.getObjectId().equals(objectId)) {
									mb.setReplyNum((Integer) mWeiboList.get(i).get("reply"));
									for (int j = 0; j < mbList.length; j++) {
										if (j == i) {
											mbList[j] = mb;
											break;
										}
									}
									messageBoards.setMessageBoardList(mbList);
									messageBoardsList.set(k, messageBoards);
									CacheManager
											.getInstance()
											.populateData(context)
											.saveObject(messageBoardsList, RenheApplication.getInstance().getUserInfo().getEmail(),
													mType + "");
									break;
								}
							}
						}
					}
				}
			} else if (intent.getAction().equals(ROOM_ITEM_STATE_ACTION_STRING_REPLY_CHANGE)) {

				String objectId = intent.getStringExtra("objectId");
				int nReplyNum = intent.getIntExtra("new_repleynum", 0);
				if (null != objectId && !"".equals(objectId)) {
					for (int i = 0; i < mWeiboList.size(); i++) {
						if (null != mWeiboList.get(i).get("objectId")
								&& mWeiboList.get(i).get("objectId").toString().equals(objectId)) {
							int reply = 0;
							if (null != mWeiboList.get(i).get("reply")) {
								reply = (Integer) mWeiboList.get(i).get("reply");
							}
							mWeiboList.get(i).put("reply", nReplyNum);
							updateView(i, 1, nReplyNum, false);
							break;
						}
					}
					//					mAdapter.notifyDataSetChanged();
					//					updateView();
				}

				//更新缓存
				messageBoardsList = (List<MessageBoards>) CacheManager.getInstance().populateData(context)
						.getObject(RenheApplication.getInstance().getUserInfo().getEmail(), mType + "");
				if (messageBoardsList != null && !messageBoardsList.isEmpty()) {
					for (int k = 0; k < messageBoardsList.size(); k++) {
						MessageBoards messageBoards = messageBoardsList.get(k);
						MessageBoardList[] mbList = messageBoards.getMessageBoardList();
						if (null != mbList && mbList.length > 0) {
							for (int i = 0; i < mbList.length; i++) {
								MessageBoardList mb = mbList[i];
								if (mb.getObjectId().equals(objectId)) {
									mb.setReplyNum((Integer) mWeiboList.get(i).get("reply"));
									for (int j = 0; j < mbList.length; j++) {
										if (j == i) {
											mbList[j] = mb;
											break;
										}
									}
									messageBoards.setMessageBoardList(mbList);
									messageBoardsList.set(k, messageBoards);
									CacheManager
											.getInstance()
											.populateData(context)
											.saveObject(messageBoardsList, RenheApplication.getInstance().getUserInfo().getEmail(),
													mType + "");
									break;
								}
							}
						}
					}
				}

			}
		}
	}

	class NoticeRlReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ICON_ACTION)) {
				int num = intent.getIntExtra("notice_num", 0);
				mEditor = msp.edit();
				mEditor.putInt("unreadmsg_num", num);
				mEditor.commit();
				if (num == 0) {
					newMsgNoticeRl.setVisibility(View.GONE);
				} else if (num > 0) {
					newMsgNoticeRl.setVisibility(View.VISIBLE);
					newMsgNumTv.setText(num + "条新消息");
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

//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			if (mType != 6) {
//				if (logoutFlag) {
//					AsyncImageLoader.getInstance().clearCache();
//					//					finish();
//					if (getSharedPreferences("setting_info", 0).getBoolean("clearcache", false)) {
//						CacheManager.getInstance().populateData(this).clearCache(RenheApplication.getInstance()getUserInfo().getEmail());
//					}
//					//关闭通知栏消息
//					((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(10001);
//					RenheApplication.getInstance().exit();
//				} else {
//					ToastUtil.showToast(this, "请再点击一次退出程序!");
//					logoutFlag = true;
//					handler.postDelayed(run, 2000);
//				}
//			} else {
//				finish();
//			}
//			return true;
//		}
//		return super.onKeyDown(keyCode, event); // 最后，一定要做完以后返回
//		// true，或者在弹出菜单后返回true，其他键返回super，让其他键默认
//	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("MessageboardScreen"); 
	}

	private void onLoad() {
		mWeiboListView.showFootView();
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
				new RoomTask(context, new IRoomBack() {
					@Override
					public void doPost(List<Map<String, Object>> result, long max, long min, MessageBoards msg) {
						if (null != result) {
							if(!TextUtils.isEmpty(mSenderId)){
								refreshListForShield(mSenderId, mType+"");
							}
							if (max > 0) {
								MessageBoardFragment.this.max = max;
							}
							if (!result.isEmpty()) {
								switch (mType) {
								case 1:
									RenheApplication.renheApplication.enterRoomTime = System.currentTimeMillis();
									break;
								case 2:
									RenheApplication.renheApplication.enterFriendTime = System.currentTimeMillis();
									break;
								case 3:
									RenheApplication.renheApplication.enterColleagueTime = System.currentTimeMillis();
									break;
								case 4:
									RenheApplication.renheApplication.enterCityTime = System.currentTimeMillis();
									break;
								case 5:
									RenheApplication.renheApplication.enterFollowTime = System.currentTimeMillis();
									break;
								}

								ToastUtil.showToast(context, "加载了" + result.size() + "条新留言!");
								if (null == messageBoardsList) {
									messageBoardsList = new ArrayList<MessageBoards>();
								}
								messageBoardsList.add(0, msg);
								if (msg.isAboveMaxCount()) {//Boolean 是否已经超过最大返回的数量(在请求的参数type为new时有效，若为true，则需要客户端清空本地之前的缓存数据，同时更新maxCreatedDate和minCreatedDate
									mWeiboList.clear();
									CacheManager.getInstance().populateData(context)
											.deleteObject(RenheApplication.getInstance().getUserInfo().getEmail(), mType + "");
								} else {
									refreshCacheAndListview(result, "new");
								}
								CacheManager
										.getInstance()
										.populateData(context)
										.saveObject(messageBoardsList, RenheApplication.getInstance().getUserInfo().getEmail(),
												mType + "");
								mWeiboList.addAll(0, result);
								mAdapter.notifyDataSetChanged();
							} else {
								//								ToastUtil.showToast(context, "暂无新留言!");
							}
							
						} else {
							ToastUtil.showNetworkError(context);
						}
						msg = null;
						onLoad();
					}

					@Override
					public void onPre() {
					}
				}).execute(RenheApplication.getInstance().getUserInfo().getAdSId(), RenheApplication.getInstance().getUserInfo().getSid(),
						mViewSid, mType, "new", 20, null, MessageBoardFragment.this.max);

			}
		}, 2000);
	}

	@Override
	public void onLoadMore() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {

				new RoomTask(context, new IRoomBack() {
					@Override
					public void doPost(List<Map<String, Object>> result, long max, long mMin, MessageBoards msg) {
						if (null != result) {
							if (!result.isEmpty()) {
								if (min > 0) {
									MessageBoardFragment.this.min = mMin;
								}
								if (null == messageBoardsList) {
									messageBoardsList = new ArrayList<MessageBoards>();
								}
								messageBoardsList.add(msg);
								CacheManager
										.getInstance()
										.populateData(context)
										.saveObject(messageBoardsList, RenheApplication.getInstance().getUserInfo().getEmail(),
												mType + "");
								mWeiboList.addAll(result);
								mAdapter.notifyDataSetChanged();
							} else {
								//								mFooterView.setVisibility(View.GONE);
							}
						} else {
							ToastUtil.showNetworkError(context);
						}
						msg = null;
						onLoad();
					}

					@Override
					public void onPre() {
					}
				}).execute(RenheApplication.getInstance().getUserInfo().getAdSId(), RenheApplication.getInstance().getUserInfo().getSid(),
						mViewSid, mType, "more", 20, MessageBoardFragment.this.min, null);

			}
		}, 2000);
	}

	public void updateView(int itemIndex, int type, int num, boolean isFavour) {
		//得到第一个可显示控件的位置，
		int visiblePosition = mWeiboListView.getFirstVisiblePosition();
		//只有当要更新的view在可见的位置时才更新，不可见时，跳过不更新
		if (itemIndex - visiblePosition >= 0) {
			//得到要更新的item的view
			View convertView = mWeiboListView.getChildAt(itemIndex - visiblePosition + 1);//自定义的listview有header会算作listview的子itemview，故加1
			//从view中取得holder
			if (null != convertView) {
				Map<String, Object> map = mWeiboList.get(itemIndex);
				ViewHolder viewHolder = (ViewHolder) convertView.getTag();
				viewHolder.contentTv = (TextViewFixTouchConsume) convertView.findViewById(R.id.content_txt);
				viewHolder.rawContentTv = (TextViewFixTouchConsume) convertView.findViewById(R.id.rawcontent_txt);
				viewHolder.nameTv = (TextView) convertView.findViewById(R.id.username_txt);
				viewHolder.avatarIv = (ImageView) convertView.findViewById(R.id.avatar_img);
				viewHolder.dateTv = (TextView) convertView.findViewById(R.id.datetime_txt);
				viewHolder.fromTv = (TextView) convertView.findViewById(R.id.client_txt);
				viewHolder.thumbnailPic = (ImageView) convertView.findViewById(R.id.thumbnailPic);
				viewHolder.forwardThumbnailPic = (ImageView) convertView.findViewById(R.id.forwardThumbnailPic);
				viewHolder.forwardLl = (LinearLayout) convertView.findViewById(R.id.room_item_reforward_ll);
				viewHolder.replyLl = (LinearLayout) convertView.findViewById(R.id.room_item_reply_ll);
				viewHolder.goodLl = (LinearLayout) convertView.findViewById(R.id.room_item_good_ll);
				viewHolder.goodButton = (Button) convertView.findViewById(R.id.room_item_good);
				viewHolder.replyButton = (Button) convertView.findViewById(R.id.room_item_reply);
				viewHolder.rawcontentlayout = (LinearLayout) convertView.findViewById(R.id.rawcontentlayout);
				viewHolder.mCompanyTv = (TextView) convertView.findViewById(R.id.companyTv);
				viewHolder.mIndustryTv = (TextView) convertView.findViewById(R.id.industryTv);
				viewHolder.vipIv = (ImageView) convertView.findViewById(R.id.vipImage);
				viewHolder.realNameIv = (ImageView) convertView.findViewById(R.id.realnameImage);
				viewHolder.goodIv = (ImageView) convertView.findViewById(R.id.goodiv);
				viewHolder.arrowIv = (ImageView) convertView.findViewById(R.id.arrow_iv);
				if (type == 1) {//评论
					if (num <= 0) {
						viewHolder.replyButton.setText("评论");
					} else {
						viewHolder.replyButton.setText(num + "");
					}
				} else if (type == 2) {//赞
					if (num <= 0) {
						//						viewHolder.goodButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.good_p), null, null, null);
						viewHolder.goodIv.setImageResource(R.drawable.good_p);
						viewHolder.goodButton.setTextColor(getResources().getColor(R.color.blog_item_date_text));
						viewHolder.goodButton.setText("赞");
					} else {
						if (!isFavour) {
							viewHolder.goodButton.setText(num + "");
							//							viewHolder.goodButton.setCompoundDrawablesWithIntrinsicBounds(
							//									getResources().getDrawable(R.drawable.good_p), null, null, null);
							viewHolder.goodIv.setImageResource(R.drawable.good_p);
							viewHolder.goodButton.setTextColor(getResources().getColor(R.color.blog_item_date_text));
						} else {
							viewHolder.goodButton.setText(num + "");
							//							viewHolder.goodButton.setCompoundDrawablesWithIntrinsicBounds(
							//									getResources().getDrawable(R.drawable.good), null, null, null);
							viewHolder.goodIv.setImageResource(R.drawable.good);

							viewHolder.goodButton.setTextColor(getResources().getColor(R.color.room_good_textcolor));
						}
					}
				}
			}
		}
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
