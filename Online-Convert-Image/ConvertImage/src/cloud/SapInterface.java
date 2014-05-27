//////////////////////////////////////////////////////////////////////////  
///        COPYRIGHT NOTICE  
///        Copyright (c) 2011, 上海电信 
///        All rights reserved.  
///  
/// @file SapInterface.java  
/// @brief 本文件为与Sap接口通讯的类。
///  
///     调用各种Sap命令。主要将Sap命令封装为各种XML数据。
///  
/// @version 1.0
/// @author 易源
/// @date 2011年10月
///  
///  
///        修订说明：最初版本  
//////////////////////////////////////////////////////////////////////////

package cloud;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import downFile.REST;

/** 本类的功能：SAP接口调用类
*
*/
public class SapInterface {
	/** 字符集 */
	private static String charSet = "utf-8";
	/** 最大文件名长度 */
	public static final int MAX_FILE_NAME_LEN = 251;
	
	/** 设置字符集
	 * @param charSet 字符集
	 */
	public static void setCharSet(String charSet) {
		SapInterface.charSet = charSet;
	}
	
	/** 查询用户网盘信息接口（参数皆为SAP接口参数）
	 * @param url SAP地址
	 * @param TokenID
	 * @return SAP返回信息
	 * @throws Exception
	 */
	public static String storageInfo(String url, String TokenID) throws Exception {
		BufferedReader br;
		String temp;
		StringBuffer xmlResponse = null;
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet get = new HttpGet(url + "/SAP-V2/user/storageInfo");
		HttpResponse response = null;
		
		httpclient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, SapInterface.charSet);
		
		get.addHeader("TokenID", TokenID);

		response = httpclient.execute(get);
		
		br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		xmlResponse = new StringBuffer();
		while ((temp = br.readLine()) != null) {
			xmlResponse.append(temp);
			xmlResponse.append("\n");
		}
		
