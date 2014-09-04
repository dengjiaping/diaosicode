package com.itcalf.renhe.context.room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;

import com.itcalf.renhe.Constants;
import com.itcalf.renhe.R;
import com.itcalf.renhe.RenheApplication;
import com.itcalf.renhe.dto.MessageBoards;
import com.itcalf.renhe.dto.MessageBoards.MessageBoardList;

/**
 * Feature:留言列表异步加载 Desc:留言列表异步加载
 * 
 * @author xp
 * 
 */
public class RoomTask extends AsyncTask<Object, Void, MessageBoards> {

	// 数据回调
	private IRoomBack mRoomBack;
	private Context mContext;

	public RoomTask(Context context, IRoomBack back) {
		super();
		this.mContext = context;
		this.mRoomBack = back;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mRoomBack.onPre();
	}

	// 后台线程调用服务端接口
	@Override
	protected MessageBoards doInBackground(Object... params) {
		try {
			return ((RenheApplication) mContext.getApplicationContext()).getMessageBoardCommand().getMsgBoards(
					(String) params[0], (String) params[1], (String) params[2], (Integer) params[3], (String) params[4],
					(Integer) params[5], (Long) params[6], (Long) params[7], mContext);
		} catch (Exception e) {
			if (Constants.LOG) {
				Log.d(Constants.TAG, "CityTask", e);
			}
		}
		return null;
	}

	@Override
	protected void onPostExecute(MessageBoards result) {
		super.onPostExecute(result);
		if (null != result && 1 == result.getState()) {
			// 获取留言列表数据，封装后执行回调
			MessageBoardList[] mbList = result.getMessageBoardList();
			List<Map<String, Object>> weiboList = new ArrayList<Map<String, Object>>();
			if (null != mbList && mbList.length > 0) {
				for (int i = 0; i < mbList.length; i++) {
					MessageBoardList mb = mbList[i];
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("Id", mb.getId());
					map.put("objectId", mb.getObjectId());
					map.put("avatar", R.drawable.avatar);
					if (null != mb.getSenderUserFace()) {
						map.put("userface", mb.getSenderUserFace());
					}
					map.put("sid", mb.getSenderSid());
					map.put("username", mb.getSenderName());
					map.put("datetime", mb.getCreatedDate());
					map.put("thumbnailPic1", R.drawable.none);
					map.put("forwardThumbnailPic1", R.drawable.none);
					map.put("thumbnailPic", mb.getThumbnailPic() == null ? "" : mb.getThumbnailPic());
					map.put("bmiddlePic", mb.getBmiddlePic() == null ? "" : mb.getBmiddlePic());
					map.put("forwardThumbnailPic", mb.getForwardThumbnailPic() == null ? "" : mb.getForwardThumbnailPic());
					map.put("forwardBmiddlePic", mb.getForwardBmiddlePic() == null ? "" : mb.getForwardBmiddlePic());
//					map.put("content", Html.fromHtml(mb.getMessageBoardContent()).toString());
					if(null != mb.getMessageBoardContent()){
						map.put("content", mb.getMessageBoardContent().toString());
					}
					if (null != mb.getForwardMessageBoardContent()) {
//						map.put("rawcontent", Html.fromHtml(mb.getForwardMessageBoardContent()).toString());
						map.put("rawcontent", mb.getForwardMessageBoardContent().toString());
						map.put("forwardMessageMember", mb.getForwardMessageBoardAtMembers());
//						map.put("forwardMessageMemberid", "dd5e630df24ba0ee");
					}
					map.put("messageBoardMember", mb.getAtMembers());
					map.put("client", "来自" + mb.getFromSource());
					map.put("reply", mb.getReplyNum());
					map.put("favourNumber", mb.getLikedNum());
					map.put("isFavour", mb.isLiked());
					map.put("senderTitle", mb.getSenderTitle());
					map.put("senderCompany", mb.getSenderCompany());
					map.put("senderIndustry", mb.getSenderIndustry());
					map.put("senderLocation", mb.getSenderLocation());
					map.put("accountType", mb.getSenderAccountType());
					map.put("isRealName", mb.isSenderIsRealname());
					
					map.put("isForwardRenhe", mb.isForwardRenhe());// 是否是人和网的转发，是人和网的转发，会返回forwardMemberName、forwardMemberSId、forwardMessageBoardObjectId、forwardMessageBoardId
					map.put("forwardMemberName", mb.getForwardMemberName());//被转发客厅的会员姓名
					map.put("forwardMemberSId", mb.getForwardMemberSId());//被转发客厅的会员sid
					map.put("forwardMessageBoardObjectId", mb.getForwardMessageBoardObjectId());//被转发客厅的objectId
					map.put("forwardMessageBoardId", mb.getForwardMessageBoardId());// 被转发客厅的id
					weiboList.add(map);
				}
			}
			mRoomBack.doPost(weiboList, result.getMaxCreatedDate(), result.getMinCreatedDate(), result);
			return;
		}
		mRoomBack.doPost(null, 0, 0, null);
	}

	public interface IRoomBack {

		void onPre();

		void doPost(List<Map<String, Object>> result, long max, long min, MessageBoards msg);

	}
}
