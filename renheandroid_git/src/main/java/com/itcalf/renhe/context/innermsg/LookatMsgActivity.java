package com.itcalf.renhe.context.innermsg;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.itcalf.renhe.Constants;
import com.itcalf.renhe.R;
import com.itcalf.renhe.RenheApplication;
import com.itcalf.renhe.cache.CacheManager;
import com.itcalf.renhe.context.archives.MyHomeArchivesActivity;
import com.itcalf.renhe.context.fragmentMain.MeunFragment;
import com.itcalf.renhe.context.template.ActivityTemplate;
import com.itcalf.renhe.dto.InBoxInfo;
import com.itcalf.renhe.dto.MessageBoardOperation;
import com.itcalf.renhe.dto.MsgInfo;
import com.itcalf.renhe.dto.ReceiveAddFriend;
import com.itcalf.renhe.utils.HttpUtil;
import com.itcalf.renhe.utils.ToastUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

public class LookatMsgActivity extends SwipeBackActivity {
	private Button mReplyBt;
	private Button mWriteBt;
	private Button mDelBt;

	private MsgInfo mMsgInfo;

	private ImageView mAvatarImage;
	private TextView mNameTv;
	private TextView mJobTv;
	private TextView mTitleInfoTv;
	private TextView mTimeTv;
	private TextView mContentTv;

	private ImageView mArrayRightImage;

	private Integer mMsgType = 0;

	private RelativeLayout relativeLayout;

	private LinearLayout mBottomLayout;
	private Button mAgreeBt;
	private Button mLaterAgreeBt;
	private Button mNoAgreeBt;
	private TextView mAgreeTv;

	//站内信回复时需要使用的文本
	private String mContentString;
	private SharedPreferences msp;
	private Editor editor;
	private List<Map<String, Object>> mInBoxInfos;//站内信缓存
	private List<InBoxInfo> inBoxInfos;

