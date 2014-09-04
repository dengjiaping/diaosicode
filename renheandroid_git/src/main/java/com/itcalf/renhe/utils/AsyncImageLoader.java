package com.itcalf.renhe.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.AvoidXfermode;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.itcalf.renhe.Constants;
import com.itcalf.renhe.R;
import com.itcalf.renhe.cache.CacheManager;
import com.itcalf.renhe.cache.ExternalStorageUtil;

public class AsyncImageLoader {

	private static AsyncImageLoader mAsyncImage;
	public static Map<String, SoftReference<Drawable>> imageCache = new HashMap<String, SoftReference<Drawable>>();
	private final int TIMEOUT = 5000;
	private boolean CACHE = true;
	private static Context ct;
	private static String email;
	private boolean isCorner;
	private boolean isAvatar;
	private boolean isHostPic;
	public static AsyncImageLoader getInstance() {
		if (null == mAsyncImage) {
			mAsyncImage = new AsyncImageLoader();
		}
		return mAsyncImage;
	}

	public void clearCache() {
		imageCache.clear();
	}

	public AsyncImageLoader populateData(Context ct, String email, boolean isCorner, boolean isAvatar,boolean isHostPic) {
		this.ct = ct;
		this.email = email;
		this.isCorner = isCorner;
		this.isAvatar = isAvatar;
		this.isHostPic = isHostPic;
		return mAsyncImage;
	}

	public void enforceLoadPic(final ImageView imageView, String picPath, Integer width, Integer height) {
		CACHE = false;
		loadPic1(imageView, picPath, width, height);
	}

	public void loadPic1(final ImageView imageView, String picPath, Integer width, Integer height) {
		if (null != picPath && !"".equals(picPath)) {
			try {
				new AsyncImageLoader().loadDrawable1(picPath, new ImageCallback() {
					public void imageLoaded(Drawable imageDrawable, String imageUrl) {
						if (null != imageDrawable) {
							imageView.setImageDrawable(imageDrawable);
							imageView.invalidate();
						}
					}
				}, width, height, ct, email, isCorner, isAvatar);
			} catch (Exception e) {
				System.out.println(e);
			}
		}
	}

	/**
	 * loading Pic
	 * 
	 * @param imageView
	 * @param picPath
	 */
	public void loadPic(final ImageView imageView,String sid, String picPath, Integer width, Integer height) {
		if (null != picPath && !"".equals(picPath)) {
			try {
				new AsyncImageLoader().loadDrawable(sid,picPath, new ImageCallback() {
					public void imageLoaded(Drawable imageDrawable, String imageUrl) {
						if (null != imageDrawable) {
							imageView.setImageDrawable(imageDrawable);
							imageView.invalidate();
						}
					}
				}, width, height, ct, email, isCorner, isAvatar,false);
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(Constants.TAG, "loadPic", e);
			}
		}
	}

	/**
	 * loading Pic
	 * 
	 * @param imageView
	 * @param picPath
	 */
	public void loadPic(final ImageView imageView,String sid, String picPath, final Integer width, final Integer height, final Matrix matrix,boolean loadRoomPic) {
		if (null != picPath && !"".equals(picPath)) {
			try {
				new AsyncImageLoader().loadDrawable(sid,picPath, new ImageCallback() {
					public void imageLoaded(Drawable imageDrawable, String imageUrl) {
						if (null != imageDrawable) {
							BitmapDrawable bd = (BitmapDrawable) imageDrawable;
							Bitmap bitmap = bd.getBitmap();
							// matrix.postScale(0.6f, 0.6f);
							if (bitmap.getWidth() > 0 && bitmap.getHeight() > 0)
								bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
							imageView.setImageBitmap(bitmap);
							imageView.invalidate();
						}
					}
				}, width, height, ct, email, isCorner, isAvatar,loadRoomPic);
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(Constants.TAG, "loadPic", e);
			}
		}
	}

	public void loadPic2(final ImageView imageView, final AlertDialog dialog,String sid,String picPath, Integer width, Integer height,
			final Context context, final Matrix matrix) {
		if (null != picPath && !"".equals(picPath)) {
			try {
				new AsyncImageLoader().loadDrawable(sid,picPath, new ImageCallback() {
					public void imageLoaded(Drawable imageDrawable, String imageUrl) {
						dialog.cancel();
						if (null != imageDrawable) {

							BitmapDrawable bd = (BitmapDrawable) imageDrawable;
							Bitmap bitmap = bd.getBitmap();

							if (bitmap.getWidth() > 0 && bitmap.getHeight() > 0)
								bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

							imageView.setImageBitmap(bitmap);
							imageView.invalidate();
//							AlertDialog alertDialog = new AlertDialog.Builder(context).setView(imageView).show();
						}
					}
				}, width, height, ct, email, isCorner, isAvatar,false);
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(Constants.TAG, "loadPic", e);
			}
		}
	}

