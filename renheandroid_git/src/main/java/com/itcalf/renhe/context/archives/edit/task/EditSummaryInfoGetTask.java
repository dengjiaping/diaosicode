  package com.itcalf.renhe.context.archives.edit.task;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.itcalf.renhe.BaseAsyncTask;
import com.itcalf.renhe.Constants;
import com.itcalf.renhe.dto.MessageBoardOperation;
import com.itcalf.renhe.utils.HttpUtil;
  /**
   * Title: EditSummaryInfoSpecialtiesTask.java<br>
   * Description: <br>
   * Copyright (c) 人和网版权所有 2014    <br>
   * Create DateTime: 2014-8-6 上午9:58:59 <br>
   * @author wangning
   */
public class EditSummaryInfoGetTask  extends BaseAsyncTask<MessageBoardOperation> {
	private Context mContext;
	private String[] strings;
	public EditSummaryInfoGetTask(Context mContext,String[] strings) {
		super(mContext);
		this.mContext = mContext;
		this.strings = strings;
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
		reqParams.put("aimTags", strings);// 
		try
		{
			MessageBoardOperation mb = (MessageBoardOperation) HttpUtil.doHttpRequest(
					Constants.Http.EDITGET, reqParams,
					MessageBoardOperation.class, null);
			return mb;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	
}

