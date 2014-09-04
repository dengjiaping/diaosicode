package com.itcalf.renhe.database.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "renhe.db";
	private static final int DATABASE_VERSION = 1;

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}
	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		db.execSQL(TablesConstant.SQL_CONTACT);
		db.execSQL(TablesConstant.SQL_USER);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TablesConstant.SQL_USER);
		db.execSQL(TablesConstant.SQL_CONTACT);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS "
				+ TablesConstant.USER_TABLE);
		db.execSQL("DROP TABLE IF EXISTS "
				+ TablesConstant.SQL_CONTACT);
		onCreate(db);
	}
}
