package com.itcalf.renhe.command.impl;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.itcalf.renhe.Constants;
import com.itcalf.renhe.command.IProfileCommand;
import com.itcalf.renhe.dto.Profile;
import com.itcalf.renhe.utils.HttpUtil;

public class ProfileCommandImpl implements IProfileCommand {

	@Override
	public Profile showProfile(String viewSId, String sid, String adSId,Context context) throws Exception {
		Map<String, Object> reqParams = new HashMap<String, Object>();
		reqParams.put("viewSId", viewSId);// 请求viewprofile的会员sid
		reqParams.put("sid", sid);
		reqParams.put("adSId", adSId);
		return (Profile) HttpUtil.doHttpRequest(
				Constants.Http.SHOW_PROFILE, reqParams, Profile.class,context);
	}

}
