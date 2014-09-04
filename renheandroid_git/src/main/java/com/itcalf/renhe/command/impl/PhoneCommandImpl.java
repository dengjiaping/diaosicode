package com.itcalf.renhe.command.impl;

import android.content.Context;

import com.itcalf.renhe.Constants;
import com.itcalf.renhe.command.IPhoneCommand;
import com.itcalf.renhe.dto.Version;
import com.itcalf.renhe.utils.HttpUtil;

public class PhoneCommandImpl implements IPhoneCommand {

	@Override
	public Version getLastedVersion(Context context) throws Exception {
		return (Version) HttpUtil.doHttpRequest(
				Constants.Http.CHECK_VERION_UPDATE, null,
				Version.class,context);
	}

}
