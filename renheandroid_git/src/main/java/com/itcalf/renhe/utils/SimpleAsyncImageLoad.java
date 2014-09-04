package com.itcalf.renhe.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import com.itcalf.renhe.Constants;
import com.itcalf.renhe.R;
import com.itcalf.renhe.cache.ExternalStorageUtil;

import android.content.Context;
import android.graphics.AvoidXfermode;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

public class SimpleAsyncImageLoad {
	private static int TIMEOUT = 5000;
    // private HashMap<String, SoftReference<Drawable>> imageCache;
      
//     public SimpleAsyncImageLoad() {
//             AsyncImageLoader.imageCache = new HashMap<String, SoftReference<Drawable>>();
//         }
      
     public static  Drawable loadDrawable(final String userId, final String imageUrl,final String email,final Integer width,
 			final Integer height,final Context ct,final ImageCallback imageCallback) {
    	  final Handler handler = new Handler() {
              public void handleMessage(Message message) {
                  imageCallback.imageLoaded((Drawable) message.obj, imageUrl);
              }
          };
             if (AsyncImageLoader.imageCache.containsKey(imageUrl + width + height)) {
                 SoftReference<Drawable> softReference = AsyncImageLoader.imageCache.get(imageUrl + width + height);
                 Drawable drawable = softReference.get();
                 if (drawable != null) {
                	 handler.sendMessage(handler.obtainMessage(0,
     						softReference.get()));
                     return drawable;
                 }
             }
           
             new Thread() {
                 @Override
                 public void run() {
                     Drawable drawable = loadImageFromUrl(userId, imageUrl,email,width,height,ct);
                     AsyncImageLoader.imageCache.put(imageUrl + width + height, new SoftReference<Drawable>(drawable));
                     Message message = handler.obtainMessage(0, drawable);
                     handler.sendMessage(message);
                 }
             }.start();
             return null;
         }
      
    public static Drawable loadImageFromUrl(String userId, String url,String email,final Integer width,
 			final Integer height,final Context ct) {
//            URL m;
//            InputStream i = null;
//            try {
//                m = new URL(url);
//                i = (InputStream) m.getContent();
//            } catch (MalformedURLException e1) {
//                e1.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            Drawable d = Drawable.createFromStream(i, "src");
            return loadImageFromUrl1(userId, url, width, height, ct, email, false, true);
        }
      
