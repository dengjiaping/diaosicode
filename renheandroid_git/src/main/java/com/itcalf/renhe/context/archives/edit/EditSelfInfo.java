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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.itcalf.renhe.Constants;
import com.itcalf.renhe.R;
import com.itcalf.renhe.cache.CacheManager;
import com.itcalf.renhe.context.archives.MyHomeArchivesActivity;
import com.itcalf.renhe.context.archives.edit.task.EditSelfInfoTask;
import com.itcalf.renhe.context.relationship.AdvanceSearchSelectIndustryMainActivity;
import com.itcalf.renhe.context.relationship.selectindustry.SelectIndustryExpandableListActivity;
import com.itcalf.renhe.context.template.ActivityTemplate;
import com.itcalf.renhe.context.template.BaseActivity;
import com.itcalf.renhe.dto.Profile;
import com.itcalf.renhe.dto.Profile.UserInfo;
import com.itcalf.renhe.dto.Profile.UserInfo.SummaryInfo;

/**
 * Title: EditSelfInfo.java<br>
 * Description: <br>
 * Copyright (c) 人和网版权所有 2014    <br>
 * Create DateTime: 2014-7-14 下午4:38:14 <br>
 * @author wangning
 */
public class EditSelfInfo extends EditBaseActivity {
	private EditText nameEt;
	private TextView areaEt;
	private TextView industryEt;
	private LinearLayout areaLl;
	private LinearLayout industryLl;
	private LinearLayout canProvideGroup;
	private ImageButton addProvideIB;
	private LinearLayout wantGetGroup;
	private ImageButton wantGetIB;

	private final static int AREA_REQUEST_CODE = 10;
	private final static int INDUSTRY_REQUEST_CODE = 11;
	private int cityCode = -1;
	private int industryCode = -1;

	private final static int CAN_PROVIDE_COUNT = 3;
	private final static int WANT_GET_COUNT = 3;
	private final static int REMOVE_PROVIDE = 0;
	private final static int REMOVE_GET = 1;
	private int provedeCount = 1;
	private int getCount = 1;
	private boolean isModify = false;
	
