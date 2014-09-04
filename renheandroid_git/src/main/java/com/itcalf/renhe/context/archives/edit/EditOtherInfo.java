package com.itcalf.renhe.context.archives.edit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.itcalf.renhe.Constants;
import com.itcalf.renhe.R;
import com.itcalf.renhe.cache.CacheManager;
import com.itcalf.renhe.context.archives.MyHomeArchivesActivity;
import com.itcalf.renhe.context.archives.edit.task.EditAwardInfoTask;
import com.itcalf.renhe.context.archives.edit.task.EditInterestInfoTask;
import com.itcalf.renhe.context.archives.edit.task.EditOrgansitionInfoTask;
import com.itcalf.renhe.context.archives.edit.task.EditWebsiteInfoTask;
import com.itcalf.renhe.context.template.ActivityTemplate;
import com.itcalf.renhe.context.template.BaseActivity;
import com.itcalf.renhe.dto.Profile;
import com.itcalf.renhe.dto.Profile.UserInfo;
import com.itcalf.renhe.dto.Profile.UserInfo.OtherInfo;
import com.itcalf.renhe.dto.Profile.UserInfo.OtherInfo.Site;

/**
 * Title: EditSelfInfo.java<br>
 * Description: <br>
 * Copyright (c) 人和网版权所有 2014 <br>
 * Create DateTime: 2014-7-14 下午4:38:14 <br>
 * 
 * @author wangning
 */
public class EditOtherInfo extends EditBaseActivity {
	private static final String[] m = { "博客", "相册", "公司网站", "个人网站" };
	private EditText interestEt;
	private EditText organizationEt;
	private EditText awardEt;
	private LinearLayout canProvideGroup;
	private ImageButton addProvideIB;
	private Spinner webSpinner;
	private TextView selectedTv;
	private ArrayAdapter<String> adapter;

	private final static int CAN_PROVIDE_COUNT = 3;
	private final static int REMOVE_PROVIDE = 0;
	private int provedeCount = 0;

