package com.itcalf.renhe.dto;

import java.io.Serializable;
import java.util.Arrays;

public class ContactList implements Serializable {

	private static final long serialVersionUID = -1187223497234726128L;
	private int state;
	private Member[] memberList;

	public static class Member implements Serializable {

		private static final long serialVersionUID = 8067421929445489899L;
		private String sid;
		private String name;
		private String title;
		private String company;
		private String userface;
		private int accountType;//账号vip等级类型：0：普通会员；1：VIP会员；2：黄金会员；3：铂金会员
		private boolean isRealname;//是否是实名认证的会员
		public String getSid() {
			return sid;
		}

		public void setSid(String sid) {
			this.sid = sid;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getCompany() {
			return company;
		}

		public void setCompany(String company) {
			this.company = company;
		}

		public String getUserface() {
			return userface;
		}

		public void setUserface(String userface) {
			this.userface = userface;
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
			return "Member [sid=" + sid + ", name=" + name + ", title=" + title
					+ ", company=" + company + ", userface=" + userface + "]";
		}

	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public Member[] getMemberList() {
		return memberList;
	}

	public void setMemberList(Member[] memberList) {
		this.memberList = memberList;
	}

	@Override
	public String toString() {
		return "Contact [state=" + state + ", memberList="
				+ Arrays.toString(memberList) + "]";
	}

}
