package com.itcalf.renhe.context.relationship;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import select.wheel.widget.OnWheelScrollListener;
import select.wheel.widget.WheelView;
import select.wheel.widget.adapters.ArrayWheelAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.itcalf.renhe.R;

public class WheelActivity extends Activity {
	private static final String path = Environment.getExternalStorageDirectory() + File.separator + "Android" + File.separator
			+ "com.renheandroidnew" + File.separator;
	private static final String TAG = "MainActivity";
	private static final String DBNAME = "city.db";
	private static final String TABLE_NAME = "mycity";
	private static final String IND_DBNAME = "industry";
	private static final String IND_TABLE_NAME = "industry";
	private SQLiteDatabase db;
	private Map<String, Integer> provinceMap;
	private Map<String, Integer> cityMap;
	private Map<String, Integer> areaMap;

	private String[] provinceArray;
	private String[] cityArray;
	private String[] areaArray;

	private WheelView provinceWheelView;
	private WheelView cityWheelView;
	private WheelView areaWheelView;

	private ProviceCityAreaAdapter provinceAdapter;
	private ProviceCityAreaAdapter cityAdapter;
	private ProviceCityAreaAdapter areaAdapter;
	LinearLayout mainLayout;
	private TextView currentSelectTv;
	String flag = "";
	public static final int ALL_AREA = -1;
	public static final int ALL_CHINA = -2;
	public static final int ALL_FORGIGN = -3;
	public static final int ALL_INDUSTRY = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.advanced_search_wheel);
		mainLayout = (LinearLayout) findViewById(R.id.mainlayout);
		currentSelectTv = (TextView) findViewById(R.id.current_select_tv);
		initWheelView(true);
		findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				if (null != flag && flag.equals("area")) {
					if (provinceArray[provinceWheelView.getCurrentItem()].equals("全球")) {
						intent.putExtra("yourcity", "全球");
						intent.putExtra("yourcitycode", ALL_AREA + "");
					} else if (provinceArray[provinceWheelView.getCurrentItem()].equals("国内")) {
						intent.putExtra("yourcity", "国内");
						intent.putExtra("yourcitycode", ALL_CHINA + "");
					} else if (provinceArray[provinceWheelView.getCurrentItem()].equals("海外")) {
						intent.putExtra("yourcity", "海外");
						intent.putExtra("yourcitycode", ALL_FORGIGN + "");
					} else {
						if (cityArray[cityWheelView.getCurrentItem()].equals("全部")) {
							intent.putExtra("yourcity", provinceArray[provinceWheelView.getCurrentItem()]);
						} else {
							intent.putExtra("yourcity", provinceArray[provinceWheelView.getCurrentItem()] + " "
									+ cityArray[cityWheelView.getCurrentItem()]);
						}
						intent.putExtra("yourcitycode", cityMap.get(cityArray[cityWheelView.getCurrentItem()]) + "");
					}

				} else if (null != flag && flag.equals("industry")) {
					if (provinceArray[provinceWheelView.getCurrentItem()].equals("全部")) {
						intent.putExtra("yourindustry", "全部");
						intent.putExtra("yourindustrycode", ALL_INDUSTRY + "");
					} else {
						if (cityArray[cityWheelView.getCurrentItem()].equals("全部")) {
							intent.putExtra("yourindustry", provinceArray[provinceWheelView.getCurrentItem()]);
						} else {
							intent.putExtra("yourindustry", cityArray[cityWheelView.getCurrentItem()]);
						}
						intent.putExtra("yourindustrycode", cityMap.get(cityArray[cityWheelView.getCurrentItem()]) + "");
					}
				}
				setResult(RESULT_OK, intent);
				finish();
			}
		});

		mainLayout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//"提示：点击窗口外部关闭窗口！",   
			}
		});
	}

	public void initWheelView(boolean isChina) {
		provinceWheelView = (WheelView) findViewById(R.id.provice);
		cityWheelView = (WheelView) findViewById(R.id.city);
		areaWheelView = (WheelView) findViewById(R.id.area);
		Intent intent = getIntent();
		flag = intent.getStringExtra("select_wheel");
		cityWheelView.setVisibility(View.GONE);

		if (null != flag && flag.equals("area")) {
			//初始化省滚轮列表选择器
			currentSelectTv.setText("选择地区");
			initProviceMap(isChina);
		} else if (null != flag && flag.equals("industry")) {
			currentSelectTv.setText("选择行业");
			initIndustryMap();
		}
		provinceAdapter = new ProviceCityAreaAdapter(WheelActivity.this, provinceArray, 0);
		provinceWheelView.setViewAdapter(provinceAdapter);
		provinceWheelView.setCurrentItem(0);
		provinceWheelView.addScrollingListener(privinceScrollListener);

		//初始化城市滚轮列表选择器
		String provinceName = provinceArray[0];
		int id = provinceMap.get(provinceName);
		//		if (provinceName.endsWith("市")) {
		if (null != flag && flag.equals("area")) {
			initCityMap(id, false);
		} else if (null != flag && flag.equals("industry")) {
			initChildIndustryMap(id, false);
		}
		cityAdapter = new ProviceCityAreaAdapter(WheelActivity.this, cityArray, 0);
		cityWheelView.setViewAdapter(cityAdapter);
		cityWheelView.setCurrentItem(0);
		cityWheelView.addScrollingListener(cityScrollListener);

		//初始化地区滚轮列表选择器
		String cityName = cityArray[0];
		int mId = cityMap.get(cityName);
		provinceName = cityArray[0];
		mId = mId * 100 + 1;

	}

	OnWheelScrollListener privinceScrollListener = new OnWheelScrollListener() {

		@Override
		public void onScrollingStarted(WheelView wheel) {
		}

		@Override
		public void onScrollingFinished(WheelView wheel) {
			int currentItem = wheel.getCurrentItem();
			String provinceName = provinceArray[currentItem];
			int dqxx01 = provinceMap.get(provinceName);

			if (null != flag && flag.equals("area")) {
				if (provinceName.endsWith("市")) {
					initCityMap(dqxx01, false);
				} else {
					initCityMap(dqxx01, true);
				}
				if (provinceName.contains("全球") || provinceName.contains("国内") || provinceName.contains("海外")
						|| provinceName.contains("北京") || provinceName.contains("上海") || provinceName.contains("天津")
						|| provinceName.contains("重庆")) {
					cityWheelView.setVisibility(View.GONE);
				} else {
					cityWheelView.setVisibility(View.VISIBLE);
				}
			} else if (null != flag && flag.equals("industry")) {
				if (provinceName.contains("全部")) {
					cityWheelView.setVisibility(View.GONE);
				} else {
					cityWheelView.setVisibility(View.VISIBLE);
				}
				initChildIndustryMap(dqxx01, true);
			}
			cityAdapter = new ProviceCityAreaAdapter(WheelActivity.this, cityArray, 0);
			cityWheelView.setViewAdapter(cityAdapter);
			cityWheelView.setCurrentItem(0);

			String cityName = cityArray[0];
			int dqx_dqxx01_2 = cityMap.get(cityName);
			if (provinceName.endsWith("市")) {
				dqx_dqxx01_2 = dqx_dqxx01_2 * 100 + 1;
			}
		}
	};

	OnWheelScrollListener cityScrollListener = new OnWheelScrollListener() {

		@Override
		public void onScrollingStarted(WheelView wheel) {
		}

		@Override
		public void onScrollingFinished(WheelView wheel) {
			String provinceName = provinceArray[provinceWheelView.getCurrentItem()];
			int dqx_dqxx01 = cityMap.get(cityArray[wheel.getCurrentItem()]);
			if (provinceName.endsWith("市")) {
				dqx_dqxx01 = dqx_dqxx01 * 100 + 1;
			}
		}
	};

	public void initProviceMap(boolean isChina) {
		try {
			WheelUtil.copyDB(WheelActivity.this, DBNAME);
			if (db == null) {
				db = SQLiteDatabase.openOrCreateDatabase(path + DBNAME, null);
			}
			provinceMap = WheelUtil.getProvince(db, TABLE_NAME, isChina);
			provinceArray = provinceMap.keySet().toArray(new String[provinceMap.size()]);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void initCityMap(int superId, boolean municipalities) {
		try {
			WheelUtil.copyDB(WheelActivity.this, DBNAME);
			if (db == null) {
				db = SQLiteDatabase.openOrCreateDatabase(path + DBNAME, null);
			}
			cityMap = WheelUtil.getCity(db, TABLE_NAME, superId, municipalities);
			cityArray = cityMap.keySet().toArray(new String[cityMap.size()]);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void initIndustryMap() {
		try {
			WheelUtil.copyDB(WheelActivity.this, IND_DBNAME);
			if (db == null) {
				db = SQLiteDatabase.openOrCreateDatabase(path + IND_DBNAME, null);
			}
			provinceMap = WheelUtil.getIndustry(db, IND_TABLE_NAME);
			provinceArray = provinceMap.keySet().toArray(new String[provinceMap.size()]);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void initChildIndustryMap(int superId, boolean municipalities) {
		try {
			WheelUtil.copyDB(WheelActivity.this, IND_DBNAME);
			if (db == null) {
				db = SQLiteDatabase.openOrCreateDatabase(path + IND_DBNAME, null);
			}
			cityMap = WheelUtil.getChildIndustry(db, IND_TABLE_NAME, superId, municipalities);
			cityArray = cityMap.keySet().toArray(new String[cityMap.size()]);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public class ProviceCityAreaAdapter extends ArrayWheelAdapter<String> {
		private int currentItem;
		private int currentValue;

		public ProviceCityAreaAdapter(Context context, String[] items, int current) {
			super(context, items);
			this.currentValue = current;
		}

		public void setCurrentValue(int value) {
			this.currentValue = value;
		}

		@Override
		protected void configureTextView(TextView view) {
			super.configureTextView(view);
			view.setTypeface(Typeface.SANS_SERIF);
		}

		@Override
		public View getItem(int index, View convertView, ViewGroup parent) {
			currentItem = index;
			return super.getItem(index, convertView, parent);
		}

	}

	@Override
	protected void onDestroy() {
		if (db != null) {
			db.close();
			db = null;
		}
		if (null != provinceMap) {
			provinceMap.clear();
		}
		if (null != cityMap) {
			cityMap.clear();
		}
		super.onDestroy();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}

}
