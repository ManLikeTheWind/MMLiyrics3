package com.dxiang.mring3.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.dxiang.mring3.R;
import com.dxiang.mring3.utils.UtilLog;
import com.dxiang.mring3.utils.Utils;


public class FileHelper {

	/**
	 * 由指定的路径和文件名创建文件
	 * 
	 * @param path
	 * @param name
	 * @return
	 * @throws IOException
	 */
	public static File createFile(String path, String name) throws IOException {
		File folder = new File(path);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		File file = new File(path + "/" + name);
		if (!file.exists()) {
			file.createNewFile();
		}
		return file;
	}

	/**
	 * 判断文件是否存在
	 * 
	 * @param path
	 * @param name
	 * @return
	 */
	public static boolean fileExist(String path, String name) {
		File file = new File(path + name);
		if (file.exists() && !file.isDirectory()) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @Title: copyDrawableFiles
	 * @Description: 拷贝drawable下的图片到指定位置
	 * @param name
	 * @param path
	 * @return boolean
	 */
	public static boolean copyDrawableFiles(Context c,String name, String path) {
		try {
			int id = R.drawable.class.getField(name).getInt(R.drawable.class);
			Bitmap bitmap = BitmapFactory.decodeResource(c.getResources(), id);
			savBitmap(bitmap, name + ".png", path);
		} catch (IllegalArgumentException e) {
			UtilLog.e(e.getMessage());
			return false;
		} catch (IllegalAccessException e) {
			
			UtilLog.e(e.getMessage());
			return false;
		} catch (NoSuchFieldException e) {
			UtilLog.e(e.getMessage());
			return false;
		} catch (IOException e) {
			UtilLog.e(e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @Title: savBitmap
	 * @Description: 保存bitmap成图片
	 * @param bitmap
	 * @param name
	 * @param path
	 * @throws IOException
	 */
	public static void savBitmap(Bitmap bitmap, String name, String path) throws IOException {
		if (bitmap == null) {
			return;
		}
		File file = createFile(path, name);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
		} catch (FileNotFoundException e) {
			UtilLog.e(e.getMessage());
		} finally {
			Utils.closeStream(fos);
		}
	}

	/**
	 * 
	 * @Title: savBitmap
	 * @Description: 保存bitmap成图片
	 * @param bitmap
	 * @param name
	 *            完整的文件名（带有路径）
	 * @param path
	 * @throws IOException
	 */
	public static void savBitmap(Bitmap bitmap, String name) throws IOException {
		if (bitmap == null) {
			return;
		}
		File f = new File(name);
		if (!f.exists()) {
			f.createNewFile();
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(name);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
		} catch (FileNotFoundException e) {
			UtilLog.e(e.getMessage());
		} finally {
			Utils.closeStream(fos);
		}
	}

	/**
	 * @Title: getTestData
	 * @Description: 该方法用于读取测试数据，例如一串sql,json,xml等
	 * @param @param rawid raw目录下对应的文件名称
	 * @return String
	 */
	public static String getTestData(Context c,int rawid) {
		InputStream is = null;
		InputStreamReader reader = null;
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		try {
			is = c.getResources().openRawResource(rawid);
			reader = new InputStreamReader(is);
			br = new BufferedReader(reader);
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			Utils.closeStream(br);
			Utils.closeStream(reader);
			Utils.closeStream(is);
		}
		return sb.toString();
	}

	/**
	 * @Title: deleteFile
	 * @Description: 删除文件
	 * @param path
	 * @param name
	 * @return boolean
	 */
	public static boolean deleteFile(String path, String name) {
		if (fileExist(path, name)) {
			return false;
		}
		File file = new File(path, name);
		file.delete();
		return true;
	}

	/**
	* @Title: copyFile
	* @Description: 拷贝文件
	* @param @param srcPath
	* @param @param srcName
	* @param @param desPath
	* @param @param desName
	* @return boolean
	 */
	public static boolean copyFile(String srcPath, String srcName, String desPath, String desName) {
		if (!fileExist(srcPath, srcName)) {
			return false;
		}
		
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		try {
			File inFile = new File(srcPath, srcName);
			File outFile = new File(desPath, desName);
			
			if(!fileExist(desPath, desName)){
				createFile(desPath, desName);
			}
			
			fis = new FileInputStream(inFile);
			bis = new BufferedInputStream(fis);

			fos = new FileOutputStream(outFile);
			bos = new BufferedOutputStream(fos);

			byte[] buffer = new byte[1024 * 8];
			int len = -1;
			while ((len = fis.read(buffer)) != -1) {
				bos.write(buffer, 0, len);
			}
			bos.flush();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Utils.closeStream(bis);
			Utils.closeStream(fis);
			Utils.closeStream(bos);
			Utils.closeStream(fos);
		}
		return false;
	}
}
