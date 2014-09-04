package com.itcalf.renhe.context.fragmentMain;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Title: MyDialogFragment.java<br>
 * Description: <br>
 * Copyright (c) 人和网版权所有 2014    <br>
 * Create DateTime: 2014-8-27 下午12:33:13 <br>
 * @author wangning
 */
public class MyDialogFragment extends DialogFragment {
	private static String CURRENT_TIME = "CURRENT_TIME";  
	public static MyDialogFragment newInstance(String currentTime) {
		// 创建一个新的带有指定参数的Fragment实例  
		MyDialogFragment fragment = new MyDialogFragment();
		Bundle args = new Bundle();
		args.putString(CURRENT_TIME, currentTime);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// 使用AlertBuilder创建新的对话框  
//		AlertDialog.Builder timeDialog = new AlertDialog.Builder(getActivity());
//		// 配置对话框UI  
//		timeDialog.setTitle("The Current Time Is...");
//		timeDialog.setMessage(getArguments().getString(CURRENT_TIME));
//		// 返回配置完成的对话框  
//		return timeDialog.create();
		
		
		ProgressDialog findPd = new ProgressDialog(getActivity());
		findPd.setMessage(getArguments().getString(CURRENT_TIME));
		//			findPd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		findPd.setCanceledOnTouchOutside(false);
		return findPd;

	}
}
