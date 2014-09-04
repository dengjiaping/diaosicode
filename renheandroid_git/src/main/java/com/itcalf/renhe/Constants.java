package com.itcalf.renhe;

import org.apache.http.protocol.HTTP;

public class Constants {
	
	/** 记录日志标志 */
	public static boolean LOG = true;
	/** 标志TAG */
	public static String TAG = "Renhe";

	public static final String API_PATH = "/api/json/";

	// TODO 开发用，发布时置为false
	public static boolean renhe_log = true;//开发用，发布时置为false
	
	
	public static class Http {
		//线上
//		private static final String HOST_ADDRESS = "http://4g.renhe.cn/";
//		public static final String PUSH_URL = "http://push.renhe.cn/register";
		
		//线上测试
		private static final String HOST_ADDRESS = "http://4gtest.renhe.cn/";
		public static final String PUSH_URL = "http://pushtest.renhe.cn/register";
		
		//线下
//		private static final String HOST_ADDRESS = "http://192.168.1.13:8086/";
//		private static final String HOST_ADDRESS = "http://192.168.1.18:8008/";
//		public static final String PUSH_URL = "http://192.168.1.18:8008/register";
		
		public static final String LOGIN = HOST_ADDRESS + "login.shtml";
		public static final String REGISTER = HOST_ADDRESS + "register.shtml";
		public static final String SHOW_PROFILE = HOST_ADDRESS + "viewprofile.shtml";
		public static final String SELF_MESSAGEBOARDS = HOST_ADDRESS + "messageboard/selfMessageBoards.shtml";
		public static final String FRIEND_MESSAGEBOARDS = HOST_ADDRESS + "messageboard/friendMessageBoards.shtml";
		public static final String CITY_MESSAGEBOARDS = HOST_ADDRESS + "messageboard/cityMessageBoards.shtml";
		public static final String INDUSTRY_MESSAGEBOARDS = HOST_ADDRESS + "messageboard/industryMessageBoards.shtml";
		public static final String PUBLIC_MESSAGEBOARDS = HOST_ADDRESS + "messageboard/publicMessageBoards.shtml";
		public static final String PERSONAL_MESSAGEBOARDS = HOST_ADDRESS + "messageboard/personalMesssageBoards.shtml";
		public static final String SHOW_MESSAGEBOARD = HOST_ADDRESS + "messageboard/showMessageBoard.shtml";
		public static final String MSG_COMMENTS = HOST_ADDRESS + "messageboard/comments.shtml";
		public static final String MSG_FOLLOWERS = HOST_ADDRESS + "messageboard/followers.shtml";
		public static final String MSG_FOLLOWINGS = HOST_ADDRESS + "messageboard/followings.shtml";
		public static final String PUBLIC_MESSAGEBOARD = HOST_ADDRESS + "messageboard/publishMessageBoard.shtml";
		public static final String FORWARD_MESSAGEBOARD = HOST_ADDRESS + "messageboard/forwardMessageBoard.shtml";
		public static final String FAVOUR_MESSAGEBOARD = HOST_ADDRESS + "messageboard/likeMessageBoard.shtml";
		public static final String UNFAVOUR_MESSAGEBOARD = HOST_ADDRESS + "messageboard/unlikeMessageBoard.shtml";
		public static final String REPLY_MESSAGEBOARD = HOST_ADDRESS + "messageboard/replyMessageBoard.shtml";
		public static final String DEL_MESSAGEBOARD = HOST_ADDRESS + "messageboard/deleteMessageBoard.shtml";
		public static final String  SEARCH_RELATIONSHIP = HOST_ADDRESS + "member/search.shtml";
		public static final String  SEARCH_FOLLOWERS = HOST_ADDRESS + "messageboard/followers.shtml";
		public static final String  SEARCH_FOLLOWERINGS = HOST_ADDRESS + "messageboard/followings.shtml";
		public static final String  CONTACTLIST = HOST_ADDRESS + "member/contactList.shtml";
		public static final String  INNERMSG_INBOX = HOST_ADDRESS + "message/inboxList.shtml";
		public static final String  INNERMSG_SENDBOX = HOST_ADDRESS + "message/sendboxList.shtml";
		public static final String  INNERMSG_SEND= HOST_ADDRESS + "message/sendMessage.shtml";
		public static final String  INNERMSG_MSGINFO= HOST_ADDRESS + "message/viewMessage.shtml";
		public static final String  INNERMSG_DELETEMSG= HOST_ADDRESS + "message/deleteMessage.shtml";
		public static final String  INNERMSG_CHECKMESSAGE= HOST_ADDRESS + "message/checkNewMessage.shtml";
		public static final String  INNERMSG_CHECKUNREADMESSAGE= HOST_ADDRESS + "message/checkUnReadMessageCount.shtml";
		public static final String  MESSAGEBOARD_REMOVEFOLLOW= HOST_ADDRESS + "messageboard/removeFollow.shtml";
		public static final String  MESSAGEBOARD_ADDFOLLOW= HOST_ADDRESS + "messageboard/addFollow.shtml";
		public static final String  MORE_FEEDBACK= HOST_ADDRESS + "feedback/insertFeedback.shtml";
		public static final String  MEMBER_UPLOADIMG= HOST_ADDRESS + "member/uploadUserFaceImage.shtml";
		public static final String CHECK_VERION_UPDATE = HOST_ADDRESS + "version.shtml";
		public static final String ADD_FRIEND_REQUEST =  "http://www.renhe.cn/contact/add_friend_request.html?sfid=";
		public static final String EDITOR_MEMBER = "http://www.renhe.cn/member/index.html";
		public static final String CHECK_FOLLOWRENHE = HOST_ADDRESS + "messageboard/checkFollowRenhe.shtml";
		public static final String ADDFRIEND = HOST_ADDRESS + "contact/addFriend.shtml";
		public static final String RECEIVEADDFRIEND = HOST_ADDRESS + "contact/receiveAddFriend.shtml";
		public static final String UNREADMSG = HOST_ADDRESS + "messageboard/messageBoardNotifyList.shtml";
		public static final String UNREADMSGNUM = HOST_ADDRESS + "messageboard/messageBoardNotifyCount.shtml";
		//新注册流程
		public static final String SENDREGISTERMOBILE = HOST_ADDRESS + "sendRegisterMobileVerificationCode.shtml";
		public static final String VERIFICATIONREGISTER = HOST_ADDRESS + "verificationRegisterMobileCode.shtml";
		public static final String REGISTERMOBILE = HOST_ADDRESS + "registerMobile.shtml";
		//新登录
		public static final String NEWLOGIN = HOST_ADDRESS + "userLogin.shtml";
		//老用户绑定手机号
		public static final String SENDMOBILEVALIDATIONCODE = HOST_ADDRESS + "member/sendMobileValidationCode.shtml";
		public static final String BINDMOBILE = HOST_ADDRESS + "member/bindMobile.shtml";
		
