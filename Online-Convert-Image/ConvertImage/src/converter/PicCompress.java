package converter;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import logPackage.cloudLog;

import cloud.SapInterface;

import poller.Request;

public class PicCompress implements Runnable {
	/**日志记录*/
	private cloudLog log;
	
	/**负责处理的任务*/
	private Request compressRequest;
	
	/**图像压缩后的宽度*/
	private final int widths[];
	
	/**图像压缩后的地址*/
	private String compressPos;
	
	/**图像压缩完成标记*/
	private boolean finishFlag = false;
	
	/**图像压缩成功标记*/
	private boolean okFlag = false;
	/**任务失败信息*/
	private String failMsg = "";
	
	public PicCompress(Request compressRequest, int widths[], String compressPos, cloudLog log) {
		this.compressRequest = compressRequest;
		this.widths = widths;
		this.compressPos = compressPos;
		this.log = log;
	}
	
	public boolean isFinished() {
		return finishFlag;
	}
	
	public boolean isSucceed() {      
		return okFlag;
	}
	
	public Request getRequest() {
		return compressRequest;
	}
	
	public String getFailMsg() {
		return failMsg;
	}
	
	private String compress(String inputPath, String outputPath, String format) {
		File inputFile = new File(inputPath);
		if (!inputFile.exists())
			return "can't find input file";
		Image srcImage = null;
		try {
			srcImage = ImageIO.read(inputFile);
		} catch (IOException e) {
			e.printStackTrace();
			return "input file format error";
		}
		for (int index = 0; index < widths.length; ++index) {
			int width = widths[index];
			int height = (int)(srcImage.getHeight(null) / (srcImage.getWidth(null) + 1e-5) * width);
			BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
			tag.getGraphics().drawImage(srcImage, 0, 0, width, height, null);
			try {
				ImageIO.write(tag, format, new File(compressPos + compressRequest.getRequestId() + "_" + index + "." + format));
			} catch (IOException e) {
				for (int ahead = 0; ahead < index; ++ahead)
					try {
						FileOperator.deleteFile(compressPos + compressRequest.getRequestId() + "_" + ahead + "." + format);
					} catch (IOException ex) {
					}
				e.printStackTrace();
				return "can't build compressed pic when width == " + width;
			}
		}
		return "OK";
	}

	@Override
	public void run() {
		String userId = compressRequest.getUserId();
		String fileId = compressRequest.getFileId();
		String requestId = compressRequest.getRequestId();
		
		try {
			Object[] downResult = SapInterface.downloadFile(userId, fileId);
			if (downResult != null) {
				System.out.println(compressRequest.getRequestId() +  " | download file OK");
				log.writeLog(compressRequest.getRequestId() +  " | download file OK");
		
				String filePath = (String)downResult[1];
				String format = compressRequest.getFormat();
				String compressResult = compress(filePath, compressPos, format);
				if (!compressResult.equals("OK")) {
					System.out.println(requestId + " | compress fail : " + compressResult);
					log.writeLog(requestId + " | compress fail" + compressResult);
					failMsg = compressResult;
				}
				else {
					System.out.println(requestId + " | compress OK");
					log.writeLog(requestId + " | compress OK");
					okFlag = true;
				}
				new File(filePath).delete();
			}
			else 
				throw new Exception("Sap download error");
		} catch (Exception ex) {
			System.out.println(requestId +  " | download file failed");
			log.writeLog(requestId +  " | download file failed");
			failMsg = ex.toString();
		}
		finishFlag = true;
		System.out.println(compressRequest.getRequestId() + " compress finish");
	}
}
