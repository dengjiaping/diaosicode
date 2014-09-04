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
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itcalf.renhe.R;
import com.itcalf.renhe.RenheApplication;
import com.itcalf.renhe.adapter.ReplyListAdapter;
import com.itcalf.renhe.cache.CacheManager;
import com.itcalf.renhe.context.archives.MyHomeArchivesActivity;
import com.itcalf.renhe.context.room.ReplyListTask.IDataBack;
import com.itcalf.renhe.context.template.BaseActivity;
import com.itcalf.renhe.dto.MessageBoardDetail;
import com.itcalf.renhe.dto.MessageBoardDetail.MessageBoardInfo;
import com.itcalf.renhe.dto.MessageBoardDetail.MessageBoardInfo.ForwardMessageBoardInfo;
import com.itcalf.renhe.dto.MessageBoardDetail.SenderInfo;
import com.itcalf.renhe.dto.MessageBoardOperation;
import com.itcalf.renhe.dto.MessageBoards.MessageBoardList.MessageBoardMember;
import com.itcalf.renhe.imageUtil.StandardImageXML;
import com.itcalf.renhe.utils.AsyncImageLoader;
import com.itcalf.renhe.utils.MatrixUtil;
import com.itcalf.renhe.utils.NetworkUtil;
import com.itcalf.renhe.utils.ToastUtil;
import com.itcalf.renhe.utils.TransferUrl2Drawable;
import com.itcalf.renhe.view.TextViewFixTouchConsume;
import com.itcalf.widget.scrollview.ScrollViewX;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Feature: 显示留言内容界面 Desc:留言内容界面
 * 
 * @author xp
 * 
 */
public class TwitterShowMessageBoardActivity extends BaseActivity {
	RelativeLayout topLayout;
	// 头像
	private ImageView mAvatarImage;
	// 名称
	private TextView mNameTv;
	// 公司
	private TextView mCompanyTv;
	// 行业
	private TextView mIndustryTv;
	// 分割线
	private View mLayout1;
	// 留言内容
	private TextView mContentTv;
	// 留言原文
	private TextView mRawcontentTv;
	// 来源
	private TextView mSourceTv;
	// 时间
	private TextView mTimeTv;
	// 回复按钮
	private LinearLayout mReplyLl;
	// 转发按钮
	private LinearLayout mForwordLl;
	private LinearLayout mGoodLl;
	private Button mReplyBt;
	private Button mForwordBt;
	private Button mGoodBt;
//	private View mLayoutView;
	// 发送人
	private TextView mSenderTv;

	private String mObjectId;
	private String mId;
	private String mSid;

	private ImageView mThumbnailPic;
	private ImageView mForwardThumbnailPic;
	private View mRawcontentlayout;
	private String mImageUrl;// 缩略图地址
	private String mForwardImageUrl;// 转发缩略图地址

	private boolean isLiked = false;
	private int likeNumber = 0;
	private ScrollViewX scrollView;
	int[] location = new int[2];
	private RelativeLayout blankRel;
	private TextView blankSignTv;
	/**
	 * 留言列表
	 */

	// 列表视图
	private ListView mListView;
	// 列表头部
	private View mHeaderView;
	// 列表底部
	private RelativeLayout mFooterView;
	private RelativeLayout mFooterViewMore;
	private RelativeLayout mFooterViewIng;
	private LinearLayout replyListLl;
	// 列表数据
	private List<Map<String, Object>> mData;
	private ReplyListAdapter mSimpleAdapter;
	private String[] mFrom = new String[] { "titleTv", "infoTv", "timeTv" };
	private int[] mTo = new int[] { R.id.titleTv, R.id.infoTv, R.id.timeTv };
	private int mStart;
	private int mCount = 20;
	private Handler handler = new Handler();
	private int replyNumber = 0;
	private int msgRepleyNumber;
	private int msgLikeNumber;
	private boolean isToRepleylist = false;
	private boolean isFromNoticeList = false;
	private TransferUrl2Drawable transferUrl;
	private LinearLayout btLl;
	private RelativeLayout nowifiLayout;
	
	private ImageView mVipImage;
	private ImageView mRealNameImage;
	private int accountType;//账号vip等级类型：0：普通会员；1：VIP会员；2：黄金会员；3：铂金会员
	private boolean isRealName;//是否是实名认证的会员
	
	private LinearLayout selfinfoLinearLayout;
	private TextView replyNumTv;
	private LinearLayout repleyNumLl;
	
