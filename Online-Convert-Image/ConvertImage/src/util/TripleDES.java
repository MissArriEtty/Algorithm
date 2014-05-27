package util;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class TripleDES {
	private static final String ALGORITHM = "DESede/CBC/PKCS7Padding";// "DESede";

	private static final String PASSWORD_CRYPT_KEY = "123456781234567812345678";
	private static final String DEFAULT_PASSWORD_IV = "00000000";
	private static String privatePswIV = DEFAULT_PASSWORD_IV;

	public static byte[] encryptMode(byte[] keybyte, byte[] src) {
		try {
			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
			SecureRandom sr = new SecureRandom();
			DESedeKeySpec dks = new DESedeKeySpec(keybyte);
			SecretKeyFactory keyFactory = SecretKeyFactory
					.getInstance("DESede");
			SecretKey securekey = keyFactory.generateSecret(dks);
			IvParameterSpec iv = new IvParameterSpec(toIVBytes(privatePswIV));
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, securekey, iv, sr);
			return cipher.doFinal(src);
		} catch (java.security.NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (javax.crypto.NoSuchPaddingException e2) {
			e2.printStackTrace();
		} catch (java.lang.Exception e3) {
			e3.printStackTrace();
		}
		return null;
	}

	private static byte[] toIVBytes(String privatePswIV2) {
		byte[] IVBytes = new byte[8];
		char chars[] = privatePswIV2.toCharArray();
		for (int i = 0; i < 8; i++)
			IVBytes[i] = Byte.parseByte(chars[i] + "");
		return IVBytes;
	}

	public static byte[] decryptMode(byte[] keybyte, byte[] src) {
		byte[] tmp = null;
		try {
			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
			SecureRandom sr = new SecureRandom();
			DESedeKeySpec dks = new DESedeKeySpec(keybyte);
			SecretKeyFactory keyFactory = SecretKeyFactory
					.getInstance("DESede");
			SecretKey securekey = keyFactory.generateSecret(dks);
			IvParameterSpec iv = new IvParameterSpec(toIVBytes(privatePswIV));
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, securekey, iv, sr);
			tmp = cipher.doFinal(src);
		} catch (java.security.NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (javax.crypto.NoSuchPaddingException e2) {
			e2.printStackTrace();
		} catch (java.lang.Exception e3) {
			e3.printStackTrace();
		}
		return tmp;
	}

	public static String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
			if (n < b.length - 1)
				hs = hs + ":";
		}
		return hs.toUpperCase();
	}

	public static String byteArr2HexStr(byte[] arrB) {
		int iLen = arrB.length;
		StringBuffer sb = new StringBuffer(iLen * 2);
		for (int i = 0; i < iLen; i++) {
			int intTmp = arrB[i];
			while (intTmp < 0) {
				intTmp = intTmp + 256;
			}
			if (intTmp < 16) {
				sb.append("0");
			}
			sb.append(Integer.toString(intTmp, 16));
		}
		return sb.toString();
	}

	public static byte[] hexStr2ByteArr(String strIn) {
		byte[] arrB = strIn.getBytes();
		int iLen = arrB.length;

		byte[] arrOut = new byte[iLen / 2];
		for (int i = 0; i < iLen; i = i + 2) {
			String strTmp = new String(arrB, i, 2);
			arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
		}
		return arrOut;
	}

	public static byte[] base64Decode(String input) {
		BASE64Decoder base64 = new BASE64Decoder();
		byte[] output = null;
		try {
			output = base64.decodeBuffer(input);
		} catch (IOException e) {
		}
		return output;
	}

	public static String base64Encode(byte[] input) {
		BASE64Encoder base64 = new BASE64Encoder();
		return base64.encode(input);
	}

	public static String desEncode(String plaintext, String key,
			String desInitial) {
		if (desInitial != null)
			privatePswIV = desInitial;
		String desPsw = null;
		byte[] keyBytes = (key == null) ? desKeyGetBytes(PASSWORD_CRYPT_KEY)
				: desKeyGetBytes(key);
		byte[] desCode = encryptMode(keyBytes, plaintext.getBytes());
		if (desCode != null) {
			desPsw = base64Encode(desCode);
		}
		return desPsw;
	}

	public static String desEncodeOnly(String plaintext, String key,
			String desInitial) {
		if (desInitial != null)
			privatePswIV = desInitial;
		String desPsw = null;
		byte[] keyBytes = (key == null) ? desKeyGetBytes(PASSWORD_CRYPT_KEY)
				: desKeyGetBytes(key);
		byte[] desCode = encryptMode(keyBytes, plaintext.getBytes());
		if (desCode != null) {
			
			desPsw = ByteUtil.byte2hex(desCode);
		}
		return desPsw;
	}

	public static String desDecode(String ciphertext, String key,
			String desInitial) {
		if (desInitial != null)
			privatePswIV = desInitial;
		String psw = null;
		byte[] hexByte = base64Decode(ciphertext);
		byte[] keyBytes = (key == null) ? desKeyGetBytes(PASSWORD_CRYPT_KEY)
				: desKeyGetBytes(key);
		byte[] decode = decryptMode(keyBytes, hexByte);
		if (decode != null) {
			psw = new String(decode);
		}
		return psw;
	}

	public static String desDecodeOnly(String ciphertext, String key,
			String desInitial) {
		if (desInitial != null)
			privatePswIV = desInitial;
		String psw = null;
		byte[] hexByte = ByteUtil.fromHexString(ciphertext);
		byte[] keyBytes = (key == null) ? desKeyGetBytes(PASSWORD_CRYPT_KEY)
				: desKeyGetBytes(key);
		byte[] decode = decryptMode(keyBytes, hexByte);
		if (decode != null) {
			psw = new String(decode);
		}
		return psw;
	}

	public static String desEncode(byte[] plaintext, String key,
			String desInitial) {
		if (desInitial != null)
			privatePswIV = desInitial;
		String desPsw = null;
		byte[] keyBytes = (key == null) ? desKeyGetBytes(PASSWORD_CRYPT_KEY)
				: desKeyGetBytes(key);
		byte[] desCode = encryptMode(keyBytes, plaintext);
		if (desCode != null) {
			desPsw = base64Encode(desCode);
		}
		return desPsw;
	}

	private static byte[] desKeyGetBytes(String desKey) {
		byte[] output = null;
		if (desKey != null) {
			if (desKey.length() > 24) {
				output = hexStringToByte(desKey);
			} else {
				output = desKey.getBytes();
			}
		}
		return output;
	}

	public static byte[] hexStringToByte(String hex) {
		int len = (hex.length() / 2);
		byte[] result = new byte[len];
		char[] achar = hex.toCharArray();
		for (int i = 0; i < len; i++) {
			int pos = i * 2;
			result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
		}
		return result;
	}

	private static byte toByte(char c) {
		byte b = (byte) "0123456789ABCDEF".indexOf(c);
		return b;
	}
}
