package com.dxiang.mring3.camera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import com.dxiang.mring3.app.BaseApp;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;

public class CameraLib {
	/**
	 * Date and time the camera intent was started.
	 */
	public Date dateCameraIntentStarted = null;
	/**
	 * Default location where we want the photo to be ideally stored.
	 */
	public Uri preDefinedCameraUri = null;
	/**
	 * Potential 3rd location of photo data.
	 */
	public Uri photoUriIn3rdLocation = null;
	/**
	 * Retrieved location of the photo.
	 */
	public Uri photoUri = null;
	/**
	 * Orientation of the retrieved photo.
	 */
	public int rotateXDegrees = 0;

	/**
	 *  
	 */
	public Intent startCameraIntent(Context mContext) {
		Intent intent = null;
		if (mContext != null
				&& Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
			try {
				String manufacturer = android.os.Build.MANUFACTURER
						.toLowerCase(Locale.ENGLISH);
				String model = android.os.Build.MODEL
						.toLowerCase(Locale.ENGLISH);
				String buildType = android.os.Build.TYPE
						.toLowerCase(Locale.ENGLISH);
				String buildDevice = android.os.Build.DEVICE
						.toLowerCase(Locale.ENGLISH);
				String buildId = android.os.Build.ID
						.toLowerCase(Locale.ENGLISH);
				String sdkVersion = android.os.Build.VERSION.RELEASE
						.toLowerCase(Locale.ENGLISH);
				Log.e("xxssxxss", "manufacturer:" + manufacturer + ";model:"
						+ model + ";buildType:" + buildType + ";buildDevice:"
						+ buildDevice + ";buildId:" + buildId + ";sdkVersion:"
						+ sdkVersion);
				//
				boolean setPreDefinedCameraUri = false;
				if (!(manufacturer.contains("samsung"))
						&& !(manufacturer.contains("sony"))) {
					setPreDefinedCameraUri = true;
				}
				if (manufacturer.contains("samsung")
						&& model.contains("galaxy nexus")) { // TESTED
					setPreDefinedCameraUri = true;
				}
				if (manufacturer.contains("samsung")
						&& model.contains("gt-n7000")
						&& buildId.contains("imm76l")) { // TESTED
					setPreDefinedCameraUri = true;
				}

				if (buildType.contains("userdebug")
						&& buildDevice.contains("ariesve")) { // TESTED
					setPreDefinedCameraUri = true;
				}
				if (buildType.contains("userdebug")
						&& buildDevice.contains("crespo")) { // TESTED
					setPreDefinedCameraUri = true;
				}

				//
				//
				// /////////////////////////////////////////////////////////////////////
				// TEST
				if (manufacturer.contains("samsung")
						&& model.contains("sgh-t999l")) { // T-Mobile LTE
					// enabled Samsung
					// S3
					setPreDefinedCameraUri = true;
				}
				if (buildDevice.contains("cooper")) {
					setPreDefinedCameraUri = true;
				}
				if (buildType.contains("userdebug")
						&& buildDevice.contains("t0lte")) {
					setPreDefinedCameraUri = true;
				}
				if (buildType.contains("userdebug")
						&& buildDevice.contains("kot49h")) {
					setPreDefinedCameraUri = true;
				}
				if (buildType.contains("userdebug")
						&& buildDevice.contains("t03g")) {
					setPreDefinedCameraUri = true;
				}
				if (buildType.contains("userdebug")
						&& buildDevice.contains("gt-i9100")) {
					setPreDefinedCameraUri = true;
				}
				//
				// ///////////////////////////////////////////////////////////////////////

				dateCameraIntentStarted = new Date();
				intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				if (setPreDefinedCameraUri) {
					String filename = System.currentTimeMillis() + ".jpg";
					ContentValues values = new ContentValues();
					values.put(Images.Media.TITLE, filename);
					preDefinedCameraUri = mContext.getContentResolver().insert(
							Images.Media.EXTERNAL_CONTENT_URI,
							values);
					// Log.e("test",
					// "preDefinedCameraUri "
					// + preDefinedCameraUri.getPath()
					// + " filename " + filename);
					intent.putExtra(MediaStore.EXTRA_OUTPUT,
							preDefinedCameraUri);
				}
				// GenUtil.print(TAG"test", "setPreDefinedCameraUri "
				// + setPreDefinedCameraUri);
			} catch (ActivityNotFoundException e) {

			}
		}
		return intent;
	}

