package com.itcalf.renhe.context.innermsg;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Window;

import com.itcalf.renhe.R;
import com.itcalf.renhe.context.template.BaseActivity;
import com.itcalf.renhe.view.SwipeBackLayout;

/**
 * 侧滑finish
 * @author wangning
 *
 */
public class SwipeBackActivity extends BaseActivity {
	protected com.itcalf.renhe.view.SwipeBackLayout layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		layout = (SwipeBackLayout) LayoutInflater.from(this).inflate(
				R.layout.base, null);
		layout.attachToActivity(this);
	}
	
	
	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
//		overridePendingTransition(R.anim.base_slide_right_in, R.anim.base_slide_remain);
		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}




	// Press the back button in mobile phone
	@Override
	public void onBackPressed() {
		super.onBackPressed();
//		overridePendingTransition(0, R.anim.base_slide_right_out);
		overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
	}


}
