package com.itcalf.renhe.context.archives;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.itcalf.renhe.BaseAsyncTask;
import com.itcalf.renhe.Constants;
import com.itcalf.renhe.dto.AddFriend;
import com.itcalf.renhe.utils.HttpUtil;
/**
 * 好友添加  参数是被添加好友的SID
 * @author Administrator
 *
 */
public abstract class AddFriendTask extends BaseAsyncTask<AddFriend> {

	public AddFriendTask(Context mContext) {
		super(mContext);
	}

	@Override
	protected AddFriend doInBackground(String... params) {
		Map<String, Object> reqParams = new HashMap<String, Object>();
		reqParams.put("toMemberSId", params[0]);// 被添加好友的会员sid
		reqParams.put("sid", getMyApplication().getUserInfo().getSid());// 发起添加好友请求的会员sid
		reqParams.put("adSId", getMyApplication().getUserInfo().getAdSId());// 加密后用户id和密码的信息以后的每次请求中都要带上它
		try
		{
			AddFriend mb = (AddFriend) HttpUtil.doHttpRequest(
					Constants.Http.ADDFRIEND, reqParams,
					AddFriend.class, null);
			return mb;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
}
