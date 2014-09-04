package com.itcalf.renhe.po;

import java.io.Serializable;

public class Contact implements Serializable {

	private static final long serialVersionUID = 7392938055673357174L;
	private String id;
	private String email;
	private String name;
	private String job;
	private String company;
	private String contactface;
	private int accountType;//账号vip等级类型：0：普通会员；1：VIP会员；2：黄金会员；3：铂金会员
	private boolean isRealname;//是否是实名认证的会员
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getContactface() {
		return contactface;
	}

	public void setContactface(String contactface) {
		this.contactface = contactface;
	}
	
	public int getAccountType() {
		return accountType;
	}

	public void setAccountType(int accountType) {
		this.accountType = accountType;
	}


	public boolean isRealname() {
		return isRealname;
	}

	public void setRealname(boolean isRealname) {
		this.isRealname = isRealname;
	}

	@Override
	public String toString() {
		return "Contact [company=" + company + ", contactface=" + contactface
				+ ", id=" + id + ", job=" + job + ", name=" + name + "]";
	}

}
