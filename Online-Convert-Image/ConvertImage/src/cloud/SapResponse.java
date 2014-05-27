//////////////////////////////////////////////////////////////////////////  
///        COPYRIGHT NOTICE  
///        Copyright (c) 2011, 上海电信 
///        All rights reserved.  
///  
/// @file SapResponse.java  
/// @brief 本文件为处理Sap响应信息的类。
///  
///     Sap返回XML类型的信息，需要将这些信息方便处理的格式。主要包含封装各种信
/// 息的子类和获取这些信息的方法。
///  
/// @version 1.0
/// @author 易源
/// @date 2011年10月
///  
///  
///        修订说明：最初版本  
//////////////////////////////////////////////////////////////////////////

package cloud;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

/** 本类的功能：SAP返回的处理
*
*/
public class SapResponse {
	/** 失败 */
	public final static String FAILURE = "0";
	/** 成功 */
	public final static String SUCCESS = "1";
	/** 服务器内部错误 */
	public final static String SERVER_INTERNAL_ERROR = "1001";
	/** 云异常 */
	public final static String CLOUD_EXCEPTION = "10015";
	/** 文件已存在 */
	public final static String FILE_EXISTS = "30017";
	
	/** 本类的功能：文件预上传返回信息
	 *
	 */
	public static class CreateFileResponse {
		public String resultCode;
		public String resultDesc;
		public String fileID;
		public String parentFolderID;
		public String fileName;
		public String fileType;
		public String fileDataType;
		public long fileSize;
		public long offset;
		public String realStorageID;
		public String status;
		public String canShare;
		public String createTime;
		public String uploadStatus;
		public String uploadType;
		public String AccessKey;
		public String Authorization;
		public String Date;
		public String RealStorageURL;
		public String SAPHostAddr;
	}
	