	private CheckBox toForwardCb;
	private EditText replyEt;
	private TextView leftReplyNumTv;
	private ImageButton goReplyIb;
	private RelativeLayout rootRl;
	private LinearLayout bottomReplyLl;
	private static final int TOTAL_REPLY_NUMBER = 140;//评论最多输入140个字
	private LinearLayout shareLl;
	private String userFaceUrl;
	private String userCompany;
	private String userJob;
	private String userContent;
	private String userId;
	private String messageId;
	private ImageView goodIv;
	private AnimationDrawable animationDrawable;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getTemplate().doInActivity(this, R.layout.twitter_rooms_show_msg);
	}

	@Override
	protected void findView() {
		super.findView();
		topLayout = (RelativeLayout) findViewById(R.id.topLayout);
		mAvatarImage = (ImageView) findViewById(R.id.avatarImage);
		mNameTv = (TextView) findViewById(R.id.nameTv);
		mCompanyTv = (TextView) findViewById(R.id.companyTv);
		mIndustryTv = (TextView) findViewById(R.id.industryTv);
		mLayout1 = findViewById(R.id.layout1);
		mContentTv = (TextView) findViewById(R.id.contentTv);
		mRawcontentTv = (TextView) findViewById(R.id.rawcontentTv);
		mSourceTv = (TextView) findViewById(R.id.sourceTv);
		mTimeTv = (TextView) findViewById(R.id.timeTv);
		mReplyLl = (LinearLayout) findViewById(R.id.replyBt);
		mForwordLl = (LinearLayout) findViewById(R.id.forwordBt);
		mGoodLl = (LinearLayout) findViewById(R.id.foavourBt);
		mReplyBt = (Button) findViewById(R.id.room_item_reply);
		mForwordBt = (Button) findViewById(R.id.room_item_reforward);
		mGoodBt = (Button) findViewById(R.id.room_item_good);
//		mLayoutView = findViewById(R.id.layout);
		mSenderTv = ((TextView) findViewById(R.id.senderTxt));
		scrollView = (ScrollViewX) findViewById(R.id.room_showmsg_scrollview);
		mThumbnailPic = (ImageView) findViewById(R.id.thumbnailPic);
		mForwardThumbnailPic = (ImageView) findViewById(R.id.forwardThumbnailPic);
		mRawcontentlayout = findViewById(R.id.rawcontentlayout);

		//留言列表
		mListView = (ListView) findViewById(R.id.reply_listView);
		//		mFooterView = LayoutInflater.from(this).inflate(R.layout.room_footerview, null);
		mFooterView = (RelativeLayout) findViewById(R.id.footer_layout);
		mFooterViewMore = (RelativeLayout) findViewById(R.id.footer_layout_more);
		mFooterViewIng = (RelativeLayout) findViewById(R.id.footer_layout_ing);
		replyListLl = (LinearLayout) findViewById(R.id.reply_list_ll);
		blankRel = (RelativeLayout) findViewById(R.id.blank_rl);
		blankSignTv = (TextView)findViewById(R.id.balnk_rl_tv);
		btLl = (LinearLayout)findViewById(R.id.bt_ll);
		
		nowifiLayout = (RelativeLayout)findViewById(R.id.nowifi_rl);
		mVipImage = (ImageView)findViewById(R.id.vipImage);
		mRealNameImage = (ImageView)findViewById(R.id.realnameImage);
		
		selfinfoLinearLayout = (LinearLayout)findViewById(R.id.room_detail_selfinfo_ll);
		replyNumTv = (TextView)findViewById(R.id.repleyNumTv);
		repleyNumLl = (LinearLayout)findViewById(R.id.repleyNumLl);
		
		toForwardCb = (CheckBox)findViewById(R.id.forwardCk);
		replyEt = (EditText)findViewById(R.id.reply_edt);
		leftReplyNumTv = (TextView)findViewById(R.id.leftreply_num_tv);
		leftReplyNumTv.setText(TOTAL_REPLY_NUMBER+"");
		goReplyIb = (ImageButton)findViewById(R.id.gotoReply);
		rootRl = (RelativeLayout)findViewById(R.id.rootRl);
		bottomReplyLl = (LinearLayout)findViewById(R.id.bottom_reply_ll);
		shareLl = (LinearLayout)findViewById(R.id.shareBt);
		goodIv = (ImageView)findViewById(R.id.goodiv);
	}
    
	@Override
	protected void initData() {
		super.initData();
		mThumbnailPic.setVisibility(View.GONE);
		mForwardThumbnailPic.setVisibility(View.GONE);
		setTextValue(R.id.title_txt, "留言正文");
		Bundle bundle = getIntent().getExtras();
		mObjectId = bundle.getString("objectId");
		isToRepleylist = bundle.getBoolean("isToReplylist", false);
		isFromNoticeList = bundle.getBoolean("isFromNoticeList",false);
		msgLikeNumber = bundle.getInt("favourNumber");
		msgRepleyNumber = bundle.getInt("replyNum");
		initMsg();
		mData = new ArrayList<Map<String, Object>>();
		mSimpleAdapter = new ReplyListAdapter(TwitterShowMessageBoardActivity.this, mData, R.layout.rooms_reply_item, mFrom, mTo,mListView);
		mListView.setAdapter(mSimpleAdapter);
		if (getSharedPreferences("setting_info", 0).getBoolean("fastdrag", false)) {
			mListView.setFastScrollEnabled(true);
		}

		//如果来看评论，滑动到评论
		if(isToRepleylist){
//			selfinfoLinearLayout.setVisibility(View.GONE);
			selfinfoLinearLayout.setVisibility(View.VISIBLE);
		}else{
			selfinfoLinearLayout.setVisibility(View.VISIBLE);
		}
		transferUrl = new TransferUrl2Drawable(this);
		
	}

	public void setListViewHeightBasedOnChildren(ListView listView) {
		// 获取ListView对应的Adapter   
		if (mSimpleAdapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0, len = mSimpleAdapter.getCount(); i < len; i++) {
			// listAdapter.getCount()返回数据项的数目   
			View listItem = mSimpleAdapter.getView(i, null, listView);
			// 计算子项View 的宽高   
			listItem.measure(0, 0);
			// 统计所有子项的总高度   
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (mSimpleAdapter.getCount() - 1));
		// listView.getDividerHeight()获取子项间分隔符占用的高度   
		// params.height最后得到整个ListView完整显示需要的高度   
		listView.setLayoutParams(params);
	}

	private void initMsg() {
		// 初始化异步获取留言内容
		new AsyncTask<String, Void, MessageBoardDetail>() {

			@Override
			protected MessageBoardDetail doInBackground(String... params) {
				try {
					return getRenheApplication().getMessageBoardCommand().getMsgBoradDetail(params[0], params[1], params[2],
							TwitterShowMessageBoardActivity.this);
				} catch (Exception e) {
					return null;
				}
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				selfinfoLinearLayout.setVisibility(View.GONE);
				showDialog(1);
			}

			@SuppressLint("SimpleDateFormat")
			@Override
			protected void onPostExecute(MessageBoardDetail result) {
				super.onPostExecute(result);
				removeDialog(1);
				if (null != result) {
					if (1 == result.getState()) {
						scrollView.setVisibility(View.VISIBLE);
						btLl.setVisibility(View.VISIBLE);
						bottomReplyLl.setVisibility(View.VISIBLE);
						// 加载我的信息内容
//						if(!isToRepleylist){
//							selfinfoLinearLayout.setVisibility(View.VISIBLE);
//						}
						selfinfoLinearLayout.setVisibility(View.VISIBLE);
						SenderInfo senderInfo = result.getSenderInfo();
						if (null != senderInfo) {
//							AsyncImageLoader
//									.getInstance()
//									.populateData(TwitterShowMessageBoardActivity.this, getRenheApplication().getUserInfo().getEmail(),
//											false, true, false)
//									.loadPic(mAvatarImage, senderInfo.getSid(), senderInfo.getUserface(),
//											(int) getResources().getDimension(R.dimen.renhe_hall_bottom_image_wh),
//											(int) getResources().getDimension(R.dimen.renhe_hall_bottom_image_wh));
							ImageLoader imageLoader = ImageLoader.getInstance();		
							try {
								imageLoader.displayImage(senderInfo.getUserface(), mAvatarImage, CacheManager.options,  CacheManager.animateFirstDisplayListener);
							} catch (Exception e) {
								e.printStackTrace();
							}
							userFaceUrl = senderInfo.getUserface();
							userCompany = senderInfo.getCompany();
							userJob = senderInfo.getTitle();
							userId = senderInfo.getSid();
							messageId = result.getMessageBoardInfo().getId();
							if (null != senderInfo.getName()) {
								mNameTv.setText(senderInfo.getName().trim());
								replyEt.setHint("回复 "+senderInfo.getName().trim());
							}
							if (null != senderInfo.getTitle()) {
								mCompanyTv.setText(senderInfo.getTitle().trim() + " ");
							}
							if (null != senderInfo.getCompany()) {
								mCompanyTv.setText(mCompanyTv.getText().toString() + senderInfo.getCompany().trim());
							}
							if (null != senderInfo.getLocation()) {
								mIndustryTv.setText(senderInfo.getLocation().trim() + " ");
							}
							if (null != senderInfo.getIndustry()) {
								mIndustryTv.setText(mIndustryTv.getText().toString() + senderInfo.getIndustry().trim());
							}
							//vip、实名认证
							accountType = senderInfo.getAccountType();
							isRealName = senderInfo.isRealname();
							mSid = senderInfo.getSid();
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
							if(isRealName && accountType <= 0){
								mRealNameImage.setVisibility(View.VISIBLE);
								mRealNameImage.setImageResource(R.drawable.realname);
							}else{
								mRealNameImage.setVisibility(View.GONE);
							}
						}
						MessageBoardInfo mbInfo = result.getMessageBoardInfo();
						if (null != mbInfo) {
							mId = mbInfo.getId();
							mObjectId = mbInfo.getObjectId();
							userContent = mbInfo.getContent();
							String content = Html.fromHtml(mbInfo.getContent()).toString();
							MessageBoardMember[] messageBoardMembers = mbInfo.getAtMembers();

							if (null != messageBoardMembers && messageBoardMembers.length > 0) {
								SpannableString span = getSpannableString(content, messageBoardMembers);
								transferUrl.transferUrl(span);
								mContentTv.setText(span);
								mContentTv.setMovementMethod(LinkMovementMethod.getInstance());
							} else {
								SpannableString spannableString = getNoAtSpannedString(null, content);
								transferUrl.transferUrl(spannableString);
								mContentTv.setText(spannableString);
								mContentTv.setMovementMethod(LinkMovementMethod.getInstance());
							}

							if (mbInfo.getThumbnailPic() != null) {
								mImageUrl = mbInfo.getBmiddlePic();
								AsyncImageLoader
										.getInstance()
										.populateData(TwitterShowMessageBoardActivity.this,
												getRenheApplication().getUserInfo().getEmail(), false, true, false)
										.loadPic(mThumbnailPic, null, mbInfo.getThumbnailPic(), null, null,
												MatrixUtil.getPostMatrix(TwitterShowMessageBoardActivity.this),true);
								mThumbnailPic.setVisibility(View.VISIBLE);
							}

							ForwardMessageBoardInfo fmbInfo = mbInfo.getForwardMessageBoardInfo();
							if (null != fmbInfo) {
								MessageBoardMember[] forwardMessageBoardMembers = fmbInfo.getForwardMessageBoardAtMembers();
								mRawcontentlayout.setVisibility(View.VISIBLE);
								mSenderTv.setText(senderInfo.getName());
								String mrawContent = Html.fromHtml(fmbInfo.getContent()).toString();

								if (null != forwardMessageBoardMembers && forwardMessageBoardMembers.length > 0) {
									SpannableString span = getSpannableString(mrawContent, forwardMessageBoardMembers);
									transferUrl.transferUrl(span);
									mRawcontentTv.setText(span);
//									mRawcontentTv.setMovementMethod(LinkMovementMethod.getInstance());
									mRawcontentTv.setMovementMethod(TextViewFixTouchConsume.LocalLinkMovementMethod.getInstance());
								} else {
									SpannableString spannableString = getNoAtSpannedString(null, mrawContent);
									transferUrl.transferUrl(spannableString);
									mRawcontentTv.setText(spannableString);
//									mRawcontentTv.setMovementMethod(LinkMovementMethod.getInstance());
									mRawcontentTv.setMovementMethod(TextViewFixTouchConsume.LocalLinkMovementMethod.getInstance());

								}

								if (fmbInfo.getThumbnailPic() != null) {
									mForwardImageUrl = fmbInfo.getBmiddlePic();
									AsyncImageLoader
											.getInstance()
											.populateData(TwitterShowMessageBoardActivity.this,
													getRenheApplication().getUserInfo().getEmail(), false, true, false)
											.loadPic(mForwardThumbnailPic, null, fmbInfo.getThumbnailPic(), null, null,
													MatrixUtil.getPostMatrix(TwitterShowMessageBoardActivity.this),true);
									mForwardThumbnailPic.setVisibility(View.VISIBLE);
								}
								final ForwardMessageBoardInfo mFmbInfo = fmbInfo;
								//点击转发内容区域，进入转发内容详情
								mRawcontentlayout.setOnClickListener(new OnClickListener() {
									
									@Override
									public void onClick(View arg0) {
										if(mFmbInfo.isForwardRenhe() && null != mFmbInfo.getMessageBoardObjectId()){
											String objectId = mFmbInfo.getMessageBoardObjectId();
											Bundle bundle = new Bundle();
											bundle.putString("objectId", objectId);
											Intent intent = new Intent(TwitterShowMessageBoardActivity.this, TwitterShowMessageBoardActivity.class);
											intent.putExtras(bundle);
											startActivity(intent);
											overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
										}
									}
								});
							} else {
								mRawcontentlayout.setVisibility(View.GONE);
							}
							mSourceTv.setText("来自" + mbInfo.getFromSource());
							DateFormat format1 = new SimpleDateFormat("MM-dd HH:mm");
							String mDateTime = mbInfo.getCreatedDate();
							try {
								Date date = format1.parse(mbInfo.getCreatedDate());
								SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.readable_date_md_hm));
								mDateTime = dateFormat.format(date);
							} catch (ParseException e) {
								e.printStackTrace();
							}
							
							mTimeTv.setText(mDateTime);
							if (mbInfo.getReplyNum() < 1) {
								mReplyBt.setText("回复");
							} else {
								Paint paint = new Paint();
								paint.setTextSize(14);
								Float l = paint.measureText("" + mbInfo.getReplyNum());
//								mReplyBt.setText("回复 " + mbInfo.getReplyNum());
								mReplyBt.setText(mbInfo.getReplyNum()+"");
								replyNumber = mbInfo.getReplyNum();
							}
							isLiked = mbInfo.isLiked();
							likeNumber = mbInfo.getLikedNum();
							if (likeNumber <= 0) {
								mGoodBt.setText("赞");
//								mGoodBt.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.room_detail_good_p),
//										null, null, null);
								goodIv.setImageResource(R.drawable.room_detail_good_p);
								mGoodBt.setTextColor(getResources().getColor(R.color.blog_item_date_text));
							} else {
								mGoodBt.setText(likeNumber + "");
								if (isLiked) {
//									mGoodBt.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.room_detail_good),
//											null, null, null);
									goodIv.setImageResource(R.drawable.room_detail_good);
									mGoodBt.setTextColor(getResources().getColor(R.color.room_good_textcolor));
								} else {
//									mGoodBt.setCompoundDrawablesWithIntrinsicBounds(
//											getResources().getDrawable(R.drawable.room_detail_good_p), null, null, null);
									goodIv.setImageResource(R.drawable.room_detail_good_p);
									mGoodBt.setTextColor(getResources().getColor(R.color.blog_item_date_text));
								}
							}
							if(!isFromNoticeList){
								if (likeNumber != msgLikeNumber) {
									Intent intent = new Intent(MessageBoardActivity.ROOM_ITEM_STATE_ACTION_STRING);
									intent.putExtra("objectId", mObjectId);
									intent.putExtra("favourNumber", likeNumber);
									intent.putExtra("isFavour", mbInfo.isLiked());
									sendBroadcast(intent);
								}
								if (mbInfo.getReplyNum() != msgRepleyNumber) {
									Intent intent = new Intent(MessageBoardActivity.ROOM_ITEM_STATE_ACTION_STRING_REPLY_CHANGE);
									intent.putExtra("objectId", mObjectId);
									intent.putExtra("new_repleynum", mbInfo.getReplyNum());
									sendBroadcast(intent);
								}
							}
						}
					}
					initGet(mObjectId, true, false);
				} else {
					nowifiLayout.setVisibility(View.VISIBLE);
//					ToastUtil.showNetworkError(ShowMessageBoardActivity.this);
				}
				
			}

		}.execute(mObjectId, getRenheApplication().getUserInfo().getAdSId(), getRenheApplication().getUserInfo().getSid());
	}

	//
	private void initGet(String objectId, final boolean hideFooter, final boolean isFromReply) {
		// 初始化异步加载留言回复列表数据
		new ReplyListTask(this, new IDataBack() {

			@Override
			public void onPre() {
				if(NetworkUtil.hasNetworkConnection(TwitterShowMessageBoardActivity.this) != -1){
					mFooterView.setVisibility(View.VISIBLE);
					mFooterViewMore.setVisibility(View.GONE);
					mFooterViewIng.setVisibility(View.VISIBLE);
					blankRel.setVisibility(View.GONE);
				}else{
					blankRel.setVisibility(View.GONE);
					blankSignTv.setText("网络不给力，请稍后再试~");
				}
			}

			@Override
			public void onPost(List<Map<String, Object>> result) {
				if (null != result) {
					mStart += result.size();
					if (hideFooter) {
						if (result.size() <= 0) {
							//							replyListLl.setVisibility(View.GONE);
							mListView.setVisibility(View.GONE);
							mFooterView.setVisibility(View.GONE);
							blankRel.setVisibility(View.GONE);
							blankSignTv.setText("还没有人回复");
						} else {
							//							replyListLl.setVisibility(View.VISIBLE);

							mListView.setVisibility(View.VISIBLE);
							mFooterView.setVisibility(View.VISIBLE);
							mFooterViewMore.setVisibility(View.VISIBLE);
							mFooterViewIng.setVisibility(View.GONE);
							blankRel.setVisibility(View.GONE);
						}
					}
					if (result.size() < mCount) {
						mFooterView.setVisibility(View.GONE);
					} else {
						mFooterView.setVisibility(View.VISIBLE);
						mFooterViewMore.setVisibility(View.VISIBLE);
						mFooterViewIng.setVisibility(View.GONE);
					}
					mData.addAll(result);
//					for(int i = result.size() - 1; i >= 0; i--){
//						mData.add(result.get(i));
//					}
					mSimpleAdapter.notifyDataSetChanged();
					if (isFromReply) {
						mListView.setSelection(mListView.getAdapter().getCount() - 1);
					}
					setListViewHeightBasedOnChildren(mListView);
				} else {
					mFooterView.setVisibility(View.GONE);
					if (hideFooter) {
						//						replyListLl.setVisibility(View.GONE);
						mListView.setVisibility(View.GONE);
						mFooterView.setVisibility(View.GONE);
						blankRel.setVisibility(View.GONE);
						blankSignTv.setText("还没有人回复");
					}
//					ToastUtil.showNetworkError(ShowMessageBoardActivity.this);
					//					mStart = (mStart -= mCount) == 0 ? 0 : mStart;
				}
			}
		}).execute(objectId, getRenheApplication().getUserInfo().getAdSId(), getRenheApplication().getUserInfo().getSid(),
				mStart, mCount);
	}

	private void toggleHeaderView(boolean isShow) {
		if (isShow) {
			mHeaderView.findViewById(R.id.waitPb).setVisibility(View.VISIBLE);
			((TextView) mHeaderView.findViewById(R.id.titleTv)).setText("数据加载中...");
		} else {
			mHeaderView.findViewById(R.id.waitPb).setVisibility(View.GONE);
			((TextView) mHeaderView.findViewById(R.id.titleTv)).setText("点击刷新");
		}
	}

	private void toggleFooterView(boolean isShow) {
		if (isShow) {
			((TextView) mFooterView.findViewById(R.id.titleTv)).setText("加载数据中...");
			mFooterView.findViewById(R.id.waitPb).setVisibility(View.VISIBLE);
		} else {
			((TextView) mFooterView.findViewById(R.id.titleTv)).setText("查看更多数据");
			mFooterView.findViewById(R.id.waitPb).setVisibility(View.GONE);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 1:
			ProgressDialog findPd = new ProgressDialog(this);
			//			findPd.setTitle("获取详情数据");
			findPd.setMessage("请稍候...");
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
		mThumbnailPic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (null != mImageUrl && !"".equals(mImageUrl)) {
					Intent intent = new Intent(TwitterShowMessageBoardActivity.this, StandardImageXML.class);
					intent.putExtra("imageurl", mImageUrl);
					startActivity(intent);
				}

			}
		});

		mForwardThumbnailPic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (null != mForwardImageUrl && !"".equals(mForwardImageUrl)) {
					Intent intent = new Intent(TwitterShowMessageBoardActivity.this, StandardImageXML.class);
					intent.putExtra("imageurl", mForwardImageUrl);
					startActivity(intent);
				}
			}
		});
		mLayout1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TwitterShowMessageBoardActivity.this, MyHomeArchivesActivity.class);
				intent.putExtra(MyHomeArchivesActivity.FLAG_INTENT_DATA, mSid);
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			}
		});
		// 监听转发按钮单击事件
		mForwordLl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (null != mObjectId) {
					Bundle bundle = new Bundle();
					bundle.putString("objectId", mObjectId);
					if (!TextUtils.isEmpty(mRawcontentTv.getText().toString())) {
						bundle.putString("sender", mNameTv.getText().toString());
						bundle.putString("rawContent", mContentTv.getText().toString());
					}
					startActivity(ForwardMessageBoardActivity.class, bundle);
				}
			}
		});
		// 监听回复按钮单击事件
		mReplyLl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (null != mId && null != mObjectId) {
					Bundle bundle = new Bundle();
					bundle.putString("objectId", mObjectId);
					bundle.putString("id", mId);
					Intent intent = new Intent(TwitterShowMessageBoardActivity.this, AddReplyActivity.class);
					intent.putExtras(bundle);
					startActivityForResult(intent, 2);
					overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
				}
			}
		});
		mGoodLl.setOnClickListener(new goodClick(mGoodBt));
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (null != mData && mData.size() > position) {
					Intent intent = new Intent(TwitterShowMessageBoardActivity.this, AddReplyActivity.class);
					Map<String, Object> map = mData.get(position);
					if (null != map) {
//						String sid = (String) map.get("sid");
//						intent.putExtra(MyHomeArchivesActivity.FLAG_INTENT_DATA, sid);
//						startActivity(intent);
						String senderName = (String) map.get("titleTv");
						String replyMessageBoardId = (String) map.get("id");
						String replyMessageBoardObjectId = (String) map.get("objectId");
						Bundle bundle = new Bundle();
						bundle.putString("senderName", senderName);
						bundle.putString("objectId", mObjectId);
						bundle.putString("id", mId);
						bundle.putString("replyMessageBoardId", replyMessageBoardId);
						bundle.putString("replyMessageBoardObjectId", replyMessageBoardObjectId);
						bundle.putBoolean("isFromReplylist", true);
						intent.putExtras(bundle);
						startActivityForResult(intent, 2);
					}
				}
			}
		});
		mFooterViewMore.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				initGet(mObjectId, false, false);
			}
		});
		replyEt.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				if(arg1){
					toForwardCb.setVisibility(View.VISIBLE);
				}else{
					toForwardCb.setVisibility(View.GONE);
				}
			}
		});
		replyEt.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (count > TOTAL_REPLY_NUMBER) {
					return;
				}
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
				leftReplyNumTv.setText(TOTAL_REPLY_NUMBER - s.length() + "");
			}
		});
		goReplyIb.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final View mView = v;
				String content = replyEt.getText().toString().trim();
				final String mcontent = content;
				if (!TextUtils.isEmpty(content)) {
					new AsyncTask<Object, Void, MessageBoardOperation>() {
						@Override
						protected MessageBoardOperation doInBackground(
								Object... params) {
							try {
								return getRenheApplication()
										.getMessageBoardCommand()
										.replyMessageBoard((String) params[0],
												(String) params[1],
												(String) params[2],
												(String) params[3],
												(String) params[4],
												(Boolean) params[5],(String) params[6],(String) params[7],TwitterShowMessageBoardActivity.this);
							} catch (Exception e) {
								System.out.println(e);
								return null;
							}
						}

						@Override
						protected void onPreExecute() {
							super.onPreExecute();
							showDialog(1);
						}

						@Override
						protected void onPostExecute(
								MessageBoardOperation result) {
							super.onPostExecute(result);
							removeDialog(1);
							InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(mView.getWindowToken(), 0);
							if (null != result) {
								if (1 == result.getState()) {
									replyEt.setText("");
									replyEt.clearFocus();
									if(mData.size() <= replyNumber){
										mReplyBt.setText(replyNumber + 1 + "");
//										initGet(mObjectId, true, true);
										mListView.setVisibility(View.VISIBLE);
										if(!TextUtils.isEmpty(mcontent)){
											mReplyBt.setText(replyNumber + 1 + "");
//										initGet(mObjectId, true, true);
											replyNumber += 1;
											
											String[] mmFrom = new String[] { "titleTv", "infoTv", "timeTv",
													"objectId" ,"userFace","accountType","isRealName"};
											//本地造一个评论信息放在列表第一个位置
											SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm");
											Date curDate = new Date(System.currentTimeMillis());//获取当前时间       
											String curDatestr = formatter.format(curDate);
											Map<String, Object> map = new HashMap<String, Object>();
											map.put(mmFrom[0], RenheApplication.getInstance().getUserInfo().getName());
											map.put(mmFrom[1], Html.fromHtml(mcontent));
											map.put(mmFrom[2], curDatestr);
											map.put(mmFrom[3], "");
											map.put("id","");
											map.put("sid", RenheApplication.getInstance().getUserInfo().getSid());
											map.put(mmFrom[4], RenheApplication.getInstance().getUserInfo().getUserface());
											map.put(mmFrom[5], RenheApplication.getInstance().getAccountType());
											map.put(mmFrom[6], false);
											mData.add(0,map);
											mSimpleAdapter.notifyDataSetChanged();
										}
									}
									
									//发广播，通知客厅界面更新消息的状态
//									if(!isFromReplyList){
									
										Intent intent = new Intent();
										intent.putExtra("objectId", mObjectId);
										intent.setAction(MessageBoardActivity.ROOM_ITEM_STATE_ACTION_STRING_REPLY);
										TwitterShowMessageBoardActivity.this.sendBroadcast(intent);
										
										Intent intent1 = new Intent();
										intent1.putExtra("objectId", mObjectId);
										setResult(RESULT_OK,intent1);
//									}
									ToastUtil.showToast(TwitterShowMessageBoardActivity.this, "发布成功");
								} else {
									ToastUtil.showErrorToast(
											TwitterShowMessageBoardActivity.this, "发布失败");
								}
							}else {
								ToastUtil.showNetworkError(TwitterShowMessageBoardActivity.this);
							}
						}
					}.execute(getRenheApplication().getUserInfo().getAdSId(),
							getRenheApplication().getUserInfo().getSid(), mId,
							mObjectId, content, toForwardCb.isChecked(),null, null);
				} else {
					ToastUtil.showToast(TwitterShowMessageBoardActivity.this, "回复不能为空");
				}
			}
		});
		shareLl.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(TwitterShowMessageBoardActivity.this, ShareDialogActivity.class);
				intent.putExtra("userName", mNameTv.getText().toString().trim());
				intent.putExtra("userFaceUrl", userFaceUrl);
				intent.putExtra("userCompany", userCompany);
				intent.putExtra("userJob", userJob);
				intent.putExtra("userId", userId);
				intent.putExtra("messageId", messageId);
				if(!TextUtils.isEmpty(userContent)){
					if(userContent.length() > 50){
						userContent = userContent.substring(0, 50);
					}
				}
				intent.putExtra("userContent", userContent);
				TwitterShowMessageBoardActivity.this.startActivity(intent);
				TwitterShowMessageBoardActivity.this.overridePendingTransition(R.anim.push_bottom_in, R.anim.push_bottom_out);
			}
		});
	}
	public int getDistance() {
		final float scale = this.getResources().getDisplayMetrics().density;
		int pxVal = (int) (20 * scale + 0.5f);
		return pxVal + topLayout.getHeight();
	}

	class goodClick implements OnClickListener {

		View goodButton;

		public goodClick(View view) {
			this.goodButton = view;
		}

		@Override
		public void onClick(View arg0) {
			LinearLayout layout = (LinearLayout) arg0;
			Button gButton = (Button) layout.getChildAt(1);
			if (null != mObjectId) {
				if (isLiked) {
					markFavour(mId, mObjectId, false);
				} else {
					markFavour(mId, mObjectId, true);
				}
			}
			if (isLiked) {
				isLiked = false;
				likeNumber = likeNumber - 1;
			} else {
				isLiked = true;
				likeNumber = likeNumber + 1;
			}
			if (isLiked) {
//				gButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.room_detail_good), null, null, null);
				goodIv.setImageResource(R.drawable.room_detail_good);
				
				Animation animation = AnimationUtils.loadAnimation(TwitterShowMessageBoardActivity.this, R.anim.good_scale);
				goodIv.startAnimation(animation);
//				if(goodIv.getDrawable() != null){
//					Drawable manimationDrawable = goodIv.getDrawable();
//					if(manimationDrawable instanceof AnimationDrawable){
//						((AnimationDrawable)manimationDrawable).stop();
//					}
//				}
//				goodIv.setImageResource(R.anim.good_animation);
//				animationDrawable = (AnimationDrawable) goodIv.getDrawable();
//				animationDrawable.start();
				gButton.setTextColor(getResources().getColor(R.color.room_good_textcolor));
				if (likeNumber > 0) {
					gButton.setText(likeNumber + "");
				} else {
					gButton.setText("赞");
				}
			} else {
//				gButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.room_detail_good_p), null, null, null);
				goodIv.setImageResource(R.drawable.room_detail_good_p);
				gButton.setTextColor(getResources().getColor(R.color.blog_item_date_text));
				if (likeNumber > 0) {
					gButton.setText(likeNumber + "");
				} else {
					gButton.setText("赞");
				}
			}
			//发广播，通知客厅界面更新消息的状态
			Intent intent = new Intent();
			intent.putExtra("objectId", mObjectId);
			intent.putExtra("isFavour", isLiked);
			intent.putExtra("favourNumber", likeNumber);
			intent.setAction(MessageBoardActivity.ROOM_ITEM_STATE_ACTION_STRING);
			sendBroadcast(intent);
		}

	}

	private void markFavour(String id, String objectId, final boolean toFavour) {
		new AsyncTask<String, Void, MessageBoardOperation>() {

			@Override
			protected MessageBoardOperation doInBackground(String... params) {
				try {
					if (toFavour) {
						return ((RenheApplication) getApplicationContext()).getMessageBoardCommand().favourMessageBoard(
								params[0], params[1], params[2], params[3], TwitterShowMessageBoardActivity.this);
					} else {
						return ((RenheApplication) getApplicationContext()).getMessageBoardCommand().unFavourMessageBoard(
								params[0], params[1], params[3], TwitterShowMessageBoardActivity.this);
					}
				} catch (Exception e) {
					return null;
				}
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(MessageBoardOperation result) {
				super.onPostExecute(result);
				if (null != result) {
					if (1 == result.getState()) {
					}
				} else {
					//ToastUtil.showNetworkError(ct);
				}
			}

		}.execute(((RenheApplication) getApplicationContext()).getUserInfo().getAdSId(),
				((RenheApplication) getApplicationContext()).getUserInfo().getSid(), id, objectId);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 2:
			if (resultCode == RESULT_OK) {
				mListView.setVisibility(View.VISIBLE);
				String content  = data.getStringExtra("content");
				if(TextUtils.isEmpty(content)){
					content = "";
				}
				mReplyBt.setText(replyNumber + 1 + "");
//				initGet(mObjectId, true, true);
				replyNumber += 1;
				
				String[] mmFrom = new String[] { "titleTv", "infoTv", "timeTv",
						"objectId" ,"userFace","accountType","isRealName"};
				//本地造一个评论信息放在列表第一个位置
				SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm");
				Date curDate = new Date(System.currentTimeMillis());//获取当前时间       
				String curDatestr = formatter.format(curDate);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put(mmFrom[0], RenheApplication.getInstance().getUserInfo().getName());
				map.put(mmFrom[1], Html.fromHtml(content));
				map.put(mmFrom[2], curDatestr);
				map.put(mmFrom[3], "");
				map.put("id","");
				map.put("sid", RenheApplication.getInstance().getUserInfo().getSid());
				map.put(mmFrom[4], RenheApplication.getInstance().getUserInfo().getUserface());
				map.put(mmFrom[5], RenheApplication.getInstance().getAccountType());
				map.put(mmFrom[6], false);
				mData.add(0,map);
				mSimpleAdapter.notifyDataSetChanged();
			}
			break;
		}
	}

	protected SpannableString getSpannableString(String content, MessageBoardMember[] messageBoardMembers) {

		SpannableString span = new SpannableString(content);
		char c = '@';
		char[] chars = content.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (c == chars[i]) {
				String itemName = "";
				int index;
				String tempContent = content.substring(i + 1, content.length());
				int at = tempContent.indexOf("@");
				int seleparator = tempContent.indexOf("//");
				int halfAngleColon = tempContent.indexOf(":");
				int fullAngleColon = tempContent.indexOf("：");

				if (at < 0 && seleparator < 0 && halfAngleColon < 0 && fullAngleColon < 0) {
					if (tempContent.indexOf(" ") < 0) {
						itemName = tempContent;
					} else {
						itemName = tempContent.substring(0, tempContent.indexOf(" "));
					}
				} else {
					if (halfAngleColon < 0) {
						if (fullAngleColon != -1) {
							halfAngleColon = tempContent.indexOf("：");
						} else {
							halfAngleColon = 1000000;
						}
					}
					if (seleparator < 0) {
						seleparator = 1000000;
					}
					if (at < 0) {
						at = 1000000;
					}
					index = at < seleparator ? (at < halfAngleColon ? at : (seleparator < halfAngleColon ? seleparator
							: halfAngleColon)) : (seleparator < halfAngleColon ? seleparator : halfAngleColon);
					int blankindex = tempContent.indexOf(" ");
					if(blankindex != -1){
						index = index > blankindex ? blankindex : index;
					}
					itemName = tempContent.substring(0, index);
				}
				for (MessageBoardMember member : messageBoardMembers) {

					if (member.getMemberName().trim().equals(itemName.trim())) {
						span.setSpan(new MessageMemberSpanClick(member.getMemberId()), i, i + itemName.length() + 1,
								Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
						span.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.room_at_color)), i,
								i + itemName.length() + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
						break;
					}
				}

				getNoAtSpannedString(span, content);
			}
		}
		return span;

	}

	//	protected void getSpannedString(SpannableString span,String teString) {
	//		String[] zh = getResources().getStringArray(R.array.face_zh);
	//		String[] en = getResources().getStringArray(R.array.face_en);
	//		for(int i = 0; i < zh.length; i ++){
	//			if(count(teString, zh[i]) != null){
	//				int[] a = count(teString, zh[i]);
	//				if(a != null && a.length > 0){
	//					for(int f : a){
	//						int id =getResources().getIdentifier(en[i], "drawable", getPackageName()); //name:图片的名，defType：资源类型（drawable，string。。。），defPackage:工程的包名
	//						Drawable drawable=getResources().getDrawable(id);
	//						BitmapDrawable bd = (BitmapDrawable) drawable;
	//	                    bd.setBounds(0, 0, bd.getIntrinsicWidth(),
	//	                            bd.getIntrinsicHeight());
	//	                    span.setSpan(new ImageSpan(bd), f, f + zh[i].length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
	//					}
	//				}
	//			}
	//		}
	//	}
	protected SpannableString getNoAtSpannedString(SpannableString span, String teString) {
		String[] zh = getResources().getStringArray(R.array.face_zh);
		String[] en = getResources().getStringArray(R.array.face_en);
		if (null == span) {
			span = new SpannableString(teString);
		}
		for (int i = 0; i < zh.length; i++) {
			if (count(teString, zh[i]) != null) {
				int[] a = count(teString, zh[i]);
				if (a != null && a.length > 0) {
					for (int f : a) {
						int id = getResources().getIdentifier(en[i], "drawable", getPackageName()); //name:图片的名，defType：资源类型（drawable，string。。。），defPackage:工程的包名
						Drawable drawable = getResources().getDrawable(id);
						BitmapDrawable bd = (BitmapDrawable) drawable;
						bd.setBounds(0, 0, bd.getIntrinsicWidth() / 2, bd.getIntrinsicHeight() / 2);
						span.setSpan(new ImageSpan(bd), f, f + zh[i].length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
				}
			}
		}
		return span;
	}

	protected int[] count(String text, String sub) {
		int count = 0, start = 0;
		while ((start = text.indexOf(sub, start)) >= 0) {
			start += sub.length();
			count++;
		}
		if (count == 0) {
			return null;
		}
		int a[] = new int[count];
		int count2 = 0;
		while ((start = text.indexOf(sub, start)) >= 0) {
			a[count2] = start;
			start += sub.length();
			count2++;
		}
		return a;
	}

	class MessageMemberSpanClick extends ClickableSpan implements OnClickListener {
		String id;

		public MessageMemberSpanClick(String id) {
			this.id = id;
		}

		@Override
		public void updateDrawState(TextPaint ds) {
			ds.setUnderlineText(false); //去掉下划线
		}

		@Override
		public void onClick(View v) {
			if (null != id && !"".equals(id)) {
				Intent intent = new Intent(TwitterShowMessageBoardActivity.this, MyHomeArchivesActivity.class);
				intent.putExtra(MyHomeArchivesActivity.FLAG_INTENT_DATA, id);
				TwitterShowMessageBoardActivity.this.startActivity(intent);
			}
		}

	}
}
