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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.itcalf.renhe.R;
import com.itcalf.renhe.RenheApplication;
import com.itcalf.renhe.cache.CacheManager;
import com.itcalf.renhe.context.archives.MyHomeArchivesActivity;
import com.itcalf.renhe.context.template.BaseActivity;
import com.itcalf.renhe.utils.DateUtil;
import com.itcalf.renhe.utils.SimpleAsyncImageLoad;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 
 * @author 王宁
 * 
 */
public class ReplyListAdapter extends SimpleAdapter {
	private Context ct;
	
	TextView infoTv;
	TextView timeTv;
	 private ListView listView;
	Map<String, Object> map;
	// 留言显示数据
	private List<Map<String, Object>> mWeiboList = new ArrayList<Map<String, Object>>();

	@SuppressWarnings("unchecked")
	public ReplyListAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to,ListView listView) {
		super(context, data, resource, from, to);
		this.mWeiboList = (List<Map<String, Object>>) data;
		this.ct = context;
		this.listView = listView;
	}

	@SuppressLint("SimpleDateFormat")
	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		if (null != view) {
			ImageView avartar = (ImageView)view.findViewById(R.id.avatar_img);
			infoTv = (TextView)view.findViewById(R.id.infoTv);
			map = (Map<String, Object>) getItem(position);
			final String senderSid = (String) map.get("sid");
			if (null != map) {
				String userface = (String)map.get("userFace");
//				avartar.setTag(userface + position);
				if (null != userface && !TextUtils.isEmpty(userface)) {
					avartar.setTag(userface + position);
					final int pos = position;
					SimpleAsyncImageLoad.loadDrawable(null, userface, RenheApplication.getInstance().getUserInfo().getEmail(), 70, 70, ct, new SimpleAsyncImageLoad.ImageCallback() {

						@Override
						public void imageLoaded(Drawable imageDrawable, String imageUrl) {
							ImageView imageViewByTag = (ImageView) listView.findViewWithTag(imageUrl + pos);
							if (imageViewByTag != null) {
								imageViewByTag.setImageDrawable(imageDrawable);
							}
						}
					});
					//方法2
//					avartar.setTag(userface);
//					CacheManager.IMAGE_CACHE.initData(ct, "renhe_imagecache");
//					CacheManager.IMAGE_CACHE.setContext(ct);
//					CacheManager.IMAGE_CACHE.setCacheFolder(CacheManager.DEFAULT_IMAGECACHE_FOLDER);
//					if (!CacheManager.IMAGE_CACHE.get(userface, avartar)) {
//						((ImageView)avartar).setImageDrawable(ct.getResources().getDrawable(R.drawable.avatar));
//					}
					//方法3
//					ImageLoader imageLoader = ImageLoader.getInstance();		
//					try {
//						imageLoader.displayImage(userface, (ImageView)avartar, CacheManager.options,  CacheManager.animateFirstDisplayListener);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
				}
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
					vipIv.setImageResource(R.drawable.reply_item_vip1);
					break;
				case 2:
					vipIv.setVisibility(View.VISIBLE);
					vipIv.setImageResource(R.drawable.reply_item_vip2);
					break;
				case 3:
					vipIv.setVisibility(View.VISIBLE);
					vipIv.setImageResource(R.drawable.reply_item_vip3);
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
				
				
				String info = ((Spanned)map.get("infoTv")).toString();
				info = ToDBC(info);
				infoTv.setText(getSpannedString(info));
//				infoTv.setText(info);
				timeTv = (TextView)view.findViewById(R.id.timeTv);
				String time = map.get("timeTv").toString();
				DateFormat format1 = new SimpleDateFormat("MM-dd HH:mm");
				try {
					Date date = format1.parse(time);
					SimpleDateFormat dateFormat = new SimpleDateFormat(ct.getString(R.string.readable_date_md_hm));
					time = dateFormat.format(date);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
				timeTv.setText(time);
				
				avartar.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(ct, MyHomeArchivesActivity.class);
						intent.putExtra(MyHomeArchivesActivity.FLAG_INTENT_DATA, senderSid);
						ct.startActivity(intent);
						((BaseActivity)ct).overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
					}
				});
			}
		}
		return view;
	}
	protected SpannableString getSpannedString(String teString) {
		String[] zh = ct.getResources().getStringArray(R.array.face_zh);
		String[] en = ct.getResources().getStringArray(R.array.face_en);
		SpannableString spannableString = new SpannableString(teString);
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
						spannableString.setSpan(new ImageSpan(bd), f, f + zh[i].length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
				}
			}
		}
		return spannableString;
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
	public static String ToDBC(String input) {
		   char[] c = input.toCharArray();
		   for (int i = 0; i< c.length; i++) {
		       if (c[i] == 12288) {
		         c[i] = (char) 32;
		         continue;
		       }if (c[i]> 65280&& c[i]< 65375)
		          c[i] = (char) (c[i] - 65248);
		       }
		   return new String(c);
		}
}