package com.itcalf.renhe.context.archives;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.itcalf.renhe.R;
import com.itcalf.renhe.RenheApplication;
import com.itcalf.renhe.cache.CacheManager;
import com.itcalf.renhe.cache.ExternalStorageUtil;
import com.itcalf.renhe.context.contacts.SearchContactsActivity;
import com.itcalf.renhe.context.innermsg.SendInnerMsgActivity;
import com.itcalf.renhe.context.register.BindPhoneGuideActivity;
import com.itcalf.renhe.context.room.MessageBoardActivity;
import com.itcalf.renhe.context.template.ActivityTemplate;
import com.itcalf.renhe.context.template.BaseActivity;
import com.itcalf.renhe.dto.AddFriend;
import com.itcalf.renhe.dto.Profile;
import com.itcalf.renhe.dto.Profile.UserInfo.AimTagInfo;
import com.itcalf.renhe.dto.Profile.UserInfo.ContactInfo;
import com.itcalf.renhe.dto.Profile.UserInfo.EduExperienceInfo;
import com.itcalf.renhe.dto.Profile.UserInfo.OtherInfo;
import com.itcalf.renhe.dto.Profile.UserInfo.OtherInfo.Site;
import com.itcalf.renhe.dto.Profile.UserInfo.PreferredTagInfo;
import com.itcalf.renhe.dto.Profile.UserInfo.SpecialtiesInfo;
import com.itcalf.renhe.dto.Profile.UserInfo.SummaryInfo;
import com.itcalf.renhe.dto.Profile.UserInfo.WorkExperienceInfo;
import com.itcalf.renhe.dto.SearchCity;
import com.itcalf.renhe.utils.AsyncImageLoader;
import com.itcalf.renhe.utils.ToastUtil;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * Feature:个人档案界面 Desc:个人档案界面
 * 
 * @author xp
 * 
 */
public class MyHomeArchivesActivity extends BaseActivity {

	public static String FLAG_INTENT_DATA = "profileSid";
	//登出标识（两次按下返回退出时的标识字段）
	private boolean logoutFlag;
	private RelativeLayout mBackBt;
	private ImageView mAvatarImgV;
	private TextView mNameTv;// 姓名
	//	private TextView mLoginnameTv;// 登录名
	private TextView mCompanyTv;// 公司
	private TextView mIndustryTv;// 公司地址
	private ImageView mRightImage;
	private ImageView mVipImage;
	private ImageView mRealNameImage;
	private LinearLayout mContactLayout;// 联系信息
	private LinearLayout mSummaryInfoLayout;// 概要信息
	private LinearLayout mEduExperienceInfoLayout;// 教育经历
	private LinearLayout mWorkExperienceInfoLayout;// 工作经历
	private LinearLayout mOtherInfoLayout;// 其他信息

	private RelativeLayout mRoomBt;// 客厅
	private RelativeLayout mContactBt;// 联系人
	private TextView roomNumTv;
	private TextView contactNumTv;

	private static final boolean TEST = false;

	//	private LinearLayout mUserLayout;
	private ScrollView mScrollView;
	private String mOtherSid;

	private ImageButton mSharetoBt;

	private Profile mProfile;

	private String APP_ID = "wx6d03435b4ef6f18d";
	private IWXAPI api;
	private String userFaceUrl;
	private String userName;
	private String userCompany;
	private String userIndustry;
	private String userDesp;
	private int accountType;//账号vip等级类型：0：普通会员；1：VIP会员；2：黄金会员；3：铂金会员
	private boolean isRealName;//是否是实名认证的会员

