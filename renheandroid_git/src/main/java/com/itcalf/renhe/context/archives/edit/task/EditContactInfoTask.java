  package com.itcalf.renhe.context.archives.edit.task;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.itcalf.renhe.BaseAsyncTask;
import com.itcalf.renhe.Constants;
import com.itcalf.renhe.dto.MessageBoardOperation;
import com.itcalf.renhe.utils.HttpUtil;
  /**
   * Title: EditSummaryInfoProfessionTask.java<br>
   * Description: <br>
   * Copyright (c) 人和网版权所有 2014    <br>
   * Create DateTime: 2014-8-6 上午9:58:59 <br>
   * @author wangning
   */
public class EditContactInfoTask  extends BaseAsyncTask<MessageBoardOperation> {
	private Context mContext;
	public EditContactInfoTask(Context mContext) {
		super(mContext);
		this.mContext = mContext;
	}
	@Override
	public void doPre() {
		
	}
	@Override
	public void doPost(MessageBoardOperation result) {
		
	}
	@Override
	protected MessageBoardOperation doInBackground(String... params) {
		Map<String, Object> reqParams = new HashMap<String, Object>();
		reqParams.put("sid", params[0]);// 
		reqParams.put("adSId", params[1]);// 
		reqParams.put("tel", params[2]);// 
		reqParams.put("qq", params[3]);// 
		reqParams.put("weixin", params[4]);// 
		try
		{
			MessageBoardOperation mb = (MessageBoardOperation) HttpUtil.doHttpRequest(
					Constants.Http.EDIT_CONTACT_INFO, reqParams,
					MessageBoardOperation.class, null);
			return mb;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	
}