		return xmlResponse.toString();
	}
	
	/** 文件预上传接口，预上传单个文件（参数皆为SAP接口参数）
	 * @param url
	 * @param TokenID
	 * @param parentFolderID
	 * @param fileName
	 * @param fileSize
	 * @param fileType
	 * @param uploadType
	 * @return SAP返回信息
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static String createFile(String url, String TokenID, String parentFolderID, String fileName, long fileSize, String fileType, String uploadType) throws ClientProtocolException, IOException {
		BufferedReader br;
		String temp;
		StringBuffer xmlRequest = null;
		StringBuffer xmlResponse = null;
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost post = new HttpPost(url + "/SAP-V2/file/create");
		HttpResponse response = null;
		StringEntity strEntity = null;
		
		httpclient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, SapInterface.charSet);
		
		xmlRequest = new StringBuffer("<CreateFile>");
		xmlRequest.append("<FileInfo>");
		xmlRequest.append("<parentFolderID>");
		xmlRequest.append(parentFolderID);
		xmlRequest.append("</parentFolderID>");
		xmlRequest.append("<fileName><![CDATA[");
		xmlRequest.append(fileName);
		xmlRequest.append("]]></fileName>");
		xmlRequest.append("<fileSize>");
		xmlRequest.append(fileSize);
		xmlRequest.append("</fileSize>");
		xmlRequest.append("<fileType><![CDATA[");
		xmlRequest.append(fileType);
		xmlRequest.append("]]></fileType>");
		xmlRequest.append("<uploadType>");
		xmlRequest.append(uploadType);
		xmlRequest.append("</uploadType>");
		xmlRequest.append("</FileInfo>");
		xmlRequest.append("</CreateFile>");
		
		strEntity = new StringEntity(xmlRequest.toString(), charSet);
		post.addHeader("TokenID", TokenID);
		post.addHeader("Content-Type", "application/xml;charset=utf-8");
		post.setEntity(strEntity);
			
		response = httpclient.execute(post);
		br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		xmlResponse = new StringBuffer();
		while ((temp = br.readLine()) != null) {
			xmlResponse.append(temp);
			xmlResponse.append("\n");
		}

		return xmlResponse.toString();
	}
	
	/** 文件预上传接口，可批量预上传（参数皆为SAP接口参数）
	 * @param url
	 * @param TokenID
	 * @param parentFolderIDs
	 * @param fileNames
	 * @param fileSizes
	 * @param fileTypes
	 * @param uploadTypes
	 * @return SAP返回信息
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static String createFileBatch(String url, String TokenID, String[] parentFolderIDs, String[] fileNames, long[] fileSizes, String[] fileTypes, String[] uploadTypes) throws ClientProtocolException, IOException {
		int i;
		BufferedReader br;
		String temp;
		StringBuffer xmlRequest = null;
		StringBuffer xmlResponse = null;
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost post = new HttpPost(url + "/SAP-V2/file/create");
		HttpResponse response = null;
		StringEntity strEntity = null;
		
		httpclient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, SapInterface.charSet);
		
		xmlRequest = new StringBuffer("<CreateFile>");
		for(i = 0; i < fileNames.length; i++) {
			xmlRequest.append("<FileInfo>");
			xmlRequest.append("<parentFolderID>");
			xmlRequest.append(i < parentFolderIDs.length ? parentFolderIDs[i] : "");
			xmlRequest.append("</parentFolderID>");
			xmlRequest.append("<fileName><![CDATA[");
			xmlRequest.append(fileNames[i]);
			xmlRequest.append("]]></fileName>");
			xmlRequest.append("<fileSize>");
			xmlRequest.append(i < fileSizes.length ? fileSizes[i] : 0);
			xmlRequest.append("</fileSize>");
			xmlRequest.append("<fileType><![CDATA[");
			xmlRequest.append(i < fileTypes.length ? fileTypes[i] : "");
			xmlRequest.append("]]></fileType>");
			xmlRequest.append("<uploadType>");
			xmlRequest.append(i < uploadTypes.length ? uploadTypes[i] : "");
			xmlRequest.append("</uploadType>");
			xmlRequest.append("</FileInfo>");
		}
		xmlRequest.append("</CreateFile>");
		
		strEntity = new StringEntity(xmlRequest.toString(), charSet);
		post.addHeader("TokenID", TokenID);
		post.addHeader("Content-Type", "application/xml;charset=utf-8");
		post.setEntity(strEntity);
		
		response = httpclient.execute(post);
		br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		xmlResponse = new StringBuffer();
		while ((temp = br.readLine()) != null) {
			xmlResponse.append(temp);
			xmlResponse.append("\n");
		}

		return xmlResponse.toString();
	}
	
	/** 修改文件上传状态接口（参数皆为SAP接口参数）
	 * @param url
	 * @param TokenID
	 * @param fileID
	 * @param uploadStatus
	 * @return SAP信息
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static String uploadedFile(String url, String TokenID, String fileID, String uploadStatus) throws ClientProtocolException, IOException {
		BufferedReader br;
		String temp;
		StringBuffer xmlRequest = null;
		StringBuffer xmlResponse = null;
		HttpClient httpclient = new DefaultHttpClient();
		HttpPut put = new HttpPut(url + "/SAP-V2/file/uploaded");
		HttpResponse response = null;
		StringEntity strEntity = null;
		
		httpclient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, SapInterface.charSet);
		
		xmlRequest = new StringBuffer("<UploadedFile>");
		xmlRequest.append("<FileInfo>");
		xmlRequest.append("<fileID>");
		xmlRequest.append(fileID);
		xmlRequest.append("</fileID>");
		xmlRequest.append("<uploadStatus>");
		xmlRequest.append(uploadStatus);
		xmlRequest.append("</uploadStatus>");
		xmlRequest.append("</FileInfo>");
		xmlRequest.append("</UploadedFile>");

		strEntity = new StringEntity(xmlRequest.toString(), charSet);
		put.addHeader("TokenID", TokenID);
		put.addHeader("Content-Type", "application/xml;charset=utf-8");
		put.setEntity(strEntity);
		
		response = httpclient.execute(put);
		br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		xmlResponse = new StringBuffer();
		while ((temp = br.readLine()) != null) {
			xmlResponse.append(temp);
			xmlResponse.append("\n");
		}
		
		return xmlResponse.toString();
	}
	
	/** 修改文件上传状态接口，批量确认（参数皆为SAP接口参数）
	 * @param url
	 * @param TokenID
	 * @param fileIDs
	 * @param uploadStatus
	 * @return SAP返回信息
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static String uploadedFileBatch(String url, String TokenID, String[] fileIDs, String[] uploadStatus) throws ClientProtocolException, IOException {
		int i;
		BufferedReader br;
		String temp;
		StringBuffer xmlRequest = null;
		StringBuffer xmlResponse = null;
		HttpClient httpclient = new DefaultHttpClient();
		HttpPut put = new HttpPut(url + "/SAP-V2/file/uploaded");
		HttpResponse response = null;
		StringEntity strEntity = null;
		
		httpclient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, SapInterface.charSet);
		
		xmlRequest = new StringBuffer("<UploadedFile>");
		for(i = 0; i < fileIDs.length; i++) {
			xmlRequest.append("<FileInfo>");
			xmlRequest.append("<fileID>");
			xmlRequest.append(fileIDs[i]);
			xmlRequest.append("</fileID>");
			xmlRequest.append("<uploadStatus>");
			xmlRequest.append(i < uploadStatus.length ? uploadStatus[i] : "0");
			xmlRequest.append("</uploadStatus>");
			xmlRequest.append("</FileInfo>");
		}
		xmlRequest.append("</UploadedFile>");
		
		strEntity = new StringEntity(xmlRequest.toString(), charSet);
		put.addHeader("TokenID", TokenID);
		put.addHeader("Content-Type", "application/xml;charset=utf-8");
		put.setEntity(strEntity);
		
		response = httpclient.execute(put);
		br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		xmlResponse = new StringBuffer();
		while ((temp = br.readLine()) != null) {
			xmlResponse.append(temp);
			xmlResponse.append("\n");
		}
		
		return xmlResponse.toString();
	}
	
	/** 文件预下载接口（参数皆为SAP接口参数）
	 * @param url
	 * @param TokenID
	 * @param fileID
	 * @return SAP返回信息
	 * @throws IOException
	 */
	public static String downFile(String url, String TokenID, String fileID) throws IOException {
		BufferedReader br;
		String temp;
		StringBuffer xmlResponse = null;
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet get = new HttpGet(url + "/SAP-V2/file/down/" + fileID);
		HttpResponse response = null;
		
		httpclient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, SapInterface.charSet);
		
		get.addHeader("TokenID", TokenID);

		response = httpclient.execute(get);
		
		br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		xmlResponse = new StringBuffer();
		while ((temp = br.readLine()) != null) {
			xmlResponse.append(temp);
			xmlResponse.append("\n");
		}
		
		return xmlResponse.toString();
	}
	
	/** 获取指定目录下的所有文件接口（参数皆为SAP接口参数）
	 * @param url
	 * @param TokenID
	 * @param parentFolderID
	 * @return SAP返回信息
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static String queryFile(String url, String TokenID, String parentFolderID) throws ClientProtocolException, IOException {
		BufferedReader br;
		String temp;
		StringBuffer xmlResponse = null;
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet get = new HttpGet(url + "/SAP-V2/file/query/" + parentFolderID);
		HttpResponse response = null;
		
		httpclient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, SapInterface.charSet);
		
		get.addHeader("TokenID", TokenID);

		response = httpclient.execute(get);
		
		br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		xmlResponse = new StringBuffer();
		while ((temp = br.readLine()) != null) {
			xmlResponse.append(temp);
			xmlResponse.append("\n");
		}
		
		return xmlResponse.toString();
	}
	
	/** 删除指定文件接口（参数皆为SAP接口参数）
	 * @param url
	 * @param TokenID
	 * @param FileIDS
	 * @param Completely
	 * @return SAP返回信息
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static String deleteFile(String url, String TokenID, String FileIDS, String Completely) throws ClientProtocolException, IOException {
		BufferedReader br;
		String temp;
		StringBuffer xmlResponse = null;
		HttpClient httpclient = new DefaultHttpClient();
		HttpDelete delete = new HttpDelete(url + "/SAP-V2/file/delete/");
		HttpResponse response = null;
		
		httpclient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, SapInterface.charSet);
		
		delete.addHeader("TokenID", TokenID);
		delete.addHeader("FileIDS", FileIDS);
		if(Completely != null) {
			delete.addHeader("Completely", Completely);
		}

		response = httpclient.execute(delete);
		
		br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		xmlResponse = new StringBuffer();
		while ((temp = br.readLine()) != null) {
			xmlResponse.append(temp);
			xmlResponse.append("\n");
		}
		
		return xmlResponse.toString();
	}
	
	/** 移动文件接口（参数皆为SAP接口参数）
	 * @param url
	 * @param TokenID
	 * @param ParentFolderID
	 * @param FileIDS
	 * @param Flag
	 * @return SAP信息
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static String moveFile(String url, String TokenID, String ParentFolderID, String FileIDS, String Flag) throws ClientProtocolException, IOException {
		BufferedReader br;
		String temp;
		StringBuffer xmlResponse = null;
		HttpClient httpclient = new DefaultHttpClient();
		HttpPut put = new HttpPut(url + "/SAP-V2/file/move");
		HttpResponse response = null;
		
		httpclient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, SapInterface.charSet);
		
		put.addHeader("TokenID", TokenID);
		put.addHeader("ParentFolderID", ParentFolderID);
		put.addHeader("FileIDS", FileIDS);
		if(Flag != null) {
			put.addHeader("Flag", Flag);
		}
		
		response = httpclient.execute(put);
		br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		xmlResponse = new StringBuffer();
		while ((temp = br.readLine()) != null) {
			xmlResponse.append(temp);
			xmlResponse.append("\n");
		}
		
		return xmlResponse.toString();
	}
	
	/** 创建文件夹接口（参数皆为SAP接口参数）
	 * @param url
	 * @param TokenID
	 * @param parentFolderID
	 * @param folderName
	 * @return SAP返回信息
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static String createFolder(String url, String TokenID, String parentFolderID, String folderName) throws ClientProtocolException, IOException {
		BufferedReader br;
		String temp;
		StringBuffer xmlRequest = null;
		StringBuffer xmlResponse = null;
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost post = new HttpPost(url + "/SAP-V2/folder/create");
		HttpResponse response = null;
		StringEntity strEntity = null;
		
		httpclient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, SapInterface.charSet);
		
		xmlRequest = new StringBuffer("<CreateFolder><FolderInfo><parentFolderID>");
		xmlRequest.append(parentFolderID);
		xmlRequest.append("</parentFolderID><folderName><![CDATA[");
		xmlRequest.append(folderName);
		xmlRequest.append("]]></folderName></FolderInfo></CreateFolder>");

		strEntity = new StringEntity(xmlRequest.toString(), charSet);
		post.addHeader("TokenID", TokenID);
		post.addHeader("Content-Type", "application/xml;charset=utf-8");
		post.setEntity(strEntity);
		
		response = httpclient.execute(post);
		br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		xmlResponse = new StringBuffer();
		while ((temp = br.readLine()) != null) {
			xmlResponse.append(temp);
			xmlResponse.append("\n");
		}
		
		return xmlResponse.toString();
	}
	
	/** 批量创建文件夹接口，批量创建（参数皆为SAP接口参数）
	 * @param url
	 * @param TokenID
	 * @param parentFolderIDs
	 * @param folderNames
	 * @return SAP返回信息
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static String createFolderBatch(String url, String TokenID, String[] parentFolderIDs, String[] folderNames) throws ClientProtocolException, IOException {
		int i;
		BufferedReader br;
		String temp;
		StringBuffer xmlRequest = null;
		StringBuffer xmlResponse = null;
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost post = new HttpPost(url + "/SAP-V2/folder/multicreate");
		HttpResponse response = null;
		StringEntity strEntity = null;
		
		httpclient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, SapInterface.charSet);
		
		xmlRequest = new StringBuffer("<CreateFolder>");
		for(i = 0; i < folderNames.length; i++) {
			xmlRequest.append("<FolderInfo>");
			xmlRequest.append("<parentFolderID>");
			xmlRequest.append(i < parentFolderIDs.length ? parentFolderIDs[i] : "");
			xmlRequest.append("</parentFolderID>");
			xmlRequest.append("<folderName><![CDATA[");
			xmlRequest.append(folderNames[i]);
			xmlRequest.append("]]></folderName>");
			xmlRequest.append("</FolderInfo>");
		}
		xmlRequest.append("</CreateFolder>");

		strEntity = new StringEntity(xmlRequest.toString(), charSet);
		post.addHeader("TokenID", TokenID);
		post.addHeader("Content-Type", "application/xml;charset=utf-8");
		post.setEntity(strEntity);
		
		response = httpclient.execute(post);
		br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		xmlResponse = new StringBuffer();
		while ((temp = br.readLine()) != null) {
			xmlResponse.append(temp);
			xmlResponse.append("\n");
		}
		
		return xmlResponse.toString();
	}
	
	/** 获取某个目录下的文件夹接口（参数皆为SAP接口参数）
	 * @param url
	 * @param TokenID
	 * @param parentFolderID
	 * @return SAP返回信息
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static String queryFolder(String url, String TokenID, String parentFolderID) throws ClientProtocolException, IOException {
		BufferedReader br;
		String temp;
		StringBuffer xmlResponse = null;
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet get = new HttpGet(url + "/SAP-V2/folder/query/" + parentFolderID);
		HttpResponse response = null;
		
		httpclient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, SapInterface.charSet);
		
		get.addHeader("TokenID", TokenID);

		response = httpclient.execute(get);
		
		br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		xmlResponse = new StringBuffer();
		while ((temp = br.readLine()) != null) {
			xmlResponse.append(temp);
			xmlResponse.append("\n");
		}
		
		return xmlResponse.toString();
	}
	
	/** 获取某个文件夹的信息接口（参数皆为SAP接口参数）
	 * @param url
	 * @param TokenID
	 * @param folderID
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static String queryFolderInfo(String url, String TokenID, String folderID) throws ClientProtocolException, IOException {
		BufferedReader br;
		String temp;
		StringBuffer xmlResponse = null;
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet get = new HttpGet(url + "/SAP-V2/folder/queryinfo/" + folderID);
		HttpResponse response = null;
		
		httpclient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, SapInterface.charSet);
		
		get.addHeader("TokenID", TokenID);

		response = httpclient.execute(get);
		
		br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		xmlResponse = new StringBuffer();
		while ((temp = br.readLine()) != null) {
			xmlResponse.append(temp);
			xmlResponse.append("\n");
		}
		
		return xmlResponse.toString();
	}
	
	/** 用户获取应用网盘中指定目录下的文件夹、文件信息
	 * @param url
	 * @param TokenID
	 * @param AppID
	 * @param userAppToken
	 * @param parentFolderID
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static String AppObjectList(String url, String TokenID, String AppID, String userAppToken, String parentFolderID) throws ClientProtocolException, IOException {
		BufferedReader br;
		String temp;
		StringBuffer xmlResponse = null;
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet get = new HttpGet(url + "/SAP-V2/filesys/app/query/" + parentFolderID);
		HttpResponse response = null;
		
		httpclient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, SapInterface.charSet);
		
		get.addHeader("TokenID", TokenID);
		get.addHeader("AppID", AppID);
		get.addHeader("userAppToken", userAppToken);

		response = httpclient.execute(get);
		
		br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		xmlResponse = new StringBuffer();
		while ((temp = br.readLine()) != null) {
			xmlResponse.append(temp);
			xmlResponse.append("\n");
		}
		
		return xmlResponse.toString();
	}
	
	/*
	public static String copyFileByCloudFolderName(String url, String TokenID, String FileID, String UserID, String parentFolderName, String fileName) throws Exception {
		BufferedReader br;
		String temp;
		StringBuffer xmlRequest = null;
		StringBuffer xmlResponse = null;
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost post = new HttpPost(url + "/SAP-V2/file/copy");
		HttpResponse response = null;
		StringEntity strEntity = null;
		
		httpclient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, SapInterface.charSet);
		
		xmlRequest = new StringBuffer("<CopyFile><FileInfo><parentFolderName>");
		xmlRequest.append(parentFolderName);
		xmlRequest.append("</parentFolderName><fileName>");
		xmlRequest.append(fileName);
		xmlRequest.append("</fileName></FileInfo></CopyFile>");
		
		strEntity = new StringEntity(xmlRequest.toString(), charSet);
		post.addHeader("TokenID", TokenID);
		post.addHeader("FileID", FileID);
		post.addHeader("UserID", UserID);
		post.addHeader("Content-Type", "application/xml;charset=utf-8");
		post.setEntity(strEntity);
			
		response = httpclient.execute(post);
		br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		xmlResponse = new StringBuffer();
		while ((temp = br.readLine()) != null) {
			xmlResponse.append(temp);
			xmlResponse.append("\n");
		}
		
		return xmlResponse.toString();
	}*/
	
	/** 应用复制文件到用户应用目录
	 * @param url
	 * @param TokenID
	 * @param FileID
	 * @param UserID
	 * @param parentFolderID
	 * @param fileName
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static String copyFile(String url, String TokenID, String FileID, String UserID, String parentFolderID, String fileName) throws ClientProtocolException, IOException {
		BufferedReader br;
		String temp;
		StringBuffer xmlRequest = null;
		StringBuffer xmlResponse = null;
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost post = new HttpPost(url + "/SAP-V2/file/copy");
		HttpResponse response = null;
		StringEntity strEntity = null;
		
		httpclient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, SapInterface.charSet);
		httpclient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 0);
		
		xmlRequest = new StringBuffer("<CopyFile><FileInfo><parentFolderID>");
		xmlRequest.append(parentFolderID);
		xmlRequest.append("</parentFolderID><fileName><![CDATA[");
		xmlRequest.append(fileName);
		xmlRequest.append("]]></fileName></FileInfo></CopyFile>");
		
		strEntity = new StringEntity(xmlRequest.toString(), charSet);
		post.addHeader("TokenID", TokenID);
		post.addHeader("FileID", FileID);
		post.addHeader("UserID", UserID);
		post.addHeader("Content-Type", "application/xml;charset=utf-8");
		post.setEntity(strEntity);
		
		response = httpclient.execute(post);
		br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		xmlResponse = new StringBuffer();
		while ((temp = br.readLine()) != null) {
			xmlResponse.append(temp);
			xmlResponse.append("\n");
		}
		
		return xmlResponse.toString();
	}
	
	/** 应用预复制文件到用户应用目录
	 * @param url
	 * @param TokenID
	 * @param FileID
	 * @param UserID
	 * @param fileName
	 * @return
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static String preCopyToAppDir(String url, String TokenID, String FileID, String UserID, String parentFolderID, String fileName) throws ClientProtocolException, IOException {
		BufferedReader br;
		String temp;
		StringBuffer xmlRequest = null;
		StringBuffer xmlResponse = null;
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost post = new HttpPost(url + "/SAP-V2/file/preCopyToAppDir");
		HttpResponse response = null;
		StringEntity strEntity = null;
		
		httpclient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, SapInterface.charSet);
		httpclient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 0);
		
		xmlRequest = new StringBuffer("<CopyFile><FileInfo><parentFolderID>");
		xmlRequest.append(parentFolderID);
		xmlRequest.append("</parentFolderID><fileName><![CDATA[");
		xmlRequest.append(fileName);
		xmlRequest.append("]]></fileName></FileInfo></CopyFile>");
		
		strEntity = new StringEntity(xmlRequest.toString(), charSet);
		post.addHeader("TokenID", TokenID);
		post.addHeader("FileID", FileID);
		post.addHeader("UserID", UserID);
		post.addHeader("Content-Type", "application/xml;charset=utf-8");
		post.setEntity(strEntity);
		
		response = httpclient.execute(post);
		br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		xmlResponse = new StringBuffer();
		while ((temp = br.readLine()) != null) {
			xmlResponse.append(temp);
			xmlResponse.append("\n");
		}
		
		return xmlResponse.toString();
	}
	
	/**
	 * @param userId
	 * @param fileName
	 * @param is
	 * @return
	 */
	public static File saveToFile(String userId, String fileName, InputStream is){
		String path = REST.DOWNLOAD_PATH;
		File tmp = new File(path);
		if(!tmp.exists()||!tmp.isDirectory()) {
			//DownloadFile.log.writeLog("ERROR download directory " + path + " not exist");
			return null;
		}
		File f= new File(path,fileName);
		
		try {
			if(!f.exists()) f.createNewFile();
			FileOutputStream op = new FileOutputStream(f);
			int by;
			byte[] buff= new byte[2*1024*1024];//2M buffer
			while((by=is.read(buff))!=-1)
				op.write(buff,0,by);
			op.flush();
			op.close();
			buff=null;
		} catch (IOException e) {
			e.printStackTrace();
		//	interfacer.InterFacer.log.writeLog("(filename)" + fileName + ": ERROR fail to save to file");
		}
		return f;
	}
	
	/**
	 * @param userId
	 * @return
	 */
	public static String getUserToken(String userId) {
		String token = null;
		try {		
			URL url = new URL(REST.AAA_GET_USERTOKEN);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	
			conn.setRequestProperty("Content-Type", "Application/xml");

			conn.setReadTimeout(30 * 1000);
			conn.setConnectTimeout(30 * 1000);
			conn.setRequestMethod("PUT");
			conn.addRequestProperty("key", userId);
		//	System.out.println(userId);
		/*	
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = "";
			while ((line = br.readLine()) != null)
				System.out.println(line);
		*/	
			SAXBuilder sb = new SAXBuilder(); 
			Document xmlDoc = sb.build(conn.getInputStream());
			Element root = xmlDoc.getRootElement();
			String recode = root.getChild("flag").getValue();
			String message = root.getChild("message").getValue();
			if(recode.equals("0")){
				token = root.getChild("token").getValue().trim();
			}else{
			//	System.out.println(message);
				throw new Exception("get user token error " + message);
			}
			xmlDoc = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return token;
	}

	/**
	 * @param userId
	 * @param fileID
	 * @return
	 */
	public static Object[] downloadFile(String userId, String fileID) throws IOException {
		/*no platform
		File tmpFile =null;
		URL url;
		try {
			String userToken = getUserToken(userId);
			url = new URL(REST.SAP_PRE_DOWNLOAD.replace("{fileID}", fileID));
			HttpURLConnection uc = (HttpURLConnection) url.openConnection();
			uc.setConnectTimeout(10000);
			uc.setReadTimeout(10000);
			uc.setRequestProperty("Content-type", REST.SAP_CONTENT_TYPE);
			uc.addRequestProperty("TokenID", userToken);		
	
			SAXBuilder sb = new SAXBuilder(); 	
 			Document xmlDoc = sb.build(uc.getInputStream());
			Element root = xmlDoc.getRootElement();
			Element meta = root.getChild("FileInfo").getChild("metaData");	
			Map<String,String> metas = new HashMap<String,String>();
			if(meta!=null){
				for(Object e : meta.getChildren()){
					Element term =(Element) e;
					metas.put(term.getChildText("name"), term.getChildText("value"));
				}
				String downURL = metas.get("RealStorageURL");
				if(downURL !=null){
					HttpURLConnection fileDownLoadConnection = (HttpURLConnection) new URL(downURL).openConnection();
					fileDownLoadConnection.setConnectTimeout(10000);
					fileDownLoadConnection.setReadTimeout(100000);
					fileDownLoadConnection.addRequestProperty("AccessKey", metas.get("AccessKey"));
					fileDownLoadConnection.addRequestProperty("Authorization", metas.get("Authorization"));
					fileDownLoadConnection.addRequestProperty("Date", metas.get("Date"));
					String fileName = root.getChild("FileInfo").getChild("fileName").getValue();
					tmpFile = saveToFile(userId, fileID, fileDownLoadConnection.getInputStream());
					String filePath = tmpFile.getAbsolutePath();
					long size = tmpFile.length();
					Object[] result = {fileName, filePath, size};
					return result;
				//	log.info("Download ok ! File Path:"+tmpFile.getAbsolutePath());
				} else {
					System.out.println("downURL is null");
					throw new Exception("downURL is null");
				}
			}else{
				System.out.println("meta is null");
		//		System.out.println(xmlDoc.toString());
				throw new Exception("meta is null, the response of pre-download is " + xmlDoc.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;*/
		File tmpFile = new File(fileID);
		FileInputStream input = new FileInputStream(tmpFile);
		FileOutputStream output = new FileOutputStream("D:\\originalFile\\" + tmpFile.getName());
		int b = 0;
		while ((b = input.read()) != -1)
			output.write(b);
		input.close();
		output.close();
		Object[] result = new Object[]{tmpFile.getName(), "D:\\originalFile\\" + tmpFile.getName(), tmpFile.length()};
		
		return result;
	}
}
