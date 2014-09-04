package com.itcalf.renhe.context.innermsg;

import java.io.File;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.itcalf.renhe.R;
import com.itcalf.renhe.RenheApplication;
import com.itcalf.renhe.cache.ExternalStorageUtil;
import com.itcalf.renhe.po.Contact;

public class ReceiverGridAdapter extends BaseAdapter {
	public static final String TAG = "ReceiverGridAdapter";
	private Context context;
	private List<Contact> list;

	public ReceiverGridAdapter(Context context, List<Contact> list) {
		this.context = context;
		this.list = list;

	}

	@Override
	public int getCount() {
		return list != null ? list.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder mHolder;
		if (convertView == null) {
			mHolder = new Holder();
			convertView = LayoutInflater.from(context).inflate(R.layout.select_receiver, null);
			mHolder.avarterIv = (ImageView) convertView.findViewById(R.id.avarterIv);
			convertView.setTag(mHolder);
		} else {
			mHolder = (Holder) convertView.getTag();
		}
		Contact contact = list.get(position);
		Bitmap bitmap = getUserPic(contact.getContactface());
		mHolder.avarterIv.setImageBitmap(bitmap);
		return convertView;
	}
	private Bitmap getUserPic(String userFaceUrl) {
		String fileName = getWebPath(userFaceUrl);
		if (null != fileName) {
			File file = new File(ExternalStorageUtil.getCacheAvatarPath(context, RenheApplication.getInstance().getUserInfo().getEmail())
					+ fileName);
			if (null != file && file.isFile()) {
				Bitmap mbitmap = BitmapFactory.decodeFile(ExternalStorageUtil.getCacheAvatarPath(context, RenheApplication.getInstance()
						.getUserInfo().getEmail())
						+ fileName);
				return mbitmap;
			} else {
				//放人和网logo
				Bitmap mlogo = BitmapFactory.decodeResource(context.getResources(), R.drawable.avatar);
				return mlogo;
			}
			//目前分享好友也是放人和网应用图标，如果后期想改回好友头像，将上面代码解除注释
			//			Bitmap mlogo1 = BitmapFactory.decodeResource(getResources(), R.drawable.icon_134);
			//			return mlogo1;
		} else {
			//放人和网logo
			Bitmap mlogo1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.avatar);
			return mlogo1;
		}
	}
	private String getWebPath(String webPath) {
		if (null != webPath && !"".equals(webPath)) {
			String id = webPath.substring(webPath.indexOf("/") + 2);
			id = id.replaceAll("/", "_");
			return id;
		}
		return null;
	}
	class Holder {
		ImageView avarterIv;
	}

}
