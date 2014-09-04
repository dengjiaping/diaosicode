package com.itcalf.renhe.command;

import android.content.Context;

import com.itcalf.renhe.dto.Version;

public interface IPhoneCommand {

	Version getLastedVersion(Context context) throws Exception;
	
}
