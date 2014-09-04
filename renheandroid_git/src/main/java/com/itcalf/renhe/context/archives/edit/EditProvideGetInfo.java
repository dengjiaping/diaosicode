package com.itcalf.renhe.context.archives.edit;

import java.util.ArrayList;
import java.util.List;

import android.R.anim;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.itcalf.renhe.R;
import com.itcalf.renhe.cache.CacheManager;
import com.itcalf.renhe.context.archives.MyHomeArchivesActivity;
import com.itcalf.renhe.context.archives.edit.task.EditSummaryInfoGetTask;
import com.itcalf.renhe.context.archives.edit.task.EditSummaryInfoProvideTask;
import com.itcalf.renhe.context.template.ActivityTemplate;
import com.itcalf.renhe.context.template.BaseActivity;
import com.itcalf.renhe.dto.MessageBoardOperation;
import com.itcalf.renhe.dto.Profile;
import com.itcalf.renhe.dto.Profile.UserInfo;
import com.itcalf.renhe.dto.Profile.UserInfo.AimTagInfo;
import com.itcalf.renhe.dto.Profile.UserInfo.PreferredTagInfo;

/**
 * Title: EditSelfInfo.java<br>
 * Description: <br>
 * Copyright (c) 人和网版权所有 2014    <br>
 * Create DateTime: 2014-7-14 下午4:38:14 <br>
 * @author wangning
 */
public class EditProvideGetInfo extends EditBaseActivity {
	private LinearLayout canProvideGroup;
	private ImageButton addProvideIB;
	private LinearLayout wantGetGroup;
	private ImageButton wantGetIB;

	private final static int CAN_PROVIDE_COUNT = 3;
	private final static int WANT_GET_COUNT = 3;
	private final static int REMOVE_PROVIDE = 0;
	private final static int REMOVE_GET = 1;
	private int provedeCount = 0;
	private int getCount = 0;

	private RelativeLayout provideRl;
	private RelativeLayout getRl;
	private boolean isModify = false;

