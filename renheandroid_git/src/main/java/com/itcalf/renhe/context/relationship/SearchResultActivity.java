  package com.itcalf.renhe.context.relationship;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.itcalf.renhe.R;
import com.itcalf.renhe.cache.CacheManager;
import com.itcalf.renhe.context.archives.MyHomeArchivesActivity;
import com.itcalf.renhe.context.innermsg.SwipeBackActivity;
import com.itcalf.renhe.context.relationship.SearchRelationshipTask.IDataBack;
import com.itcalf.renhe.context.room.AddMessageBoardActivity;
import com.itcalf.renhe.context.template.ActivityTemplate;
import com.itcalf.renhe.utils.NetworkUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
  /**
   * Title: SearchResultActivity.java<br>
   * Description: <br>
   * Copyright (c) 人和网版权所有 2014    <br>
   * Create DateTime: 2014-4-11 下午5:07:31 <br>
   * @author wangning
   */
public class SearchResultActivity extends SwipeBackActivity {

	private ListView mRelationshipList;
	private View mFooterView;
	private TextView keywordTv;
	private RelativeLayout noResultLayout;
	private Button advancedSearchButton;
	private List<Map<String, Object>> mData;
	private SimpleAdapter mSimpleAdapter;
	private int mStart = 0;
	private int mCount = 20;
	private String keyword = "";
	private int cityCode = -1;
	private int industryCode = -1;
	private String company = "";
	private String job = "";
	private RelativeLayout keywordLayout;
	private RelativeLayout nowifiLayout;
//	private EditText keywordEt;
	private String mkeyWord = "";
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		new ActivityTemplate().doInActivity(this, R.layout.relationship_search_result);
	}

	@Override
	protected void findView() {
		super.findView();
		mRelationshipList = (ListView) findViewById(R.id.relationship_list);
		mFooterView = LayoutInflater.from(this).inflate(R.layout.room_footerview, null);
		mFooterView.setVisibility(View.GONE);
//		advancedSearchButton = (Button)findViewById(R.id.advanced_search_btn);
		keywordTv = (TextView)findViewById(R.id.keyword_tv);
		noResultLayout = (RelativeLayout)findViewById(R.id.noresult);
		keywordLayout = (RelativeLayout)findViewById(R.id.layout);
		nowifiLayout = (RelativeLayout)findViewById(R.id.nowifi_rl);
//		keywordEt = (EditText)findViewById(R.id.keyword_edt);
	}
