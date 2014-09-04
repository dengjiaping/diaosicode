package com.itcalf.renhe.dto;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 查看个人档案的接口
 */
public class Profile implements Serializable {

	private static final long serialVersionUID = -5579529941528137456L;
	private int state; // 1 请求成功；-1 权限不足; -2发生未知错误; -3 viewSId 不正确
	private boolean isSelf; // 说明：是否是自己
	private boolean isConnection; // 说明：是否是朋友
	private boolean isFollowing; // 说明：是否已关注
	private boolean  isInvite; //boolean 说明：非好友的情况下10天内是否已经发出过加好友的邀请
	private UserInfo userInfo;

	/**
	 * 
	 * 用户信息
	 * 
	 */
	public static class UserInfo implements Serializable {

		private static final long serialVersionUID = 7023288322044035722L;
		private int followerNum; // 粉丝人数
		private int followingNum; // 关注的人数
		private int messageBoardNum; // 留言数
		private int connectionNum; // 联系人数量
		private int friendDegree; // 朋友关系层数图片的url
		private String name; // 登录成功后会员信息中的姓名
		private String userface; // 登录成功后会员信息中的头像信息 70*70 规格的图片url
		// 例子：http://android.renhe.cn/userface/1.jpg
		private String title; // 登录成功后会员信息中的职务信息
		private String company; // 公司信息
		private String industry; // 公司行业
		private String location; // 公司地址
		private ContactInfo contactInfo; // 联系信息 若没有则不显示相应的项
		private SummaryInfo summaryInfo; // 概要信息
		private EduExperienceInfo[] eduExperienceInfo; // 教育经历
		private WorkExperienceInfo[] workExperienceInfo;// 工作经历
		private OtherInfo otherInfo; // 其他信息
		private int accountType;//账号vip等级类型：0：普通会员；1：VIP会员；2：黄金会员；3：铂金会员
		private boolean isRealName;//是否是实名认证的会员
		private boolean bindMobile ;
		private int id;
		private AimTagInfo[] aimTagInfo;//我想得到
		private PreferredTagInfo[] preferredTagInfo;//我能提供;
		private SpecialtiesInfo[] specialtiesInfo;//我能提供;
		private int gender;//说明：性别，必填，0代表女；1代表男
		private int industryId;//说明：行业，必填
		private int addressId;//说明：地区，必填
		public int getFollowerNum() {
			return followerNum;
		}

		public void setFollowerNum(int followerNum) {
			this.followerNum = followerNum;
		}

		public int getFollowingNum() {
			return followingNum;
		}

		public void setFollowingNum(int followingNum) {
			this.followingNum = followingNum;
		}

		public int getMessageBoardNum() {
			return messageBoardNum;
		}

		public void setMessageBoardNum(int messageBoardNum) {
			this.messageBoardNum = messageBoardNum;
		}

		public int getConnectionNum() {
			return connectionNum;
		}

		public void setConnectionNum(int connectionNum) {
			this.connectionNum = connectionNum;
		}

		public int getFriendDegree() {
			return friendDegree;
		}

