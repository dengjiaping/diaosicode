package com.itcalf.renhe.context.more;

import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.widget.TextView;

import com.itcalf.renhe.R;
import com.itcalf.renhe.context.innermsg.SwipeBackActivity;
import com.itcalf.renhe.context.template.ActivityTemplate;
import com.itcalf.renhe.context.template.BaseActivity;

/**
 * Feature:关于人和网界面 Desc:关于人和网界面
 * 
 * @author xp
 * 
 */
public class AboutRenheActivity extends SwipeBackActivity {
	private TextView mVersionText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new ActivityTemplate().doInActivity(this, R.layout.more_aboutrenhe);
	}

	@Override
	protected void findView() {
		super.findView();
		mVersionText = (TextView) findViewById(R.id.versionText);
	}

	@Override
	protected void initData() {
		super.initData();
		setTextValue(R.id.title_txt, "关于");
		String ver = "";
		try {
			ver = AboutRenheActivity.this.getPackageManager().getPackageInfo(AboutRenheActivity.this.getPackageName(), 0).versionName;
			mVersionText.setText("人和网Android " + ver + "版");
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	protected void initListener() {
		super.initListener();
	}
}
