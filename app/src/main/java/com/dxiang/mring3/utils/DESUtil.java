package com.dxiang.mring3.utils;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;

public class DESUtil {
	private String key;

	private static DESUtil mDESUtil;

	public static DESUtil getInstance() {
		if (mDESUtil == null) {
			mDESUtil = new DESUtil();
		}

		return mDESUtil;
	}

	private DESUtil() {
		key = Commons.KEY;
	}

	public void setKey(String k) {
		key = k;
	}

	private byte[] getDecrypt(byte[] encryptedData, byte[] rawKeyData)
			throws InvalidKeyException, NoSuchAlgorithmException,
			InvalidKeySpecException, NoSuchPaddingException,
			BadPaddingException, IllegalBlockSizeException,
			IllegalStateException {
		SecureRandom sr = new SecureRandom();
		DESKeySpec dks = new DESKeySpec(rawKeyData);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey key = keyFactory.generateSecret(dks);
		Cipher cipher = Cipher.getInstance("DES");
		cipher.init(2, key);
		byte[] decryptedData = cipher.doFinal(encryptedData);
		return decryptedData;
	}

	public String getDecrypt(String encryptStr) throws InvalidKeyException,
			NoSuchAlgorithmException, InvalidKeySpecException,
			NoSuchPaddingException, BadPaddingException,
			IllegalBlockSizeException, IllegalStateException, IOException {
		char[] c = new char[encryptStr.length() / 2];
		StringBuffer sbuf = new StringBuffer();
		for (int i = 0; i < c.length; i++) {
			int a = Integer
					.parseInt(encryptStr.substring(i * 2, i * 2 + 2), 16);
			c[i] = (char) a;
			sbuf.append(c[i]);
		}
		encryptStr = sbuf.toString();
		BASE64Decoder decoder = new BASE64Decoder();
		byte[] encryptedData = decoder.decodeBuffer(encryptStr);
		byte[] rawKeyData = key.getBytes();
		byte[] decryptData = getDecrypt(encryptedData, rawKeyData);
		return new String(decryptData);
	}

	private byte[] encryptString(byte[] data, byte[] rawKeyData)
			throws InvalidKeyException, NoSuchAlgorithmException,
			InvalidKeySpecException, NoSuchPaddingException,
			BadPaddingException, IllegalBlockSizeException,
			IllegalStateException {
		SecureRandom sr = new SecureRandom();
		DESKeySpec dks = new DESKeySpec(rawKeyData);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey key = keyFactory.generateSecret(dks);
		Cipher cipher = Cipher.getInstance("DES");
		cipher.init(1, key, sr);
		byte[] encryptedData = cipher.doFinal(data);
		return encryptedData;
	}

	public String encryptString(String rawStr) throws InvalidKeyException,
			NoSuchAlgorithmException, InvalidKeySpecException,
			NoSuchPaddingException, BadPaddingException,
			IllegalBlockSizeException, IllegalStateException, IOException {
		byte[] rawStrData = rawStr.getBytes();
		byte[] rawKeyData = key.getBytes();
		byte[] encryptData = encryptString(rawStrData, rawKeyData);
		String encryptStr = new BASE64Encoder().encode(encryptData);
		byte[] c = encryptStr.getBytes();
		StringBuffer strBuf = new StringBuffer();
		for (int i = 0; i < c.length; i++) {
			int t = c[i];
			String str = Integer.toHexString(t);
			if (str.length() == 1)
				strBuf.append(0);
			strBuf.append(Integer.toHexString(t));
		}
		return strBuf.toString();
	}

}