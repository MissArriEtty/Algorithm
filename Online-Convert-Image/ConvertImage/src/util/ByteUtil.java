package util;


public final class ByteUtil {

	public static String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
		}
		return hs.toUpperCase();
	}

	public static byte[] fileByte(byte[] b, int length) {
		int pad = b.length % length;
		byte[] appendByte = new byte[length - pad];
		for (int i = 0; i < (length - pad); i++) {
			appendByte[i] = Byte.MAX_VALUE;
		}
		return org.apache.commons.lang.ArrayUtils.addAll(b, appendByte);
	}

	public static String byte2Str(byte[] b) {
		String result = "";
		for (Byte bytes : b) {
			result += (char) bytes.intValue();
		}
		return result;
	}

	public static byte[] fromHex(byte[] sc) {
		byte[] res = new byte[sc.length / 2];
		for (int i = 0; i < sc.length; i++) {
			byte c1 = (byte) (sc[i] - 48 < 17 ? sc[i] - 48 : sc[i] - 55);
			i++;
			byte c2 = (byte) (sc[i] - 48 < 17 ? sc[i] - 48 : sc[i] - 55);
			res[i / 2] = (byte) (c1 * 16 + c2);
		}
		return res;
	}

	public static byte[] fromHexString(String hex) {
		return fromHex(hex.getBytes());
	}

	public static String decode(String in) {
		return new String(fromHex(in.getBytes()));
	}
}
