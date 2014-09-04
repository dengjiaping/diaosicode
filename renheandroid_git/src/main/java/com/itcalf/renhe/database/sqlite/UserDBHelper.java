package com.itcalf.renhe.database.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;

import com.itcalf.renhe.dto.UserInfo;

public class UserDBHelper extends BaseDBHelper {

	public UserDBHelper(Context context, String table) {
		super(context, table);
	}

	/**
	 * 获取所有用户账户
	 * @return
	 */
	public synchronized UserInfo[] findAllUser() {
		Cursor cursor = find(null, null, null, null, null, TablesConstant.USER_TABLE_COLUMN_LOGINTIME + " DESC");
		UserInfo[] userInfos = null;
		if(null != cursor) {
			if(cursor.moveToFirst()) {
				userInfos = new UserInfo[cursor.getCount()];
				do {
					UserInfo userInfo = new UserInfo();
					contentValueToPo(cursor, userInfo);
					userInfos[cursor.getPosition()] = userInfo;
				}while(cursor.moveToNext());
			}
			cursor.close();
		}
		return userInfos;
	}
//
	/**
	 * 插入一个用户账户
	 * @param user
	 * @return
	 */
	public synchronized long insertOrUpdateUser(UserInfo userInfo) {
		ContentValues values = poToContentValues(userInfo);
		if(isExist(TablesConstant.USER_TABLE_COLUMN_EMAIL, userInfo.getEmail())) {
			return updateUser(userInfo);
		}else {
			return insertData(values);
		}
	}

	/**
	 * 根据用户名更新用户账户
	 * @param user
	 * @return
	 */
	public synchronized long updateUser(UserInfo userInfo) {
		ContentValues values = poToContentValues(userInfo);
		return updateData(values, TablesConstant.USER_TABLE_COLUMN_EMAIL + "=?", new String[]{userInfo.getEmail()});
	}
	
	/**
	 * 根据用户名删除用户账户
	 * @param email
	 * @return
	 */
	public synchronized long delUserByEmail(String email) {
		return delData(TablesConstant.USER_TABLE_COLUMN_EMAIL + "=?", new String[]{email});
	}
	
	public ContentValues poToContentValues(UserInfo userInfo) {
		ContentValues values = new ContentValues();
		values.put(TablesConstant.USER_TABLE_COLUMN_EMAIL, userInfo.getEmail());
		if(null != userInfo.getPwd()) {
			values.put(TablesConstant.USER_TABLE_COLUMN_PASSWORD, userInfo.getPwd());
		}
		if(null != userInfo.getId()) {
			values.put(TablesConstant.USER_TABLE_COLUMN_USERID, userInfo.getId());
		}
		if(null != userInfo.getName()) {
			values.put(TablesConstant.USER_TABLE_COLUMN_NAME, userInfo.getName());
		}
		if(null != userInfo.getLogintime()) {
			values.put(TablesConstant.USER_TABLE_COLUMN_LOGINTIME, userInfo.getLogintime());
		}
		values.put(TablesConstant.USER_TABLE_COLUMN_REMEMBER, userInfo.isRemember());
		if(null != userInfo.getUserface()) {
			values.put(TablesConstant.USER_TABLE_COLUMN_USERFACE, userInfo.getUserface());
		}
		if(null != userInfo.getMobile()) {
			values.put(TablesConstant.USER_TABLE_COLUMN_MOBILE, userInfo.getMobile());
		}
		if(null != userInfo.getAccountType()) {
			values.put(TablesConstant.USER_TABLE_COLUMN_LOGINACCOUNT, userInfo.getAccountType());
		}
		return values;
	}
	
	public void contentValueToPo(Cursor cursor, UserInfo userInfo) {
		ContentValues values = new ContentValues();
		DatabaseUtils.cursorRowToContentValues(cursor, values);
		userInfo.setId(values.getAsLong(TablesConstant.USER_TABLE_COLUMN_USERID));
		userInfo.setName(values.getAsString(TablesConstant.USER_TABLE_COLUMN_NAME));
		userInfo.setPwd(values.getAsString(TablesConstant.USER_TABLE_COLUMN_PASSWORD));
		userInfo.setEmail(values.getAsString(TablesConstant.USER_TABLE_COLUMN_EMAIL));
		userInfo.setLogintime(values.getAsString(TablesConstant.USER_TABLE_COLUMN_LOGINTIME));
		userInfo.setRemember(values.getAsBoolean(TablesConstant.USER_TABLE_COLUMN_REMEMBER));
		userInfo.setUserface(values.getAsString(TablesConstant.USER_TABLE_COLUMN_USERFACE));
		userInfo.setAccountType(values.getAsString(TablesConstant.USER_TABLE_COLUMN_LOGINACCOUNT));
	}
	
}
