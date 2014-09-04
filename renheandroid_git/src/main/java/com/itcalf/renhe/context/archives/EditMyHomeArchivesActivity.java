package com.itcalf.renhe.context.archives;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.itcalf.renhe.R;
import com.itcalf.renhe.RenheApplication;
import com.itcalf.renhe.cache.CacheManager;
import com.itcalf.renhe.context.archives.edit.EditContactInfo;
import com.itcalf.renhe.context.archives.edit.EditEduInfo;
import com.itcalf.renhe.context.archives.edit.EditOtherInfo;
import com.itcalf.renhe.context.archives.edit.EditProvideGetInfo;
import com.itcalf.renhe.context.archives.edit.EditSelfInfo;
import com.itcalf.renhe.context.archives.edit.EditSummaryInfo;
import com.itcalf.renhe.context.archives.edit.EditWorkInfo;
import com.itcalf.renhe.context.register.BindPhoneGuideActivity;
import com.itcalf.renhe.context.template.ActivityTemplate;
import com.itcalf.renhe.context.template.BaseActivity;
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
import com.itcalf.renhe.utils.ToastUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Feature:个人档案界面 Desc:个人档案界面
 * 
 * @author xp
 * 
 */
public class EditMyHomeArchivesActivity extends BaseActivity {

	public static String FLAG_INTENT_DATA = "profileSid";
	//登出标识（两次按下返回退出时的标识字段）
	private boolean logoutFlag;
	// private LinearLayout mLayoutAttention;//关注
	// private LinearLayout mLayoutRoom;//客厅
	// private LinearLayout mLayoutVermicelli ;//粉丝
	// private LinearLayout mLayoutContact ;//联系人
	private ImageView mBackBt;
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
	private LinearLayout mProvideGetInfoLayout;// 提供、得到
	private LinearLayout mEduExperienceInfoLayout;// 教育经历
	private LinearLayout mWorkExperienceInfoLayout;// 工作经历
	private LinearLayout mOtherInfoLayout;// 其他信息

	private LinearLayout mAddContactLayout;// 联系信息
	private LinearLayout mAddSummaryInfoLayout;// 概要信息
	private LinearLayout mAddEduExperienceInfoLayout;// 教育经历
	private LinearLayout mAddWorkExperienceInfoLayout;// 工作经历
	private LinearLayout mAddOtherInfoLayout;// 其他信息

	//	private LinearLayout mUserLayout;
	private ScrollView mScrollView;
	private String mOtherSid;
	private Profile mProfile;
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

	private LinearLayout selfInfoLl;