	/** 解析文件预上传返回的信息
	 * @param xml SAP返回信息
	 * @return 处理后的信息
	 */
	@SuppressWarnings({ "unchecked" })
	public static CreateFileResponse[] parseCreateFileResponse(String xml) {
		int i, j, k;
		CreateFileResponse temp = null;
		CreateFileResponse[] response = null;
		InputSource src = new InputSource(new StringReader(xml));
		SAXBuilder sb = new SAXBuilder();
		Document doc;
		List<Element> fileInfoList;	//<FileInfo />
		List<Element> fileInfoItemList;		//<resultCode />, <resultDesc /> etc.
		Element root;
		Element fileInfoItem;		//A list item in fileInfo list
		String itemName;
		String itemValue;
		List<Element> metaDataItems;		//<HeaderEntry />
		String metaDataName;
		String metaDataValue;
		
		try {
			doc = sb.build(src);
		} catch (JDOMException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		root = doc.getRootElement();
		if(root == null) {
			return null;
		}
		fileInfoList = root.getChildren();
		response = new CreateFileResponse[fileInfoList.size()];
		
		for(i = 0; i < fileInfoList.size(); i++) {
			temp = new CreateFileResponse();
			fileInfoItemList = fileInfoList.get(i).getChildren();
			for(j = 0; j < fileInfoItemList.size(); j++) {
				fileInfoItem = fileInfoItemList.get(j);
				itemName = fileInfoItem.getName();
				itemValue = fileInfoItem.getValue();
				if(itemName.equals("resultCode")) {
					temp.resultCode = itemValue;
				} else if(itemName.equals("resultDesc")) {
					temp.resultDesc = itemValue;
				} else if(itemName.equals("fileID")) {
					temp.fileID = itemValue;
				} else if(itemName.equals("parentFolderID")) {
					temp.parentFolderID = itemValue;
				} else if(itemName.equals("fileName")) {
					temp.fileName = itemValue;
				} else if(itemName.equals("fileType")) {
					temp.fileType = itemValue;
				} else if(itemName.equals("fileDataType")) {
					temp.fileDataType = itemValue;
				} else if(itemName.equals("fileSize")) {
					temp.fileSize = Long.parseLong(itemValue);
				} else if(itemName.equals("offset")) {
					temp.offset = Long.parseLong(itemValue);
				} else if(itemName.equals("realStorageID")) {
					temp.realStorageID = itemValue;
				} else if(itemName.equals("status")) {
					temp.status = itemValue;
				} else if(itemName.equals("canShare")) {
					temp.canShare = itemValue;
				} else if(itemName.equals("createTime")) {
					temp.createTime = itemValue;
				} else if(itemName.equals("uploadStatus")) {
					temp.uploadStatus = itemValue;
				} else if(itemName.equals("uploadType")) {
					temp.uploadType = itemValue;
				} else if(itemName.equals("metaData")) {
					metaDataItems = fileInfoItem.getChildren();
					for(k = 0; k < metaDataItems.size(); k++) {
						metaDataName = metaDataItems.get(k).getChildText("name");
						metaDataValue = metaDataItems.get(k).getChildText("value");
						if(metaDataName.equals("AccessKey")) {
							temp.AccessKey = metaDataValue;
						} else if(metaDataName.equals("Authorization")) {
							temp.Authorization = metaDataValue;
						} else if(metaDataName.equals("Date")) {
							temp.Date = metaDataValue;
						} else if(metaDataName.equals("RealStorageURL")) {
							temp.RealStorageURL = metaDataValue;
						} else if(metaDataName.equals("SAPHostAddr")) {
							temp.SAPHostAddr = metaDataValue;
						}
					}
				}
			}
			response[i] = temp;
		}
	return response;
	}
	
	/** 本类的功能：修改文件上传状态返回信息
	 *
	 */
	public static class UploadedFileResponse {
		public String resultCode;
		public String resultDesc;
		public String fileID;
	}
	
	/** 解析修改文件上传状态接口返回信息
	 * @param xml SAP信息
	 * @return 处理后的信息
	 */
	@SuppressWarnings("unchecked")
	public static UploadedFileResponse[] parseUploadedFileResponse(String xml) {
		SAXBuilder sb = new SAXBuilder();
		UploadedFileResponse[] response = null;
		Document doc;

		try {
			doc = sb.build(new InputSource(new StringReader(xml)));
		} catch (JDOMException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		Element root = doc.getRootElement();
		if(root == null) {
			return null;
		}
		List<Element> fileInfos = root.getChildren();
		response = new UploadedFileResponse[fileInfos.size()];
		String itemName;
		String itemValue;
		for(int i = 0; i < fileInfos.size(); i++) {
			UploadedFileResponse temp = new UploadedFileResponse();
			List<Element> fileInfoItems = fileInfos.get(i).getChildren();
			for(int j = 0; j < fileInfoItems.size(); j++) {
				itemName = fileInfoItems.get(i).getName();
				itemValue = fileInfoItems.get(i).getValue();
				if(itemName.equals("resultCode")) {
					temp.resultCode = itemValue;
				} else if(itemName.equals("resultDesc")) {
					temp.resultDesc = itemValue;
				} else if(itemName.equals("fileID")) {
					temp.fileID = itemValue;
				}
			}
			response[i] = temp;
		}
		return response;
	}
	
	/** 本类的功能：删除文件返回信息
	 *
	 */
	public static class DeleteFileResponse {
		public String resultCode;
		public String resultDesc;
		public String fileID;
	}
	
	/** 解析删除文件返回信息
	 * @param xml SAP返回信息
	 * @return 处理后的信息
	 */
	public static DeleteFileResponse parseDeleteFileResponse(String xml) {
		SAXBuilder sb = new SAXBuilder();
		DeleteFileResponse response;
		Document doc;
		try {
			doc = sb.build(new InputSource(new StringReader(xml)));
		} catch (JDOMException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		Element elem = doc.getRootElement();
		if(elem == null) {
			return null;
		}
		elem = elem.getChild("FileInfo");
		if(elem == null) {
			return null;
		}
		
		response = new DeleteFileResponse();
		Element child = elem.getChild("resultCode");
		if(child != null) {
			response.resultCode = child.getValue();
		}
		child = elem.getChild("resultDesc");
		if(child != null) {
			response.resultDesc = child.getValue();
		}
		child = elem.getChild("fileID");
		if(child != null) {
			response.fileID = child.getValue();
		}
		
		return response;
	}
	
	/** 本类的功能：创建文件夹返回信息
	 *
	 */
	public static class CreateFolderResponse {
		public String resultCode;
		public String resultDesc;
		public String folderID;
		public String folderName;
		public String status;
		public String createTime;
		public String canShare;
		public String folderType;
		public String parentFolderID;
	}
	
	/** 解析创建文件夹返回信息
	 * @param xml SAP返回信息
	 * @return 处理后的信息
	 */
	@SuppressWarnings("unchecked")
	public static CreateFolderResponse parseCreateFolderResponse(String xml) {
		SAXBuilder sb = new SAXBuilder();
		CreateFolderResponse response = new CreateFolderResponse();
		Document doc;
		try {
			doc = sb.build(new InputSource(new StringReader(xml)));
		} catch (JDOMException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		Element root = doc.getRootElement();
		if(root == null) {
			return null;
		}
		Element child = root.getChild("FolderInfo");
		if(child == null) {
			return null;
		}
		List<Element> folderInfoItems = child.getChildren();
			
		String itemName;
		String itemValue;
		for(int i = 0; i < folderInfoItems.size(); i++) {
			itemName = folderInfoItems.get(i).getName();
			itemValue = folderInfoItems.get(i).getValue();
			if(itemName.equals("resultCode")) {
				response.resultCode = itemValue;
			} else if(itemName.equals("resultDesc")) {
				response.resultDesc = itemValue;
			} else if(itemName.equals("folderID")) {
				response.folderID = itemValue;
			} else if(itemName.equals("folderName")) {
				response.folderName = itemValue;
			} else if(itemName.equals("status")) {
				response.status = itemValue;
			} else if(itemName.equals("createTime")) {
				response.createTime = itemValue;
			} else if(itemName.equals("canShare")) {
				response.canShare = itemValue;
			} else if(itemName.equals("folderType")) {
				response.folderType = itemValue;
			} else if(itemName.equals("parentFolderID")) {
				response.parentFolderID = itemValue;
			}
		}
		return response;
	}
	
	/** 本类的功能：获取某个文件夹的信息接口返回信息
	 *
	 */
	public static class QueryFolderInfoResponse {
		public String folderID;
		public String folderName;
		public String status;
		public String createTime;
		public String canShare;
		public String folderType;
		public String parentFolderID;
	}
	
	/** 解析获取某个文件夹的信息接口返回的信息
	 * @param xml SAP返回信息
	 * @return 处理后的信息
	 */
	@SuppressWarnings("unchecked")
	public static QueryFolderInfoResponse parseQueryFolderInfoResponse(String xml) {
		int i;
		QueryFolderInfoResponse response = null;
		InputSource src = new InputSource(new StringReader(xml));
		SAXBuilder sb = new SAXBuilder();
		Document doc;
		
		try {
			doc = sb.build(src);
		} catch (JDOMException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		Element root = doc.getRootElement();
		if(root == null) {
			return null;
		}
		Element child = root.getChild("FolderInfo");
		if(child == null) {
			return null;
		}
		List<Element> folderInfoItems = child.getChildren();
		response = new QueryFolderInfoResponse();

		String itemName;
		String itemValue;
		for(i = 0; i < folderInfoItems.size(); i++) {
			itemName = folderInfoItems.get(i).getName();
			itemValue = folderInfoItems.get(i).getValue();
			if(itemName.equals("folderID")) {
				response.folderID = itemValue;
			} else if(itemName.equals("folderName")) {
				response.folderName = itemValue;
			} else if(itemName.equals("status")) {
				response.status = itemValue;
			} else if(itemName.equals("createTime")) {
				response.createTime = itemValue;
			} else if(itemName.equals("canShare")) {
				response.canShare = itemValue;
			} else if(itemName.equals("folderType")) {
				response.folderType = itemValue;
			} else if(itemName.equals("parentFolderID")) {
				response.parentFolderID = itemValue;
			}
		}
		return response;
	}
	
	/** 本类的功能：“用户获取应用网盘中指定目录下的文件夹、文件信息”接口返回信息
	 *
	 */
	public static class AppObjectListResponse {
		public String type;
		public String folderID;
		public String folderName;
		public String status;
		public String createTime;
		public String canShare;
		public String folderType;
		public String parentFolderID;
		public String fileID;
		public String fileName;
		public String fileType;
		public long fileSize;
		public String realStorageID;
		public String realStorageURL;
		public String uploadStatus;
		public String uploadType;
	}
	
	/** 解析”用户获取应用网盘中指定目录下的文件夹、文件信息“返回信息
	 * @param xml SAP返回
	 * @return 处理后的信息
	 */
	@SuppressWarnings("unchecked")
	public static AppObjectListResponse[] parseAppObjectListResponse(String xml) {
		int i, j, k, count;
		AppObjectListResponse temp = null;
		AppObjectListResponse[] response = null;
		InputSource src = new InputSource(new StringReader(xml));
		SAXBuilder sb = new SAXBuilder();
		Document doc;
		int total = 0;
		List<Element> thirdLevel;
		List<Element> fileInfos;
		List<Element> fileInfoItems;
		List<Element> folderInfos;
		List<Element> folderInfoItems;
		String itemName;
		String itemValue;
		
		try {
			doc = sb.build(src);
		} catch (JDOMException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		Element root = doc.getRootElement();
		if(root == null) {
			return null;
		}
		Element child = root.getChild("FilesysObject");
		if(child == null) {
			return null;
		}
		thirdLevel = child.getChildren();
		count = 0;
		for(i = 0; i < thirdLevel.size(); i++) {
			if(thirdLevel.get(i).getName().equals("Total")) {
				total = Integer.parseInt(thirdLevel.get(i).getValue());
				response = new AppObjectListResponse[total];
				break;
			}
		}
		if(total == 0) {
			return null;
		}
		for(i = 0; i < thirdLevel.size(); i++) {
			if(thirdLevel.get(i).getName().equals("Folders")) {
				folderInfos = thirdLevel.get(i).getChildren();
				for(j = 0; j < folderInfos.size(); j++) {
					temp = new AppObjectListResponse();
					temp.type = "folder";
					folderInfoItems = folderInfos.get(j).getChildren();
					for(k = 0; k < folderInfoItems.size(); k++) {
						itemName = folderInfoItems.get(k).getName();
						itemValue = folderInfoItems.get(k).getValue();
						if(itemName.equals("folderID")) {
							temp.folderID = itemValue;
						} else if(itemName.equals("folderName")) {
							temp.folderName = itemValue;
						} else if(itemName.equals("status")) {
							temp.status = itemValue;
						} else if(itemName.equals("createTime")) {
							temp.createTime = itemValue;
						} else if(itemName.equals("canShare")) {
							temp.canShare = itemValue;
						} else if(itemName.equals("folderType")) {
							temp.folderType = itemValue;
						} else if(itemName.equals("parentFolderID")) {
							temp.fileID = itemValue;
						} else if(itemName.equals("fileName")) {
							temp.fileName = itemValue;
						} else if(itemName.equals("fileType")) {
							temp.fileType = itemValue;
						} else if(itemName.equals("fileSize")) {
							temp.fileSize = Long.parseLong(itemValue);
						} else if(itemName.equals("realStorageID")) {
							temp.realStorageID = itemValue;
						} else if(itemName.equals("realStorageURL")) {
							temp.realStorageURL = itemValue;
						} else if(itemName.equals("uploadStatus")) {
							temp.uploadStatus = itemValue;
						} else if(itemName.equals("uploadType")) {
							temp.uploadType = itemValue;
						}
					}
					response[count++] = temp;
				}
			} else if(thirdLevel.get(i).getName().equals("Files")) {
				fileInfos = thirdLevel.get(i).getChildren();
				for(j = 0; j < fileInfos.size(); j++) {
					temp = new AppObjectListResponse();
					temp.type = "file";
					fileInfoItems = fileInfos.get(j).getChildren();
					for(k = 0; k < fileInfoItems.size(); k++) {
						itemName = fileInfoItems.get(k).getName();
						itemValue = fileInfoItems.get(k).getValue();
						if(itemName.equals("folderID")) {
							temp.folderID = itemValue;
						} else if(itemName.equals("folderName")) {
							temp.folderName = itemValue;
						} else if(itemName.equals("status")) {
							temp.status = itemValue;
						} else if(itemName.equals("createTime")) {
							temp.createTime = itemValue;
						} else if(itemName.equals("canShare")) {
							temp.canShare = itemValue;
						} else if(itemName.equals("folderType")) {
							temp.folderType = itemValue;
						} else if(itemName.equals("parentFolderID")) {
							temp.fileID = itemValue;
						} else if(itemName.equals("fileName")) {
							temp.fileName = itemValue;
						} else if(itemName.equals("fileType")) {
							temp.fileType = itemValue;
						} else if(itemName.equals("fileSize")) {
							temp.fileSize = Long.parseLong(itemValue);
						} else if(itemName.equals("realStorageID")) {
							temp.realStorageID = itemValue;
						} else if(itemName.equals("realStorageURL")) {
							temp.realStorageURL = itemValue;
						} else if(itemName.equals("uploadStatus")) {
							temp.uploadStatus = itemValue;
						} else if(itemName.equals("uploadType")) {
							temp.uploadType = itemValue;
						}
					}
					response[count++] = temp;
				}
			}
		}
		return response;
	}
	
	/** 本类的功能：”应用复制文件到用户应用目录“返回信息
	 *
	 */
	public static class CopyFileResponse {
		public String resultCode;
		public String resultDesc;
	}
	
	/** 解析”应用复制文件到用户应用目录“返回信息
	 * @param xml SAP返回
	 * @return 处理后的信息
	 */
	public static CopyFileResponse parseCopyFileResponse(String xml) {
		CopyFileResponse response = null;
		InputSource src = new InputSource(new StringReader(xml));
		SAXBuilder sb = new SAXBuilder();
		Document doc;
		
		try {
			doc = sb.build(src);
		} catch (JDOMException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		Element elem = doc.getRootElement();
		if(elem == null) {
			return null;
		}
		elem = elem.getChild("FileInfo");
		if(elem == null) {
			return null;
		}
		
		response = new CopyFileResponse();
		Element child = elem.getChild("resultCode");
		if(child != null) {
			response.resultCode = child.getValue();
		}
		child = elem.getChild("resultDesc");
		if(child != null) {
			response.resultDesc = child.getValue();
		}
		
		return response;
	}
	
	/** 本类的功能：”应用预复制文件到用户应用目录“返回信息
	 *
	 */
	public static class PreCopyResponse {
		public String resultCode;
		public String resultDesc;
		public String fileID;
		public String fileName;
		public String parentFolderID;
		public String fileType;
		public long fileSize;
		public String realStorageID;
		public String realStorageURL;
		public String status;
		public String canShare;
		public String createTime;
		public String uploadStatus;
		public String uploadType;
		public String uploadTime;
		public String path;
		public String AccessKey;
		public String Authorization;
		public String Date;
		public String RealStorageURL;
		public String x_cdmi_copy_source;
		public String x_cdmi_content_md5;
		public String x_cdmi_prefix;
	}
	
	/** 解析”应用预复制文件到用户应用目录“返回信息
	 * @param xml SAP返回
	 * @return 处理后的信息
	 */
	@SuppressWarnings("unchecked")
	public static PreCopyResponse parsePreCopyResponse(String xml) {
		PreCopyResponse response = null;
		InputSource src = new InputSource(new StringReader(xml));
		SAXBuilder sb = new SAXBuilder();
		Document doc;
		
		try {
			doc = sb.build(src);
		} catch (JDOMException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		Element elem = doc.getRootElement();
		if(elem == null) {
			return null;
		}
		elem = elem.getChild("FileInfo");
		if(elem == null) {
			return null;
		}
		
		response = new PreCopyResponse();
		List<Element> items = elem.getChildren();
		String itemName;
		String itemValue;
		List<Element> metaDataItems;
		String metaDataName;
		String metaDataValue;
		int i, k;
		for(i = 0; i < items.size(); i++) {
			itemName = items.get(i).getName();
			itemValue = items.get(i).getValue();
			if(itemName.equals("resultCode")) {
				response.resultCode = itemValue;
			} else if(itemName.equals("resultDesc")) {
				response.resultDesc = itemValue;
			} else if(itemName.equals("fileID")) {
				response.fileID = itemValue;
			} else if(itemName.equals("fileName")) {
				response.fileName = itemValue;
			} else if(itemName.equals("parentFolderID")) {
				response.parentFolderID = itemValue;
			} else if(itemName.equals("fileType")) {
				response.fileType = itemValue;
			} else if(itemName.equals("fileSize")) {
				response.fileSize = Long.parseLong(itemValue);
			} else if(itemName.equals("realStorageID")) {
				response.realStorageID = itemValue;
			} else if(itemName.equals("realStorageURL")) {
				response.realStorageURL = itemValue;
			} else if(itemName.equals("status")) {
				response.status = itemValue;
			} else if(itemName.equals("canShare")) {
				response.canShare = itemValue;
			} else if(itemName.equals("createTime")) {
				response.createTime = itemValue;
			} else if(itemName.equals("uploadStatus")) {
				response.uploadStatus = itemValue;
			} else if(itemName.equals("uploadType")) {
				response.uploadType = itemValue;
			} else if(itemName.equals("uploadTime")) {
				response.uploadTime = itemValue;
			} else if(itemName.equals("path")) {
				response.path = itemValue;
			} else if(itemName.equals("metaData")) {
				metaDataItems = items.get(i).getChildren();
				for(k = 0; k < metaDataItems.size(); k++) {
					metaDataName = metaDataItems.get(k).getChildText("name");
					metaDataValue = metaDataItems.get(k).getChildText("value");
					if(metaDataName.equals("AccessKey")) {
						response.AccessKey = metaDataValue;
					} else if(metaDataName.equals("Authorization")) {
						response.Authorization = metaDataValue;
					} else if(metaDataName.equals("Date")) {
						response.Date = metaDataValue;
					} else if(metaDataName.equals("RealStorageURL")) {
						response.RealStorageURL = metaDataValue;
					} else if(metaDataName.equals("x-cdmi-copy-source")) {
						response.x_cdmi_copy_source = metaDataValue;
					} else if(metaDataName.equals("x-cdmi-content-md5")) {
						response.x_cdmi_content_md5 = metaDataValue;
					} else if(metaDataName.equals("x-cdmi-prefix")) {
						response.x_cdmi_prefix = metaDataValue;
					}
				}
			}
		}
		
		return response;
	}
}
