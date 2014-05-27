//////////////////////////////////////////////////////////////////////////  
///        COPYRIGHT NOTICE  
///        Copyright (c) 2011, 上海电信 
///        All rights reserved.  
///  
/// @file Upload.java  
/// @brief 本文件为上传线程类。
///  
///     转码完成的任务需要通过本类实例化的一个线程来完成上传到云端的工作。每个
/// 上传任务需实例化一个单独的线程。本线程与SAP交互并完成上传到云端的所有交互
/// 工作，主要调用cloud包中的类完成具体的SAP交互和云传输。
///  
/// @version 1.0
/// @author 易源
/// @date 2011年10月
///  
///  
///        修订说明：最初版本  
//////////////////////////////////////////////////////////////////////////

package converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.Timestamp;

import org.apache.http.client.ClientProtocolException;

import logPackage.cloudLog;

import poller.Request;

import cloud.CloudInterface;
import cloud.CloudResponse;
import cloud.SapInterface;
import cloud.SapResponse;
import dbio.dbUpdate;

/** 本类的功能：上传文件的线程
*
* 具体负责上传文件的操作，包括数据库操作和调用cloud包与SAP和云交互等
*/
public class Upload implements Runnable {
	//public static String localFilePath = "";
	/** 云公共空间目录名 */
	private static final String cacheFolderId = "D:\\compressPic\\";
	/** 操作重试次数 */
	public static int RETRY = 3;
	/** 获取大小操作重试次数*/
	public static int RETRY_FOR_FETCHING_SIZE = 10;
	/** 日志文件 */
	public static cloudLog log = new cloudLog(Upload.class.getName());
	
	/** SAP地址 */
	public static String defSapAddr = "";
	/** SAP的Token ID*/
	public static String defAppTokenId = "";
	/** 本地文件路径 */
	String localFilePath;
	/** 本地文件名 */
	String localFileName;
	/** 云公共空间中的文件名 */
	String fileNameInCache;
	/** 文件类型 */
	String fileType;
	/** 文件大小 */
	long fileSize;			// the file size as an argument passed in, obtaining from mldonkey
	/** 待上传的任务对象 */
	Request request;
	/**待上传任务的id*/
	String requestId;
	
	/** 数据库更新连接 */
	private Connection updateConn;
	/** 用于数据库更新操作 */
	private dbUpdate dbUpdater;
	/** 数据库更新操作的Statement */
	private Statement ustmt;
	
	/** 构造函数
	 * @param request 上传任务
	 * @param filePath 文件路径
	 * @param fileSize 文件大小
	 */
	public Upload(Request request, String filePath, long fileSize) {
		this.localFilePath = filePath;
		this.localFileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
		this.fileNameInCache = this.localFileName;// + new Random().nextInt();
		this.request = request;
		this.requestId = request.getRequestId();
		this.fileSize = fileSize;
		
		if(this.localFileName != null) {
			if(this.localFileName.lastIndexOf('.') == -1) {
				this.fileType = "";
			} else {
				this.fileType = this.localFileName.substring(this.localFileName.lastIndexOf('.') + 1);
			}
		} else {
			this.fileType = "";
		}
		
		dbUpdater = new dbUpdate(Converter.dbAddr, Converter.userName, Converter.passwd);
		updateConn = dbUpdater.connect();
		ustmt = dbUpdater.createStatement(updateConn);
		System.out.println("uploading converted file for job:" + requestId);
	//	System.out.println("job parameters:" + job.getProperty().toString());
		
		log.writeLog("cache app token: " + defAppTokenId);
	}
	
