package com.itcalf.renhe.context.relationship;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.itcalf.renhe.context.archives.edit.EditEduInfoSelectSchool;
import com.itcalf.renhe.dto.SearchCity;

public class AdvanceSearchUtil {
	
	/**
	 * 澶嶅埗assert鐩綍涓嬫枃浠跺埌鎸囧畾鐩綍涓嬮潰
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
	
//	public static Map<String, Integer>  getProvince(SQLiteDatabase db, String tableName,boolean isChina){
//		Map<String, Integer> provinceMap = new LinkedHashMap<String, Integer>();
//		Cursor cursor = null;
//		if(isChina){
//			cursor = db.query(tableName, new String[]{"id","name"}, "type=? AND super_id =?", new String[]{"province","10001"}, null, null, "id ASC");
//		}else{
//			cursor = db.query(tableName, new String[]{"id","name"}, "type=? AND super_id =? AND id != 10001", new String[]{"country","0"}, null, null, "id ASC");
//		}
//		if (cursor != null ) {
////			if(isChina){
////				provinceMap.put("鍏ㄩ儴", WheelActivity.ALL_CHINA);
////			}else{
////				provinceMap.put("鍏ㄩ儴", WheelActivity.ALL_FORGIGN);
////			}
//			provinceMap.put("鍏ㄩ儴", WheelActivity.ALL_AREA);
//			provinceMap.put("娴峰", WheelActivity.ALL_FORGIGN);
//			provinceMap.put("鍥藉唴", WheelActivity.ALL_CHINA);
//			while(cursor.moveToNext()) {
//				provinceMap.put(cursor.getString(1), cursor.getInt(0));
//			}
//		}
//		if(cursor != null && !cursor.isClosed()){
//			cursor.close();
//		}
//		return provinceMap;
//	}
	
	/**
	 * 
	 * @param db
	 * @param tableName
	 * @return
	 */
	public static SearchCity[] getCity(SQLiteDatabase db, String tableName) {
		SearchCity[] cityArrays = null;
		Cursor cursor = db.query(tableName, new String[]{"id", "name"}, "type=?", new String[]{"city"}, null, null, "id ASC");
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				cityArrays = new SearchCity[cursor.getCount()];
				do {
					SearchCity searchCity = new SearchCity();
					searchCity.setId(cursor.getInt(0));
					searchCity.setName(cursor.getString(1));
					cityArrays[cursor.getPosition()] = searchCity;
				} while (cursor.moveToNext());
			}
			cursor.close();
		}
		return cityArrays;
	}
	public static SearchCity[] getForeignCity(SQLiteDatabase db, String tableName) {
		SearchCity[] cityArrays = null;
		Cursor cursor = db.query(tableName, new String[]{"id", "name"}, "type=?", new String[]{"country"}, null, null, "id ASC");
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				cityArrays = new SearchCity[cursor.getCount()];
				do {
					SearchCity searchCity = new SearchCity();
					searchCity.setId(cursor.getInt(0));
					searchCity.setName(cursor.getString(1));
					cityArrays[cursor.getPosition()] = searchCity;
				} while (cursor.moveToNext());
			}
			cursor.close();
		}
		return cityArrays;
	}
	public static SearchCity getCurrentCity(SQLiteDatabase db, String tableName,String cityName){
		SearchCity city = null;
		Cursor cursor = db.query(tableName, new String[]{"id"}, "type=? AND name=?", new String[]{"city",cityName}, null, null, "id ASC");
		if (cursor != null) {
			if (cursor.getCount() > 0 && cursor.moveToFirst()) {
				city = new SearchCity();
				city.setId(cursor.getInt(0));
				city.setName(cityName);
			}
			cursor.close();
		}
		return city;
	}
	public static SearchCity[]  getIndustry(SQLiteDatabase db, String tableName){
		Cursor cursor = db.query(tableName, new String[]{"id","name"}, "super_category_id =?", new String[]{"0"}, null, null, "order_id DESC");
		SearchCity[] cityArrays = null;
		if (cursor != null) {
		if (cursor.moveToFirst()) {
			cityArrays = new SearchCity[cursor.getCount()];
			do {
//				if(!cursor.getString(1).equals("其它")){
					SearchCity searchCity = new SearchCity();
					searchCity.setId(cursor.getInt(0));
					searchCity.setName(cursor.getString(1));
					cityArrays[cursor.getPosition()] = searchCity;
//				}
			} while (cursor.moveToNext());
		}
			cursor.close();
		}
		return cityArrays;
	}
	/**
	 * 
	 * @param db
	 * @param tableName
	 * @param dqx_dqxx01
	 * @param Municipalities  
	 * @return
	 */
	public static SearchCity[] getChildIndustry(SQLiteDatabase db, String tableName, int super_id,String super_name) {
		Cursor cursor = db.query(tableName, new String[]{"id", "name"}, "super_category_id=?", new String[]{""+super_id}, null, null, "order_id ASC");
		SearchCity[] cityArrays = null;
		if (cursor != null) {
		if (cursor.moveToFirst()) {
			cityArrays = new SearchCity[cursor.getCount()];
			do {
//				if(!cursor.getString(1).equals("其它")){
				if(!cursor.getString(1).equals(super_name) || cursor.getString(1).equals("其它")){
					SearchCity searchCity = new SearchCity();
					searchCity.setId(cursor.getInt(0));
					searchCity.setName(cursor.getString(1));
					cityArrays[cursor.getPosition()] = searchCity;
				}
			} while (cursor.moveToNext());
		}
			cursor.close();
		}
		return cityArrays;
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
	/**
	 * 获取大学
	 * @param db
	 * @param tableName
	 * @return
	 */
	public static List<Map<String, Object>> getSchool(SQLiteDatabase db, String tableName,String key) {
		if(db != null){
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			Cursor cursor = db.query(tableName, new String[]{"id", "name"}, "name LIKE ?", new String[]{key+"%"}, null, null, "id ASC");
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					do {
						SearchCity searchCity = new SearchCity();
						searchCity.setId(cursor.getInt(0));
						searchCity.setName(cursor.getString(1));
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("name", cursor.getString(1));
						map.put("id", cursor.getInt(0));
						list.add(map);
					} while (cursor.moveToNext());
				}
				cursor.close();
			}
			return list;
		}
		return null;
	}
	public static int getSchoolId(SQLiteDatabase db, String tableName,String key) {
		if(db != null){
			Cursor cursor = db.query(tableName, new String[]{"id"}, "name =?", new String[]{key}, null, null, "id ASC");
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					do {
						return cursor.getInt(0);
					} while (cursor.moveToNext());
				}
				cursor.close();
			}
		}
		return EditEduInfoSelectSchool.NO_THIS_SCHOOL;
	}
}
