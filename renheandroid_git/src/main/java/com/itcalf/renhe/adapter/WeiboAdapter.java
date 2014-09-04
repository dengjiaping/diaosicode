package com.itcalf.renhe.adapter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.itcalf.renhe.R;
import com.itcalf.renhe.RenheApplication;
import com.itcalf.renhe.cache.CacheManager;
import com.itcalf.renhe.context.archives.MyHomeArchivesActivity;
import com.itcalf.renhe.context.room.AddReplyActivity;
import com.itcalf.renhe.context.room.ForwardMessageBoardActivity;
import com.itcalf.renhe.context.room.MessageBoardActivity;
import com.itcalf.renhe.dto.MessageBoardOperation;
import com.itcalf.renhe.dto.MessageBoards.MessageBoardList.MessageBoardMember;
import com.itcalf.renhe.imageUtil.StandardImageXML;
import com.itcalf.renhe.utils.AsyncImageLoader;
import com.itcalf.renhe.utils.DateUtil;
import com.itcalf.renhe.utils.MatrixUtil;
import com.itcalf.renhe.utils.SimpleAsyncImageLoad;
import com.itcalf.renhe.view.TextViewFixTouchConsume;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Feature:扩展SimpleAdapter的BaseAdapter
 * Description:增加了图片缓存加载，控制特殊视图的显示
 * 
 * @author xp
 * 
 */
public class WeiboAdapter extends SimpleAdapter {
	private String email;
	private Context ct;
	private ListView mListView;
	com.itcalf.renhe.view.TextViewFixTouchConsume rawcontentTv;
	com.itcalf.renhe.view.TextViewFixTouchConsume contentTv;
	TextView nameTv;

	Map<String, Object> map;
	// 留言显示数据
	private List<Map<String, Object>> mWeiboList = new ArrayList<Map<String, Object>>();
	SharedPreferences userInfo;
	Editor editor;

