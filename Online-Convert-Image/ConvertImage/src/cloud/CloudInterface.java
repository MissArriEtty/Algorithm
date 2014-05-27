//////////////////////////////////////////////////////////////////////////  
///        COPYRIGHT NOTICE  
///        Copyright (c) 2011, 上海电信 
///        All rights reserved.  
///  
/// @file CloudInterface.java  
/// @brief 本文件为与云服务器交互的代码。
///  
///     上传文件到云服务器需要调用本类的putObject方法。
///  
/// @version 1.0
/// @author 易源
/// @date 2011年10月
///  
///  
///        修订说明：最初版本  
//////////////////////////////////////////////////////////////////////////

package cloud;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import logPackage.cloudLog;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

/** 本类的功能：云服务器操作接口
*
* 调用云服务器的上传操作
*/
public class CloudInterface {
	/** 上传缓存大小 */
	private static final int BUFFER_SIZE = 4096;
	/** 上传分片大小 */
	private static int fragmentSize = 8 * 1024 * 1024;
	/** 上传文件内存映射大小 */
	private static long memoryMapSize = 8 * fragmentSize;
	/** 日志记录 */
	private static cloudLog log = null;
	//private static cloudLog log = new cloudLog(CloudInterface.class.getName());
	
	/** 设置日志对象
	 * @param log 日志对象
	 */
	public static void setLog(cloudLog log) {
		CloudInterface.log = log;
	}
	
	/** 设置上传分片大小
	 * @param fragmentSize 分片大小
	 */
	public static void setFragmentSize(int fragmentSize) {
		CloudInterface.fragmentSize = fragmentSize;
		memoryMapSize = 8 * CloudInterface.fragmentSize;
	}
	
	/** 调用云服务器的putObject接口上传数据
	 * @param localFilePath 本地文件路径
	 * @param fileSize 文件大小
	 * @param url 目标地址URL
	 * @param date 日期
	 * @param authorization 授权码
	 * @return 云服务器返回码
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static CloudResponse.PutObjectResponse putObject(String localFilePath, long fileSize, String url, String date, String authorization) throws FileNotFoundException, IOException {
		long i;
		int j;
		long count;
		HttpClient httpclient = new DefaultHttpClient();
		HttpPut put = null;
		HttpResponse httpResponse = null;
		CloudResponse.PutObjectResponse response = new CloudResponse.PutObjectResponse();		// the return variable
		long length = new File(localFilePath).length();
		byte[] output = null;
		MappedByteBuffer in = null;
		ByteArrayEntity byteEntity = null;
		long currentMapSize;
		int currentFragmentSize;
		boolean firstByte;
		
		if(url == null || url.equals("")) {
			System.out.println("putObject(): No remote address when uploading file: " + localFilePath);
			log.writeLog("putObject(): No remote address when uploading file: " + localFilePath);
			return null;
		}
		
		if(length == 0 && length != fileSize) {		//some error may occur in NFS when reading the file size
			length = fileSize;		//alternatively, use the variable "fileSize" instead of the size read from the NFS
		}
		
		System.out.println("Uploading: start time: " + System.currentTimeMillis());
		count = 0;
		System.out.println("putObject(): Local file path: " + localFilePath);
		log.writeLog("putObject(): Local file path: " + localFilePath);
		System.out.println("putObject(): File size: " + length);
		log.writeLog("putObject(): File size: " + length);
		for(i = 0; i < length; i += memoryMapSize) {
			System.out.println("putObject(): Memory map " + (i + 1));
			//log.writeLog("putObject(): Memory map " + (i + 1));
			//Current file map size
			currentMapSize = i + memoryMapSize <= length ? memoryMapSize : length - i;
			//Open the local file
			try {
				in = new FileInputStream(localFilePath).getChannel().map(FileChannel.MapMode.READ_ONLY, i, currentMapSize);
			} catch (IllegalArgumentException e) {
				log.writeLog("FileInputStream(): " + e.toString());
				throw e;
			}
			
			//Read and send data
			firstByte = true;		//If it's the first byte of the fragment, then calculating the size of the fragment is needed.
			currentFragmentSize = fragmentSize;	//no use, just for compiler checking
			for(j = 0; j < currentMapSize; j += BUFFER_SIZE) {
				if(firstByte) {		//Check out if this is the first byte of a new fragment in this memory map.
					System.out.println("putObject(): Fragment " + (j / fragmentSize + 1));
					//log.writeLog("putObject(): Fragment " + (j / fragmentSize + 1));
					currentFragmentSize = j + fragmentSize <= currentMapSize ? fragmentSize : (int) (currentMapSize - j);
					output = new byte[currentFragmentSize];
					firstByte = false;
				}
				//System.out.println(output.length + " : " + j + " : " + j%fragmentSize + " : " + (j + BUFFER_SIZE > currentMapSize ? currentMapSize - j : BUFFER_SIZE));
				in.get(output, j % fragmentSize, j + BUFFER_SIZE > currentMapSize ? (int) (currentMapSize - j) : BUFFER_SIZE);
				//output[j % fragmentSize] = in.get(j);		//read
				count += (j + BUFFER_SIZE > currentMapSize ? (int) (currentMapSize - j) : BUFFER_SIZE);
				/*if(count % (1024*1024) == 0) {
					System.out.println(count);
				}*/
				
