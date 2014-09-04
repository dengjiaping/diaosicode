package com.itcalf.renhe.context.room;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.util.Log;
import android.widget.TextView;

import com.itcalf.renhe.R;
import com.itcalf.renhe.RenheApplication;
import com.itcalf.renhe.dto.MsgComments;
import com.itcalf.renhe.dto.MsgComments.CommentList;

/**
 * Feature:回复留言列表异步加载
 * Desc:回复留言列表异步加载
 * @author xp
 *
 */
public class ReplyListTask extends AsyncTask<Object, Void, MsgComments> {

	//数据回调
	private IDataBack mDataBack;
	private Context mContext;
	//显示内容字段
	private String[] mFrom = new String[] { "titleTv", "infoTv", "timeTv",
			"objectId" ,"userFace","accountType","isRealName"};

	public ReplyListTask(Context context, IDataBack back) {
		super();
		this.mContext = context;
		this.mDataBack = back;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mDataBack.onPre();
	}

	//后台线程调用服务端接口
	@Override
	protected MsgComments doInBackground(Object... params) {
		try {
			return ((RenheApplication) mContext.getApplicationContext())
					.getMessageBoardCommand().getMsgComments((String) params[0],
							(String) params[1], (String) params[2],
							(Integer) params[3], (Integer) params[4],mContext);
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}
	
	@Override
	protected void onPostExecute(MsgComments result) {
		super.onPostExecute(result);
		if (null != result && 1 == result.getState()) {
			//获取回复留言列表数据，封装后执行回调
			CommentList[] commentList = result.getCommentList();
			if (null != commentList && commentList.length > 0) {
				List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
				for (int i = 0; i < commentList.length; i++) {
					CommentList comment = commentList[i];
					Map<String, Object> map = new HashMap<String, Object>();
					map.put(mFrom[0], comment.getSenderName());
					map.put(mFrom[1], Html.fromHtml(comment.getContent()));
//					map.put(mFrom[1], Html.fromHtml(comment.getContent(), new Html.ImageGetter() {
//						@Override
//						public Drawable getDrawable(String source) {
//							InputStream is = null;
//							try {
//								is = (InputStream) new URL(source).getContent();
//								Drawable d = Drawable.createFromStream(is, "src");
//								d.setBounds(0, 0, d.getIntrinsicWidth(),
//										d.getIntrinsicHeight());
//								is.close();
//								return d;
//							} catch (Exception e) {
//								return null;
//							}
//						}
//					}, null));
					map.put(mFrom[2], comment.getCreatedDate());
					map.put(mFrom[3], comment.getObjecteId());
					map.put("id",comment.getId()+"");
					map.put("sid", comment.getSenderSid());
					map.put(mFrom[4], comment.getSenderUserFace());
					map.put(mFrom[5], comment.getSenderAccountType());
					map.put(mFrom[6], comment.isSenderIsRealname());
					list.add(map);
				}
				mDataBack.onPost(list);
			}else {
				mDataBack.onPost(null);
			}
		}
	}

	interface IDataBack {

		void onPre();

		void onPost(List<Map<String, Object>> result);

	}

}