    public interface ImageCallback {
             public void imageLoaded(Drawable imageDrawable, String imageUrl);
         }
    
    
    private static String getWebPath(String userId, String webPath, boolean isAvatar) {
		if (isAvatar) {
			if(null != webPath && !webPath.equals("")){
				String id = webPath.substring(webPath.indexOf("/")+2);
				id = id.replaceAll("/", "_");
				return "out"+id;
			}else{
				return "";
			}
		} else {
			if(null != webPath && !webPath.equals("")){
				return webPath.substring(webPath.lastIndexOf("/") + 1);
			}else{
				return "";
			}
			
		}
	}
    protected static  Drawable loadImageFromUrl1(String userId, String imageUrl, Integer width,
			Integer height, Context ct, String email, boolean isCorner,
			boolean isAvatar) {
		String fileName = getWebPath(userId,imageUrl, isAvatar);
		File file = new File(ExternalStorageUtil.getCacheAvatarPath(ct, email)
				+ fileName);
		if (null != file && file.isFile()) {
			Bitmap bitmap = BitmapFactory.decodeFile(ExternalStorageUtil.getCacheAvatarPath(ct, email) + fileName);
			if (null != bitmap) {
				if (null != width && null != height) {
					bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
				}
				if (isCorner) {
					// bitmap = getRoundedCornerBitmap(bitmap);
					bitmap = getRoundedCornerBitmap(ct, bitmap, 5, 5, 5, 5, bitmap.getWidth(), bitmap.getHeight());
				}
				return new BitmapDrawable(bitmap);
			}
		}
		ByteArrayOutputStream bos = null;
		DefaultHttpClient lClient = new DefaultHttpClient();
		HttpGet lHttpGet = new HttpGet(imageUrl);
		lClient.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, TIMEOUT);
		lClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
				TIMEOUT);
		try {
			HttpResponse lRes = lClient.execute(lHttpGet);
			if (lRes.getStatusLine().getStatusCode() == 200) {
				InputStream lIs = lRes.getEntity().getContent();
				bos = new ByteArrayOutputStream();
				// int i;
				byte[] byteRead = new byte[1024];
				int i = 0;
				while ((i = lIs.read(byteRead)) != -1) {
					bos.write(byteRead, 0, i);
				}
				byte[] byteArray = bos.toByteArray();
				if (null != byteArray) {
					saveFile(byteArray,
							ExternalStorageUtil.getCacheAvatarPath(ct, email),
							fileName);
					// BitmapFactory.Options lOptions = new
					// BitmapFactory.Options();
					// lOptions.inSampleSize = 10;
					Bitmap lBitmap = BitmapFactory.decodeByteArray(byteArray,
							0, byteArray.length);
					if (null != lBitmap) {
						// Bitmap lBitmap = BitmapFactory.decodeStream(lIs,
						// null,
						// lOptions);
						if (null != width && null != height) {
							lBitmap = Bitmap.createScaledBitmap(lBitmap, width,
									height, false);
						}
						if (isCorner) {
							// lBitmap = getRoundedCornerBitmap(lBitmap);
							lBitmap = getRoundedCornerBitmap(ct, lBitmap, 5, 5,
									5, 5, lBitmap.getWidth(),
									lBitmap.getHeight());
						}
						return new BitmapDrawable(lBitmap);
					}
				}
			}
			return null;
		} catch (Exception e) {
			if (Constants.LOG) {
				Log.d(Constants.TAG, "loadImageFromUrl", e);
			}
			return null;
		} finally {
			try {
				if (null != bos)
					bos.close();
			} catch (IOException e) {
				if (Constants.LOG) {
					Log.d(Constants.TAG, "loadImageFromUrl", e);
				}
				return null;
			}
		}
	}
    public static Bitmap getRoundedCornerBitmap(Context context, Bitmap bitmap,
			float upperLeft, float upperRight, float lowerRight,
			float lowerLeft, int endWidth, int endHeight) {
		float densityMultiplier = context.getResources().getDisplayMetrics().density;

		// scale incoming bitmap to appropriate px size given arguments and
		// display dpi
		bitmap = Bitmap.createScaledBitmap(bitmap,
				Math.round(endWidth * densityMultiplier),
				Math.round(endHeight * densityMultiplier), true);

		// create empty bitmap for drawing
		Bitmap output = Bitmap.createBitmap(
				Math.round(endWidth * densityMultiplier),
				Math.round(endHeight * densityMultiplier), Config.ARGB_8888);

		// get canvas for empty bitmap
		Canvas canvas = new Canvas(output);
		int width = canvas.getWidth();
		int height = canvas.getHeight();

		// scale the rounded corners appropriately given dpi
		upperLeft *= densityMultiplier;
		upperRight *= densityMultiplier;
		lowerRight *= densityMultiplier;
		lowerLeft *= densityMultiplier;

		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.WHITE);

		// fill the canvas with transparency
		canvas.drawARGB(0, 0, 0, 0);

		// draw the rounded corners around the image rect. clockwise, starting
		// in upper left.
		canvas.drawCircle(upperLeft, upperLeft, upperLeft, paint);
		canvas.drawCircle(width - upperRight, upperRight, upperRight, paint);
		canvas.drawCircle(width - lowerRight, height - lowerRight, lowerRight,
				paint);
		canvas.drawCircle(lowerLeft, height - lowerLeft, lowerLeft, paint);

		// fill in all the gaps between circles. clockwise, starting at top.
		RectF rectT = new RectF(upperLeft, 0, width - upperRight, height / 2);
		RectF rectR = new RectF(width / 2, upperRight, width, height
				- lowerRight);
		RectF rectB = new RectF(lowerLeft, height / 2, width - lowerRight,
				height);
		RectF rectL = new RectF(0, upperLeft, width / 2, height - lowerLeft);

		canvas.drawRect(rectT, paint);
		canvas.drawRect(rectR, paint);
		canvas.drawRect(rectB, paint);
		canvas.drawRect(rectL, paint);

		// set up the rect for the image
		Rect imageRect = new Rect(0, 0, width, height);

		// set up paint object such that it only paints on Color.WHITE
		paint.setXfermode(new AvoidXfermode(Color.WHITE, 255,
				AvoidXfermode.Mode.TARGET));

		// draw resized bitmap onto imageRect in canvas, using paint as
		// configured above
		canvas.drawBitmap(bitmap, imageRect, imageRect, paint);

		return output;
	}
	public static String saveFile(byte[] bytes, String filePath, String fileName) {
		File file = new File(filePath, fileName);
		FileOutputStream fos = null;
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			fos = new FileOutputStream(file);
			fos.write(bytes);
			fos.flush();
		} catch (Exception e) {
			e.printStackTrace();
			if (null != fos)
				try {
					fos.close();
				} catch (IOException e1) {
				}
		}
		return file.getAbsolutePath();
	}

}