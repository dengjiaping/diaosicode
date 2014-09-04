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
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.AnimationDrawable;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itcalf.renhe.Constants;
import com.itcalf.renhe.R;
import com.itcalf.renhe.RenheApplication;
import com.itcalf.renhe.cache.CacheManager;
import com.itcalf.renhe.context.archives.MyHomeArchivesActivity;
import com.itcalf.renhe.context.room.AddReplyActivity;
import com.itcalf.renhe.context.room.BlockMessageboardMemberTask;
import com.itcalf.renhe.context.room.ForwardMessageBoardActivity;
import com.itcalf.renhe.context.room.MessageBoardActivity;
import com.itcalf.renhe.context.room.TwitterShowMessageBoardActivity;
import com.itcalf.renhe.context.room.WebViewActivityForReport;
import com.itcalf.renhe.dto.MessageBoardOperation;
import com.itcalf.renhe.dto.MessageBoards.MessageBoardList.MessageBoardMember;
import com.itcalf.renhe.imageUtil.StandardImageXML;
import com.itcalf.renhe.utils.AsyncImageLoader;
import com.itcalf.renhe.utils.DateUtil;
import com.itcalf.renhe.utils.MatrixUtil;
import com.itcalf.renhe.utils.TransferUrl2Drawable;
import com.itcalf.renhe.view.TextViewFixTouchConsume;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Feature:扩展SimpleAdapter的BaseAdapter
 * Description:增加了图片缓存加载，控制特殊视图的显示
 * 
 * @author xp
 * 
 */

public class NWeiboAdapter extends BaseAdapter {
	private LayoutInflater flater;
	private String email;
	private Context ct;
	private ListView mListView;
	Map<String, Object> map;
	// 留言显示数据
	private List<Map<String, Object>> mWeiboList = new ArrayList<Map<String, Object>>();
	SharedPreferences userInfo;
	Editor editor;
	private Animation anim;
	private AnimationDrawable animationDrawable;
	private TransferUrl2Drawable transferUrl;
	private AlertDialog mAlertDialog;
	private int type = 1;
	public NWeiboAdapter(Context context, List<? extends Map<String, ?>> data, String email, ListView listView,int type) {
		this.flater = LayoutInflater.from(context);
		this.mWeiboList = (List<Map<String, Object>>) data;
		this.email = email;
		this.ct = context;
		this.mListView = listView;
		userInfo = ct.getSharedPreferences("setting_info", 0);
		editor = userInfo.edit();
		transferUrl = new TransferUrl2Drawable(ct);
		this.type = type;
	}

	@Override
	public int getCount() {
		return mWeiboList.size() != 0 ? mWeiboList.size() : 0;
	}

