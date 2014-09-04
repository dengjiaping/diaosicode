  package com.itcalf.renhe.context.room;

import com.itcalf.renhe.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
  /**
   * Title: GuideActivity.java<br>
   * Description: <br>
   * Copyright (c) 人和网版权所有 2014    <br>
   * Create DateTime: 2014-5-23 下午6:36:53 <br>
   * @author wangning
   */
public class GuideActivity extends Activity {
	private ImageView guideIV;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guide);
		guideIV = (ImageView)findViewById(R.id.guide_iv);
		guideIV.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
}

