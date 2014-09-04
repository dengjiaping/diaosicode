package com.itcalf.renhe.context.innermsg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.itcalf.renhe.Constants;
import com.itcalf.renhe.R;
import com.itcalf.renhe.context.contacts.SelectContactsActivity;
import com.itcalf.renhe.context.template.ActivityTemplate;
import com.itcalf.renhe.context.template.BaseActivity;
import com.itcalf.renhe.dto.MessageBoardOperation;
import com.itcalf.renhe.po.Contact;
import com.itcalf.renhe.utils.HttpUtil;
import com.itcalf.renhe.utils.ToastUtil;

public class SendInnerMsgActivity extends BaseActivity {

	private ImageButton mSelectContactBtn;

	private EditText mNameEt;
	private EditText mTitleEt;
	private EditText mContentEt;

	private List<Contact> mContacts;
	private Contact mContact;

	private String mOldContent;
	private String mTextContent;
	private List<Contact> mReceiverList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new ActivityTemplate().doInActivity(this, R.layout.innermsg_sendmsg);
	}

	@Override
	protected void findView() {
		super.findView();
		mNameEt = (EditText) findViewById(R.id.nameEt);
		mTitleEt = (EditText) findViewById(R.id.titleEt);
		mContentEt = (EditText) findViewById(R.id.contentEt);
	}

	@Override
	protected void initData() {
		super.initData();
		setTextValue(R.id.title_txt, "写站内信");
		mSelectContactBtn = (ImageButton) findViewById(R.id.selectContactImgbtn);
		if (getIntent().getExtras() != null) {
			String sid = getIntent().getExtras().getString("sid");
			String name = getIntent().getExtras().getString("name");

			if (sid != null && name != null) {
				mContact = new Contact();
				mContact.setId(sid);
				mContact.setName(name);
				mNameEt.setText(name);
				mContacts = new ArrayList<Contact>();
				mContacts.add(mContact);
				mSelectContactBtn.setVisibility(View.GONE);
			}

			String share = getIntent().getExtras().getString("share");
			String sharesid = getIntent().getExtras().getString("sharesid");
			if (share != null) {
				setTextValue(R.id.title_txt, "分享人脉");
				mTitleEt.setText("您的好友" + getRenheApplication().getUserInfo().getName() + "分享" + share + "给您");
				mOldContent = "您的好友" + "<a href=\"http://www.renhe.cn/viewprofile.html?sid="
						+ getRenheApplication().getUserInfo().getSid() + "\">" + getRenheApplication().getUserInfo().getName()
						+ "</a>" + "分享" + "<a href=\"http://www.renhe.cn/viewprofile.html?sid=" + sharesid + "\">" + share
						+ "</a>" + "给您";
				mContentEt.setText(Html.fromHtml(mOldContent));
				mTextContent = mContentEt.getText().toString();

			}
		}
	}
	 public boolean onPrepareOptionsMenu(Menu menu) {  
	        MenuItem save = menu.findItem(R.id.item_send);
	        save.setVisible(true);
	        save.setTitle("发送");
	        return super.onPrepareOptionsMenu(menu);  
	    }  
	 private void goBack(){
		 if (mNameEt.getText().toString().length() > 0 || mTitleEt.getText().toString().length() > 0
					|| mContentEt.getText().toString().length() > 0) {
				Dialog alertDialog = new AlertDialog.Builder(SendInnerMsgActivity.this).setTitle("新站内信").setMessage("发送新站内信？")
						.setPositiveButton("发送", new DialogInterface.OnClickListener() {

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
			} else {
				finish();
				overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
			}
	 }
	 private void goSave(){
		 if (mNameEt.getText().toString().trim().length() == 0) {
				ToastUtil.showToast(SendInnerMsgActivity.this, "收件人不能为空");
				return ;
			}
			if (mTitleEt.getText().toString().trim().length() == 0) {
				ToastUtil.showToast(SendInnerMsgActivity.this, "主题不能为空");
				return ;
			}
			if (mContentEt.getText().toString().trim().length() == 0) {
				ToastUtil.showToast(SendInnerMsgActivity.this, "内容不能为空");
				return ;
			}
			String content = mContentEt.getText().toString().trim();
			if (mOldContent != null) {
				if (content.contains(mTextContent)) {
					content = content.replace(mTextContent, mOldContent);
				}
			}
			//				content = content + "\n"+contentEt_hint1.getText().toString().trim()+
			//						"\n"+contentEt_hint2.getText().toString().trim();
			new SendMsgTask().execute(toSids(mNameEt.getText().toString()), mTitleEt.getText().toString(), content);
		
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
		mSelectContactBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SendInnerMsgActivity.this, SelectContactsActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("contacts", (Serializable) mReceiverList);
				intent.putExtras(bundle);
				startActivityForResult(intent, 2);
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

				//				startActivityForResult(SelectContactsActivity.class, 2);
			}
		});

		mNameEt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mSelectContactBtn.performClick();
			}
		});
	}

	private String toSids(String content) {
		for (int i = 0; mContacts != null && i < mContacts.size(); i++) {
			content = content.replace(mContacts.get(i).getName(), mContacts.get(i).getId());
		}
		return content;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case RESULT_OK:
			mContacts = new ArrayList<Contact>();
			if (mContact != null && mContact.getId() != null && mContact.getId().length() > 0)
				mContacts.add(mContact);
			mReceiverList = (List<Contact>) data.getExtras().getSerializable("contacts");
			mNameEt.setText("");
			//			Set set = map.keySet();
			//			if (set != null) {
			//				Iterator<String> it = set.iterator();
			//				while (it.hasNext()) {
			//					Contact contact = map.get(it.next());
			//					mContacts.add(contact);
			//					mNameEt.setText(mNameEt.getText().toString().length() > 0 ? mNameEt
			//							.getText().toString() + "," + contact.getName()
			//							: contact.getName());
			//				}
			//			}
			for (Contact contact : mReceiverList) {
				mContacts.add(contact);
				mNameEt.setText(mNameEt.getText().toString().length() > 0 ? mNameEt.getText().toString() + ","
						+ contact.getName() : contact.getName());
			}

			break;
		default:
			break;
		}
	}

	class SendMsgTask extends AsyncTask<String, Void, MessageBoardOperation> {

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
			if (getIntent().getExtras() != null && getIntent().getExtras().getString("share") != null) {
				//reqParams.put("systemMessage", "false");
				reqParams.put("systemMessage", "true");
			} else {

			}

			reqParams.put("adSId", getRenheApplication().getUserInfo().getAdSId());
			try {
				MessageBoardOperation mb = (MessageBoardOperation) HttpUtil.doHttpRequest(Constants.Http.INNERMSG_SEND,
						reqParams, MessageBoardOperation.class, SendInnerMsgActivity.this);
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
			if (result != null && result.getState() == 1) {
				ToastUtil.showToast(SendInnerMsgActivity.this, "站内信发送成功");
				SendInnerMsgActivity.this.finish();
			} else {
				ToastUtil.showNetworkError(SendInnerMsgActivity.this);
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
			findPd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
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
