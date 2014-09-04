package com.itcalf.renhe.context.archives;

import java.util.List;

import com.itcalf.renhe.R;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SoftGridAdapter extends BaseAdapter {
	public static final String TAG = "SoftGridAdapter";
	private Context context;
	private List<ResolveInfo> appList;
	private PackageManager mPackageManager;

	public SoftGridAdapter(Context context, List<ResolveInfo> list) {
		this.context = context;
		this.appList = list;
		mPackageManager = context.getPackageManager();

	}

	@Override
	public int getCount() {
		return appList != null ? appList.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return appList.get(position);
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
			convertView = LayoutInflater.from(context).inflate(R.layout.grid_item, null);
			mHolder.app_img = (ImageView) convertView.findViewById(R.id.ItemImage);
			mHolder.appName_txt = (TextView) convertView.findViewById(R.id.app_name);
			convertView.setTag(mHolder);
		} else {
			mHolder = (Holder) convertView.getTag();
		}
		ResolveInfo mResolveInfo = appList.get(position);
		if (mResolveInfo.activityInfo.packageName.contains("renhe")) {
			Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
	        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
	        mainIntent.setPackage("com.itcalf.renhe");
	        mResolveInfo = mPackageManager.queryIntentActivities(mainIntent, 0).get(0);
	        mHolder.appName_txt.setText(mResolveInfo.loadLabel(mPackageManager).toString());// 应用名
			mHolder.app_img.setBackgroundDrawable(mResolveInfo.loadIcon(mPackageManager));// 应用图标
		} else {
			mHolder.appName_txt.setText(mResolveInfo.loadLabel(mPackageManager).toString());// 应用名
			mHolder.app_img.setBackgroundDrawable(mResolveInfo.loadIcon(mPackageManager));// 应用图标
		}

		return convertView;
	}

	class Holder {
		ImageView app_img;
		TextView appName_txt;
	}

}
