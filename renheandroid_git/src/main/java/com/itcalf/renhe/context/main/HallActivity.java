package com.itcalf.renhe.context.main;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.MenuItem;

import com.itcalf.renhe.R;

/**
 * Feature:人和网九宫格首页
 * Desc:人和网Android客户端主界面
 * @author xp
 *
 */
public class HallActivity extends Activity {

	private Fragment mContent;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rooms_tab);
		setTitle("人和网");
						
//		initSlidingMenu(savedInstanceState);
		
//		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	/**
	 * 初始化滑动菜单
	 */
//	private void initSlidingMenu(Bundle savedInstanceState){
//		SlidingMenu menu = new SlidingMenu(this);//直接new，而不是getSlidingMenu  
//		menu.setMode(SlidingMenu.LEFT);  
//		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);  
//		menu.setShadowDrawable(R.drawable.shadow);  
//		menu.setShadowWidthRes(R.dimen.shadow_width);  
//		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);  
//		menu.setBehindWidth(400);//设置SlidingMenu菜单的宽度  
//		menu.setFadeDegree(0.35f);  
//		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);//必须调用  
//		menu.setMenu(R.layout.menu_frame);//就是普通的layout布局  
//		
//		CanvasTransformer mTransformer = new CanvasTransformer() {
//
//			@Override
//			public void transformCanvas(Canvas canvas, float percentOpen) {
//				float scale = (float) (percentOpen*0.25 + 0.75);  
//				canvas.scale(scale, scale, canvas.getWidth()/2, canvas.getHeight()/2);
//			}
//			
//		};
//		menu.setBehindCanvasTransformer(mTransformer);  
//		menu.showContent();
//	}
	
	/**
	 * 切换Fragment，也是切换视图的内容
	 */
//	public void switchContent(Fragment fragment) {
//		mContent = fragment;
//		getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
//		getSlidingMenu().showContent();
//	}

	/**
	 * 菜单按钮点击事件，通过点击ActionBar的Home图标按钮来打开滑动菜单
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
//			toggle();
			return true;	
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * 保存Fragment的状态
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
//		getSupportFragmentManager().putFragment(outState, "mContent", mContent);
	}
	

	
	
	
	
	
	
	
	
	
	
	
	
	

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			if(logoutFlag) {
//				AsyncImageLoader.getInstance().clearCache();
				finish();
//			}else {
//				ToastUtil.showToast(HallActivity.this, "请再点击一次退出程序!");
//				logoutFlag = true;
//				handler.postDelayed(run, 2000);
//			}
			return true;
		}
		return super.onKeyDown(keyCode, event); // 最后，一定要做完以后返回
		// true，或者在弹出菜单后返回true，其他键返回super，让其他键默认
	}

}