	@Override
	public Object getItem(int arg0) {
		return mWeiboList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = flater.inflate(R.layout.weibo_item_list, null);
			viewHolder = new ViewHolder();
			viewHolder.contentTv = (TextViewFixTouchConsume) convertView.findViewById(R.id.content_txt);
			viewHolder.rawContentTv = (TextViewFixTouchConsume) convertView.findViewById(R.id.rawcontent_txt);
			viewHolder.nameTv = (TextView) convertView.findViewById(R.id.username_txt);
			viewHolder.avatarIv = (ImageView) convertView.findViewById(R.id.avatar_img);
			viewHolder.dateTv = (TextView) convertView.findViewById(R.id.datetime_txt);
			viewHolder.fromTv = (TextView) convertView.findViewById(R.id.client_txt);
			viewHolder.thumbnailPic = (ImageView) convertView.findViewById(R.id.thumbnailPic);
			viewHolder.forwardThumbnailPic = (ImageView) convertView.findViewById(R.id.forwardThumbnailPic);
			viewHolder.forwardLl = (LinearLayout) convertView.findViewById(R.id.room_item_reforward_ll);
			viewHolder.replyLl = (LinearLayout) convertView.findViewById(R.id.room_item_reply_ll);
			viewHolder.goodLl = (LinearLayout) convertView.findViewById(R.id.room_item_good_ll);
			viewHolder.goodButton = (Button) convertView.findViewById(R.id.room_item_good);
			viewHolder.replyButton = (Button) convertView.findViewById(R.id.room_item_reply);
			viewHolder.rawcontentlayout = (LinearLayout) convertView.findViewById(R.id.rawcontentlayout);
			viewHolder.mCompanyTv = (TextView) convertView.findViewById(R.id.companyTv);
			viewHolder.mIndustryTv = (TextView) convertView.findViewById(R.id.industryTv);
			viewHolder.vipIv = (ImageView) convertView.findViewById(R.id.vipImage);
			viewHolder.realNameIv = (ImageView) convertView.findViewById(R.id.realnameImage);
			viewHolder.goodIv = (ImageView) convertView.findViewById(R.id.goodiv);
			viewHolder.arrowIv = (ImageView) convertView.findViewById(R.id.arrow_iv);
			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		map = mWeiboList.get(position);
		if (null != map) {
			String userId = (String) map.get("id");
			String userface = (String) map.get("userface");
			Object replyObj = map.get("reply");
			Object dateObject = map.get("datetime");
			String content = "";
			if(null != map.get("content")){
				content = (String)map.get("content");
			}
			String mrawContent = "";
			if(null != map.get("rawcontent")){
				mrawContent = (String) map.get("rawcontent");
			}
			Object messageBoardMembersObject = map.get("messageBoardMember");
			Object forwardMessageBoardMembersObject = map.get("forwardMessageMember");
			String name = (String) map.get("username");
			String client = (String) map.get("client");
			String datetime = (String) map.get("datetime");
			String company = (String) map.get("senderCompany");
			String title = (String) map.get("senderTitle");
			String industry = (String) map.get("senderIndustry");
			String location = (String) map.get("senderLocation");
			Object accountObject = map.get("accountType");
			Object realNameObject = map.get("isRealName");
			int accountType = 0;
			boolean isRealName = false;
			if (null != accountObject) {
				accountType = (Integer) map.get("accountType");//账号vip等级类型：0：普通会员；1：VIP会员；2：黄金会员；3：铂金会员
			}
			if (null != realNameObject) {
				isRealName = (Boolean) map.get("isRealName");//是否是实名认证的会员
			}
			viewHolder.nameTv.setText(name);
			viewHolder.fromTv.setText(client);
			viewHolder.dateTv.setText(datetime);
			if (null != title) {
				viewHolder.mCompanyTv.setText(title.trim() + " ");
			}
			if (null != company) {
				viewHolder.mCompanyTv.setText(viewHolder.mCompanyTv.getText().toString() + company.trim());
			}
			if (null != location) {
				viewHolder.mIndustryTv.setText(location.trim() + " ");
			}
			if (null != industry) {
				viewHolder.mIndustryTv.setText(viewHolder.mIndustryTv.getText().toString() + industry.trim());
			}
			switch (accountType) {
			case 0:
				viewHolder.vipIv.setVisibility(View.GONE);
				break;
			case 1:
				viewHolder.vipIv.setVisibility(View.VISIBLE);
				viewHolder.vipIv.setImageResource(R.drawable.vip_1);
				break;
			case 2:
				viewHolder.vipIv.setVisibility(View.VISIBLE);
				viewHolder.vipIv.setImageResource(R.drawable.vip_2);
				break;
			case 3:
				viewHolder.vipIv.setVisibility(View.VISIBLE);
				viewHolder.vipIv.setImageResource(R.drawable.vip_3);
				break;

			default:
				break;
			}
			if (isRealName && accountType <= 0) {
				viewHolder.realNameIv.setVisibility(View.VISIBLE);
				viewHolder.realNameIv.setImageResource(R.drawable.realname);
			} else {
				viewHolder.realNameIv.setVisibility(View.GONE);
			}
			if (content != null) {
				SpannableString span;
				if (null != messageBoardMembersObject) {
					MessageBoardMember[] messageBoardMembers = (MessageBoardMember[]) messageBoardMembersObject;
					if (messageBoardMembers.length > 0) {
						span = getSpannableString(content, messageBoardMembers);
					} else {
						span = getNoAtSpannedString(null, content);
					}
				} else {
					span = getNoAtSpannedString(null, content);
				}
				//				SpannableStringBuilder style = transferUrl(content);
				transferUrl.transferUrl(span);
				viewHolder.contentTv.setText(span);
				viewHolder.contentTv.setMovementMethod(TextViewFixTouchConsume.LocalLinkMovementMethod.getInstance());

			}

			if (mrawContent != null) {
				SpannableString span;
				if (null != forwardMessageBoardMembersObject) {
					MessageBoardMember[] forwardMessageBoardMembers = (MessageBoardMember[]) forwardMessageBoardMembersObject;
					if (forwardMessageBoardMembers.length > 0) {
						span = getSpannableString(mrawContent, forwardMessageBoardMembers);
					} else {
						span = getNoAtSpannedString(null, mrawContent);
					}
				} else {
					span = getNoAtSpannedString(null, mrawContent);
				}
				transferUrl.transferUrl(span);
				viewHolder.rawContentTv.setText(span);
				viewHolder.rawContentTv.setMovementMethod(TextViewFixTouchConsume.LocalLinkMovementMethod.getInstance());

			}
			if (null != dateObject) {
				String date = dateObject.toString();
				string2Date(date, viewHolder.dateTv);
			}
			int replyNum = 0;
			if (null != replyObj) {
				replyNum = (Integer) replyObj;
			}

			if (null != viewHolder.replyButton) {
				if (replyNum >= 1) {
					viewHolder.replyButton.setText(replyNum + "");
				} else {
					viewHolder.replyButton.setText(ct.getResources().getString(R.string.room_reply));
				}
			}
			if (!TextUtils.isEmpty(userface) && null != viewHolder.avatarIv) {
				viewHolder.avatarIv.setTag(userface + position);
				final int pos = position;
				//								SimpleAsyncImageLoad.loadDrawable(userId, userface, email, 70, 70, ct, new SimpleAsyncImageLoad.ImageCallback() {
				//				
				//									@Override
				//									public void imageLoaded(Drawable imageDrawable, String imageUrl) {
				//										ImageView imageViewByTag = (ImageView) mListView.findViewWithTag(imageUrl + pos);
				//										if (imageViewByTag != null) {
				//											imageViewByTag.setImageDrawable(imageDrawable);
				//										}
				//									}
				//								});

				//使用ImageCache工具类解决listview滑动时，头像显示错乱的问题，不过导致有的头像加载不出					
				//				viewHolder.avatarIv.setTag(userface);
				//				CacheManager.IMAGE_CACHE.initData(ct, "renhe_imagecache");
				//				CacheManager.IMAGE_CACHE.setContext(ct);
				//				CacheManager.IMAGE_CACHE.setCacheFolder(CacheManager.DEFAULT_IMAGECACHE_FOLDER);
				//				if (!CacheManager.IMAGE_CACHE.get(userface, viewHolder.avatarIv)) {
				//					viewHolder.avatarIv.setImageDrawable(ct.getResources().getDrawable(R.drawable.avatar));
				//				}
				ImageLoader imageLoader = ImageLoader.getInstance();
				try {
					imageLoader.displayImage(userface, viewHolder.avatarIv, CacheManager.options,
							CacheManager.animateFirstDisplayListener);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (position == 0) {
					this.notifyDataSetChanged();
				}
			}
			String bmiddlePic = (String) map.get("bmiddlePic");
			if (null != viewHolder.thumbnailPic) {
				viewHolder.thumbnailPic.setOnClickListener(new clickPic(bmiddlePic));
			}
			Object obj1 = map.get("thumbnailPic");
			if (obj1 != null && !obj1.toString().equals("") && null != viewHolder.thumbnailPic) {
				if (userInfo.getBoolean("roomshowpic", true)) {
					viewHolder.thumbnailPic.setVisibility(View.VISIBLE);
					AsyncImageLoader
							.getInstance()
							.populateData(ct, email, false, true, false)
							.loadPic(viewHolder.thumbnailPic, null, obj1.toString(), null, null,
									MatrixUtil.getPostMatrix((Activity) ct), true);
					//					ImageLoader imageLoader = ImageLoader.getInstance();		
					//					try {
					//						imageLoader.displayImage(obj1.toString(), viewHolder.thumbnailPic, CacheManager.options, CacheManager.animateFirstDisplayListener);
					//					} catch (Exception e) {
					//						e.printStackTrace();
					//					}
				} else {
					// 不显示图片 ，先处理为影藏
					viewHolder.thumbnailPic.setVisibility(View.GONE);
				}
			} else if (viewHolder.thumbnailPic != null) {
				viewHolder.thumbnailPic.setVisibility(View.GONE);
			}

			String forwardBmiddlePic = (String) map.get("forwardBmiddlePic");
			if (null != viewHolder.forwardThumbnailPic) {
				viewHolder.forwardThumbnailPic.setOnClickListener(new clickPic(forwardBmiddlePic));
			}
			Object obj2 = map.get("forwardThumbnailPic");
			if (obj2 != null && !obj2.toString().equals("") && null != viewHolder.forwardThumbnailPic) {
				if (userInfo.getBoolean("roomshowpic", true)) {
					viewHolder.forwardThumbnailPic.setVisibility(View.VISIBLE);
					AsyncImageLoader
							.getInstance()
							.populateData(ct, email, false, true, false)
							.loadPic(viewHolder.forwardThumbnailPic, null, obj2.toString(), null, null,
									MatrixUtil.getPostMatrix((Activity) ct), true);
					//					ImageLoader imageLoader = ImageLoader.getInstance();		
					//					try {
					//						imageLoader.displayImage(obj2.toString(), viewHolder.forwardThumbnailPic, CacheManager.options, CacheManager.animateFirstDisplayListener);
					//					} catch (Exception e) {
					//						e.printStackTrace();
					//					}
				} else {
					// 不显示图片 ，先处理为影藏
					viewHolder.forwardThumbnailPic.setVisibility(View.GONE);
				}

			} else if (viewHolder.forwardThumbnailPic != null) {
				viewHolder.forwardThumbnailPic.setVisibility(View.GONE);
			}

			String rawContent = (String) map.get("rawcontent");
			if (null != viewHolder.rawcontentlayout) {
				if (null == rawContent) {
					viewHolder.rawcontentlayout.setVisibility(View.GONE);
				} else {
					viewHolder.rawcontentlayout.setVisibility(View.VISIBLE);
				}
			}
			final int pos = position;
			if (null != viewHolder.forwardLl) {
				viewHolder.forwardLl.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						String rawcontent = "";
						String username = "";
						String realrawContent = "";
						Map<String, Object> map = (Map<String, Object>) getItem(pos);
						Object rawcontentObj = map.get("content");
						Object sendernameObj = map.get("username");
						String objectId = (String) map.get("objectId");
						Object realrawContentObj = (String) map.get("rawcontent");
						if (null != rawcontentObj) {
							rawcontent = rawcontentObj.toString();
						}
						if (null != sendernameObj) {
							username = sendernameObj.toString();
						}
						if (null != realrawContentObj) {
							realrawContent = realrawContentObj.toString();
						}
						if (null != objectId) {
							Bundle bundle = new Bundle();
							bundle.putString("objectId", objectId);
							if (!TextUtils.isEmpty(rawcontent) && !TextUtils.isEmpty(realrawContent)) {
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
			if (null != viewHolder.replyButton) {
				viewHolder.replyLl.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Map<String, Object> map = mWeiboList.get(pos);
						Object replyObj = map.get("reply");
						int mReplyNum = 0;
						if (null != replyObj) {
							mReplyNum = (Integer) replyObj;
						}
//						String mObjectId = (String) map.get("objectId");
//						String mId = "";
//						if (null != map.get("Id")) {
//							mId = map.get("Id").toString();
//						}
//						if (null != mId && null != mObjectId) {
//							Bundle bundle = new Bundle();
//							bundle.putString("objectId", mObjectId);
//							bundle.putString("id", mId);
//							Intent intent = new Intent(ct, AddReplyActivity.class);
//							intent.putExtras(bundle);
//							((Activity) ct).startActivity(intent);
//						}
						//如何想直接进评论界面，解除上面的注释
						if (mReplyNum > 0) {
							String objectId = (String) map.get("objectId");
							String sid = (String) map.get("sid");
							Object favourNumberObj = map.get("favourNumber");
							Object isFavourObj = map.get("isFavour");
							Bundle bundle = new Bundle();
							bundle.putString("sid", sid);
							bundle.putString("objectId", objectId);
							if (isFavourObj != null) {
								bundle.putBoolean("isFavour", (Boolean) isFavourObj);
							}
							if (favourNumberObj != null) {
								bundle.putInt("favourNumber", (Integer) favourNumberObj);
							}
							bundle.putInt("replyNum", mReplyNum);
							bundle.putBoolean("isToReplylist", true);
							Intent intent = new Intent(ct, TwitterShowMessageBoardActivity.class);
							intent.putExtras(bundle);
							((Activity) ct).startActivity(intent);
							if(null != ((Activity) ct).getParent()){
								((Activity) ct).getParent().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
							}
						} else {
							String mObjectId = (String) map.get("objectId");
							String mId = "";
							if (null != map.get("Id")) {
								mId = map.get("Id").toString();
							}
							if (null != mId && null != mObjectId) {
								Bundle bundle = new Bundle();
								bundle.putString("objectId", mObjectId);
								bundle.putString("id", mId);
								Intent intent = new Intent(ct, AddReplyActivity.class);
								intent.putExtras(bundle);
								((Activity) ct).startActivity(intent);
								if(null != ((Activity) ct).getParent()){
									((Activity) ct).getParent().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
								}
							}
						}
						
					}
				});
			}
			// 监听赞按钮单击事件
			Object favourNumberObj = map.get("favourNumber");
			Object isFavourObj = map.get("isFavour");
			int favourNumber = 0;
			boolean isFavour = false;
			if (null != viewHolder.goodLl && null != viewHolder.goodButton) {

				if (null != favourNumberObj) {
					favourNumber = (Integer) favourNumberObj;
					if (favourNumber > 0) {
						viewHolder.goodButton.setText(favourNumber + "");

					} else {
						viewHolder.goodButton.setText("赞");
					}
					if (null != isFavourObj) {
						isFavour = (Boolean) isFavourObj;
						if (isFavour) {
							//							viewHolder.goodButton.setCompoundDrawablesWithIntrinsicBounds(
							//									ct.getResources().getDrawable(R.drawable.good), null, null, null);
								viewHolder.goodIv.setImageResource(R.drawable.good);
								viewHolder.goodButton.setTextColor(ct.getResources().getColor(R.color.room_good_textcolor));
						} else {
							//							viewHolder.goodButton.setCompoundDrawablesWithIntrinsicBounds(
							//									ct.getResources().getDrawable(R.drawable.good_p), null, null, null);
							viewHolder.goodIv.setImageResource(R.drawable.good_p);
							viewHolder.goodButton.setTextColor(ct.getResources().getColor(R.color.blog_item_date_text));
						}
					}
				} else {
					//					viewHolder.goodButton.setCompoundDrawablesWithIntrinsicBounds(ct.getResources()
					//							.getDrawable(R.drawable.good_p), null, null, null);
					viewHolder.goodIv.setImageResource(R.drawable.good_p);
					viewHolder.goodButton.setTextColor(ct.getResources().getColor(R.color.blog_item_date_text));
				}
				viewHolder.goodLl.setOnClickListener(new goodClick(viewHolder.goodLl.getChildAt(1), position, favourNumber,
						isFavour, viewHolder.goodIv));
			}
			viewHolder.arrowIv.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					Map<String, Object> map = mWeiboList.get(pos);
					String mObjectId = (String) map.get("objectId");
					String mId = "";
					String senderSid = "";
					if (null != map.get("Id")) {
						mId = map.get("Id").toString();
					}
					if (null != map.get("sid")) {
						senderSid = map.get("sid").toString();
					}
					if (null != mId && null != mObjectId) {
						createDialog(ct,RenheApplication.getInstance().getUserInfo().getSid(),mId,mObjectId,senderSid);
					}
				}
			});
			
