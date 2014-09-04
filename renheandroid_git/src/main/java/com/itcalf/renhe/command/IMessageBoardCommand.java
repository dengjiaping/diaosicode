package com.itcalf.renhe.command;

import android.content.Context;

import com.itcalf.renhe.dto.MessageBoardDetail;
import com.itcalf.renhe.dto.MessageBoardOperation;
import com.itcalf.renhe.dto.MessageBoards;
import com.itcalf.renhe.dto.MsgComments;
import com.itcalf.renhe.dto.NewMessageBoards;
import com.itcalf.renhe.dto.UnReadMsgNum;

public interface IMessageBoardCommand {

	public static final int SELF_MSG_BOARD = 1;
	public static final int FRIEND_MSG_BOARD = 2;
	public static final int INDUSTRY_MSG_BOARD = 3;
	public static final int CITY_MSG_BOARD = 4;
	public static final int FOLLOW_MSG_BOARD = 5;
	public static final int PERSONAL_MESSAGEBOARDS = 6;

	/**
	 * 
	 * @param sid
	 *            会员加密后的id用于请求客厅留言
	 * @param adSId
	 *            加密后用户id和密码的信息 以后的每次请求中都要带上它
	 * @param type
	 *            取值1、2、3、4、5
	 * @param cls
	 *            取值必须为more 更多,new 更新, renew 强制取最新
	 * @param count
	 *            取数据的count
	 * @param minCreateDate
	 *            Integer 最小的createDate
	 * @param maxCreateDate
	 *            Integer 最大的createDate
	 * @return
	 * @throws Exception
	 */
	MessageBoards getMsgBoards(String adSId, String sid, String viewSId, Integer type,
			String cls, Integer count, Long minCreateDate,
			Long maxCreateDate,Context context) throws Exception;

	/**
	 * 获取信息详情
	 * 
	 * @param objectId
	 *            messageBoard的objectId用于请求客厅留言的详细页面
	 * @param adSId
	 * @param sid
	 * @return
	 * @throws Exception
	 */
	MessageBoardDetail getMsgBoradDetail(String objectId, String adSId,
			String sid,Context context) throws Exception;

	/**
	 * 获取留言列表
	 * 
	 * @param objectId
	 *            messageBoard的objectId用于请求客厅留言的详细页面
	 * @param adSId
	 * @param sid
	 * @param start
	 * @param count
	 * @return
	 * @throws Exception
	 */
	MsgComments getMsgComments(String objectId, String adSId, String sid,
			int start, int count,Context context) throws Exception;

	/**
	 * 发布留言
	 * 
	 * @param adSId
	 *            会员加密后的id用于请求客厅留言
	 * @param sid
	 * @param content
	 * @return
	 * @throws Exception
	 */
	MessageBoardOperation publicMessageBoard(String adSId, String sid,
			String content,Context context) throws Exception;

	/**
	 * 回复留言
	 * 
	 * @param adSId
	 *            会员加密后的id用于请求客厅留言
	 * @param sid
	 * @param messageBoardId
	 * @param messageBoardObjectId
	 * @param content
	 * @return
	 * @throws Exception
	 */
	MessageBoardOperation replyMessageBoard(String adSId, String sid,
			String messageBoardId, String messageBoardObjectId, String content, boolean forwardMessageBoard,String replyMessageBoardId, String replyMessageBoardObjectId, Context context)
			throws Exception;

	/**
	 * 转发留言
	 * 
	 * @param adSId
	 *            会员加密后的id用于请求客厅留言
	 * @param sid
	 * @param messageBoardObjectId
	 * @param content
	 * @return
	 * @throws Exception
	 */
	public MessageBoardOperation forwardMessageBoard(String adSId, String sid,
			String messageBoardObjectId, String content,Context context) throws Exception;
	/**
	 * 对留言点赞
	 * 
	 * @param adSId
	 *            会员加密后的id用于请求客厅留言
	 * @param sid
	 * @param messageBoardObjectId
	 * @param content
	 * @return
	 * @throws Exception
	 */
	public MessageBoardOperation favourMessageBoard(String adSId, String sid,
			String id,String messageBoardObjectId,Context context) throws Exception;
	/**
	 * 对留言取消点赞
	 * 
	 * @param adSId
	 *            会员加密后的id用于请求客厅留言
	 * @param sid
	 * @param messageBoardObjectId
	 * @param content
	 * @return
	 * @throws Exception
	 */
	public MessageBoardOperation unFavourMessageBoard(String adSId, String sid,
			String messageBoardObjectId,Context context) throws Exception;
	
	/**
	 * 将jpush设置到服务器
	 * 
	 * @param id
	 *            
	 * @param token
	 * @return
	 * @throws Exception
	 */
	public MessageBoardOperation setMyJPush(String id, String token,
			String codeOS,String codeAPP,Context context) throws Exception;
	
	/**
	 * 删除jpush设置
	 * 
	 * @param id
	 *            
	 * @param token
	 * @return
	 * @throws Exception
	 */
	public MessageBoardOperation delMyJPush(String id, String token,
			String codeOS,String codeAPP,Context context) throws Exception;
	/**
	 * 获取新消息
	 * 
	 * @param adSId
	 *            会员加密后的id用于请求客厅留言
	 * @param sid
	 * @return
	 * @throws Exception
	 */
	public NewMessageBoards unReadNewMsg(String adSId, String sid,
			Context context) throws Exception;
	/**
	 * 获取新消息数目
	 * 
	 * @param adSId
	 *            会员加密后的id用于请求客厅留言
	 * @param sid
	 * @return
	 * @throws Exception
	 */
	public UnReadMsgNum unReadNewMsgNum(String adSId, String sid, Context context) throws Exception;
}