	private LinearLayout interestLl;
	private LinearLayout organizationLl;
	private LinearLayout awardLl;
	private RelativeLayout websiteRl;
	private boolean isModify = false;
	private Profile pf;
	private String associations;
	private String interests;
	private String awards;
	private int[] webTypes;//其中0代表博客；1代表相册；2代表公司网站；3代表个人网站
	private String[] webInfos;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new ActivityTemplate().doInActivity(this, R.layout.archieve_eidt_otherinfo);
	}

	@Override
	protected void findView() {
		super.findView();
		setTextValue(R.id.title_txt, "编辑其它信息");
		interestEt = (EditText) findViewById(R.id.interestEdt);
		organizationEt = (EditText) findViewById(R.id.organizationEdt);
		awardEt = (EditText) findViewById(R.id.awardEdt);

		canProvideGroup = (LinearLayout) findViewById(R.id.website_group);
		addProvideIB = (ImageButton) findViewById(R.id.add_website_ib);

		canProvideGroup.removeAllViews();
//		View canProvideInfoView = LayoutInflater.from(EditOtherInfo.this).inflate(R.layout.archieve_edit_otherinfo_website_item,
//				null);
//		((EditText)canProvideInfoView.findViewById(R.id.canprovide_item_ET)).addTextChangedListener(new EditTextListener());
//		canProvideGroup.addView(canProvideInfoView);
//		ImageButton removeProvideIB = (ImageButton) canProvideInfoView.findViewById(R.id.remove_provide_ib);
//		removeProvideIB.setOnClickListener(new RemoveItemListener(canProvideGroup, canProvideInfoView, REMOVE_PROVIDE));
//		webSpinner = (Spinner) canProvideInfoView.findViewById(R.id.webspinner);
//		selectedTv = (TextView) canProvideInfoView.findViewById(R.id.selectedTv);
//		// 将可选内容与ArrayAdapter连接起来
//		adapter = new ArrayAdapter<String>(this, R.layout.website_spinner_item, m);
//
//		// 设置下拉列表的风格
//		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//		// 将adapter 添加到spinner中
//		webSpinner.setAdapter(adapter);
//
//		// 添加事件Spinner事件监听
//		webSpinner.setOnItemSelectedListener(new SpinnerSelectedListener(canProvideInfoView));

		interestLl = (LinearLayout) findViewById(R.id.interestLl);
		organizationLl = (LinearLayout) findViewById(R.id.organizationLl);
		awardLl = (LinearLayout) findViewById(R.id.awardLl);
		websiteRl = (RelativeLayout) findViewById(R.id.website_rl);
		if (getIntent().getBooleanExtra("toOrgansition", false)) {
			interestLl.setVisibility(View.GONE);
			organizationLl.setVisibility(View.VISIBLE);
			awardLl.setVisibility(View.GONE);
			websiteRl.setVisibility(View.GONE);
		} else if (getIntent().getBooleanExtra("toInterest", false)) {
			interestLl.setVisibility(View.VISIBLE);
			organizationLl.setVisibility(View.GONE);
			awardLl.setVisibility(View.GONE);
			websiteRl.setVisibility(View.GONE);
		} else if (getIntent().getBooleanExtra("toAwards", false)) {
			interestLl.setVisibility(View.GONE);
			organizationLl.setVisibility(View.GONE);
			awardLl.setVisibility(View.VISIBLE);
			websiteRl.setVisibility(View.GONE);
		} else if (getIntent().getBooleanExtra("toWebsite", false)) {
			interestLl.setVisibility(View.GONE);
			organizationLl.setVisibility(View.GONE);
			awardLl.setVisibility(View.GONE);
			websiteRl.setVisibility(View.VISIBLE);
		}
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

		OtherInfo o = pf.getUserInfo().getOtherInfo();
		
		if(null != o){
			associations = o.getAssociations();
			interests = o.getInterests();
			awards = o.getAwards();
			if (!TextUtils.isEmpty(associations)) {
				organizationEt.setText(associations);
				organizationEt.setSelection(associations.length());
			}
			if (!TextUtils.isEmpty(interests)) {
				interestEt.setText(interests);
				interestEt.setSelection(interests.length());
			}
			if (!TextUtils.isEmpty(awards)) {
				awardEt.setText(awards);
				awardEt.setSelection(awards.length());
			}
			Site[] sites = o.getSiteList();
			for (int i = 0; i < sites.length; i++) {
				if (provedeCount < CAN_PROVIDE_COUNT && !TextUtils.isEmpty(sites[i].getSiteUrl())) {
					provedeCount++;
					View canProvideInfoView = LayoutInflater.from(EditOtherInfo.this).inflate(R.layout.archieve_edit_otherinfo_website_item,
							null);
					canProvideGroup.addView(canProvideInfoView);
					ImageButton removeProvideIB = (ImageButton) canProvideInfoView.findViewById(R.id.remove_provide_ib);
					removeProvideIB.setOnClickListener(new RemoveItemListener(canProvideGroup, canProvideInfoView, REMOVE_PROVIDE));
					webSpinner = (Spinner) canProvideInfoView.findViewById(R.id.webspinner);
					selectedTv = (TextView) canProvideInfoView.findViewById(R.id.selectedTv);
					// 将可选内容与ArrayAdapter连接起来
					adapter = new ArrayAdapter<String>(this, R.layout.website_spinner_item, m);
			
					// 设置下拉列表的风格
					adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			
					// 将adapter 添加到spinner中
					webSpinner.setAdapter(adapter);
					String type = sites[i].getSiteType();
					if(type.startsWith(m[0])){
						webSpinner.setSelection(0);
					}else if(type.startsWith(m[1])){
						webSpinner.setSelection(1);
					}else if(type.startsWith(m[2])){
						webSpinner.setSelection(2);
					}else if(type.startsWith(m[3])){
						webSpinner.setSelection(3);
					}
					((EditText)canProvideInfoView.findViewById(R.id.canprovide_item_ET)).setText(sites[i].getSiteUrl());
					((EditText)canProvideInfoView.findViewById(R.id.canprovide_item_ET)).setSelection(sites[i].getSiteUrl().length());
					((EditText)canProvideInfoView.findViewById(R.id.canprovide_item_ET)).addTextChangedListener(new EditTextListener());
					// 添加事件Spinner事件监听
					webSpinner.setOnItemSelectedListener(new SpinnerSelectedListener(canProvideInfoView));

				}
			}
			if(provedeCount >= CAN_PROVIDE_COUNT){
				addProvideIB.setVisibility(View.GONE);
			}
		}
		organizationEt.addTextChangedListener(new EditTextListener());
		interestEt.addTextChangedListener(new EditTextListener());
		awardEt.addTextChangedListener(new EditTextListener());
	}

	@Override
	protected void initListener() {
		super.initListener();
		addProvideIB.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (provedeCount < CAN_PROVIDE_COUNT) {
					View canProvideInfoView = LayoutInflater.from(
							EditOtherInfo.this).inflate(
							R.layout.archieve_edit_otherinfo_website_item,
							null);
					((EditText)canProvideInfoView.findViewById(R.id.canprovide_item_ET)).addTextChangedListener(new EditTextListener());
					((EditText)canProvideInfoView.findViewById(R.id.canprovide_item_ET)).requestFocus();
					
					canProvideGroup.addView(canProvideInfoView);
					ImageButton removeProvideIB = (ImageButton) canProvideInfoView
							.findViewById(R.id.remove_provide_ib);
					removeProvideIB
							.setOnClickListener(new RemoveItemListener(
									canProvideGroup, canProvideInfoView,
									REMOVE_PROVIDE));

					provedeCount++;
					webSpinner = (Spinner) canProvideInfoView.findViewById(R.id.webspinner);
					// 将可选内容与ArrayAdapter连接起来
					adapter = new ArrayAdapter<String>(EditOtherInfo.this,
							R.layout.website_spinner_item, m);

					// 设置下拉列表的风格
					adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

					// 将adapter 添加到spinner中
					webSpinner.setAdapter(adapter);
					// 添加事件Spinner事件监听
					webSpinner.setOnItemSelectedListener(new SpinnerSelectedListener(canProvideInfoView));
				}
				if (provedeCount == CAN_PROVIDE_COUNT) {
					addProvideIB.setVisibility(View.GONE);
				}
				
			}
		});
	}
	@Override
	public void goBack() {
		super.goBack();
		if(isModify){
			Dialog alertDialog = new AlertDialog.Builder(EditOtherInfo.this).setTitle("提示")
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
		}else{
			finish();
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
		}
	}
	@Override
	public void goSave() {
		super.goSave();
		checkPreSave();
		if(getIntent().getBooleanExtra("toOrgansition", false)){
			new EditOrgansitionInfoTask(EditOtherInfo.this){
				public void doPre() {
					showDialog(1);
				};
				public void doPost(com.itcalf.renhe.dto.MessageBoardOperation result) {
					super.doPost(result);
					if (result == null) {
						removeDialog(1);
						Toast.makeText(EditOtherInfo.this, "网络异常，请重试", Toast.LENGTH_SHORT).show();
					} else if (result.getState() == 1) {
						//更新本地缓存
						UserInfo userInfo = pf.getUserInfo();
						OtherInfo otherInfo = userInfo.getOtherInfo();
						otherInfo.setAssociations(associations);
						userInfo.setOtherInfo(otherInfo);
						pf.setUserInfo(userInfo);
						CacheManager.getInstance().populateData(EditOtherInfo.this)
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
					} else if (result.getState() == -3) {
						removeDialog(1);
						Toast.makeText(EditOtherInfo.this, "长度过长，长度不能超过200个字", Toast.LENGTH_SHORT).show();
					} 
				
				};
			}.execute(getRenheApplication().getUserInfo().getSid(), getRenheApplication().getUserInfo().getAdSId(),associations);
		}else if(getIntent().getBooleanExtra("toInterest", false)){
			new EditInterestInfoTask(EditOtherInfo.this){
				public void doPre() {
					showDialog(1);
				};
				public void doPost(com.itcalf.renhe.dto.MessageBoardOperation result) {
					super.doPost(result);
					if (result == null) {
						removeDialog(1);
						Toast.makeText(EditOtherInfo.this, "网络异常，请重试", Toast.LENGTH_SHORT).show();
					} else if (result.getState() == 1) {
						//更新本地缓存
						UserInfo userInfo = pf.getUserInfo();
						OtherInfo otherInfo = userInfo.getOtherInfo();
						otherInfo.setInterests(interests);
						userInfo.setOtherInfo(otherInfo);
						pf.setUserInfo(userInfo);
						CacheManager.getInstance().populateData(EditOtherInfo.this)
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
					} else if (result.getState() == -3) {
						removeDialog(1);
						Toast.makeText(EditOtherInfo.this, "长度过长，长度不能超过200个字", Toast.LENGTH_SHORT).show();
					} 
				
				};
			}.execute(getRenheApplication().getUserInfo().getSid(), getRenheApplication().getUserInfo().getAdSId(),interests);
		}else if(getIntent().getBooleanExtra("toAwards", false)){
			new EditAwardInfoTask(EditOtherInfo.this){
				public void doPre() {
					showDialog(1);
				};
				public void doPost(com.itcalf.renhe.dto.MessageBoardOperation result) {
					super.doPost(result);
					if (result == null) {
						removeDialog(1);
						Toast.makeText(EditOtherInfo.this, "网络异常，请重试", Toast.LENGTH_SHORT).show();
					} else if (result.getState() == 1) {
						//更新本地缓存
						UserInfo userInfo = pf.getUserInfo();
						OtherInfo otherInfo = userInfo.getOtherInfo();
						otherInfo.setAwards(awards);
						userInfo.setOtherInfo(otherInfo);
						pf.setUserInfo(userInfo);
						CacheManager.getInstance().populateData(EditOtherInfo.this)
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
					} else if (result.getState() == -3) {
						removeDialog(1);
						Toast.makeText(EditOtherInfo.this, "长度过长，长度不能超过200个字", Toast.LENGTH_SHORT).show();
					} 
				
				};
			}.execute(getRenheApplication().getUserInfo().getSid(), getRenheApplication().getUserInfo().getAdSId(),awards);
		}else if(getIntent().getBooleanExtra("toWebsite", false)){
			if (null != canProvideGroup) {
				int childCount = canProvideGroup.getChildCount();
				webTypes = new int[childCount];
				webInfos = new String[childCount];
				for (int i = 0; i < childCount; i++) {
					String s = ((EditText) canProvideGroup.getChildAt(i)
							.findViewById(R.id.canprovide_item_ET))
							.getText().toString();
					String type = ((TextView)canProvideGroup.getChildAt(i)
							.findViewById(R.id.selectedTv))
							.getText().toString();
					if(type.equals(m[0])){
						webTypes[i] = 0;
					}else if(type.equals(m[1])){
						webTypes[i] = 1;
					}else if(type.equals(m[2])){
						webTypes[i] = 2;
					}else if(type.equals(m[3])){
						webTypes[i] = 3;
					}
					webInfos[i] = s;
				}

				new EditWebsiteInfoTask(EditOtherInfo.this,webTypes,webInfos){
					public void doPre() {
						showDialog(1);
					};
					public void doPost(com.itcalf.renhe.dto.MessageBoardOperation result) {
						super.doPost(result);
						if (result == null) {
							removeDialog(1);
							Toast.makeText(EditOtherInfo.this, "网络异常，请重试", Toast.LENGTH_SHORT).show();
						} else if (result.getState() == 1) {
							//更新本地缓存
							UserInfo userInfo = pf.getUserInfo();
							OtherInfo otherInfo = userInfo.getOtherInfo();
							Site[] sites = new Site[webInfos.length];
							for(int i = 0; i < webInfos.length; i++){
								Site site = new Site();
								if(webInfos[i] != null){
									site.setSiteType(m[webTypes[i]]);
									site.setSiteUrl(webInfos[i]);
									sites[i] = site;
								}
							}
							otherInfo.setSiteList(sites);
							userInfo.setOtherInfo(otherInfo);
							pf.setUserInfo(userInfo);
							CacheManager.getInstance().populateData(EditOtherInfo.this)
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
						} else if (result.getState() == -3) {
							removeDialog(1);
							Toast.makeText(EditOtherInfo.this, "个数过多，个数不超过3个", Toast.LENGTH_SHORT).show();
						} else if (result.getState() == -4) {
							removeDialog(1);
							if(Constants.renhe_log){
								Log.e("editWebsite", "webTypes与webInfos长度不符");
//								Toast.makeText(EditOtherInfo.this, "webTypes与webInfos长度不符", Toast.LENGTH_SHORT).show();
							}
						} 
					
					};
				}.execute(getRenheApplication().getUserInfo().getSid(), getRenheApplication().getUserInfo().getAdSId());
			
			}
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
			default:
				break;
			}
		}

	}

	class SpinnerSelectedListener implements OnItemSelectedListener {
		View view;

		public SpinnerSelectedListener(View view) {
			this.view = view;
		}

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			if (getIntent().getBooleanExtra("toWebsite", false)) {
				isModify = true;
			}
			TextView selectedTv = (TextView) view.findViewById(R.id.selectedTv);
			selectedTv.setText(m[arg2]);
		}

		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}

	private boolean checkPreSave() {
		associations = organizationEt.getText().toString().trim();
//		if (TextUtils.isEmpty(associations)) {
//			Toast.makeText(EditOtherInfo.this, "姓名不能为空", Toast.LENGTH_SHORT).show();
//			organizationEt.requestFocus();
//			return false;
//		}
		interests = interestEt.getText().toString().trim();
//		if (TextUtils.isEmpty(address)) {
//			Toast.makeText(EditOtherInfo.this, "所在地不能为空", Toast.LENGTH_SHORT).show();
//			return false;
//		}
		awards = awardEt.getText().toString().trim();
//		if (TextUtils.isEmpty(industry)) {
//			Toast.makeText(EditOtherInfo.this, "从事行业不能为空", Toast.LENGTH_SHORT).show();
//			return false;
//		}
		return true;
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
	class EditTextListener implements TextWatcher {

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
}