	//	private LinearLayout provideLl;
	//	private EditText canProvideEt;
	//	private EditText wantGetEt;
	private AimTagInfo[] aimTagInfo;
	private PreferredTagInfo[] preferredTagInfos;
	private Profile pf;
	private boolean isProvideNull = true;
	private boolean isGetNull = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new ActivityTemplate().doInActivity(this, R.layout.archieve_eidt_provide_getinfo);
	}

	@Override
	protected void findView() {
		super.findView();
		setTextValue(R.id.title_txt, "供求信息");
		canProvideGroup = (LinearLayout) findViewById(R.id.can_provide_group);
		addProvideIB = (ImageButton) findViewById(R.id.add_can_provide_ib);
		wantGetGroup = (LinearLayout) findViewById(R.id.want_get_group);
		wantGetIB = (ImageButton) findViewById(R.id.add_want_get_ib);
		provideRl = (RelativeLayout) findViewById(R.id.can_provide_rl);
		getRl = (RelativeLayout) findViewById(R.id.want_get_rl);

		canProvideGroup.removeAllViews();

		wantGetGroup.removeAllViews();

	}

	@Override
	protected void initData() {
		super.initData();
		if (getIntent().getSerializableExtra("Profile") != null) {
			pf = (Profile) getIntent().getSerializableExtra("Profile");
		} else {
			pf = (Profile) CacheManager.getInstance().populateData(this)
					.getObject(getRenheApplication().getUserInfo().getEmail(), CacheManager.PROFILE);
		}
		if (getIntent().getBooleanExtra("toProvide", false)) {
			provideRl.setVisibility(View.VISIBLE);
			getRl.setVisibility(View.GONE);
			//			if(null != getIntent().getSerializableExtra("provide")){
			preferredTagInfos = pf.getUserInfo().getPreferredTagInfo();
			for (int i = 0; i < preferredTagInfos.length; i++) {
				if (!TextUtils.isEmpty(preferredTagInfos[i].getTitle())) {
					provedeCount++;
					View canProvideInfoView = LayoutInflater.from(EditProvideGetInfo.this).inflate(
							R.layout.archieve_edit_selfinfo_canprovide_info, null);
					((EditText) canProvideInfoView.findViewById(R.id.canprovide_item_ET))
							.setText(preferredTagInfos[i].getTitle());
					((EditText) canProvideInfoView.findViewById(R.id.canprovide_item_ET))
					.setSelection(preferredTagInfos[i].getTitle().length());
					((EditText) canProvideInfoView.findViewById(R.id.canprovide_item_ET))
					.addTextChangedListener(new EditTextListener());
					canProvideGroup.addView(canProvideInfoView);
					ImageButton removeProvideIB = (ImageButton) canProvideInfoView.findViewById(R.id.remove_provide_ib);
					removeProvideIB
							.setOnClickListener(new RemoveItemListener(canProvideGroup, canProvideInfoView, REMOVE_PROVIDE));

				}
			}
			//			}else{
			//				View canProvideInfoView = LayoutInflater.from(EditProvideGetInfo.this).inflate(
			//						R.layout.archieve_edit_selfinfo_canprovide_info, null);
			//				canProvideGroup.addView(canProvideInfoView);
			//				ImageButton removeProvideIB = (ImageButton)canProvideInfoView.findViewById(R.id.remove_provide_ib);
			//				removeProvideIB.setOnClickListener(new RemoveItemListener(canProvideGroup, canProvideInfoView,REMOVE_PROVIDE));
			//			}

			if(provedeCount >= CAN_PROVIDE_COUNT){
				addProvideIB.setVisibility(View.GONE);
			}
		} else {
			provideRl.setVisibility(View.GONE);
			getRl.setVisibility(View.VISIBLE);
			//			if(null != getIntent().getSerializableExtra("get")){
			aimTagInfo = pf.getUserInfo().getAimTagInfo();
			for (int i = 0; i < aimTagInfo.length; i++) {
				if (!TextUtils.isEmpty(aimTagInfo[i].getTitle())) {
					getCount++;
					View wantGetInfoView = LayoutInflater.from(EditProvideGetInfo.this).inflate(
							R.layout.archieve_edit_selfinfo_canprovide_info, null);
					((EditText) wantGetInfoView.findViewById(R.id.canprovide_item_ET)).setText(aimTagInfo[i].getTitle());
					((EditText) wantGetInfoView.findViewById(R.id.canprovide_item_ET)).setSelection(aimTagInfo[i].getTitle().length());
					((EditText) wantGetInfoView.findViewById(R.id.canprovide_item_ET)).addTextChangedListener(new EditTextListener());
					wantGetGroup.addView(wantGetInfoView);
					ImageButton removeGetIB = (ImageButton) wantGetInfoView.findViewById(R.id.remove_provide_ib);
					removeGetIB.setOnClickListener(new RemoveItemListener(wantGetGroup, wantGetInfoView, REMOVE_GET));

				}
			}
			//			}else{
			//				View wantGetInfoView = LayoutInflater.from(EditProvideGetInfo.this).inflate(
			//						R.layout.archieve_edit_selfinfo_canprovide_info, null);
			//				wantGetGroup.addView(wantGetInfoView);
			//				ImageButton removeGetIB = (ImageButton)wantGetInfoView.findViewById(R.id.remove_provide_ib);
			//				removeGetIB.setOnClickListener(new RemoveItemListener(wantGetGroup, wantGetInfoView,REMOVE_GET));
			//			}
			if(getCount >= WANT_GET_COUNT){
				addProvideIB.setVisibility(View.GONE);
			}
		}
		
	}

	@Override
	protected void initListener() {
		super.initListener();
		addProvideIB.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (provedeCount < CAN_PROVIDE_COUNT) {
					View canProvideInfoView = LayoutInflater.from(EditProvideGetInfo.this).inflate(
							R.layout.archieve_edit_selfinfo_canprovide_info, null);

					ImageButton removeProvideIB = (ImageButton) canProvideInfoView.findViewById(R.id.remove_provide_ib);
					removeProvideIB
							.setOnClickListener(new RemoveItemListener(canProvideGroup, canProvideInfoView, REMOVE_PROVIDE));
					((EditText) canProvideInfoView.findViewById(R.id.canprovide_item_ET)).addTextChangedListener(new EditTextListener());
					((EditText) canProvideInfoView.findViewById(R.id.canprovide_item_ET)).requestFocus();
					canProvideGroup.addView(canProvideInfoView);
					provedeCount++;
				}
				if (provedeCount == CAN_PROVIDE_COUNT) {
					addProvideIB.setVisibility(View.GONE);
				}
			}
		});
		wantGetIB.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (getCount < WANT_GET_COUNT) {
					View wantGetInfoView = LayoutInflater.from(EditProvideGetInfo.this).inflate(
							R.layout.archieve_edit_selfinfo_canprovide_info, null);
					((EditText) wantGetInfoView.findViewById(R.id.canprovide_item_ET)).addTextChangedListener(new EditTextListener());
					wantGetGroup.addView(wantGetInfoView);
					ImageButton removeGetIB = (ImageButton) wantGetInfoView.findViewById(R.id.remove_provide_ib);
					removeGetIB.setOnClickListener(new RemoveItemListener(wantGetGroup, wantGetInfoView, REMOVE_GET));
					getCount++;
				}
				if (getCount == WANT_GET_COUNT) {
					wantGetIB.setVisibility(View.GONE);
				}
			}
		});
	}
	@Override
	public void goBack() {
		super.goBack();
		if (isModify) {
			Dialog alertDialog = new AlertDialog.Builder(EditProvideGetInfo.this).setTitle("提示")
					.setMessage("有更改尚未保存，是否保存更改的内容？").setPositiveButton("保存", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							goSave();
						}
					}).setNegativeButton("放弃", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							finish();
							overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
						}
					}).setNeutralButton("取消", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					}).create();

			alertDialog.show();
		} else {
			finish();
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
		}
	}
	@Override
	public void goSave() {
		super.goSave();
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//		imm.hideSoftInputFromWindow(item.getWindowToken(), 0);
		if (getIntent().getBooleanExtra("toProvide", false)) {
			//我能提供
			PreferredTagInfo[] mpreferredTagInfos = null;
			List<String> tempList = new ArrayList<String>();
			if (null != canProvideGroup) {
				int childCount = canProvideGroup.getChildCount();
				for (int i = 0; i < childCount; i++) {
					String s = ((EditText) canProvideGroup.getChildAt(i).findViewById(R.id.canprovide_item_ET)).getText()
							.toString().trim();
					if(!TextUtils.isEmpty(s)){
						isProvideNull = false;
						tempList.add(s);
					}
				}
			}
			if(isProvideNull){
				Toast.makeText(EditProvideGetInfo.this, "我能提供不能为空", Toast.LENGTH_SHORT).show();
				return ;
			}else{
				if(tempList.size() > 0){
					mpreferredTagInfos = new PreferredTagInfo[tempList.size()];
					for(int i =0 ; i < tempList.size(); i++){
						PreferredTagInfo pInfo = new PreferredTagInfo();
						pInfo.setTitle(tempList.get(i));
						mpreferredTagInfos[i] = pInfo;
					}
				}
//				specialties = (String[]) tempList.toArray();
			}
			if(null == mpreferredTagInfos || mpreferredTagInfos.length <= 0){
				Toast.makeText(EditProvideGetInfo.this, "我能提供不能为空", Toast.LENGTH_SHORT).show();
				return ;
			}
//			final StringBuffer speString = new StringBuffer();
//			for(int i = 0; i < specialties.length; i++){
//				if(i != specialties.length - 1){
//					speString.append(specialties[i]+",");
//				}else{
//					speString.append(specialties[i]);
//				}
//			}
			final PreferredTagInfo[] finalpreferredTagInfos = mpreferredTagInfos;
			String[] preStrings = new String[mpreferredTagInfos.length];
			for(int i = 0; i < preStrings.length; i++){
				if(!TextUtils.isEmpty(mpreferredTagInfos[i].getTitle())){
					preStrings[i] = mpreferredTagInfos[i].getTitle();
				}
			}
			new EditSummaryInfoProvideTask(EditProvideGetInfo.this,preStrings){

				@Override
				public void doPre() {
					super.doPre();
					showDialog(1);
					
				}
				@Override
				public void doPost(MessageBoardOperation result) {
					super.doPost(result);
					if(result == null){
						removeDialog(1);
						Toast.makeText(EditProvideGetInfo.this, "网络异常，请重试", Toast.LENGTH_SHORT).show();
					}else if(result.getState() == 1){
						//更新本地缓存
						UserInfo userInfo = pf.getUserInfo();
						userInfo.setPreferredTagInfo(finalpreferredTagInfos);
						pf.setUserInfo(userInfo);
						CacheManager.getInstance().populateData(EditProvideGetInfo.this)
						.saveObject(pf, getRenheApplication().getUserInfo().getEmail(), CacheManager.PROFILE);
						Intent brocastIntent = new Intent(MyHomeArchivesActivity.REFRESH_ARCHIEVE_RECEIVER_ACTION);
						brocastIntent.putExtra("Profile", pf);
						sendBroadcast(brocastIntent);
						Intent intent = new Intent();
						intent.putExtra("Profile", pf);
						setResult(RESULT_OK, intent);
						removeDialog(1);
						finish();
						overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
					}else if(result.getState() == -3){
						removeDialog(1);
						Toast.makeText(EditProvideGetInfo.this, "我能提供不能为空", Toast.LENGTH_SHORT).show();
					}else if(result.getState() == -4){
						removeDialog(1);
						Toast.makeText(EditProvideGetInfo.this, "我能提供单项长度过长，长度不能超过30个字", Toast.LENGTH_SHORT).show();
					}else if(result.getState() == -5){
						removeDialog(1);
						Toast.makeText(EditProvideGetInfo.this, "我能提供数量过多，不能超过3个", Toast.LENGTH_SHORT).show();
					}else {
						removeDialog(1);
						Toast.makeText(EditProvideGetInfo.this, "发生未知错误，请重试", Toast.LENGTH_SHORT).show();
					}
				}
			
			}.execute(getRenheApplication().getUserInfo().getSid(), getRenheApplication().getUserInfo().getAdSId());
		
		}else{
			//我想获得
			AimTagInfo[] mAimTagInfos = null;
			List<String> tempList = new ArrayList<String>();
			if (null != wantGetGroup) {
				int childCount = wantGetGroup.getChildCount();
				for (int i = 0; i < childCount; i++) {
					String s = ((EditText) wantGetGroup.getChildAt(i).findViewById(R.id.canprovide_item_ET)).getText()
							.toString().trim();
					if(!TextUtils.isEmpty(s)){
						isGetNull = false;
						tempList.add(s);
					}
				}
			}
			if(isGetNull){
				Toast.makeText(EditProvideGetInfo.this, "我想得到不能为空", Toast.LENGTH_SHORT).show();
				return ;
			}else{
				mAimTagInfos = new AimTagInfo[tempList.size()];
				for(int i =0 ; i < tempList.size(); i++){
					AimTagInfo aInfo = new AimTagInfo();
					aInfo.setTitle(tempList.get(i));
					mAimTagInfos[i] = aInfo;
				}
//				specialties = (String[]) tempList.toArray();
			}
			if(null == mAimTagInfos || mAimTagInfos.length <= 0){
				Toast.makeText(EditProvideGetInfo.this, "我想得到不能为空", Toast.LENGTH_SHORT).show();
				return ;
			}
//			final StringBuffer speString = new StringBuffer();
//			for(int i = 0; i < specialties.length; i++){
//				if(i != specialties.length - 1){
//					speString.append(specialties[i]+",");
//				}else{
//					speString.append(specialties[i]);
//				}
//			}
			final AimTagInfo[] finalaimTagInfos = mAimTagInfos;
			String[] preStrings = new String[mAimTagInfos.length];
			for(int i = 0; i < preStrings.length; i++){
				if(!TextUtils.isEmpty(mAimTagInfos[i].getTitle())){
					preStrings[i] = mAimTagInfos[i].getTitle();
				}
			}
			new EditSummaryInfoGetTask(EditProvideGetInfo.this,preStrings){


				@Override
				public void doPre() {
					super.doPre();
					showDialog(1);
					
				}
				@Override
				public void doPost(MessageBoardOperation result) {
					super.doPost(result);
					if(result == null){
						removeDialog(1);
						Toast.makeText(EditProvideGetInfo.this, "网络异常，请重试", Toast.LENGTH_SHORT).show();
					}else if(result.getState() == 1){
						//更新本地缓存
						UserInfo userInfo = pf.getUserInfo();
						userInfo.setAimTagInfo(finalaimTagInfos);
						pf.setUserInfo(userInfo);
						CacheManager.getInstance().populateData(EditProvideGetInfo.this)
						.saveObject(pf, getRenheApplication().getUserInfo().getEmail(), CacheManager.PROFILE);
						Intent brocastIntent = new Intent(MyHomeArchivesActivity.REFRESH_ARCHIEVE_RECEIVER_ACTION);
						brocastIntent.putExtra("Profile", pf);
						sendBroadcast(brocastIntent);
						Intent intent = new Intent();
						intent.putExtra("Profile", pf);
						setResult(RESULT_OK, intent);
						removeDialog(1);
						finish();
						overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
					}else if(result.getState() == -3){
						removeDialog(1);
						Toast.makeText(EditProvideGetInfo.this, "我想得到不能为空", Toast.LENGTH_SHORT).show();
					}else if(result.getState() == -4){
						removeDialog(1);
						Toast.makeText(EditProvideGetInfo.this, "我想得到单项长度过长，长度不能超过30个字", Toast.LENGTH_SHORT).show();
					}else if(result.getState() == -5){
						removeDialog(1);
						Toast.makeText(EditProvideGetInfo.this, "我想得到数量过多，不能超过3个", Toast.LENGTH_SHORT).show();
					}else {
						removeDialog(1);
						Toast.makeText(EditProvideGetInfo.this, "发生未知错误，请重试", Toast.LENGTH_SHORT).show();
					}
				}
			
			
			}.execute(getRenheApplication().getUserInfo().getSid(), getRenheApplication().getUserInfo().getAdSId());
		}
	}
	class RemoveItemListener implements OnClickListener {
		LinearLayout groupView;
		View itemView;
		int type;

		public RemoveItemListener(LinearLayout groupView, View itemView, int type) {
			this.groupView = groupView;
			this.itemView = itemView;
			this.type = type;
		}

		@Override
		public void onClick(View arg0) {
			isModify = true;
			if (null != groupView) {
				groupView.removeView(itemView);
			}
			switch (type) {
			case REMOVE_PROVIDE:
				provedeCount--;
				if (provedeCount < CAN_PROVIDE_COUNT) {
					addProvideIB.setVisibility(View.VISIBLE);
				}
				break;
			case REMOVE_GET:
				getCount--;
				if (getCount < WANT_GET_COUNT) {
					wantGetIB.setVisibility(View.VISIBLE);
				}
				break;
			default:
				break;
			}
		}

	}
	class EditTextListener implements TextWatcher{

		@Override
		public void afterTextChanged(Editable arg0) {
			
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			
		}

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			isModify = true;
		}
		
	}
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 1:
			ProgressDialog findPd = new ProgressDialog(this);
			findPd.setMessage("正在保存...");
//			findPd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			findPd.setCanceledOnTouchOutside(false);
			return findPd;
		default:
			return null;
		}
	}
}
