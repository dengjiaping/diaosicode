  package com.itcalf.renhe.context.archives.edit;


import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.itcalf.renhe.R;
import com.itcalf.renhe.context.template.BaseActivity;
  /**
   * Title: EditBaseActivity.java<br>
   * Description: <br>
   * Copyright (c) 人和网版权所有 2014    <br>
   * Create DateTime: 2014-8-28 下午4:37:36 <br>
   * @author wangning
   */
public class EditBaseActivity extends BaseActivity {
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem saveItem = menu.findItem(R.id.item_save);
		saveItem.setVisible(true);
		return super.onPrepareOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case android.R.id.home:
			goBack();
			return true;
		case R.id.item_save:
			goSave();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	public void goBack(){};
	public void goSave(){};
	@Override
	public void onBackPressed() {
		goBack();
	}
}

