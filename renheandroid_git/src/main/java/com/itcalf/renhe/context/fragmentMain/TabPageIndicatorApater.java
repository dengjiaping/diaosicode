package com.itcalf.renhe.context.fragmentMain;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabPageIndicatorApater extends FragmentPagerAdapter
{
	private String[] TITLE;
	public TabPageIndicatorApater(FragmentManager fm,String[] TITLE)
	{
		super(fm);
		this.TITLE = TITLE;
	}

	@Override
	public Fragment getItem(int position)
	{
		Fragment fragment = new MessageBoardFragment();
		Bundle b = new Bundle();
		b.putInt("value", position + 1);
		fragment.setArguments(b);
		return fragment;
	}

	@Override
	public CharSequence getPageTitle(int position)
	{
		// TODO Auto-generated method stub
		return TITLE[position%TITLE.length];
	}
	
	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		return TITLE.length;
	}

}
