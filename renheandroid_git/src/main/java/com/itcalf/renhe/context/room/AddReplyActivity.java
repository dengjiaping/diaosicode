package com.itcalf.renhe.context.room;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.itcalf.renhe.R;
import com.itcalf.renhe.context.template.BaseActivity;
import com.itcalf.renhe.dto.MessageBoardOperation;
import com.itcalf.renhe.utils.ToastUtil;

/**
 * Feature:添加回复界面
 * Desc:添加回复界面
 * @author xp
 *
 */
public class AddReplyActivity extends BaseActivity {

	//回复字数视图组件
//	private TextView mCountTv;
	//回复内容视图组件
	private EditText mContentEdt;
	//删除内容视图组件
	//是否转发视图组件
	private CheckBox mForwardCk;
	private String mObjectId;
	private String mId;
	private String replyMessageBoardId;
	private String replyMessageBoardObjectId;
	private String senderName;
	private boolean isFromReplyList = false;
	private boolean isModify = false;
	private String countString = "140";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getTemplate().doInActivity(this, R.layout.rooms_replycomment);
	}

	@Override
	protected void findView() {
		super.findView();
//		mCountTv = (TextView) findViewById(R.id.countTv);
		mContentEdt = (EditText) findViewById(R.id.contentEdt);
		mForwardCk = (CheckBox) findViewById(R.id.forwardCk);
	}
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem sendItem = menu.findItem(R.id.item_send);  
		sendItem.setVisible(true);
		sendItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		MenuItem countItem = menu.findItem(R.id.item_count);  
		countItem.setVisible(true);
		countItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		countItem.setTitle(countString);
		return super.onPrepareOptionsMenu(menu);
	}
	@Override
	protected void initData() {
		super.initData();
		final float scale = this.getResources().getDisplayMetrics().density;
//		mForwardCk.setPadding(mForwardCk.getPaddingLeft()
//				- (int) (16.0f * scale + 0.5f), mForwardCk.getPaddingTop(),
//				mForwardCk.getPaddingRight(), mForwardCk.getPaddingBottom());
		mObjectId = getIntent().getExtras().getString("objectId");
		mId = getIntent().getExtras().getString("id");
		replyMessageBoardId = getIntent().getExtras().getString("replyMessageBoardId");
		replyMessageBoardObjectId = getIntent().getExtras().getString("replyMessageBoardObjectId");
		senderName = getIntent().getExtras().getString("senderName");
		isFromReplyList = getIntent().getExtras().getBoolean("isFromReplylist", false);
		if(isFromReplyList && senderName != null){
			setTextValue(R.id.title_txt, "回复 "+senderName);
			mContentEdt.setText("回复"+senderName+" : ");
			mContentEdt.setSelection(mContentEdt.getText().length());
		}
		if(!isFromReplyList){
			setTextValue(R.id.title_txt, "回复");
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 1:
			ProgressDialog findPd = new ProgressDialog(this);
//			findPd.setTitle("发布新回复");
			findPd.setMessage("请稍候...");
//			findPd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			findPd.setCanceledOnTouchOutside(false);
			return findPd;
		default:
			return null;
		}
	}
	private void goBack(){
		if(isModify){
			Dialog alertDialog = new AlertDialog.Builder(AddReplyActivity.this).setTitle("新留言")
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
		final String mcontent = content;
		
		if (!TextUtils.isEmpty(content)) {
			new AsyncTask<Object, Void, MessageBoardOperation>() {
				@Override
				protected MessageBoardOperation doInBackground(
						Object... params) {
					try {
						return getRenheApplication()
								.getMessageBoardCommand()
								.replyMessageBoard((String) params[0],
										(String) params[1],
										(String) params[2],
										(String) params[3],
										(String) params[4],
										(Boolean) params[5],(String) params[6],(String) params[7],AddReplyActivity.this);
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
				protected void onPostExecute(
						MessageBoardOperation result) {
					super.onPostExecute(result);
					removeDialog(1);
					if (null != result) {
						if (1 == result.getState()) {
							//发广播，通知客厅界面更新消息的状态
//							if(!isFromReplyList){
								Intent intent = new Intent();
								intent.putExtra("objectId", mObjectId);
								intent.setAction(MessageBoardActivity.ROOM_ITEM_STATE_ACTION_STRING_REPLY);
								AddReplyActivity.this.sendBroadcast(intent);
								
								Intent intent1 = new Intent();
								intent1.putExtra("objectId", mObjectId);
								intent1.putExtra("content", mcontent);
								setResult(RESULT_OK,intent1);
//							}
							ToastUtil.showToast(AddReplyActivity.this, "发布成功");
							finish();
						} else {
							ToastUtil.showErrorToast(
									AddReplyActivity.this, "发布失败");
						}
					}else {
						ToastUtil.showNetworkError(AddReplyActivity.this);
					}
				}
			}.execute(getRenheApplication().getUserInfo().getAdSId(),
					getRenheApplication().getUserInfo().getSid(), mId,
					mObjectId, content, mForwardCk.isChecked(),replyMessageBoardId, replyMessageBoardObjectId);
		} else {
			ToastUtil.showToast(AddReplyActivity.this, "回复不能为空");
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
		//监听文本内容变化事件
		mContentEdt.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (count > 140) {
					return;
				}
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
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
		//监听回复留言按钮事件
		//监听删除按钮事件
//		mDelImage.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				mContentEdt.setText("");
//			}
//		});
	}
	@Override
	public void onBackPressed() {
		goBack();
	}
}