	/**
	 * 
	 * 
	 * @param mContext
	 * @param resultCode
	 * @param intent
	 */
	public void onCameraIntentResult(Context mContext, int resultCode,
			Intent intent) {
		if (resultCode == Activity.RESULT_OK) {
			Cursor myCursor = null;
			Date dateOfPicture = null;
			try {
				// Create a Cursor to obtain the file Path for the large image
				String[] largeFileProjection = {
						Images.ImageColumns._ID,
						Images.ImageColumns.DATA,
						Images.ImageColumns.ORIENTATION,
						Images.ImageColumns.DATE_TAKEN };
				String largeFileSort = Images.ImageColumns._ID
						+ " DESC";
				myCursor = mContext.getContentResolver().query(
						Images.Media.EXTERNAL_CONTENT_URI,
						largeFileProjection, null, null, largeFileSort);
				myCursor.moveToFirst();
				if (!myCursor.isAfterLast()) {
					// This will actually give you the file path location of the
					// image.
					String largeImagePath = myCursor
							.getString(myCursor
									.getColumnIndexOrThrow(Images.ImageColumns.DATA));
					photoUri = Uri.fromFile(new File(largeImagePath));
					// GenUtil.print(TAG"test", "photoUri " + photoUri);
					if (photoUri != null) {
						dateOfPicture = new Date(
								myCursor.getLong(myCursor
										.getColumnIndexOrThrow(Images.ImageColumns.DATE_TAKEN)));
						if (dateOfPicture != null
								&& dateOfPicture.after(dateCameraIntentStarted)) {
							rotateXDegrees = myCursor
									.getInt(myCursor
											.getColumnIndexOrThrow(Images.ImageColumns.ORIENTATION));
						} else {
							photoUri = null;
						}
					}

					// GenUtil.print(TAG"test", "*****rotateXDegrees " +
					// rotateXDegrees);
					if (!myCursor.isAfterLast()) {
						myCursor.moveToNext();
						String largeImagePath3rdLocation = myCursor
								.getString(myCursor
										.getColumnIndexOrThrow(Images.ImageColumns.DATA));
						Date dateOfPicture3rdLocation = new Date(
								myCursor.getLong(myCursor
										.getColumnIndexOrThrow(Images.ImageColumns.DATE_TAKEN)));
						if (dateOfPicture3rdLocation != null
								&& dateOfPicture3rdLocation
										.after(dateCameraIntentStarted)) {
							photoUriIn3rdLocation = Uri.fromFile(new File(
									largeImagePath3rdLocation));
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (myCursor != null && !myCursor.isClosed()) {
					myCursor.close();
				}
			}

			if (photoUri == null) {
				try {
					photoUri = intent.getData();
				} catch (Exception e) {
				}
			}

			if (photoUri == null) {
				photoUri = preDefinedCameraUri;
			}

			try {
				if (photoUri != null
						&& new File(photoUri.getPath()).length() <= 0) {
					if (preDefinedCameraUri != null) {
						Uri tempUri = photoUri;
						photoUri = preDefinedCameraUri;
						preDefinedCameraUri = tempUri;
					}
				}
			} catch (Exception e) {
			}

			photoUri = getFileUriFromContentUri(mContext, photoUri);
			preDefinedCameraUri = getFileUriFromContentUri(mContext,
					preDefinedCameraUri);
			try {
				if (photoUriIn3rdLocation != null) {
					if (photoUriIn3rdLocation.equals(photoUri)
							|| photoUriIn3rdLocation
									.equals(preDefinedCameraUri)) {
						photoUriIn3rdLocation = null;
					} else {
						photoUriIn3rdLocation = getFileUriFromContentUri(
								mContext, photoUriIn3rdLocation);
					}
				}
			} catch (Exception e) {
			}

			if (photoUri != null) {
				onPhotoUriFound();
			} else {
				onPhotoUriNotFound();
			}
		}
	}

	/**
	 * Being called if the photo could be located. The photo's Uri and its
	 * orientation could be retrieved.
	 */
	protected void onPhotoUriFound() {
		// GenUtil.print(TAG"test", "Your photo is stored under: " +
		// photoUri.toString());
	}

	/**
	 * Being called if the photo could not be located (Uri == null).
	 */
	protected void onPhotoUriNotFound() {
		Log.v("test", "Could not find a photoUri that is != null");
	}

	/**
	 * Given an Uri that is a content Uri (e.g.
	 * content://media/external/images/media/1884) this function returns the
	 * respective file Uri, that is e.g. file://media/external/DCIM/abc.jpg
	 * 
	 * @param cameraPicUri
	 * @return Uri
	 */
	private Uri getFileUriFromContentUri(Context mContext, Uri cameraPicUri) {
		try {
			if (cameraPicUri != null
					&& cameraPicUri.toString().startsWith("content")) {
				String[] proj = { Images.Media.DATA };
				Cursor cursor = mContext.getContentResolver().query(
						cameraPicUri, proj, null, null, null);
				cursor.moveToFirst();
				// This will actually give you the file path location of the
				// image.
				String largeImagePath = cursor
						.getString(cursor
								.getColumnIndexOrThrow(Images.ImageColumns.DATA));

				// Log.e("test",
				// "getFileUriFromContentUri " + cameraPicUri.getPath()
				// + " largeImagePath " + largeImagePath);
				return Uri.fromFile(new File(largeImagePath));
			}
			return cameraPicUri;
		} catch (Exception e) {
			return cameraPicUri;
		}
	}

	public static void savaBitmap(String fileName, Bitmap bitmap) {
		if (bitmap == null) {
			return;
		}
		File file = new File(fileName);
		if (file.exists()) {
			try {
				file.delete();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			bitmap.compress(CompressFormat.JPEG, 80, fos);
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/*
	 * 按指定角度旋转图片，原图片未释放。
	 * 
	 * @param bm
	 * 
	 * @param maxWidthOrHeigth 宽图以
	 * 
	 * @param rotateXDegree
	 * 
	 * @return Bitmap
	 */
	public static Bitmap RotateBitmap(Bitmap bm, int maxWidthOrHeigth,
			int rotateXDegree) {
		if (bm == null)
			return bm;
		Matrix matrix = new Matrix();

		int w = bm.getWidth();
		int h = bm.getHeight();
		float scale = (float) 1.0;
		if (w > h) {
			scale = ((float) maxWidthOrHeigth) / h;
		} else if (h > w) {
			scale = ((float) maxWidthOrHeigth) / w;
		}

		matrix.postScale(scale, scale);
		if (rotateXDegree > 0)
			matrix.postRotate(rotateXDegree);
		Bitmap bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
				bm.getHeight(), matrix, false);
		matrix = null;
		return bitmap;
	}

	/**
	 * Reads a Bitmap from an Uri.
	 * 
	 * @param context
	 * @param selectedImage
	 * @return Bitmap
	 */
	public static Bitmap zoomBitmapForUri(Uri selectedImage, int angle,
			int reqWidth, int reqHeight, int maxWidthOrHeigth) {
		Bitmap bm = null;
		int w, h;
		AssetFileDescriptor fileDescriptor = null;
		try {
			fileDescriptor = BaseApp.getInstance().getContentResolver()
					.openAssetFileDescriptor(selectedImage, "r");//Uri 在这里使用了
		} catch (FileNotFoundException e) {
			return null;
		} finally {

			BitmapFactory.Options options = new BitmapFactory.Options();
			try {

				options.inJustDecodeBounds = true;
				BitmapFactory.decodeFileDescriptor(
						fileDescriptor.getFileDescriptor(), null, options);

				if (angle == 0 || angle == 180) {
					if (options.outWidth > options.outHeight) {
						w = reqWidth;
						h = reqHeight;
					} else {
						w = reqHeight;
						h = reqWidth;
					}
				} else {
					w = reqWidth;
					h = reqHeight;
				}
				options.inSampleSize = calculateInSampleSize(options.outWidth,
						options.outHeight, w, h);
				options.inJustDecodeBounds = false;
				// GenUtil.print(TAG"test", "######options.inSampleSize = "
				// + options.inSampleSize + " " + options.outWidth + " "
				// + options.outHeight + " " + w + " " + h);
				options.inJustDecodeBounds = false;
				options.inPreferredConfig = Bitmap.Config.RGB_565;// 在这里设置像素大小
				// options.inScaled = false;
				bm = BitmapFactory.decodeFileDescriptor(
						fileDescriptor.getFileDescriptor(), null, options);
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
				Log.e("systemoutofMemory",
						"*************************************");
				System.gc();

				try {
					bm = BitmapFactory.decodeFileDescriptor(
							fileDescriptor.getFileDescriptor(), null, options);
				} catch (OutOfMemoryError e2) {
					System.gc();
				}
			} finally {
				if (fileDescriptor != null) {
					try {

						fileDescriptor.close();
					} catch (IOException e) {
						return null;
					}
					fileDescriptor = null;
				}
			}
		}
		if (bm == null)
			return null;
		Bitmap rotateBitmap = RotateBitmap(bm, maxWidthOrHeigth, angle);
		if (bm != null) {
			bm.recycle();
			bm = null;
		}
		return rotateBitmap;
	}

	/**
	 * Resize the bitmap
	 * 
	 * @param filePath
	 *            文件路径
	 * @param reqWidth
	 *            目标宽度
	 * @param reqHeight
	 *            目标高度
	 * @param angle
	 *            图片角度
	 * @return
	 */
	public static Bitmap zoomBitmap(String filePath, int reqWidth,
			int reqHeight, int maxWidthOrHeigth) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		int w, h;
		if (options.outWidth > options.outHeight) {
			w = reqWidth;
			h = reqHeight;
		} else {
			w = reqHeight;
			h = reqWidth;
		}

		options.inSampleSize = calculateInSampleSize(options.outWidth,
				options.outHeight, w, h);
		options.inJustDecodeBounds = false;
		// GenUtil.print(TAG"test", "######options.inSampleSize = " +
		// options.inSampleSize
		// + " " + options.outWidth + " " + options.outHeight + " " + w
		// + " " + h);
		Bitmap result = null;
		try {
			result = BitmapFactory.decodeFile(filePath, options);
			// GenUtil.print(TAG"test",
			// "result w " + result.getWidth() + " h "
			// + result.getHeight());
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			System.gc();
			try {
				result = BitmapFactory.decodeFile(filePath, options);
			} catch (OutOfMemoryError e2) {
				e2.printStackTrace();
				return null;
			}
		}
		Bitmap rotateBitmap = RotateBitmap(result, maxWidthOrHeigth, 0);
		if (result != null) {
			result.recycle();
			result = null;
		}
		return rotateBitmap;
	}

	// public static List<String> fileOutList
	public static int calculateInSampleSize(int srcWidth, int srcHeight,
			int reqWidth, int reqHeight) {
		int inSampleSize = 1;
		if (srcHeight > reqHeight || srcWidth > reqWidth) {
			float scaleW = (float) srcWidth / (float) reqWidth;
			float scaleH = (float) srcHeight / (float) reqHeight;
			float sample = scaleW > scaleH ? scaleW : scaleH;
			// 只能是2的次幂
			if (sample < 3)
				inSampleSize = (int) sample;
			else if (sample < 6.5)
				inSampleSize = 4;
			else if (sample < 8)
				inSampleSize = 8;
			else
				inSampleSize = (int) sample;
		}
		return inSampleSize;
	}

	public static final String CAMERA_IMAGE_BUCKET_NAME = Environment
			.getExternalStorageDirectory().toString() + "/DCIM/Camera";
	public static final String CAMERA_IMAGE_BUCKET_ID = getBucketId(CAMERA_IMAGE_BUCKET_NAME);

	/**
	 * Matches code in MediaProvider.computeBucketValues. Should be a common
	 * function.
	 */
	public static String getBucketId(String path) {
		return String.valueOf(path.toLowerCase().hashCode());
	}

	public static List<CameraInfo> getCameraImages() {
		Calendar c1 = new GregorianCalendar();
		c1.set(Calendar.HOUR_OF_DAY, 0);
		c1.set(Calendar.MINUTE, 0);
		c1.set(Calendar.SECOND, 0);
		long start = c1.getTimeInMillis();
		c1.set(Calendar.HOUR_OF_DAY, 23);
		c1.set(Calendar.MINUTE, 59);
		c1.set(Calendar.SECOND, 59);
		long stop = c1.getTimeInMillis();
		return getCameraImages(start, stop);
	}

	public static ArrayList<CameraInfo> resultCameraInfo = new ArrayList<CameraInfo>();

	public static List<CameraInfo> getCameraImages(long startTimer,
			long stopTimer) {
		// Log.e("test", "getCameraImages start " + startTimer + " stop "
		// + stopTimer);
		String[] largeFileProjection = { Images.ImageColumns.DATA,
				Images.ImageColumns.ORIENTATION };

		// final String selection = MediaStore.Images.Media.BUCKET_ID + " = ? ";
		// final String[] selectionArgs = { CAMERA_IMAGE_BUCKET_ID };

		final String selection = Images.Media.BUCKET_ID
				+ " = ? and " + Images.ImageColumns.DATE_TAKEN
				+ ">=? and " + Images.ImageColumns.DATE_TAKEN
				+ "<=? ";
		final String[] selectionArgs = { CAMERA_IMAGE_BUCKET_ID,
				startTimer + "", stopTimer + "" };
		final Cursor myCursor = BaseApp
				.getInstance()
				.getApplicationContext()
				.getContentResolver()
				.query(Images.Media.EXTERNAL_CONTENT_URI, largeFileProjection,
						selection, selectionArgs, null);

		resultCameraInfo.clear();
		CameraInfo mCameraInfo;
		long timer;
		int rotateXDegrees;
		if (myCursor.moveToFirst()) {
			final int dataColumn = myCursor
					.getColumnIndexOrThrow(Images.Media.DATA);
			do {
				final String data = myCursor.getString(dataColumn);

				File f = new File(data);
				// Log.e("test", "getCameraImages data " + data);
				if (f.exists()) {
					timer = f.lastModified();

					// Log.e("test", "getCameraImages timer " + timer);
					if (timer >= startTimer && timer <= stopTimer) {

						rotateXDegrees = myCursor
								.getInt(myCursor
										.getColumnIndexOrThrow(Images.ImageColumns.ORIENTATION));
						// Log.e("test", "getCameraImages data " + data
						// + "  rotateXDegrees " + rotateXDegrees
						// + " timer " + timer);
						mCameraInfo = new CameraInfo(data, rotateXDegrees,
								timer);
						resultCameraInfo.add(mCameraInfo);
					}
				}

			} while (myCursor.moveToNext());
		}
		myCursor.close();

		if (resultCameraInfo.size() > 0) {
			// Intent mIntent = new Intent(MyApp.WEB_INTERFACE_EVNET);
			// mIntent.putExtra(TourWebAppInterface.WEBINFTERFACE_TYPE,
			// TourWebAppInterface.ACTION_TYPE_ADD_TRACE_IMAGE);
			// // 发送广播
			// MyApp.getInstance().sendBroadcast(mIntent);
		}
		return resultCameraInfo;
	}

}