		//档案编辑
		public static final String EDITPROFESSION = HOST_ADDRESS + "editprofile/editProfession.shtml";
		public static final String EDITSPECIALTIES = HOST_ADDRESS + "editprofile/editSpecialties.shtml";
		public static final String EDITPROVIDE = HOST_ADDRESS + "editprofile/editMemberPreferredTags.shtml";
		public static final String EDITGET = HOST_ADDRESS + "editprofile/editMemberAimTags.shtml";
		public static final String ADD_WORK_INFO = HOST_ADDRESS + "editprofile/addMemberExperience.shtml";
		public static final String EDIT_WORK_INFO = HOST_ADDRESS + "editprofile/editMemberExperience.shtml";
		public static final String DELETE_WORK_INFO = HOST_ADDRESS + "editprofile/deleteMemberExperience.shtml";
		public static final String ADD_EDU_INFO = HOST_ADDRESS + "editprofile/addMemberEducation.shtml";
		public static final String EDIT_EDU_INFO = HOST_ADDRESS + "editprofile/editMemberEducation.shtml";
		public static final String DELETE_EDU_INFO = HOST_ADDRESS + "editprofile/deleteMemberEducation.shtml";
		public static final String EDIT_SELF_INFO = HOST_ADDRESS + "editprofile/editBasicInfo.shtml";
		public static final String EDIT_ORGANSITION_INFO = HOST_ADDRESS + "editprofile/editAssociations.shtml";
		public static final String EDIT_INTEREST_INFO = HOST_ADDRESS + "editprofile/editInterests.shtml";
		public static final String EDIT_AWARD_INFO = HOST_ADDRESS + "editprofile/editAwards.shtml";
		public static final String EDIT_CONTACT_INFO = HOST_ADDRESS + "editprofile/editContactInfo.shtml";
		public static final String EDIT_WEBSITE_INFO = HOST_ADDRESS + "editprofile/editWebsiteInfo.shtml";
		public static final String BLOCK_MESSAGEBOARD_MEMBER = HOST_ADDRESS + "messageboard/addBlockMessageboardMember.shtml";
		
	}
	public static class Prefs {

