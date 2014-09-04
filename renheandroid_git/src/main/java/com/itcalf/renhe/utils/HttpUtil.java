package com.itcalf.renhe.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.itcalf.renhe.Constants;
import com.itcalf.renhe.dto.UploadAvatar;

/**
 * HTTP 接口类
 * 
 * @author xp
 * 
 */
public class HttpUtil {

	/**
	 * Http 请求的公共接口
	 * 
	 * @param reqParams
	 *            请求参数
	 * @param clazz
	 *            请求返回的结果对象类型
	 * @return 封装好的JSON对象
	 * @throws Exception
	 */
	public static Object doHttpRequest(String webService, Map<String, Object> reqParams, Class<?> clazz, Context context)
			throws Exception {
		StringBuffer logBuffer = new StringBuffer();
		if(null != reqParams && reqParams.size() > 0){
			for(Map.Entry<String, Object> entry: reqParams.entrySet()){
				String key = entry.getKey().toString();
				String value = entry.getValue().toString();
				logBuffer.append(key+":"+value+" ");
			}
			if(Constants.renhe_log){
				Log.d(webService+"", logBuffer+"");
			}
		}
		HttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "Custom user agent");
		if (context != null) {
			ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = connectivityManager.getActiveNetworkInfo();
			if (info != null && info.getTypeName().equalsIgnoreCase("MOBILE")
					&& !TextUtils.isEmpty(android.net.Proxy.getDefaultHost())) {
				HttpHost proxy = new HttpHost("10.0.0.172", 80);
				client.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY, proxy);
			}
		}
		HttpPost post = getHttpPost(webService, reqParams);
//		org.apache.http.Header[] headers = post.getAllHeaders();
		Object result = null;
		try {
			HttpResponse resp = client.execute(post);
			if (resp.getStatusLine().getStatusCode() == 200) {
				HttpEntity resEntityGet = resp.getEntity();
				if (null != resEntityGet) {
					String rs = EntityUtils.toString(resp.getEntity());
					if(Constants.renhe_log){
						Log.d(webService+"——response", rs+"");
					}
					if (null != rs) {
						Gson gson = new GsonBuilder().create();
						result = gson.fromJson(rs, clazz);
						gson = null;
					}
					rs = null;
				}
				resEntityGet = null;
			}
			resp = null;
		} catch (Exception e) {
			throw e;
		} finally {
			reqParams = null;
			post = null;
			client = null;
		}
		
		return result;
	}

	/**
	 * 获取HttpPost
	 * 
	 * @param reqParams
	 *            请求的参数
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private static HttpPost getHttpPost(String url, Map<String, Object> reqParams) throws UnsupportedEncodingException {
		HttpPost post = new HttpPost(url);
		//放置参数
		if (null != reqParams) {
			List<NameValuePair> lNameValuePairs = new ArrayList<NameValuePair>(2);
			Set<Entry<String, Object>> set = reqParams.entrySet();
			Iterator<Entry<String, Object>> it = set.iterator();
			while (it.hasNext()) {
				Map.Entry<java.lang.String, java.lang.Object> entry = (Map.Entry<java.lang.String, java.lang.Object>) it.next();
				if(entry.getValue() instanceof String[]){
					for(String itemValue: (String[]) entry.getValue()){
						lNameValuePairs.add(new BasicNameValuePair(entry.getKey(), itemValue));
					}
				}else if(entry.getValue() instanceof int[]){
					for(int itemValue: (int[]) entry.getValue()){
						lNameValuePairs.add(new BasicNameValuePair(entry.getKey(), String.valueOf(itemValue)));
					}
				} else {
					lNameValuePairs.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())));
				}
			}
			reqParams = null;
			post.setEntity(new UrlEncodedFormEntity(lNameValuePairs, "GB2312"));
		}
		return post;
	}

	/**
	 * 上传图片到服务器
	 * 
	 * @param webServiceURL
	 * @param bm
	 * @param auth
	 * @return
	 * @throws Exception
	 */
	public static UploadAvatar executeMultipartPost(String webServiceURL, Bitmap bm, String sid, String adsid) throws Exception {
		ByteArrayOutputStream bos = null;
		ByteArrayBody bab = null;
		try {
			bos = new ByteArrayOutputStream();
			bm.compress(CompressFormat.JPEG, 100, bos);
			byte[] data = bos.toByteArray();
			HttpClient httpClient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			HttpPost postRequest = new HttpPost(webServiceURL + "?sid=" + sid + "&adSId=" + adsid);
			bab = new ByteArrayBody(data, UUID.randomUUID().toString() + ".jpg");
			MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			reqEntity.addPart("image", bab);
			postRequest.setEntity(reqEntity);
			HttpResponse response = httpClient.execute(postRequest, localContext);
			if (response.getStatusLine().getStatusCode() == 200) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
				String sResponse;
				StringBuilder s = new StringBuilder();
				while ((sResponse = reader.readLine()) != null) {
					s = s.append(sResponse);
				}
				Gson gson = new GsonBuilder().create();
				UploadAvatar result = gson.fromJson(s.toString(), UploadAvatar.class);
				response = null;
				s = null;
				reader = null;
				gson = null;
				return result;
			}
		} catch (Exception e) {
			Log.e(e.getClass().getName(), e.getMessage());
		} finally {
			if (bos != null)
				bos.close();
			bm = null;
			bab = null;
		}
		return null;
	}
}
