package com.itcalf.renhe.context.archives.edit;

import java.util.ArrayList;
import java.util.List;

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
import com.itcalf.renhe.context.archives.edit.EditProvideGetInfo.EditTextListener;
import com.itcalf.renhe.context.archives.edit.EditProvideGetInfo.RemoveItemListener;
import com.itcalf.renhe.context.archives.edit.task.EditSummaryInfoProfessionTask;
import com.itcalf.renhe.context.archives.edit.task.EditSummaryInfoSpecialtiesTask;
import com.itcalf.renhe.context.template.ActivityTemplate;
import com.itcalf.renhe.context.template.BaseActivity;
import com.itcalf.renhe.dto.MessageBoardOperation;
import com.itcalf.renhe.dto.Profile;
import com.itcalf.renhe.dto.Profile.UserInfo;
import com.itcalf.renhe.dto.Profile.UserInfo.SpecialtiesInfo;
import com.itcalf.renhe.dto.Profile.UserInfo.SummaryInfo;

/**
 * Title: EditSelfInfo.java<br>
 * Description: <br>
 * Copyright (c) 人和网版权所有 2014    <br>
 * Create DateTime: 2014-7-14 下午4:38:14 <br>
 * @author wangning
 */
public class EditSummaryInfo extends EditBaseActivity {
	private EditText selfInfoEt;
	private LinearLayout professionGroup;
	private ImageButton addFessionIB;

