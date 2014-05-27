package test;

import logPackage.cloudLog;
import converter.Converter;
import converter.PicCompress;
import poller.Request;

public class PicCompressTest {
	byte[] change(String inputStr) {
		byte[] result = new byte[inputStr.length() / 2];
		for (int i = 0; i < inputStr.length() / 2; ++i) 
			result[i] = (byte)(Integer.parseInt(inputStr.substring(i * 2, i * 2 +2), 16) & 0xff);
		return result;
	}
	
	public static void main(String args[]) {
		Request request = new Request("0", "zwj", "example.jpg", null, "jpg");
		request.setFormat("jpg");
		int widths[] = new int[]{50, 100, 200};
		String compressPos = "compressPics\\";
		cloudLog log = new cloudLog(Converter.class.getName());
		PicCompress compressWork = new PicCompress(request, widths, compressPos, log);
		System.out.println("start to compress");
		new Thread(compressWork).start();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(compressWork.isFinished() + " " + compressWork.isSucceed());
		if (!compressWork.isSucceed())
			System.out.println(compressWork.getFailMsg());
	}
}
