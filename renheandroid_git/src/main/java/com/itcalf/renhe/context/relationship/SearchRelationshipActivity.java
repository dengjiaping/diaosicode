package com.itcalf.renhe.context.relationship;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.itcalf.renhe.R;
import com.itcalf.renhe.RenheApplication;
import com.itcalf.renhe.cache.CacheManager;
import com.itcalf.renhe.context.archives.MyHomeArchivesActivity;
import com.itcalf.renhe.context.relationship.SearchRelationshipTask.IDataBack;
import com.itcalf.renhe.context.template.ActivityTemplate;
import com.itcalf.renhe.context.template.BaseActivity;
import com.itcalf.renhe.utils.AsyncImageLoader;
import com.itcalf.renhe.utils.ToastUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

public class SearchRelationshipActivity extends BaseActivity {
	//登出标识（两次按下返回退出时的标识字段）
	private boolean logoutFlag;
//	private SlidingMenu menu;
	private EditText mKeywordEt;
	private Button mSearchBt;
	private ListView mRelationshipList;
	private View mFooterView;
	private Button advancedSearchButton;
	private List<Map<String, Object>> mData;
	private SimpleAdapter mSimpleAdapter;
	private int mStart = 0;
	private int mCount = 20;

	private Drawable imgCloseButton;
	private boolean isFromMenu;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		RenheApplication.getInstance().addActivity(this);
		new ActivityTemplate().doInActivity(this, R.layout.relationship_search);
		
	}

	@Override
	protected void findView() {
		super.findView();
		isFromMenu = getIntent().getBooleanExtra("isFromMenu", true);
		if(isFromMenu){
//			menu = mySlidingMenu.initSlidingMenu();
		}
		mKeywordEt = (EditText) findViewById(R.id.keyword_edt);
		mSearchBt = (Button) findViewById(R.id.searchBt);
		mRelationshipList = (ListView) findViewById(R.id.relationship_list);
		mFooterView = LayoutInflater.from(this).inflate(R.layout.room_footerview, null);
		mFooterView.setVisibility(View.GONE);
		imgCloseButton = getResources().getDrawable(R.drawable.relationship_input_del);
//		advancedSearchButton = (Button)findViewById(R.id.advanced_search_btn);
	}

	@Override
	protected void initData() {
		super.initData();
		setTextValue(R.id.title_txt, "人脉搜索");
		mData = new ArrayList<Map<String, Object>>();
		mSimpleAdapter = new ImageUpdateAdapter(this, mData, R.layout.relationship_search_list_item, mFrom, mTo);
		mRelationshipList.addFooterView(mFooterView, null, false);
		mRelationshipList.setAdapter(mSimpleAdapter);
		if (getSharedPreferences("setting_info", 0).getBoolean("fastdrag", false)) {
			mRelationshipList.setFastScrollEnabled(true);
		}
		// Set bounds of the Clear button so it will look ok
		if(imgCloseButton != null){
			imgCloseButton.setBounds(0, 0, imgCloseButton.getIntrinsicWidth(), imgCloseButton.getIntrinsicHeight());
		}
		
		// initSearch("", true);
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
		mKeywordEt.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String keyword = s.toString();
				if (!TextUtils.isEmpty(keyword)) {

				} else {

				}
			}
		});
		mSearchBt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String keyword = mKeywordEt.getText().toString().trim();
				mData.clear();
				mStart = 0;
				mCount = 20;