	private String name;
	private int gender;//0代表女；1代表男
	private String address;
	private int addressId;
	private String industry;
	private int industryId;
	private Profile pf;
	private RadioGroup radioGroup;
	private RadioButton femaleRb;
	private RadioButton maleRb;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new ActivityTemplate().doInActivity(this, R.layout.archieve_eidt_selfinfo);
	}

	@Override
	protected void findView() {
		super.findView();
		setTextValue(R.id.title_txt, "基本信息");
		nameEt = (EditText) findViewById(R.id.name_et);
		areaEt = (TextView) findViewById(R.id.area_et);
		industryEt = (TextView) findViewById(R.id.industry_et);
		areaLl = (LinearLayout) findViewById(R.id.area_ll);
		industryLl = (LinearLayout) findViewById(R.id.industry_ll);
		canProvideGroup = (LinearLayout) findViewById(R.id.can_provide_group);
		addProvideIB = (ImageButton) findViewById(R.id.add_can_provide_ib);
		wantGetGroup = (LinearLayout) findViewById(R.id.want_get_group);
		wantGetIB = (ImageButton) findViewById(R.id.add_want_get_ib);

		canProvideGroup.removeAllViews();
		View canProvideInfoView = LayoutInflater.from(EditSelfInfo.this).inflate(R.layout.archieve_edit_selfinfo_canprovide_info,
				null);
		canProvideGroup.addView(canProvideInfoView);
		ImageButton removeProvideIB = (ImageButton) canProvideInfoView.findViewById(R.id.remove_provide_ib);
		removeProvideIB.setOnClickListener(new RemoveItemListener(canProvideGroup, canProvideInfoView, REMOVE_PROVIDE));

		wantGetGroup.removeAllViews();
		View wantGetInfoView = LayoutInflater.from(EditSelfInfo.this).inflate(R.layout.archieve_edit_selfinfo_canprovide_info,
				null);
		wantGetGroup.addView(wantGetInfoView);
		ImageButton removeGetIB = (ImageButton) wantGetInfoView.findViewById(R.id.remove_provide_ib);
		removeGetIB.setOnClickListener(new RemoveItemListener(wantGetGroup, wantGetInfoView, REMOVE_GET));
		
		radioGroup = (RadioGroup)findViewById(R.id.sex_rg);
		femaleRb = (RadioButton)findViewById(R.id.female_rb);
		maleRb = (RadioButton)findViewById(R.id.male_rb);

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
	name = pf.getUserInfo().getName();
	gender = pf.getUserInfo().getGender();
	address = pf.getUserInfo().getLocation();
	addressId = pf.getUserInfo().getAddressId();
	industry = pf.getUserInfo().getIndustry();
	industryId = pf.getUserInfo().getIndustryId();
	if(!TextUtils.isEmpty(name)){
		nameEt.setText(name);
		if(pf.getUserInfo().isRealName()){
			nameEt.setTextColor(getResources().getColor(R.color.new_archieve_fourbt_textcolor));
			nameEt.setEnabled(false);
		}
	}
	if(!TextUtils.isEmpty(address)){
		areaEt.setText(address);
	}
	if(!TextUtils.isEmpty(industry)){
		industryEt.setText(industry);
	}
	if(gender == 0){
		maleRb.setChecked(true);
	}else{
		femaleRb.setChecked(true);
	}
	nameEt.addTextChangedListener(new EditTextListener());
	areaEt.addTextChangedListener(new EditTextListener());
	industryEt.addTextChangedListener(new EditTextListener());
}
	@Override
	protected void initListener() {
		super.initListener();
		areaLl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(EditSelfInfo.this, SelectCityWithChinaAndForeignActivity.class);
				startActivityForResult(intent, AREA_REQUEST_CODE);
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			}
		});
		industryLl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				Intent intent = new Intent(EditSelfInfo.this, AdvanceSearchSelectIndustryMainActivity.class);
				Intent intent = new Intent(EditSelfInfo.this, SelectIndustryExpandableListActivity.class);
				
				intent.putExtra("isFromArcheveEdit", true);
				intent.putExtra("selectedId", industryId);
				intent.putExtra("selectedIndustry", industry);
				startActivityForResult(intent, INDUSTRY_REQUEST_CODE);
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			}
		});
		addProvideIB.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (provedeCount < CAN_PROVIDE_COUNT) {
					View canProvideInfoView = LayoutInflater.from(EditSelfInfo.this).inflate(
							R.layout.archieve_edit_selfinfo_canprovide_info, null);
					canProvideGroup.addView(canProvideInfoView);
					ImageButton removeProvideIB = (ImageButton) canProvideInfoView.findViewById(R.id.remove_provide_ib);
					removeProvideIB
							.setOnClickListener(new RemoveItemListener(canProvideGroup, canProvideInfoView, REMOVE_PROVIDE));

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
					View wantGetInfoView = LayoutInflater.from(EditSelfInfo.this).inflate(
							R.layout.archieve_edit_selfinfo_canprovide_info, null);
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
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int arg1) {
				isModify = true;
	            switch (group.getCheckedRadioButtonId()) {
	            case R.id.female_rb:
	                gender = 1;
	                break;
	            case R.id.male_rb:
	            	gender = 0;
	                break;
	            }
	        }
		});
	}
	@Override
	public void goBack(){
		if(isModify){
			Dialog alertDialog = new AlertDialog.Builder(EditSelfInfo.this).setTitle("提示")
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
	public void goSave(){
		if (!checkPreSave()) {
			return;
		}
		new EditSelfInfoTask(EditSelfInfo.this){
			public void doPre() {
				showDialog(1);
			};
			public void doPost(com.itcalf.renhe.dto.MessageBoardOperation result) {

				super.doPost(result);
				if (result == null) {
					removeDialog(1);
					Toast.makeText(EditSelfInfo.this, "网络异常，请重试", Toast.LENGTH_SHORT).show();
				} else if (result.getState() == 1) {
					//更新本地缓存
					UserInfo userInfo = pf.getUserInfo();
					userInfo.setName(name);
					userInfo.setGender(gender);
					userInfo.setLocation(address);
					userInfo.setAddressId(addressId);
					userInfo.setIndustry(industry);
					userInfo.setIndustryId(industryId);
					pf.setUserInfo(userInfo);
					CacheManager.getInstance().populateData(EditSelfInfo.this)
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
					Toast.makeText(EditSelfInfo.this, "姓名不能为空", Toast.LENGTH_SHORT).show();
				} else if (result.getState() == -4) {
					removeDialog(1);
					Toast.makeText(EditSelfInfo.this, "性别不能为空", Toast.LENGTH_SHORT).show();
				} else if (result.getState() == -5) {
					removeDialog(1);
					Toast.makeText(EditSelfInfo.this, "从事行业不能为空", Toast.LENGTH_SHORT).show();
				} else {
					removeDialog(1);
					Toast.makeText(EditSelfInfo.this, "所在地不能为空", Toast.LENGTH_SHORT).show();
				}
			
			};
		}.execute(getRenheApplication().getUserInfo().getSid(), getRenheApplication().getUserInfo().getAdSId(),name,gender+"",industryId+"",addressId+"");
	
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == AREA_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				String yourCity = data.getStringExtra("yourcity");
				String yourCityCodetemp = data.getStringExtra("yourcitycode");
				if (yourCity != null && yourCityCodetemp != null) {
					areaEt.setText(yourCity);
					cityCode = Integer.parseInt(yourCityCodetemp);
					address = yourCity;
					addressId = cityCode;
				}
			}
		} else if (requestCode == INDUSTRY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				String yourIndustry = data.getStringExtra("yourindustry");
				String yourIndustryCodetemp = data.getStringExtra("yourindustrycode");
				String yourindustrySection = data.getStringExtra("yourindustrySection");
				if (yourIndustry != null && yourIndustryCodetemp != null) {
					industryEt.setText(yourIndustry);
					industryCode = Integer.parseInt(yourIndustryCodetemp);
					industry = yourIndustry;
					industryId = industryCode;
				}
			}
		}
	}
	private boolean checkPreSave() {
		name = nameEt.getText().toString().trim();
		if (TextUtils.isEmpty(name)) {
			Toast.makeText(EditSelfInfo.this, "姓名不能为空", Toast.LENGTH_SHORT).show();
			nameEt.requestFocus();
			return false;
		}
		address = areaEt.getText().toString().trim();
		if (TextUtils.isEmpty(address)) {
			Toast.makeText(EditSelfInfo.this, "所在地不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}
		industry = industryEt.getText().toString().trim();
		if (TextUtils.isEmpty(industry)) {
			Toast.makeText(EditSelfInfo.this, "从事行业不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}
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
