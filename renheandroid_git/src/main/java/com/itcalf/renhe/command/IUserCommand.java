package com.itcalf.renhe.command;

import com.itcalf.renhe.dto.UserInfo;

public interface IUserCommand {

	UserInfo login(UserInfo userInfo);
	
	UserInfo register(UserInfo userInfo);
	UserInfo oldRegister(UserInfo userInfo);
	
	UserInfo getLoginUser();
	
	UserInfo[] getAllUsers();
	
	long delUser(String email);
	
	long insertOrUpdate(UserInfo userInfo);
	
}
