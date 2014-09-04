package com.itcalf.renhe.adapter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.itcalf.renhe.R;
import com.itcalf.renhe.cache.CacheManager;
import com.itcalf.renhe.context.archives.MyHomeArchivesActivity;
import com.itcalf.renhe.dto.MessageBoards.MessageBoardList.MessageBoardMember;
import com.itcalf.renhe.utils.DateUtil;
import com.itcalf.renhe.view.TextViewFixTouchConsume;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 消息提醒
 * @author wangning
 * 
 */
public class NewsWeiboAdapter extends SimpleAdapter {
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
	public NewsWeiboAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to,
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
				int type = (Integer) map.get("type");
				String userface = (String) map.get("userface");
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
				String content = (String) map.get("replyContent");
				String mrawContent = (String) map.get("sourceContent");
				if(content == null){
					content = "";
				}
				if(type == 1 || type == 2 || type == 3){//回复
					contentTv.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
				}else if(type == 6){//赞
					content = "赞了您的客厅";
					contentTv.setCompoundDrawablesWithIntrinsicBounds(ct.getResources().getDrawable(R.drawable.good), null, null, null);
					contentTv.setCompoundDrawablePadding(10);
				}else if(type == 5){//@
					contentTv.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
				}else if(type == 4){//转发
					contentTv.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
				}
				if(null != content && !"".equals(content)){
					SpannableString spannableString = getNoAtSpannedString(null,content);
					contentTv.setText(spannableString);
				}
				if(null != mrawContent && !"".equals(mrawContent)){
					SpannableString spannableString2 = getNoAtSpannedString(null,mrawContent);
					rawContentTv.setText(spannableString2);	
				}
				TextView dateTv = (TextView) view.findViewById(R.id.datetime_txt);
				if (null != dateObject) {
					String date = dateObject.toString();
					string2Date(date, dateTv);
				}
				View avatarView = view.findViewById(R.id.avatar_img);
				if (!TextUtils.isEmpty(userface) && null != avatarView) {
//					avatarView.setTag(userface + position);
//					final int pos = position;
//					SimpleAsyncImageLoad.loadDrawable("", userface, email, 70, 70, ct,
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
					if(position == 0){
						this.notifyDataSetChanged();
					}
				}
			}
		}
		return view;
	}




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
				dateTv.setTextColor(ct.getResources().getColor(R.color.notice_list_sourcecontent_color));
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
