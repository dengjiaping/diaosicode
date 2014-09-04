  package com.itcalf.renhe;

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
public class UploadCrashInfoTask  extends BaseAsyncTask<MessageBoardOperation> {
	private Context mContext;
	public UploadCrashInfoTask(Context mContext) {
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
		reqParams.put("phonemode", params[2]);// 
		reqParams.put("gender", params[3]);// 
		reqParams.put("industryId", params[4]);// 
		reqParams.put("addressId", params[5]);// 
		try
		{
			MessageBoardOperation mb = (MessageBoardOperation) HttpUtil.doHttpRequest(
					Constants.Http.EDIT_SELF_INFO, reqParams,
					MessageBoardOperation.class, null);
			return mb;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	
}
