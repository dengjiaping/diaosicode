package com.itcalf.renhe.database.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;

import com.itcalf.renhe.po.Contact;

public class ContactDBHelper extends BaseDBHelper {

	public ContactDBHelper(Context context, String table) {
		super(context, table);
	}

	/**
	 * 获取所有联系人
	 * 
	 * @return
	 */
	public synchronized Contact[] findAllContact(String email) {
		Cursor cursor = find(null, TablesConstant.CONTACT_TABLE_COLUMN_EMAIL + "=?", new String[] { email }, null, null,
				TablesConstant.CONTACT_TABLE_COLUMN_NAME + " DESC");
		Contact[] contacts = null;
		if (null != cursor) {
			if (cursor.moveToFirst()) {
				contacts = new Contact[cursor.getCount()];
				do {
					Contact contact = new Contact();
					contentValueToPo(cursor, contact);
					contacts[cursor.getPosition()] = contact;
				} while (cursor.moveToNext());
			}
			cursor.close();
		}
		return contacts;
	}

	/**
	 * 插入一个用户账户
	 * 
	 * @param user
	 * @return
	 */
	public synchronized long insertOrUpdateUser(Contact contact) {
		ContentValues values = poToContentValues(contact);
		if (isExist(TablesConstant.CONTACT_TABLE_COLUMN_ID, contact.getId())) {
			return updateContact(contact);
		} else {
			return insertData(values);
		}
	}

	/**
	 * 根据用户名更新用户账户
	 * 
	 * @param user
	 * @return
	 */
	public synchronized long updateContact(Contact contact) {
		ContentValues values = poToContentValues(contact);
		return updateData(values, TablesConstant.CONTACT_TABLE_COLUMN_ID + "=?", new String[] { contact.getId() });
	}

	/**
	 * 根据用户名删除用户账户
	 * 
	 * @param email
	 * @return
	 */
	public synchronized long delContactByEmail(String email) {
		return delData(TablesConstant.CONTACT_TABLE_COLUMN_EMAIL + "=?", new String[] { email });
	}

	/**
	 * 删除所有数据
	 * 
	 * @return
	 */
	public synchronized long delAll() {
		return delData(null, null);
	}

	public ContentValues poToContentValues(Contact contact) {
		ContentValues values = new ContentValues();
		values.put(TablesConstant.CONTACT_TABLE_COLUMN_ID, contact.getId());
		values.put(TablesConstant.CONTACT_TABLE_COLUMN_EMAIL, contact.getEmail());
		values.put(TablesConstant.CONTACT_TABLE_COLUMN_NAME, contact.getName());
		if (null != contact.getJob()) {
			values.put(TablesConstant.CONTACT_TABLE_COLUMN_JOB, contact.getJob());
		}
		if (null != contact.getCompany()) {
			values.put(TablesConstant.CONTACT_TABLE_COLUMN_COMPANY, contact.getCompany());
		}
		if (null != contact.getContactface()) {
			values.put(TablesConstant.CONTACT_TABLE_COLUMN_CONTACTFACE, contact.getContactface());
		}
		values.put(TablesConstant.CONTACT_TABLE_COLUMN_ACCOUNTTYPE, contact.getAccountType());
		if (contact.isRealname()) {
			values.put(TablesConstant.CONTACT_TABLE_COLUMN_REALNAME, true);
		} else {
			values.put(TablesConstant.CONTACT_TABLE_COLUMN_REALNAME, false);
		}
		return values;
	}

	public void contentValueToPo(Cursor cursor, Contact contact) {
		ContentValues values = new ContentValues();
		DatabaseUtils.cursorRowToContentValues(cursor, values);
		contact.setId(values.getAsString(TablesConstant.CONTACT_TABLE_COLUMN_ID));
		contact.setEmail(values.getAsString(TablesConstant.CONTACT_TABLE_COLUMN_EMAIL));
		contact.setName(values.getAsString(TablesConstant.CONTACT_TABLE_COLUMN_NAME));
		contact.setJob(values.getAsString(TablesConstant.CONTACT_TABLE_COLUMN_JOB));
		contact.setCompany(values.getAsString(TablesConstant.CONTACT_TABLE_COLUMN_COMPANY));
		contact.setContactface(values.getAsString(TablesConstant.CONTACT_TABLE_COLUMN_CONTACTFACE));
		contact.setAccountType(values.getAsInteger(TablesConstant.CONTACT_TABLE_COLUMN_ACCOUNTTYPE));
		if (values.getAsInteger(TablesConstant.CONTACT_TABLE_COLUMN_REALNAME) == 1) {
			contact.setRealname(true);
		} else {
			contact.setRealname(false);
		}
	}

}
