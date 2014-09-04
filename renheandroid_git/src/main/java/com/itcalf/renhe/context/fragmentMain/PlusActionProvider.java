package com.itcalf.renhe.context.fragmentMain;

import com.actionbarsherlock.view.ActionProvider;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.actionbarsherlock.view.SubMenu;
import com.itcalf.renhe.R;

import android.content.Context;
import android.view.View;
/**
 * actionbarsherlock ActionProvider��ע�⵼��
 * @author ������
 *
 */
public class PlusActionProvider extends ActionProvider
{
	private Context context;
	public PlusActionProvider(Context context)
	{
		super(context);
		this.context = context;
		// TODO Auto-generated constructor stub
	}

	@Override
	public View onCreateActionView()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onPrepareSubMenu(SubMenu subMenu)
	{
		// TODO Auto-generated method stub
		subMenu.clear();
		subMenu.add(context.getString(R.string.plus_group_chat))
		.setIcon(R.drawable.ofm_group_chat_icon)
		.setOnMenuItemClickListener(new OnMenuItemClickListener()
		{
			
			@Override
			public boolean onMenuItemClick(MenuItem item)
			{
				// TODO Auto-generated method stub
				return true;
			}
		});
		subMenu.add(context.getString(R.string.plus_add_friend))
		.setIcon(R.drawable.ofm_add_icon)
		.setOnMenuItemClickListener(new OnMenuItemClickListener()
		{
			
			@Override
			public boolean onMenuItemClick(MenuItem item)
			{
				// TODO Auto-generated method stub
				return false;
			}
		});
		subMenu.add(context.getString(R.string.plus_video_chat))
		.setIcon(R.drawable.ofm_video_icon)
		.setOnMenuItemClickListener(new OnMenuItemClickListener()
		{
			
			@Override
			public boolean onMenuItemClick(MenuItem item)
			{
				// TODO Auto-generated method stub
				return false;
			}
		});
		subMenu.add(context.getString(R.string.plus_scan))
		.setIcon(R.drawable.ofm_group_chat_icon)
		.setOnMenuItemClickListener(new OnMenuItemClickListener()
		{
			
			@Override
			public boolean onMenuItemClick(MenuItem item)
			{
				// TODO Auto-generated method stub
				return false;
			}
		});
		subMenu.add(context.getString(R.string.plus_take_photo))
		.setIcon(R.drawable.ofm_camera_icon)
		.setOnMenuItemClickListener(new OnMenuItemClickListener()
		{
			
			@Override
			public boolean onMenuItemClick(MenuItem item)
			{
				// TODO Auto-generated method stub
				return false;
			}
		});
		super.onPrepareSubMenu(subMenu);
	}
	
	@Override
	public boolean hasSubMenu()
	{
		// TODO Auto-generated method stub
		return true;
	}
	
}
