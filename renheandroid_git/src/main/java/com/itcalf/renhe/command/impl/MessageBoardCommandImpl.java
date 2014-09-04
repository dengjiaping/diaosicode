package com.itcalf.renhe.command.impl;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.itcalf.renhe.Constants;
import com.itcalf.renhe.command.IMessageBoardCommand;
import com.itcalf.renhe.dto.MessageBoardDetail;
import com.itcalf.renhe.dto.MessageBoardOperation;
import com.itcalf.renhe.dto.MessageBoards;
import com.itcalf.renhe.dto.MsgComments;
import com.itcalf.renhe.dto.NewMessageBoards;
import com.itcalf.renhe.dto.UnReadMsgNum;
import com.itcalf.renhe.utils.HttpUtil;

public class MessageBoardCommandImpl implements IMessageBoardCommand {

	@Override
	public MessageBoards getMsgBoards(String adSId, String sid, String viewSId, Integer type, String cls, Integer count,
			Long minCreateDate, Long maxCreateDate, Context context) throws Exception {
		Map<String, Object> reqParams = new HashMap<String, Object>();
		reqParams.put("sid", sid);
		reqParams.put("count", count);// 取数据的count
		reqParams.put("adSId", adSId);
		if (null != viewSId) {
			reqParams.put("viewSId", viewSId);
		}
		reqParams.put("type", cls);
		if (null != minCreateDate) {
			reqParams.put("minCreatedDate", minCreateDate);
		}
		if (null != maxCreateDate) {
			reqParams.put("maxCreatedDate", maxCreateDate);
		}
		switch (type) {
		case SELF_MSG_BOARD:
			return (MessageBoards) HttpUtil.doHttpRequest(Constants.Http.SELF_MESSAGEBOARDS, reqParams, MessageBoards.class,
					context);
		case FRIEND_MSG_BOARD:
			return (MessageBoards) HttpUtil.doHttpRequest(Constants.Http.FRIEND_MESSAGEBOARDS, reqParams, MessageBoards.class,
					context);
		case INDUSTRY_MSG_BOARD:
			return (MessageBoards) HttpUtil.doHttpRequest(Constants.Http.INDUSTRY_MESSAGEBOARDS, reqParams, MessageBoards.class,
					context);
		case CITY_MSG_BOARD:
			return (MessageBoards) HttpUtil.doHttpRequest(Constants.Http.CITY_MESSAGEBOARDS, reqParams, MessageBoards.class,
					context);
		case FOLLOW_MSG_BOARD:
			return (MessageBoards) HttpUtil.doHttpRequest(Constants.Http.PUBLIC_MESSAGEBOARDS, reqParams, MessageBoards.class,
					context);
		case PERSONAL_MESSAGEBOARDS:
			return (MessageBoards) HttpUtil.doHttpRequest(Constants.Http.PERSONAL_MESSAGEBOARDS, reqParams, MessageBoards.class,
					context);
		}
		return null;
	}

	@Override
	public MessageBoardDetail getMsgBoradDetail(String objectId, String adSId, String sid, Context context) throws Exception {
		Map<String, Object> reqParams = new HashMap<String, Object>();
		reqParams.put("objectId", objectId);// messageBoard的objectId用于请求客厅留言的详细页面
		reqParams.put("adSId", adSId);
		reqParams.put("sid", sid);
		return (MessageBoardDetail) HttpUtil.doHttpRequest(Constants.Http.SHOW_MESSAGEBOARD, reqParams, MessageBoardDetail.class,
				context);
	}

	@Override
	public MsgComments getMsgComments(String objectId, String adSId, String sid, int start, int count, Context context)
			throws Exception {
		Map<String, Object> reqParams = new HashMap<String, Object>();
		reqParams.put("objectId", objectId);// messageBoard的objectId用于请求客厅留言的详细页面
		reqParams.put("start", start);
		reqParams.put("count", count);
		reqParams.put("sid", sid);
		reqParams.put("adSId", adSId);
		return (MsgComments) HttpUtil.doHttpRequest(Constants.Http.MSG_COMMENTS, reqParams, MsgComments.class, context);
	}

	@Override
	public MessageBoardOperation publicMessageBoard(String adSId, String sid, String content, Context context) throws Exception {
		Map<String, Object> reqParams = new HashMap<String, Object>();
		reqParams.put("adSId", adSId);
		reqParams.put("sid", sid);
		reqParams.put("content", content);
		return (MessageBoardOperation) HttpUtil.doHttpRequest(Constants.Http.PUBLIC_MESSAGEBOARD, reqParams,
				MessageBoardOperation.class, context);
	}

