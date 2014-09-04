package com.itcalf.renhe.context.fragmentMain;

import com.itcalf.renhe.context.innermsg.InnerMsgListActivity;
import com.itcalf.renhe.context.room.MessageBoardActivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class InnerMsgTabPageIndicatorApater extends FragmentPagerAdapter
{
	private String[] TITLE;
	public InnerMsgTabPageIndicatorApater(FragmentManager fm,String[] TITLE)
	{
		super(fm);
		this.TITLE = TITLE;
	}

	@Override
	public Fragment getItem(int position)
	{
		Fragment fragment = new InnerMsgListActivity();
		Bundle b = new Bundle();
		b.putInt("value", position + 1);
		fragment.setArguments(b);
		return fragment;
	}

	@Override
	public CharSequence getPageTitle(int position)
	{
		return TITLE[position%TITLE.length];
	}
	
	@Override
	public int getCount()
	{
		return TITLE.length;
	}

}