	private static final int PROFESSIONAL_REQUEST_CODE = 10;
	private static final int SPECIALTIES_REQUEST_CODE = 11;
	private static final int PROVIDE_REQUEST_CODE = 12;
	private static final int GET_REQUEST_CODE = 13;
	private static final int ADDWORK_REQUEST_CODE = 14;
	private static final int EDITWORK_REQUEST_CODE = 15;
	private static final int ADDEDU_REQUEST_CODE = 16;
	private static final int EDITEDU_REQUEST_CODE = 17;
	private static final int EDITSELF_REQUEST_CODE = 18;
	private static final int EDITORGANSITION_REQUEST_CODE = 19;
	private static final int EDITINTEREST_REQUEST_CODE = 20;
	private static final int EDITAWARD_REQUEST_CODE = 21;
	private static final int EDITCONTACT_REQUEST_CODE = 22;
	private static final int EDITWEBSITE_REQUEST_CODE = 23;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		RenheApplication.getInstance().addActivity(this);
		new ActivityTemplate().doInActivity(this, R.layout.archives_edit);
	}

	@Override
	protected void findView() {
		super.findView();

		mBackBt = (ImageView) findViewById(R.id.backBt);
		mAvatarImgV = (ImageView) findViewById(R.id.avatarImage);
		mAvatarImgV.setEnabled(false);
		mNameTv = (TextView) findViewById(R.id.nameTv);
		//		mLoginnameTv = (TextView) findViewById(R.id.loginnameTv);
		mCompanyTv = (TextView) findViewById(R.id.companyTv);
		mIndustryTv = (TextView) findViewById(R.id.industryTv);
		mContactLayout = (LinearLayout) findViewById(R.id.contactLayout);
		mSummaryInfoLayout = (LinearLayout) findViewById(R.id.summaryInfoLayout);
		mProvideGetInfoLayout = (LinearLayout) findViewById(R.id.provideGetInfoLayout);
		mEduExperienceInfoLayout = (LinearLayout) findViewById(R.id.eduExperienceInfoLayout);
		mWorkExperienceInfoLayout = (LinearLayout) findViewById(R.id.workExperienceInfoLayout);
		mOtherInfoLayout = (LinearLayout) findViewById(R.id.otherInfoLayout);
		mRightImage = (ImageView) findViewById(R.id.rightImage);
		mVipImage = (ImageView) findViewById(R.id.vipImage);
		mRealNameImage = (ImageView) findViewById(R.id.realnameImage);
		//		mUserLayout = (LinearLayout) findViewById(R.id.layout2);
		mScrollView = (ScrollView) findViewById(R.id.scrollView);
		rootRl = (RelativeLayout) findViewById(R.id.rootRl);
		selfInfoLl = (LinearLayout) findViewById(R.id.layout01);

		mAddSummaryInfoLayout = (LinearLayout) findViewById(R.id.addSummaryInfoLayout);
		mAddWorkExperienceInfoLayout = (LinearLayout) findViewById(R.id.addWorkExperienceInfoLayout);
		mAddEduExperienceInfoLayout = (LinearLayout) findViewById(R.id.addEduExperienceInfoLayout);
		mAddOtherInfoLayout = (LinearLayout) findViewById(R.id.addOtherInfoLayout);
		mAddContactLayout = (LinearLayout) findViewById(R.id.addcontactInfoLayout);
	}

	@Override
	protected void initData() {
		super.initData();
		setTextValue(R.id.title_txt, "更新档案");
		mOtherSid = getIntent().getStringExtra(FLAG_INTENT_DATA);
		mOtherSid = getRenheApplication().getUserInfo().getSid();

		Profile pf = (Profile) getIntent().getSerializableExtra("Profile");
		if (null != pf) {
			populateData(pf);
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
		mAvatarImgV.setEnabled(true);
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
//		AsyncImageLoader
//				.getInstance()
//				.populateData(EditMyHomeArchivesActivity.this, getRenheApplication().getUserInfo().getEmail(), false, true, true)
//				.loadPic(mAvatarImgV, mOtherSid, result.getUserInfo().getUserface(),
//						(int) getResources().getDimension(R.dimen.renhe_archieve_image_wh),
//						(int) getResources().getDimension(R.dimen.renhe_archieve_image_wh));
		if(getIntent().getBooleanExtra("isUpdateAvatar", false)){
			Bundle extras = getIntent().getExtras();
			 Bitmap bitmap = extras.getParcelable("avarimage");
			if (null != bitmap) {
				mAvatarImgV.setImageBitmap(bitmap);
			}
		}else{
			ImageLoader imageLoader = ImageLoader.getInstance();		
			try {
				imageLoader.displayImage(result.getUserInfo().getUserface(), mAvatarImgV, CacheManager.options,  CacheManager.animateFirstDisplayListener);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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
		if (result != null && result.getUserInfo() != null) {
			//编辑概要信息
			final SummaryInfo sInfo = result.getUserInfo().getSummaryInfo();
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
							specialtiesStringBuffer.append(specialtiesInfo[i].getTitle().trim() + MyHomeArchivesActivity.ITEM_SELEPARATOR);
						} else {
							specialtiesStringBuffer.append(specialtiesInfo[i].getTitle().trim());
						}
					}
				}
			}
			findViewById(R.id.summaryLl).setVisibility(View.VISIBLE);
			View summaryInfoView = LayoutInflater.from(EditMyHomeArchivesActivity.this).inflate(R.layout.summary_info, null);
			if (!isSpecialtiesNull) {
				((TextView) summaryInfoView.findViewById(R.id.specialtiesTv)).setText(specialtiesStringBuffer.toString());
			} else {
//				((TextView) summaryInfoView.findViewById(R.id.specialtiesTv)).setText("无");
				((TextView) summaryInfoView.findViewById(R.id.specialtiesTv)).setVisibility(View.GONE);
				((LinearLayout) summaryInfoView.findViewById(R.id.addProfessionInfoLayout)).setVisibility(View.VISIBLE);
			}
			if (!isProfessionalNull) {
				((TextView) summaryInfoView.findViewById(R.id.professionalTv)).setText(sInfo.getProfessional().trim());
			} else {
//				((TextView) summaryInfoView.findViewById(R.id.professionalTv)).setText("无");
				((TextView) summaryInfoView.findViewById(R.id.professionalTv)).setVisibility(View.GONE);
				((LinearLayout) summaryInfoView.findViewById(R.id.addSelfInfoLayout)).setVisibility(View.VISIBLE);
			
			}
			mSummaryInfoLayout.addView(summaryInfoView);
			LinearLayout selfInfoLl = (LinearLayout) summaryInfoView.findViewById(R.id.self_info_ll);
			LinearLayout selfProfessionLl = (LinearLayout) summaryInfoView.findViewById(R.id.self_profession_ll);
			selfInfoLl.setClickable(true);
			selfProfessionLl.setClickable(true);
			selfInfoLl.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					//						mAddSummaryInfoLayout.performClick();
					Intent intent = new Intent(EditMyHomeArchivesActivity.this, EditSummaryInfo.class);
					intent.putExtra("toSpecialties", true);
					if (null != sInfo && sInfo.getProfessional() != null && !TextUtils.isEmpty(sInfo.getProfessional().trim())) {
						intent.putExtra("professionals", sInfo.getProfessional().trim());
					}
					intent.putExtra("Profile", mProfile);
					startActivityForResult(intent, PROFESSIONAL_REQUEST_CODE);
					overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
				}
			});
			selfProfessionLl.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					//						mAddSummaryInfoLayout.performClick();
					Intent intent = new Intent(EditMyHomeArchivesActivity.this, EditSummaryInfo.class);
					intent.putExtra("toProfessional", true);
					intent.putExtra("Profile", mProfile);
					startActivityForResult(intent, SPECIALTIES_REQUEST_CODE);
					overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
				}
			});

			//供求信息
			final AimTagInfo[] aimTagInfo = result.getUserInfo().getAimTagInfo();
			final PreferredTagInfo[] preferredTagInfo = result.getUserInfo().getPreferredTagInfo();
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
			//			if(!isAimNull || !isPreferNull){
			findViewById(R.id.provideGetLl).setVisibility(View.VISIBLE);
			View provideGetInfoView = LayoutInflater.from(EditMyHomeArchivesActivity.this).inflate(R.layout.provide_get_info,
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
//				((TextView) provideGetInfoView.findViewById(R.id.specialtiesTv)).setText("无");
				((TextView) provideGetInfoView.findViewById(R.id.specialtiesTv)).setVisibility(View.GONE);
				((LinearLayout) provideGetInfoView.findViewById(R.id.addProfessionInfoLayout)).setVisibility(View.VISIBLE);
			}
			if (!isPreferNull) {
				for (int i = 0; i < preferredTagInfo.length; i++) {
					if (!TextUtils.isEmpty(preferredTagInfo[i].getTitle())) {
						if (i != preferredTagInfo.length - 1) {
							preferStringBuffer.append(preferredTagInfo[i].getTitle().trim() + MyHomeArchivesActivity.ITEM_SELEPARATOR);
						} else {
							preferStringBuffer.append(preferredTagInfo[i].getTitle().trim());
						}
					}
				}
				((TextView) provideGetInfoView.findViewById(R.id.professionalTv)).setText(preferStringBuffer.toString());
			} else {
//				((TextView) provideGetInfoView.findViewById(R.id.professionalTv)).setText("无");
				((TextView) provideGetInfoView.findViewById(R.id.professionalTv)).setVisibility(View.GONE);
				((LinearLayout) provideGetInfoView.findViewById(R.id.addSelfInfoLayout)).setVisibility(View.VISIBLE);
			}
			mProvideGetInfoLayout.removeAllViews();
			mProvideGetInfoLayout.addView(provideGetInfoView);
			LinearLayout provideInfoLl = (LinearLayout) provideGetInfoView.findViewById(R.id.self_info_ll);
			LinearLayout getLl = (LinearLayout) provideGetInfoView.findViewById(R.id.self_profession_ll);
			provideInfoLl.setClickable(true);
			getLl.setClickable(true);
			provideInfoLl.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(EditMyHomeArchivesActivity.this, EditProvideGetInfo.class);
					intent.putExtra("toProvide", true);
					intent.putExtra("Profile", mProfile);
					startActivityForResult(intent, PROVIDE_REQUEST_CODE);
					overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
				}
			});
			getLl.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(EditMyHomeArchivesActivity.this, EditProvideGetInfo.class);
					intent.putExtra("toGet", true);
					intent.putExtra("Profile", mProfile);
					startActivityForResult(intent, GET_REQUEST_CODE);
					overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
				}
			});
			//			}

			//编辑联系方式
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
					View contactInfoView = LayoutInflater.from(EditMyHomeArchivesActivity.this).inflate(R.layout.contact_info,
							null);
					((TextView) contactInfoView.findViewById(R.id.titleTv)).setText("电子邮箱");
					((TextView) contactInfoView.findViewById(R.id.valueTv)).setText(email);
					((TextView) contactInfoView.findViewById(R.id.valueTv)).setTextColor(getResources()
							.getColor(R.color.color777));
					if (!result.isSelf()) {
//						Linkify.addLinks(((TextView) contactInfoView.findViewById(R.id.valueTv)), Linkify.EMAIL_ADDRESSES);
					}
					mContactLayout.addView(contactInfoView);
					LinearLayout contactLayout = (LinearLayout) contactInfoView.findViewById(R.id.main_ll);
					contactLayout.setClickable(false);
					//					contactLayout.setOnClickListener(new OnClickListener() {
					//						
					//						@Override
					//						public void onClick(View arg0) {
					//							mAddContactLayout.performClick();
					//						}
					//					});
				}
				if (!TextUtils.isEmpty(qq)) {
					View contactInfoView = LayoutInflater.from(EditMyHomeArchivesActivity.this).inflate(R.layout.contact_info,
							null);
					((TextView) contactInfoView.findViewById(R.id.titleTv)).setText("QQ           ");
					((TextView) contactInfoView.findViewById(R.id.valueTv)).setText(qq);
					mContactLayout.addView(contactInfoView);
					LinearLayout contactLayout = (LinearLayout) contactInfoView.findViewById(R.id.main_ll);
					contactLayout.setClickable(true);
					contactLayout.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							mAddContactLayout.performClick();
						}
					});
				}
				if (!TextUtils.isEmpty(weixin)) {
					View contactInfoView = LayoutInflater.from(EditMyHomeArchivesActivity.this).inflate(R.layout.contact_info,
							null);
					((TextView) contactInfoView.findViewById(R.id.titleTv)).setText("微信           ");
					((TextView) contactInfoView.findViewById(R.id.valueTv)).setText(weixin);
					mContactLayout.addView(contactInfoView);
					LinearLayout contactLayout = (LinearLayout) contactInfoView.findViewById(R.id.main_ll);
					contactLayout.setClickable(true);
					contactLayout.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							mAddContactLayout.performClick();
						}
					});
				}
				if (!TextUtils.isEmpty(tel)) {
					View contactInfoView = LayoutInflater.from(EditMyHomeArchivesActivity.this).inflate(R.layout.contact_info,
							null);
					((TextView) contactInfoView.findViewById(R.id.titleTv)).setText("固定电话");
					((TextView) contactInfoView.findViewById(R.id.valueTv)).setText(tel);
//					Linkify.addLinks(((TextView) contactInfoView.findViewById(R.id.valueTv)), Linkify.PHONE_NUMBERS);
					mContactLayout.addView(contactInfoView);
					LinearLayout contactLayout = (LinearLayout) contactInfoView.findViewById(R.id.main_ll);
					contactLayout.setClickable(true);
					contactLayout.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							mAddContactLayout.performClick();
						}
					});
				}
				if (!TextUtils.isEmpty(mobile)) {
					View contactInfoView = LayoutInflater.from(EditMyHomeArchivesActivity.this).inflate(R.layout.contact_info,
							null);
					((TextView) contactInfoView.findViewById(R.id.titleTv)).setText("移动电话");
					((TextView) contactInfoView.findViewById(R.id.valueTv)).setText(mobile);
					bindPhoneTv = ((TextView) contactInfoView.findViewById(R.id.bindPhone));
					contactInfoView.findViewById(R.id.contact_seperate).setVisibility(View.GONE);//默认手机是显示在最后一项，隐藏掉分割线，保持联系方式外边的方框上下边界颜色统一
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
//						Linkify.addLinks(((TextView) contactInfoView.findViewById(R.id.valueTv)), Linkify.PHONE_NUMBERS);
					}
					mContactLayout.addView(contactInfoView);
				} else {
					if (result.isSelf()) {
						View contactInfoView = LayoutInflater.from(EditMyHomeArchivesActivity.this).inflate(
								R.layout.contact_info, null);
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
			//工作经历
			findViewById(R.id.workLl).setVisibility(View.VISIBLE);
			WorkExperienceInfo[] weInfos = result.getUserInfo().getWorkExperienceInfo();
			mWorkExperienceInfoLayout.removeAllViews();
			if (null != weInfos && weInfos.length > 0) {
				for (int i = 0; i < weInfos.length; i++) {
					final WorkExperienceInfo weInfo = weInfos[i];
					View workExperienceInfoView = LayoutInflater.from(EditMyHomeArchivesActivity.this).inflate(
							R.layout.workexperience_info, null);
					if (!TextUtils.isEmpty(weInfo.getCompany()) || !TextUtils.isEmpty(weInfo.getTitle())
							|| !TextUtils.isEmpty(weInfo.getTimeInfo()) || !TextUtils.isEmpty(weInfo.getContent())) {

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
						LinearLayout workLayout = (LinearLayout) workExperienceInfoView.findViewById(R.id.main_ll);
						workLayout.setClickable(true);
						workLayout.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								//								mAddWorkExperienceInfoLayout.performClick();
								Intent intent = new Intent(EditMyHomeArchivesActivity.this, EditWorkInfo.class);
								intent.putExtra("work", weInfo);
								startActivityForResult(intent, EDITWORK_REQUEST_CODE);
								overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
							}
						});
					}
				}
			}
			//教育经历
			findViewById(R.id.eduLl).setVisibility(View.VISIBLE);
			EduExperienceInfo[] edInfos = result.getUserInfo().getEduExperienceInfo();
			mEduExperienceInfoLayout.removeAllViews();
			if (null != edInfos && edInfos.length > 0) {
				for (int i = 0; i < edInfos.length; i++) {
					final EduExperienceInfo edInfo = edInfos[i];
					if (!TextUtils.isEmpty(edInfo.getSchoolName()) || !TextUtils.isEmpty(edInfo.getStudyField())
							|| !TextUtils.isEmpty(edInfo.getTimeInfo())) {

						View eduExperienceInfoView = LayoutInflater.from(EditMyHomeArchivesActivity.this).inflate(
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
						LinearLayout eduLayout = (LinearLayout) eduExperienceInfoView.findViewById(R.id.main_ll);
						eduLayout.setClickable(true);
						eduLayout.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								//								mAddEduExperienceInfoLayout.performClick();
								Intent intent = new Intent(EditMyHomeArchivesActivity.this, EditEduInfo.class);
								intent.putExtra("edu", edInfo);
								startActivityForResult(intent, EDITEDU_REQUEST_CODE);
								overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
							}
						});
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
				findViewById(R.id.otherLl).setVisibility(View.VISIBLE);
				//组织
				View otherInfoViewAssociations = LayoutInflater.from(EditMyHomeArchivesActivity.this).inflate(
						R.layout.other_info, null);
				((TextView) otherInfoViewAssociations.findViewById(R.id.titleTv)).setText("组织");
				if (!TextUtils.isEmpty(associations)) {
					((TextView) otherInfoViewAssociations.findViewById(R.id.valueTv)).setText(associations);
				} else {
//					((TextView) otherInfoViewAssociations.findViewById(R.id.valueTv)).setText("无");
					((TextView) otherInfoViewAssociations.findViewById(R.id.valueTv)).setVisibility(View.GONE);
					((LinearLayout) otherInfoViewAssociations.findViewById(R.id.addSelfInfoLayout)).setVisibility(View.VISIBLE);
				
				}
				mOtherInfoLayout.addView(otherInfoViewAssociations);
				LinearLayout otherLayoutAssociations = (LinearLayout) otherInfoViewAssociations.findViewById(R.id.main_ll);
				otherLayoutAssociations.setClickable(true);
				otherLayoutAssociations.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						//						mAddOtherInfoLayout.performClick();
						Intent intent = new Intent(EditMyHomeArchivesActivity.this, EditOtherInfo.class);
						intent.putExtra("toOrgansition", true);
						intent.putExtra("Profile", mProfile);
						startActivityForResult(intent, EDITORGANSITION_REQUEST_CODE);
						overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
					}
				});
				//兴趣爱好
				View otherInfoViewInterest = LayoutInflater.from(EditMyHomeArchivesActivity.this).inflate(R.layout.other_info,
						null);
				((TextView) otherInfoViewInterest.findViewById(R.id.titleTv)).setText("兴趣爱好");
				if (!TextUtils.isEmpty(interest)) {
					((TextView) otherInfoViewInterest.findViewById(R.id.valueTv)).setText(interest);
				} else {
//					((TextView) otherInfoViewInterest.findViewById(R.id.valueTv)).setText("无");
					((TextView) otherInfoViewInterest.findViewById(R.id.valueTv)).setVisibility(View.GONE);
					((LinearLayout) otherInfoViewInterest.findViewById(R.id.addSelfInfoLayout)).setVisibility(View.VISIBLE);
				}
				mOtherInfoLayout.addView(otherInfoViewInterest);
				LinearLayout otherLayoutInterest = (LinearLayout) otherInfoViewInterest.findViewById(R.id.main_ll);
				otherLayoutInterest.setClickable(true);
				otherLayoutInterest.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						//						mAddOtherInfoLayout.performClick();
						Intent intent = new Intent(EditMyHomeArchivesActivity.this, EditOtherInfo.class);
						intent.putExtra("toInterest", true);
						intent.putExtra("Profile", mProfile);
						startActivityForResult(intent, EDITINTEREST_REQUEST_CODE);
						overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
					}
				});
				//奖励荣誉
				View otherInfoViewAwards = LayoutInflater.from(EditMyHomeArchivesActivity.this)
						.inflate(R.layout.other_info, null);
				((TextView) otherInfoViewAwards.findViewById(R.id.titleTv)).setText("奖励、荣誉");
				if (!TextUtils.isEmpty(awards)) {
					((TextView) otherInfoViewAwards.findViewById(R.id.valueTv)).setText(awards);
				} else {
//					((TextView) otherInfoViewAwards.findViewById(R.id.valueTv)).setText("无");
					((TextView) otherInfoViewAwards.findViewById(R.id.valueTv)).setVisibility(View.GONE);
					((LinearLayout) otherInfoViewAwards.findViewById(R.id.addSelfInfoLayout)).setVisibility(View.VISIBLE);
				}
				mOtherInfoLayout.addView(otherInfoViewAwards);
				LinearLayout otherLayoutAwards = (LinearLayout) otherInfoViewAwards.findViewById(R.id.main_ll);
				otherLayoutAwards.setClickable(true);
				otherLayoutAwards.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						//						mAddOtherInfoLayout.performClick();
						Intent intent = new Intent(EditMyHomeArchivesActivity.this, EditOtherInfo.class);
						intent.putExtra("toAwards", true);
						intent.putExtra("Profile", mProfile);
						startActivityForResult(intent, EDITAWARD_REQUEST_CODE);
						overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
					}
				});

				// 网站
				View siteInfoView = LayoutInflater.from(EditMyHomeArchivesActivity.this).inflate(R.layout.site_info, null);

				if (!TextUtils.isEmpty(webProfileUrl) || (null != sites && sites.length > 0)) {
					if (!TextUtils.isEmpty(webProfileUrl)) {
						View siteUrlInfoView = LayoutInflater.from(EditMyHomeArchivesActivity.this).inflate(
								R.layout.siteurl_info, null);
						((TextView) siteUrlInfoView.findViewById(R.id.titleTv)).setText("网络档案");
						((TextView) siteUrlInfoView.findViewById(R.id.valueTv)).setText(webProfileUrl);
//						Linkify.addLinks(((TextView) siteUrlInfoView.findViewById(R.id.valueTv)), Linkify.WEB_URLS);
						((LinearLayout) siteInfoView.findViewById(R.id.siteLayout)).addView(siteUrlInfoView);
					}

					if (null != sites) {
						for (Site site : sites) {
							View siteUrlInfoView = LayoutInflater.from(EditMyHomeArchivesActivity.this).inflate(
									R.layout.siteurl_info, null);
							String siteType = site.getSiteType();
							if(!TextUtils.isEmpty(siteType) && (siteType.trim().endsWith(":")) || siteType.trim().endsWith("：")){
								siteType = siteType.substring(0,siteType.length() - 1);
							}
							((TextView) siteUrlInfoView.findViewById(R.id.titleTv)).setText(siteType);
							((TextView) siteUrlInfoView.findViewById(R.id.valueTv)).setText(site.getSiteUrl());
//							Linkify.addLinks(((TextView) siteUrlInfoView.findViewById(R.id.valueTv)), Linkify.WEB_URLS);
							((LinearLayout) siteInfoView.findViewById(R.id.siteLayout)).addView(siteUrlInfoView);
						}

					}

				} else {
					View siteUrlInfoView = LayoutInflater.from(EditMyHomeArchivesActivity.this).inflate(R.layout.siteurl_info,
							null);
//					((TextView) siteUrlInfoView.findViewById(R.id.titleTv)).setText("无");
					((TextView) siteUrlInfoView.findViewById(R.id.titleTv)).setVisibility(View.GONE);
					((LinearLayout) siteUrlInfoView.findViewById(R.id.addSelfInfoLayout)).setVisibility(View.VISIBLE);
					((LinearLayout) siteInfoView.findViewById(R.id.siteLayout)).addView(siteUrlInfoView);
				}
				mOtherInfoLayout.addView(siteInfoView);
				LinearLayout otherLayout = (LinearLayout) siteInfoView.findViewById(R.id.main_ll);
				otherLayout.setClickable(true);
				otherLayout.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						//						mAddOtherInfoLayout.performClick();
						Intent intent = new Intent(EditMyHomeArchivesActivity.this, EditOtherInfo.class);
						intent.putExtra("toWebsite", true);
						intent.putExtra("Profile", mProfile);
						startActivityForResult(intent, EDITWEBSITE_REQUEST_CODE);
						overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
					}
				});
			}
		}
		result = null;
	}

	@Override
	protected void initListener() {
		super.initListener();
		selfInfoLl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (null != mProfile) {
					Intent intent = new Intent(EditMyHomeArchivesActivity.this, EditSelfInfo.class);
					intent.putExtra("Profile", mProfile);
					startActivityForResult(intent, EDITSELF_REQUEST_CODE);
					overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
				}
			}
		});
		mAddSummaryInfoLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(EditMyHomeArchivesActivity.this, EditSummaryInfo.class);
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			}
		});
		mAddWorkExperienceInfoLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(EditMyHomeArchivesActivity.this, EditWorkInfo.class);
				intent.putExtra("addWork", true);
				intent.putExtra("Profile", mProfile);
				startActivityForResult(intent, ADDWORK_REQUEST_CODE);
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			}
		});
		mAddEduExperienceInfoLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(EditMyHomeArchivesActivity.this, EditEduInfo.class);
				intent.putExtra("addEdu", true);
				intent.putExtra("Profile", mProfile);
				startActivityForResult(intent, ADDEDU_REQUEST_CODE);
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			}
		});
		mAddOtherInfoLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(EditMyHomeArchivesActivity.this, EditOtherInfo.class);
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			}
		});
		mAddContactLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(EditMyHomeArchivesActivity.this, EditContactInfo.class);
				intent.putExtra("Profile", mProfile);
				startActivityForResult(intent, EDITCONTACT_REQUEST_CODE);
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
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
			finish();
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
			return true;
		}
		return super.onKeyDown(keyCode, event); // 最后，一定要做完以后返回
		// true，或者在弹出菜单后返回true，其他键返回super，让其他键默认
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 1:
			if (resultCode == RESULT_OK) {
				new ProfileTask().execute(mOtherSid, getRenheApplication().getUserInfo().getSid(), getRenheApplication()
						.getUserInfo().getAdSId());
				Intent brocastIntent = new Intent(MyHomeArchivesActivity.REFRESH_ARCHIEVE_RECEIVER_ACTION);
				sendBroadcast(brocastIntent);
			}
			break;
		case PROFESSIONAL_REQUEST_CODE:
		case SPECIALTIES_REQUEST_CODE:
		case PROVIDE_REQUEST_CODE:
		case GET_REQUEST_CODE:
		case ADDWORK_REQUEST_CODE:
		case EDITWORK_REQUEST_CODE:
		case ADDEDU_REQUEST_CODE:
		case EDITEDU_REQUEST_CODE:
		case EDITSELF_REQUEST_CODE:
		case EDITORGANSITION_REQUEST_CODE:
		case EDITINTEREST_REQUEST_CODE:
		case EDITAWARD_REQUEST_CODE:
		case EDITCONTACT_REQUEST_CODE:
		case EDITWEBSITE_REQUEST_CODE:
			if (resultCode == RESULT_OK) {
				if (data.getSerializableExtra("Profile") != null) {
					Profile pf = (Profile) data.getSerializableExtra("Profile");
					populateData(pf);
				} else {
					new ProfileTask().execute(mOtherSid, getRenheApplication().getUserInfo().getSid(), getRenheApplication()
							.getUserInfo().getAdSId());
				}
			}
			break;
		default:
			break;
		}
	}

	class ProfileTask extends AsyncTask<String, Void, Profile> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Profile doInBackground(String... params) {
			try {
				return getRenheApplication().getProfileCommand().showProfile(params[0], params[1], params[2],
						EditMyHomeArchivesActivity.this);
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
				ToastUtil.showNetworkError(EditMyHomeArchivesActivity.this);
			}
		}
	}
}
