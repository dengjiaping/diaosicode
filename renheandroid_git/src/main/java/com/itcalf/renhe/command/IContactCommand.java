package com.itcalf.renhe.command;

import android.content.Context;

import com.itcalf.renhe.dto.ContactList;
import com.itcalf.renhe.dto.ContactList.Member;
import com.itcalf.renhe.po.Contact;

/**
 * 联系人接口
 * 
 * @author xp
 * 
 */
public interface IContactCommand {

	/**
	 * 获取联系人列表
	 * 
	 * @return
	 * @throws Exception
	 */
	ContactList getContactList(String viewSId, String sid, String adSId) throws Exception;

	/**
	 * 同步联系人
	 * 
	 * @param ml
	 * @param email
	 * @return
	 * @throws Exception
	 */
	long saveContactList(Member[] ml, String email) throws Exception;

	/**
	 * 从数据库中获取所有联系人列表
	 * 
	 * @param email
	 * @return
	 * @throws Exception 
	 */
	Contact[] getAllContact(String email) throws Exception;

}
