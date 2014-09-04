package com.itcalf.renhe.dto;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 
 * 1,查看自己客厅留言接口(自己发布的客厅留言及自己关注的客厅留言) 2,查看朋友客厅的留言接口 3,查看同城的留言接口 4,查看同行的留言接口
 * 5,最受关注的留言接口
 */
public class MessageBoards implements Serializable {

	private static final long serialVersionUID = -6501243574466513990L;
	private int state; // 说明：1 请求成功；-1 权限不足; -2发生未知错误;
	private MessageBoardList[] messageBoardList; // 客厅留言列表 被转发的数量现在人和网暂时没这个功能
	private long minCreatedDate;
	private long maxCreatedDate;
	private boolean aboveMaxCount;

	/**
	 * 
	 * 客厅留言列表 被转发的数量现在人和网暂时没这个功能
	 * 
	 */
	public static class MessageBoardList implements Serializable {

		private static final long serialVersionUID = -9126901235211751204L;
		private int id; // mysql中的id
		private String objectId; // mongoDB中的id
		private String senderSid; // 留言者加密后的id
		private String senderName; // 留言者姓名
		private String senderUserFace; // 留言者头像信息
		private String messageBoardContent; // 留言内容
		private MessageBoardMember[] atMembers;//留言中@ 列表
		private String thumbnailPic;// 留言正文的图片s
		private String bmiddlePic;// 留言正文的图片
		private String forwardThumbnailPic;//  留言转发的图片，只可能在有转发的时候存在
		private String forwardBmiddlePic;//  留言转发的图片，只可能在有转发的时候存在
		private String fromSource; // 留言来自于哪里 来自网页客户端或来自Android客户端
		private String createdDate; // 格式为：6-28 11:12
		private String forwardMessageBoardContent; // 留言转发的内容
		private MessageBoardMember[] forwardMessageBoardAtMembers ; // 留言转发的id
		private int replyNum; // 被回复数量
		private int likedNum;//赞的个数
		private boolean liked;//该用户是否点过赞

		private String senderTitle; // 发布者的职务
		private String senderCompany; // 发布者的公司名
		private String senderIndustry; // 发布者的公司行业
		private String senderLocation; // 发布者的公司地址
		private int senderAccountType;//账号vip等级类型：0：普通会员；1：VIP会员；2：黄金会员；3：铂金会员
		private boolean senderIsRealname;//是否是实名认证的会员
		
		private boolean isForwardRenhe;
		private String forwardMemberName;//被转发客厅的会员姓名
		private String forwardMemberSId;//被转发客厅的会员sid
		private String forwardMessageBoardObjectId;//被转发客厅的objectId
		private String forwardMessageBoardId;//被转发客厅的id
		public String getBmiddlePic() {
			return bmiddlePic;
		}

		public void setBmiddlePic(String bmiddlePic) {
			this.bmiddlePic = bmiddlePic;
		}

		public String getForwardBmiddlePic() {
			return forwardBmiddlePic;
		}

		public void setForwardBmiddlePic(String forwardBmiddlePic) {
			this.forwardBmiddlePic = forwardBmiddlePic;
		}

		public String getThumbnailPic() {
			return thumbnailPic;
		}

		public void setThumbnailPic(String thumbnailPic) {
			this.thumbnailPic = thumbnailPic;
		}

		public String getForwardThumbnailPic() {
			return forwardThumbnailPic;
		}