//	@Override
//	public boolean onPrepareOptionsMenu(Menu menu) {
//		MenuItem searchItem = menu.findItem(R.id.menu_search_result); 
//		SearchView searchView = (SearchView) searchItem.getActionView();
//		searchItem.setVisible(true);
//		searchView.setClickable(false);
//		searchView.setFocusable(false);
//		searchView.setIconified(false);
//		searchView.setEnabled(false);
//		searchView.setQueryHint(mkeyWord);
//		return super.onPrepareOptionsMenu(menu);
//	}
	@Override
	protected void initData() {
		super.initData();
		setTextValue(R.id.title_txt, "人脉搜索结果");
		String key = getIntent().getStringExtra("keyword");
		cityCode = getIntent().getIntExtra("citycode", -1);
		industryCode = getIntent().getIntExtra("industryCode", -1);
		String area = getIntent().getStringExtra("area");
		String industry = getIntent().getStringExtra("industry");
		String intent_company = getIntent().getStringExtra("company");
		String intent_job = getIntent().getStringExtra("job");
		StringBuffer keywordTvSb = new StringBuffer();;
		if(null != key && !key.equals("")){
			keyword = key;
//			keywordTv.setText("\""+keyword+"\"");
			keywordTvSb.append("\""+keyword+"\" ");
		}
		if(null != area && !area.equals("") && !area.equals(AdvancedSearchActivity.ALL_AREA_STRING)){
			keywordTvSb.append("\""+area+"\" ");
		}
		if(null != industry && !industry.equals("") && !industry.equals(AdvancedSearchActivity.ALL_INDUSTRY_STRING) && !industry.equals(AdvancedSearchActivity.ALL_INDUSTRY_STRING2)){
			keywordTvSb.append("\""+industry+"\" ");
		}
		if(null != intent_company && !intent_company.equals("")){
			company = intent_company;
			keywordTvSb.append("\""+company+"\" ");
		}
		if(null != intent_job && !intent_job.equals("")){
			job = intent_job;
			keywordTvSb.append("\""+job+"\" ");
		}
		keywordTv.setText(keywordTvSb);
		mkeyWord = keywordTvSb.toString();
		getWindow().invalidatePanelMenu(Window.FEATURE_OPTIONS_PANEL);
//		keywordEt.setText(keywordTvSb);
		
		if(TextUtils.isEmpty(keywordTv.getText().toString().trim())){
//			keywordLayout.setVisibility(View.GONE);
		}else{
//			keywordLayout.setVisibility(View.VISIBLE);
		}
		mData = new ArrayList<Map<String, Object>>();
		mSimpleAdapter = new ImageUpdateAdapter(this, mData, R.layout.relationship_search_list_item, mFrom, mTo);
		mRelationshipList.addFooterView(mFooterView, null, false);
		mRelationshipList.setAdapter(mSimpleAdapter);
		if (getSharedPreferences("setting_info", 0).getBoolean("fastscroll", false)) {
			mRelationshipList.setFastScrollEnabled(true);
		}
		initSearch(keyword,cityCode,industryCode,company,job,true);
	}
	private void toggleFooterView(boolean isShow) {
		if (isShow) {
			((TextView) mFooterView.findViewById(R.id.titleTv)).setText("加载中...");
			mFooterView.findViewById(R.id.waitPb).setVisibility(View.VISIBLE);
		} else {
			((TextView) mFooterView.findViewById(R.id.titleTv)).setText("查看更多");
			mFooterView.findViewById(R.id.waitPb).setVisibility(View.GONE);
		}
	}

	@Override
	protected void initListener() {
		super.initListener();
		mFooterView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mStart += mCount;
				initSearch(keyword,cityCode,industryCode,company,job, false);
			}
		});
		mRelationshipList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position < mData.size() && null != mData.get(position)) {
					Intent intent = new Intent(SearchResultActivity.this, MyHomeArchivesActivity.class);
					intent.putExtra(MyHomeArchivesActivity.FLAG_INTENT_DATA, (String) mData.get(position).get("sid"));
					startActivity(intent);
				}
			}
		});

//		advancedSearchButton.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				Intent intent = new Intent(SearchResultActivity.this, AdvancedSearchActivity.class);
//				startActivityForResult(intent,2);
//			}
//		});
//		keywordEt.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				finish();
//				overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
//			}
//		});
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 1:
			ProgressDialog findPd = new ProgressDialog(this);
//			findPd.setTitle("搜索联系人");
			findPd.setMessage("请稍候...");