	private String getWebPath(String sid,String webPath, boolean isAvatar) {
		if (isAvatar) {
			String id = webPath.substring(webPath.indexOf("/")+2);
			id = id.replaceAll("/", "_");
			return id;
//			if(null == sid || sid.equals("")){
//				String id = webPath.substring(0, webPath.lastIndexOf("/"));
//				id = id.substring(id.lastIndexOf("/") + 1);
//				return id + "_" + webPath.substring(webPath.lastIndexOf("/") + 1);
//			}else{
//				return sid + "_" + webPath.substring(webPath.lastIndexOf("/") + 1);
//			}
		} else {
			return webPath.substring(webPath.lastIndexOf("/") + 1);
		}
	}

	public Drawable loadDrawable(final String sid,final String imageUrl, final ImageCallback callback, final Integer width, final Integer height,
			final Context ct, final String email, final boolean isCorner, final boolean isAvatar,final boolean loadRoomPic) {
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				callback.imageLoaded((Drawable) msg.obj, imageUrl);
			}
		};
		if (imageCache.containsKey(imageUrl + width + height)) {
			SoftReference<Drawable> softReference = imageCache.get(imageUrl + width + height);
			if (softReference.get() != null) {
				handler.sendMessage(handler.obtainMessage(0, softReference.get()));
				return softReference.get();
			}
		}
		new Thread() {
			public void run() {
				try {
					Drawable drawable = loadImageFromUrl(sid,imageUrl, width, height, ct, email, isCorner, isAvatar);
					if (null != drawable) {
						imageCache.put(imageUrl + width + height, new SoftReference<Drawable>(drawable));
						handler.sendMessage(handler.obtainMessage(0, drawable));
					}else{
						if(loadRoomPic){
							handler.sendMessage(handler.obtainMessage(0, AsyncImageLoader.ct.getResources().getDrawable(R.drawable.pic_error)));
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.start();
		return null;
	}

	public Drawable loadDrawable1(final String imageUrl, final ImageCallback callback, final Integer width, final Integer height,
			final Context ct, final String email, final boolean isCorner, final boolean isAvatar) {
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				callback.imageLoaded((Drawable) msg.obj, imageUrl);
			}
		};

		new Thread() {
			public void run() {
				try {
					Drawable drawable = loadImageFromUrl1(imageUrl, width, height, ct, email, isCorner, isAvatar);
					if (null != drawable) {
						imageCache.put(imageUrl + width + height, new SoftReference<Drawable>(drawable));
						handler.sendMessage(handler.obtainMessage(0, drawable));
					}
				} catch (Exception e) {
				}
			};
		}.start();
		return null;
	}

	public static Bitmap getRoundedCornerBitmap(Context context, Bitmap bitmap, float upperLeft, float upperRight,
			float lowerRight, float lowerLeft, int endWidth, int endHeight) {
		float densityMultiplier = context.getResources().getDisplayMetrics().density;

		// scale incoming bitmap to appropriate px size given arguments and
		// display dpi
		bitmap = Bitmap.createScaledBitmap(bitmap, Math.round(endWidth * densityMultiplier),
				Math.round(endHeight * densityMultiplier), true);

		// create empty bitmap for drawing
		Bitmap output = Bitmap.createBitmap(Math.round(endWidth * densityMultiplier), Math.round(endHeight * densityMultiplier),
				Config.ARGB_8888);

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
		canvas.drawCircle(width - lowerRight, height - lowerRight, lowerRight, paint);
		canvas.drawCircle(lowerLeft, height - lowerLeft, lowerLeft, paint);

		// fill in all the gaps between circles. clockwise, starting at top.
		RectF rectT = new RectF(upperLeft, 0, width - upperRight, height / 2);
		RectF rectR = new RectF(width / 2, upperRight, width, height - lowerRight);
		RectF rectB = new RectF(lowerLeft, height / 2, width - lowerRight, height);
		RectF rectL = new RectF(0, upperLeft, width / 2, height - lowerLeft);

		canvas.drawRect(rectT, paint);
		canvas.drawRect(rectR, paint);
		canvas.drawRect(rectB, paint);
		canvas.drawRect(rectL, paint);

		// set up the rect for the image
		Rect imageRect = new Rect(0, 0, width, height);

		// set up paint object such that it only paints on Color.WHITE
		paint.setXfermode(new AvoidXfermode(Color.WHITE, 255, AvoidXfermode.Mode.TARGET));

		// draw resized bitmap onto imageRect in canvas, using paint as
		// configured above
		canvas.drawBitmap(bitmap, imageRect, imageRect, paint);

		return output;
	}

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = 10;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	public static Bitmap createReflectedImage(Bitmap originalImage) {
		// The gap we want between the reflection and the original image
		final int reflectionGap = 1;

		int width = originalImage.getWidth();
		int height = originalImage.getHeight();

		// This will not scale but will flip on the Y axis
		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		// Create a Bitmap with the flip matrix applied to it.
		// We only want the bottom half of the image
		Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, height / 2, width, height / 2, matrix, false);

		// Create a new bitmap with same width but taller to fit reflection
		Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height / 2), Config.ARGB_8888);

		// Create a new Canvas with the bitmap that's big enough for
		// the image plus gap plus reflection
		Canvas canvas = new Canvas(bitmapWithReflection);
		// Draw in the original image
		canvas.drawBitmap(originalImage, 0, 0, null);
		// Draw in the gap
		Paint defaultPaint = new Paint();
		canvas.drawRect(0, height, width, height + reflectionGap, defaultPaint);
		// Draw in the reflection
		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

		// Create a shader that is a linear gradient that covers the reflection
		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, originalImage.getHeight(), 0, bitmapWithReflection.getHeight()
				+ reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.CLAMP);
		// Set the paint to use this shader (linear gradient)
		paint.setShader(shader);
		// Set the Transfer mode to be porter duff and destination in
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		// Draw a rectangle using the paint with our linear gradient
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight() + reflectionGap, paint);

		return bitmapWithReflection;
	}

	protected Drawable loadImageFromUrl(String sid,String imageUrl, Integer width, Integer height, Context ct, String email,
			boolean isCorner, boolean isAvatar) {
		String fileName = getWebPath(sid,imageUrl, isAvatar);
		File file = new File(ExternalStorageUtil.getCacheAvatarPath(ct, email) + fileName);
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
		lClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, TIMEOUT);
		lClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, TIMEOUT);
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
					saveFile(byteArray, ExternalStorageUtil.getCacheAvatarPath(ct, email), fileName);
					// BitmapFactory.Options lOptions = new
					// BitmapFactory.Options();
					// lOptions.inSampleSize = 10;
					Bitmap lBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
					if (null != lBitmap) {
						// Bitmap lBitmap = BitmapFactory.decodeStream(lIs,
						// null,
						// lOptions);
						if (null != width && null != height) {
							lBitmap = Bitmap.createScaledBitmap(lBitmap, width, height, false);
						}
						if (isCorner) {
							// lBitmap = getRoundedCornerBitmap(lBitmap);
							lBitmap = getRoundedCornerBitmap(ct, lBitmap, 5, 5, 5, 5, lBitmap.getWidth(), lBitmap.getHeight());
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

	protected Drawable loadImageFromUrl1(String imageUrl, Integer width, Integer height, Context ct, String email,
			boolean isCorner, boolean isAvatar) {
		String fileName = getWebPath("", imageUrl, isAvatar);
		File file = new File(ExternalStorageUtil.getCacheAvatarPath(ct, email) + fileName);
		// if (CACHE && null != file && file.isFile()) {
		// Bitmap bitmap = BitmapFactory.decodeFile(ExternalStorageUtil
		// .getCacheAvatarPath(ct, email) + fileName);
		// if (null != bitmap) {
		// if (null != width && null != height) {
		// bitmap = Bitmap.createScaledBitmap(bitmap, width,
		// height, false);
		// }
		// if (isCorner) {
		// // bitmap = getRoundedCornerBitmap(bitmap);
		// bitmap = getRoundedCornerBitmap(ct, bitmap, 5, 5, 5, 5,
		// bitmap.getWidth(), bitmap.getHeight());
		// }
		// return new BitmapDrawable(bitmap);
		// }
		// }
		ByteArrayOutputStream bos = null;
		DefaultHttpClient lClient = new DefaultHttpClient();
		HttpGet lHttpGet = new HttpGet(imageUrl);
		lClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, TIMEOUT);
		lClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, TIMEOUT);
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
					saveFile(byteArray, ExternalStorageUtil.getCacheAvatarPath(ct, email), fileName);
					// BitmapFactory.Options lOptions = new
					// BitmapFactory.Options();
					// lOptions.inSampleSize = 10;
					Bitmap lBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
					if (null != lBitmap) {
						// Bitmap lBitmap = BitmapFactory.decodeStream(lIs,
						// null,
						// lOptions);
						if (null != width && null != height) {
							lBitmap = Bitmap.createScaledBitmap(lBitmap, width, height, false);
						}
						if (isCorner) {
							// lBitmap = getRoundedCornerBitmap(lBitmap);
							lBitmap = getRoundedCornerBitmap(ct, lBitmap, 5, 5, 5, 5, lBitmap.getWidth(), lBitmap.getHeight());
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

	public static String saveFile(byte[] bytes, String filePath, String fileName) {
		File file = new File(filePath, fileName);
		FileOutputStream fos = null;
		try {
			if (!file.isFile()) {
				file.createNewFile();
			}
			fos = new FileOutputStream(file);
			fos.write(bytes);
			fos.flush();
		} catch (Exception e) {
			if (null != fos)
				try {
					fos.close();
				} catch (IOException e1) {
				}
		}
		return file.getAbsolutePath();
	}

	public interface ImageCallback {
		public void imageLoaded(Drawable imageDrawable, String imageUrl);
	}

}
