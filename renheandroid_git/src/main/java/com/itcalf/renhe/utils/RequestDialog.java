  package com.itcalf.renhe.utils;

import com.itcalf.renhe.R;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
  /**
   * Title: RequestDialog.java<br>
   * Description: <br>
   * Copyright (c) 人和网版权所有 2014    <br>
   * Create DateTime: 2014-6-10 上午10:57:22 <br>
   * @author wangning
   */
public class RequestDialog {
	private Context context;
	private LinearLayout dialogLayout;
	private String content = "";
	private TextView contentTv;
	public RequestDialog(Context context,String cString){
		this.context = context;
		this.content = cString;
		initView();
	}
	private void initView(){
		dialogLayout = (LinearLayout) View.inflate(context,R.layout.request_dialog,null);
		contentTv = (TextView)dialogLayout.findViewById(R.id.textview);
		contentTv.setText(content+"...");
	}
	public void addFade(ViewGroup group)
	  {
	    if (group != null) {
	      ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
	        -1, -1);
	      group.addView(dialogLayout, params);
	    }
	  }

	public void removeFade(ViewGroup group)
	  {
	    if (group != null) {
	      int index = group.getChildCount() - 1;
	      group.removeViewAt(index);
	    }
	  }

//	public RelativeLayout createFade() 
//	  {
//	    RelativeLayout.LayoutParams rParams = new RelativeLayout.LayoutParams(
//	      -1, -1);
//	    rParams.addRule(13, -1);
//
//	    RelativeLayout rLayout = new RelativeLayout(this.context);
//	    rLayout.setBackgroundColor(context.getResources().getColor(R.color.transparent_background));
//
//
//
////	    ProgressBar progress = new ProgressBar(this.context);
////	    lLayout.addView(progress, lParams);
////
////	    TextView mark_tv = new TextView(this.context);
////	    mark_tv.setText("验证中...");
////	    mark_tv.setTextColor(-16777216);
////	    mark_tv.setTextSize(20.0F);
//	    
//	    rLayout.addView(dialogLayout, rParams);
//	    rLayout.setOnTouchListener(new View.OnTouchListener()
//	    {
//	      public boolean onTouch(View arg0, MotionEvent arg1) {
//	        return true;
//	      }
//	    });
//	    return rLayout;
//	  }
}

