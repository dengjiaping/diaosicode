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
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.itcalf.renhe.R;
import com.itcalf.renhe.cache.CacheManager;
import com.itcalf.renhe.context.archives.MyHomeArchivesActivity;
import com.itcalf.renhe.context.archives.edit.task.EditContactInfoTask;
import com.itcalf.renhe.context.template.ActivityTemplate;
import com.itcalf.renhe.context.template.BaseActivity;
import com.itcalf.renhe.dto.Profile;
import com.itcalf.renhe.dto.Profile.UserInfo;
import com.itcalf.renhe.dto.Profile.UserInfo.ContactInfo;

/**
 * Title: EditSelfInfo.java<br>
 * Description: <br>
 * Copyright (c) 人和网版权所有 2014    <br>
 * Create DateTime: 2014-7-14 下午4:38:14 <br>
 * @author wangning
 */
public class EditContactInfo extends EditBaseActivity {
	private String qq;
	private String tel;
	private String weixin;
	private EditText qqEt;
	private EditText telEt;
	private EditText weixinEt;
	private boolean isModify = false;
	private Profile pf;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new ActivityTemplate().doInActivity(this, R.layout.archieve_eidt_contactinfo);

	}
	
	@Override
	protected void findView() {
		super.findView();
		setTextValue(R.id.title_txt, "编辑联系方式");
		qqEt = (EditText)findViewById(R.id.company_qq_et);
		telEt = (EditText)findViewById(R.id.company_tel_et);
		weixinEt = (EditText)findViewById(R.id.company_weichat_et);
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
		ContactInfo contactInfo = pf.getUserInfo().getContactInfo();
		if(null != contactInfo){
			qq = contactInfo.getQq();
			tel = contactInfo.getTel();
			weixin = contactInfo.getWeixin();
			if(!TextUtils.isEmpty(qq)){
				qqEt.setText(qq);
				qqEt.setSelection(qq.length());
			}
			if(!TextUtils.isEmpty(tel)){
				telEt.setText(tel);
				telEt.setSelection(tel.length());
			}
			if(!TextUtils.isEmpty(weixin)){
				weixinEt.setText(weixin);
				weixinEt.setSelection(weixin.length());
			}
			
		}
		telEt.addTextChangedListener(new EditTextListener());
		qqEt.addTextChangedListener(new EditTextListener());
		weixinEt.addTextChangedListener(new EditTextListener());
	}
	@Override
	public void goBack() {
		super.goBack();
		if(isModify){
			Dialog alertDialog = new AlertDialog.Builder(EditContactInfo.this).setTitle("提示")
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
		if (!checkPreSave()) {
			return;
		}
		new EditContactInfoTask(EditContactInfo.this){
			public void doPre() {
				showDialog(1);
			};
			public void doPost(com.itcalf.renhe.dto.MessageBoardOperation result) {
				super.doPost(result);
				if (result == null) {
					removeDialog(1);
					Toast.makeText(EditContactInfo.this, "网络异常，请重试", Toast.LENGTH_SHORT).show();
				} else if (result.getState() == 1) {
					//更新本地缓存
					UserInfo userInfo = pf.getUserInfo();
					ContactInfo contactInfo = userInfo.getContactInfo();
					contactInfo.setTel(tel);
					contactInfo.setQq(qq);
					contactInfo.setWeixin(weixin);
					userInfo.setContactInfo(contactInfo);
					pf.setUserInfo(userInfo);
					CacheManager.getInstance().populateData(EditContactInfo.this)
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
					Toast.makeText(EditContactInfo.this, "固定电话长度过长，长度不能超过30个字符", Toast.LENGTH_SHORT).show();
				} else if (result.getState() == -4) {
					removeDialog(1);
					Toast.makeText(EditContactInfo.this, "QQ长度过长，长度不能超过30个字符", Toast.LENGTH_SHORT).show();
				} else if (result.getState() == -5) {
					removeDialog(1);
					Toast.makeText(EditContactInfo.this, "微信长度过长，长度不能超过30个字符", Toast.LENGTH_SHORT).show();
				} 
			
			};
		}.execute(getRenheApplication().getUserInfo().getSid(), getRenheApplication().getUserInfo().getAdSId(),tel,qq,weixin);
	}
	@Override
	protected void initListener() {
		super.initListener();
	}
	private boolean checkPreSave() {
		tel = telEt.getText().toString().trim();
//		if (TextUtils.isEmpty(tel)) {
//			Toast.makeText(EditSelfInfo.this, "姓名不能为空", Toast.LENGTH_SHORT).show();
//			nameEt.requestFocus();
//			return false;
//		}
		qq = qqEt.getText().toString().trim();
//		if (TextUtils.isEmpty(address)) {
//			Toast.makeText(EditSelfInfo.this, "所在地不能为空", Toast.LENGTH_SHORT).show();
//			return false;
//		}
		weixin = weixinEt.getText().toString().trim();
//		if (TextUtils.isEmpty(industry)) {
//			Toast.makeText(EditSelfInfo.this, "从事行业不能为空", Toast.LENGTH_SHORT).show();
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