	/** 文件预上传
	 * @return SAP文件预上传返回信息
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	private SapResponse.CreateFileResponse[] createCloudFile() throws ClientProtocolException, IOException {
		SapResponse.CreateFileResponse[] createFileResponse = null;
		String xmlData;
		int count = 0;
		do {
			xmlData = SapInterface.createFile(defSapAddr, defAppTokenId, cacheFolderId, fileNameInCache, fileSize, fileType, "PUT");
			createFileResponse = SapResponse.parseCreateFileResponse(xmlData);
			//determine whether to retry
			if(createFileResponse != null && createFileResponse.length > 0 && createFileResponse[0].resultCode != null) {
				if(createFileResponse[0].resultCode.equals(SapResponse.FILE_EXISTS)) {		//a file with the same name exists
					System.out.println(requestId + " | createCloudFile(): A file with the same name exists");
					log.writeLog(requestId + " | createCloudFile(): A file with the same name exists");
					
					SapInterface.deleteFile(defSapAddr, defAppTokenId, createFileResponse[0].fileID, "true");
				} else if(createFileResponse[0].resultCode.equals(SapResponse.SERVER_INTERNAL_ERROR)) {		//server internal error
					System.out.println(requestId + " | createCloudFile(): Server internal error");
					log.writeLog(requestId + " | createCloudFile(): Server internal error");
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						//e.printStackTrace();
						log.writeLog(requestId + " | createCloudFile(): " + e.toString());
					}
				} else if(createFileResponse[0].resultCode.equals(SapResponse.CLOUD_EXCEPTION)) {
					System.out.println(requestId + " | createCloudFile(): Cloud exception");
					log.writeLog(requestId + " | createCloudFile(): Cloud exception");
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						//e.printStackTrace();
						log.writeLog(requestId + " | createCloudFile(): " + e.toString());
					}
				} else {
					break;		// don't retry and break the while loop. (success or some other exception that need not retry)
				}
			} else {
				System.out.println(requestId + " | createCloudFile(): Wrong response");
				log.writeLog(requestId + " | createCloudFile(): Wrong response");
			}
		} while(++count < RETRY);
		return createFileResponse;
	}
	
	/** 传输文件数据到云服务器
	 * @param localFilePath 本地文件路径
	 * @param realStorageUrl 云服务器上目标位置
	 * @param date 日期
	 * @param authorization 授权码
	 * @return 云服务器返回信息
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private CloudResponse.PutObjectResponse putObject(String realStorageUrl, String date, String authorization) throws FileNotFoundException, IOException {
		CloudResponse.PutObjectResponse cloudResponse = null;
		int count = 0;
		CloudInterface.setLog(log);
		do {
			cloudResponse = CloudInterface.putObject(localFilePath, this.fileSize, realStorageUrl, date, authorization);

			// determine whether to retry
			if(cloudResponse != null) {
				if(cloudResponse.statusCode == 200) {
					break;
				} else {
					System.out.println(requestId + " | putObject(): (" + cloudResponse.statusCode + ")" + cloudResponse.code + " | " + cloudResponse.message);
					log.writeLog(requestId + " | putObject(): (" + cloudResponse.statusCode + ")" + cloudResponse.code + " | " + cloudResponse.message);
				}
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					//e.printStackTrace();
					log.writeLog(requestId + " | putObject(): " + e.toString());
				}
			} else {
				System.out.println(requestId + " | putObject(): No response");
				log.writeLog(requestId + " | putObject(): No response");
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					//e.printStackTrace();
					log.writeLog(requestId + " | putObject(): " + e.toString());
				}
			}
		} while(++count < RETRY);
		return cloudResponse;
	}
	
	/** 确认上传
	 * @param fileId 文件ID
	 * @return SAP接口确认上传返回信息
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	private SapResponse.UploadedFileResponse[] confirmUploading(String fileId) throws ClientProtocolException, IOException {
		String xmlData;
		SapResponse.UploadedFileResponse[] uploadedFileResponse = null;
		int count = 0;
		do {
			xmlData = SapInterface.uploadedFile(defSapAddr, defAppTokenId, fileId, "1");
			uploadedFileResponse = SapResponse.parseUploadedFileResponse(xmlData);
			// determine whether to retry
			if(uploadedFileResponse != null && uploadedFileResponse.length > 0 && uploadedFileResponse[0].resultCode != null) {
				if(uploadedFileResponse[0].resultCode.equals(SapResponse.SUCCESS)) {
					break;
				} else {
					System.out.println(requestId + " | confirmUploading(): (" + uploadedFileResponse[0].resultCode + ")");
					log.writeLog(requestId + " | confirmUploading(): (" + uploadedFileResponse[0].resultCode + ")");
				}
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					//e.printStackTrace();
					log.writeLog(requestId + " | confirmUploading(): " + e.toString());
				}
			} else {
				System.out.println(requestId + " | confirmUploading(): No response");
				log.writeLog(requestId + " | confirmUploading(): No response");
			}
		} while(++count < RETRY);
		return uploadedFileResponse;
	}
	
	/**
	 * Copy the file to common spaces in the cloud, and then copy it to user dir	
	 */
	/** 上传文件主流程，调用SAP和云服务器的操作
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private void uploadFile() throws ClientProtocolException, IOException, FileNotFoundException {
		SapResponse.CreateFileResponse[] createFileResponse = null;
		CloudResponse.PutObjectResponse putObjectResponse = null;
		SapResponse.UploadedFileResponse[] uploadedFileResponse = null;

		String fileId = null;
		
		// pre-upload a file
		createFileResponse = createCloudFile();
	
		if(createFileResponse != null && createFileResponse.length > 0 && createFileResponse[0].resultCode != null && createFileResponse[0].resultCode.equals("1")) {		//create file successfully
			System.out.println(requestId + " | Create file OK!");
			log.writeLog(requestId + " | Create file OK!");
			
			// upload a file to the cloud
			putObjectResponse = putObject(createFileResponse[0].RealStorageURL, createFileResponse[0].Date, createFileResponse[0].Authorization);
			
			if(putObjectResponse != null && putObjectResponse.statusCode == 200) {
				System.out.println(requestId + " | Put object OK!");
				log.writeLog(requestId + " | Put object OK!");
				
				// confirm the "uploaded" status
				if(putObjectResponse.x_cdmi_file_id != "")
					fileId = putObjectResponse.x_cdmi_file_id;
				else
					fileId = createFileResponse[0].fileID;
				uploadedFileResponse = confirmUploading(fileId);
				
				if(uploadedFileResponse != null && uploadedFileResponse.length > 0 && uploadedFileResponse[0].resultCode != null && uploadedFileResponse[0].resultCode.equals("1")) {		//confirmation of uploading OK
					System.out.println(requestId + " | Upload file OK!");
					log.writeLog(requestId + " | Upload file OK!");
				
					log.writeLog(requestId + " | Real storage url of " + fileNameInCache + ": " + createFileResponse[0].RealStorageURL);
				
				} else {	//error in confirming the uploaded status
					if(uploadedFileResponse == null) {
						errInUploadedFile(null, "No response");
					} else if(uploadedFileResponse.length <= 0) {
						errInUploadedFile(null, "Wrong number of files");
					} else if(uploadedFileResponse[0].resultCode == null) {
						errInUploadedFile(null, "Null resultCode field");
					} else if(!uploadedFileResponse[0].resultCode.equals("1")) {
						errInUploadedFile(uploadedFileResponse[0].resultCode, uploadedFileResponse[0].resultDesc);
					}
				}

			} else {	//error in putting object		
				if(putObjectResponse == null) {
					errInPutObject(null, "No response");
				} else if(putObjectResponse.statusCode != 200) {
					errInPutObject(String.valueOf(putObjectResponse.statusCode), putObjectResponse.code);
				}
			}
		} else {	//error in creating file
			if(createFileResponse == null) {
				errInCreateFile(null, "No response");
			} else if(createFileResponse.length <= 0) {
				errInCreateFile(null, "Wrong number of files");
			} else if(createFileResponse[0].resultCode == null) {
				errInCreateFile(null, "Null resultCode field");
			} else if(!createFileResponse[0].resultCode.equals("1")) {
				errInCreateFile(createFileResponse[0].resultCode, createFileResponse[0].resultDesc);
			}
		}
	}
	
	/** 文件预上传错误处理
	 * @param sapCode SAP码
	 * @param desc 描述
	 */
	private void errInCreateFile(String sapCode, String desc) {
		int code = ResultCode.getSapResultCode(sapCode, desc);
		System.out.println(requestId + " | Create file failed! (" + sapCode + ")" + desc);
		log.writeLog(requestId + " | Create file failed! (" + sapCode + ")" + desc);
		dbUpdater.executeUpdate(ustmt, 
				"update requestTable set status = 'error', errorMsg = 'Sap - CreateFile: (" + sapCode + ")" + desc + "', errorCode = " + code + " where requestId = '" + requestId + "'");
	}
	
