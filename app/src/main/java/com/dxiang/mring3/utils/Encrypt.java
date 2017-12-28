package com.dxiang.mring3.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import android.util.Log;

public class Encrypt {

	private static final String key = "abababababababab";// 加解密秘钥
	public static final String TAG = "AESUtils";
	
	public static String getMD5String(String str)
    {
        try
        {
            byte[] res=str.getBytes();
            MessageDigest md = MessageDigest.getInstance("MD5".toUpperCase());
            byte[] result=md.digest(res);
            for(int i=0;i<result.length;i++)
            {
                md.update(result[i]);
            }
            byte[] hash=md.digest();
            StringBuffer d=new StringBuffer("");
            for(int i=0;i<hash.length;i++)
            {
                int v=hash[i] & 0xFF;
                if(v<16) d.append("0");
                    d.append(Integer.toString(v,16).toUpperCase()+"");
            }
                return d.toString();
        }
        catch(Exception e)
        {
            return "";
        }
    }

	public static String getMD5(byte[] source) {
		String s = null;
		// 用来将字节转换成16进制表示的字符
		char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(source);
			// MD5的计算结果是一个128位的长整数，用字节表示为16个字节
			byte[] tmp = md.digest();
			// 每个字节用16进制表示的话，使用2个字符(高4位一个,低4位一个)，所以表示成16进制需要32个字符
			char[] str = new char[16 * 2];
			int k = 0;// 转换结果中对应的字符位置
			for (int i = 0; i < 16; i++) {// 对MD5的每一个字节转换成16进制字符
				byte byte0 = tmp[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];// 对字节高4位进行16进制转换
				str[k++] = hexDigits[byte0 & 0xf]; // 对字节低4位进行16进制转换
			}
			s = new String(str);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String encrypt(String clearText) {
		Log.d(TAG, "key=" + key + ",加密前:" + clearText);
		byte[] result = null;
		try {
			byte[] rawkey = getRawKey(key.getBytes());
			result = encrypt(rawkey, clearText.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		String content = toHex(result);
		Log.d(TAG, "加密后:" + content);
		return content;

	}

	public static String decrypt(String encrypted) {
		Log.d(TAG, "key=" + key + ",解密前:" + encrypted);
		byte[] rawKey;
		try {
			rawKey = getRawKey(key.getBytes());
			byte[] enc = toByte(encrypted);
			byte[] result = decrypt(rawKey, enc);
			String coentn = new String(result);
			Log.d(TAG, "解密后:" + coentn);
			return coentn;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	private static byte[] getRawKey(byte[] seed) throws Exception {
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");// windows��androidƽ̨�������
		sr.setSeed(seed);
		kgen.init(128, sr);
		SecretKey sKey = kgen.generateKey();
		byte[] raw = sKey.getEncoded();

		return raw;
	}

	private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		byte[] encrypted = cipher.doFinal(clear);
		return encrypted;
	}

	private static byte[] decrypt(byte[] raw, byte[] encrypted)
			throws Exception {
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, skeySpec);
		byte[] decrypted = cipher.doFinal(encrypted);
		return decrypted;
	}

	public static String toHex(String txt) {
		return toHex(txt.getBytes());
	}

	public static String fromHex(String hex) {
		return new String(toByte(hex));
	}

	public static byte[] toByte(String hexString) {
		int len = hexString.length() / 2;
		byte[] result = new byte[len];
		for (int i = 0; i < len; i++)
			result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2),
					16).byteValue();
		return result;
	}

	public static String toHex(byte[] buf) {
		if (buf == null)
			return "";
		StringBuffer result = new StringBuffer(2 * buf.length);
		for (int i = 0; i < buf.length; i++) {
			appendHex(result, buf[i]);
		}
		return result.toString();
	}

	private static void appendHex(StringBuffer sb, byte b) {
		final String HEX = "0123456789ABCDEF";
		sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
	}

}
