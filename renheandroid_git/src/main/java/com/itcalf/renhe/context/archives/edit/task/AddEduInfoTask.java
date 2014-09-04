  package com.itcalf.renhe.context.archives.edit.task;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.itcalf.renhe.BaseAsyncTask;
import com.itcalf.renhe.Constants;
import com.itcalf.renhe.dto.MessageBoardOperation;
import com.itcalf.renhe.dto.MessageBoardOperationWithErroInfo;
import com.itcalf.renhe.utils.HttpUtil;
  /**
   * Title: EditSummaryInfoProfessionTask.java<br>
   * Description: <br>
   * Copyright (c) 人和网版权所有 2014    <br>
   * Create DateTime: 2014-8-6 上午9:58:59 <br>
   * @author wangning
   */
public class AddEduInfoTask  extends BaseAsyncTask<MessageBoardOperationWithErroInfo> {
	private Context mContext;
	public AddEduInfoTask(Context mContext) {
		super(mContext);
		this.mContext = mContext;
	}
	@Override
	public void doPre() {
		
	}
	@Override
	public void doPost(MessageBoardOperationWithErroInfo result) {
		
	}
	@Override
	protected MessageBoardOperationWithErroInfo doInBackground(String... params) {
		Map<String, Object> reqParams = new HashMap<String, Object>();
		reqParams.put("sid", params[0]);// 
		reqParams.put("adSId", params[1]);// 
		reqParams.put("schoolId", params[2]);// 
		reqParams.put("studyField", params[3]);// 
		reqParams.put("degree", params[4]);// 
		reqParams.put("startYear", params[5]);// 
		reqParams.put("startMonth", params[6]);// 
		reqParams.put("endYear", params[7]);// 
		reqParams.put("endMonth", params[8]);// 
		reqParams.put("activities", params[9]);// 
		reqParams.put("notes", params[10]);// 
		reqParams.put("schoolName", params[11]);// 
		try
		{
			MessageBoardOperationWithErroInfo mb = (MessageBoardOperationWithErroInfo) HttpUtil.doHttpRequest(
					Constants.Http.ADD_EDU_INFO, reqParams,
					MessageBoardOperationWithErroInfo.class, null);
			return mb;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	
}