	/** 上传文件数据错误处理
	 * @param cloudCode 云错误码
	 * @param desc 描述
	 */
	private void errInPutObject(String cloudCode, String desc) {
		int code = ResultCode.getCloudResultCode(desc);
		System.out.println(requestId + " | Cannot upload the file to the cloud: (" + cloudCode + ")" + desc);
		log.writeLog(requestId + " | Cannot upload the file to the cloud: (" + cloudCode + ")" + desc);
		dbUpdater.executeUpdate(ustmt, 
				"update requestTable set status = 'error', errorMsg = 'Cloud - PutObject: (" + cloudCode + ")" + desc + "', errorCode = " + code + " where requestId = '" + requestId + "'");
	}
	
	/** 文件确认上传错误处理
	 * @param sapCode SAP码
	 * @param desc 描述
	 */
	private void errInUploadedFile(String sapCode, String desc) {
		int code = ResultCode.getSapResultCode(sapCode, desc);
		System.out.println(requestId + " | Fail to modify the uploading status: (" + sapCode + ")" + desc);
		log.writeLog(requestId + " | Fail to modify the uploading status: (" + sapCode + ")" + desc);
		dbUpdater.executeUpdate(ustmt, 
				"update requestTable set status = 'error', errorMsg = 'Sap - UploadedFile: (" + sapCode + ")" + desc + "', errorCode = " + code + " where requestId = '" + requestId + "'");
	}
	