				if((j + BUFFER_SIZE) % fragmentSize == 0 || j + BUFFER_SIZE >= currentMapSize) {	//send
					try {
						put = new HttpPut(url);
					} catch (IllegalArgumentException e) {
						log.writeLog("HttpPut(): " + e.toString() + " : " + url);
					}
					firstByte = true;
					System.out.println("putObject(): Sending bytes from " + (count - currentFragmentSize + 1) + " to " + count + "...");
					//log.writeLog("putObject(): Sending bytes from " + (count - currentFragmentSize + 1) + " to " + count + "...");
					byteEntity = new ByteArrayEntity(output);
					put.setEntity(byteEntity);
					put.addHeader("Content-Type", "binary/octet-stream");
					put.addHeader("Date", date);
					put.addHeader("Authorization", authorization);
					put.addHeader("x-cdmi-object-offset", String.valueOf(i + (int) (j / fragmentSize) * fragmentSize));
					put.addHeader("x-cdmi-overwrite", "true");
					System.out.println("putObject(): offset: " + String.valueOf(i + (int) (j / fragmentSize) * fragmentSize));
					//log.writeLog("putObject(): offset: " + String.valueOf(i + (int) (j / fragmentSize) * fragmentSize));
					if(count >= length) {
						put.addHeader("x-cdmi-object-commit", "true");
						System.out.println("commit");
						//log.writeLog("putObject(): commit");
					} else {
						put.addHeader("x-cdmi-object-commit", "false");
						System.out.println("not commit");
					}
					
					System.out.println("before http put");
					httpResponse = httpclient.execute(put);
					System.out.println("after http put");
					response = CloudResponse.parsePutObjectResponse(httpResponse);		//parse and generate the response value
					
					if(response == null || response.statusCode != 200) {
						System.out.println("putObject(): Http error occurs in uploading object");
						log.writeLog("putObject(): Http error occurs in uploading object");
						System.out.println("Uploading: end time: " + System.currentTimeMillis());
						return response;
					}
				}
			}
		}
		System.out.println("putObject(): Sending finished!");
		log.writeLog("putObject(): Sending finished!");
		//log.writeLog("Sending finished!");
		System.out.println("Uploading: end time: " + System.currentTimeMillis());
	
		return response;
	}
	
	/** 调用云服务器的复制文件接口
	 * @param url
	 * @param date
	 * @param authorization
	 * @param copySource
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static CloudResponse.CopyObjectResponse copyObject(String targetUrl, String date, String authorization, String copySource) throws ClientProtocolException, IOException {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPut put = null;
		HttpResponse httpResponse = null;
		CloudResponse.CopyObjectResponse response = new CloudResponse.CopyObjectResponse();
		
		put = new HttpPut(targetUrl);
		put.addHeader("Content-Type", "binary/octet-stream");
		put.addHeader("Date", date);
		put.addHeader("Authorization", authorization);
		put.addHeader("x-cdmi-copy-source", copySource);
		put.addHeader("x-cdmi-overwrite", "true");
		
		httpclient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 0);
		
		httpResponse = httpclient.execute(put);
		
		response = CloudResponse.parseCopyObjectResponse(httpResponse);
		
		return response;
	}
}
