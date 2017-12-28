package com.dxiang.mring3.utils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
//import android.media.tv.TvContract.Channels;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

/**
 * @action Bitmap ����ʱ��ͼƬ�����һЩ��������ת��������С
 * 
 * @author DongXiang
 *
 * @time 2016��7��27������4:24:23
 * 
 * @version 1.0
 *
 * strFilePath����·��  : ͼƬ���Բ�����
 * 					�ⲿ����·�� String filePath = Environment.getExternalStorageDirectory() + File.separator + "test.jpg"; 
 * 					�ڲ����� String dir = FileUtils.getCacheDir(context) + "Image" + File.separator+"test.jpg";
 * 
 * Ŀǰ���ɵ�ͼƬ��PNG ͼ��������Ҫ����ȫ��PNG����JPEG��  ͼƬ���� �����޸�
 * 
 */
public class BitmapHelper {

	/**
	 * �ַ���  �ļ�·�� ת����Uri
	 * @param filePath
	 * @return
	 */
	public static Uri filePath2Uri(String filePath){
		return Uri.fromFile(new File(filePath));
	}
	/**
	 * Uri ��ȡ��Ӧ���ַ�����ַ
	 * @param uri
	 * @return
	 */
	public static String uri2FilePath(Uri uri){
		return  uri.getPath();
	}
	