//			findPd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			findPd.setCanceledOnTouchOutside(false);
			return findPd;
		default:
			return null;
		}
	}

	private void initSearch(String keyword,int cityCode, int industryCode, String company, String job, final boolean hideFooter) {
		new SearchRelationshipTask(this, new IDataBack() {
			@Override
			public void onPre() {
				
				if (hideFooter) {
					showDialog(1);
					mFooterView.setVisibility(View.GONE);
				} else {
					toggleFooterView(true);
				}
			}

			@Override
			public void onPost(List<Map<String, Object>> result) {
				if (hideFooter) {
					removeDialog(1);
				} else {
					toggleFooterView(false);
				}
				if (null != result) {
					nowifiLayout.setVisibility(View.GONE);
					mRelationshipList.setVisibility(View.VISIBLE);
					noResultLayout.setVisibility(View.GONE);
					if (result.size() == mCount) {
						mFooterView.setVisibility(View.VISIBLE);
					} else {
						mFooterView.setVisibility(View.GONE);
					}
					mData.addAll(result);
					mSimpleAdapter.notifyDataSetChanged();
				} else {
					nowifiLayout.setVisibility(View.GONE);
					mRelationshipList.setVisibility(View.GONE);
					noResultLayout.setVisibility(View.VISIBLE);
//					ToastUtil.showNetworkError(SearchResultActivity.this);
					mStart = (mStart -= mCount) == 0 ? 0 : mStart;
				}
				if(NetworkUtil.hasNetworkConnection(SearchResultActivity.this) == -1){
					nowifiLayout.setVisibility(View.VISIBLE);
					mRelationshipList.setVisibility(View.GONE);
					noResultLayout.setVisibility(View.GONE);
				}
			}

		}).execute(keyword,cityCode,industryCode,company,job,mStart, mCount);
	}

	private String[] mFrom = new String[] { "headImage", "nameTv", "titleTv", "infoTv", "rightImage" };
	private int[] mTo = new int[] { R.id.headImage, R.id.nameTv, R.id.titleTv, R.id.infoTv, R.id.rightImage };

	class ImageUpdateAdapter extends SimpleAdapter {

		public ImageUpdateAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
			super(context, data, resource, from, to);
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			convertView = super.getView(position, convertView, parent);
			if (convertView != null && position < mData.size()) {
				String picPath = (String) mData.get(position).get("avatar_path");
				if (null != picPath && !"".equals(picPath)) {
					
					ImageView imageView = (ImageView) convertView.findViewById(mTo[0]);
//					imageView.setTag(picPath + position);
					if (null != imageView) {
//						SimpleAsyncImageLoad.loadDrawable(null,picPath, getRenheApplication().getUserInfo().getEmail(), 70, 70,
//								SearchResultActivity.this, new SimpleAsyncImageLoad.ImageCallback() {
//
//									@Override
//									public void imageLoaded(Drawable imageDrawable, String imageUrl) {
//										ImageView imageViewByTag = (ImageView) mRelationshipList.findViewWithTag(imageUrl
//												+ position);
//										if (imageViewByTag != null && !imageUrl.equals("")) {
//											imageViewByTag.setImageDrawable(imageDrawable);
//										}
//									}
//								});
						//方法2
//						imageView.setTag(picPath);
//						CacheManager.IMAGE_CACHE.initData(SearchResultActivity.this, "renhe_imagecache");
//						CacheManager.IMAGE_CACHE.setContext(SearchResultActivity.this);
//						CacheManager.IMAGE_CACHE.setCacheFolder(CacheManager.DEFAULT_IMAGECACHE_FOLDER);
//						if (!CacheManager.IMAGE_CACHE.get(picPath, imageView)) {
//							((ImageView)imageView).setImageDrawable(SearchResultActivity.this.getResources().getDrawable(R.drawable.avatar));
//						}
						//方法3
						ImageLoader imageLoader = ImageLoader.getInstance();		
						try {
							imageLoader.displayImage(picPath, (ImageView)imageView, CacheManager.options,  CacheManager.animateFirstDisplayListener);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}else{
					ImageView imageView = (ImageView) convertView.findViewById(mTo[0]);
					imageView.setImageDrawable(getResources().getDrawable(R.drawable.avatar));
				}
				ImageView rightIv = (ImageView) convertView.findViewById(R.id.rightImage);
				if(mData.get(position).get(mFrom[4]) != null){
					rightIv.setVisibility(View.VISIBLE);
				}else{
					rightIv.setVisibility(View.GONE);
				}
				ImageView vipIv = (ImageView) convertView.findViewById(R.id.vipImage);
				ImageView realNameIv = (ImageView) convertView.findViewById(R.id.realnameImage);
				
				Object accountObject  = mData.get(position).get("accountType");
				Object realNameObject = mData.get(position).get("isRealName");
				int accountType = 0;
				boolean isRealName = false;
				if(null != accountObject){
					accountType = (Integer)mData.get(position).get("accountType");;//账号vip等级类型：0：普通会员；1：VIP会员；2：黄金会员；3：铂金会员
				}
				if(null != realNameObject){
					isRealName= (Boolean)mData.get(position).get("isRealName");//是否是实名认证的会员
				}
				switch (accountType) {
				case 0:
					vipIv.setVisibility(View.GONE);
					break;
				case 1:
					vipIv.setVisibility(View.VISIBLE);
					vipIv.setImageResource(R.drawable.vip_1);
					break;
				case 2:
					vipIv.setVisibility(View.VISIBLE);
					vipIv.setImageResource(R.drawable.vip_2);
					break;
				case 3:
					vipIv.setVisibility(View.VISIBLE);
					vipIv.setImageResource(R.drawable.vip_3);
					break;

				default:
					break;
				}
				if(isRealName && accountType <= 0){
					realNameIv.setVisibility(View.VISIBLE);
					realNameIv.setImageResource(R.drawable.realname);
				}else{
					realNameIv.setVisibility(View.GONE);
				}
			}
			return convertView;
		}
	}
	
	@Override
	public void finish() {
		super.finish();
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 2 && resultCode == RESULT_OK){
			
		}
	}
}