			//点击转发内容区域，进入转发内容详情
//			if(null != viewHolder.rawcontentlayout && isForwardRenhe){
				viewHolder.rawcontentlayout.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						Map<String, Object> map = mWeiboList.get(pos);
						boolean isForwardRenhe = false;
						if(null != map.get("isForwardRenhe")){
							isForwardRenhe = (Boolean)map.get("isForwardRenhe");
						}
						if(isForwardRenhe && null != map.get("forwardMessageBoardObjectId")){
							String objectId = (String)map.get("forwardMessageBoardObjectId");
							Bundle bundle = new Bundle();
							bundle.putString("objectId", objectId);
							Intent intent = new Intent(ct, TwitterShowMessageBoardActivity.class);
							intent.putExtras(bundle);
							((Activity) ct).startActivity(intent);
							if(null != ((Activity) ct).getParent()){
								((Activity) ct).getParent().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
							}
						}
					}
				});
//			}
		}

		return convertView;
	}

	public static class ViewHolder {
		public TextViewFixTouchConsume contentTv;
		public TextViewFixTouchConsume rawContentTv;
		public TextView nameTv;
		public ImageView avatarIv;
		public TextView dateTv;
		public TextView fromTv;
		public ImageView thumbnailPic;
		public ImageView forwardThumbnailPic;
		public LinearLayout forwardLl;
		public LinearLayout replyLl;
		public LinearLayout goodLl;
		public Button goodButton;
		public Button replyButton;
		public LinearLayout rawcontentlayout;
		public TextView mCompanyTv;
		public TextView mIndustryTv;
		public ImageView vipIv;
		public ImageView realNameIv;
		public ImageView goodIv;
		public ImageView arrowIv;
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
		ImageView goodIV = null;
		int position;
		int favourNumber;
		boolean isFavour;

		public goodClick(View view, int position, int favourNumber, boolean isFavour, ImageView goodIv) {
			this.goodButton = view;
			this.position = position;
			this.favourNumber = favourNumber;
			this.isFavour = isFavour;
			this.goodIV = new ImageView(ct);
			this.goodIV = goodIv;
		}

		@Override
		public void onClick(View arg0) {
			LinearLayout layout = (LinearLayout) arg0;
			Button gButton = (Button) layout.getChildAt(1);

			//			String mObjectId = (String) map.get("objectId");
			//			int mId = (Integer)map.get("Id");
			String mObjectId = (String) mWeiboList.get(position).get("objectId");
			int mId = -1;
			if (null != mWeiboList.get(position).get("Id")) {
				mId = (Integer) mWeiboList.get(position).get("Id");
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
				//点赞的动画

				//				gButton.setCompoundDrawablesWithIntrinsicBounds(ct.getResources().getDrawable(R.drawable.good), null, null, null);
//				goodIV.setImageResource(R.anim.good_animation);
//				animationDrawable = (AnimationDrawable) goodIV.getDrawable();
//				animationDrawable.start();
				goodIV.clearAnimation();
				Animation animation = AnimationUtils.loadAnimation(ct, R.anim.good_scale);
				goodIV.startAnimation(animation);
				
				gButton.setTextColor(ct.getResources().getColor(R.color.room_good_textcolor));
				if (favourNumber > 0) {
					gButton.setText(favourNumber + "");
				} else {
					gButton.setText("赞");
				}
			} else {
				goodIV.clearAnimation();
				//				gButton.setCompoundDrawablesWithIntrinsicBounds(ct.getResources().getDrawable(R.drawable.good_p), null, null,
				//						null);
				goodIV.setImageResource(R.drawable.good_p);
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
			intent.putExtra("isNeedUpdateView", false);
			intent.setAction(MessageBoardActivity.ROOM_ITEM_STATE_ACTION_STRING);
			ct.sendBroadcast(intent);
		}

	}

	private void markFavour(int id, String objectId, final boolean toFavour) {
		String idd = "";
		if (id != -1) {
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

	protected SpannableString getSpannableString(String content, MessageBoardMember[] messageBoardMembers) {
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
				int fullAngleColon = tempContent.indexOf("：");

				if (at < 0 && seleparator < 0 && halfAngleColon < 0 && fullAngleColon < 0) {
					if (tempContent.indexOf(" ") < 0) {
						itemName = tempContent;
					} else {
						itemName = tempContent.substring(0, tempContent.indexOf(" "));
					}
				} else {
					if (halfAngleColon < 0) {
						if (fullAngleColon != -1) {
							halfAngleColon = tempContent.indexOf("：");
						} else {
							halfAngleColon = 1000000;
						}
					}
					if (seleparator < 0) {
						seleparator = 1000000;
					}
					if (at < 0) {
						at = 1000000;
					}

					index = at < seleparator ? (at < halfAngleColon ? at : (seleparator < halfAngleColon ? seleparator
							: halfAngleColon)) : (seleparator < halfAngleColon ? seleparator : halfAngleColon);
					int blankindex = tempContent.indexOf(" ");
					if (blankindex != -1) {
						index = index > blankindex ? blankindex : index;
					}
					itemName = tempContent.substring(0, index);
				}
				for (MessageBoardMember member : messageBoardMembers) {

					if (member.getMemberName().trim().equals(itemName.trim())) {
						span.setSpan(new MessageMemberSpanClick(member.getMemberId()), i, i + itemName.length() + 1,
								Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
						span.setSpan(new ForegroundColorSpan(ct.getResources().getColor(R.color.room_at_color)), i,
								i + itemName.length() + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

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
	protected SpannableString getNoAtSpannedString(SpannableString span, String teString) {
		String[] zh = ct.getResources().getStringArray(R.array.face_zh);
		String[] en = ct.getResources().getStringArray(R.array.face_en);
		if (null == span) {
			span = new SpannableString(teString);
		}
		for (int i = 0; i < zh.length; i++) {
			if (count(teString, zh[i]) != null) {
				int[] a = count(teString, zh[i]);
				if (a != null && a.length > 0) {
					for (int f : a) {
						int id = ct.getResources().getIdentifier(en[i], "drawable", ct.getPackageName()); //name:图片的名，defType：资源类型（drawable，string。。。），defPackage:工程的包名
						Drawable drawable = ct.getResources().getDrawable(id);
						BitmapDrawable bd = (BitmapDrawable) drawable;
						bd.setBounds(0, 0, bd.getIntrinsicWidth() / 2, bd.getIntrinsicHeight() / 2);
						span.setSpan(new ImageSpan(bd), f, f + zh[i].length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
				}
			}
		}
		return span;
	}

	protected int[] count(String text, String sub) {
		int count = 0, start = 0;
		while ((start = text.indexOf(sub, start)) >= 0) {
			start += sub.length();
			count++;
		}
		if (count == 0) {
			return null;
		}
		int a[] = new int[count];
		int count2 = 0;
		while ((start = text.indexOf(sub, start)) >= 0) {
			a[count2] = start;
			start += sub.length();
			count2++;
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
			if (null != id && !"".equals(id)) {
				Intent intent = new Intent(ct, MyHomeArchivesActivity.class);
				intent.putExtra(MyHomeArchivesActivity.FLAG_INTENT_DATA, id);
				ct.startActivity(intent);
			}
		}

	}
	public void createDialog(Context context,final String userId,final String msgId,final String msgObjectId,final String senderSid) {
		RelativeLayout view = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.report_shield_dialog, null);

		Builder mDialog = new AlertDialog.Builder(context);
		//		mDialog.setView(view,0,0,0,0);
		LinearLayout reportLl = (LinearLayout) view.findViewById(R.id.reportLl);
		LinearLayout shieldLl = (LinearLayout) view.findViewById(R.id.shieldLl);
		mAlertDialog = mDialog.create();
		mAlertDialog.setView(view, 0, 0, 0, 0);
		mAlertDialog.setCanceledOnTouchOutside(true);
		mAlertDialog.show();
		reportLl.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(null != mAlertDialog){
					mAlertDialog.dismiss();
				}
				Intent intent = new Intent(ct, WebViewActivityForReport.class);
				intent.putExtra("sid", userId);
				intent.putExtra("type", 1);
				intent.putExtra("entityId", msgId);
				intent.putExtra("entityObjectId", msgObjectId);
				ct.startActivity(intent);
				if(null != ((Activity) ct).getParent()){
					((Activity) ct).getParent().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
				}
			}
		});
		shieldLl.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(null != mAlertDialog){
					mAlertDialog.dismiss();
				}
				new BlockMessageboardMemberTask(ct){
					public void doPre() {};
					public void doPost(MessageBoardOperation result) {
						if(null != result){
							if(result.getState() == 1){
								Intent intent = new Intent(MessageBoardActivity.ROOM_REFRESH_AFTER_SHIELD);
								intent.putExtra("senderSid", senderSid);
								intent.putExtra("type", type+"");
								ct.sendBroadcast(intent);
								if(Constants.renhe_log){
									Log.e("屏蔽", "屏蔽成功--senderSid--"+senderSid);
								}
							}else if(result.getState() == -3){
								if(Constants.renhe_log){
								Log.e("屏蔽", "blockedMemberSId不能为空--senderSid--"+senderSid);
								}
							}
						}
					};
				}.execute(RenheApplication.getInstance().getUserInfo().getSid(), RenheApplication.getInstance().getUserInfo().getAdSId(),senderSid);
			}
		});
	}
}
