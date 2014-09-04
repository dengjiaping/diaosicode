package com.itcalf.renhe.context.fragmentMain;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itcalf.renhe.R;
import com.itcalf.renhe.RenheApplication;
import com.itcalf.renhe.cache.CacheManager;
import com.itcalf.renhe.context.archives.DialogActivity;
import com.itcalf.renhe.context.archives.MyHomeArchivesActivity;
import com.itcalf.renhe.context.contacts.SearchContactsActivity;
import com.itcalf.renhe.context.more.MySettingActivity;
import com.itcalf.renhe.context.relationship.AdvancedSearchActivity;
import com.itcalf.renhe.context.room.NewMessageBoardActivity;
import com.itcalf.renhe.dto.UserInfo;
import com.itcalf.renhe.utils.AsyncImageLoader;
import com.itcalf.renhe.utils.ToastUtil;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

@SuppressLint("ValidFragment")
public class MeunFragment extends Fragment implements OnClickListener {
	private FragmentManager fm;
	private SlidingMenu slidingMenu;
	private MainFragment mainFragment;
	private View rootView;// ����Fragment view
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
	public final static String ICON_ACTION = "notice_icon_num";
	public final static String NEWMSG_ICON_ACTION = "newmsg_notice_icon_num";
	public final static String UPDATE_AVATAR_ACTION = "update_avatar_image";
	private SharedPreferences msp;
	private SharedPreferences.Editor mEditor;
	private float scale;
	private Button innerUnreadIB;
	private Button noticeUnreadIB;
	private TextView betaTv;
	private RoomsFragment roomsFragment;
	private InnerMsgsFragment innerMsgsFragment;
	private MyArchivesFragment myHomeArchivesActivity;
	private AdvancedSearchFragment advancedSearchActivity;
	private SearchContactsFragment searchContactsActivity;
	private RelativeLayout actionBarRl;
	private IconReceiver iconReceiver;
	private InnerMsgIconReceiver innerMsgIconReceiver;
	private UpdateAvarImage updateAvarImage;
	private Fragment mCurrentFragment;
	private InitCurrentFragment initCurrentFragmentReveiver;
	public final static String INIT_CURRENTFRAGMENT_ACTION = "initCurrentFragment";
	private boolean logoutFlag = false;

