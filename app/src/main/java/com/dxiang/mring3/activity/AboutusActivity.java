package com.dxiang.mring3.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gem.imgcash.ImageDownLoader;
import com.gem.imgcash.ImageDownLoader.onImageLoaderListener;
import com.dxiang.mring3.R;
import com.dxiang.mring3.file.UpdateCode;
import com.dxiang.mring3.utils.Commons;
import com.dxiang.mring3.utils.Utils;
import com.dxiang.mring3.utils.WindowArg;

/**
 * 关于界面
 * 
 * @author humingzhuo
 */

@SuppressLint("ValidFragment")
@SuppressWarnings("unused")
public class AboutusActivity extends BaseActivity {

	
	
	// 控件
	private TextView tv_code;
	private TextView tv_text;
	private TextView tv_about,mTitle_tv;
	private ImageView mTitle_iv,imageView;
	private Button btn_show;
	private RelativeLayout rl_title_layout;

	// 变量
	private int code; // 当前版本号
	private double name; //当前版本
	private UpdateCode updatecode;
	
//	private DisplayImageOptions mImageOptions;
	 private ImageView liear_title;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_aboutus);
//		mImageOptions = new DisplayImageOptions.Builder()
//        .showImageOnLoading(R.drawable.codeicon)//设置图片在下载期间显示的图片
//        .showImageForEmptyUri(R.drawable.codeicon)//设置图片URI为空或是错误的时候显示的图片
//        .showImageOnFail(R.drawable.codeicon)//设置图片加载/解码过程中错误时候显示的图片
//        .cacheInMemory(true)//设置现在的图片是否缓存在内存�?
//        .cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中  
//        .considerExifParams(true)//是否考虑JPEG图像EXIF参数（旋转，翻转�?
//        .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类�?
//        .build();
		initWidget();
		setClickListener();
		init();
		showImage();
	}


	public void onPause() {
	    super.onPause();
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}


	/**
	 * 初始化页面
	 */
	private void init() {
		tv_code.setText(getResources().getString(R.string.version_number)+Utils.getVersionName(mContext));
		tv_about.setText(getResources().getString(R.string.update_version)+" "+Utils.getVersionName(mContext));
		mTitle_tv.setText(getResources().getString(R.string.about_us));
		
		mTitle_iv.setVisibility(View.VISIBLE);
//		mTitle_iv.setBackgroundResource(R.drawable.navbar_leftarrow);
		
		try {
			code = Utils.getVersionCode(mContext);
			name = Double.parseDouble(Utils.getVersionName(mContext).trim());
			tv_code.setText(Utils.getResouceStr(mContext,R.string.version_number)+String.valueOf(name));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if (name < Utils.lowest) {
			tv_about.setVisibility(View.VISIBLE);
			tv_about.setText(Utils.getResouceStr(mContext,R.string.update_version)+Utils.latest);
		} else if (name >= Utils.lowest && name < Utils.latest) {
			tv_about.setVisibility(View.VISIBLE);
			tv_about.setText(Utils.getResouceStr(mContext,R.string.update_version)+Utils.latest);
		} else {
			tv_about.setVisibility(View.GONE);
			
		}
		
	}

	/**
	 * 初始化系统控件
	 */
	private void initWidget() {
		tv_code = (TextView) AboutusActivity.this.findViewById(R.id.tv_version_number); // 当前的版本
		tv_about = (TextView) AboutusActivity.this.findViewById(R.id.tv_update);// 升级按钮和文字描述
		rl_title_layout = (RelativeLayout) AboutusActivity.this.findViewById(R.id.rl_title_layout);//返回
		mTitle_iv = (ImageView)AboutusActivity.this.findViewById(R.id.title_iv);
		mTitle_tv = (TextView) AboutusActivity.this.findViewById(R.id.title_tv);
		imageView = (ImageView) AboutusActivity.this.findViewById(R.id.imageView2);
		liear_title=(ImageView) AboutusActivity.this.findViewById(R.id.liear_title );
	}

	/**
	 * 控件绑定事件
	 */
	private void setClickListener() {

		Click click = new Click();
		tv_about.setOnClickListener(click);
//		rl_title_layout.setOnClickListener(click);
		liear_title.setOnClickListener(click);
		mTitle_iv.setOnClickListener(click);
	}
	/**
	 * 处理点击事件
	 */
	private class Click implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.tv_update: // 确定
//				if (name < latest) {
					// 等更新新的数据
					if(!Utils.isFastDoubleClickToo()){
//						if (name < latest) {
							// 等更新新的数据
						updatecode=new UpdateCode();
						UpdateCode.ISABOUTUS=true;
						updatecode.onCreateDialogPt(AboutusActivity.this);
//						}
					}
//				}

				break;
			case R.id.liear_title:
				if (Utils.isFastDoubleClick()) {
					return;
				}
				startActivity(new Intent(AboutusActivity.this,MainGroupActivity.class));
				finish();
				break;
			case R.id.title_iv:
				if (Utils.isFastDoubleClick()) {
					return;
				}
				AboutusActivity.this.finish();
				break;
			default:
				break;
			}
		}

	}
	@Override
	public void onStart() {
		super.onStart();
	}
	@Override
	public void onStop() {
		super.onStop();
	}
	/**
	 * 显示下载二维码图片
	 */
	private void showImage(){
		 String saveDir = Commons.getImageSavedpath()
					+ Commons.ABOUTUSURL.replaceAll("[^\\w]", "");
         Bitmap bm=ImageDownLoader.getShareImageDownLoader().showCacheBitmap(saveDir);
         if(bm==null){
        	 imageView.setBackgroundResource(R.mipmap.codeicon);
        	 int w =WindowArg.getInstance(this).getWindowWidth();
        	 int h =WindowArg.getInstance(this).getWindowHeigt();
        	 ImageDownLoader.getShareImageDownLoader().downloadImage(saveDir, Commons.ABOUTUSURL,w/3,h/4, new onImageLoaderListener() {
        		 
        		 @Override
        		 public void onImageLoader(Bitmap arg0, String arg1) {
        			 // TODO Auto-generated method stub
        			 imageView.setImageBitmap(arg0);
        		 }
        	 });
         }else{
        	 imageView.setImageBitmap(bm);
         }
	}
}
