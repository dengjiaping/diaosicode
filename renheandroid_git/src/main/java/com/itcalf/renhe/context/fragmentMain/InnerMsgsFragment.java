package com.itcalf.renhe.context.fragmentMain;

import java.lang.reflect.Field;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.itcalf.renhe.R;
import com.itcalf.renhe.context.innermsg.SendInnerMsgActivity;
import com.itcalf.renhe.context.relationship.AdvancedSearchActivity;
import com.itcalf.renhe.context.room.AddMessageBoardActivity;
import com.umeng.analytics.MobclickAgent;
import com.viewpagerindicator.TabPageIndicator;

@SuppressLint("ValidFragment")
public class InnerMsgsFragment extends SherlockFragment
{
//	private View rootView;
	private TabPageIndicator indicator ;
	private static final String[] TITLE ={ "收件箱", "发件箱"};
	public InnerMsgsFragment()
	{
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
//		if(null == rootView)
//		{
//			rootView = inflater.inflate(R.layout.innermsgs_fragment, null);
//		}
		getActivity().setTitle("站内信");
		if (getActivity() instanceof MainFragment) {
			MainFragment fca = (MainFragment) getActivity();
			fca.switchTitle("站内信");
		} 
		View rootView = inflater.inflate(R.layout.innermsgs_fragment, container, false);
		FragmentPagerAdapter adapter = new InnerMsgTabPageIndicatorApater(getChildFragmentManager(),TITLE);
		ViewPager pager = (ViewPager)rootView.findViewById(R.id.pager);
		pager.setAdapter(adapter);
		indicator= (TabPageIndicator)rootView.findViewById(R.id.indicator);
		indicator.setViewPager(pager);
		return rootView;
		
	}
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("InnermsgScreen"); //统计页面
	}
	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("InnermsgScreen"); 
	}
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		MenuItem editItem = menu.findItem(R.id.item_edit);  
		editItem.setVisible(true);
		editItem.setTitle("写站内信");
		super.onPrepareOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.item_edit:
			startActivity(new Intent(getActivity(), SendInnerMsgActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public void onDetach() {
		super.onDetach();
		try {
			Field childFragmentManager = Fragment.class
					.getDeclaredField("mChildFragmentManager");
			childFragmentManager.setAccessible(true);
			childFragmentManager.set(this, null);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