		public void setForwardThumbnailPic(String forwardThumbnailPic) {
			this.forwardThumbnailPic = forwardThumbnailPic;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getObjectId() {
			return objectId;
		}

		public void setObjectId(String objectId) {
			this.objectId = objectId;
		}

		public String getSenderSid() {
			return senderSid;
		}

		public void setSenderSid(String senderSid) {
			this.senderSid = senderSid;
		}

		public String getSenderName() {
			return senderName;
		}

		public void setSenderName(String senderName) {
			this.senderName = senderName;
		}

		public String getSenderUserFace() {
			return senderUserFace;
		}

		public void setSenderUserFace(String senderUserFace) {
			this.senderUserFace = senderUserFace;
		}

		public String getMessageBoardContent() {
			return messageBoardContent;
		}

		public void setMessageBoardContent(String messageBoardContent) {
			this.messageBoardContent = messageBoardContent;
		}

		public String getForwardMessageBoardContent() {
			return forwardMessageBoardContent;
		}

		public void setForwardMessageBoardContent(String forwardMessageBoardContent) {
			this.forwardMessageBoardContent = forwardMessageBoardContent;
		}

		public String getFromSource() {
			return fromSource;
		}

		public void setFromSource(String fromSource) {
			this.fromSource = fromSource;
		}

		public String getCreatedDate() {
			return createdDate;
		}

		public void setCreatedDate(String createdDate) {
			this.createdDate = createdDate;
		}

		public int getReplyNum() {
			return replyNum;
		}

		public void setReplyNum(int replyNum) {
			this.replyNum = replyNum;
		}

		public int getLikedNum() {
			return likedNum;
		}

		public void setLikedNum(int likedNum) {
			this.likedNum = likedNum;
		}

		public boolean isLiked() {
			return liked;
		}

		public void setLiked(boolean liked) {
			this.liked = liked;
		}

		@Override
		public String toString() {
			return "MessageBoardList [createdDate=" + createdDate + ", forwardMessageBoardContent=" + forwardMessageBoardContent
					+ ", fromSource=" + fromSource + ", id=" + id + ", messageBoardContent=" + messageBoardContent
					+ ", objectId=" + objectId + ", replyNum=" + replyNum + ", senderName=" + senderName + ", senderSid="
					+ senderSid + ", senderUserFace=" + senderUserFace + "]";
		}

		public static class MessageBoardMember implements Serializable {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			private String memberSid ;
			private String memberName;

			public String getMemberId() {
				return memberSid ;
			}

			public void setMemberId(String memberId) {
				this.memberSid  = memberId;
			}

			public String getMemberName() {
				return memberName;
			}

			public void setMemberName(String memberName) {
				this.memberName = memberName;
			}

			@Override
			public String toString() {
				return "MessageBoardMember [memberId=" + memberSid  + ", memberName=" + memberName + "]";
			}

		}

		public MessageBoardMember[] getAtMembers() {
			return atMembers;
		}

		public void setAtMembers(MessageBoardMember[] atMembers) {
			this.atMembers = atMembers;
		}

		public MessageBoardMember[] getForwardMessageBoardAtMembers() {
			return forwardMessageBoardAtMembers;
		}

		public void setForwardMessageBoardAtMembers(MessageBoardMember[] forwardMessageBoardAtMembers) {
			this.forwardMessageBoardAtMembers = forwardMessageBoardAtMembers;
		}

		public String getSenderTitle() {
			return senderTitle;
		}

		public void setSenderTitle(String senderTitle) {
			this.senderTitle = senderTitle;
		}

		public String getSenderCompany() {
			return senderCompany;
		}

		public void setSenderCompany(String senderCompany) {
			this.senderCompany = senderCompany;
		}

		public String getSenderIndustry() {
			return senderIndustry;
		}

		public void setSenderIndustry(String senderIndustry) {
			this.senderIndustry = senderIndustry;
		}

		public String getSenderLocation() {
			return senderLocation;
		}

		public void setSenderLocation(String senderLocation) {
			this.senderLocation = senderLocation;
		}

		public int getSenderAccountType() {
			return senderAccountType;
		}

		public void setSenderAccountType(int senderAccountType) {
			this.senderAccountType = senderAccountType;
		}

		public boolean isSenderIsRealname() {
			return senderIsRealname;
		}

		public void setSenderIsRealname(boolean senderIsRealname) {
			this.senderIsRealname = senderIsRealname;
		}

		public boolean isForwardRenhe() {
			return isForwardRenhe;
		}

		public void setForwardRenhe(boolean isForwardRenhe) {
			this.isForwardRenhe = isForwardRenhe;
		}

		public String getForwardMemberName() {
			return forwardMemberName;
		}

		public void setForwardMemberName(String forwardMemberName) {
			this.forwardMemberName = forwardMemberName;
		}

		public String getForwardMemberSId() {
			return forwardMemberSId;
		}

		public void setForwardMemberSId(String forwardMemberSId) {
			this.forwardMemberSId = forwardMemberSId;
		}

		public String getForwardMessageBoardObjectId() {
			return forwardMessageBoardObjectId;
		}

		public void setForwardMessageBoardObjectId(String forwardMessageBoardObjectId) {
			this.forwardMessageBoardObjectId = forwardMessageBoardObjectId;
		}

		public String getForwardMessageBoardId() {
			return forwardMessageBoardId;
		}

		public void setForwardMessageBoardId(String forwardMessageBoardId) {
			this.forwardMessageBoardId = forwardMessageBoardId;
		}
		
	}

	@Override
	public String toString() {
		return new org.apache.commons.lang3.builder.ToStringBuilder(this).append("state", state).append("messageBoardList", messageBoardList)
				.append("minCreatedDate", minCreatedDate).append("maxCreatedDate", maxCreatedDate)
				.append("aboveMaxCount", aboveMaxCount).toString();
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public MessageBoardList[] getMessageBoardList() {
		return messageBoardList;
	}

	public void setMessageBoardList(MessageBoardList[] messageBoardList) {
		this.messageBoardList = messageBoardList;
	}

	public long getMinCreatedDate() {
		return minCreatedDate;
	}

	public void setMinCreatedDate(long minCreatedDate) {
		this.minCreatedDate = minCreatedDate;
	}

	public long getMaxCreatedDate() {
		return maxCreatedDate;
	}

	public void setMaxCreatedDate(long maxCreatedDate) {
		this.maxCreatedDate = maxCreatedDate;
	}

	public boolean isAboveMaxCount() {
		return aboveMaxCount;
	}

	public void setAboveMaxCount(boolean aboveMaxCount) {
		this.aboveMaxCount = aboveMaxCount;
	}

//	@Override
//	public String toString() {
//		return "MessageBoards [state=" + state + ", messageBoardList=" + Arrays.toString(messageBoardList) + ", minCreatedDate="
//				+ minCreatedDate + ", maxCreatedDate=" + maxCreatedDate + "]";
//	}

}
