package com.dxiang.mring3.utils;

// last update:  2010-10-30

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.StringTokenizer;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

public class FileUtil {

	public static Bitmap getBitmapFromLargeHeight(Resources res, int id,
			int width, int hei) {
		if (width <= 0)
			return null;

		int iWidthOrg = 0;
		int iHeightOrg = 0;

		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			Bitmap bitmap = BitmapFactory.decodeResource(res, id, options);
			options.inJustDecodeBounds = false;
			// GenUtil.print(TAG, "width = " + width + "IMG=" + "h=" +
			// options.outHeight
			// + ", w=" + options.outWidth + ", img=" + strImagePath);

			iWidthOrg = options.outWidth;
			iHeightOrg = options.outHeight;

			int scale = iHeightOrg / hei;

			if (scale == 0) {
				options.inSampleSize = 1;
			} else {
				options.inSampleSize = scale;
			}

			// Log.i(TAG, "scale=" + scale + ", sample=" + scale);
			bitmap = BitmapFactory.decodeResource(res, id, options);

			if (iWidthOrg <= width) {
				return bitmap;
			}

			int wedth = (int) ((float) hei * (float) iWidthOrg / (float) iHeightOrg);

			Bitmap bmResize = Bitmap.createScaledBitmap(bitmap, wedth, hei,
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

	public static Bitmap getBitmapFromLargeWidth(String strImagePath,
			int width, boolean circle, Context context) {
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
			// GenUtil.print(TAG, "width = " + width + "IMG=" + "h=" +
			// options.outHeight
			// + ", w=" + options.outWidth + ", img=" + strImagePath);

			iWidthOrg = options.outWidth;
			iHeightOrg = options.outHeight;

			int scale = iWidthOrg / width;

			if (scale == 0) {
				options.inSampleSize = 1;
			} else {
				options.inSampleSize = scale;
			}
			bitmap = BitmapFactory.decodeFile(strImagePath, options);
			if (iWidthOrg <= width) {
				if (circle) {
					return ImageUtil.toRoundBitmap(bitmap, context, 10);
				} else
					return bitmap;
			}

			int height = (int) ((float) iHeightOrg * (float) width / (float) iWidthOrg);

			Bitmap bmResize = Bitmap.createScaledBitmap(bitmap, width, height,
					true);

			if (bitmap != null) {
				bitmap.recycle();
				bitmap = null;
			}
			if (circle) {
				return ImageUtil.toRoundBitmap(bmResize, context, 10);
			} else {
				return bmResize;
			}
		} catch (Exception e) {
		}
		return null;
	}

	public static Bitmap getsmallBitmapUseWidth(Bitmap bmp, int width) {
		try {
			Bitmap bmResize = Bitmap.createScaledBitmap(bmp, width,
					width * 2 / 3, true);
			if (bmp != null) {
				bmp.recycle();
				bmp = null;
			}
			return bmResize;
		} catch (Exception e) {
		}
		return null;
	}

	private static String TAG = "FileUtil";

	public static void savaBitmap(String fileName, Bitmap bitmap)
			throws IOException {
		if (bitmap == null) {
			return;
		}
		String savafolder = fileName.substring(0, fileName.toString()
				.lastIndexOf("/"));
		try {
			FileUtil.createFolder(savafolder);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File file = new File(fileName);
		if (!file.exists()) {
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			bitmap.compress(CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
		}
		if (bitmap != null) {
			bitmap.recycle();
			bitmap = null;
		}
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

	/**
	 * Resize the bitmap
	 * 
	 * @param filePath
	 *            文件路径
	 * @param reqWidth
	 *            目标宽度
	 * @param reqHeight
	 *            目标高度
	 * @return
	 */
	public static Bitmap zoomBitmap(String filePath, int reqWidth, int reqHeight) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		options.inSampleSize = calculateInSampleSize(options.outWidth,
				options.outHeight, reqWidth, reqHeight);
		options.inJustDecodeBounds = false;
		Bitmap result = null;
		try {
			result = BitmapFactory.decodeFile(filePath, options);
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
		return result;
	}

	public static byte[] encodeStream(InputStream is) {
		byte[] btBuffer = null;
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] b = new byte[1024];
			int n;
			while ((n = is.read(b)) != -1) {
				out.write(b, 0, n);
			}
			btBuffer = out.toByteArray();
			out.close();
			// for (int iIndex = 0; iIndex < btBuffer.length; iIndex++) {
			// byte tmpByte = btBuffer[iIndex];
			// btBuffer[iIndex] = (byte) ((0x0f & tmpByte) << 4 | (((0xf0 &
			// tmpByte) >> 4) & 0x0f));
			// }
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return btBuffer;
	}

	/**
	 * ��ȡ�ı��ļ�����
	 * 
	 * @param filePathAndName
	 *            ����������·�����ļ���
	 * @param encoding
	 *            �ı��ļ��򿪵ı��뷽ʽ
	 * @return �����ı��ļ�������
	 */
	public static String readTxt(String filePathAndName, String encoding,
			int nLine) throws IOException {
		encoding = encoding.trim();
		StringBuffer str = new StringBuffer("");
		String st = "";
		int iCount = 0;
		try {
			FileInputStream fs = new FileInputStream(filePathAndName);
			InputStreamReader isr;
			if (encoding.equals("")) {
				isr = new InputStreamReader(fs);
			} else {
				isr = new InputStreamReader(fs, encoding);
			}
			BufferedReader br = new BufferedReader(isr);
			try {
				String data = "";
				while ((data = br.readLine()) != null) {
					str.append(data + "\n");
					if (nLine > 0) {
						iCount++;
						if (iCount > nLine)
							break;
					}
				}
			} catch (IOException e) {
				str.append(e.toString());
			}
			st = str.toString();
		} catch (IOException es) {
			throw es;
		}
		return st;
	}

	/**
	 * ��ȡ�ı��ļ�����
	 * 
	 * @param filePathAndName
	 *            ����������·�����ļ���
	 * @param encoding
	 *            �ı��ļ��򿪵ı��뷽ʽ
	 * @return �����ı��ļ�������
	 */
	public static String readTxt(String filePathAndName, String encoding)
			throws IOException {
		return readTxt(filePathAndName, encoding, 0);
	}

	/**
	 * �����ı��ļ�������
	 * 
	 * @param file
	 *            �ļ�����
	 * @return �����ı��ļ������� nLine = 0 ��ʾ��ȡȫ���ļ�����
	 */
	public static String readFile(File file, int nLine) {
		String output = "";
		int iLineCounter = 0;

		if (file.exists()) {
			if (file.isFile()) {
				try {
					BufferedReader input = new BufferedReader(new FileReader(
							file));
					StringBuffer buffer = new StringBuffer();
					String text;
					while ((text = input.readLine()) != null) {
						buffer.append(text + "\n");
						if (nLine > 0) {
							iLineCounter++;
							if (iLineCounter > nLine)
								break;
						}
					}
					input.close();
					output = buffer.toString();
				} catch (IOException ioException) {
					System.err.println("File Error!");
				}
			} else if (file.isDirectory()) {
				String[] dir = file.list();
				output += "Directory contents:\n";
				for (int i = 0; i < dir.length; i++) {
					output += dir[i] + "\n";
				}
			}
		} else {
			System.err.println("Does not exist!");
		}
		return output;
	}

	/**
	 * �½��ļ�
	 * 
	 * @param filePathAndName
	 *            �ı��ļ�������·�����ļ���
	 * @param fileContent
	 *            �ı��ļ�����
	 * @return
	 * @throws Exception
	 */
	public static void createFile(String filePathAndName, String fileContent)
			throws Exception {

		try {
			String filePath = filePathAndName;
			filePath = filePath.toString();
			File myFilePath = new File(filePath);
			if (!myFilePath.exists()) {
				myFilePath.createNewFile();
			}
			FileWriter resultFile = new FileWriter(myFilePath);
			PrintWriter myFile = new PrintWriter(resultFile);
			String strContent = fileContent;
			myFile.println(strContent);
			myFile.close();
			resultFile.close();
		} catch (Exception e) {

			throw new Exception("�����ļ���������", e);
		}
	}

	/**
	 * 有编码方式的文件创建
	 * 
	 * @param filePathAndName
	 *            文本文件完整绝对路径及文件名
	 * @param fileContent
	 *            文本文件内容
	 * @param encoding
	 *            编码方式 例如 GBK 或者 UTF-8
	 * @return
	 * @throws Exception
	 */
	public static void createFile(String filePathAndName, String fileContent,
			String encoding) throws Exception {

		try {
			String filePath = filePathAndName;
			filePath = filePath.toString();
			File myFilePath = new File(filePath);
			if (!myFilePath.exists()) {
				myFilePath.createNewFile();
			}
			PrintWriter myFile = new PrintWriter(myFilePath, encoding);
			String strContent = fileContent;
			myFile.println(strContent);
			myFile.close();
		} catch (Exception e) {
			throw new Exception("创建文件操作出错", e);
		}
	}

	/**
	 * 创建文件夹
	 * 
	 * @param filePath
	 * @throws Exception
	 */
	public static String createFolder(String filePath) throws Exception {
		File file = new File(filePath);
		try {
			if (!file.exists()) {
				if (!file.mkdir()) {
					throw new IOException("创建文件夹：" + filePath + "出错");
				}
			}
			return filePath;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 创建文件夹
	 * 
	 * @param filePath
	 *            recurring to create all folders 产生所有文件夹
	 * @throws Exception
	 */
	public static String createFolders(String filePath) throws Exception {
		
		File file = new File(filePath);
		try {
			if (!file.exists()) {
				if (!file.mkdirs()) {
					throw new IOException("创建文件夹：" + filePath + "出错");
				}
			}
			return filePath;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 多级目录创建 （用上面的那个就可以了，这个不用了）
	 * 
	 * @param folderPath
	 *            准备要在本级目录下创建新目录的目录路径 例如 E:\\Test\\java\\
	 * @param paths
	 *            无限级目录参数，各级目录以单数线区分 例如 a|b|c
	 * @return 返回创建文件夹后的路径 例如 c:myfa c
	 * @throws Exception
	 */
	public static String createFolders(String folderPath, String paths)
			throws Exception {
		String txts = folderPath;
		try {
			String txt;
			txts = folderPath;
			StringTokenizer st = new StringTokenizer(paths, "|");
			for (int i = 0; st.hasMoreTokens(); i++) {
				txt = st.nextToken().trim();
				txts = createFolder(txts + txt + "/");
			}
		} catch (Exception e) {
			throw new Exception("创建目录操作出错！", e);
		}
		return txts;
	}

	/**
	 * 复制单个文件
	 * 
	 * @param oldPathFile
	 *            准备复制的文件源
	 * @param newPathFile
	 *            拷贝到新绝对路径带文件名
	 * @return
	 * @throws Exception
	 */
	public static void copyFile(String oldPathFile, String newPathFile)
			throws Exception {
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPathFile);
			if (oldfile.exists()) { // 文件存在时
				InputStream inStream = new FileInputStream(oldPathFile); // 读入原文件
				FileOutputStream fs = new FileOutputStream(newPathFile);
				byte[] buffer = new byte[1444];
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // 字节数 文件大小
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * <p>
	 * 将sourceFolder文件夹下的内容复制到destinationFolder文件夹下
	 * </p>
	 * <p>
	 * 如destinationFolder文件夹不存在则自动创建
	 * </p>
	 * 
	 * @param sourceFolder
	 *            源文件夹 如：C:\\aaa
	 * @param fileArray
	 *            list of all files including files in subfolder
	 * @throws Exception
	 */
	public static void getAllFilePaths(String sourceFolder,
			ArrayList<String> fileArray) throws Exception {
		try {

			File file = new File(sourceFolder);
			if (!file.isDirectory())
				return;

			File[] filelist = file.listFiles();

			for (int i = 0; i < filelist.length; i++) {
				if (filelist[i].isDirectory()) {
					getAllFilePaths(filelist[i].getAbsolutePath(), fileArray);
				} else {
					String filePath = filelist[i].getAbsolutePath();
					fileArray.add(filePath);
				}
			}

		} catch (Exception e) {
			throw new Exception("获取整个文件夹内容操作出错", e);
		}
	}

	/**
	 * <p>
	 * 将sourceFolder文件夹下的内容复制到destinationFolder文件夹下
	 * </p>
	 * <p>
	 * 如destinationFolder文件夹不存在则自动创建
	 * </p>
	 * 
	 * @param sourceFolder
	 *            源文件夹 如：C:\\aaa
	 * @param destinationFolder
	 *            目标文件夹 D:\\java
	 * @throws Exception
	 */
	public static void copyFolder(String sourceFolder, String destinationFolder)
			throws Exception {
		try {
			new File(destinationFolder).mkdirs(); // 如果文件夹不存在 则建立新文件夹
			File a = new File(sourceFolder);
			String[] file = a.list();
			File temp = null;
			for (int i = 0; i < file.length; i++) {
				if (sourceFolder.endsWith(File.separator)) {
					temp = new File(sourceFolder + file[i]);
				} else {
					temp = new File(sourceFolder + File.separator + file[i]);
				}
				if (temp.isFile()) {
					FileInputStream input = new FileInputStream(temp);
					FileOutputStream output = new FileOutputStream(
							destinationFolder + "/"
									+ (temp.getName()).toString());
					byte[] b = new byte[1024 * 5];
					int len;
					while ((len = input.read(b)) != -1) {
						output.write(b, 0, len);
					}
					output.flush();
					output.close();
					input.close();
				}
				if (temp.isDirectory()) {// 如果是子文件夹
					copyFolder(sourceFolder + "/" + file[i], destinationFolder
							+ "/" + file[i]);
				}
			}
		} catch (Exception e) {
			throw new Exception("复制整个文件夹内容操作出错", e);
		}
	}

	/**
	 * <p>
	 * 将sourceFolder文件夹下的内容转码到destinationFolder文件夹下，利用sourceDecode and
	 * destEncode 参数
	 * </p>
	 * <p>
	 * 如destinationFolder文件夹不存在则自动创建
	 * </p>
	 * 
	 * @param sourceFolder
	 *            源文件夹 如：C:\\aaa
	 * @param destinationFolder
	 *            目标文件夹 D:\\java
	 * @throws Exception
	 */
	public static void convFolder(String sourceFolderIn, String sourceDecode,
			String destinationFolderOut, String destEncode, String filterIn)
			throws Exception {
		try {
			String sourceFolder = addFolderSeparator(sourceFolderIn);
			String destinationFolder = addFolderSeparator(destinationFolderOut);

			new File(destinationFolder).mkdirs(); // 如果文件夹不存在 则建立新文件夹
			File a = new File(sourceFolder);
			String[] file = a.list();
			File temp = null;
			for (int i = 0; i < file.length; i++) {
				temp = new File(sourceFolder + file[i]);
				String source = sourceFolder + file[i];
				String dest = destinationFolder + file[i];
				if (temp.isFile()) {
					if (temp.getName().toLowerCase().indexOf(filterIn) > -1) {
						convFile(source, sourceDecode, dest, destEncode);
					}
				}
				if (temp.isDirectory()) {// 如果是子文件夹
					convFolder(source, sourceDecode, dest, destEncode, filterIn);
				}
			}
		} catch (Exception e) {
			throw new Exception("转换整个文件夹内容操作出错", e);
		}
	}

	public static String addFolderSeparator(String strFoldName) {

		String strTmp = strFoldName;

		if (!strTmp.endsWith(File.separator)) {
			strTmp += "\\";
		}

		return strTmp;

	}

	/**
	 * <p>
	 * ��sourceFolder�ļ����µ�����ת�뵽destinationFolder�ļ����£�����sourceDecode
	 * and destEncode ����
	 * </p>
	 * <p>
	 * ��destinationFolder�ļ��в��������Զ�����
	 * </p>
	 * 
	 * @param sourceFolder
	 *            Դ�ļ��� �磺C:\\aaa
	 * @param destinationFolder
	 *            Ŀ���ļ��� D:\\java
	 * @throws Exception
	 */
	public static boolean convFile(String sourceFile, String sourceDecode,
			String destinationFile, String destEncode) {

		String encodingS = sourceDecode.trim();
		String encodingD = destEncode.trim();

		try {
			FileInputStream fs = new FileInputStream(sourceFile);
			FileOutputStream os = new FileOutputStream(destinationFile);

			InputStreamReader isr;
			if (encodingS.equals("")) {
				isr = new InputStreamReader(fs);
			} else {
				isr = new InputStreamReader(fs, encodingS);
			}

			OutputStreamWriter osr;
			if (encodingD.equals("")) {
				osr = new OutputStreamWriter(os);
			} else {
				osr = new OutputStreamWriter(os, encodingD);
			}

			BufferedWriter bw = new BufferedWriter(osr);
			BufferedReader br = new BufferedReader(isr);
			try {
				String data = "";
				while ((data = br.readLine()) != null) {
					bw.write(data);
				}
				bw.flush();
				bw.close();
			} catch (IOException e) {
				return false;
			}
			osr.close();
			os.close();
		} catch (IOException es) {
			return false;
		}

		System.out.println(" inputfile size =  "
				+ getFileLength(sourceFile, sourceDecode));
		System.out.println(" outputfile size =  "
				+ getFileLength(destinationFile, destEncode));

		return true;

	}

	public static long getFileLength(String fPath, String encoding) {
		long lLen = 10000000000L;

		FileInputStream is = null;
		try {
			is = new FileInputStream(fPath);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}

		InputStreamReader isr = null;

		try {
			isr = new InputStreamReader(is, encoding);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -2;
		}

		try {
			lLen = isr.skip(lLen);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -3;
		}

		return lLen;
	}

	/**
	 * ɾ���ļ�
	 * 
	 * @param filePathAndName
	 *            �ı��ļ�������·�����ļ���
	 * @return Boolean �ɹ�ɾ���true�����쳣����false
	 */
	public static void delFile(String filePathAndName) {
		String filePath = filePathAndName;
		File myDelFile = new File(filePath);
		if (myDelFile.exists()) {
			myDelFile.delete();
		}
	}

	/**
	 * ɾ���ļ�
	 * 
	 * @param filePathAndName
	 *            �ı��ļ�������·�����ļ���
	 * @return Boolean �ɹ�ɾ���true�����쳣����false
	 */
	public static Boolean delFile_Return(String filePathAndName) {
		String filePath = filePathAndName;
		File myDelFile = new File(filePath);
		if (myDelFile.exists()) {
			return myDelFile.delete();
		} else {
			return false;
		}
	}

	/**
	 * ɾ���ļ���
	 * 
	 * @param folderPath
	 *            �ļ���������·��
	 * @return
	 * @throws Exception
	 */
	public static void delFolder(String folderPath) throws Exception {

		try {
			delAllFile(folderPath);
			// ɾ����������������
			String filePath = folderPath;
			filePath = filePath.toString();
			File myFilePath = new File(filePath);
			boolean result = myFilePath.delete(); // ɾ����ļ���
			if (!result) {
				throw new IOException("�ļ���:" + folderPath + ",ɾ��ʧ��");
			}
		} catch (IOException e) {
			throw new Exception("ɾ���ļ��г���", e);
		}
	}

	/**
	 * ɾ���ļ����ļ����������ļ�
	 * 
	 * @return
	 * @throws IOException
	 */
	public static boolean delAllFile(String filePath) throws IOException {
		File file = new File(filePath);
		if (!file.exists())// 文件夹不存在不存在
			throw new IOException("指定目录不存在:" + file.getName());
		boolean rslt = true;// 保存中间结果
		if (!(rslt = file.delete())) {// 先尝试直接删除
			// 若文件夹非空。枚举、递归删除里面内容
			File subs[] = file.listFiles();
			for (int i = 0; i <= subs.length - 1; i++) {
				if (subs[i].isDirectory())
					delAllFile(subs[i].toString());// 递归删除子文件夹内容
				rslt = subs[i].delete();// 删除子文件夹本身
			}
			// rslt = file.delete();// 删除此文件夹本身
		}

		if (!rslt)
			throw new IOException("无法删除:" + file.getName());
		// 删除文件
		return true;
	}

	/**
	 * �ƶ��ļ�
	 * 
	 * @param oldPath
	 * @param newPath
	 * @return
	 * @throws Exception
	 */
	public static void moveFile(String oldPath, String newPath)
			throws Exception {

		try {
			copyFile(oldPath, newPath);
			delFile(oldPath);
		} catch (Exception e) {
			throw e;
		}

	}

	/**
	 * �ƶ�Ŀ¼
	 * 
	 * @param oldPath
	 * @param newPath
	 * @return
	 */
	public static void moveFolder(String oldPath, String newPath) {
		try {
			copyFolder(oldPath, newPath);
			delFolder(oldPath);
		} catch (Exception e) {
			// TODO �Զ���� catch ��
			e.printStackTrace();
		}
	}

	/**
	 * <p>
	 * ȡ�õ�ǰ���ļ����ھ��·��
	 * </p>
	 * <p>
	 * �磺E:/workspace/mobile/WebRoot/WEB-INF/classes/com/mobileSky/ty/utils/
	 * </p>
	 * 
	 * @return
	 */
	public static String getClassPath() {
		String strClassName = FileUtil.class.getName();

		String strPackageName = "";
		if (FileUtil.class.getPackage() != null) {
			strPackageName = FileUtil.class.getPackage().getName();
		}

		String strClassFileName = "";
		if (!"".equals(strPackageName)) {
			strClassFileName = strClassName.substring(
					strPackageName.length() + 1, strClassName.length());
		} else {
			strClassFileName = strClassName;
		}

		URL url = null;
		url = FileUtil.class.getResource(strClassFileName + ".class");
		String strURL = url.toString();
		strURL = strURL.substring(strURL.indexOf('/') + 1,
				strURL.lastIndexOf('/'));
		return strURL + "/";
	}

	/**
	 * ȡ�õ�ǰ��Ŀ��Ŀ¼���磺E:/workspace/mobile/������mobileΪ��Ŀ��
	 * 
	 * @param projectName
	 * @return
	 */
	public static String getProject(String projectName) {
		String classPath = getClassPath();
		return classPath.substring(0, classPath.indexOf(projectName)
				+ projectName.length() + 1);
	}

	/**
	 * ��C:\aaa\dsaת��ΪC:/aaa/dsa
	 * 
	 * @param path
	 * @return
	 */
	public static String getStr(String path) {
		String result = "";
		char[] pathChar = path.toCharArray();
		for (int i = 0; i < pathChar.length; i++) {
			if (pathChar[i] == '\\') {
				pathChar[i] = '/';
			}
			result += pathChar[i];
		}
		return result;
	}

	/**
	 * �ļ��Ƿ����
	 * 
	 * @param filePath
	 * @return
	 */
	public static boolean fileExist(String filePath) {
		boolean exists = false;
		File file = new File(filePath);
		exists = file.exists();
		return exists;
	}

	/**
	 * �ļ�����
	 * 
	 * @param folderPath
	 * @return
	 */
	public static boolean folderExistOrCreate(String folderPath) {
		boolean exists = false;
		File file = new File(folderPath);
		if (!file.exists()) {
			file.mkdirs();
			exists = true;
		} else {
			exists = true;
		}
		return exists;
	}

	/**
	 * ��ȡ�ļ���
	 * 
	 * @param filepath
	 *            �ļ�·��
	 * @return �����ļ���
	 */
	public static String getFileName(String filepath) {
		String fileName = null;
		File file = new File(filepath);
		if (file.exists()) {
			fileName = file.getName();
		}
		return fileName;
	}

	/**
	 * ���ļ����ϰ�ʱ����������
	 * 
	 * @param files
	 *            �ļ��б�
	 * @return
	 */
	public static File[] sortFiles(File files[]) {
		File[] returnFiles = null;
		if (files != null) {
			returnFiles = new File[files.length];
			String[] filesTime = new String[files.length];
			for (int i = 0; i < files.length; i++) {
				filesTime[i] = files[i].lastModified() + files[i].getName();
			}

			Collections.sort(Arrays.asList(filesTime));
			Collections.reverse(Arrays.asList(filesTime));

			for (int i = 0; i < filesTime.length; i++) {
				for (int j = 0; j < files.length; j++) {
					if ((files[j].lastModified() + files[j].getName())
							.equals(filesTime[i])) {
						returnFiles[i] = files[j];
					}
				}
			}
		}
		return returnFiles;
	}

	/**
	 * ���ļ����ϰ�ʱ����������
	 * 
	 * @param files
	 *            �ļ��б�
	 * @return
	 */
	public static File[] sortFilesByName(File files[]) {
		File[] returnFiles = null;
		if (files != null) {
			returnFiles = new File[files.length];
			String[] filesTime = new String[files.length];
			for (int i = 0; i < files.length; i++) {
				filesTime[i] = files[i].getName();
			}

			Collections.sort(Arrays.asList(filesTime));
			Collections.reverse(Arrays.asList(filesTime));

			for (int i = 0; i < filesTime.length; i++) {
				for (int j = 0; j < files.length; j++) {
					if (files[j].getName().equals(filesTime[i])) {
						returnFiles[i] = files[j];
					}
				}
			}
		}
		return returnFiles;
	}

	public static ArrayList<File> sortFiles(ArrayList<File> list) {
		ArrayList<File> outList = new ArrayList<File>();

		File[] flist = new File[list.size()];
		for (int i = 0; i < list.size(); i++) {
			flist[i] = list.get(i);
		}

		File[] outFlist = sortFiles(flist);

		for (int i = 0; i < outFlist.length; i++) {
			outList.add(outFlist[i]);
		}

		return outList;
	}

	public static ArrayList<File> sortFilesByName(ArrayList<File> list) {
		ArrayList<File> outList = new ArrayList<File>();

		File[] flist = new File[list.size()];
		for (int i = 0; i < list.size(); i++) {
			flist[i] = list.get(i);
		}

		File[] outFlist = sortFilesByName(flist);

		for (int i = 0; i < outFlist.length; i++) {
			if (outFlist[i].getName().toUpperCase().endsWith("MYTRACK.GPX")) {
				outList.add(0, outFlist[i]);
			} else {
				outList.add(outFlist[i]);
			}
		}

		return outList;
	}

	public static long getFileSize(String filePath) {
		return fileExist(filePath) == true ? new File(filePath).length() : -1;
	}

	public static boolean fileRename(String fileSrc, String fileDest) {

		File file = new File(fileSrc);
		File fileDes = new File(fileDest);
		return file.renameTo(fileDes);
	}

	public static boolean Filecombine(String dir, String savepathName) {
		File fileDir = new File(dir);
		if (fileDir.exists()) {
			File[] files = fileDir.listFiles();

			File outFile = new File(savepathName); // �ϲ�������ļ�
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(outFile); // �����
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
				return false;
			}
			FileInputStream fis = null;
			File f = null;
			for (int i = 0; i < files.length; i++) { // �ҵ�Ŀ¼�µ������ļ���ѭ�����ж�ȡ�����浽�µ��ļ�
				try {
					f = new File(dir + (i + 1) + ".x");
					fis = new FileInputStream(f);

					byte[] b = new byte[1024];
					int len;
					while ((len = fis.read(b)) != -1) {
						fos.write(b, 0, len);
					}

				} catch (FileNotFoundException e) {
					e.printStackTrace();
					return false;
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				} finally {
					try {
						fis.close(); // һ��Ҫ�ص�
					} catch (IOException e) {
						e.printStackTrace();
						return false;
					}
				}
				f.delete();
			}
			try {

				fos.flush();
				fos.close(); // һ��Ҫ�ص�
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}
		return false;
	}

	public static boolean Filecombine(File[] fList, String savepathName) {

		File[] files = fList;

		File outFile = new File(savepathName); // �ϲ�������ļ�
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(outFile); // �����
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return false;
		}
		FileInputStream fis = null;
		File f = null;

		for (int i = 0; i < files.length; i++) {
			try {
				f = files[i];
				fis = new FileInputStream(f);

				byte[] b = new byte[1024];
				int len;
				while ((len = fis.read(b)) != -1) {
					fos.write(b, 0, len);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			} finally {
				try {
					fis.close(); // һ��Ҫ�ص�
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			}
			f.delete();
		}

		try {
			fos.flush();
			fos.close(); // һ��Ҫ�ص�
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public static String FileNameFilter(String strFileName) {

		String strFName = strFileName;

		strFName = strFName.replace("?", "");
		strFName = strFName.replace("+", "");
		strFName = strFName.replace("\"", "");
		strFName = strFName.replace("$", "");
		strFName = strFName.replace("(", "");
		strFName = strFName.replace(")", "");
		strFName = strFName.replace("%", "");

		return strFName;

	}

	public static String[] convFileSize(long size) {
		String str = "";
		if (size >= 1024) {
			str = "KB";
			size /= 1024;
			if (size >= 1024) {
				str = "MB";
				size /= 1024;
			}
		}
		DecimalFormat formatter = new DecimalFormat();
		formatter.setGroupingSize(3); // ÿ3�������� ,�ָ� ���磺 1��000
		String result[] = new String[2];
		result[0] = formatter.format(size);
		result[1] = str;
		return result;
	}

	public static String getFolder(String fileFullPath) {
		String strTmp = fileFullPath.substring(0,
				fileFullPath.lastIndexOf("\\") + 1);
		String strTmp1 = fileFullPath.substring(0,
				fileFullPath.lastIndexOf("/") + 1);

		if (strTmp.length() > strTmp1.length()) {
			return strTmp;
		} else {
			return strTmp1;
		}
	}

	public static boolean recreateFolder(String foldPath) {

		try {
			FileUtil.delAllFile(foldPath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			FileUtil.delFolder(foldPath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			FileUtil.createFolders(foldPath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public static String readTxtcoding(String strFile) {
		String strEnc = "utf-8";
		File file = new File(strFile);
		if (file == null || !file.exists())
			return "";

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		final int BOM_SIZE = 4;
		byte[] btBom = new byte[BOM_SIZE];
		try {
			int iLen = fis.read(btBom);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if ((btBom[0] == (byte) 0xFF) && (btBom[1] == (byte) 0xFE))
			strEnc = "unicode";// "UTF-16LE"
		else if ((btBom[0] == (byte) 0xFE) && (btBom[1] == (byte) 0xFF))
			strEnc = "UTF-16BE";
		else if ((btBom[0] == (byte) 0xEF) && (btBom[1] == (byte) 0xBB)
				&& (btBom[2] == (byte) 0xBF))
			strEnc = "UTF-8";
		else if ((btBom[0] == (byte) 0xFF) && (btBom[1] == (byte) 0xFE)
				&& (btBom[2] == (byte) 0x00) && (btBom[3] == (byte) 0x00))
			strEnc = "UTF-32LE";
		else if ((btBom[0] == (byte) 0x00) && (btBom[1] == (byte) 0x00)
				&& (btBom[2] == (byte) 0xFE) && (btBom[3] == (byte) 0xFF))
			strEnc = "UTF-32BE";

		if (fis != null)
			try {
				fis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		// if ( !Charset.isSupported(strEnc) )
		// strEnc = "";

		return strEnc;
	}

	/**
	 * ���Ƶ����ļ�
	 * 
	 * @param oldPathFile
	 *            ׼�����Ƶ��ļ�Դ
	 * @param newPathFile
	 *            �������¾��·�����ļ���
	 * @return
	 * @throws Exception
	 */
	public static byte[] readFile(String strPathFile) {

		File file = new File(strPathFile);

		if (!file.exists())
			return null; // �ļ�����ʱ

		byte[] fileBuffer = new byte[(int) file.length()];
		InputStream inStream = null;

		try {
			inStream = new FileInputStream(strPathFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		byte[] buffer = new byte[1024];
		int byteread = -1;
		int bytesum = 0;
		try {
			while ((byteread = inStream.read(buffer)) != -1) {
				for (int i = 0; i < byteread; i++) {
					fileBuffer[bytesum + i] = buffer[i];
				}
				bytesum += byteread;
				if (bytesum == file.length())
					break;
			}
			inStream.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		return fileBuffer;
	}

	/**
	 * ���Ƶ����ļ�
	 * 
	 * @param oldPathFile
	 *            ׼�����Ƶ��ļ�Դ
	 * @param newPathFile
	 *            �������¾��·�����ļ���
	 * @return
	 * @throws Exception
	 */
	public static void AppendToFile(String strPathFile, String strContent,
			String strEncode) {

		String strTmp = "";
		try {
			strTmp = FileUtil.readTxt(strPathFile, strEncode);
			strTmp += strContent;
			try {
				FileUtil.createFile(strPathFile, strTmp, strEncode);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

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
			// GenUtil.print(TAG, "width = " + width + "IMG=" + "h=" +
			// options.outHeight
			// + ", w=" + options.outWidth + ", img=" + strImagePath);

			iWidthOrg = options.outWidth;
			iHeightOrg = options.outHeight;

			int scale = iWidthOrg / width;

			if (scale == 0) {
				options.inSampleSize = 1;
			} else {
				options.inSampleSize = scale;
			}

			// Log.i(TAG, "scale=" + scale + ", sample=" + scale);
			bitmap = BitmapFactory.decodeFile(strImagePath, options);

			if (iWidthOrg <= width) {
				return bitmap;
			}

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
	public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
	        int reqWidth, int reqHeight) {
	 
	    // 首先设置 inJustDecodeBounds=true 来获取图片尺寸
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeResource(res, resId, options);
	 
	    // 计算 inSampleSize 的值
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
	 
	    // 根据计算出的 inSampleSize 来解码图片生成Bitmap
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeResource(res, resId, options);
	}
	
	public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
    // 原始图片的宽高
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;
 
    if (height > reqHeight || width > reqWidth) {
 
        final int halfHeight = height / 2;
        final int halfWidth = width / 2;
 
        // 在保证解析出的bitmap宽高分别大于目标尺寸宽高的前提下，取可能的inSampleSize的最大值
        while ((halfHeight / inSampleSize) > reqHeight
                && (halfWidth / inSampleSize) > reqWidth) {
            inSampleSize *= 2;
        }
    }
 
    return inSampleSize;
}
}
