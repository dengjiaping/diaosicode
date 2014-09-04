/*package com.itcalf.renhe.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.jpush.android.api.JPushInterface;

import com.itcalf.renhe.Constants;
import com.itcalf.renhe.R;
import com.itcalf.renhe.RenheApplication;
import com.itcalf.renhe.context.archives.DialogActivity;
import com.itcalf.renhe.context.archives.MyHomeArchivesActivity;
import com.itcalf.renhe.context.contacts.SearchContactsActivity;
import com.itcalf.renhe.context.innermsg.MsgTabActivity;
import com.itcalf.renhe.context.more.MySettingActivity;
import com.itcalf.renhe.context.relationship.AdvancedSearchActivity;
import com.itcalf.renhe.context.relationship.SearchRelationshipActivity;
import com.itcalf.renhe.context.room.NewMessageBoardActivity;
import com.itcalf.renhe.context.room.RoomsActivity;
import com.itcalf.renhe.dto.UserInfo;
import com.itcalf.renhe.utils.AsyncImageLoader;
import com.itcalf.widget.slidingmenu.lib.SlidingMenu;
import com.itcalf.widget.slidingmenu.lib.SlidingMenu.CanvasTransformer;

public class MySlidingMenu implements OnClickListener {
	private final Activity activity;
	private UserInfo userInfo;
	private RelativeLayout selfInfoRl;
	private ImageView mAvatarImg;
	private TextView nameTv;
	private TextView jobTv;
	private TextView companyTv;
	private LinearLayout roomLl;
	private LinearLayout contactLl;
	private LinearLayout researchLl;
	private ImageButton innerMsgLl;
	private ImageButton settingLl;
	private ImageButton noticeLl;
	private LinearLayout shareLl;
	//	private BadgeView badge;
	//	private BadgeView innerMsgBadge;
	private IconReceiver iconReceiver;
	private InnerMsgIconReceiver innerMsgIconReceiver;
	public final static String ICON_ACTION = "notice_icon_num";
	public final static String NEWMSG_ICON_ACTION = "newmsg_notice_icon_num";
	public final static String UPDATE_AVATAR_ACTION = "update_avatar_image";
	private SharedPreferences msp;
	private SharedPreferences.Editor mEditor;
	private float scale;
	private Button innerUnreadIB;
	private Button noticeUnreadIB;
	private TextView betaTv;
	private UpdateAvarImage updateAvarImage;
	public MySlidingMenu(Activity activity) {
		this.activity = activity;
		this.userInfo = RenheApplication.renheApplication.getUserInfo();
	}

	public SlidingMenu initSlidingMenu() {
		scale = activity.getResources().getDisplayMetrics().density;
		DisplayMetrics metric = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
		int width = metric.widthPixels; // 屏幕宽度（像素）
		menu = new SlidingMenu(activity);//直接new，而不是getSlidingMenu  
		menu.setMode(SlidingMenu.LEFT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setMenu(R.layout.menu_frame);//就是普通的layout布局  
		menu.setShadowDrawable(R.drawable.shadow);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setBehindOffset((int) (width * 0.15));//设置SlidingMenu菜单的宽度  
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(activity, SlidingMenu.SLIDING_CONTENT);//必须调用  
		final Interpolator interp = new Interpolator() {
			@Override
			public float getInterpolation(float t) {
				t -= 1.0f;
				return t * t * t + 1.0f;
			}
		};
		CanvasTransformer mTransformer = new CanvasTransformer() {//菜单弹出/弹入动画

			@SuppressLint("NewApi")
			@Override
			public void transformCanvas(Canvas canvas, float percentOpen) {
				canvas.translate(canvas.getWidth() * (1 - interp.getInterpolation(percentOpen)), 0);
			}

		};
		//		menu.setBehindCanvasTransformer(mTransformer);
		menu.showContent();

		selfInfoRl = (RelativeLayout) menu.findViewById(R.id.selfinfo_ll);
		mAvatarImg = (ImageView) menu.findViewById(R.id.avatar_img);
		nameTv = (TextView) menu.findViewById(R.id.nickname_txt);
		jobTv = (TextView) menu.findViewById(R.id.job_txt);
		companyTv = (TextView) menu.findViewById(R.id.company_txt);
		roomLl = (LinearLayout) menu.findViewById(R.id.room_bt);
		contactLl = (LinearLayout) menu.findViewById(R.id.contact_bt);
		researchLl = (LinearLayout) menu.findViewById(R.id.research_bt);
		innerMsgLl = (ImageButton) menu.findViewById(R.id.innermsg_bt);
		settingLl = (ImageButton) menu.findViewById(R.id.setting_bt);
		noticeLl = (ImageButton) menu.findViewById(R.id.notice_bt);
		shareLl = (LinearLayout) menu.findViewById(R.id.share_bt);
		roomLl.setOnClickListener(this);
		contactLl.setOnClickListener(this);
		researchLl.setOnClickListener(this);
		innerMsgLl.setOnClickListener(this);
		settingLl.setOnClickListener(this);
		noticeLl.setOnClickListener(this);
		shareLl.setOnClickListener(this);
		selfInfoRl.setOnClickListener(this);
		innerUnreadIB = (Button) menu.findViewById(R.id.innermsg_unread);
		noticeUnreadIB = (Button) menu.findViewById(R.id.notice_unread);
		msp = activity.getSharedPreferences("setting_info", 0);
		mEditor = msp.edit();
		int noticeNum = msp.getInt("unreadmsg_num", 0);
		manageUnreadButton(noticeUnreadIB, noticeNum);

		//站内信数目提醒

		int newMsgNoticeNum = msp.getInt("newmsg_unreadmsg_num", 0);
		manageUnreadButton(innerUnreadIB, newMsgNoticeNum);

		if (!TextUtils.isEmpty(userInfo.getUserface())) {
			AsyncImageLoader
					.getInstance()
					.populateData(activity, userInfo.getEmail(), false, true, true)
					.loadPic(mAvatarImg, userInfo.getSid(), userInfo.getUserface(),
							(int) activity.getResources().getDimension(R.dimen.renhe_hall_bottom_image_wh),
							(int) activity.getResources().getDimension(R.dimen.renhe_hall_bottom_image_wh));
		}
		if (!TextUtils.isEmpty(userInfo.getName())) {
			nameTv.setText(userInfo.getName());
		}
		jobTv.setText("");
		if (!TextUtils.isEmpty(userInfo.getTitle())) {
			jobTv.setText(userInfo.getTitle());
		}
		if (!TextUtils.isEmpty(userInfo.getCompany())) {
			companyTv.setText(userInfo.getCompany());
		}
		//		String companyTile = userInfo.getTitle() + "  " + userInfo.getCompany();
		//		jobTv.setText(companyTile);

		iconReceiver = new IconReceiver();
		IntentFilter intentFilter = new IntentFilter(ICON_ACTION);
		activity.registerReceiver(iconReceiver, intentFilter);

		innerMsgIconReceiver = new InnerMsgIconReceiver();
		intentFilter = new IntentFilter(NEWMSG_ICON_ACTION);
		activity.registerReceiver(innerMsgIconReceiver, intentFilter);

		updateAvarImage = new UpdateAvarImage();
		intentFilter = new IntentFilter(UPDATE_AVATAR_ACTION);
		activity.registerReceiver(updateAvarImage, intentFilter);
		
		betaTv = (TextView) menu.findViewById(R.id.beta);
		if (Constants.renhe_log) {
			betaTv.setVisibility(View.VISIBLE);
		} else {
			betaTv.setVisibility(View.GONE);
		}
		return menu;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.room_bt:
			if (RenheApplication.renheApplication.currentItem == Constants.Item.ROOM) {
				menu.toggle();
			} else {
				RenheApplication.renheApplication.currentItem = Constants.Item.ROOM;
				activity.startActivity(new Intent(activity, RoomsActivity.class));
				activity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
				activity.finish();
			}
			//			activity.sendBroadcast(new Intent(JPushInterface.ACTION_MESSAGE_RECEIVED));
			break;
		case R.id.contact_bt:
			if (RenheApplication.renheApplication.currentItem == Constants.Item.CONTACT) {
				menu.toggle();
			} else {
				RenheApplication.renheApplication.currentItem = Constants.Item.CONTACT;
				Intent intent = new Intent(activity, SearchContactsActivity.class);
				intent.putExtra("isFromMenu", true);
				activity.startActivity(intent);
				activity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
				activity.finish();
			}
			break;
		case R.id.research_bt:
			if (RenheApplication.renheApplication.currentItem == Constants.Item.RESEARCH) {
				menu.toggle();
			} else {
				RenheApplication.renheApplication.currentItem = Constants.Item.RESEARCH;
				Intent intent = new Intent(activity, AdvancedSearchActivity.class);
				activity.startActivity(intent);
				activity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
				activity.finish();
			}
			break;
		case R.id.innermsg_bt:
			if (RenheApplication.renheApplication.currentItem == Constants.Item.INNERMSG) {
				menu.toggle();
			} else {
				RenheApplication.renheApplication.currentItem = Constants.Item.INNERMSG;
				activity.startActivity(new Intent(activity, MsgTabActivity.class));
				activity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
				activity.finish();
			}
			break;
		case R.id.setting_bt:
			activity.startActivity(new Intent(activity, MySettingActivity.class));
			activity.overridePendingTransition(R.anim.zoom_enter, 0);
			break;
		case R.id.notice_bt:
			Intent intent2 = new Intent(ICON_ACTION);
			intent2.putExtra("notice_num", 0);
			activity.sendBroadcast(intent2);

			activity.startActivity(new Intent(activity, NewMessageBoardActivity.class));
			activity.overridePendingTransition(R.anim.zoom_enter, 0);
			break;
		case R.id.selfinfo_ll:
			if (RenheApplication.renheApplication.currentItem == Constants.Item.SELFINFO) {
				menu.toggle();
			} else {
				RenheApplication.renheApplication.currentItem = Constants.Item.SELFINFO;
				Intent intent = new Intent(activity, MyHomeArchivesActivity.class);
				intent.putExtra("isFromMenu", true);
				activity.startActivity(intent);
				activity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
				activity.finish();
			}
			break;
		case R.id.share_bt:
			Intent intent = new Intent();
			intent.setClass(activity, DialogActivity.class);
			activity.startActivity(intent);
			activity.overridePendingTransition(R.anim.push_bottom_in, R.anim.push_bottom_out);
			break;
		default:
			break;
		}
	};

	class IconReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ICON_ACTION)) {
				int num = intent.getIntExtra("notice_num", 0);
				mEditor.putInt("unreadmsg_num", num);
				mEditor.commit();
				manageUnreadButton(noticeUnreadIB, num);
			}
		}

	}

	class InnerMsgIconReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(NEWMSG_ICON_ACTION)) {
				int num = intent.getIntExtra("newmsg_notice_num", 0);
				manageUnreadButton(innerUnreadIB, num);
			}

		}

	}

	public void manageUnreadButton(Button unreadBt, int num) {
		if (num > 0) {
			unreadBt.setVisibility(View.VISIBLE);
			if (num >= 100) {
				unreadBt.setText("99+");
			} else {
				unreadBt.setText(num + "");
			}
		} else {
			unreadBt.setVisibility(View.GONE);
		}
	}

	class UpdateAvarImage extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			Bundle extras = arg1.getExtras();
			 Bitmap bitmap = extras.getParcelable("avarimage");
			if (null != bitmap) {
				mAvatarImg.setImageBitmap(bitmap);
			}
		}

	}
}*/