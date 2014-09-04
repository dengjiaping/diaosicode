package com.itcalf.renhe.context.innermsg;

import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.itcalf.renhe.Constants;
import com.itcalf.renhe.R;
import com.itcalf.renhe.context.template.ActivityTemplate;
import com.itcalf.renhe.context.template.BaseActivity;
import com.itcalf.renhe.dto.MessageBoardOperation;
import com.itcalf.renhe.dto.MsgInfo;
import com.itcalf.renhe.utils.HttpUtil;
import com.itcalf.renhe.utils.ToastUtil;

public class InnerMsgReplyActivity extends BaseActivity {
	private MsgInfo mMsgInfo;

	private TextView mNameTv;
	private EditText mTitleEt;
	private EditText mContentEt;

	private String mOldContent;
	private String mTextContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new ActivityTemplate().doInActivity(this, R.layout.innermsg_replymsg);
	}

	@Override
	protected void findView() {
		super.findView();
		mNameTv = (TextView) findViewById(R.id.nameTv);
		mTitleEt = (EditText) findViewById(R.id.titleEt);
		mContentEt = (EditText) findViewById(R.id.contentEt);
	}
	@Override
	protected void initData() {
		super.initData();
		setTextValue(R.id.title_txt, "回复");
//		mSendBt.setText("发送");
		mContentEt.requestFocus();
		mMsgInfo = (MsgInfo) getIntent().getExtras().getSerializable("msgInfo");
		String content=getIntent().getExtras().getString("ContentString");
		mNameTv.setText(mMsgInfo.getUserInfo().getName());
		mTitleEt.setText("Re: " + mMsgInfo.getMessageInfo().getSubject());
		
		mOldContent = "<br/>" + content;
		mContentEt.setText(Html.fromHtml("<br/>"
				+ content));
		mTextContent = mContentEt.getText().toString().trim();
	}
    public boolean onPrepareOptionsMenu(Menu menu) {  
        MenuItem save = menu.findItem(R.id.item_send);
        save.setVisible(true);
        save.setTitle("发送");
        return super.onPrepareOptionsMenu(menu);  
    }  
    private void goBack(){
    	Dialog alertDialog = new AlertDialog.Builder(InnerMsgReplyActivity.this).setTitle("新站内信").setMessage("发送新站内信？")
				.setPositiveButton("发送", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						goBack();
					}
				}).setNegativeButton("放弃", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
						overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
					}
				}).setNeutralButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).create();

		alertDialog.show();
    }
    private void goSave(){
    	if (mNameTv.getText().toString().trim().length() == 0) {
			ToastUtil.showToast(InnerMsgReplyActivity.this, "收件人不能为空");
			return ;
		}
		if (mTitleEt.getText().toString().trim().length() == 0) {
			ToastUtil.showToast(InnerMsgReplyActivity.this, "主题不能为空");
			return ;
		}
		if (mContentEt.getText().toString().trim().length() == 0) {
			ToastUtil.showToast(InnerMsgReplyActivity.this, "内容不能为空");
			return ;
		}
		String content = mContentEt.getText().toString().trim();
		if (content.contains(mTextContent)) {
			content=content.replace(mTextContent, mOldContent);
		} 
		new SendMsgTast().execute(mMsgInfo.getUserInfo().getSid(),
				mTitleEt.getText().toString(), content);
    }
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case android.R.id.home:
			goBack();
		
			return true;
		case R.id.item_send:
			goSave();
		return true;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	protected void initListener() {
		super.initListener();
	}

	class SendMsgTast extends AsyncTask<String, Void, MessageBoardOperation> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showDialog(1);
		}

		@Override
		protected MessageBoardOperation doInBackground(String... params) {
			Map<String, Object> reqParams = new HashMap<String, Object>();
			reqParams.put("sid", getRenheApplication().getUserInfo().getSid());
			reqParams.put("receiverSIds", params[0]);
			reqParams.put("subject", params[1]);
			reqParams.put("content", params[2]);
			reqParams.put("adSId", getRenheApplication().getUserInfo()
					.getAdSId());
			try {
				MessageBoardOperation mb = (MessageBoardOperation) HttpUtil
						.doHttpRequest(Constants.Http.INNERMSG_SEND, reqParams,
								MessageBoardOperation.class,InnerMsgReplyActivity.this);
				return mb;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(MessageBoardOperation result) {
			super.onPostExecute(result);
			removeDialog(1);

			if (result != null) {
				if(result.getState() == 1) {
					ToastUtil.showToast(InnerMsgReplyActivity.this, "站内信发送成功");
					InnerMsgReplyActivity.this.finish();
				}
			} else {
				ToastUtil.showNetworkError(InnerMsgReplyActivity.this);
			}
		}

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 1:
			ProgressDialog findPd = new ProgressDialog(this);
//			findPd.setTitle("发送站内信中");
			findPd.setMessage("正在发送...");
//			findPd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			findPd.setCanceledOnTouchOutside(false);
			return findPd;
		default:
			return null;
		}
	}
	@Override
	public void onBackPressed() {
		goBack();
	}
}