	@Override
	public MessageBoardOperation replyMessageBoard(String adSId, String sid, String messageBoardId, String messageBoardObjectId,
			String content, boolean forwardMessageBoard, String replyMessageBoardId, String replyMessageBoardObjectId, Context context) throws Exception {
		Map<String, Object> reqParams = new HashMap<String, Object>();
		reqParams.put("adSId", adSId);
		reqParams.put("sid", sid);
		reqParams.put("messageBoardId", messageBoardId);
		reqParams.put("messageBoardObjectId", messageBoardObjectId);
		reqParams.put("content", content);
		reqParams.put("forwardMessageBoard", forwardMessageBoard);
		if(null != replyMessageBoardId){
			reqParams.put("replyMessageBoardId", replyMessageBoardId);
		}
		if(null != replyMessageBoardObjectId){
			reqParams.put("replyMessageBoardObjectId", replyMessageBoardObjectId);
		}
		return (MessageBoardOperation) HttpUtil.doHttpRequest(Constants.Http.REPLY_MESSAGEBOARD, reqParams,
				MessageBoardOperation.class, context);
	}

	@Override
	public MessageBoardOperation forwardMessageBoard(String adSId, String sid, String messageBoardObjectId, String content,
			Context context) throws Exception {
		Map<String, Object> reqParams = new HashMap<String, Object>();
		reqParams.put("adSId", adSId);
		reqParams.put("sid", sid);
		reqParams.put("messageBoardObjectId", messageBoardObjectId);
		reqParams.put("content", content);
		return (MessageBoardOperation) HttpUtil.doHttpRequest(Constants.Http.FORWARD_MESSAGEBOARD, reqParams,
				MessageBoardOperation.class, context);
	}
	@Override
	public MessageBoardOperation favourMessageBoard(String adSId, String sid, String id, String messageBoardObjectId,
			Context context) throws Exception {
		Map<String, Object> reqParams = new HashMap<String, Object>();
		reqParams.put("adSId", adSId);
		reqParams.put("sid", sid);
		reqParams.put("messageBoardObjectId", messageBoardObjectId);
		reqParams.put("messageBoardId", id);
		return (MessageBoardOperation) HttpUtil.doHttpRequest(Constants.Http.FAVOUR_MESSAGEBOARD, reqParams,
				MessageBoardOperation.class, context);
	}
	
	@Override
	public MessageBoardOperation unFavourMessageBoard(String adSId, String sid,String messageBoardObjectId,
			Context context) throws Exception {
		Map<String, Object> reqParams = new HashMap<String, Object>();
		reqParams.put("adSId", adSId);
		reqParams.put("sid", sid);
		reqParams.put("messageBoardObjectId", messageBoardObjectId);
		return (MessageBoardOperation) HttpUtil.doHttpRequest(Constants.Http.UNFAVOUR_MESSAGEBOARD, reqParams,
				MessageBoardOperation.class, context);
	}
	@Override
	public MessageBoardOperation setMyJPush(String id, String token, String codeOS, String codeAPP, Context context) throws Exception {
		Map<String, Object> reqParams = new HashMap<String, Object>();
		reqParams.put(Constants.RenheJpush.PARAM_ID_STR, id);
		reqParams.put(Constants.RenheJpush.PARAM_TOKEN_STR, token);
		reqParams.put(Constants.RenheJpush.PARAM_OS_STR, Constants.RenheJpush.CODE_OS_ANDROID);
		reqParams.put(Constants.RenheJpush.PARAM_APP_STR, Constants.RenheJpush.CODE_APP_RENHECARD);
		return (MessageBoardOperation) HttpUtil.doHttpRequest(Constants.Http.PUSH_URL, reqParams,
				MessageBoardOperation.class, context);
	}
	@Override
	public MessageBoardOperation delMyJPush(String id, String token, String codeOS, String codeAPP, Context context) throws Exception {
		Map<String, Object> reqParams = new HashMap<String, Object>();
		reqParams.put(Constants.RenheJpush.PARAM_ID_STR, id);
		reqParams.put(Constants.RenheJpush.PARAM_TOKEN_STR, token);
		reqParams.put(Constants.RenheJpush.PARAM_OS_STR, Constants.RenheJpush.CODE_OS_ANDROID);
		reqParams.put(Constants.RenheJpush.PARAM_APP_STR, Constants.RenheJpush.CODE_APP_RENHECARD);
		return (MessageBoardOperation) HttpUtil.doHttpRequest(Constants.Http.PUSH_URL, reqParams,
				MessageBoardOperation.class, context);
	}
	@Override
	public NewMessageBoards unReadNewMsg(String adSId, String sid, Context context) throws Exception {
		Map<String, Object> reqParams = new HashMap<String, Object>();
		reqParams.put("adSId", adSId);
		reqParams.put("sid", sid);
		return (NewMessageBoards) HttpUtil.doHttpRequest(Constants.Http.UNREADMSG, reqParams,
				NewMessageBoards.class, context);
	}
	@Override
	public UnReadMsgNum unReadNewMsgNum(String adSId, String sid, Context context) throws Exception {
		Map<String, Object> reqParams = new HashMap<String, Object>();
		reqParams.put("adSId", adSId);
		reqParams.put("sid", sid);
		return (UnReadMsgNum) HttpUtil.doHttpRequest(Constants.Http.UNREADMSGNUM, reqParams,
				UnReadMsgNum.class, context);
	}
}