	/**
	 * get the orientation of the bitmap {@link ExifInterface}
	 *
	 * @param path
	 * @return
	 */
	public static int getDegress(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	/**
	 * rotate the bitmap
	 *
	 * @param bitmap
	 * @param degress
	 * @return
	 */
	public static Bitmap rotateBitmap(Bitmap bitmap, int degress) {
		if (bitmap != null) {
			Matrix m = new Matrix();
			m.postRotate(degress);
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), m, true);
			return bitmap;
		}
		return bitmap;
	}

	/**
	 * caculate the bitmap sampleSize
	 * 
	 * ��ȡbitmap��ѹ��������
	 * 
	 * @param options
	 * @param rqsW  
	 * @param rqsH
	 * @return rqsW��rqsH=0 ����ѹ��������ֵ��1������ȡ�� ��ѹ�������� >1 ��===ȡ��Сֵ
	 */
	public static int caculateInSampleSize(Options options, int rqsW, int rqsH) {
		int height = options.outHeight;
		int width = options.outWidth;
		// ��Ҫ�޸���������Ķ�Ӧ�趨�Ŀ���ͬ��
		if (width > height) {//��֤height ���
			int temp = width;
			width = height;
			height = temp;
		}
		if (rqsW > rqsH) {//��֤rqsh ���
			int rqsT = rqsH;
			rqsH = rqsW;
			rqsW = rqsT;
		}

		int inSampleSize = 1;
		if (rqsW == 0 || rqsH == 0)
			return 1;
		if (height > rqsH || width > rqsW) {
			int heightRatio = Math.round((float) height / (float) rqsH);
			int widthRatio = Math.round((float) width / (float) rqsW);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}

	/**
	 * ������Ҫѹ����ĳ�ߴ�ѹ��ָ��·����ͼƬ�����õ�ͼƬ���󣺿�������ͼƬ�� ���ط��
	 * 
	 *  options.inPurgeable = true;      Bitmap �����Ƿ�ʹ�������û���
	 *  options.inInputShareable = true;	���ʹ�õ�, �ǿ����Ƿ��� inputfile ���������,// ���ÿɲ���
	 *  			 ���������, ��ôҪʵ�� inPurgeable ���ƾ���Ҫ����һ�� file ����, ������ϵͳ��Ҫ decode ��ʱ�򴴽�һ�� bitmap ����.
	 *
	 *	Bitmap bmp = BitmapFactory.decodeFile(path, options);
	 *
	 * @param path
	 * @param rqsW
	 * @param rqsH
	 * @return
	 */
	public static Bitmap compressBitmap(String path, int rqsW, int rqsH) {
		Options options = getBitmapOptions(path);
		options.inSampleSize = caculateInSampleSize(options, rqsW, rqsH);
		options.inPreferredConfig = Bitmap.Config.ARGB_4444; //����Bitmap�� ���ط��
		
		return BitmapFactory.decodeFile(path, options);
	}

	/**
	 * ѹ��ָ��·��ͼƬ�������䱣���ڻ���Ŀ¼�У�ͨ��isDelSrc�ж��Ƿ�ɾ��Դ�ļ�������ȡ��������ͼƬ·��
	 *  ���ص�ͼƬ��  PNG������80 
	 *
	 * @param context
	 * @param srcPath
	 * @param rqsW
	 * @param rqsH
	 * @param isDelSrc
	 * @return
	 */
	public static String compressBitmap(Context context, String srcPath,
			int rqsW, int rqsH, boolean isDelSrc) {
		int degree = getDegress(srcPath);
		Bitmap bitmap = compressBitmap(srcPath, rqsW, rqsH);// ���ݳ����Լ�ͼƬ�ĳ���õ�����ͼƬ
		File srcFile = new File(srcPath);
		String desPath = getImageCacheDir(context) + srcFile.getName();
		try {
			if (degree != 0)
				bitmap = rotateBitmap(bitmap, degree);
			File file = new File(desPath);
			FileOutputStream fos = new FileOutputStream(file);
			bitmap.compress(CompressFormat.PNG, 80, fos);// 80��ͼƬ����
			fos.close();
			if (isDelSrc)
				srcFile.deleteOnExit();

		} catch (Exception e) {
		}

		bitmap.recycle();
		System.gc();

		return desPath;
	}

	/**
	 * ѹ��ĳ���������е�ͼƬ�����Խ������������ѹ�����⣬���õ�ͼƬ����
	 *
	 * @return Bitmap {@link Bitmap}
	 */
	public static Bitmap compressBitmap(InputStream is, int reqsW, int reqsH) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ReadableByteChannel channel = Channels.newChannel(is);
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			while (channel.read(buffer) != -1) {
				buffer.flip();
				while (buffer.hasRemaining())
					baos.write(buffer.get());
				buffer.clear();
			}
			byte[] bts = baos.toByteArray();
			Bitmap bitmap = compressBitmap(bts, reqsW, reqsH);
			is.close();
			channel.close();
			baos.close();
			return bitmap;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * ѹ��ָ��byte[]ͼƬ�����õ�ѹ�����ͼ��
	 *
	 * @param bts
	 * @param reqsW
	 * @param reqsH
	 * @return
	 */
	public static Bitmap compressBitmap(byte[] bts, int reqsW, int reqsH) {
		Options options = new Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(bts, 0, bts.length, options);
		options.inSampleSize = caculateInSampleSize(options, reqsW, reqsH);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeByteArray(bts, 0, bts.length, options);

	}

	/**
	 * ѹ���Ѵ��ڵ�ͼƬ���󣬲�����ѹ�����ͼƬ��   ���ص�ͼƬ��ʽ�� PNG������100��
	 *
	 * @param bitmap
	 * @param reqsW
	 * @param reqsH
	 * @return
	 */
	public static Bitmap compressBitmap(Bitmap bitmap, int reqsW, int reqsH) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			
			bitmap.compress(CompressFormat.PNG, 100, baos);
			byte[] bts = baos.toByteArray();
			Bitmap res = compressBitmap(bts, reqsW, reqsH);
			baos.close();
			return res;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return bitmap;
		}
	}

	/**
	 * ѹ����ԴͼƬ��������ͼƬ����
	 *
	 * @param res ��Դ  context.getResources()
	 *            {@link Resources}
	 * @param resID  ��Դid
	 * @param reqsW 
	 * @param reqsH
	 * @return
	 */
	public static Bitmap compressBitmap(Resources res, int resID, int reqsW,int reqsH) {
			
		Options options = new Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resID, options);
		options.inSampleSize = caculateInSampleSize(options, reqsW, reqsH);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resID, options);
	}

	/**
	 * �õ�ָ��·��ͼƬ��options
	 *
	 * @param srcPath
	 * @return Options {@link Options}
	 */
	public static Options getBitmapOptions(String srcPath) {
		Options options = new Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(srcPath, options);
		options.inJustDecodeBounds = false;
		return options;
	}

	/**
	 * ��ȡͼƬ����·��
	 *
	 * @param context
	 * @return
	 */
	public static String getImageCacheDir(Context context) {

		String dir = FileUtils.getCacheDir(context) + "Image" + File.separator;
		File file = new File(dir);
		if (!file.exists())
			file.mkdirs();
		return dir;
	}

	/**
	 * Bitmap ���浽ָ���� strFilePath
	 * 
	 * ����ͼƬ�ĸ�ʽ������ͼƬ���������������������޸ģ�����ѹ��ͼƬ
	 * 
	 * @param strFilePath
	 *            ��ַ FilePath= /storage/emulated/0/ 
	 *            ���磺�ⲿ����·�� String filePath = Environment.getExternalStorageDirectory() + File.separator ; //+ "test.jpg"; 
	 *           
	 *           	       �ڲ����� String dir = FileUtils.getCacheDir(context) + "Image" + File.separator; //+"test.jpg";
	 * @param fileName
	 *            �ļ�������Ҫ���к�׺ "test.jpg"
	 * @param bitmap
	 *            ������Ϊ��,·��Ϊ�յ�ʱ�򣬻��Լ�����
	 * @return true �洢�ɹ�
	 */
	public static boolean saveBitmap2File(String strFilePath, String fileName,
			Bitmap bitmap) {

		if (bitmap == null) {
			Log.e("ͼƬ��ַ����Ϊ��", "ͼƬ��ַ����Ϊ��");
			return false;
		}
		if (!strFilePath.endsWith(File.separator)) {
			strFilePath += File.separator;
		}

		// String path = getSDPath() +"/revoeye/";
		File dirFile = new File(strFilePath);
		if (!dirFile.exists()) {
			dirFile.mkdir();
		}
//		File bitmapFile = new File(strFilePath + fileName);
		saveBitmap2FilePath(strFilePath + fileName, bitmap);
//		BufferedOutputStream bos;
//		try {
//			bos = new BufferedOutputStream(new FileOutputStream(bitmapFile));
//			bitmap.compress(Bitmap.CompressFormat.PNG, 80, bos); // ����ͼƬ�ĸ�ʽ��ͼƬ������ҪҪ���浽�ĸ�������
//			bos.flush();
//			bos.close();
//
//			return true;
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		return false;
	}
	
	/**
	 * ����ͼƬ�ĸ�ʽ������ͼƬ���������������������޸ģ�����ѹ��ͼƬ
	 * 
	 * ������ת�� ͼƬ������ת��û��ת��ͼƬ�����Դ�������
	 * �Զ��ж�ͼƬ�Ƿ���ת�����洢û����ת�Ƕȵ�bitmap�����ļ���
	 * @param FilePath ����·��  
	 * 					�ⲿ����·�� String filePath = Environment.getExternalStorageDirectory() + File.separator + "test.jpg"; 
	 * 					�ڲ����� String dir = FileUtils.getCacheDir(context) + "Image" + File.separator+"test.jpg";
	 * @return 
	 */
	public static boolean saveBitmap2RotateFilePath(String FilePath) {
		File file = new File(FilePath);
		if (file.exists()) {
			Log.e("filePath", file.getAbsolutePath() + " == FilePath= "
					+ FilePath);

			Bitmap bitmapRotate = BitmapHelper.rotateBitmap(
					BitmapFactory.decodeFile(FilePath),
					BitmapHelper.getDegress(FilePath));

			Log.e("bitmapDegress=", "== " + BitmapHelper.getDegress(FilePath));
			
			saveBitmap2FilePath(FilePath, bitmapRotate);
			return true;
		}
		return false;

	}
	/**
	 * bitmap �浽ָ���� ����·����
	 * 
	 * ����ͼƬ�ĸ�ʽ������ͼƬ���������������������޸ģ�����ѹ��ͼƬ
	 * 
	 * 
	 * @param strFilePath����·��  : ͼƬ���Բ�����
	 * 					�ⲿ����·�� String filePath = Environment.getExternalStorageDirectory() + File.separator + "test.jpg"; 
	 * 					�ڲ����� String dir = FileUtils.getCacheDir(context) + "Image" + File.separator+"test.jpg";
	 * @param bitmap 
	 * @return
	 */
	public static boolean saveBitmap2FilePath(String strFilePath, Bitmap bitmap) {

		if (bitmap == null) {
			Log.e("ͼƬ��ַ����Ϊ��", "ͼƬ��ַ����Ϊ��");
			return false;
		}
		
		File bitmapFile = new File(strFilePath);
		BufferedOutputStream bos;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(bitmapFile));
			bitmap.compress(CompressFormat.PNG, 80, bos); // ����ͼƬ�ĸ�ʽ��ͼƬ������ҪҪ���浽�ĸ�������
			bos.flush();
			bos.close();

			return true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}
	
}

class FileUtils {
	/**
	 * ��ȡapp �Ļ���Ŀ¼
	 * 
	 * @param context
	 * @return
	 */
	public static String getCacheDir(Context context) {

		File cacheDir = context.getCacheDir();// �ļ�����Ŀ¼ΪgetFilesDir();
		String cachePath = cacheDir.getPath();
		return cachePath;
	}
}
