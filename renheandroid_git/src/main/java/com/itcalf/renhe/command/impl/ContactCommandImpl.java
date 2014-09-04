package com.itcalf.renhe.command.impl;

import java.util.HashMap;
import java.util.Map;

import android.app.Application;

import com.itcalf.renhe.Constants;
import com.itcalf.renhe.command.IContactCommand;
import com.itcalf.renhe.database.sqlite.ContactDBHelper;
import com.itcalf.renhe.database.sqlite.TablesConstant;
import com.itcalf.renhe.dto.ContactList;
import com.itcalf.renhe.dto.ContactList.Member;
import com.itcalf.renhe.po.Contact;
import com.itcalf.renhe.utils.HttpUtil;

public class ContactCommandImpl implements IContactCommand {

	private Application mRenheApplication;

	public ContactCommandImpl(Application application) {
		super();
		mRenheApplication = application;
	}

	@Override
	public ContactList getContactList(String viewSId, String sid, String adSId)
			throws Exception {
		Map<String, Object> reqParams = new HashMap<String, Object>();
		reqParams.put("viewSId", viewSId);
		reqParams.put("sid", sid);
		reqParams.put("adSId", adSId);
		return (ContactList) HttpUtil.doHttpRequest(Constants.Http.CONTACTLIST,
				reqParams, ContactList.class,mRenheApplication);
	}

	@Override
	public long saveContactList(Member[] ml, String email) throws Exception {
		ContactDBHelper ctDBHelper = new ContactDBHelper(
				mRenheApplication.getApplicationContext(),
				TablesConstant.CONTACT_TABLE);
		long count = 0;
		try {
			for (int i = 0; i < ml.length; i++) {
				Contact ct = new Contact();
				ct.setId(ml[i].getSid());
				ct.setName(ml[i].getName());
				ct.setJob(ml[i].getTitle());
				ct.setCompany(ml[i].getCompany());
				ct.setContactface(ml[i].getUserface());
				ct.setEmail(email);
				ct.setAccountType(ml[i].getAccountType());
				ct.setRealname(ml[i].isRealname());
				count += ctDBHelper.insertOrUpdateUser(ct);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			ctDBHelper.closeDB();
		}
		return count;
	}

	@Override
	public Contact[] getAllContact(String email) throws Exception {
		ContactDBHelper ctDBHelper = new ContactDBHelper(
				mRenheApplication.getApplicationContext(),
				TablesConstant.CONTACT_TABLE);
		try {
			return ctDBHelper.findAllContact(email);
		} catch (Exception e) {
			throw e;
		} finally {
			ctDBHelper.closeDB();
		}
	}

}
