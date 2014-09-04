  package com.itcalf.renhe.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.view.View;

import com.itcalf.renhe.R;
  /**
   * Title: TransferUrl2Drawable.java<br>
   * Description: <br>
   * Copyright (c) 人和网版权所有 2014    <br>
   * Create DateTime: 2014-6-13 上午11:14:09 <br>
   * @author wangning
   */
public class TransferUrl2Drawable {
	private Context context;
	public TransferUrl2Drawable(Context context){
		this.context = context;
	}
	public void transferUrl(SpannableString text) {
		Map<String, Integer[]> map = toHref(text.toString());
		//		SpannableStringBuilder style = new SpannableStringBuilder(text);
		Drawable drawable = context.getResources().getDrawable(R.drawable.url_bacg);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		for (Map.Entry<String, Integer[]> entry : map.entrySet()) {
			String url = entry.getKey();
			Integer[] index = entry.getValue();
			MyURLSpan myURLSpan = new MyURLSpan(url);
			if(index[0] != null && index[1] != null){
				text.setSpan(myURLSpan, index[0], index[1], Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
				text.setSpan(new ImageSpan(drawable), index[0], index[1], Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
			}
		}
		//		return style;
	}

	class MyURLSpan extends ClickableSpan {

		private String mUrl;

		MyURLSpan(String url) {
			mUrl = url;
		}

		@Override
		public void onClick(View widget) {
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUrl));
			context.startActivity(intent);
		}
	}

//	private static String regex = "(http[s]{0,1}|ftp)://[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?";
//	private static String regex = "(http://|ftp://|https://|www){0,1}[^\u4e00-\u9fa5\\s]*?\\.(com|net|cn|me|tw|fr)[^\u4e00-\u9fa5\\s]*";
	private static String regex = "\\(?\\b(http://|www[.])[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";

	public static Map<String, Integer[]> toHref(String title) {
		String url = "";
		Pattern pat = Pattern.compile(regex);
		Matcher mat = pat.matcher(title);
		Map<String, Integer[]> map = new HashMap<String, Integer[]>();
		while (mat.find()) {
			url = mat.group();
//			if((url.indexOf(":") == 0 || url.indexOf("：") == 0) && url.length() > 1){
//				url = url.substring(1, url.length()).trim();
//			}
			if (url.startsWith("(") && url.endsWith(")")) {
				url = url.substring(1, url.length() - 1);
		      }
			if (url.indexOf("http://") < 0)
				url = "http://" + url;
			map.put(url, new Integer[] { mat.start(), mat.end() });
		}
		return map;
	}
}

