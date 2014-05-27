package downFile;

import config.configuration;

/**
 *@author Gene
 *@date 2011-8-10
 *@desc
 */
public class REST {
	
	private static configuration conf = new configuration("download-conf.xml");
	/**
	 * 获取用户列表地址
	 */
	public static final String SAP_USER_TREE = conf.getConf("SAP_USER_TREE");//"http://180.153.147.137/SAP-V2/folderTree/query/root";
	
	/**
	 * UserID转换为token地址
	 */
	
	public static final String AAA_GET_USERTOKEN = conf.getConf("AAA_GET_USERTOKEN");//"http://10.200.11.240/authenticate2/rest/tokenFromKey";
//	public static final String AAA_GET_USERTOKEN = "http://180.153.147.130/authenticate2/rest/tokenFromKey";

	/**
	 * SAP contentType
	 */
	public static final String SAP_CONTENT_TYPE = conf.getConf("SAP_CONTENT_TYPE");//"Application/xml";
	/**
	 * sap预下载
	 */
	public static final String SAP_PRE_DOWNLOAD = conf.getConf("SAP_PRE_DOWNLOAD");//"http://10.200.11.244/SAP-V2/file/down/{fileID}";
//	public static final String SAP_PRE_DOWNLOAD="http://180.153.147.137/SAP-V2/file/down/{fileID}";
	/**
	 * 下载地址
	 */
	public final static String DOWNLOAD_PATH = conf.getConf("downloadPath");
}