	private final static int PROFESSION_COUNT = 12;
	private int professionCount = 0;
	private LinearLayout specialtiesLl;
	private RelativeLayout professionalLl;
	private boolean isModify = false;
	private String professions;
	private Profile pf;
	private boolean isSpecialtiesNull = true;
	private SpecialtiesInfo[] specialtiesInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new ActivityTemplate().doInActivity(this, R.layout.archieve_eidt_summaryinfo);
	}

	@Override
	protected void findView() {
		super.findView();
		setTextValue(R.id.title_txt, "概要信息");
		selfInfoEt = (EditText) findViewById(R.id.contentEdt);
		professionGroup = (LinearLayout) findViewById(R.id.profession_group);
		addFessionIB = (ImageButton) findViewById(R.id.add_profession_IB);
		specialtiesLl = (LinearLayout) findViewById(R.id.self_ll);
		professionalLl = (RelativeLayout) findViewById(R.id.profession_rl);

		professionGroup.removeAllViews();

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
		if (getIntent().getBooleanExtra("toSpecialties", false)) {
			specialtiesLl.setVisibility(View.VISIBLE);
			professionalLl.setVisibility(View.GONE);
			if (null != getIntent().getStringExtra("professionals")) {
				professions = getIntent().getStringExtra("professionals");
			}
			if (!TextUtils.isEmpty(professions)) {
				selfInfoEt.setText(professions);
				selfInfoEt.setSelection(professions.length());
			}
		} else {
			specialtiesInfo = pf.getUserInfo().getSpecialtiesInfo();
			specialtiesLl.setVisibility(View.GONE);
			professionalLl.setVisibility(View.VISIBLE);
			for (int i = 0; i < specialtiesInfo.length; i++) {
				if (!TextUtils.isEmpty(specialtiesInfo[i].getTitle())) {
					professionCount++;
					View canProvideInfoView = LayoutInflater.from(EditSummaryInfo.this).inflate(
							R.layout.archieve_edit_selfinfo_canprovide_info, null);
					((EditText) canProvideInfoView.findViewById(R.id.canprovide_item_ET)).setText(specialtiesInfo[i].getTitle());
					((EditText) canProvideInfoView.findViewById(R.id.canprovide_item_ET)).setSelection(specialtiesInfo[i]
							.getTitle().length());
					((EditText) canProvideInfoView.findViewById(R.id.canprovide_item_ET))
							.addTextChangedListener(new EditTextListener());
					professionGroup.addView(canProvideInfoView);
					ImageButton removeProvideIB = (ImageButton) canProvideInfoView.findViewById(R.id.remove_provide_ib);
					removeProvideIB.setOnClickListener(new RemoveItemListener(professionGroup, canProvideInfoView));

				}
			}
			if(professionCount >= PROFESSION_COUNT){
				addFessionIB.setVisibility(View.GONE);
			}else{
				addFessionIB.setVisibility(View.VISIBLE);
			}

		}
	}

	@Override
	protected void initListener() {
		super.initListener();
		addFessionIB.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (professionCount < PROFESSION_COUNT) {
					View canProvideInfoView = LayoutInflater.from(EditSummaryInfo.this).inflate(
							R.layout.archieve_edit_selfinfo_canprovide_info, null);
					professionGroup.addView(canProvideInfoView);
					ImageButton removeProvideIB = (ImageButton) canProvideInfoView.findViewById(R.id.remove_provide_ib);
					removeProvideIB.setOnClickListener(new RemoveItemListener(professionGroup, canProvideInfoView));
					((EditText) canProvideInfoView.findViewById(R.id.canprovide_item_ET)).addTextChangedListener(new EditTextListener());
					((EditText) canProvideInfoView.findViewById(R.id.canprovide_item_ET)).requestFocus();
					professionCount++;
				}
				if (professionCount == PROFESSION_COUNT) {
					addFessionIB.setVisibility(View.GONE);
				}
			}
		});
		if (getIntent().getBooleanExtra("toSpecialties", false)) {
			selfInfoEt.addTextChangedListener(new EditTextListener());
		}
	}
	@Override
	public void goBack() {
		super.goBack();
		if (isModify) {
			Dialog alertDialog = new AlertDialog.Builder(EditSummaryInfo.this).setTitle("提示")
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
//		imm.hideSoftInputFromWindow(arg0.getWindowToken(), 0);
		//个人简介
		if (getIntent().getBooleanExtra("toSpecialties", false)) {
			if (TextUtils.isEmpty(selfInfoEt.getText().toString().trim())) {
				Toast.makeText(EditSummaryInfo.this, "个人简介不能为空", Toast.LENGTH_SHORT).show();
			} else {

				new EditSummaryInfoProfessionTask(EditSummaryInfo.this) {
					@Override
					public void doPre() {
						super.doPre();
						showDialog(1);

					}

					@Override
					public void doPost(MessageBoardOperation result) {
						super.doPost(result);
						if (result == null) {
							removeDialog(1);
							Toast.makeText(EditSummaryInfo.this, "网络异常，请重试", Toast.LENGTH_SHORT).show();
						} else if (result.getState() == 1) {
							//更新本地缓存
							UserInfo userInfo = pf.getUserInfo();
							SummaryInfo sInfo = userInfo.getSummaryInfo();
							sInfo.setProfessional(selfInfoEt.getText().toString().trim());
							userInfo.setSummaryInfo(sInfo);
							pf.setUserInfo(userInfo);
							CacheManager.getInstance().populateData(EditSummaryInfo.this)
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
							Toast.makeText(EditSummaryInfo.this, "个人简介不能为空", Toast.LENGTH_SHORT).show();
						} else if (result.getState() == -4) {
							removeDialog(1);
							Toast.makeText(EditSummaryInfo.this, "个人简介长度过长，长度不能超过500个字", Toast.LENGTH_SHORT).show();
						} else {
							removeDialog(1);
							Toast.makeText(EditSummaryInfo.this, "发生未知错误，请重试", Toast.LENGTH_SHORT).show();
						}
					}
				}.execute(getRenheApplication().getUserInfo().getSid(), getRenheApplication().getUserInfo().getAdSId(),
						selfInfoEt.getText().toString().trim());
			}
		} else {
			//个人专长
			String[] specialties = null;
			List<String> tempList = new ArrayList<String>();
			if (null != professionGroup) {
				int childCount = professionGroup.getChildCount();
				for (int i = 0; i < childCount; i++) {
					String s = ((EditText) professionGroup.getChildAt(i).findViewById(R.id.canprovide_item_ET)).getText()
							.toString().trim();
					if (!TextUtils.isEmpty(s)) {
						isSpecialtiesNull = false;
						tempList.add(s);
					}
				}
			}
			if (isSpecialtiesNull) {
				Toast.makeText(EditSummaryInfo.this, "个人专长不能为空", Toast.LENGTH_SHORT).show();
				return ;
			} else {
				specialties = new String[tempList.size()];
				for (int i = 0; i < tempList.size(); i++) {
					specialties[i] = tempList.get(i);
				}
				//						specialties = (String[]) tempList.toArray();
			}
			if (null == specialties || specialties.length <= 0) {
				Toast.makeText(EditSummaryInfo.this, "个人专长不能为空", Toast.LENGTH_SHORT).show();
				return ;
			}
			final SpecialtiesInfo[] finalspecialtiesInfo = new SpecialtiesInfo[specialties.length];
			for (int i = 0; i < specialties.length; i++) {
				SpecialtiesInfo mInfo = new SpecialtiesInfo();
				mInfo.setTitle(specialties[i]);
				finalspecialtiesInfo[i] = mInfo;
			}

			new EditSummaryInfoSpecialtiesTask(EditSummaryInfo.this, specialties) {
				@Override
				public void doPre() {
					super.doPre();
					showDialog(1);

				}

				@Override
				public void doPost(MessageBoardOperation result) {
					super.doPost(result);
					if (result == null) {
						removeDialog(1);
						Toast.makeText(EditSummaryInfo.this, "网络异常，请重试", Toast.LENGTH_SHORT).show();
					} else if (result.getState() == 1) {
						//更新本地缓存
						UserInfo userInfo = pf.getUserInfo();
						SummaryInfo sInfo = userInfo.getSummaryInfo();
						userInfo.setSummaryInfo(sInfo);
						userInfo.setSpecialtiesInfo(finalspecialtiesInfo);
						pf.setUserInfo(userInfo);
						CacheManager.getInstance().populateData(EditSummaryInfo.this)
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
						Toast.makeText(EditSummaryInfo.this, "个人专长不能为空", Toast.LENGTH_SHORT).show();
					} else if (result.getState() == -4) {
						removeDialog(1);
						Toast.makeText(EditSummaryInfo.this, "个人专长单项长度过长，长度不能超过30个字", Toast.LENGTH_SHORT).show();
					} else if (result.getState() == -5) {
						removeDialog(1);
						Toast.makeText(EditSummaryInfo.this, "个人专长数量过多，不能超过12个", Toast.LENGTH_SHORT).show();
					} else {
						removeDialog(1);
						Toast.makeText(EditSummaryInfo.this, "发生未知错误，请重试", Toast.LENGTH_SHORT).show();
					}
				}

			}.execute(getRenheApplication().getUserInfo().getSid(), getRenheApplication().getUserInfo().getAdSId());
		}
	}
	class RemoveItemListener implements OnClickListener {
		LinearLayout groupView;
		View itemView;

		public RemoveItemListener(LinearLayout groupView, View itemView) {
			this.groupView = groupView;
			this.itemView = itemView;
		}

		@Override
		public void onClick(View arg0) {
			isModify = true;
			if (null != groupView) {
				groupView.removeView(itemView);
			}
			professionCount--;
			if (professionCount < PROFESSION_COUNT) {
				addFessionIB.setVisibility(View.VISIBLE);
			}
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
