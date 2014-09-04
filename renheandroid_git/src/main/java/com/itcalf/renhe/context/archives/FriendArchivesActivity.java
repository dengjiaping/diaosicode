package com.itcalf.renhe.context.archives;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

import com.itcalf.renhe.R;
import com.itcalf.renhe.context.template.ActivityTemplate;
import com.itcalf.renhe.context.template.BaseActivity;
import com.itcalf.renhe.utils.EmailUtil;

public class FriendArchivesActivity extends BaseActivity {
	// private LinearLayout mLayoutAttention;//关注
	// private LinearLayout mLayoutRoom;//客厅
	// private LinearLayout mLayoutVermicelli ;//粉丝
	// private LinearLayout mLayoutContact ;//联系人
	//	
	private Button mEditBt;
	private Button mAttentionBt;// 关注
	private Button mRoomBt;// 客厅
	private Button mVermicelliBt;// 粉丝
	private Button mContactBt;// 联系人

	private ImageButton mMailtoBt;
	private ImageButton mLettertoBt;
	private ImageButton mCallBt;
	private ImageButton mSharetoBt;
	private static final boolean TEST = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new ActivityTemplate().doInActivity(this, R.layout.archives_friend);
	}

	@Override
	protected void findView() {
		super.findView();
		mAttentionBt = (Button) findViewById(R.id.attentionBt);
		mRoomBt = (Button) findViewById(R.id.roomBt);
		mVermicelliBt = (Button) findViewById(R.id.vermicelliBt);
		mContactBt = (Button) findViewById(R.id.contactBt);
		mEditBt = (Button) findViewById(R.id.editBt);

		mMailtoBt = (ImageButton) findViewById(R.id.mailtoBt);
		mLettertoBt = (ImageButton) findViewById(R.id.lettertoBt);
		mCallBt = (ImageButton) findViewById(R.id.callBt);
		mSharetoBt = (ImageButton) findViewById(R.id.sharetoBt);

	}

	@Override
	protected void initData() {
		super.initData();

	}

	@Override
	protected void initListener() {
		super.initListener();

		mMailtoBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Uri uri = Uri.parse("mailto:"+"w.t.start@gmail.com");
				Intent email = new Intent(Intent.ACTION_SENDTO, uri);
				startActivity(email);
			}
		});
		mCallBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent myIntentDial = new Intent(Intent.ACTION_DIAL, Uri
						.parse("tel:" + "13675895154"));
				startActivity(myIntentDial);
			}
		});
		mEditBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
	}
}
