package com.dxiang.mring3.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

public class ImageUtil {

	private static final String TAG = "ImageUtil";

	public ImageUtil() {
	}

	/** 水平方向模糊度 */
	private static float hRadius = 10;
	/** 竖直方向模糊度 */
	private static float vRadius = 10;
	/** 模糊迭代度 */
	private static int iterations = 7;

	/**
	 * 
	 高斯模糊
	 */
	public static Bitmap BoxBlurFilter(Bitmap bmp) {
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		int[] inPixels = new int[width * height];
		int[] outPixels = new int[width * height];
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Config.ARGB_8888);
		bmp.getPixels(inPixels, 0, width, 0, 0, width, height);
		for (int i = 0; i < iterations; i++) {
			blur(inPixels, outPixels, width, height, hRadius);
			blur(outPixels, inPixels, height, width, vRadius);
		}
		blurFractional(inPixels, outPixels, width, height, hRadius);
		blurFractional(outPixels, inPixels, height, width, vRadius);
		bitmap.setPixels(inPixels, 0, width, 0, 0, width, height);
		if (bmp != null && !bmp.isRecycled()) {
			bmp.recycle();
			bmp = null;
		}
		return bitmap;
	}

	public static void blur(int[] in, int[] out, int width, int height,
			float radius) {
		int widthMinus1 = width - 1;
		int r = (int) radius;
		int tableSize = 2 * r + 1;
		int divide[] = new int[256 * tableSize];

		for (int i = 0; i < 256 * tableSize; i++)
			divide[i] = i / tableSize;

		int inIndex = 0;

		for (int y = 0; y < height; y++) {
			int outIndex = y;
			int ta = 0, tr = 0, tg = 0, tb = 0;

			for (int i = -r; i <= r; i++) {
				int rgb = in[inIndex + clamp(i, 0, width - 1)];
				ta += (rgb >> 24) & 0xff;
				tr += (rgb >> 16) & 0xff;
				tg += (rgb >> 8) & 0xff;
				tb += rgb & 0xff;
			}

			for (int x = 0; x < width; x++) {
				out[outIndex] = (divide[ta] << 24) | (divide[tr] << 16)
						| (divide[tg] << 8) | divide[tb];

				int i1 = x + r + 1;
				if (i1 > widthMinus1)
					i1 = widthMinus1;
				int i2 = x - r;
				if (i2 < 0)
					i2 = 0;
				int rgb1 = in[inIndex + i1];
				int rgb2 = in[inIndex + i2];

				ta += ((rgb1 >> 24) & 0xff) - ((rgb2 >> 24) & 0xff);
				tr += ((rgb1 & 0xff0000) - (rgb2 & 0xff0000)) >> 16;
				tg += ((rgb1 & 0xff00) - (rgb2 & 0xff00)) >> 8;
				tb += (rgb1 & 0xff) - (rgb2 & 0xff);
				outIndex += height;
			}
			inIndex += width;
		}
	}

	public static void blurFractional(int[] in, int[] out, int width,
			int height, float radius) {
		radius -= (int) radius;
		float f = 1.0f / (1 + 2 * radius);
		int inIndex = 0;

		for (int y = 0; y < height; y++) {
			int outIndex = y;

			out[outIndex] = in[0];
			outIndex += height;
			for (int x = 1; x < width - 1; x++) {
				int i = inIndex + x;
				int rgb1 = in[i - 1];
				int rgb2 = in[i];
				int rgb3 = in[i + 1];

				int a1 = (rgb1 >> 24) & 0xff;
				int r1 = (rgb1 >> 16) & 0xff;
				int g1 = (rgb1 >> 8) & 0xff;
				int b1 = rgb1 & 0xff;
				int a2 = (rgb2 >> 24) & 0xff;
				int r2 = (rgb2 >> 16) & 0xff;
				int g2 = (rgb2 >> 8) & 0xff;
				int b2 = rgb2 & 0xff;
				int a3 = (rgb3 >> 24) & 0xff;
				int r3 = (rgb3 >> 16) & 0xff;
				int g3 = (rgb3 >> 8) & 0xff;
				int b3 = rgb3 & 0xff;
				a1 = a2 + (int) ((a1 + a3) * radius);
				r1 = r2 + (int) ((r1 + r3) * radius);
				g1 = g2 + (int) ((g1 + g3) * radius);
				b1 = b2 + (int) ((b1 + b3) * radius);
				a1 *= f;
				r1 *= f;
				g1 *= f;
				b1 *= f;
				out[outIndex] = (a1 << 24) | (r1 << 16) | (g1 << 8) | b1;
				outIndex += height;
			}
			out[outIndex] = in[width - 1];
			inIndex += width;
		}
	}

	public static int clamp(int x, int a, int b) {
		return (x < a) ? a : (x > b) ? b : x;
	}

	/**
	 * 高斯模糊
	 * 
	 * @param bmp
	 * @return
	 */

	public static Bitmap blurImageAmeliorate(Bitmap bmp) {
		long start = System.currentTimeMillis();
		// 高斯矩阵
		int[] gauss = new int[] { 1, 2, 1, 2, 4, 2, 1, 2, 1 };

		int width = bmp.getWidth();
		int height = bmp.getHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Config.RGB_565);

		int pixR = 0;
		int pixG = 0;
		int pixB = 0;

		int pixColor = 0;

		int newR = 0;
		int newG = 0;
		int newB = 0;

		int delta = 32; // 值越小图片会越亮，越大则越暗

		int idx = 0;
		int[] pixels = new int[width * height];
		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		for (int i = 1, length = height - 1; i < length; i++) {
			for (int k = 1, len = width - 1; k < len; k++) {
				idx = 0;
				for (int m = -1; m <= 1; m++) {
					for (int n = -1; n <= 1; n++) {
						pixColor = pixels[(i + m) * width + k + n];
						pixR = Color.red(pixColor);
						pixG = Color.green(pixColor);
						pixB = Color.blue(pixColor);

						newR = newR + (int) (pixR * gauss[idx]);
						newG = newG + (int) (pixG * gauss[idx]);
						newB = newB + (int) (pixB * gauss[idx]);
						idx++;
					}
				}

				newR /= delta;
				newG /= delta;
				newB /= delta;

				newR = Math.min(255, Math.max(0, newR));
				newG = Math.min(255, Math.max(0, newG));
				newB = Math.min(255, Math.max(0, newB));

				pixels[i * width + k] = Color.argb(255, newR, newG, newB);

				newR = 0;
				newG = 0;
				newB = 0;
			}
		}

		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		long end = System.currentTimeMillis();
		Log.d("may", "used time=" + (end - start));
		return bitmap;
	}

	// ming changed 2014 10 09
	// 截取中心部分正方形，同时缩小到指定尺寸 , 另存到一个文件中
	// strFileType: jpg, png
	public static void cutBitmapToFile(String strFilePathOrig,
			String strFilePathDest, int iDestWidth, int iDestHeight,
			String strFileType, boolean bForce) {

		Bitmap bmp = ImageUtil.getBitmapFromFile(strFilePathOrig);

		bmp = cutBitmap(bmp, iDestWidth, iDestHeight, bForce);

		saveMyBitmap(bmp, strFilePathDest, strFileType);

	}

	// ming changed 2014 10 09
	// 缩小到指定尺寸，再截取中心部分的长方形
	public static Bitmap cutBitmap(Bitmap bitmap, int iDWidth, int iDHeight,
			boolean bForce) {
		if (bitmap == null)
			return null;
		Bitmap bmp = null;

		int iOrgWidth = bitmap.getWidth();
		int iOrgHeight = bitmap.getHeight();

		float rOrg = (float) iOrgWidth / (float) iOrgHeight;
		float rDest = (float) iDWidth / (float) iDHeight;

		int iWidthOff = 0;
		int iHeightOff = 0;
		int iDestHeight = iDHeight;
		int iDestWidth = iDWidth;
		if (rDest < rOrg) { // align in height
			bmp = ImageUtil.resizeBitmapByHeight(bitmap, iDHeight, bForce);
			if (bmp.getHeight() == bitmap.getHeight()) {
				iDestHeight = bmp.getHeight();
				iDestWidth = (int) ((float) iDestHeight * ((float) iDWidth / (float) iDHeight));
			}
			iWidthOff = (bmp.getWidth() - iDestWidth) / 2;
		} else { // align in width
			bmp = ImageUtil.resizeBitmapByWidth(bitmap, iDestWidth, bForce);
			if (bmp.getWidth() == bitmap.getWidth()) {
				iDestWidth = bmp.getWidth();
				iDestHeight = (int) ((float) iDestWidth * ((float) iDHeight / (float) iDWidth));
			}
			iHeightOff = (bmp.getHeight() - iDestHeight) / 2;
		}
		// if (bitmap != null && !bitmap.isRecycled()) {
		// bitmap.recycle();
		// bitmap = null;
		// }
		return ScaleBitmap(bmp, iWidthOff, iHeightOff, iDestWidth, iDestHeight);
	}

	// ming changed 2014 10 09
	// 按指定宽高缩放图片
	public static Bitmap resizeBitmap(Bitmap bitmap, int iWidth, int iHeight) {
		return Bitmap.createScaledBitmap(bitmap, iWidth, iHeight, true);
	}

	// ming changed 2014 10 09
	// 按指定宽度按比例缩放图片
	public static Bitmap resizeBitmapByWidth(Bitmap bitmap, int iWidth,
			boolean bForce) {
		if (bitmap == null)
			return null;

		int iWidthOrig = bitmap.getWidth();
		int iHeightOrig = bitmap.getHeight();

		if (iWidthOrig == 0) {
			return bitmap;
		}

		int iHeight = 0;
		if (!bForce) {
			if (iWidth > iWidthOrig) {
				return bitmap;
			}
		}

		iHeight = (int) ((float) iWidth * (float) iHeightOrig / (float) iWidthOrig);

		return Bitmap.createScaledBitmap(bitmap, iWidth, iHeight, true);
	}

	// ming changed 2014 10 09
	// 按指定高度按比例缩放图片
	// bForce: true (resize no matter the original size, even it is smaller)
	public static Bitmap resizeBitmapByHeight(Bitmap bitmap, int iHeight,
			boolean bForce) {
		if (bitmap == null)
			return null;

		int iWidthOrig = bitmap.getWidth();
		int iHeightOrig = bitmap.getHeight();

		if (iWidthOrig == 0) {
			return bitmap;
		}

		int iWidth = 0;
		if (!bForce) {
			if (iHeight > iHeightOrig || iWidthOrig == 0) {
				return bitmap;
			}
		}

		iWidth = (int) ((float) iHeight * (float) iWidthOrig / (float) iHeightOrig);

		return Bitmap.createScaledBitmap(bitmap, iWidth, iHeight, true);
	}

	public static Bitmap ScaleBitmap(Bitmap bmSrc, int iWidthOff,
			int iHeightOff, int iWidth, int iHeight) {

		Log.v("laolao", "iWidthOff = " + iWidthOff);
		Log.v("laolao", "iHeightOff = " + iHeightOff);
		Log.v("laolao", "iWidth = " + iWidth);
		Log.v("laolao", "iHeight = " + iHeight);
		Log.v("laolao", "bmSrc.getWidth() = " + bmSrc.getWidth());
		Log.v("laolao", "bmSrc.getHeight() = " + bmSrc.getHeight());

		Bitmap bmScale = Bitmap.createBitmap(iWidth, iHeight,
				Config.ARGB_8888);
		Canvas canvas = new Canvas(bmScale);
		Rect rtSrc = new Rect(iWidthOff, iHeightOff, iWidth + iWidthOff,
				iHeight + iHeightOff);
		Rect rtDst = new Rect(0, 0, iWidth, iHeight);
		canvas.drawBitmap(bmSrc, rtSrc, rtDst, null);
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		if (bmSrc != null && !bmSrc.isRecycled()) {
			bmSrc.recycle();
			bmSrc = null;
		}
		return bmScale;
	}

	// ming changed 2014 10 09
	public static Bitmap getBitmapFromFile(String strImagePath) {

		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = false;
			Bitmap bitmap = BitmapFactory.decodeFile(strImagePath, options);

			return bitmap;
		} catch (Exception e) {
		}
		return null;

	}

	// ming changed 2011 12 04
	public static Bitmap getBitmapFromLargeWidth(String strImagePath, int width) {
		if (width <= 0)
			return null;
		File f = new File(strImagePath);
		if (!f.exists() || !f.isFile())
			return null;

		int iWidthOrg = 0;
		int iHeightOrg = 0;

		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			Bitmap bitmap = BitmapFactory.decodeFile(strImagePath, options);
			options.inJustDecodeBounds = false;
			iWidthOrg = options.outWidth;
			iHeightOrg = options.outHeight;

			int scale = iWidthOrg / width;

			// if (scale%10 != 0)
			// scale += 10;
			// scale /= 10;
			// if (scale <= 0)
			// scale = 1;

			if (scale == 0) {
				options.inSampleSize = 1;
			} else {
				options.inSampleSize = scale;
			}

			Log.i(TAG, "scale=" + scale + ", sample=" + scale);
			bitmap = BitmapFactory.decodeFile(strImagePath, options);
			if (scale == 0)
				return bitmap;

			int height = (int) ((float) iHeightOrg * (float) width / (float) iWidthOrg);

			Bitmap bmResize = Bitmap.createScaledBitmap(bitmap, width, height,
					true);

			if (bitmap != null) {
				bitmap.recycle();
				bitmap = null;
			}

			return bmResize;
		} catch (Exception e) {
		}
		return null;
	}

	// ming changed 2011 12 04
	public static Bitmap getBitmapFromLargeWidth_NOLIMIT(String strImagePath,
			int width) {
		if (width <= 0)
			return null;
		File f = new File(strImagePath);
		if (!f.exists() || !f.isFile())
			return null;

		int iWidthOrg = 0;
		int iHeightOrg = 0;

		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			Bitmap bitmap = BitmapFactory.decodeFile(strImagePath, options);
			options.inJustDecodeBounds = false;

			iWidthOrg = options.outWidth;
			iHeightOrg = options.outHeight;

			int scale = iWidthOrg / width;

			// if (scale%10 != 0)
			// scale += 10;
			// scale /= 10;
			// if (scale <= 0)
			// scale = 1;

			if (scale == 0) {
				options.inSampleSize = 1;
			} else {
				options.inSampleSize = scale;
			}

			Log.i(TAG, "scale=" + scale + ", sample=" + scale);
			bitmap = BitmapFactory.decodeFile(strImagePath, options);
			// if(scale == 0 ) return bitmap;

			int height = (int) ((float) iHeightOrg * (float) width / (float) iWidthOrg);

			Bitmap bmResize = Bitmap.createScaledBitmap(bitmap, width, height,
					true);

			if (bitmap != null) {
				bitmap.recycle();
				bitmap = null;
			}

			return bmResize;
		} catch (Exception e) {
		}
		return null;
	}

	public static boolean saveMyBitmap(Bitmap mBitmap, String strFilePath,
			String imageType) {

		Log.v("ming", "strFilePath = " + strFilePath);

		File f = new File(strFilePath);
		try {
			f.createNewFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		}
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}

		if (imageType.toUpperCase().equals("PNG")) {
			mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		} else {
			mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
		}

		try {

			fOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		try {
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;

	}

	public static boolean ImgScaleResize(String strFIn, String strFOut,
			int iWidth, String imageType) {
		Bitmap bm = getBitmapFromLargeWidth_NOLIMIT(strFIn, iWidth);

		if (bm == null)
			return false;

		Log.i(TAG,
				"bm.getWidth()=" + bm.getHeight() + ", bm.getWidth() ="
						+ bm.getWidth());

		return saveMyBitmap(bm, strFOut, imageType);

	}

	public static boolean AddImage(String strImageIn, String strBase,
			int iXShift, int iYShift, String strImageOut) {

		Bitmap bmpIn = ImageUtil.getBitmapFromFile(strImageIn);
		Bitmap bmpBase = ImageUtil.getBitmapFromFile(strBase);

		int iWidth = bmpBase.getWidth();
		int iHeight = bmpBase.getHeight();

		bmpIn = ImageUtil.resizeBitmap(bmpIn, iWidth, iHeight - iXShift);

		Bitmap bmScale = Bitmap.createBitmap(iWidth, iHeight,
				Config.ARGB_8888);
		Canvas canvas = new Canvas(bmScale);
		Rect rtSrc = new Rect(0, 0, bmpIn.getWidth(), bmpIn.getHeight());
		Rect rtDst = new Rect(iXShift, iYShift, iWidth, iHeight);
		canvas.drawBitmap(bmpIn, rtSrc, rtDst, null);

		rtSrc = new Rect(0, 0, iWidth, iHeight);
		rtDst = new Rect(0, 0, iWidth, iHeight);
		canvas.drawBitmap(bmpBase, rtSrc, rtDst, null);

		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();

		ImageUtil.saveMyBitmap(bmScale, strImageOut, "png");

		return true;

	}

	/*
	 * Making image in circular shape
	 */
	public static Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
		// TODO Auto-generated method stub
		int targetWidth = scaleBitmapImage.getWidth();
		int targetHeight = scaleBitmapImage.getHeight();
		Bitmap targetBitmap = Bitmap.createBitmap(targetWidth, targetHeight,
				Config.ARGB_8888);

		Canvas canvas = new Canvas(targetBitmap);
		Path path = new Path();
		path.addCircle(((float) targetWidth - 1) / 2,
				((float) targetHeight - 1) / 2,
				(Math.min(((float) targetWidth), ((float) targetHeight)) / 2),
				Path.Direction.CCW);

		canvas.clipPath(path);
		Bitmap sourceBitmap = scaleBitmapImage;
		canvas.drawBitmap(sourceBitmap, new Rect(0, 0, sourceBitmap.getWidth(),
				sourceBitmap.getHeight()), new Rect(0, 0, targetWidth,
				targetHeight), null);
		return targetBitmap;
	}

	/**
	 * 有边框圆图
	 * 
	 * @param bitmap
	 * @param context
	 * @param circleBounds
	 * @return
	 */
	public static Bitmap toRoundBitmap(Bitmap bitmap, Context context,
			float circleBounds) {
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager wManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		wManager.getDefaultDisplay().getMetrics(metrics);
		final float density = metrics.density;
		float boundSize = density * circleBounds + 0.5f;
		int width = (int) (bitmap.getWidth());
		int height = (int) (bitmap.getHeight());
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;
			top = 0;
			bottom = width;
			left = 0;
			right = width;
			height = width;
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}
		Bitmap output = Bitmap.createBitmap((int) (width + boundSize * 1.5),
				(int) (height + boundSize * 1.5), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		// final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right,
				(int) bottom);
		final Rect dst = new Rect((int) (dst_left + boundSize),
				(int) (dst_top + boundSize), (int) (dst_right + boundSize),
				(int) (dst_bottom + boundSize));
		final RectF rectF = new RectF(dst);

		paint.setAntiAlias(true);
		// canvas.drawARGB(0, 0, 0, 0);
		// paint.setColor(Color.WHITE);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, dst, paint);

		float cx = (rectF.centerX());
		float cy = (rectF.centerY());
		Paint paint2 = new Paint();
		paint2.setColor(Color.WHITE);
		paint2.setAntiAlias(true);

		// 表示画空心圆
		paint2.setStyle(Paint.Style.STROKE);
		// 画的线宽度
		paint2.setStrokeWidth((float) 4);
		canvas.drawCircle(cx, cy, roundPx, paint2);
		return output;
	}

	/**
	 * 无边框圆图
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap toRoundBitmap(Bitmap bitmap) {
		int width = (int) (bitmap.getWidth());
		int height = (int) (bitmap.getHeight());
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;
			top = 0;
			bottom = width;
			left = 0;
			right = width;
			height = width;
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}
		Bitmap output = Bitmap.createBitmap((int) (width), (int) (height),
				Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		// final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right,
				(int) bottom);
		final Rect dst = new Rect((int) (dst_left), (int) (dst_top),
				(int) (dst_right), (int) (dst_bottom));
		final RectF rectF = new RectF(dst);

		paint.setAntiAlias(true);
		// canvas.drawARGB(0, 0, 0, 0);
		// paint.setColor(Color.WHITE);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, dst, paint);
		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
			bitmap = null;
		}
		return output;
	}

}
