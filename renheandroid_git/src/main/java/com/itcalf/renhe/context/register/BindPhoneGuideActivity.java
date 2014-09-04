  package com.itcalf.renhe.context.register;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itcalf.renhe.R;
import com.itcalf.renhe.context.portal.RegisterActivity;
import com.itcalf.renhe.context.template.ActivityTemplate;
import com.itcalf.renhe.context.template.BaseActivity;
import com.itcalf.renhe.utils.DialogUtil;
import com.itcalf.renhe.utils.DialogUtil.MyDialogClickListener;
import com.itcalf.renhe.utils.RequestDialog;
  /**
   * Title: NewRegisterActivity.java<br>
   * Description: <br>
   * Copyright (c) 人和网版权所有 2014    <br>
   * Create DateTime: 2014-6-9 下午1:14:36 <br>
   * @author wangning
   */
public class BindPhoneGuideActivity extends BaseActivity {
	private Button bindButton;
	private boolean isBind;
	private String mobile = "";
	private TextView isBindTv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new ActivityTemplate().doInActivity(this, R.layout.bind_phone_guide);
	}
	@SuppressLint("NewApi")
	@Override
	protected void findView() {
		super.findView();
		setTextValue(R.id.title_txt, "绑定手机号码");
		if(null != getIntent().getExtras()){
			isBind= getIntent().getExtras().getBoolean("isbind", false);
			mobile= getIntent().getExtras().getString("mobile", "");
		}
		isBindTv = (TextView)findViewById(R.id.isbindTv);
		bindButton = (Button)findViewById(R.id.bindBt);
		if(isBind){
			isBindTv.setText(getString(R.string.bind_bind)+" "+mobile);
			bindButton.setVisibility(View.GONE);
		}else{
			isBindTv.setText(getString(R.string.bind_nobind));
			bindButton.setVisibility(View.VISIBLE);
		}
	}
	@Override
	protected void initData() {
		super.initData();
	}
	@Override
	protected void initListener() {
		super.initListener();
		bindButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				startActivityForResult(BindPhoneActivity.class, 1);
			}
		});
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK && requestCode == 1){
			setResult(RESULT_OK);
			finish();
		}
	}
}