	/** 删除转换后的文件
	 * @return 是否成功
	 */
	private boolean deleteOutputFile() {
		int count = 0;
		do {
			try {
				FileOperator.deleteFile(localFilePath);
				
				System.out.println(requestId + " | Delete output file " + localFilePath + " OK!");
				log.writeLog(requestId + " | Delete output file " + localFilePath + " OK!");
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				log.writeLog(requestId + " | deleteFile(): " + e.toString());
			}
		} while(++count < RETRY);
		
		System.out.println(requestId + " | Delete output file " + localFilePath + " failed");
		log.writeLog(requestId + " | Delete output file " + localFilePath + " failed");
		return false;
	}
		
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		
		System.out.println("Uploading thread is running:\n    Uploaded file name: " + fileNameInCache);
		log.writeLog("Uploading thread is running:\n    Uploaded file name: " + fileNameInCache);
		
		System.out.println(requestId + " | Local file path: " + localFilePath);
		log.writeLog(requestId + " | Local file path: " + localFilePath);
		System.out.println(requestId + " | File size: " + fileSize);
		log.writeLog(requestId + " | File size: " + fileSize);
		
		/*no platform
		try {
			uploadFile();
		} catch (Exception e) {
			e.printStackTrace();
			log.writeLog(requestId + " | " + e.toString());
			
			dbUpdater.executeUpdate(ustmt, 
					"update requestTable set status = 'error', errorMsg = '" + e.toString() + "', errorCode = " + ResultCode.INTERNAL_EXCEPTION + " where requestId = '" + requestId + "'");
		}*/
		try {
			FileInputStream input = new FileInputStream(new File(localFilePath));
			FileOutputStream output = new FileOutputStream(new File(cacheFolderId + localFileName));
			int b;
			while ((b = input.read()) != -1)
				output.write(b);
			input.close();
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// delete the local file
		deleteOutputFile();

		System.out.println(requestId + " | Uploading thread stops");
		log.writeLog(requestId + " | Uploading thread stops");
	}
}
