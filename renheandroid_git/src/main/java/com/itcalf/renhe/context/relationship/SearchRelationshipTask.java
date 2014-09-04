package com.itcalf.renhe.context.relationship;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.AsyncTask;

import com.itcalf.renhe.Constants;
import com.itcalf.renhe.R;
import com.itcalf.renhe.RenheApplication;
import com.itcalf.renhe.dto.Relationship;
import com.itcalf.renhe.utils.HttpUtil;

public class SearchRelationshipTask extends
		AsyncTask<Object, Void, Relationship> {

	private IDataBack mDataBack;
	private Context mContext;
	private String[] mFrom = new String[] { "headImage", "nameTv", "titleTv",
			"infoTv", "rightImage", "sid","accountType","isRealName" };

	public SearchRelationshipTask(Context context, IDataBack back) {
		super();
		this.mContext = context;
		this.mDataBack = back;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mDataBack.onPre();
	}

	@Override
	protected void onPostExecute(Relationship result) {
		super.onPostExecute(result);
		if (null != result) {
			if (1 == result.getState()) {
				if (null != result.getMemberList()
						&& result.getMemberList().length > 0) {
					List<Map<String, Object>> rsList = new ArrayList<Map<String, Object>>();
					for (int i = 0; i < result.getMemberList().length; i++) {
						final Map<String, Object> map = new LinkedHashMap<String, Object>();
						map.put("avatar_path", result.getMemberList()[i].getUserface());
						map.put(mFrom[1], result.getMemberList()[i].getName());
						map.put(mFrom[2], result.getMemberList()[i].getTitle());
						map.put(mFrom[3],
								result.getMemberList()[i].getCompany());
						if (result.getMemberList()[i].getContactStep() == 1) {
							map.put(mFrom[4], R.drawable.icon_1st);
						} else if(result.getMemberList()[i].getContactStep() == 2)  {
//							map.put(mFrom[4], R.drawable.icon_2nd);
						}
//						else{
//							map.put(mFrom[4], R.drawable.icon_other);
//						}
						map.put(mFrom[5], result.getMemberList()[i].getSid());
						map.put(mFrom[6], result.getMemberList()[i].getAccountType());
						map.put(mFrom[7], result.getMemberList()[i].isRealname());
						rsList.add(map);
					}
					mDataBack.onPost(rsList);
					return ;
				}
			}
		}
		mDataBack.onPost(null);
	}

	@Override
	protected Relationship doInBackground(Object... params) {
		Map<String, Object> reqParams = new HashMap<String, Object>();
		reqParams.put("sid", ((RenheApplication) mContext
				.getApplicationContext()).getUserInfo().getSid());
		reqParams.put("query", params[0]);
		reqParams.put("addressId", params[1]);
		reqParams.put("industryId", params[2]);
		reqParams.put("company", params[3]);
		reqParams.put("title", params[4]);
		reqParams.put("start", params[5]);
		reqParams.put("count", params[6]);
		reqParams.put("adSId", ((RenheApplication) mContext
				.getApplicationContext()).getUserInfo().getAdSId());
		try {
			Relationship mb = (Relationship) HttpUtil.doHttpRequest(
					Constants.Http.SEARCH_RELATIONSHIP, reqParams,
					Relationship.class,mContext);
			return mb;
		} catch (Exception e) {
			System.out.println(e);
		}
		return null;
	}

	interface IDataBack {

		void onPre();

		void onPost(List<Map<String, Object>> result);

	}
}