//				initSearch(keyword, true);
				Intent intent = new Intent(SearchRelationshipActivity.this,SearchResultActivity.class);
				intent.putExtra("keyword", keyword);
				startActivity(intent);
			}

		});
		mFooterView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String keyword = mKeywordEt.getText().toString().trim();
				mStart += mCount;
				initSearch(keyword, false);
			}
		});
		mRelationshipList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position < mData.size() && null != mData.get(position)) {
					Intent intent = new Intent(SearchRelationshipActivity.this, MyHomeArchivesActivity.class);
					intent.putExtra(MyHomeArchivesActivity.FLAG_INTENT_DATA, (String) mData.get(position).get("sid"));
					startActivity(intent);
				}
			}
		});

		mKeywordEt.addTextChangedListener(tbxEdit_TextChanged);
		mKeywordEt.setOnTouchListener(txtEdit_OnTouch);
		mKeywordEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {  
			@Override
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
				if(arg1 == EditorInfo.IME_ACTION_SEARCH){
					mSearchBt.performClick();
				}
				return true;
			}  
		});
		advancedSearchButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(SearchRelationshipActivity.this, AdvancedSearchActivity.class);
				startActivity(intent);
			}
		});
	}

	/** 搜索框输入状态监听 **/
	private TextWatcher tbxEdit_TextChanged = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			if (!TextUtils.isEmpty(s.toString())) {
				mKeywordEt.setCompoundDrawablesWithIntrinsicBounds(null, null,
						getResources().getDrawable(R.drawable.clearbtn_selected), null);
				AnimationUtils.loadAnimation(SearchRelationshipActivity.this, R.anim.push_right_in);
				mSearchBt.setVisibility(View.VISIBLE);
			} else {
				mSearchBt.setVisibility(View.GONE);
				mKeywordEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
			}
		}

	};

	/** 搜索框点击事件监听 **/
	private OnTouchListener txtEdit_OnTouch = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			/** 手指离开的事件 */
			case MotionEvent.ACTION_UP:
				/** 手指抬起时候的坐标 **/
				int curX = (int) event.getX();
				if (curX > v.getWidth() - v.getPaddingRight() - imgCloseButton.getIntrinsicWidth() && !TextUtils.isEmpty(mKeywordEt.getText().toString())) {
					mKeywordEt.setText("");
					mSearchBt.setVisibility(View.GONE);
					((RelativeLayout.LayoutParams) mKeywordEt.getLayoutParams()).rightMargin = 0;
					int cacheInputType = mKeywordEt.getInputType();
					// setInputType 可以更改 TextView 的输入方式
					mKeywordEt.setInputType(InputType.TYPE_NULL);// EditText始终不弹出软件键盘
					mKeywordEt.onTouchEvent(event);
					mKeywordEt.setInputType(cacheInputType);
					return true;
				}
				break;
			}
			return false;
		}
	};

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

	private void initSearch(String keyword, final boolean hideFooter) {
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
					if (result.size() == mCount) {
						mFooterView.setVisibility(View.VISIBLE);
					} else {
						mFooterView.setVisibility(View.GONE);
					}
					mData.addAll(result);
					mSimpleAdapter.notifyDataSetChanged();
				} else {
					ToastUtil.showNetworkError(SearchRelationshipActivity.this);
					mStart = (mStart -= mCount) == 0 ? 0 : mStart;
				}
			}

		}).execute(keyword, mStart, mCount);
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
				if (null != picPath) {
					ImageView imageView = (ImageView) convertView.findViewById(mTo[0]);
					imageView.setTag(picPath + position);
					if (null != imageView) {
						
//						SimpleAsyncImageLoad.loadDrawable(null,picPath, getRenheApplication().getUserInfo().getEmail(), 70, 70,
//								SearchRelationshipActivity.this, new SimpleAsyncImageLoad.ImageCallback() {
//
//									@Override
//									public void imageLoaded(Drawable imageDrawable, String imageUrl) {
//										ImageView imageViewByTag = (ImageView) mRelationshipList.findViewWithTag(imageUrl
//												+ position);
//										if (imageViewByTag != null) {
//											imageViewByTag.setImageDrawable(imageDrawable);
//										}
//									}
//								});
						//方法2
//						imageView.setTag(picPath);
//						CacheManager.IMAGE_CACHE.initData(SearchRelationshipActivity.this, "renhe_imagecache");
//						CacheManager.IMAGE_CACHE.setContext(SearchRelationshipActivity.this);
//						CacheManager.IMAGE_CACHE.setCacheFolder(CacheManager.DEFAULT_IMAGECACHE_FOLDER);
//						if (!CacheManager.IMAGE_CACHE.get(picPath, imageView)) {
//							((ImageView)imageView).setImageDrawable(SearchRelationshipActivity.this.getResources().getDrawable(R.drawable.avatar));
//						}
						//方法3
						ImageLoader imageLoader = ImageLoader.getInstance();		
						try {
							imageLoader.displayImage(picPath, (ImageView)imageView, CacheManager.options,  CacheManager.animateFirstDisplayListener);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
			return convertView;
		}
	}
	
	@Override
	public void finish() {
		super.finish();
	}
	Handler handler = new Handler();
	Runnable run = new Runnable() {
		@Override
		public void run() {
			logoutFlag = false;
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(isFromMenu){
				if(logoutFlag) {
					AsyncImageLoader.getInstance().clearCache();
//					finish();
					if(getSharedPreferences("setting_info", 0)
							.getBoolean("clearcache", false)) {
						CacheManager.getInstance().populateData(this)
							.clearCache(getRenheApplication().getUserInfo().getEmail());
					}
					//关闭通知栏消息
					((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(10001);
					RenheApplication.getInstance().exit();
				}else {
					ToastUtil.showToast(this, "请再点击一次退出程序!");
					logoutFlag = true;
					handler.postDelayed(run, 2000);
				}
			}else{
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event); // 最后，一定要做完以后返回
		// true，或者在弹出菜单后返回true，其他键返回super，让其他键默认
	}
}
