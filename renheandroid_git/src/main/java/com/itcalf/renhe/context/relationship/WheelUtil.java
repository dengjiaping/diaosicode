package com.itcalf.renhe.context.relationship;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.tech.IsoDep;
import android.os.Environment;

public class WheelUtil {
	
	/**
	 * 复制assert目录下文件到指定目录下面
	 * @param context
	 * @param fileName
	 * @throws IOException
	 */
	public static void copyDB(Context context, String fileName) throws IOException {
		
		String filePath = Environment.getExternalStorageDirectory()+File.separator+"Android"+File.separator+"com.renheandroidnew"+File.separator;
		if (new File(filePath+fileName).exists()) {
			return;
		}
		File file = new File(filePath);
		if (!file.isDirectory()) {
			file.mkdirs();
		}
		FileOutputStream fos = new FileOutputStream(filePath+fileName);
		InputStream is = context.getResources().getAssets().open(fileName);
		byte[] buffer = new byte[1024*5];
		int count = 0;
		while((count = is.read(buffer)) > 0){
			fos.write(buffer, 0, count);
		}
		fos.close();
		is.close();
	}
	
	public static Map<String, Integer>  getProvince(SQLiteDatabase db, String tableName,boolean isChina){
		Map<String, Integer> provinceMap = new LinkedHashMap<String, Integer>();
		Cursor cursor = null;
		if(isChina){
			cursor = db.query(tableName, new String[]{"id","name"}, "type=? AND super_id =?", new String[]{"province","10001"}, null, null, "id ASC");
		}else{
			cursor = db.query(tableName, new String[]{"id","name"}, "type=? AND super_id =? AND id != 10001", new String[]{"country","0"}, null, null, "id ASC");
		}
		if (cursor != null ) {
//			if(isChina){
//				provinceMap.put("全部", WheelActivity.ALL_CHINA);
//			}else{
//				provinceMap.put("全部", WheelActivity.ALL_FORGIGN);
//			}
			provinceMap.put("全部", WheelActivity.ALL_AREA);
			provinceMap.put("海外", WheelActivity.ALL_FORGIGN);
			provinceMap.put("国内", WheelActivity.ALL_CHINA);
			while(cursor.moveToNext()) {
				provinceMap.put(cursor.getString(1), cursor.getInt(0));
			}
		}
		if(cursor != null && !cursor.isClosed()){
			cursor.close();
		}
		return provinceMap;
	}
	
	/**
	 * 
	 * @param db
	 * @param tableName
	 * @param dqx_dqxx01
	 * @param Municipalities  是否是直辖市
	 * @return
	 */
	public static Map<String, Integer> getCity(SQLiteDatabase db, String tableName, int super_id, boolean municipalities) {
		Map<String, Integer> cityMap = new LinkedHashMap<String, Integer>();
		Cursor cursor = db.query(tableName, new String[]{"id", "name"}, "super_id=?", new String[]{""+super_id}, null, null, "id ASC");
		if (cursor != null) {
//			if (municipalities) {
//				cursor.moveToNext();
//			}
//			if(super_id == WheelActivity.ALL_AREA){
//				cityMap.put("全球", super_id);
//			}
//			if(super_id == WheelActivity.ALL_CHINA){
//				cityMap.put("国内", super_id);
//			}
//			if(super_id == WheelActivity.ALL_FORGIGN){
//				cityMap.put("海外", super_id);
//			}
			cityMap.put("全部", super_id);
			while(cursor.moveToNext()) {
				cityMap.put(cursor.getString(1), cursor.getInt(0));
			}
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return cityMap;
	}
	
//	public static Map<String, Integer> getArea(SQLiteDatabase db, String tableName, int dqx_dqxx01) {
//		Map<String, Integer> areaMap = new LinkedHashMap<String, Integer>();
//		Cursor cursor = db.query(tableName, new String[]{"DQXX02", "DQXX01"}, "DQX_DQXX01=?", new String[]{""+dqx_dqxx01}, null, null, "DQXX01 ASC");
//		if (cursor != null ) {
//			while(cursor.moveToNext()) {
//				areaMap.put(cursor.getString(0), cursor.getInt(1));
//			}
//		}
//		if (cursor != null && !cursor.isClosed()) {
//			cursor.close();
//		}
//		return areaMap;
//	}
	public static Map<String, Integer>  getIndustry(SQLiteDatabase db, String tableName){
		Map<String, Integer> provinceMap = new LinkedHashMap<String, Integer>();
		Cursor cursor = db.query(tableName, new String[]{"id","name"}, "super_category_id =?", new String[]{"0"}, null, null, "id ASC");
		if (cursor != null ) {
			provinceMap.put("全部", WheelActivity.ALL_INDUSTRY);
			while(cursor.moveToNext()) {
				if(cursor.getInt(0) != 0){
					provinceMap.put(cursor.getString(1), cursor.getInt(0));
				}else{
					provinceMap.put(cursor.getString(1), 1);
				}
			}
		}
		if(cursor != null && !cursor.isClosed()){
			cursor.close();
		}
		return provinceMap;
	}
	/**
	 * 
	 * @param db
	 * @param tableName
	 * @param dqx_dqxx01
	 * @param Municipalities  
	 * @return
	 */
	public static Map<String, Integer> getChildIndustry(SQLiteDatabase db, String tableName, int super_id, boolean municipalities) {
		Map<String, Integer> cityMap = new LinkedHashMap<String, Integer>();
		Cursor cursor = db.query(tableName, new String[]{"id", "name"}, "super_category_id=?", new String[]{""+super_id}, null, null, "id ASC");
		if (cursor != null) {
//			if (municipalities) {
//				cursor.moveToNext();
//			}
			cityMap.put("全部", super_id);
			while(cursor.moveToNext()) {
				cityMap.put(cursor.getString(1), cursor.getInt(0));
			}
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return cityMap;
	}
	public static int findPrimaryKey(SQLiteDatabase db, String tableName, String address) {
		int key = -1;
		Cursor cursor = db.query(tableName, new String[]{"DQXX01"}, "DQXX05=?", new String[]{address}, null, null, null);
		if (cursor != null) {
			if (cursor.moveToNext()) {
				key = cursor.getInt(0);
			}
		}
		return key;
	}
}