		public void setFriendDegree(int friendDegree) {
			this.friendDegree = friendDegree;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getUserface() {
			return userface;
		}

		public void setUserface(String userface) {
			this.userface = userface;
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

		public String getIndustry() {
			return industry;
		}

		public void setIndustry(String industry) {
			this.industry = industry;
		}

		public String getLocation() {
			return location;
		}

		public void setLocation(String location) {
			this.location = location;
		}

		public ContactInfo getContactInfo() {
			return contactInfo;
		}

		public void setContactInfo(ContactInfo contactInfo) {
			this.contactInfo = contactInfo;
		}

		public SummaryInfo getSummaryInfo() {
			return summaryInfo;
		}

		public void setSummaryInfo(SummaryInfo summaryInfo) {
			this.summaryInfo = summaryInfo;
		}

		public WorkExperienceInfo[] getWorkExperienceInfo() {
			return workExperienceInfo;
		}

		public void setWorkExperienceInfo(
				WorkExperienceInfo[] workExperienceInfo) {
			this.workExperienceInfo = workExperienceInfo;
		}

		public EduExperienceInfo[] getEduExperienceInfo() {
			return eduExperienceInfo;
		}

		public void setEduExperienceInfo(EduExperienceInfo[] eduExperienceInfo) {
			this.eduExperienceInfo = eduExperienceInfo;
		}

		public OtherInfo getOtherInfo() {
			return otherInfo;
		}

		public void setOtherInfo(OtherInfo otherInfo) {
			this.otherInfo = otherInfo;
		}

		/**
		 * 
		 * 联系信息 若没有则不显示相应的项
		 * 
		 */
		public static class ContactInfo implements Serializable {

			private static final long serialVersionUID = 5501139945296102859L;
			private String mobile; // 手机
			private String email; // 邮件
			private String tel; // 电话
			private String qq; // qq账号
			private String weixin ; // weixin账号

			public String getMobile() {
				return mobile;
			}

			public void setMobile(String mobile) {
				this.mobile = mobile;
			}

			public String getEmail() {
				return email;
			}

			public void setEmail(String email) {
				this.email = email;
			}

			public String getTel() {
				return tel;
			}

			public void setTel(String tel) {
				this.tel = tel;
			}

			public String getQq() {
				return qq;
			}

			public void setQq(String qq) {
				this.qq = qq;
			}

			public String getWeixin() {
				return weixin;
			}

			public void setWeixin(String weixin) {
				this.weixin = weixin;
			}

			@Override
			public String toString() {
				return "ContactInfo [email=" + email + ", mobile=" + mobile
						+ ", qq=" + qq + ", tel=" + tel + "]";
			}

		}

		/**
		 * 
		 * 概要信息
		 * 
		 */
		public static class SummaryInfo implements Serializable {

			private static final long serialVersionUID = -515369478473389807L;
			private String professional; // 个人简介
			private String specialties; // 个人专长

			public String getProfessional() {
				return professional;
			}

			public void setProfessional(String professional) {
				this.professional = professional;
			}

			public String getSpecialties() {
				return specialties;
			}

			public void setSpecialties(String specialties) {
				this.specialties = specialties;
			}

			@Override
			public String toString() {
				return "SummaryInfo [professional=" + professional
						+ ", specialties=" + specialties + "]";
			}

		}

		/**
		 * 
		 * 工作经历
		 * 
		 */
		public static class WorkExperienceInfo implements Serializable {

			private static final long serialVersionUID = 7280065871059762720L;
			private String title; // 职务
			private String company; // 公司
			private String timeInfo; // 工作时间（服务器端会转成形式如"2011-3到现在"的时间）
			private String content; // 工作经历描述
			private String duringDate;// 这段经历持续的时间

			/**
			 * 以下是档案编辑新增字段
			 * @return
			 */
			private int id;//工作经历id
			private String website;//公司网址
			private String startYear;//格式是“2014”
			private String startMonth;//格式是“8”
			private String endYear;
			private String endMonth;
			private int status;//是否是当前职位，若为1 则代表是当前职位，endYear和endMonth无效，表示目前还是这个岗位；若为0 则代表不是当前职位，endYear和endMonth有效
			private int industry;//公司行业
			private String industryName;
			private int orgtype;//公司性质
			private int orgsize;//公司规模
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

			public String getTimeInfo() {
				return timeInfo;
			}

			public void setTimeInfo(String timeInfo) {
				this.timeInfo = timeInfo;
			}

			public String getContent() {
				return content;
			}

			public void setContent(String content) {
				this.content = content;
			}

			public void setDuringDate(String duringDate) {
				this.duringDate = duringDate;
			}

			public String getDuringDate() {
				return duringDate;
			}

			
			public int getId() {
				return id;
			}

			public void setId(int id) {
				this.id = id;
			}

			public String getWebsite() {
				return website;
			}

			public void setWebsite(String website) {
				this.website = website;
			}

			public String getStartYear() {
				return startYear;
			}

			public void setStartYear(String startYear) {
				this.startYear = startYear;
			}

			public String getStartMonth() {
				return startMonth;
			}

			public void setStartMonth(String startMonth) {
				this.startMonth = startMonth;
			}

			public String getEndYear() {
				return endYear;
			}

			public void setEndYear(String endYear) {
				this.endYear = endYear;
			}

			public String getEndMonth() {
				return endMonth;
			}

			public void setEndMonth(String endMonth) {
				this.endMonth = endMonth;
			}

			public int getStatus() {
				return status;
			}

			public void setStatus(int status) {
				this.status = status;
			}

			public int getIndustry() {
				return industry;
			}

			public void setIndustry(int industry) {
				this.industry = industry;
			}

			public int getOrgtype() {
				return orgtype;
			}

			public void setOrgtype(int orgtype) {
				this.orgtype = orgtype;
			}

			public int getOrgsize() {
				return orgsize;
			}

			public void setOrgsize(int orgsize) {
				this.orgsize = orgsize;
			}
			
			public String getIndustryName() {
				return industryName;
			}

			public void setIndustryName(String industryName) {
				this.industryName = industryName;
			}

			@Override
			public String toString() {
				return "WorkExperienceInfo [company=" + company + ", content="
						+ content + ", duringDate=" + duringDate
						+ ", timeInfo=" + timeInfo + ", title=" + title + "]";
			}

		}

		/**
		 * 
		 * 教育经历
		 * 
		 */
		public static class EduExperienceInfo implements Serializable {

			private static final long serialVersionUID = 7388083681424047751L;
			private String schoolName; // 学校名称
			private String studyField; // 专业名称
			private String timeInfo; // 教育经历时间（服务器端会转成形式如"2011-3到现在"的时间）
			private String content; // 教育经历描述
			private String duringDate;// 这段经历持续的时间
			
			private int id;//工作经历id
			private int schoolId;//说明：学校id，必填
			private String degree;//说明：教育经历学历，必填，长度不能超过50个字符
			private String startYear;//说明：起始工作年份，必填
			private String startMonth;//说明：起始工作月份，必填
			private String endYear;//说明：结束工作年份，
			private String endMonth;//说明：结束工作月份
			private String activities;//说明：社会活动，不是必填，但长度不能超过500个字符
			public String getSchoolName() {
				return schoolName;
			}

			public void setSchoolName(String schoolName) {
				this.schoolName = schoolName;
			}

			public String getStudyField() {
				return studyField;
			}

			public void setStudyField(String studyField) {
				this.studyField = studyField;
			}

			public String getTimeInfo() {
				return timeInfo;
			}

			public void setTimeInfo(String timeInfo) {
				this.timeInfo = timeInfo;
			}

			public String getContent() {
				return content;
			}

			public void setContent(String content) {
				this.content = content;
			}

			public void setDuringDate(String duringDate) {
				this.duringDate = duringDate;
			}

			public String getDuringDate() {
				return duringDate;
			}

			
			public int getSchoolId() {
				return schoolId;
			}

			public void setSchoolId(int schoolId) {
				this.schoolId = schoolId;
			}

			public String getDegree() {
				return degree;
			}

			public void setDegree(String degree) {
				this.degree = degree;
			}

			public String getStartYear() {
				return startYear;
			}

			public void setStartYear(String startYear) {
				this.startYear = startYear;
			}

			public String getStartMonth() {
				return startMonth;
			}

			public void setStartMonth(String startMonth) {
				this.startMonth = startMonth;
			}

			public String getEndYear() {
				return endYear;
			}

			public void setEndYear(String endYear) {
				this.endYear = endYear;
			}

			public String getEndMonth() {
				return endMonth;
			}

			public void setEndMonth(String endMonth) {
				this.endMonth = endMonth;
			}

			public String getActivities() {
				return activities;
			}

			public void setActivities(String activities) {
				this.activities = activities;
			}
			
			public int getId() {
				return id;
			}

			public void setId(int id) {
				this.id = id;
			}

			@Override
			public String toString() {
				return "EduExperienceInfo [content=" + content
						+ ", duringDate=" + duringDate + ", schoolName="
						+ schoolName + ", studyField=" + studyField
						+ ", timeInfo=" + timeInfo + "]";
			}

		}

		/**
		 * 
		 * 其他信息
		 * 
		 */
		public static class OtherInfo implements Serializable {

			private static final long serialVersionUID = -5762958234138335319L;
			private String interests; // 爱好
			private String associations; // 组织
			private String awards; // 荣誉
			private Site[] siteList; // 网站列表需循环输出
			private String webProfileUrl; // 网络档案

			public String getInterests() {
				return interests;
			}

			public void setInterests(String interests) {
				this.interests = interests;
			}

			public String getAssociations() {
				return associations;
			}

			public void setAssociations(String associations) {
				this.associations = associations;
			}

			public String getAwards() {
				return awards;
			}

			public void setAwards(String awards) {
				this.awards = awards;
			}

			public Site[] getSiteList() {
				return siteList;
			}

			public void setSiteList(Site[] siteList) {
				this.siteList = siteList;
			}

			public String getWebProfileUrl() {
				return webProfileUrl;
			}

			public void setWebProfileUrl(String webProfileUrl) {
				this.webProfileUrl = webProfileUrl;
			}

			@Override
			public String toString() {
				return "OtherInfo [associations=" + associations + ", awards="
						+ awards + ", interests=" + interests + ", siteList="
						+ siteList + ", webProfileUrl=" + webProfileUrl + "]";
			}

			public static class Site implements Serializable {

				private static final long serialVersionUID = 7911063827373519720L;
				private String siteType;
				private String siteUrl;

				public String getSiteType() {
					return siteType;
				}

				public void setSiteType(String siteType) {
					this.siteType = siteType;
				}

				public String getSiteUrl() {
					return siteUrl;
				}

				public void setSiteUrl(String siteUrl) {
					this.siteUrl = siteUrl;
				}

				@Override
				public String toString() {
					return "SiteList [siteType=" + siteType + ", siteUrl="
							+ siteUrl + "]";
				}

			}

		}
		public static class AimTagInfo implements Serializable{
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			private String title;

			public String getTitle() {
				return title;
			}

			public void setTitle(String title) {
				this.title = title;
			}
			
		}
		public static class PreferredTagInfo implements Serializable{
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			private String title;

			public String getTitle() {
				return title;
			}

			public void setTitle(String title) {
				this.title = title;
			}
			
		}
		public static class SpecialtiesInfo implements Serializable{
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			private String title;
			
			public String getTitle() {
				return title;
			}
			
			public void setTitle(String title) {
				this.title = title;
			}
			
		}

		@Override
		public String toString() {
			return "UserInfo [company=" + company + ", connectionNum="
					+ connectionNum + ", contactInfo=" + contactInfo
					+ ", eduExperienceInfo="
					+ Arrays.toString(eduExperienceInfo) + ", followerNum="
					+ followerNum + ", followingNum=" + followingNum
					+ ", friendDegree=" + friendDegree + ", industry="
					+ industry + ", location=" + location
					+ ", messageBoardNum=" + messageBoardNum + ", name=" + name
					+ ", otherInfo=" + otherInfo + ", summaryInfo="
					+ summaryInfo + ", title=" + title + ", userface="
					+ userface + ", workExperienceInfo="
					+ Arrays.toString(workExperienceInfo) + "]";
		}

		public int getAccountType() {
			return accountType;
		}

		public void setAccountType(int accountType) {
			this.accountType = accountType;
		}

		public boolean isRealName() {
			return isRealName;
		}

		public void setRealName(boolean isRealName) {
			this.isRealName = isRealName;
		}

		public boolean isBindMobile() {
			return bindMobile;
		}

		public void setBindMobile(boolean bindMobile) {
			this.bindMobile = bindMobile;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public AimTagInfo[] getAimTagInfo() {
			return aimTagInfo;
		}

		public void setAimTagInfo(AimTagInfo[] aimTagInfo) {
			this.aimTagInfo = aimTagInfo;
		}

		public PreferredTagInfo[] getPreferredTagInfo() {
			return preferredTagInfo;
		}

		public void setPreferredTagInfo(PreferredTagInfo[] preferredTagInfo) {
			this.preferredTagInfo = preferredTagInfo;
		}

		public SpecialtiesInfo[] getSpecialtiesInfo() {
			return specialtiesInfo;
		}

		public void setSpecialtiesInfo(SpecialtiesInfo[] specialtiesInfo) {
			this.specialtiesInfo = specialtiesInfo;
		}

		public int getGender() {
			return gender;
		}

		public void setGender(int gender) {
			this.gender = gender;
		}

		public int getIndustryId() {
			return industryId;
		}

		public void setIndustryId(int industryId) {
			this.industryId = industryId;
		}

		public int getAddressId() {
			return addressId;
		}

		public void setAddressId(int addressId) {
			this.addressId = addressId;
		}
		
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public boolean isSelf() {
		return isSelf;
	}

	public void setSelf(boolean isSelf) {
		this.isSelf = isSelf;
	}

	public boolean isConnection() {
		return isConnection;
	}

	public void setConnection(boolean isConnection) {
		this.isConnection = isConnection;
	}

	public boolean isFollowing() {
		return isFollowing;
	}

	public void setFollowing(boolean isFollowing) {
		this.isFollowing = isFollowing;
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}
	
	public boolean isInvite() {
		return isInvite;
	}

	public void setInvite(boolean isInvite) {
		this.isInvite = isInvite;
	}

	@Override
	public String toString() {
		return "Profile [isConnection=" + isConnection + ", isFollowing="
				+ isFollowing + ", isSelf=" + isSelf + ", state=" + state
				+ ", userInfo=" + userInfo + "]";
	}
	
}