	private TextView bindPhoneTv;
	private Button newEditButton;
	private RelativeLayout rootRl;
	private ImageView cameraIv;
	public static final String REFRESH_ARCHIEVE_RECEIVER_ACTION = "com.renhe.refresh_archieve";
	private LinearLayout mProvideGetInfoLayout;// 提供、得到
	public static final String ITEM_SELEPARATOR = "      ";
	private boolean isUpdateAvatar = false;
	private Bitmap updatedBitmap;
	private View seleparateView;
	private ImageView contactNumIcon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 
		new ActivityTemplate().doInActivity(this, R.layout.archives_myhome);
	}

	@Override
	protected void findView() {
		super.findView();

		mBackBt = (RelativeLayout) findViewById(R.id.backBtRl);
		mAvatarImgV = (ImageView) findViewById(R.id.avatarImage);
		mAvatarImgV.setEnabled(false);
		mNameTv = (TextView) findViewById(R.id.nameTv);
		//		mLoginnameTv = (TextView) findViewById(R.id.loginnameTv);
		mCompanyTv = (TextView) findViewById(R.id.companyTv);
		mIndustryTv = (TextView) findViewById(R.id.industryTv);
		mContactLayout = (LinearLayout) findViewById(R.id.contactLayout);
		mSummaryInfoLayout = (LinearLayout) findViewById(R.id.summaryInfoLayout);
		mEduExperienceInfoLayout = (LinearLayout) findViewById(R.id.eduExperienceInfoLayout);
		mWorkExperienceInfoLayout = (LinearLayout) findViewById(R.id.workExperienceInfoLayout);
		mOtherInfoLayout = (LinearLayout) findViewById(R.id.otherInfoLayout);
		mRightImage = (ImageView) findViewById(R.id.rightImage);
		mVipImage = (ImageView) findViewById(R.id.vipImage);
		mRealNameImage = (ImageView) findViewById(R.id.realnameImage);
		mRoomBt = (RelativeLayout) findViewById(R.id.roomBt);
		mContactBt = (RelativeLayout) findViewById(R.id.contactBt);

		roomNumTv = (TextView) findViewById(R.id.roomNum);
		contactNumTv = (TextView) findViewById(R.id.contactNum);

		//		mUserLayout = (LinearLayout) findViewById(R.id.layout2);
		mScrollView = (ScrollView) findViewById(R.id.scrollView);

		mSharetoBt = (ImageButton) findViewById(R.id.sharetoBt);

		newEditButton = (Button) findViewById(R.id.new_editBt);
		rootRl = (RelativeLayout) findViewById(R.id.rootRl);
		cameraIv = (ImageView) findViewById(R.id.cameraImage);

		seleparateView = (View) findViewById(R.id.room_contact_seleprate);

		mProvideGetInfoLayout = (LinearLayout) findViewById(R.id.provideGetInfoLayout);
		newEditButton.setEnabled(true);
		newEditButton.setBackgroundResource(R.drawable.archieve_edit_bt_selected);
		contactNumIcon = (ImageView) findViewById(R.id.contact_flag_icon);
	}
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem share = menu.findItem(R.id.item_share);
		share.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
	    share.setVisible(true);
		return super.onPrepareOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.item_share:
			Intent intent = new Intent();
			intent.putExtra("profile", mProfile);
			intent.putExtra("othersid", mOtherSid);
			intent.putExtra("userName", userName);
			intent.putExtra("userDesp", userDesp);
			intent.putExtra("userFaceUrl", userFaceUrl);
			intent.putExtra("isFromArchieve", true);
			intent.setClass(MyHomeArchivesActivity.this, DialogActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.push_bottom_in, R.anim.push_bottom_out);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	protected void initData() {
		super.initData();
		setTextValue(R.id.title_txt, "档案");
		/**
		 * 注册到微信
		 */
		api = WXAPIFactory.createWXAPI(getApplicationContext(), APP_ID, true);
		api.registerApp(APP_ID);
			mOtherSid = getIntent().getStringExtra(FLAG_INTENT_DATA);
			showDialog(2);
			new ProfileTask().execute(mOtherSid, getRenheApplication().getUserInfo().getSid(), getRenheApplication()
					.getUserInfo().getAdSId());
	}
	
	class ProfileTask extends AsyncTask<String, Void, Profile> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mScrollView.setVisibility(View.GONE);
		}

		@Override
		protected Profile doInBackground(String... params) {
			try {
				return getRenheApplication().getProfileCommand().showProfile(params[0], params[1], params[2],
						MyHomeArchivesActivity.this);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Profile result) {
			super.onPostExecute(result);

			try {
				dismissDialog(2);
				removeDialog(2);
			} catch (Exception e) {
			}
			if (null != result) {
				if (1 == result.getState() && null != result.getUserInfo()) {
					populateData(result);
				}
				mScrollView.setVisibility(View.VISIBLE);
			} else {
				ToastUtil.showNetworkError(MyHomeArchivesActivity.this);
			}
		}
	}

	private void populateData(Profile result) {
		this.userFaceUrl = result.getUserInfo().getUserface();
		this.userName = result.getUserInfo().getName().trim();
		if (null != result.getUserInfo().getTitle()) {
			this.userCompany = result.getUserInfo().getTitle().trim() + " ";
		}
		if (null != result.getUserInfo().getCompany()) {
			this.userCompany = mCompanyTv.getText().toString() + result.getUserInfo().getCompany().trim();
		}
		if (null != result.getUserInfo().getLocation()) {
			this.userIndustry = result.getUserInfo().getLocation().trim() + " ";
		}
		if (null != result.getUserInfo().getIndustry()) {
			this.userIndustry = mIndustryTv.getText().toString() + result.getUserInfo().getIndustry().trim();
		}
		this.userDesp = this.userCompany + this.userIndustry;
		this.accountType = result.getUserInfo().getAccountType();
		this.isRealName = result.getUserInfo().isRealName();
		mProfile = result;
		//		result.setConnection(false);
		//		result.setSelf(false);
		if (result.isSelf()) {
			cameraIv.setVisibility(View.VISIBLE);
			newEditButton.setText("更新档案");
			if (result.getUserInfo().getLocation() != null) {
				SearchCity searchCity = new SearchCity(-111, result.getUserInfo().getLocation());
				RenheApplication.getInstance().setCurrentCity(searchCity);
			}
			mAvatarImgV.setEnabled(true);
			//			mUserLayout.setVisibility(View.VISIBLE);
			CacheManager.getInstance().populateData(MyHomeArchivesActivity.this)
					.saveObject(result, getRenheApplication().getUserInfo().getEmail(), CacheManager.PROFILE);
		} else if (result.isConnection()) {

			//			if (messageNum == 0) {
			//				mRoomBt.setText("留言");
			//			} else {
			//				mRoomBt.setText("留言(" + messageNum + ")");
			//			}
			newEditButton.setText("写站内信");
			//			mUserLayout.setVisibility(View.GONE);
			if (result.isFollowing()) {
			} else {
			}
		} else {

			//			if (messageNum == 0) {
			//				mRoomBt.setText("留言");
			//			} else {
			//				mRoomBt.setText("留言(" + messageNum + ")");
			//			}

			if (result.isInvite()) {
				newEditButton.setText("已发送好友请求");
				newEditButton.setEnabled(false);
				newEditButton.setBackgroundResource(R.drawable.archieve_edit_bt_p_shape);
			} else {
				newEditButton.setText("加好友");
			}

			//			mUserLayout.setVisibility(View.GONE);
			if (result.isFollowing()) {
			} else {
			}
			//			mContactBt.setBackgroundResource(R.drawable.archives_userbtn_selected);
			//			mContactBt.setTextColor(getResources().getColor(R.color.grayColor));
			//			mContactBt.setCompoundDrawables(null, null, getResources().getDrawable(R.drawable.archives_right_gray), null);
			if (RenheApplication.getInstance().getAccountType() <= 0) {
				contactNumTv.setTextColor(getResources().getColor(R.color.new_archieve_fourbt_textcolor));
				contactNumIcon.setVisibility(View.INVISIBLE);
				mContactBt.setEnabled(false);
			}
		}
		if (result.getUserInfo().getFriendDegree() == 1) {
			mRightImage.setImageDrawable(getResources().getDrawable(R.drawable.icon_1st));
		} else if (result.getUserInfo().getFriendDegree() == 2) {
			mRightImage.setImageDrawable(getResources().getDrawable(R.drawable.icon_2nd));
		} else {
			mRightImage.setVisibility(View.GONE);
		}
		switch (this.accountType) {
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
		if (this.isRealName && accountType <= 0) {
			mRealNameImage.setVisibility(View.VISIBLE);
			mRealNameImage.setImageResource(R.drawable.realname);
		} else {
			mRealNameImage.setVisibility(View.GONE);
		}
		AsyncImageLoader
				.getInstance()
				.populateData(MyHomeArchivesActivity.this, getRenheApplication().getUserInfo().getEmail(), false, true, true)
				.loadPic(mAvatarImgV, mOtherSid, result.getUserInfo().getUserface(),
						(int) getResources().getDimension(R.dimen.renhe_archieve_image_wh),
						(int) getResources().getDimension(R.dimen.renhe_archieve_image_wh));
		//		ImageLoader imageLoader = ImageLoader.getInstance();		
		//		try {
		//			imageLoader.displayImage(result.getUserInfo().getUserface(), mAvatarImgV, CacheManager.options,  CacheManager.animateFirstDisplayListener);
		//		} catch (Exception e) {
		//			e.printStackTrace();
		//		}
		if (null != result.getUserInfo().getName()) {
			mNameTv.setText(result.getUserInfo().getName().trim());
		}
		if (null != result.getUserInfo().getTitle()) {
			mCompanyTv.setText(result.getUserInfo().getTitle().trim() + " ");
		}
		if (null != result.getUserInfo().getCompany()) {
			mCompanyTv.setText(mCompanyTv.getText().toString() + result.getUserInfo().getCompany().trim());
		}
		if (null != result.getUserInfo().getLocation()) {
			mIndustryTv.setText(result.getUserInfo().getLocation().trim() + " ");
		}
		if (null != result.getUserInfo().getIndustry()) {
			mIndustryTv.setText(mIndustryTv.getText().toString() + result.getUserInfo().getIndustry().trim());
		}
		//		mLoginnameTv.setText("登录名："+getRenheApplication().getUserInfo().getEmail());
		int messageNum = result.getUserInfo().getMessageBoardNum();
		int connectionNum = result.getUserInfo().getConnectionNum();
		int followingNum = result.getUserInfo().getFollowingNum();
		int followerNum = result.getUserInfo().getFollowerNum();
		//		if (connectionNum == 0) {
		//			mContactBt.setText("联系人");
		//		} else {
		//			mContactBt.setText("联系人(" + connectionNum + ")");
		//		}
		//		if (followingNum == 0) {
		//			mAttentionBt.setText("关注");
		//		} else {
		//			mAttentionBt.setText("关注(" + followingNum + ")");
		//		}
		//		if (followerNum == 0) {
		//			mFansBt.setText("粉丝");
		//		} else {
		//			mFansBt.setText("粉丝(" + result.getUserInfo().getFollowerNum() + ")");
		//		}
		roomNumTv.setText(messageNum + "条留言");
		contactNumTv.setText(connectionNum + "位联系人");
		if (messageNum <= 0) {
			mRoomBt.setVisibility(View.GONE);
			seleparateView.setVisibility(View.GONE);
		}
		if (connectionNum <= 0) {
			mContactBt.setVisibility(View.GONE);
			seleparateView.setVisibility(View.GONE);
		}
		if (result != null && result.getUserInfo() != null) {
			SummaryInfo sInfo = result.getUserInfo().getSummaryInfo();
			mSummaryInfoLayout.removeAllViews();
			boolean isSpecialtiesNull = true;
			boolean isProfessionalNull = true;
			SpecialtiesInfo[] specialtiesInfo = result.getUserInfo().getSpecialtiesInfo();
			StringBuffer specialtiesStringBuffer = new StringBuffer();
			if (null != sInfo) {
				if (sInfo.getProfessional() != null && !TextUtils.isEmpty(sInfo.getProfessional().trim())) {
					isProfessionalNull = false;
				}
			}
			if (null != specialtiesInfo) {
				for (int i = 0; i < specialtiesInfo.length; i++) {
					if (!TextUtils.isEmpty(specialtiesInfo[i].getTitle())) {
						isSpecialtiesNull = false;
						if (i != specialtiesInfo.length - 1) {
							specialtiesStringBuffer.append(specialtiesInfo[i].getTitle().trim()
									+ MyHomeArchivesActivity.ITEM_SELEPARATOR);
						} else {
							specialtiesStringBuffer.append(specialtiesInfo[i].getTitle().trim());
						}
					}
				}
			}
			if (!isSpecialtiesNull || !isProfessionalNull) {
				findViewById(R.id.summaryLl).setVisibility(View.VISIBLE);
				View summaryInfoView = LayoutInflater.from(MyHomeArchivesActivity.this).inflate(R.layout.summary_info, null);
				if (!isProfessionalNull) {
					((LinearLayout) summaryInfoView.findViewById(R.id.self_info_ll)).setVisibility(View.VISIBLE);
					((TextView) summaryInfoView.findViewById(R.id.professionalTv)).setText(sInfo.getProfessional().trim());
				} else {
					((LinearLayout) summaryInfoView.findViewById(R.id.self_info_ll)).setVisibility(View.GONE);
					((View) summaryInfoView.findViewById(R.id.separate_line)).setVisibility(View.GONE);
				}
				if (!isSpecialtiesNull) {
					((LinearLayout) summaryInfoView.findViewById(R.id.self_profession_ll)).setVisibility(View.VISIBLE);
					((TextView) summaryInfoView.findViewById(R.id.specialtiesTv)).setText(specialtiesStringBuffer.toString());
				} else {
					((View) summaryInfoView.findViewById(R.id.separate_line)).setVisibility(View.GONE);
					((LinearLayout) summaryInfoView.findViewById(R.id.self_profession_ll)).setVisibility(View.GONE);
				}
				mSummaryInfoLayout.addView(summaryInfoView);
			}
			//			if (null != sInfo && sInfo.getSpecialties() != null && sInfo.getProfessional() != null
			//					&& (!TextUtils.isEmpty(sInfo.getSpecialties().trim()) || !TextUtils.isEmpty(sInfo.getProfessional().trim()))) {
			//				findViewById(R.id.summaryLl).setVisibility(View.VISIBLE);
			//				View summaryInfoView = LayoutInflater.from(MyHomeArchivesActivity.this).inflate(R.layout.summary_info, null);
			//				((TextView) summaryInfoView.findViewById(R.id.professionalTv)).setText(sInfo.getProfessional().trim());
			//				((TextView) summaryInfoView.findViewById(R.id.specialtiesTv)).setText(sInfo.getSpecialties().trim());
			//				mSummaryInfoLayout.addView(summaryInfoView);
			//			}
			//供求信息
			AimTagInfo[] aimTagInfo = result.getUserInfo().getAimTagInfo();
			PreferredTagInfo[] preferredTagInfo = result.getUserInfo().getPreferredTagInfo();
			boolean isAimNull = true;
			boolean isPreferNull = true;
			if (null != aimTagInfo) {
				for (AimTagInfo aInfo : aimTagInfo) {
					if (!TextUtils.isEmpty(aInfo.getTitle())) {
						isAimNull = false;
					}
				}
			}
			if (null != preferredTagInfo) {
				for (PreferredTagInfo prInfo : preferredTagInfo) {
					if (!TextUtils.isEmpty(prInfo.getTitle())) {
						isPreferNull = false;
					}
				}
			}
			if (!isAimNull || !isPreferNull) {
				findViewById(R.id.provideGetLl).setVisibility(View.VISIBLE);
				View provideGetInfoView = LayoutInflater.from(MyHomeArchivesActivity.this).inflate(R.layout.provide_get_info,
						null);
				StringBuffer aimStringBuffer = new StringBuffer();
				StringBuffer preferStringBuffer = new StringBuffer();
				if (!isAimNull) {//我想得到
					for (int i = 0; i < aimTagInfo.length; i++) {
						if (!TextUtils.isEmpty(aimTagInfo[i].getTitle())) {
							if (i != aimTagInfo.length - 1) {
								aimStringBuffer.append(aimTagInfo[i].getTitle().trim() + MyHomeArchivesActivity.ITEM_SELEPARATOR);
							} else {
								aimStringBuffer.append(aimTagInfo[i].getTitle().trim());
							}
						}
					}
					((TextView) provideGetInfoView.findViewById(R.id.specialtiesTv)).setText(aimStringBuffer.toString());
				} else {
					((View) provideGetInfoView.findViewById(R.id.separate_line)).setVisibility(View.GONE);
					((LinearLayout) provideGetInfoView.findViewById(R.id.self_profession_ll)).setVisibility(View.GONE);
					((TextView) provideGetInfoView.findViewById(R.id.specialtiesTv)).setText("无");
				}
				if (!isPreferNull) {
					for (int i = 0; i < preferredTagInfo.length; i++) {
						if (!TextUtils.isEmpty(preferredTagInfo[i].getTitle())) {
							if (i != preferredTagInfo.length - 1) {
								preferStringBuffer.append(preferredTagInfo[i].getTitle().trim()
										+ MyHomeArchivesActivity.ITEM_SELEPARATOR);
							} else {
								preferStringBuffer.append(preferredTagInfo[i].getTitle().trim());
							}
						}
					}
					((TextView) provideGetInfoView.findViewById(R.id.professionalTv)).setText(preferStringBuffer.toString());
				} else {
					((View) provideGetInfoView.findViewById(R.id.separate_line)).setVisibility(View.GONE);
					((LinearLayout) provideGetInfoView.findViewById(R.id.self_info_ll)).setVisibility(View.GONE);
					((TextView) provideGetInfoView.findViewById(R.id.professionalTv)).setText("无");
				}
				mProvideGetInfoLayout.removeAllViews();
				mProvideGetInfoLayout.addView(provideGetInfoView);
			}

			ContactInfo cInfo = result.getUserInfo().getContactInfo();
			mContactLayout.removeAllViews();
			if (null != cInfo && (result.isSelf() || result.isConnection())) {
				findViewById(R.id.contactLl).setVisibility(View.VISIBLE);
				String email = cInfo.getEmail();
				final String mobile = cInfo.getMobile();
				String qq = cInfo.getQq();
				String weixin = cInfo.getWeixin();
				String tel = cInfo.getTel();
				if (!TextUtils.isEmpty(email)) {
					View contactInfoView = LayoutInflater.from(MyHomeArchivesActivity.this).inflate(R.layout.contact_info, null);
					((TextView) contactInfoView.findViewById(R.id.titleTv)).setText("电子邮箱");
					((TextView) contactInfoView.findViewById(R.id.valueTv)).setText(email);
					if (!result.isSelf()) {
						Linkify.addLinks(((TextView) contactInfoView.findViewById(R.id.valueTv)), Linkify.EMAIL_ADDRESSES);
						stripUnderlines((TextView) contactInfoView.findViewById(R.id.valueTv));
					}
					mContactLayout.addView(contactInfoView);
				}
				if (!TextUtils.isEmpty(qq)) {
					View contactInfoView = LayoutInflater.from(MyHomeArchivesActivity.this).inflate(R.layout.contact_info, null);
					((TextView) contactInfoView.findViewById(R.id.titleTv)).setText("QQ           ");
					((TextView) contactInfoView.findViewById(R.id.valueTv)).setText(qq);
					mContactLayout.addView(contactInfoView);
				}
				if (!TextUtils.isEmpty(weixin)) {
					View contactInfoView = LayoutInflater.from(MyHomeArchivesActivity.this).inflate(R.layout.contact_info, null);
					((TextView) contactInfoView.findViewById(R.id.titleTv)).setText("微信           ");
					((TextView) contactInfoView.findViewById(R.id.valueTv)).setText(weixin);
					mContactLayout.addView(contactInfoView);
				}
				if (!TextUtils.isEmpty(tel)) {
					View contactInfoView = LayoutInflater.from(MyHomeArchivesActivity.this).inflate(R.layout.contact_info, null);
					((TextView) contactInfoView.findViewById(R.id.titleTv)).setText("固定电话");
					((TextView) contactInfoView.findViewById(R.id.valueTv)).setText(tel);
					Linkify.addLinks(((TextView) contactInfoView.findViewById(R.id.valueTv)), Linkify.PHONE_NUMBERS);
					stripUnderlines((TextView) contactInfoView.findViewById(R.id.valueTv));
					mContactLayout.addView(contactInfoView);
				}
				if (!TextUtils.isEmpty(mobile)) {
					View contactInfoView = LayoutInflater.from(MyHomeArchivesActivity.this).inflate(R.layout.contact_info, null);
					((TextView) contactInfoView.findViewById(R.id.titleTv)).setText("移动电话");
					((TextView) contactInfoView.findViewById(R.id.valueTv)).setText(mobile);
					bindPhoneTv = ((TextView) contactInfoView.findViewById(R.id.bindPhone));
					contactInfoView.findViewById(R.id.contact_seperate).setVisibility(View.GONE);
					if (result.isSelf()) {
						//如果未绑定
						if (result.getUserInfo().isBindMobile()) {
							bindPhoneTv.setVisibility(View.VISIBLE);
							bindPhoneTv.setCompoundDrawablesWithIntrinsicBounds(
									getResources().getDrawable(R.drawable.bind_phone_small), null, null, null);
							bindPhoneTv.setText("已绑定");
							contactInfoView.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View arg0) {
									//绑定手机号
									Bundle bundle = new Bundle();
									//									Intent intent = new Intent();
									bundle.putBoolean("isbind", true);
									bundle.putString("mobile", mobile);
									startActivity(BindPhoneGuideActivity.class, bundle);
								}
							});
						} else {
							bindPhoneTv.setVisibility(View.VISIBLE);
							bindPhoneTv.setText("未绑定");
							contactInfoView.setClickable(true);
							contactInfoView.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View arg0) {
									//绑定手机号
									startActivityForResult(BindPhoneGuideActivity.class, 1);
								}
							});
						}
					} else {
						Linkify.addLinks(((TextView) contactInfoView.findViewById(R.id.valueTv)), Linkify.PHONE_NUMBERS);
						stripUnderlines((TextView) contactInfoView.findViewById(R.id.valueTv));
					}
					mContactLayout.addView(contactInfoView);
				} else {
					if (result.isSelf()) {
						View contactInfoView = LayoutInflater.from(MyHomeArchivesActivity.this).inflate(R.layout.contact_info,
								null);
						contactInfoView.findViewById(R.id.contact_seperate).setVisibility(View.GONE);
						((TextView) contactInfoView.findViewById(R.id.titleTv)).setText("移动电话");
						bindPhoneTv = ((TextView) contactInfoView.findViewById(R.id.bindPhone));
						//如果未绑定
						if (result.getUserInfo().isBindMobile()) {
							bindPhoneTv.setVisibility(View.VISIBLE);
							bindPhoneTv.setCompoundDrawablesWithIntrinsicBounds(
									getResources().getDrawable(R.drawable.bind_phone_small), null, null, null);
							bindPhoneTv.setText("已绑定");
							contactInfoView.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View arg0) {
									//绑定手机号
									Bundle bundle = new Bundle();
									//									Intent intent = new Intent();
									bundle.putBoolean("isbind", true);
									bundle.putString("mobile", mobile);
									startActivity(BindPhoneGuideActivity.class, bundle);
								}
							});
						} else {
							bindPhoneTv.setVisibility(View.VISIBLE);
							bindPhoneTv.setText("未绑定");
							contactInfoView.setClickable(true);
							contactInfoView.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View arg0) {
									//绑定手机号
									startActivityForResult(BindPhoneGuideActivity.class, 1);
								}
							});
						}
						mContactLayout.addView(contactInfoView);
					}
				}

			}
			WorkExperienceInfo[] weInfos = result.getUserInfo().getWorkExperienceInfo();
			mWorkExperienceInfoLayout.removeAllViews();
			if (null != weInfos && weInfos.length > 0) {
				for (int i = 0; i < weInfos.length; i++) {
					WorkExperienceInfo weInfo = weInfos[i];
					View workExperienceInfoView = LayoutInflater.from(MyHomeArchivesActivity.this).inflate(
							R.layout.workexperience_info, null);
					if (!TextUtils.isEmpty(weInfo.getCompany()) || !TextUtils.isEmpty(weInfo.getTitle())
							|| !TextUtils.isEmpty(weInfo.getTimeInfo()) || !TextUtils.isEmpty(weInfo.getContent())) {
						findViewById(R.id.workLl).setVisibility(View.VISIBLE);
						if (!TextUtils.isEmpty(weInfo.getCompany())) {
							((TextView) workExperienceInfoView.findViewById(R.id.companyTv)).setText(weInfo.getCompany());
						} else {
							((TextView) workExperienceInfoView.findViewById(R.id.companyTv)).setVisibility(View.GONE);
						}

						if (!TextUtils.isEmpty(weInfo.getTitle())) {
							((TextView) workExperienceInfoView.findViewById(R.id.titleTv)).setText(weInfo.getTitle());
						} else {
							((TextView) workExperienceInfoView.findViewById(R.id.titleTv)).setVisibility(View.GONE);
						}

						if (!TextUtils.isEmpty(weInfo.getTimeInfo())) {
							((TextView) workExperienceInfoView.findViewById(R.id.timeInfoTv)).setText(weInfo.getTimeInfo());
						} else {
							((TextView) workExperienceInfoView.findViewById(R.id.timeInfoTv)).setVisibility(View.GONE);
						}

						if (!TextUtils.isEmpty(weInfo.getContent())) {
							((TextView) workExperienceInfoView.findViewById(R.id.contentTv)).setText(weInfo.getContent());
						} else {
							((TextView) workExperienceInfoView.findViewById(R.id.contentTv)).setVisibility(View.GONE);
						}

						mWorkExperienceInfoLayout.addView(workExperienceInfoView);
						//						if (i + 1 < weInfos.length) {
						//							View view = new View(MyHomeArchivesActivity.this);
						//							LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						//									LinearLayout.LayoutParams.FILL_PARENT, 1);
						//							params.topMargin = 5;
						//							params.bottomMargin = 10;
						//							view.setLayoutParams(params);
						//							view.setBackgroundColor(getResources().getColor(R.color.colorCccccc));
						//							mWorkExperienceInfoLayout.addView(view);
						//						}
					}
				}
			}
			EduExperienceInfo[] edInfos = result.getUserInfo().getEduExperienceInfo();
			mEduExperienceInfoLayout.removeAllViews();
			if (null != edInfos && edInfos.length > 0) {
				for (int i = 0; i < edInfos.length; i++) {
					EduExperienceInfo edInfo = edInfos[i];
					if (!TextUtils.isEmpty(edInfo.getSchoolName()) || !TextUtils.isEmpty(edInfo.getStudyField())
							|| !TextUtils.isEmpty(edInfo.getTimeInfo())) {
						findViewById(R.id.eduLl).setVisibility(View.VISIBLE);
						View eduExperienceInfoView = LayoutInflater.from(MyHomeArchivesActivity.this).inflate(
								R.layout.eduexperience_info, null);

						if (!TextUtils.isEmpty(edInfo.getSchoolName())) {
							((TextView) eduExperienceInfoView.findViewById(R.id.schoolNameTv)).setText(edInfo.getSchoolName());
						} else {
							((TextView) eduExperienceInfoView.findViewById(R.id.schoolNameTv)).setVisibility(View.GONE);
						}

						if (!TextUtils.isEmpty(edInfo.getStudyField())) {
							((TextView) eduExperienceInfoView.findViewById(R.id.studyFieldTv)).setText(edInfo.getStudyField());
						} else {
							((TextView) eduExperienceInfoView.findViewById(R.id.studyFieldTv)).setVisibility(View.GONE);
						}

						if (!TextUtils.isEmpty(edInfo.getTimeInfo())) {
							((TextView) eduExperienceInfoView.findViewById(R.id.timeInfoTv)).setText(edInfo.getTimeInfo());
						} else {
							((TextView) eduExperienceInfoView.findViewById(R.id.timeInfoTv)).setVisibility(View.GONE);
						}

						mEduExperienceInfoLayout.addView(eduExperienceInfoView);
						//						if (i + 1 < edInfos.length) {
						//							View view = new View(MyHomeArchivesActivity.this);
						//							LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						//									LinearLayout.LayoutParams.FILL_PARENT, 1);
						//							params.topMargin = 5;
						//							params.bottomMargin = 10;
						//							view.setLayoutParams(params);
						//							view.setBackgroundColor(getResources().getColor(R.color.colorCccccc));
						//							mEduExperienceInfoLayout.addView(view);
						//						}
					}
				}
			}
			OtherInfo oInfo = result.getUserInfo().getOtherInfo();
			mOtherInfoLayout.removeAllViews();
			if (null != oInfo) {
				String associations = oInfo.getAssociations();
				String interest = oInfo.getInterests();
				String awards = oInfo.getAwards();
				String webProfileUrl = oInfo.getWebProfileUrl();
				Site[] sites = oInfo.getSiteList();
				if (!TextUtils.isEmpty(associations)) {
					findViewById(R.id.otherLl).setVisibility(View.VISIBLE);
					View otherInfoView = LayoutInflater.from(MyHomeArchivesActivity.this).inflate(R.layout.other_info, null);
					((TextView) otherInfoView.findViewById(R.id.titleTv)).setText("组织");
					((TextView) otherInfoView.findViewById(R.id.valueTv)).setText(associations);
					mOtherInfoLayout.addView(otherInfoView);
				}
				if (!TextUtils.isEmpty(interest)) {
					findViewById(R.id.otherLl).setVisibility(View.VISIBLE);
					View otherInfoView = LayoutInflater.from(MyHomeArchivesActivity.this).inflate(R.layout.other_info, null);
					((TextView) otherInfoView.findViewById(R.id.titleTv)).setText("兴趣爱好");
					((TextView) otherInfoView.findViewById(R.id.valueTv)).setText(interest);
					mOtherInfoLayout.addView(otherInfoView);
				}
				if (!TextUtils.isEmpty(awards)) {
					findViewById(R.id.otherLl).setVisibility(View.VISIBLE);
					View otherInfoView = LayoutInflater.from(MyHomeArchivesActivity.this).inflate(R.layout.other_info, null);
					((TextView) otherInfoView.findViewById(R.id.titleTv)).setText("奖励、荣誉");
					((TextView) otherInfoView.findViewById(R.id.valueTv)).setText(awards);
					mOtherInfoLayout.addView(otherInfoView);
				}
				if (!TextUtils.isEmpty(webProfileUrl) || (null != sites && sites.length > 0)) {
					findViewById(R.id.otherLl).setVisibility(View.VISIBLE);
					View siteInfoView = LayoutInflater.from(MyHomeArchivesActivity.this).inflate(R.layout.site_info, null);
					if (!TextUtils.isEmpty(webProfileUrl)) {
						View siteUrlInfoView = LayoutInflater.from(MyHomeArchivesActivity.this).inflate(R.layout.siteurl_info,
								null);
						((TextView) siteUrlInfoView.findViewById(R.id.titleTv)).setText("网络档案");
						((TextView) siteUrlInfoView.findViewById(R.id.valueTv)).setText(webProfileUrl);
						Linkify.addLinks(((TextView) siteUrlInfoView.findViewById(R.id.valueTv)), Linkify.WEB_URLS);
						stripUnderlines((TextView) siteUrlInfoView.findViewById(R.id.valueTv));
						((LinearLayout) siteInfoView.findViewById(R.id.siteLayout)).addView(siteUrlInfoView);
					}
					if (null != sites) {
						for (Site site : sites) {
							View siteUrlInfoView = LayoutInflater.from(MyHomeArchivesActivity.this).inflate(
									R.layout.siteurl_info, null);
							String siteType = site.getSiteType();
							if (!TextUtils.isEmpty(siteType) && (siteType.trim().endsWith(":")) || siteType.trim().endsWith("：")) {
								siteType = siteType.substring(0, siteType.length() - 1);
							}
							((TextView) siteUrlInfoView.findViewById(R.id.titleTv)).setText(siteType);
							((TextView) siteUrlInfoView.findViewById(R.id.valueTv)).setText(site.getSiteUrl());
							Linkify.addLinks(((TextView) siteUrlInfoView.findViewById(R.id.valueTv)), Linkify.WEB_URLS);
							stripUnderlines((TextView) siteUrlInfoView.findViewById(R.id.valueTv));
							((LinearLayout) siteInfoView.findViewById(R.id.siteLayout)).addView(siteUrlInfoView);
						}
					}
					mOtherInfoLayout.addView(siteInfoView);
				}
			}
		}
		result = null;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderIcon(android.R.drawable.ic_menu_more);
		menu.setHeaderTitle("请选择");
		menu.add(0, 0, 0, "通过站内信分享");
		menu.add(0, 1, 0, "通过手机短信分享");
		menu.add(0, 2, 0, "通过email分享");
		menu.add(0, 3, 0, "发送给微信好友");
		// menu.add(1, 3, 0, R.id.follow_btn);
	}

	// 选择本地图片回调标识
	public static final int REQUEST_CODE_CHOOSE_PICTURE = 1001;
	public static final int REQUEST_CODE_CAPTURE_CUT = 1003;
	public static final int REQUEST_CODE_CHOOSE_CAPTURE = 1004;
	private String mFilePath = "";

	/**
	 * 启动照相机
	 */
	private void startCamera() {
		try {
			// 获取照片保存的文件路径
			mFilePath = ExternalStorageUtil.getPicCacheDataPath(this, getRenheApplication().getUserInfo().getEmail());
			String lFileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
			mFilePath = mFilePath + File.separator + lFileName + ".jpg";
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			Uri uri = Uri.fromFile(new File(mFilePath));
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
			startActivityForResult(intent, REQUEST_CODE_CHOOSE_CAPTURE);
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	/**
	 * 启动照片浏览
	 */
	private void startGallery() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		startActivityForResult(Intent.createChooser(intent, "请选择照片"), REQUEST_CODE_CHOOSE_PICTURE);
	}


	private void sendMail() {
		if (mProfile != null && mProfile.getUserInfo() != null && mProfile.getUserInfo().getContactInfo() != null) {
			Uri uri = Uri.parse("mailto:" + mProfile.getUserInfo().getContactInfo().getEmail());
			Intent email = new Intent(Intent.ACTION_SENDTO, uri);
			startActivity(email);
		} else {
			ToastUtil.showToast(this, "无邮件箱信息");
		}
	}

	private void callMobile() {
		if (mProfile != null && mProfile.getUserInfo() != null && mProfile.getUserInfo().getContactInfo() != null) {
			Intent myIntentDial = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
					+ mProfile.getUserInfo().getContactInfo().getMobile()));
			startActivity(myIntentDial);
		} else {
			ToastUtil.showToast(this, "无电话号码信息");
		}
	}

	private void callLetter() {
		if (mProfile != null && mProfile.getUserInfo() != null) {
			Bundle bundle = new Bundle();
			// bundle.putString("sid", mOtherSid);
			// bundle.putString("name", mProfile.getUserInfo().getName());
			bundle.putString("share", mProfile.getUserInfo().getName());
			bundle.putString("sharesid", mOtherSid);
			startActivity(SendInnerMsgActivity.class, bundle);
		}
	}
	@Override
	protected void initListener() {
		super.initListener();
		mAvatarImgV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(MyHomeArchivesActivity.this).setTitle("设置头像")
						.setItems(new String[] { "相机拍摄", "手机相册" }, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								switch (which) {
								case 0:
									startCamera();
									break;
								case 1:
									startGallery();
									break;
								}
							}
						}).create().show();
			}
		});