	private ImageView mVipImage;
	private ImageView mRealNameImage;
	private int accountType;//账号vip等级类型：0：普通会员；1：VIP会员；2：黄金会员；3：铂金会员
	private boolean isRealName;//是否是实名认证的会员
	private TextView jobTv;
	private TextView industryTv;
	private boolean isReplyVisible = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		new ActivityTemplate().doInActivity(this, R.layout.innermsg_lookat);

	}

	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem delete = menu.findItem(R.id.item_delete);
		delete.setVisible(true);
		delete.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		delete.setTitle("删除");
		if (isReplyVisible && mMsgType == 1) {
			MenuItem reply = menu.findItem(R.id.item_reply);
			reply.setVisible(true);
			reply.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
			reply.setTitle("回复");
		}
		MenuItem edit = menu.findItem(R.id.item_edit);
		edit.setVisible(true);
		edit.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		edit.setTitle("写站内信");
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.item_delete:
			mDelBt.performClick();
			return true;
		case R.id.item_reply:
			mReplyBt.performClick();
			return true;
		case R.id.item_edit:
			mWriteBt.performClick();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void findView() {
		super.findView();
		setTextValue(R.id.title_txt, "站内信正文");
		mReplyBt = (Button) findViewById(R.id.replyBt);
		mWriteBt = (Button) findViewById(R.id.writeBt);
		mAvatarImage = (ImageView) findViewById(R.id.avatarImage);
		mNameTv = (TextView) findViewById(R.id.nameTv);
		mJobTv = (TextView) findViewById(R.id.jobTv);
		mTitleInfoTv = (TextView) findViewById(R.id.titleInfoTv);
		mTimeTv = (TextView) findViewById(R.id.timeTv);
		mContentTv = (TextView) findViewById(R.id.contentTv);

		mDelBt = (Button) findViewById(R.id.delBt);
		mArrayRightImage = (ImageView) findViewById(R.id.arrayRightImage);
		relativeLayout = (RelativeLayout) findViewById(R.id.layout1);

		mBottomLayout = (LinearLayout) findViewById(R.id.bottomlayout);
		mAgreeTv = (TextView) findViewById(R.id.agreeTv);

		mAgreeBt = (Button) findViewById(R.id.agreeBt);
		mNoAgreeBt = (Button) findViewById(R.id.noagreeBt);
		mLaterAgreeBt = (Button) findViewById(R.id.lateragreeBt);

		mVipImage = (ImageView) findViewById(R.id.vipImage);
		mRealNameImage = (ImageView) findViewById(R.id.realnameImage);

		industryTv = (TextView) findViewById(R.id.industryTv);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void initData() {
		super.initData();
		msp = getSharedPreferences("setting_info", 0);
		editor = msp.edit();
		mInBoxInfos = CacheManager.getInstance().loadInboxCache(Constants.DbTable.INBOX);
		String messageObjectId = getIntent().getStringExtra("messageObjectId");
		String msid = getIntent().getStringExtra("msid");
		mMsgType = getIntent().getIntExtra("type", 1);
		new MsgInfoTask().execute(msid, messageObjectId);
		//通知站内信列表刷新数据，将本次点击的未读邮件置为已读
		Intent intent = new Intent(InnerMsgListActivity.REFRESH_CACHE);
		sendBroadcast(intent);
	}

	@Override
	protected void initListener() {
		super.initListener();
		mAgreeBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mMsgInfo != null && mMsgInfo.getMessageInfo() != null
						&& mMsgInfo.getMessageInfo().getInviteXMessageInfo() != null) {

					new ReceiveFriend(LookatMsgActivity.this) {

						@Override
						public void doPre() {
							showDialog(3);
						}

						@Override
						public void doPost(ReceiveAddFriend result) {
							removeDialog(3);
							if (result == null) {
								ToastUtil.showErrorToast(LookatMsgActivity.this, "连接服务器失败！");
							} else if (result.getState() == 1) {
								if (mMsgInfo.getMessageInfo().getInviteType() == 2
										&& mMsgInfo.getMessageInfo().getInviteXMessageInfo().getInviteState() == 0) {
									ToastUtil.showToast(LookatMsgActivity.this, "引荐成功");
								} else {
									ToastUtil.showToast(LookatMsgActivity.this, "接受好友邀请成功");
								}

								finish();
							} else if (result.getState() == -1) {
								ToastUtil.showErrorToast(LookatMsgActivity.this, "权限不足！");
							} else if (result.getState() == -2) {
								ToastUtil.showErrorToast(LookatMsgActivity.this, "发生未知错误！");
							} else if (result.getState() == -3) {
								ToastUtil.showErrorToast(LookatMsgActivity.this, "邀请序号不存在！");
							} else if (result.getState() == -4) {
								ToastUtil.showErrorToast(LookatMsgActivity.this, "邀请类型不存在！");
							} else if (result.getState() == -5) {
								ToastUtil.showErrorToast(LookatMsgActivity.this, "接受类型不存在！");
							} else if (result.getState() == -6) {
								ToastUtil.showErrorToast(LookatMsgActivity.this, "您无权进行此操作！");
							} else if (result.getState() == -7) {
								ToastUtil.showErrorToast(LookatMsgActivity.this, "您已经通过该请求了！");
							} else if (result.getState() == -8) {
								ToastUtil.showErrorToast(LookatMsgActivity.this, "您已经拒绝过该请求！");
							}
						}
					}.execute(mMsgInfo.getMessageInfo().getInviteXMessageInfo().getInviteId() + "", mMsgInfo.getMessageInfo()
							.getInviteType() + "", "true");

				}
			}
		});
		mNoAgreeBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mMsgInfo != null && mMsgInfo.getMessageInfo() != null
						&& mMsgInfo.getMessageInfo().getInviteXMessageInfo() != null) {

					new ReceiveFriend(LookatMsgActivity.this) {

						@Override
						public void doPre() {
							showDialog(3);
						}

						@Override
						public void doPost(ReceiveAddFriend result) {
							removeDialog(3);
							if (result == null) {
								ToastUtil.showErrorToast(LookatMsgActivity.this, "连接服务器失败！");
							} else if (result.getState() == 1) {
								ToastUtil.showToast(LookatMsgActivity.this, "请求成功");
							} else if (result.getState() == -1) {
								ToastUtil.showErrorToast(LookatMsgActivity.this, "权限不足！");
							} else if (result.getState() == -2) {
								ToastUtil.showErrorToast(LookatMsgActivity.this, "发生未知错误！");
							} else if (result.getState() == -3) {
								ToastUtil.showErrorToast(LookatMsgActivity.this, "您已经通过该请求了！");
							} else if (result.getState() == -4) {
								ToastUtil.showErrorToast(LookatMsgActivity.this, "您已经拒绝过该请求了！");
							}
						}
					}.execute(mMsgInfo.getMessageInfo().getInviteXMessageInfo().getInviteId() + "", mMsgInfo.getMessageInfo()
							.getInviteType() + "", "false");

				}
			}
		});
		mLaterAgreeBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		relativeLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mMsgInfo.getUserInfo() != null && mMsgInfo.getUserInfo().isShowLink()) {
					Intent intent = new Intent(LookatMsgActivity.this, MyHomeArchivesActivity.class);
					intent.putExtra(MyHomeArchivesActivity.FLAG_INTENT_DATA, mMsgInfo.getUserInfo().getSid());
					startActivity(intent);
				}
			}
		});

		mContentTv.setMovementMethod(LinkMovementMethod.getInstance());

		mReplyBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putSerializable("msgInfo", mMsgInfo);
				bundle.putString("ContentString", mContentString);
				startActivity(InnerMsgReplyActivity.class, bundle);
			}
		});

		mWriteBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mMsgInfo != null && mMsgInfo.getUserInfo() != null) {
					// Bundle bundle = new Bundle();
					// bundle.putString("sid", mMsgInfo.getUserInfo().getSid());
					// bundle.putString("name",
					// mMsgInfo.getUserInfo().getName());
					startActivity(SendInnerMsgActivity.class);
				}
			}
		});

		mDelBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mMsgInfo != null) {
					new AlertDialog.Builder(LookatMsgActivity.this).setIcon(android.R.drawable.ic_dialog_alert)
							.setCancelable(true).setTitle("操作").setMessage("确认删除？")
							.setNegativeButton("取消", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
								}
							}).setPositiveButton("确认", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									new DeleteMsgTask().execute(mMsgInfo.getMessageInfo().getMsid(), mMsgInfo.getMessageInfo()
											.getMessageObjectId());
								}
							}).create().show();
				}
			}
		});
	}

	class MsgInfoTask extends AsyncTask<String, Void, MsgInfo> {
		String msgObjectId;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			try {
				mBottomLayout.setVisibility(View.GONE);
				mNoAgreeBt.setVisibility(View.GONE);
				mAgreeTv.setVisibility(View.GONE);
				mArrayRightImage.setVisibility(View.VISIBLE);
				mContentString = null;
				showDialog(1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		protected MsgInfo doInBackground(String... params) {
			Map<String, Object> reqParams = new HashMap<String, Object>();
			reqParams.put("sid", getRenheApplication().getUserInfo().getSid());
			reqParams.put("msid", params[0]);
			reqParams.put("messageObjectId", params[1]);
			reqParams.put("adSId", getRenheApplication().getUserInfo().getAdSId());
			msgObjectId = params[1];
			try {
				MsgInfo mb = (MsgInfo) HttpUtil.doHttpRequest(Constants.Http.INNERMSG_MSGINFO, reqParams, MsgInfo.class,
						LookatMsgActivity.this);
				return mb;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@SuppressLint("SimpleDateFormat")
		@Override
		protected void onPostExecute(MsgInfo result) {
			super.onPostExecute(result);
			try {
				removeDialog(1);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (result != null && result.getState() == 1) {
				if (mMsgType == 1) {
					if (null != mInBoxInfos && mInBoxInfos.size() > 0) {
						for (Map<String, Object> mResult : mInBoxInfos) {
							if (mResult.get("messageObjectId").equals(msgObjectId)) {

								if (mResult.get("read") != null && (Integer) (mResult.get("read")) == 0) {
									int currentNum = msp.getInt("newmsg_unreadmsg_num", 1);
									if (currentNum < 1) {
										currentNum = 1;
									}
									editor.putInt("newmsg_unreadmsg_num", currentNum - 1);
									Intent intent2 = new Intent(MeunFragment.NEWMSG_ICON_ACTION);
									intent2.putExtra("newmsg_notice_num", currentNum - 1);
									sendBroadcast(intent2);
									editor.commit();
									Intent intent = new Intent(InnerMsgListActivity.REFRESH_CACHE);
									intent.putExtra("messageObjectid", msgObjectId);
									sendBroadcast(intent);
								}
								break;
							}
						}
					}
				}

				mAvatarImage.setImageResource(R.drawable.avatar);
				mMsgInfo = result;
				//				AsyncImageLoader
				//						.getInstance()
				//						.populateData(LookatMsgActivity.this,
				//								getRenheApplication().getUserInfo().getEmail(),
				//								false, true,false)
				//						.loadPic(mAvatarImage,mMsgInfo.getUserInfo().getSid(),
				//								mMsgInfo.getUserInfo().getUserface(), (int)getResources().getDimension(R.dimen.renhe_hall_bottom_image_wh), (int)getResources().getDimension(R.dimen.renhe_hall_bottom_image_wh));
				ImageLoader imageLoader = ImageLoader.getInstance();
				try {
					imageLoader.displayImage(mMsgInfo.getUserInfo().getUserface(), mAvatarImage, CacheManager.options,
							CacheManager.animateFirstDisplayListener);
				} catch (Exception e) {
					e.printStackTrace();
				}

				mNameTv.setText(mMsgInfo.getUserInfo().getName());
				mTitleInfoTv.setText(mMsgInfo.getMessageInfo().getSubject());
				mJobTv.setText(mMsgInfo.getUserInfo().getCompany());
				if (null != mMsgInfo.getUserInfo().getTitle()) {
					mJobTv.setText(mMsgInfo.getUserInfo().getTitle() + " ");
				}
				if (null != mMsgInfo.getUserInfo().getCompany()) {
					mJobTv.setText(mJobTv.getText().toString() + mMsgInfo.getUserInfo().getCompany());
				}
				if (null != mMsgInfo.getUserInfo().getLocation()) {
					industryTv.setText(mMsgInfo.getUserInfo().getLocation() + " ");
				}
				if (null != mMsgInfo.getUserInfo().getIndustry()) {
					industryTv.setText(industryTv.getText().toString() + mMsgInfo.getUserInfo().getIndustry());
				}
				//vip、实名认证
				accountType = mMsgInfo.getUserInfo().getAccountType();
				isRealName = mMsgInfo.getUserInfo().isRealname();
				switch (accountType) {
				case 0:
					mVipImage.setVisibility(View.GONE);
					break;
				case 1:
					mVipImage.setVisibility(View.VISIBLE);
					mVipImage.setImageResource(R.drawable.vip_1);
					break;
				case 2:
					mVipImage.setVisibility(View.VISIBLE);
					mVipImage.setImageResource(R.drawable.vip_2);
					break;
				case 3:
					mVipImage.setVisibility(View.VISIBLE);
					mVipImage.setImageResource(R.drawable.vip_3);
					break;

				default:
					break;
				}
				if (isRealName && accountType <= 0) {
					mRealNameImage.setVisibility(View.VISIBLE);
					mRealNameImage.setImageResource(R.drawable.realname);
				} else {
					mRealNameImage.setVisibility(View.GONE);
				}
				// 是否是邀请类的站内信，如果是 需要特殊处理
				if (mMsgInfo.getMessageInfo().isInviteType()) {
					// 需拼接显示的HTML好友请求数据
					// 处理直接好友请求
					if (mMsgInfo.getMessageInfo().getInviteType() == 1
							&& mMsgInfo.getMessageInfo().getInviteXMessageInfo().getInviteState() == 0) {
						mNoAgreeBt.setVisibility(View.GONE);
						mAgreeBt.setText("同意邀请");
						int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 145, getResources()
								.getDisplayMetrics());

						mAgreeBt.getLayoutParams().width = px;
						mLaterAgreeBt.getLayoutParams().width = px;
						mBottomLayout.setVisibility(View.VISIBLE);
						mContentString = beanToHtml(mMsgInfo.getMessageInfo().getInviteXMessageInfo(), 0);
						URLImageParser p = new URLImageParser(mContentTv, LookatMsgActivity.this);
						Spanned htmlSpan = Html.fromHtml(mContentString, p, null);
						mContentTv.setText(htmlSpan);

						//						mContentTv.setText(Html.fromHtml(mContentString));
					}
					// 直接好友请求处理结果
					if (mMsgInfo.getMessageInfo().getInviteType() == 1
							&& mMsgInfo.getMessageInfo().getInviteXMessageInfo().getInviteState() != 0) {
						mAgreeTv.setVisibility(View.VISIBLE);
						switch (mMsgInfo.getMessageInfo().getInviteXMessageInfo().getInviteState()) {
						case 1:
							mAgreeTv.setText(Html.fromHtml("您已经同意了"
									+ mMsgInfo.getMessageInfo().getInviteXMessageInfo().getFromMemberName() + "的加好友请求"));
							break;
						case 2:
							mAgreeTv.setText(Html.fromHtml("您已经拒绝了"
									+ mMsgInfo.getMessageInfo().getInviteXMessageInfo().getFromMemberName() + "的加好友请求"));
							break;
						default:
							break;
						}
						mContentString = beanToHtml(mMsgInfo.getMessageInfo().getInviteXMessageInfo(), 0);
						URLImageParser p = new URLImageParser(mContentTv, LookatMsgActivity.this);
						Spanned htmlSpan = Html.fromHtml(mContentString, p, null);
						mContentTv.setText(htmlSpan);
					}
					// 处理引荐
					if (mMsgInfo.getMessageInfo().getInviteType() == 2
							&& mMsgInfo.getMessageInfo().getInviteXMessageInfo().getInviteState() == 0) {
						mAgreeBt.setText("同意引荐");
						int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 145, getResources()
								.getDisplayMetrics());
						mAgreeBt.getLayoutParams().width = px;
						mLaterAgreeBt.getLayoutParams().width = px;
						mBottomLayout.setVisibility(View.VISIBLE);
						mContentString = beanToHtml(mMsgInfo.getMessageInfo().getInviteXMessageInfo(), 1);
						URLImageParser p = new URLImageParser(mContentTv, LookatMsgActivity.this);
						Spanned htmlSpan = Html.fromHtml(mContentString, p, null);
						mContentTv.setText(htmlSpan);
					}
					// 引荐结果显示
					if (mMsgInfo.getMessageInfo().getInviteType() == 2
							&& mMsgInfo.getMessageInfo().getInviteXMessageInfo().getInviteState() != 0) {
						mAgreeTv.setVisibility(View.VISIBLE);
						switch (mMsgInfo.getMessageInfo().getInviteXMessageInfo().getInviteState()) {
						case 1:
							mAgreeTv.setText(Html.fromHtml("您已经同意引荐"
									+ mMsgInfo.getMessageInfo().getInviteXMessageInfo().getFromMemberName() + "认识"
									+ mMsgInfo.getMessageInfo().getInviteXMessageInfo().getToMemberName()));
							break;
						case 2:
							mAgreeTv.setText(Html.fromHtml("您已经拒绝引荐"
									+ mMsgInfo.getMessageInfo().getInviteXMessageInfo().getFromMemberName() + "认识"
									+ mMsgInfo.getMessageInfo().getInviteXMessageInfo().getToMemberName()));
							break;
						default:
							break;
						}
						mContentString = beanToHtml(mMsgInfo.getMessageInfo().getInviteXMessageInfo(), 1);
						URLImageParser p = new URLImageParser(mContentTv, LookatMsgActivity.this);
						Spanned htmlSpan = Html.fromHtml(mContentString, p, null);
						mContentTv.setText(htmlSpan);
					}

					// 引荐来的好友请求
					if (mMsgInfo.getMessageInfo().getInviteType() == 3
							&& mMsgInfo.getMessageInfo().getInviteXMessageInfo().getInviteState() == 0) {
						mNoAgreeBt.setVisibility(View.GONE);
						mAgreeBt.setText("同意邀请");
						int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 145, getResources()
								.getDisplayMetrics());
						mAgreeBt.getLayoutParams().width = px;
						mLaterAgreeBt.getLayoutParams().width = px;
						mBottomLayout.setVisibility(View.VISIBLE);
						mContentString = beanToHtml(mMsgInfo.getMessageInfo().getInviteXMessageInfo(), 2);
						URLImageParser p = new URLImageParser(mContentTv, LookatMsgActivity.this);
						Spanned htmlSpan = Html.fromHtml(mContentString, p, null);
						mContentTv.setText(htmlSpan);
					}
					// 引荐来的好友请求处理结果
					if (mMsgInfo.getMessageInfo().getInviteType() == 3
							&& mMsgInfo.getMessageInfo().getInviteXMessageInfo().getInviteState() != 0) {
						mAgreeTv.setVisibility(View.VISIBLE);
						switch (mMsgInfo.getMessageInfo().getInviteXMessageInfo().getInviteState()) {
						case 1:
							mAgreeTv.setText(Html.fromHtml("您已经同意了"
									+ mMsgInfo.getMessageInfo().getInviteXMessageInfo().getFromMemberName() + "的加好友请求"));
							break;
						case 2:
							mAgreeTv.setText(Html.fromHtml("您已经拒绝了"
									+ mMsgInfo.getMessageInfo().getInviteXMessageInfo().getFromMemberName() + "的加好友请求"));
							break;
						default:
							break;
						}
						mContentString = beanToHtml(mMsgInfo.getMessageInfo().getInviteXMessageInfo(), 2);
						URLImageParser p = new URLImageParser(mContentTv, LookatMsgActivity.this);
						Spanned htmlSpan = Html.fromHtml(mContentString, p, null);
						mContentTv.setText(htmlSpan);
					}
				} else {
					mContentString = mMsgInfo.getMessageInfo().getContent();
					URLImageParser p = new URLImageParser(mContentTv, LookatMsgActivity.this);
					Spanned htmlSpan = Html.fromHtml(mContentString, p, null);
					mContentTv.setText(htmlSpan);
				}
				mContentTv.setMovementMethod(LinkMovementMethod.getInstance());
				//				mTimeTv.setText(mMsgInfo.getMessageInfo().getCreatedDate());
				String time = mMsgInfo.getMessageInfo().getCreatedDate();
				DateFormat format1 = new SimpleDateFormat("MM-dd HH:mm");
				try {
					Date date = format1.parse(time);
					SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.readable_date_md_hm));
					time = dateFormat.format(date);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				mTimeTv.setText(time);

				if (mMsgType != 1) {// 发送详情
					//					mReplyBt.setVisibility(View.GONE);
					isReplyVisible = false;
					getWindow().invalidatePanelMenu(Window.FEATURE_OPTIONS_PANEL);
					// mNameTv.setText("发送至："+mMsgInfo.getUserInfo().getName());
				} else {// 收件详情
					if (!mMsgInfo.isShowReply()) {// 人和网详情
						mArrayRightImage.setVisibility(View.GONE);
						//						mReplyBt.setVisibility(View.GONE);
						isReplyVisible = false;
						getWindow().invalidatePanelMenu(Window.FEATURE_OPTIONS_PANEL);
						// mWriteBt.setVisibility(View.GONE);
					} else {
						mReplyBt.setVisibility(View.VISIBLE);
						mWriteBt.setVisibility(View.VISIBLE);
						isReplyVisible = true;
						getWindow().invalidatePanelMenu(Window.FEATURE_OPTIONS_PANEL);
					}
				}

				CharSequence text = mContentTv.getText();
				//				if (text instanceof Spannable) {
				//					int end = text.length();
				//					Spannable sp = (Spannable) mContentTv.getText();
				//					URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
				//					SpannableStringBuilder style = new SpannableStringBuilder(
				//							text);
				//					style.clearSpans();// should clear old spans
				//					for (URLSpan url : urls) {
				//						MyURLSpan myURLSpan = new MyURLSpan(url.getURL(),
				//								LookatMsgActivity.this);
				//						style.setSpan(myURLSpan, sp.getSpanStart(url),
				//								sp.getSpanEnd(url),
				//								Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
				//					}
				//					mContentTv.setText(style);
				//				}
			} else {
				ToastUtil.showNetworkError(LookatMsgActivity.this);
			}
		}

	}

	/**
	 * 
	 * @param info
	 * @param type
	 *            0直接加好友类型，1引荐好友
	 * @return
	 */
	private String beanToHtml(MsgInfo.MessageInfo.InviteXMessageInfo info, int type) {
		StringBuffer sb = new StringBuffer();
		switch (type) {
		case 0:
			if (!TextUtils.isEmpty(info.getPurpose())) {
				sb.append("<P>添加好友目的：" + info.getPurpose() + "</P>");
			}
			if (!TextUtils.isEmpty(info.getFromContent())) {
				sb.append("<P>" + info.getFromContent() + "</P>");
			}
			if (!TextUtils.isEmpty(info.getFromMemberName())) {
				sb.append("<A href='http://www.renhe.cn/viewprofile.html?sid=" + info.getFromMemberSId() + "' target=_blank>"
						+ info.getFromMemberName() + "</A>:");
			}
			if (!TextUtils.isEmpty(info.getFromMemberTitle())) {
				sb.append(info.getFromMemberTitle());
			}
			if (!TextUtils.isEmpty(info.getFromMemberCompany())) {
				sb.append(info.getFromMemberCompany());
			}
			break;
		case 1:
			if (!TextUtils.isEmpty(info.getFromMemberName()) && !TextUtils.isEmpty(info.getToMemberName())) {
				sb.append("<P>您的朋友<A href='http://www.renhe.cn/viewprofile.html?sid=" + info.getFromMemberSId()
						+ "' target=_blank>" + info.getFromMemberName()
						+ "</A>希望通过您的引荐，添加<A href='http://www.renhe.cn/viewprofile.html?sid=" + info.getToMemberSId()
						+ "' target=_blank>" + info.getToMemberName() + "</A>为朋友</P>");
			}
			if (!TextUtils.isEmpty(info.getFromMemberName()) && !TextUtils.isEmpty(info.getRecommendFromContent())) {
				sb.append("<P>" + info.getFromMemberName() + "对您说： " + info.getRecommendFromContent() + "</P> ");
			}
			if (!TextUtils.isEmpty(info.getFromMemberName()) && !TextUtils.isEmpty(info.getToMemberName())
					&& !TextUtils.isEmpty(info.getFromContent())) {
				sb.append(info.getFromMemberName() + "对" + info.getToMemberName() + "说：" + info.getFromContent());
			}
			break;
		case 2:
			if (!TextUtils.isEmpty(info.getInviteContent()) && !TextUtils.isEmpty(info.getFromMemberName())
					&& !TextUtils.isEmpty(info.getToMemberName())) {
				String text = info.getInviteContent().replace(
						info.getFromMemberName(),
						"<A href='http://www.renhe.cn/viewprofile.html?sid=" + info.getFromMemberSId() + "' target=_blank>"
								+ info.getFromMemberName() + "</A>");
				sb.append("<P>" + text + "</p>");
			}

			if (!TextUtils.isEmpty(info.getFromContent()) && !TextUtils.isEmpty(info.getFromMemberName())) {
				sb.append("<P>" + info.getFromMemberName() + "对您说：</P> " + info.getFromContent());
			}
			break;
		default:
			break;
		}

		return sb.toString();
	}

	class DeleteMsgTask extends AsyncTask<String, Void, MessageBoardOperation> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showDialog(2);
		}

		@Override
		protected MessageBoardOperation doInBackground(String... params) {
			Map<String, Object> reqParams = new HashMap<String, Object>();
			reqParams.put("msid", params[0]);
			reqParams.put("messageObjectId", params[1]);
			reqParams.put("sid", getRenheApplication().getUserInfo().getSid());
			reqParams.put("adSId", getRenheApplication().getUserInfo().getAdSId());
			try {
				MessageBoardOperation mb = (MessageBoardOperation) HttpUtil.doHttpRequest(Constants.Http.INNERMSG_DELETEMSG,
						reqParams, MessageBoardOperation.class, LookatMsgActivity.this);
				if (mb != null && mb.getState() == 1) {
					RenheApplication.INNER_DLETE_MSG.add(params[0] + "#" + params[1]);
				}
				return mb;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(MessageBoardOperation result) {
			super.onPostExecute(result);
			removeDialog(2);
			if (result != null && result.getState() == 1) {
				ToastUtil.showToast(LookatMsgActivity.this, "站内信删除成功");
				LookatMsgActivity.this.finish();
			} else {
				ToastUtil.showErrorToast(LookatMsgActivity.this, "站内信删除失败");
			}
		}

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 1:
			ProgressDialog findPd = new ProgressDialog(this);
			//			findPd.setTitle("正在读取站内信");
			findPd.setMessage("正在读取站内信...");
			findPd.setCanceledOnTouchOutside(false);
			//			findPd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			return findPd;
		case 2:
			ProgressDialog deletePd = new ProgressDialog(this);
			//			deletePd.setTitle("正在删除站内信");
			deletePd.setMessage("正在删除站内信...");
			deletePd.setCanceledOnTouchOutside(false);
			//			deletePd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			return deletePd;
		case 3:
			ProgressDialog friendPd = new ProgressDialog(this);
			//			friendPd.setTitle("处理好友中");
			friendPd.setMessage("处理好友中...");
			friendPd.setCanceledOnTouchOutside(false);
			//			friendPd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			return friendPd;
		default:
			return null;
		}
	}

	@Override
	public void finish() {
		super.finish();
		setResult(RESULT_OK);
	}
}