		public static final String	USERNAME			= "USERNAME";
		public static final String	LOGINTYPE			= "LOGINTYPE";
		public static final String	FRIENDS_AT_RENHE	= "FRIENDS_AT_RENHE";
		public static final String	DOWNLOADED_FROM_NET	= "DOWNLOADED_FROM_NET";
		public static final String	HAD_REGIST_JPUSH	= "HAD_REGIST_JPUSH";
		public static final String	RENHECARD_GUIDE_VERSION_NAME	= "RENHECARD_GUIDE_VERSION";

	}
	public static class StatusCode {

		public static final String	SUCCESS				= "SUCCESS";
		public static final String	GENERAL_ERROR		= "GENERAL_ERROR";
		public static final String	SERVER_ERROR		= "SERVER_ERROR";
		public static final String	UNAUTHORIZED_ERROR	= "UNAUTHORIZED_ERROR";
		public static final String	NETWORK_ERROR		= "NETWORK_ERROR";
		public static final String	CALL_ERROR			= "CALL_ERROR";
		public static final String	JSON_ERROR			= "JSON_ERROR";
		public static final String	PROTOCOL_ERROR		= "PROTOCOL_ERROR";
		public static final String	ILLEGAL_PARAMETER	= "ILLEGAL_PARAMETER";
		public static final String	PERMISSION_DENIED	= "PERMISSION_DENIED";

	}
	public static class RenheJpush {

		public static final String	PARAM_APP_STR		= "app";
		public static final String	PARAM_OS_STR		= "os";
		public static final String	PARAM_ID_STR		= "id";
		public static final String	PARAM_TOKEN_STR		= "token";

		public static final int		CODE_APP_RENHECARD	= 0;
		public static final int		CODE_OS_ANDROID		= 1;

	}
	public static class Tab {

		public static final String	KEY				= "tab";
		public static final String	TAB_ROOM	    = "tab_room";
		public static final String	TAB_FRIEND	    = "tab_friend";
		public static final String	TAB_COLLEAGUE   = "tab_colleague";
		public static final String	TAB_CITY		= "tab_city";
		public static final String	TAB_FOLLOW		= "tab_follow";
	}
	public static class Item{
		public static int ROOM = 1; 
		public static int CONTACT = 2; 
		public static int RESEARCH = 3; 
		public static int INNERMSG = 4; 
		public static int MORE = 5; 
		public static int SELFINFO = 6; 
	}
	public static final String	API_HTTP_ENCODING	= HTTP.UTF_8;
	public static String DATA_LOGOUT = "data_logout";
	
	public static class DbTable{
		public static final String INBOX = "innermsg_in_box";
		public static final String SENDBOX = "innermsg_send_box";
		public static final String INBOX_OBJECTID = "innermsg_in_box_objectid";
		public static final String SENDNBOX_OBJECTID = "innermsg_send_box_objectid";
	}
	
}