//		mSharetoBt.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				//				mNShareBt.performClick();
//				Intent intent = new Intent();
//				intent.putExtra("profile", mProfile);
//				intent.putExtra("othersid", mOtherSid);
//				intent.putExtra("userName", userName);
//				intent.putExtra("userDesp", userDesp);
//				intent.putExtra("userFaceUrl", userFaceUrl);
//				intent.putExtra("isFromArchieve", true);
//				intent.setClass(MyHomeArchivesActivity.this, DialogActivity.class);
//				MyHomeArchivesActivity.this.startActivity(intent);
//				overridePendingTransition(R.anim.push_bottom_in, R.anim.push_bottom_out);
//			}
//		});
		// registerForContextMenu(mSharetoBt);
		mRoomBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mProfile != null) {
					//					if (mProfile.isSelf()) {
					//						startActivity(RoomsActivity.class);
					//					} else {
					Intent intent = new Intent(MyHomeArchivesActivity.this, MessageBoardActivity.class);
					intent.putExtra("type", 6);
					intent.putExtra("viewSid", mOtherSid);
					if (mProfile.isConnection()) {
						intent.putExtra("friendName", mProfile.getUserInfo().getName());
					}
					startActivity(intent);
					overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
					//					}
				}
			}
		});
		mContactBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mProfile != null) {
					if (mProfile.isSelf() || mProfile.isConnection() || RenheApplication.getInstance().getAccountType() > 0) {
						Bundle bundle = new Bundle();
						bundle.putString("sid", mOtherSid);
						if (mProfile.isConnection()) {
							bundle.putString("friendName", mProfile.getUserInfo().getName());
						}
						startActivity(SearchContactsActivity.class, bundle);
					}
				}

			}
		});
		newEditButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (mProfile != null) {
					if (mProfile.isSelf()) {
						//						String url = Constants.Http.EDITOR_MEMBER;
						//						Intent intent = new Intent(Intent.ACTION_VIEW);
						//						intent.setData(Uri.parse(url));
						//						startActivity(intent);
						Intent intent = new Intent(MyHomeArchivesActivity.this, EditMyHomeArchivesActivity.class);
						intent.putExtra("Profile", mProfile);
						if (isUpdateAvatar) {
							intent.putExtra("isUpdateAvatar", isUpdateAvatar);
							Bundle extras = new Bundle();
							extras.putParcelable("avarimage", updatedBitmap);
							intent.putExtras(extras);
						}
						startActivity(intent);
						overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

					} else if (mProfile.isConnection()) {
						if (mProfile != null && mProfile.getUserInfo() != null) {
							Bundle bundle = new Bundle();
							bundle.putString("sid", mOtherSid);
							bundle.putString("name", mProfile.getUserInfo().getName());
							startActivity(SendInnerMsgActivity.class, bundle);
							overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
						}
					} else if (mProfile.isInvite()) {

					} else if (!mProfile.isInvite()) {
						new AddFriendTask(MyHomeArchivesActivity.this) {

							@Override
							public void doPre() {
								showDialog(3);
							}

							@Override
							public void doPost(AddFriend result) {
								removeDialog(3);
								if (result == null) {
									ToastUtil.showErrorToast(MyHomeArchivesActivity.this, "连接服务器失败！");
								} else if (result.getState() == 1) {
									newEditButton.setText("已发送好友请求");
									newEditButton.setEnabled(false);
									newEditButton.setBackgroundResource(R.drawable.archieve_edit_bt_p_shape);
									ToastUtil.showToast(MyHomeArchivesActivity.this, "好友请求发送成功");
								} else if (result.getState() == -1) {
									ToastUtil.showErrorToast(MyHomeArchivesActivity.this, "权限不足！");
								} else if (result.getState() == -2) {
									ToastUtil.showErrorToast(MyHomeArchivesActivity.this, "发生未知错误！");
								} else if (result.getState() == -3) {
									ToastUtil.showErrorToast(MyHomeArchivesActivity.this, "被添加好友的会员sid参数有误！");
								} else if (result.getState() == -4) {
									ToastUtil.showErrorToast(MyHomeArchivesActivity.this, "10天内已发出过添加好友邀请,请勿重复发送加好友请求！");
								} else if (result.getState() == -5) {
									ToastUtil.showErrorToast(MyHomeArchivesActivity.this, "已经是好友了");
								} else if (result.getState() == -6) {
									ToastUtil.showErrorToast(MyHomeArchivesActivity.this, "手机客户端超过每日加好友的限制(目前限制为50个)");
								} else if (result.getState() == -7) {
									ToastUtil.showErrorToast(MyHomeArchivesActivity.this, "超过每日添加好友的数量");
								} else if (result.getState() == -8) {
									ToastUtil.showErrorToast(MyHomeArchivesActivity.this, "添加好友者的好友数量超过了会员等级限制");
								} else if (result.getState() == -9) {
									ToastUtil.showErrorToast(MyHomeArchivesActivity.this, "相同账号好友数量超过了好友上限");
								} else if (result.getState() == -10) {
									ToastUtil.showErrorToast(MyHomeArchivesActivity.this, "只允许通过朋友引荐添加好友");
								} else if (result.getState() == -11) {
									ToastUtil.showErrorToast(MyHomeArchivesActivity.this, "只允许实名认证的会员添加好友");
								} else if (result.getState() == -12) {
									ToastUtil.showErrorToast(MyHomeArchivesActivity.this, "只允许付费会员添加好友");
								}
							}

						}.execute(mOtherSid);
					}
				}

			}
		});
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 1:
			ProgressDialog findPd = new ProgressDialog(this);
			findPd.setMessage("正在上传头像...");
			//			findPd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			findPd.setCanceledOnTouchOutside(false);
			return findPd;
		case 2:
			ProgressDialog findPd2 = new ProgressDialog(this);
			findPd2.setMessage("数据加载中...");
			//			findPd2.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			findPd2.setCanceledOnTouchOutside(false);
			return findPd2;
		case 3:
			ProgressDialog findPd3 = new ProgressDialog(this);
			findPd3.setMessage("好友请求正在发送中...");
			//			findPd3.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			findPd3.setCanceledOnTouchOutside(false);
			return findPd3;
		default:
			return null;
		}
	}

	Handler handler = new Handler();
	Runnable run = new Runnable() {
		@Override
		public void run() {
			logoutFlag = false;
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void stripUnderlines(TextView textView) {
		if(textView.getText() instanceof Spannable){
			Spannable s = (Spannable) textView.getText();
			URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
			for (URLSpan span : spans) {
				int start = s.getSpanStart(span);
				int end = s.getSpanEnd(span);
				s.removeSpan(span);
				span = new URLSpanNoUnderline(span.getURL());
				s.setSpan(span, start, end, 0);
			}
			textView.setText(s);
		}
	}

	//需要一个自定义的URLSpan，不用启动TextPaint的“下划线”属性:

	private class URLSpanNoUnderline extends URLSpan {
		public URLSpanNoUnderline(String url) {
			super(url);
		}

		@Override
		public void updateDrawState(TextPaint ds) {
			super.updateDrawState(ds);
			ds.setUnderlineText(false);
		}
	}

}
