package com.itcalf.renhe.context.more;

import java.util.HashMap;
import java.util.Map;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.itcalf.renhe.Constants;
import com.itcalf.renhe.R;
import com.itcalf.renhe.context.innermsg.SwipeBackActivity;
import com.itcalf.renhe.context.template.ActivityTemplate;
import com.itcalf.renhe.dto.MessageBoardOperation;
import com.itcalf.renhe.utils.HttpUtil;
import com.itcalf.renhe.utils.ToastUtil;

/**
 * Feature:意见反馈界面 
 * Desc:意见反馈界面
 * @author xp
 * 
 */
public class FeedBackActivity extends SwipeBackActivity {
	private Button mSendBt;
	private EditText mContentEt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new ActivityTemplate().doInActivity(this, R.layout.more_feedback);
	}

	@Override
	protected void findView() {
		super.findView();
		mSendBt = (Button) findViewById(R.id.sendBt);
		mContentEt = (EditText) findViewById(R.id.content_edt);
	}

	@Override
	protected void initData() {
		super.initData();
		setTextValue(R.id.sendBt, "提交");
		setTextValue(R.id.title_txt, "意见反馈");
	}

	@Override
	protected void initListener() {
		super.initListener();

		mSendBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String content = mContentEt.getText().toString().trim();
				if (content.length() == 0) {
					ToastUtil.showToast(FeedBackActivity.this, "反馈意见不能为空");
				} else {
					new FeedBackTask().execute(content);
				}
			}
		});
	}

	/**
	 * 提交人和网意见反馈信息
	 * @author xp
	 *
	 */
	class FeedBackTask extends AsyncTask<String, Void, MessageBoardOperation> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showDialog(1);
		}

		@Override
		protected MessageBoardOperation doInBackground(String... params) {
			Map<String, Object> reqParams = new HashMap<String, Object>();

			reqParams.put("sid", getRenheApplication().getUserInfo().getSid());
			reqParams.put("adSId", getRenheApplication().getUserInfo()
					.getAdSId());
			reqParams.put("content", params[0]);
			try {
				MessageBoardOperation mb = (MessageBoardOperation) HttpUtil
						.doHttpRequest(Constants.Http.MORE_FEEDBACK, reqParams,
								MessageBoardOperation.class,FeedBackActivity.this);
				return mb;
			} catch (Exception e) {
				System.out.println(e);
			}
			return null;
		}

		@Override
		protected void onPostExecute(MessageBoardOperation result) {
			super.onPostExecute(result);
			removeDialog(1);
			if (result != null && result.getState() == 1) {
				ToastUtil.showToast(FeedBackActivity.this, "提交成功");
				finish();
			} else {
				ToastUtil.showErrorToast(FeedBackActivity.this, "提交失败");
			}
		}

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 1:
			ProgressDialog findPd = new ProgressDialog(this);
			findPd.setTitle("提交信息中");
			findPd.setMessage("请稍候...");
//			findPd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			findPd.setCanceledOnTouchOutside(false);
			return findPd;
		default:
			return null;
		}
	}
}