	public MeunFragment(FragmentManager fm, SlidingMenu slidingMenu, Activity mainActivity) {
		this.fm = fm;
		this.slidingMenu = slidingMenu;
		initCurrentFragmentReveiver = new InitCurrentFragment();
		IntentFilter intentFilter = new IntentFilter(INIT_CURRENTFRAGMENT_ACTION);
		if (null != mainActivity) {
			mainActivity.registerReceiver(initCurrentFragmentReveiver, intentFilter);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		mainFragment = (MainFragment) mainFragment;
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		msp = getActivity().getSharedPreferences("setting_info", 0);
		mEditor = msp.edit();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (null == rootView) {
			rootView = inflater.inflate(R.layout.menu_frame, null);
		}
		selfInfoRl = (RelativeLayout) rootView.findViewById(R.id.selfinfo_ll);
		mAvatarImg = (ImageView) rootView.findViewById(R.id.avatar_img);
		nameTv = (TextView) rootView.findViewById(R.id.nickname_txt);
		jobTv = (TextView) rootView.findViewById(R.id.job_txt);
		companyTv = (TextView) rootView.findViewById(R.id.company_txt);
		roomLl = (LinearLayout) rootView.findViewById(R.id.room_bt);
		contactLl = (LinearLayout) rootView.findViewById(R.id.contact_bt);
		researchLl = (LinearLayout) rootView.findViewById(R.id.research_bt);
		innerMsgLl = (ImageButton) rootView.findViewById(R.id.innermsg_bt);
		settingLl = (ImageButton) rootView.findViewById(R.id.setting_bt);
		noticeLl = (ImageButton) rootView.findViewById(R.id.notice_bt);
		shareLl = (LinearLayout) rootView.findViewById(R.id.share_bt);
		roomLl.setOnClickListener(this);
		contactLl.setOnClickListener(this);
		researchLl.setOnClickListener(this);
		innerMsgLl.setOnClickListener(this);
		settingLl.setOnClickListener(this);
		noticeLl.setOnClickListener(this);
		shareLl.setOnClickListener(this);
		selfInfoRl.setOnClickListener(this);
		innerUnreadIB = (Button) rootView.findViewById(R.id.innermsg_unread);
		noticeUnreadIB = (Button) rootView.findViewById(R.id.notice_unread);
		//设置左侧菜单头部高度，与主页actionbar高度持平
		actionBarRl = (RelativeLayout) rootView.findViewById(R.id.action_bar_rl);
		int actionBarHeight = 0;
		TypedValue tv = new TypedValue();
		if (getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
			actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
		}
		if (actionBarHeight > 0) {
			LayoutParams linearParams = (LayoutParams) actionBarRl.getLayoutParams();
			linearParams.height = actionBarHeight;
			actionBarRl.setLayoutParams(linearParams);
		}

		UserInfo userInfo = RenheApplication.getInstance().getUserInfo();
		if (!TextUtils.isEmpty(userInfo.getUserface())) {
			AsyncImageLoader
					.getInstance()
					.populateData(getActivity(), userInfo.getEmail(), false, true, true)
					.loadPic(mAvatarImg, userInfo.getSid(), userInfo.getUserface(),
							(int) getActivity().getResources().getDimension(R.dimen.renhe_hall_bottom_image_wh),
							(int) getActivity().getResources().getDimension(R.dimen.renhe_hall_bottom_image_wh));
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
		int noticeNum = msp.getInt("unreadmsg_num", 0);
		manageUnreadButton(noticeUnreadIB, noticeNum);

		//站内信数目提醒

		int newMsgNoticeNum = msp.getInt("newmsg_unreadmsg_num", 0);
		manageUnreadButton(innerUnreadIB, newMsgNoticeNum);

		iconReceiver = new IconReceiver();
		IntentFilter intentFilter = new IntentFilter(ICON_ACTION);
		getActivity().registerReceiver(iconReceiver, intentFilter);

		innerMsgIconReceiver = new InnerMsgIconReceiver();
		intentFilter = new IntentFilter(NEWMSG_ICON_ACTION);
		getActivity().registerReceiver(innerMsgIconReceiver, intentFilter);

		updateAvarImage = new UpdateAvarImage();
		intentFilter = new IntentFilter(UPDATE_AVATAR_ACTION);
		getActivity().registerReceiver(updateAvarImage, intentFilter);

		Fragment mroomFragment = fm.findFragmentByTag("RoomsFragment");
		if (mroomFragment != null && mroomFragment instanceof RoomsFragment) {
			roomsFragment = (RoomsFragment) mroomFragment;
		}
		if (null != roomsFragment) {
			mCurrentFragment = roomsFragment;
		} else {
			mCurrentFragment = new RoomsFragment();
		}
		roomLl.performClick();
		return rootView;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.room_bt:
			if (roomsFragment == null) {
				roomsFragment = new RoomsFragment();
			}
			Fragment mroomFragment = fm.findFragmentByTag("RoomsFragment");
			if (mroomFragment != null && mroomFragment instanceof RoomsFragment) {
				roomsFragment = (RoomsFragment) mroomFragment;
			}
			FragmentTransaction roomtransaction = fm.beginTransaction();
			//			roomtransaction.replace(R.id.rel, roomsFragment,"RoomsFragment");
			switchContent(mCurrentFragment, roomsFragment, "RoomsFragment");
			//			roomtransaction.commit();
			slidingMenu.showContent();
			break;
		case R.id.contact_bt:
			if (searchContactsActivity == null) {
				searchContactsActivity = new SearchContactsFragment();
			}
			FragmentTransaction searchContacttransaction = fm.beginTransaction();
			//			searchContacttransaction.replace(R.id.rel, searchContactsActivity,"SearchContactsFragment");
			switchContent(mCurrentFragment, searchContactsActivity, "SearchContactsFragment");
			//			searchContacttransaction.addToBackStack(null);
			//			searchContacttransaction.commit();
			slidingMenu.showContent();
			break;
		case R.id.research_bt:
			if (advancedSearchActivity == null) {
				advancedSearchActivity = new AdvancedSearchFragment();
			}
			FragmentTransaction advancetransaction = fm.beginTransaction();
			//			advancetransaction.replace(R.id.rel, advancedSearchActivity,"AdvancedSearchFragment");
			//			advancetransaction.addToBackStack(null);
			//			advancetransaction.commit();
			switchContent(mCurrentFragment, advancedSearchActivity, "AdvancedSearchFragment");
			slidingMenu.showContent();
			break;
		case R.id.innermsg_bt:
			if (innerMsgsFragment == null) {
				innerMsgsFragment = new InnerMsgsFragment();
			}
			Fragment minnerFragment = fm.findFragmentByTag("InnerMsgsFragment");
			if (minnerFragment != null && minnerFragment instanceof InnerMsgsFragment) {
				innerMsgsFragment = (InnerMsgsFragment) minnerFragment;
			}
			FragmentTransaction innermsgtransaction = fm.beginTransaction();
			//			innermsgtransaction.replace(R.id.rel, innerMsgsFragment,"InnerMsgsFragment");
			//			innermsgtransaction.addToBackStack(null);
			//			innermsgtransaction.commit();
			switchContent(mCurrentFragment, innerMsgsFragment, "InnerMsgsFragment");
			slidingMenu.showContent();
			break;
		case R.id.setting_bt:
			getActivity().startActivity(new Intent(getActivity(), MySettingActivity.class));
			getActivity().overridePendingTransition(R.anim.zoom_enter, 0);
			break;
		case R.id.notice_bt:
			Intent intent2 = new Intent(ICON_ACTION);
			intent2.putExtra("notice_num", 0);
			getActivity().sendBroadcast(intent2);

			getActivity().startActivity(new Intent(getActivity(), NewMessageBoardActivity.class));
			getActivity().overridePendingTransition(R.anim.zoom_enter, 0);
			break;
		case R.id.selfinfo_ll:
			if (myHomeArchivesActivity == null) {
				myHomeArchivesActivity = new MyArchivesFragment();
			}
			FragmentTransaction archivetransaction = fm.beginTransaction();
			//			archivetransaction.replace(R.id.rel, myHomeArchivesActivity,"MyArchivesFragment");
			//			archivetransaction.addToBackStack(null);
			//			archivetransaction.commit();
			switchContent(mCurrentFragment, myHomeArchivesActivity, "MyArchivesFragment");
			slidingMenu.showContent();
			break;
		case R.id.share_bt:
			Intent intent = new Intent();
			intent.setClass(getActivity(), DialogActivity.class);
			getActivity().startActivity(intent);
			getActivity().overridePendingTransition(R.anim.push_bottom_in, R.anim.push_bottom_out);
			break;
		default:
			break;
		}

	};

	public void switchContent(Fragment from, Fragment to, String tag) {
		if (null != to && mCurrentFragment != to) {
			mCurrentFragment = to;
			FragmentTransaction transaction = fm.beginTransaction();
			//			if(!(to instanceof RoomsFragment)){
			//				transaction.addToBackStack(null);
			//			}
			if (!to.isAdded()) { // 先判断是否被add过
				transaction.hide(from).add(R.id.rel, to, tag).commit(); // 隐藏当前的fragment，add下一个到Activity中
			} else {
				transaction.hide(from).show(to).commit(); // 隐藏当前的fragment，显示下一个
			}

			MainFragment fca = (MainFragment) getActivity();
			if (to instanceof RoomsFragment) {
				fca.switchTitle("我的客厅");
			} else if (to instanceof InnerMsgsFragment) {
				fca.switchTitle("站内信");
			} else if (to instanceof MyArchivesFragment) {
				fca.switchTitle("档案");
			} else if (to instanceof AdvancedSearchFragment) {
				fca.switchTitle("人脉搜索");
			} else if (to instanceof SearchContactsFragment) {
				fca.switchTitle("联系人");
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

	public void myOnKeyDown(int key_code) {
		if (null != mCurrentFragment && !(mCurrentFragment instanceof RoomsFragment) && null != roomLl) {
			roomLl.performClick();
		} else {
			if (logoutFlag) {
				AsyncImageLoader.getInstance().clearCache();
				if (getActivity().getSharedPreferences("setting_info", 0).getBoolean("clearcache", false)) {
					CacheManager.getInstance().populateData(getActivity())
							.clearCache(RenheApplication.getInstance().getUserInfo().getEmail());
				}
				RenheApplication.getInstance().exit();
			} else {
				ToastUtil.showToast(getActivity(), "请再点击一次退出程序");
				logoutFlag = true;
				handler.postDelayed(run, 2000);
			}
		}
	}

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
		if (null != unreadBt) {
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

	class InitCurrentFragment extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			if (null != arg1.getStringExtra("currentfragment")) {
				String currentFrg = arg1.getStringExtra("currentfragment");
				if (currentFrg.equals("RoomsFragment")) {
					Fragment mroomFragment = fm.findFragmentByTag("RoomsFragment");
					if (mroomFragment != null && mroomFragment instanceof RoomsFragment) {
						roomsFragment = (RoomsFragment) mroomFragment;
					}
					if (null != roomsFragment) {
						mCurrentFragment = roomsFragment;
					} else {
						mCurrentFragment = new RoomsFragment();
					}
				} else if (currentFrg.equals("InnerMsgsFragment")) {
					Fragment minnerFragment = fm.findFragmentByTag("InnerMsgsFragment");
					if (minnerFragment != null && minnerFragment instanceof InnerMsgsFragment) {
						fm.beginTransaction().remove(minnerFragment);
					}
					innerMsgsFragment = new InnerMsgsFragment();
					switchContent(mCurrentFragment, innerMsgsFragment, "InnerMsgsFragment");
				}
			}

		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCurrentFragment = null;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mCurrentFragment = null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (null != iconReceiver) {
			getActivity().unregisterReceiver(iconReceiver);
		}
		if (null != innerMsgIconReceiver) {
			getActivity().unregisterReceiver(innerMsgIconReceiver);
		}
		if (null != updateAvarImage) {
			getActivity().unregisterReceiver(updateAvarImage);
		}
		if (null != initCurrentFragmentReveiver) {
			getActivity().unregisterReceiver(initCurrentFragmentReveiver);
		}
	}
}
