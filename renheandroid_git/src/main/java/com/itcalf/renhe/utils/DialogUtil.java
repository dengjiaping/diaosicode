package com.itcalf.renhe.utils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itcalf.renhe.R;
import com.itcalf.renhe.context.register.AuthActivity;
import com.itcalf.renhe.context.template.BaseActivity;

public class DialogUtil {
	private Context context;
	private String content = "";
	private MyDialogClickListener myDialogClickListener;
	public final static int SURE_BUTTON = 1;
	public final static int CANCLE_BUTTON = 0;
	
	/**
	 * 自定义dialog 按钮监听
	 * 
	 * @author Renhe
	 *
	 */
	public interface MyDialogClickListener{
		public void onclick(int id);
	}
	public DialogUtil(Context context, MyDialogClickListener myDialogClickListener) {
		this.context = context;
		this.myDialogClickListener = myDialogClickListener;
	}

	/**
	 * 创建对话框
	 */
	private AlertDialog mAlertDialog;
	private TextView dialogTitleTv;
	private View seleparateLineView;
	private Button sureButton;
	private Button cancleButton;
	private TextView contentTv;
	/**
	 * 
	 * @param context
	 * @param title
	 * @param cancleString
	 * @param sureString
	 * @param content
	 */
	public void createDialog(Context context,String title, String cancleString, String sureString, String content) {
		this.context = context;
		this.content = content;
		RelativeLayout view = (RelativeLayout) LayoutInflater.from(context)
				.inflate(com.itcalf.renhe.R.layout.register_dialog, null);

		Builder mDialog = new AlertDialog.Builder(context);
		mAlertDialog = mDialog.create();
		mAlertDialog.setView(view,0,0,0,0);
		
		dialogTitleTv = (TextView)view.findViewById(R.id.dialog_title);
		seleparateLineView = view.findViewById(R.id.seperate_line);
		sureButton = (Button) view.findViewById(com.itcalf.renhe.R.id.dialog_sure_bt);
		cancleButton = (Button) view
				.findViewById(R.id.dialog_cancle_bt);
		contentTv = (TextView) view.findViewById(R.id.content_tv);
		
		if(!TextUtils.isEmpty(title)){
			dialogTitleTv.setText(title);
		}else{
			dialogTitleTv.setVisibility(View.GONE);
			seleparateLineView.setVisibility(View.GONE);
		}
		if(!TextUtils.isEmpty(cancleString)){
		}else{
			cancleButton.setVisibility(View.GONE);
		}
		sureButton.setText(sureString);
		cancleButton.setText(cancleString);
		contentTv.setText(content);
		mAlertDialog.setCanceledOnTouchOutside(true);
		mAlertDialog.show();
		sureButton.setOnClickListener(new ButtonListener());
		cancleButton.setOnClickListener(new ButtonListener());
	}

	class ButtonListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.dialog_sure_bt:
				if(!TextUtils.isEmpty(cancleButton.getText().toString().trim())){
					myDialogClickListener.onclick(SURE_BUTTON);
				}
				mAlertDialog.dismiss();
				break;
			case R.id.dialog_cancle_bt:
				myDialogClickListener.onclick(CANCLE_BUTTON);
				mAlertDialog.dismiss();
				break;

			default:
				break;
			}
		}
	}

	public void DismissDialog() {
		if (mAlertDialog != null)
			mAlertDialog.dismiss();
	}

	public boolean isShowing() {
		if(mAlertDialog == null){
			return false;
		}else {
			return mAlertDialog.isShowing();
		}
		
	}

}
