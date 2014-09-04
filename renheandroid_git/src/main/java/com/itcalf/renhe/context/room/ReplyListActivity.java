package com.itcalf.renhe.context.room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.itcalf.renhe.R;
import com.itcalf.renhe.context.archives.MyHomeArchivesActivity;
import com.itcalf.renhe.context.room.ReplyListTask.IDataBack;
import com.itcalf.renhe.context.template.ActivityTemplate;
import com.itcalf.renhe.context.template.BaseActivity;
import com.itcalf.renhe.utils.ToastUtil;

/**
 * Feature: 留言回复列表界面 Desc:留言回复列表界面
 * 
 * @author xp
 * 
 */
public class ReplyListActivity extends BaseActivity {

	// 列表视图
	private ListView mListView;
	// 列表头部
	private View mHeaderView;
	// 列表底部
	private View mFooterView;
	// 列表数据
	private List<Map<String, Object>> mData;
	private SimpleAdapter mSimpleAdapter;
	private String[] mFrom = new String[] { "titleTv", "infoTv", "timeTv" };
	private int[] mTo = new int[] { R.id.titleTv, R.id.infoTv, R.id.timeTv };
	private int mStart;
	private int mCount = 20;
	private String mMessageBoardObjectId;
	private String mMessageBoardId;
	private static final boolean TEST = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new ActivityTemplate().doInActivity(this, R.layout.rooms_reply_list);
	}

	@Override
	protected void findView() {
		super.findView();
		mListView = (ListView) findViewById(R.id.listView);
		mFooterView = LayoutInflater.from(this).inflate(R.layout.room_footerview, null);
		mHeaderView = LayoutInflater.from(this).inflate(R.layout.room_headerview, null);
	}

	@Override
	protected void initData() {
		super.initData();
//		mTitleTxt.setText("回复");
		mData = new ArrayList<Map<String, Object>>();
		mSimpleAdapter = new SimpleAdapter(ReplyListActivity.this, mData, R.layout.rooms_reply_item, mFrom, mTo);
		mListView.addFooterView(mFooterView, null, false);
		mListView.addHeaderView(mHeaderView, null, false);
		mListView.setAdapter(mSimpleAdapter);
		if (getSharedPreferences("setting_info", 0).getBoolean("fastdrag", false)) {
			mListView.setFastScrollEnabled(true);
		}
		if (TEST) {
			testData();
		} else {
			mMessageBoardObjectId = getIntent().getExtras().getString("objectId");
			mMessageBoardId = getIntent().getExtras().getString("id");
			initGet(mMessageBoardObjectId, true);
		}
	}

	private void initGet(String objectId, final boolean hideFooter) {
		// 初始化异步加载留言回复列表数据
		new ReplyListTask(this, new IDataBack() {

			@Override
			public void onPre() {
				if (hideFooter) {
					mFooterView.setVisibility(View.GONE);
					toggleHeaderView(true);
				} else {
					toggleFooterView(true);
				}
			}

			@Override
			public void onPost(List<Map<String, Object>> result) {
				if (hideFooter) {
					mData.clear();
					toggleHeaderView(false);
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
					ToastUtil.showNetworkError(ReplyListActivity.this);
					mStart = (mStart -= mCount) == 0 ? 0 : mStart;
				}
			}
		}).execute(objectId, getRenheApplication().getUserInfo().getAdSId(), getRenheApplication().getUserInfo().getSid(),
				mStart, mCount);
	}

	private void testData() {
		for (int i = 0; i < 10; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(mFrom[0], "侯绍琦");
			map.put(mFrom[1], "做的还不错，多很希望，希望继续加油，添加更多的功能，支持新浪微博~~");
			map.put(mFrom[2], "15：23");
			mData.add(map);
		}
	}

	private void toggleHeaderView(boolean isShow) {
		if (isShow) {
			mHeaderView.findViewById(R.id.waitPb).setVisibility(View.VISIBLE);
			((TextView) mHeaderView.findViewById(R.id.titleTv)).setText("数据加载中...");
		} else {
			mHeaderView.findViewById(R.id.waitPb).setVisibility(View.GONE);
			((TextView) mHeaderView.findViewById(R.id.titleTv)).setText("点击刷新");
		}
	}

	private void toggleFooterView(boolean isShow) {
		if (isShow) {
			((TextView) mFooterView.findViewById(R.id.titleTv)).setText("加载更多数据中...");
			mFooterView.findViewById(R.id.waitPb).setVisibility(View.VISIBLE);
		} else {
			((TextView) mFooterView.findViewById(R.id.titleTv)).setText("查看更多数据");
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
				initGet(mMessageBoardObjectId, false);
			}
		});


		mHeaderView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mStart = 0;
				mCount = 20;
				initGet(mMessageBoardObjectId, true);
			}
		});

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (null != mData && mData.size() > position) {
					Intent intent = new Intent(ReplyListActivity.this, MyHomeArchivesActivity.class);
					Map<String, Object> map = mData.get(position);
					if (null != map) {
						String sid = (String) map.get("sid");
						intent.putExtra(MyHomeArchivesActivity.FLAG_INTENT_DATA, sid);
						startActivity(intent);
					}
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 2:
			if (resultCode == RESULT_OK) {
				initGet(mMessageBoardObjectId, true);
			}
			break;
		}
	}
}
