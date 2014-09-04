package com.itcalf.renhe.imageUtil;

import com.itcalf.renhe.R;
import com.itcalf.renhe.RenheApplication;
import com.itcalf.renhe.utils.AsyncImageLoader;
import com.itcalf.renhe.utils.MatrixUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;

public class StandardImageXML extends Activity {
	ImageView imageView;
	String imageUrl;
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	setContentView(R.layout.standard_image);
    	imageView = (ImageView)findViewById(R.id.g_image);
    	imageUrl = getIntent().getStringExtra("imageurl");
    	if(imageUrl == null || imageUrl.equals("")){
    		//TODO: 返回，提示错误
    		
    	}else if(imageView != null){
    		loadImage(imageUrl);
    	}
    }
    void loadImage(String mImageUrl){
    	final ProgressDialog findPd = new ProgressDialog(this);
		findPd.setMessage("请稍候...");
		findPd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		findPd.show();
		AsyncImageLoader
		.getInstance()
		.populateData(this, ((RenheApplication) getApplicationContext()).getUserInfo().getEmail(), false, true,false)
		.loadPic2(imageView, findPd, null, mImageUrl, null, null, this,
				MatrixUtil.getPostMatrix(this));
    }
}