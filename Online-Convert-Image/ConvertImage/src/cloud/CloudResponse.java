//////////////////////////////////////////////////////////////////////////  
///        COPYRIGHT NOTICE  
///        Copyright (c) 2011, 上海电信 
///        All rights reserved.  
///  
/// @file CloudResponse.java
/// @brief 本文件为处理云服务器返回信息的类。
///  
///     主要是处理putObject上传文件方法返回的信息。
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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import org.apache.http.HttpResponse;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

/** 本类的功能：处理云服务器的响应
*
*/
public class CloudResponse {
	/** 本类的功能：云接口公共响应信息
	 *
	 */
	public static class CommonResponse {
		public int statusCode = -1;		// http status
		public String statusReason = "";
		
		public String code = "";		// 错误码，见华赛接口说明书5.1
		public String message = "";		// 错误描述
	}
	/** 本类的功能：封装云putObject接口响应信息
	 *
	 */
	public static class PutObjectResponse extends CommonResponse {	
		public long x_cdmi_object_size;
		public String x_cdmi_create_time;	
		public String x_cdmi_file_id = "";		// file id
	}
	
	/** 解析putObject接口返回的信息
	 * @param httpResponse 接口返回的HTTP信息
	 * @return 处理后的信息
	 */
	public static PutObjectResponse parsePutObjectResponse(HttpResponse httpResponse) {
		if(httpResponse == null) {
			return null;
		}
		PutObjectResponse response = new PutObjectResponse();
		response.statusCode = httpResponse.getStatusLine().getStatusCode();
		response.statusReason = httpResponse.getStatusLine().getReasonPhrase();
		response.x_cdmi_object_size = Long.parseLong(httpResponse.getFirstHeader("x-cdmi-object-size").getValue());
		response.x_cdmi_create_time = httpResponse.getFirstHeader("x-cdmi-create-time").getValue();
		response.x_cdmi_file_id = httpResponse.getFirstHeader("X-cdmi-file-id").getValue();
		
		System.out.println(response.statusCode);

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
			StringBuilder xml = new StringBuilder();
			String temp;
			while ((temp = br.readLine()) != null) {
				xml.append(temp);
			}
			System.out.println(xml);
			
			if(xml == null || xml.toString().trim().equals("")) {
				response.code = "";
				response.message = "";
			} else {
				SAXBuilder sb = new SAXBuilder();
				Document doc = sb.build(new InputSource(new StringReader(xml.toString())));
				Element root = doc.getRootElement();
				if(root != null) {
					Element child = root.getChild("Code");
					if(child != null) {
						response.code = child.getValue();
					}
					child = root.getChild("Message");
					if(child != null) {
						response.message = child.getValue();
					}
				}
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
		
		return response;
	}
	
	/** 本类的功能：封装云复制对象接口响应信息
	 *
	 */
	public static class CopyObjectResponse extends CommonResponse {
		public String x_cdmi_create_time;
		public long x_cdmi_object_size;
	}
	
	/** 解析copyObject接口返回的信息
	 * @param httpResponse
	 * @return
	 */
	public static CopyObjectResponse parseCopyObjectResponse(HttpResponse httpResponse) {
		if(httpResponse == null) {
			return null;
		}
		CopyObjectResponse response = new CopyObjectResponse();
		response.statusCode = httpResponse.getStatusLine().getStatusCode();
		response.statusReason = httpResponse.getStatusLine().getReasonPhrase();
		response.x_cdmi_create_time = httpResponse.getFirstHeader("x-cdmi-create-time").getValue();
		response.x_cdmi_object_size = Long.parseLong(httpResponse.getFirstHeader("x-cdmi-object-size").getValue());
		
		System.out.println(response.statusCode);

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
			StringBuilder xml = new StringBuilder();
			String temp;
			while ((temp = br.readLine()) != null) {
				xml.append(temp);
			}
			System.out.println(xml);
			
			if(xml == null || xml.toString().trim().equals("")) {
				response.code = "";
				response.message = "";
			} else {
				SAXBuilder sb = new SAXBuilder();
				Document doc = sb.build(new InputSource(new StringReader(xml.toString())));
				Element root = doc.getRootElement();
				if(root != null) {
					Element child = root.getChild("Code");
					if(child != null) {
						response.code = child.getValue();
					}
					child = root.getChild("Message");
					if(child != null) {
						response.message = child.getValue();
					}
				}
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
		
		return response;
	}
}
