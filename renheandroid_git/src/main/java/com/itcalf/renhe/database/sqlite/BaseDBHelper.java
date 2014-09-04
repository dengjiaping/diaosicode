package com.itcalf.renhe.database.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 数据库操作抽象类
 * 
 * @author piers.xie
 */
public abstract class BaseDBHelper {

	protected DBHelper mDBHelper;
	protected SQLiteDatabase mSQLiteWriter;
	protected SQLiteDatabase mSQLiteReader;
	protected String mTable;

	public BaseDBHelper() {
		super();
	}

	public BaseDBHelper(Context context, String table) {
		super();
		this.mDBHelper = new DBHelper(context);
		this.mTable = table;
	}

	protected DBHelper getDBHelper() {
		return mDBHelper;
	}

	protected String getTable() {
		return mTable;
	}

	protected void setTable(String mTable) {
		this.mTable = mTable;
	}
	
	/**
	 * 查询表所有记录
	 * @return
	 */
	public synchronized Cursor findAll() {
		if (null == mSQLiteReader)
			mSQLiteReader = mDBHelper.getReadableDatabase();
		return mSQLiteReader.query(mTable, null, null, null, null, null, null);
	}
	
	/**
	 * 查询表记录
	 * @param columns
	 * @param selection
	 * @param selectionArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @return
	 */
	public synchronized Cursor find(String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
		if (null == mSQLiteReader)
			mSQLiteReader = mDBHelper.getReadableDatabase();
		return mSQLiteReader.query(mTable, columns, selection, selectionArgs, groupBy, having, orderBy);
	}

	/**
	 * 插入一条记录到数据库
	 * 
	 * @return
	 */
	public synchronized long insertData(ContentValues values) {
		if (null == mSQLiteWriter)
			mSQLiteWriter = mDBHelper.getWritableDatabase();
		if (null != mTable && null != values) {
			return mSQLiteWriter.insert(mTable, null, values);
		} else {
			return -1;
		}
	}

	/**
	 * 更新一条记录到数据库
	 * 
	 * @return
	 */
	public synchronized long updateData(ContentValues values, String whereClaus, String[] whereArgs) {
		if (null == mSQLiteWriter) {
			mSQLiteWriter = mDBHelper.getWritableDatabase();
		}
		if (null != mTable && null != values) {
			return mSQLiteWriter.update(mTable, values, whereClaus, whereArgs);
		} else {
			return -1;
		}
	}

	/**
	 * 删除一条记录到数据库
	 * 
	 * @return
	 */
	public synchronized long delData(String whereClaus, String[] whereArgs) {
		if (null == mSQLiteWriter) {
			mSQLiteWriter = mDBHelper.getWritableDatabase();
		}
		if (null != mTable) {
			return mSQLiteWriter.delete(mTable, whereClaus, whereArgs);
		} else {
			return -1;
		}
	}

	/**
	 * 判断是否存在ID的记录
	 * 
	 * @return
	 */
	public synchronized boolean isExist(String pKColumn, String pkValue) {
		boolean isExist = false;
		if (null == mSQLiteReader)
			mSQLiteReader = mDBHelper.getReadableDatabase();
		Cursor lCursor = mSQLiteReader.query(mTable, new String[] { pKColumn },
				pKColumn + "=?", new String[] { pkValue }, null, null, null);
		if (null != lCursor && lCursor.moveToFirst()) {
			isExist = true;
		}
		lCursor.close();
		return isExist;
	}

	/**
	 * 关闭数据库
	 */
	public synchronized void closeDB() {
		if (null != mSQLiteWriter) {
			mSQLiteWriter.close();
		}
		if (null != mSQLiteReader) {
			mSQLiteReader.close();
		}
		mDBHelper.close();
	}

}