	@SuppressWarnings("unchecked")
	public WeiboAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to,
			String email, ListView listView) {
		super(context, data, resource, from, to);
		this.mWeiboList = (List<Map<String, Object>>) data;
		this.email = email;
		this.ct = context;
		this.mListView = listView;
		userInfo = ct.getSharedPreferences("setting_info", 0);
		editor = userInfo.edit();
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		if (null != view) {
			map = (Map<String, Object>) getItem(position);
			if (null != map) {
				String userId = (String) map.get("id");
				String userface = (String) map.get("userface");
				Object replyObj = map.get("reply");
				Object dateObject = map.get("datetime");
				ImageView vipIv = (ImageView) view.findViewById(R.id.vipImage);
				ImageView realNameIv = (ImageView) view.findViewById(R.id.realnameImage);
				Object accountObject  = map.get("accountType");
				Object realNameObject = map.get("isRealName");
				int accountType = 0;
				boolean isRealName = false;
				if(null != accountObject){
					accountType = (Integer)map.get("accountType");;//账号vip等级类型：0：普通会员；1：VIP会员；2：黄金会员；3：铂金会员
				}
				if(null != realNameObject){
					isRealName= (Boolean)map.get("isRealName");//是否是实名认证的会员
				}
				switch (accountType) {
				case 0:
					vipIv.setVisibility(View.GONE);
					break;
				case 1:
					vipIv.setVisibility(View.VISIBLE);
					vipIv.setImageResource(R.drawable.vip_1);
					break;
				case 2:
					vipIv.setVisibility(View.VISIBLE);
					vipIv.setImageResource(R.drawable.vip_2);
					break;
				case 3:
					vipIv.setVisibility(View.VISIBLE);
					vipIv.setImageResource(R.drawable.vip_3);
					break;

				default:
					break;
				}
				if(isRealName && accountType <= 0){
					realNameIv.setVisibility(View.VISIBLE);
					realNameIv.setImageResource(R.drawable.realname);
				}else{
					realNameIv.setVisibility(View.GONE);
				}
				
				
				TextViewFixTouchConsume contentTv = (TextViewFixTouchConsume) view.findViewById(R.id.content_txt);
				TextViewFixTouchConsume rawContentTv = (TextViewFixTouchConsume) view.findViewById(R.id.rawcontent_txt);
				String content = (String) map.get("content");
				String mrawContent = (String) map.get("rawcontent");
				Object messageBoardMembersObject = map.get("messageBoardMember");
				Object forwardMessageBoardMembersObject = map.get("forwardMessageMember");
				if (contentTv != null && content != null) {
					if (null != messageBoardMembersObject) {
						MessageBoardMember[] messageBoardMembers = (MessageBoardMember[]) messageBoardMembersObject;
						if (messageBoardMembers.length > 0) {
							SpannableString span = getSpannableString(content,messageBoardMembers);
							contentTv.setText(span);
							contentTv.setMovementMethod(TextViewFixTouchConsume.LocalLinkMovementMethod.getInstance());
						} else {
							SpannableString spannableString = getNoAtSpannedString(null,content);
							contentTv.setText(spannableString);
						}
					} else {
						SpannableString spannableString = getNoAtSpannedString(null,content);
						contentTv.setText(spannableString);
					}
					
				}

				if(rawContentTv != null && mrawContent != null){
					if (null != forwardMessageBoardMembersObject) {
						MessageBoardMember[] forwardMessageBoardMembers= (MessageBoardMember[])forwardMessageBoardMembersObject;
						if (forwardMessageBoardMembers.length > 0) {
							SpannableString span = getSpannableString(mrawContent,forwardMessageBoardMembers);
							rawContentTv.setText(span);
							rawContentTv.setMovementMethod(TextViewFixTouchConsume.LocalLinkMovementMethod.getInstance());
						} else {
							SpannableString spannableString = getNoAtSpannedString(null,mrawContent);
							rawContentTv.setText(spannableString);
						}
					} else {
						SpannableString spannableString = getNoAtSpannedString(null,mrawContent);
						rawContentTv.setText(spannableString);
					}
				
				}
				TextView dateTv = (TextView) view.findViewById(R.id.datetime_txt);
				if (null != dateObject) {
					String date = dateObject.toString();
					string2Date(date, dateTv);
				}
				int replyNum = 0;
				if (null != replyObj) {
					replyNum = (Integer) replyObj;
				}

				Button replyButton = (Button) view.findViewById(R.id.room_item_reply);
				if (null != replyButton) {
					if (replyNum >= 1) {
						replyButton.setText(replyNum + "");
					} else {
						replyButton.setText(ct.getResources().getString(R.string.room_reply));
					}
				}
				View avatarView = view.findViewById(R.id.avatar_img);
				if (!TextUtils.isEmpty(userface) && null != avatarView) {
//					avatarView.setTag(userface + position);
//					final int pos = position;
//					SimpleAsyncImageLoad.loadDrawable(userId, userface, email, 70, 70, ct,
//							new SimpleAsyncImageLoad.ImageCallback() {
//
//								@Override
//								public void imageLoaded(Drawable imageDrawable, String imageUrl) {
//									ImageView imageViewByTag = (ImageView) mListView.findViewWithTag(imageUrl + pos);
//									if (imageViewByTag != null) {
//										imageViewByTag.setImageDrawable(imageDrawable);
//									}
//								}
//							});
					//方法2
//					avatarView.setTag(userface);
//					CacheManager.IMAGE_CACHE.initData(ct, "renhe_imagecache");
//					CacheManager.IMAGE_CACHE.setContext(ct);
//					CacheManager.IMAGE_CACHE.setCacheFolder(CacheManager.DEFAULT_IMAGECACHE_FOLDER);
//					if (!CacheManager.IMAGE_CACHE.get(userface, avatarView)) {
//						((ImageView)avatarView).setImageDrawable(ct.getResources().getDrawable(R.drawable.avatar));
//					}
					//方法3
					ImageLoader imageLoader = ImageLoader.getInstance();		
					try {
						imageLoader.displayImage(userface, (ImageView)avatarView, CacheManager.options,  CacheManager.animateFirstDisplayListener);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				View thumbnailPic = view.findViewById(R.id.thumbnailPic);
				String bmiddlePic = (String) map.get("bmiddlePic");
				if (null != thumbnailPic) {
					thumbnailPic.setOnClickListener(new clickPic(bmiddlePic));
				}
				Object obj1 = map.get("thumbnailPic");
				if (obj1 != null && !obj1.toString().equals("") && null != thumbnailPic) {
					if (userInfo.getBoolean("roomshowpic", true)) {
						thumbnailPic.setVisibility(View.VISIBLE);
						AsyncImageLoader
								.getInstance()
								.populateData(ct, email, false, true, false)
								.loadPic((ImageView) thumbnailPic, null, obj1.toString(), null, null,
										MatrixUtil.getPostMatrix((Activity) ct),true);
					} else {
						// 不显示图片 ，先处理为影藏
						thumbnailPic.setVisibility(View.GONE);
					}
				} else if (thumbnailPic != null) {
					thumbnailPic.setVisibility(View.GONE);
				}

				View forwardThumbnailPic = view.findViewById(R.id.forwardThumbnailPic);
				String forwardBmiddlePic = (String) map.get("forwardBmiddlePic");
				if (null != forwardThumbnailPic) {
					forwardThumbnailPic.setOnClickListener(new clickPic(forwardBmiddlePic));
				}
				Object obj2 = map.get("forwardThumbnailPic");
				if (obj2 != null && !obj2.toString().equals("") && null != forwardThumbnailPic) {
					if (userInfo.getBoolean("roomshowpic", true)) {
						forwardThumbnailPic.setVisibility(View.VISIBLE);
						AsyncImageLoader
								.getInstance()
								.populateData(ct, email, false, true, false)
								.loadPic((ImageView) forwardThumbnailPic, null, obj2.toString(), null, null,
										MatrixUtil.getPostMatrix((Activity) ct),true);
					} else {
						// 不显示图片 ，先处理为影藏
						forwardThumbnailPic.setVisibility(View.GONE);
					}

				} else if (forwardThumbnailPic != null) {
					forwardThumbnailPic.setVisibility(View.GONE);
				}

				String rawContent = (String) map.get("rawcontent");
				View rawView = view.findViewById(R.id.rawcontentlayout);
				if (null != rawView) {
					if (null == rawContent) {
						rawView.setVisibility(View.GONE);
					} else {
						rawView.setVisibility(View.VISIBLE);
					}
				}
				LinearLayout forwardLl = (LinearLayout) view.findViewById(R.id.room_item_reforward_ll);
				LinearLayout replyLl = (LinearLayout) view.findViewById(R.id.room_item_reply_ll);
				LinearLayout goodLl = (LinearLayout) view.findViewById(R.id.room_item_good_ll);
				final int pos = position;
				if (null != forwardLl) {
					forwardLl.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							String rawcontent = "";
							String username = "";
							Map<String, Object> map = (Map<String, Object>) getItem(pos);
							Object rawcontentObj = map.get("rawcontent");
							Object sendernameObj = map.get("username");
							String objectId = (String) map.get("objectId");
							if (null != rawcontentObj) {
								rawcontent = rawcontentObj.toString();
							}
							if (null != sendernameObj) {
								username = sendernameObj.toString();
							}
							if (null != objectId) {
								Bundle bundle = new Bundle();
								bundle.putString("objectId", objectId);
								if (!TextUtils.isEmpty(rawcontent)) {
									bundle.putString("sender", username);
									bundle.putString("rawContent", rawcontent);
								}
								Intent intent = new Intent(ct, ForwardMessageBoardActivity.class);
								intent.putExtras(bundle);
								ct.startActivity(intent);
							}
						}
					});
				}

				// 监听回复按钮单击事件
				if (null != replyButton) {
					replyLl.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {

							Map<String, Object> map = (Map<String, Object>) getItem(pos);
							String mObjectId = (String) map.get("objectId");
							String mId = "";
							if(null != map.get("Id")){
								mId = map.get("Id").toString();
							}
							if (null != mId && null != mObjectId) {
								Bundle bundle = new Bundle();
								bundle.putString("objectId", mObjectId);
								bundle.putString("id", mId);
								Intent intent = new Intent(ct, AddReplyActivity.class);
								intent.putExtras(bundle);
								((Activity) ct).startActivityForResult(intent, 2);
							}
						}
					});
				}
				// 监听赞按钮单击事件
				Button goodButton = (Button) view.findViewById(R.id.room_item_good);
				Object favourNumberObj = map.get("favourNumber");
				Object isFavourObj = map.get("isFavour");
				int favourNumber = 0;
				boolean isFavour = false;
				if (null != goodLl && null != goodButton) {

					if (null != favourNumberObj) {
						favourNumber = (Integer) favourNumberObj;
						if (favourNumber > 0) {
							goodButton.setText(favourNumber + "");

						} else {
							goodButton.setText("赞");
						}
						if (null != isFavourObj) {
							isFavour = (Boolean) isFavourObj;
							if (isFavour) {
								goodButton.setCompoundDrawablesWithIntrinsicBounds(
										ct.getResources().getDrawable(R.drawable.good), null, null, null);
								goodButton.setTextColor(ct.getResources().getColor(R.color.room_good_textcolor));
							} else {
								goodButton.setCompoundDrawablesWithIntrinsicBounds(
										ct.getResources().getDrawable(R.drawable.good_p), null, null, null);
								goodButton.setTextColor(ct.getResources().getColor(R.color.blog_item_date_text));
							}
						}
					} else {
						goodButton.setCompoundDrawablesWithIntrinsicBounds(ct.getResources().getDrawable(R.drawable.good_p),
								null, null, null);
						goodButton.setTextColor(ct.getResources().getColor(R.color.blog_item_date_text));
					}
					goodLl.setOnClickListener(new goodClick(goodLl.getChildAt(0), position, favourNumber, isFavour));
				}
			}
		}
		return view;
	}

	class clickPic implements OnClickListener {
		String picUrl;

		public clickPic(String url) {
			picUrl = url;
		}

		@Override
		public void onClick(View v) {
			if (null != picUrl && !picUrl.equals("")) {
				Intent intent = new Intent(ct, StandardImageXML.class);
				intent.putExtra("imageurl", picUrl);
				ct.startActivity(intent);
			}
		}
	}

	class goodClick implements OnClickListener {

		View goodButton;
		int position;
		int favourNumber;
		boolean isFavour;

		public goodClick(View view, int position, int favourNumber, boolean isFavour) {
			this.goodButton = view;
			this.position = position;
			this.favourNumber = favourNumber;
			this.isFavour = isFavour;
		}

		@Override
		public void onClick(View arg0) {
			LinearLayout layout = (LinearLayout) arg0;
			Button gButton = (Button) layout.getChildAt(0);

			//			String mObjectId = (String) map.get("objectId");
			//			int mId = (Integer)map.get("Id");
			String mObjectId = (String) mWeiboList.get(position).get("objectId");
			int mId = -1;
			if(null != mWeiboList.get(position).get("Id")){
				mId = (Integer)mWeiboList.get(position).get("Id") ;
			}
			if (null != mObjectId) {
				if (isFavour) {
					markFavour(mId, mObjectId, false);
				} else {
					markFavour(mId, mObjectId, true);
				}
			}
			if (isFavour) {
				isFavour = false;
				favourNumber = favourNumber - 1;
			} else {
				isFavour = true;
				favourNumber = favourNumber + 1;
			}
			if (isFavour) {
				gButton.setCompoundDrawablesWithIntrinsicBounds(ct.getResources().getDrawable(R.drawable.good), null, null, null);
				gButton.setTextColor(ct.getResources().getColor(R.color.room_good_textcolor));
				if (favourNumber > 0) {
					gButton.setText(favourNumber + "");
				} else {
					gButton.setText("赞");
				}
			} else {
				gButton.setCompoundDrawablesWithIntrinsicBounds(ct.getResources().getDrawable(R.drawable.good_p), null, null,
						null);
				gButton.setTextColor(ct.getResources().getColor(R.color.blog_item_date_text));
				if (favourNumber > 0) {
					gButton.setText(favourNumber + "");
				} else {
					gButton.setText("赞");
				}
			}
			Map<String, Object> map = mWeiboList.get(position);
			map.put("favourNumber", favourNumber);
			map.put("isFavour", isFavour);
			mWeiboList.set(position, map);
			//发广播，通知客厅界面更新消息的状态
			Intent intent = new Intent();
			intent.putExtra("objectId", mObjectId);
			intent.putExtra("isFavour", isFavour);
			intent.putExtra("favourNumber", favourNumber);
			intent.setAction(MessageBoardActivity.ROOM_ITEM_STATE_ACTION_STRING);
			ct.sendBroadcast(intent);
		}

	}

	private void markFavour(int id, String objectId, final boolean toFavour) {
		String idd = "";
		if(id != -1){
			idd = String.valueOf(id);
		}
		new AsyncTask<String, Void, MessageBoardOperation>() {

			@Override
			protected MessageBoardOperation doInBackground(String... params) {
				try {
					if (toFavour) {
						return ((RenheApplication) ct.getApplicationContext()).getMessageBoardCommand().favourMessageBoard(
								params[0], params[1], params[2], params[3], ct);
					} else {
						return ((RenheApplication) ct.getApplicationContext()).getMessageBoardCommand().unFavourMessageBoard(
								params[0], params[1], params[3], ct);
					}
				} catch (Exception e) {
					return null;
				}
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(MessageBoardOperation result) {
				super.onPostExecute(result);
				if (null != result) {
					if (1 == result.getState()) {
					}
				} else {
					//ToastUtil.showNetworkError(ct);
				}
			}

		}.execute(((RenheApplication) ct.getApplicationContext()).getUserInfo().getAdSId(),
				((RenheApplication) ct.getApplicationContext()).getUserInfo().getSid(), idd, objectId);
	}

	@SuppressWarnings("unused")
	@SuppressLint("SimpleDateFormat")
	private void string2Date(String date, TextView dateTv) {
		long DAY = 24L * 60L * 60L * 1000L;
		DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date now = new Date();
		Date date2 = null;
		try {
			date2 = format1.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (null != date2) {
			long diff = now.getTime() - date2.getTime();
			dateTv.setText(DateUtil.formatToGroupTagByDay(ct, date2));
			if (diff > DAY * 7) {
				dateTv.setTextColor(ct.getResources().getColor(R.color.blog_item_time_text_old));
			} else {
				dateTv.setTextColor(ct.getResources().getColor(R.color.blog_item_time_text_new));
			}
		} else {
			dateTv.setText(date);
		}

	}

	protected SpannableString getSpannableString(String content, MessageBoardMember[] messageBoardMembers){

		SpannableString span = new SpannableString(content);
		char c = '@';
		char[] chars = content.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (c == chars[i]) {
				String itemName = "";
				int index;
				String tempContent = content.substring(i + 1, content.length());
				int at = tempContent.indexOf("@");
				int seleparator = tempContent.indexOf("//");
				int halfAngleColon = tempContent.indexOf(":");
				int fullAngleColon =  tempContent.indexOf("：");

				if (at < 0 && seleparator < 0 && halfAngleColon < 0 && fullAngleColon < 0) {
					if(tempContent.indexOf(" ") < 0){
						itemName = tempContent;
					}else{
						itemName = tempContent.substring(0, tempContent.indexOf(" "));
					}
				} else {
					if (halfAngleColon < 0) {
						if(fullAngleColon != -1){
							halfAngleColon = tempContent.indexOf("：");
						}else{
							halfAngleColon = 1000000;
						}
					}
					if (seleparator < 0) {
						seleparator = 1000000;
					}
					if (at < 0) {
						at = 1000000;
					}
					index = at < seleparator ? (at < halfAngleColon ? at : (seleparator < halfAngleColon ? seleparator : halfAngleColon))
							: (seleparator < halfAngleColon ? seleparator : halfAngleColon);
					itemName = tempContent.substring(0, index);
				}
				for (MessageBoardMember member : messageBoardMembers) {

					if (member.getMemberName().trim().equals(itemName.trim())) {
						span.setSpan(new MessageMemberSpanClick(member.getMemberId()), i,
								i + itemName.length() + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
						span.setSpan(new ForegroundColorSpan(ct.getResources().getColor(R.color.room_at_color)), i, i + itemName.length()
								+ 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
						
						break;
					}
				}
				getNoAtSpannedString(span, content);
			}
		}
		return span;
	}
//	protected void getSpannedString(SpannableString span,String teString) {
//		String[] zh = ct.getResources().getStringArray(R.array.face_zh);
//		String[] en = ct.getResources().getStringArray(R.array.face_en);
//		for(int i = 0; i < zh.length; i ++){
//			if(count(teString, zh[i]) != null){
//				int[] a = count(teString, zh[i]);
//				if(a != null && a.length > 0){
//					for(int f : a){
//						int id =ct.getResources().getIdentifier(en[i], "drawable", ct.getPackageName()); //name:图片的名，defType：资源类型（drawable，string。。。），defPackage:工程的包名
//						Drawable drawable=ct.getResources().getDrawable(id);
//						BitmapDrawable bd = (BitmapDrawable) drawable;
//	                    bd.setBounds(0, 0, bd.getIntrinsicWidth(),
//	                            bd.getIntrinsicHeight());
//	                    span.setSpan(new ImageSpan(bd), f, f + zh[i].length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//					}
//				}
//			}
//		}
//	}
	protected SpannableString getNoAtSpannedString(SpannableString span,String teString) {
		String[] zh = ct.getResources().getStringArray(R.array.face_zh);
		String[] en = ct.getResources().getStringArray(R.array.face_en);
		if(null == span){
			span = new SpannableString(teString);
		}
		for(int i = 0; i < zh.length; i ++){
			if(count(teString, zh[i]) != null){
				int[] a = count(teString, zh[i]);
				if(a != null && a.length > 0){
					for(int f : a){
						int id =ct.getResources().getIdentifier(en[i], "drawable", ct.getPackageName()); //name:图片的名，defType：资源类型（drawable，string。。。），defPackage:工程的包名
						Drawable drawable=ct.getResources().getDrawable(id);
						BitmapDrawable bd = (BitmapDrawable) drawable;
	                    bd.setBounds(0, 0, bd.getIntrinsicWidth() / 2,
	                            bd.getIntrinsicHeight() / 2);
	                    span.setSpan(new ImageSpan(bd), f, f + zh[i].length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
				}
			}
		}
		return span;
	}
	protected int[] count(String text,String sub){  
        int count =0, start =0;  
        while((start=text.indexOf(sub,start))>=0){  
            start += sub.length();
            count ++;  
        }  
        if(count == 0){
        	return null;
        }
        int a[] = new int[count];
        int count2 = 0;
        while((start=text.indexOf(sub,start))>=0){  
        	 a[count2] = start;
            start += sub.length();
            count2 ++;  
        }  
        return a;  
    } 
	class MessageMemberSpanClick extends ClickableSpan implements OnClickListener {
		String id;

		public MessageMemberSpanClick(String id) {
			this.id = id;
		}
		@Override
		public void updateDrawState(TextPaint ds) {
		    ds.setUnderlineText(false); //去掉下划线
		}
		@Override
		public void onClick(View v) {
			if(null != id && !"".equals(id)){
				Intent intent = new Intent(ct, MyHomeArchivesActivity.class);
				intent.putExtra(MyHomeArchivesActivity.FLAG_INTENT_DATA, id);
				ct.startActivity(intent);
			}
		}

	}
}
