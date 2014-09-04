package com.itcalf.renhe.context.room;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Window;
import android.widget.EditText;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.itcalf.renhe.R;
import com.itcalf.renhe.context.template.BaseActivity;
import com.itcalf.renhe.dto.MessageBoardOperation;
import com.itcalf.renhe.utils.ToastUtil;

/**
 * Feature: 转发留言界面 Desc:转发留言界面
 * 
 * @author xp
 * 
 */
public class ForwardMessageBoardActivity extends BaseActivity {

//	private TextView mCountTv;
	private EditText mContentEdt;
//	private ImageView mDelImage;
	private String mObjectId;
	private boolean isModify = false;
	private String countString = "140";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getTemplate().doInActivity(this, R.layout.rooms_forwardcomment);
	}

	@Override
	protected void findView() {
		super.findView();
//		mCountTv = (TextView) findViewById(R.id.countTv);
		mContentEdt = (EditText) findViewById(R.id.contentEdt);
//		mDelImage = (ImageView) findViewById(R.id.delImg);
	}

	@Override
	protected void initData() {
		super.initData();
		setTextValue(R.id.title_txt, "转发");
		mObjectId = getIntent().getExtras().getString("objectId");
		String sender = getIntent().getExtras().getString("sender");
		String rawContent = getIntent().getExtras().getString("rawContent");

		if (!TextUtils.isEmpty(sender) && !TextUtils.isEmpty(rawContent)) {
			mContentEdt.append("//@" + sender + ":" + rawContent);
//			mCountTv.setText((140 - mContentEdt.getText().toString().length()) + "");
			countString = (140 - mContentEdt.getText().toString().length()) + "";
			getWindow().invalidatePanelMenu(Window.FEATURE_OPTIONS_PANEL);
		}else{
//			mContentEdt.setHint("说说分享心得...");
			mContentEdt.setHint("");
		}

	}
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem sendItem = menu.findItem(R.id.item_send);  
		sendItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		sendItem.setVisible(true);
		MenuItem countItem = menu.findItem(R.id.item_count);  
		countItem.setVisible(true);
		countItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		countItem.setTitle(countString);
		
		return super.onPrepareOptionsMenu(menu);
	}
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 1:
			ProgressDialog findPd = new ProgressDialog(this);
			findPd.setMessage("正在转发留言,请稍候...");
//			findPd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			findPd.setCanceledOnTouchOutside(false);
			return findPd;
		default:
			return null;
		}
	}
	private void goBack(){
		if(isModify){
			Dialog alertDialog = new AlertDialog.Builder(ForwardMessageBoardActivity.this).setTitle("新留言")
					.setMessage("发布新留言？").setPositiveButton("发布", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							goSave();
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
		}else{
			finish();
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
		}
	}
	private void goSave(){
		String content = mContentEdt.getText().toString().trim();
		if (!TextUtils.isEmpty(content)) {
			new AsyncTask<String, Void, MessageBoardOperation>() {
				@Override
				protected MessageBoardOperation doInBackground(String... params) {
					try {
						return getRenheApplication().getMessageBoardCommand().forwardMessageBoard(params[0], params[1],
								params[2], params[3], ForwardMessageBoardActivity.this);
					} catch (Exception e) {
						System.out.println(e);
						return null;
					}
				}

				@Override
				protected void onPreExecute() {
					super.onPreExecute();
					showDialog(1);
				}

				@Override
				protected void onPostExecute(MessageBoardOperation result) {
					super.onPostExecute(result);
					removeDialog(1);
					if (null != result) {
						if (1 == result.getState()) {
							finish();
						} else {
							ToastUtil.showErrorToast(ForwardMessageBoardActivity.this, "发布失败");
						}
					} else {
						ToastUtil.showNetworkError(ForwardMessageBoardActivity.this);
					}
				}
			}.execute(getRenheApplication().getUserInfo().getAdSId(), getRenheApplication().getUserInfo().getSid(),
					mObjectId, content);
		} else {
			ToastUtil.showToast(ForwardMessageBoardActivity.this, "内容不能为空");
		}
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
		mContentEdt.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (count > 140) {
					return;
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
//				mCountTv.setText(140 - s.length() + "");
				countString = 140 - s.length() + "";
				getWindow().invalidatePanelMenu(Window.FEATURE_OPTIONS_PANEL);
				if(s.length() > 0){
					isModify = true;
				}else{
					isModify = false;
				}
			}
		});
//		mDelImage.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				mContentEdt.setText("");
//			}
//		});
		Selection.setSelection(mContentEdt.getEditableText(), 0);
	}
	@Override
	public void onBackPressed() {
		goBack();
	}
}
