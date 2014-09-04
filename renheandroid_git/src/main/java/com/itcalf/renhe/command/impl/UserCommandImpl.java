package com.itcalf.renhe.command.impl;

import java.util.HashMap;
import java.util.Map;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.itcalf.renhe.Constants;
import com.itcalf.renhe.command.IUserCommand;
import com.itcalf.renhe.database.sqlite.TablesConstant;
import com.itcalf.renhe.database.sqlite.UserDBHelper;
import com.itcalf.renhe.dto.UserInfo;
import com.itcalf.renhe.utils.HttpUtil;

public class UserCommandImpl implements IUserCommand {

	private Context mContext;

	public UserCommandImpl(Application application) {
		super();
		mContext = application.getApplicationContext();
	}

	@Override
	public UserInfo login(UserInfo userInfo) {
		try {
			Map<String, Object> reqParams = new HashMap<String, Object>();
			reqParams.put("username", userInfo.getAccountType());
			reqParams.put("password", userInfo.getPwd());
			UserInfo info = (UserInfo) HttpUtil.doHttpRequest(Constants.Http.NEWLOGIN, reqParams, UserInfo.class, mContext);
			return info;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public UserInfo register(UserInfo userInfo) {
		Map<String, Object> reqParams = new HashMap<String, Object>();
		reqParams.put("mobile", userInfo.getMobile());
		reqParams.put("email", userInfo.getEmail());
//		reqParams.put("password", userInfo.getPwd());
//		reqParams.put("repeatPassword", userInfo.getPwd());
		reqParams.put("name", userInfo.getName());
		reqParams.put("title", userInfo.getTitle());
		reqParams.put("company", userInfo.getCompany());
		
		try {
			return (UserInfo) HttpUtil.doHttpRequest(Constants.Http.REGISTERMOBILE, reqParams, UserInfo.class, mContext);
		} catch (Exception e) {
			if (Constants.LOG) {
				Log.d(Constants.TAG, "register", e);
			}
		}
		return null;
	}

	@Override
	public UserInfo getLoginUser() {
		UserDBHelper userDBHelper = new UserDBHelper(mContext, TablesConstant.USER_TABLE);
		UserInfo userInfo = null;
		Cursor cursor = null;
		try {
			cursor = userDBHelper.find(null, TablesConstant.USER_TABLE_COLUMN_REMEMBER + "=?", new String[] { "1" }, null, null,
					TablesConstant.USER_TABLE_COLUMN_LOGINTIME + " DESC");
			if (null != cursor) {
				if (cursor.moveToFirst()) {
					userInfo = new UserInfo();
					userDBHelper.contentValueToPo(cursor, userInfo);
				}
			}
		} catch (Exception e) {
			if (Constants.LOG) {
				Log.d(Constants.TAG, "getLoginUser", e);
			}
		} finally {
			if (null != cursor) {
				cursor.close();
			}
			userDBHelper.closeDB();
		}
		return userInfo;
	}

	@Override
	public UserInfo[] getAllUsers() {
		UserInfo[] userInfos = null;
		UserDBHelper userDBHelper = new UserDBHelper(mContext, TablesConstant.USER_TABLE);
		try {
			userInfos = userDBHelper.findAllUser();
		} catch (Exception e) {
			if (Constants.LOG) {
				Log.d(Constants.TAG, "getAllUsers", e);
			}
		} finally {
			userDBHelper.closeDB();
		}
		return userInfos;
	}

	@Override
	public long delUser(String email) {
		UserDBHelper userDBHelper = new UserDBHelper(mContext, TablesConstant.USER_TABLE);
		try {
			return userDBHelper.delUserByEmail(email);
		} catch (Exception e) {
			if (Constants.LOG) {
				Log.d(Constants.TAG, "delUser", e);
			}
		} finally {
			userDBHelper.closeDB();
		}
		return 0;
	}

	@Override
	public long insertOrUpdate(UserInfo userInfo) {
		UserDBHelper userDBHelper = new UserDBHelper(mContext, TablesConstant.USER_TABLE);
		try {
			return userDBHelper.insertOrUpdateUser(userInfo);
		} catch (Exception e) {
			if (Constants.LOG) {
				Log.d(Constants.TAG, "insertOrUpdate", e);
			}
		} finally {
			userDBHelper.closeDB();
		}
		return 0;
	}

	@Override
	public UserInfo oldRegister(UserInfo userInfo) {
		Map<String, Object> reqParams = new HashMap<String, Object>();
		reqParams.put("email", userInfo.getEmail());
		reqParams.put("password", userInfo.getPwd());
		reqParams.put("repeatPassword", userInfo.getPwd());
		reqParams.put("name", userInfo.getName());
		reqParams.put("title", userInfo.getTitle());
		reqParams.put("company", userInfo.getCompany());
		reqParams.put("mobile", userInfo.getMobile());
		try {
			return (UserInfo) HttpUtil.doHttpRequest(Constants.Http.REGISTER, reqParams, UserInfo.class, mContext);
		} catch (Exception e) {
			if (Constants.LOG) {
				Log.d(Constants.TAG, "register", e);
			}
		}
		return null;
	}

}
