package com.itcalf.renhe.context.fragmentMain;

import java.lang.reflect.Field;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.itcalf.renhe.R;
import com.itcalf.renhe.context.relationship.AdvancedSearchActivity;
import com.itcalf.renhe.context.room.AddMessageBoardActivity;
import com.umeng.analytics.MobclickAgent;
import com.viewpagerindicator.TabPageIndicator;

@SuppressLint("ValidFragment")
public class RoomsFragment extends SherlockFragment {
	//	private View rootView;
	private TabPageIndicator indicator;
	private static final String[] TITLE = { "我的客厅", "朋友", "同行", "同城", "最受关注" };

	public RoomsFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getActivity().setTitle("我的客厅");
		if (getActivity() instanceof MainFragment) {
			MainFragment fca = (MainFragment) getActivity();
			fca.switchTitle("我的客厅");
		} 
//		LayoutToDrawable layoutToDrawable = new LayoutToDrawable(getActivity());
//		Drawable mDrawable = layoutToDrawable.layoutToDrawable(5);
////		getActivity().getSupportActionBar().setLogo(mDrawable);
//		getSherlockActivity().getActionBar().setLogo(mDrawable);
		
		View rootView = inflater.inflate(R.layout.roomsfragment, container, false);
		ViewGroup parent = (ViewGroup) rootView.getParent();
		if (parent != null) {
			parent.removeView(rootView);
		}
		FragmentPagerAdapter adapter = new TabPageIndicatorApater(getChildFragmentManager(), TITLE);
		ViewPager pager = (ViewPager) rootView.findViewById(R.id.pager);
		pager.setAdapter(adapter);
		indicator = (TabPageIndicator) rootView.findViewById(R.id.indicator);
		indicator.setViewPager(pager);
		return rootView;

	}
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("RoomsScreen"); //统计页面
	}
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("RoomsScreen"); 
	}
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		MenuItem searchItem = menu.findItem(R.id.menu_search);
		searchItem.setVisible(true);
		MenuItem editItem = menu.findItem(R.id.item_edit);
		editItem.setVisible(true);
		editItem.setTitle("发布新留言");
		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_search:
			startActivity(new Intent(getActivity(), AdvancedSearchActivity.class));
			return true;
		case R.id.item_edit:
			startActivity(new Intent(getActivity(), AddMessageBoardActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		try {
			Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
			childFragmentManager.setAccessible(true);
			childFragmentManager.set(this, null);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	 public static boolean onKeyDown(int keyCode, KeyEvent event) {
	        if (keyCode == event.KEYCODE_BACK) {
	        }
	        return true;
	    }
}
