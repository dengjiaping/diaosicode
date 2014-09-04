package com.itcalf.renhe.context.archives;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.itcalf.renhe.R;
import com.itcalf.renhe.context.template.ActivityTemplate;
import com.itcalf.renhe.context.template.BaseActivity;

public class NotFriendArchivesActivity extends BaseActivity {
//	private LinearLayout mLayoutAttention;//关注
//	private LinearLayout mLayoutRoom;//客厅
//	private LinearLayout mLayoutVermicelli ;//粉丝
//	private LinearLayout mLayoutContact ;//联系人
//	
	private Button mEditBt;
	private Button mAttentionBt;//关注
	private Button mRoomBt;//客厅
	private Button mVermicelliBt;//粉丝
	private Button mContactBt;//联系人
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new ActivityTemplate().doInActivity(this, R.layout.archives_not_friend);
	}
	@Override
	protected void findView() {
		super.findView();
		mAttentionBt=(Button)findViewById(R.id.attentionBt);
		mRoomBt=(Button)findViewById(R.id.roomBt);
		mVermicelliBt=(Button)findViewById(R.id.vermicelliBt);
		mContactBt=(Button)findViewById(R.id.contactBt);
		mEditBt=(Button)findViewById(R.id.editBt);
	}
	@Override
	protected void initData() {
		super.initData();
	}
	@Override
	protected void initListener() {
		super.initListener();
		mEditBt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
	}
	
}
