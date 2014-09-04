package com.itcalf.renhe.context.archives.edit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.itcalf.renhe.R;
import com.itcalf.renhe.context.relationship.AdvanceSearchUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * Title: EditEduInfoSelectSchool.java<br>
 * Description: <br>
 * Copyright (c) 人和网版权所有 2014    <br>
 * Create DateTime: 2014-7-23 下午3:42:31 <br>
 * @author wangning
 */
public class EditEduInfoSelectSchool extends SherlockListActivity {

	private EditText schoolEt;
	private SimpleAdapter mSimpleAdapter;
	private List<Map<String, Object>> schoolList = new ArrayList<Map<String, Object>>();
	private Handler mHandler;
	private Runnable run;
	private SQLiteDatabase db;
	private static final String path = Environment.getExternalStorageDirectory() + File.separator + "Android" + File.separator
			+ "com.renheandroidnew" + File.separator;
	private static final String DBNAME = "mschool.db";
	private static final String TABLE_NAME = "school";
	public final static int NO_THIS_SCHOOL = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.school_select);
		if (null != getSupportActionBar()) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setDisplayShowHomeEnabled(false);
		}
		setTitle("选择院校");
		schoolEt = (EditText) findViewById(R.id.schoolEt);
		mSimpleAdapter = new SimpleAdapter(this, schoolList, R.layout.school_item, new String[] { "name", "id" }, new int[] {
				R.id.name, R.id.id });
		setListAdapter(mSimpleAdapter);

		String schoolString = getIntent().getStringExtra("school");
		if (!TextUtils.isEmpty(schoolString)) {
			schoolEt.setText(schoolString);
		}
		initDB();
		mHandler = new Handler();
		run = new Runnable() {

			@Override
			public void run() {
				populateCity(mSimpleAdapter, schoolEt.getText().toString().trim());
			}
		};
		//		((TextView)findViewById(R.id.title_txt)).setText("院校");
		schoolEt.addTextChangedListener(tbxEdit_TextChanged);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem saveItem = menu.findItem(R.id.item_save);
		saveItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		saveItem.setTitle("确定");
		saveItem.setVisible(true);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
			return true;
		case R.id.item_save:
			String name = schoolEt.getText().toString().trim();
			int id = AdvanceSearchUtil.getSchoolId(db, TABLE_NAME, name);
			Intent intent = new Intent();
			intent.putExtra("name", name);
			intent.putExtra("id", id);
			setResult(RESULT_OK, intent);
			finish();
			overridePendingTransition(0, 0);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("SelectChinaCityMainScreen"); //统计页面
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("SelectChinaCityMainScreen"); // 保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息 
		MobclickAgent.onPause(this);
	}

	private void populateCity(SimpleAdapter mAdapter, String keyword) {
		if (TextUtils.isEmpty(keyword)) {
			if (null != schoolList) {
				schoolList.clear();
			} else {
				schoolList = new ArrayList<Map<String, Object>>();
			}
		} else {
			try {
				AdvanceSearchUtil.copyDB(this, DBNAME);
				if (db == null) {
					db = SQLiteDatabase.openOrCreateDatabase(path + DBNAME, null);
				}
				schoolList = getSchools(keyword);
			} catch (IOException e) {
				e.printStackTrace();
			}
			mSimpleAdapter = null;
			mSimpleAdapter = new SimpleAdapter(this, schoolList, R.layout.school_item, new String[] { "name", "id" }, new int[] {
					R.id.name, R.id.id });
			//			schoolLv.setAdapter(mSimpleAdapter);
			setListAdapter(mSimpleAdapter);
		}
		mAdapter.notifyDataSetChanged();
	}

	private void initDB() {
		try {
			AdvanceSearchUtil.copyDB(this, DBNAME);
			if (db == null) {
				db = SQLiteDatabase.openOrCreateDatabase(path + DBNAME, null);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private List<Map<String, Object>> getSchools(String key) {

		if (null != schoolList) {
			schoolList.clear();
		} else {
			schoolList = new ArrayList<Map<String, Object>>();
		}
		schoolList = AdvanceSearchUtil.getSchool(db, TABLE_NAME, key);
		if (schoolList == null) {
			schoolList = new ArrayList<Map<String, Object>>();
		}
		return schoolList;

	}

	private TextWatcher tbxEdit_TextChanged = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			mHandler.postDelayed(run, 500);
		}

	};

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		String name = (String) schoolList.get(position).get("name");
		int mid = (Integer) schoolList.get(position).get("id");
		Intent intent = new Intent();
		intent.putExtra("name", name);
		intent.putExtra("id", mid);
		setResult(RESULT_OK, intent);
		finish();
		overridePendingTransition(0, 0);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(0, 0);
	}

}
