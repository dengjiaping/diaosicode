package com.itcalf.renhe.database.sqlite;

public class TablesConstant {

	/**
	 * 用户表
	 */
	public static final String USER_TABLE = "NEWUSER";
	public static final String USER_TABLE_COLUMN_USERID = "USERID";
	public static final String USER_TABLE_COLUMN_EMAIL = "EMAIL";
	public static final String USER_TABLE_COLUMN_NAME = "NAME";
	public static final String USER_TABLE_COLUMN_PASSWORD = "PASSWORD";
	public static final String USER_TABLE_COLUMN_REMEMBER = "REMEMBER";
	public static final String USER_TABLE_COLUMN_USERFACE = "USERFACE";
	public static final String USER_TABLE_COLUMN_LOGINTIME = "LOGINTIME";
	public static final String USER_TABLE_COLUMN_MOBILE = "MOBILE";
	public static final String USER_TABLE_COLUMN_LOGINACCOUNT = "LOGINACCOUNT";//新增，用来标识用户登录是使用手机还是邮箱

	public static final String SQL_USER = "CREATE TABLE if not exists " + USER_TABLE + "("
			+ USER_TABLE_COLUMN_EMAIL + " TEXT PRIMARY KEY NOT NULL, "
			+ USER_TABLE_COLUMN_USERID + " NUMERIC NOT NULL, "
			+ USER_TABLE_COLUMN_PASSWORD + " TEXT NOT NULL, "
			+ USER_TABLE_COLUMN_NAME + " TEXT NOT NULL, "
			+ USER_TABLE_COLUMN_REMEMBER + " NUMERIC NOT NULL DEFAULT 0, "
			+ USER_TABLE_COLUMN_USERFACE + " TEXT, "
			+ USER_TABLE_COLUMN_LOGINTIME + " TEXT NOT NULL, "
			+ USER_TABLE_COLUMN_MOBILE + " TEXT UNIQUE, "
			+ USER_TABLE_COLUMN_LOGINACCOUNT + " TEXT NOT NULL)";
	/**
	 * 联系人表
	 */
	public static final String CONTACT_TABLE = "CONTACT_ADDVIP";
	public static final String CONTACT_TABLE_COLUMN_ID = "ID";
	public static final String CONTACT_TABLE_COLUMN_NAME = "NAME";
	public static final String CONTACT_TABLE_COLUMN_EMAIL = "EMAIL";
	public static final String CONTACT_TABLE_COLUMN_CONTACTFACE = "CONTACTFACE";
	public static final String CONTACT_TABLE_COLUMN_JOB = "JOB";
	public static final String CONTACT_TABLE_COLUMN_COMPANY = "COMPANY";
	public static final String CONTACT_TABLE_COLUMN_ACCOUNTTYPE = "ACCOUNTTYPE";
	public static final String CONTACT_TABLE_COLUMN_REALNAME= "REALNAME";

	public static final String SQL_CONTACT = "CREATE TABLE if not exists " + CONTACT_TABLE + "("
			+ CONTACT_TABLE_COLUMN_ID + " TEXT PRIMARY KEY, "
			+ CONTACT_TABLE_COLUMN_NAME + " NOT NULL, "
			+ CONTACT_TABLE_COLUMN_EMAIL + " TEXT NOT NULL, "
			+ CONTACT_TABLE_COLUMN_CONTACTFACE + " NULL, "
			+ CONTACT_TABLE_COLUMN_JOB + " TEXT, "
			+ CONTACT_TABLE_COLUMN_COMPANY + " TEXT,"
			+ CONTACT_TABLE_COLUMN_ACCOUNTTYPE + " INTEGER,"
			+ CONTACT_TABLE_COLUMN_REALNAME + " INTEGER)";
			

//	public static final String SETTINGS_TABLE = "USER_SETTINGS";
//	public static final String SETTINGS_TABLE_COLUMN_TONE = "TONE";
//	public static final String SETTINGS_TABLE_COLUMN_PUSHCOMMENT = "PUSHCOMMENT";
//	public static final String SETTINGS_TABLE_COLUMN_PUSHATME = "PUSHATME";
//	public static final String SETTINGS_TABLE_COLUMN_PUSHFANS = "PUSHFANS";
//
//	public static final String SQL_SETTINGS = "CREATE TABLE " + SETTINGS_TABLE
//			+ "(" + USER_TABLE_COLUMN_USERNAME + " TEXT PRIMARY KEY NOT NULL, "
//			+ SETTINGS_TABLE_COLUMN_TONE + " NUMERIC DEFAULT 0, "
//			+ SETTINGS_TABLE_COLUMN_PUSHCOMMENT + " NUMERIC DEFAULT 0, "
//			+ SETTINGS_TABLE_COLUMN_PUSHATME + " NUMERIC DEFAULT 0, "
//			+ SETTINGS_TABLE_COLUMN_PUSHFANS + " NUMERIC DEFAULT 0)";

}
