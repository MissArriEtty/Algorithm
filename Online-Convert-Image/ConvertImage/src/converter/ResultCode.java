//////////////////////////////////////////////////////////////////////////  
///        COPYRIGHT NOTICE  
///        Copyright (c) 2011, 上海电信 
///        All rights reserved.  
///  
/// @file ResultCode.java  
/// @brief 本文件包含返回给数据库的操作结果码，用于表明某转码任务的具体状态。
/// 
///     转码任务的具体状态主要是一些错误状态，将这些数字写入数据库相应的表中。
///  
/// @version 1.0
/// @author 易源
/// @date 2011年10月
///  
///  
///        修订说明：最初版本  
//////////////////////////////////////////////////////////////////////////

package converter;

import cloud.CloudErrorCode;

/*
 * This class is used to output the result code to the Interfacer. (Stored in the database)
 * The result code represents errors in SAP or in the cloud, or in some other places.
 */
/** 本类的功能：返回给接口和数据库的操作结果码
*
* 主要包括SAP和云服务器的操作结果码，以及其他内部错误码
*/
public class ResultCode {
	// Conversion errors
	public static final int CVT_FAILURE = 101;
	public static final int CVT_NO_PROGRESS = 102;
	public static final int CVT_UNKNOWN_ERROR = 999;
	
	// Result code for errors caused by SAP
	private static final int SAP_BASE = 100000;
	/*
	public static final int SAP_FAIL = 100000;
	public static final int SAP_SUCCESS = 100001;
	public static final int SAP_EMPTY_ARGUMENT = 101000;
	public static final int SAP_SERVER_INTERNAL_ERROR = 101001;
	public static final int SAP_OBJECT_NOT_EXIST = 110011;
	public static final int SAP_FIELD_VERIFICATION_FAILED = 110012;
	public static final int SAP_REQUEST_FORMAT_ERROR = 110013;
	public static final int SAP_UNAUTHORIZED = 110014;
	public static final int SAP_CLOUD_EXCEPTION = 110015;
	public static final int SAP_ILLEGAL_ARGUMENT_LENGTH = 110016;
	public static final int SAP_ILLEGAL_OPERATION = 110017;
	public static final int SAP_USER_INFO_NOT_EXISTS = 110018;
	public static final int SAP_ILLEGAL_MAIL_ADDRESS_FORMAT = 110019;
	public static final int SAP_ILLEGAL_CELLPHONE_NUM_FORMAT = 110020;
	public static final int SAP_EMPTY_CELLPHONE_NUM = 110021;
	public static final int SAP_PACKAGE_ID_NOT_EXISTS = 120010;
	public static final int SAP_USER_EXISTS = 120011;
	public static final int SAP_PACKAGE_ID_EXISTS = 120012;
	public static final int SAP_CELLPHONE_NUM_VERIFICATION_FAILED = 120013;
	public static final int SAP_EMPTY_FILE_TYPE = 130010;
	public static final int SAP_EMPTY_FILE_NAME = 130011;
	public static final int SAP_EMPTY_FILE_ID = 130012;
	public static final int SAP_NEGATIVE_FILE_SIZE = 130013;
	public static final int SAP_PARENT_FOLDER_NOT_EXISTS = 130014;
	public static final int SAP_TOO_MUCH_FILES = 130015;
	public static final int SAP_EMPTY_PARENT_FOLDER_ID = 130016;
	public static final int SAP_FILE_EXISTS = 130017;
	public static final int SAP_USER_SPACE_FULL = 130018;
	public static final int SAP_ILLEGAL_USER_FILE_NAME = 130019;
	public static final int SAP_EMPTY_UPLOAD_METHOD = 130020;
	public static final int SAP_ILLEGAL_UPLOAD_METHOD = 130021;
	public static final int SAP_EMPTY_UPLOAD_STATUS = 130022;
	public static final int SAP_ILLEGAL_UPLOAD_STATUS = 130023;
	public static final int SAP_EMPTY_FILE_METADATA = 130024;
	public static final int SAP_EMPTY_FOLDER_TYPE = 140010;
	public static final int SAP_EMPTY_FOLDER_NAME = 140011;
	public static final int SAP_PARENT_FOLDER_NOT_EXISTS_2 = 140012;
	public static final int SAP_TOO_MUCH_FOLDERS = 140013;
	public static final int SAP_EMPTY_PARENT_FOLDER_ID_2 = 140014;
	public static final int SAP_FOLDER_EXISTS = 140015;
	public static final int SAP_EMPTY_FOLDER_ID = 140016;
	public static final int SAP_ILLEGAL_FOLDER_NAME = 140017;
	*/
	public static final int SAP_NO_RESPONSE = 900001;
	public static final int SAP_WRONG_NUM_OF_FILES = 900002;
	public static final int SAP_NULL_RESULT_CODE = 900003;
	public static final int SAP_UNKNOWN_CODE = 900004;
	
	// Result code for errors caused by the cloud
	public static final int CLOUD_INVALID_PARAMETER = 240001;
	public static final int CLOUD_INVALID_CONTAINER_NAME = 240002;
	public static final int CLOUD_ERROR_PERMISSION_TYPE = 240003;
	public static final int CLOUD_NO_SUCH_TARGET_ACCESS_KEY = 240004;
	public static final int CLOUD_INVALID_METADATA = 240005;
	public static final int CLOUD_INVALID_OBJECT_URI = 240006;
	public static final int CLOUD_INVALID_OFFSET = 240007;
	public static final int CLOUD_OBJECT_TOO_LARGE = 240008;
	public static final int CLOUD_INCOMPLETE_BODY = 240009;
	public static final int CLOUD_MISSING_MANDATORY_PARAMETER = 240010;
	public static final int CLOUD_INVALID_ACL_BODY = 240011;
	public static final int CLOUD_NO_SOURCE_OBJECT = 240012;
	public static final int CLOUD_INVALID_REQUEST = 240013;
	public static final int CLOUD_NO_SUCH_ACCESS_KEY = 240101;
	public static final int CLOUD_ERROR_SIGN = 240102;
	public static final int CLOUD_NO_PERMISSIONS = 240103;
	public static final int CLOUD_EXPIRED_URL = 240301;
	public static final int CLOUD_NO_SUCH_OBJECT = 240401;
	public static final int CLOUD_INVALID_URL = 240402;
	public static final int CLOUD_NO_SUCH_CONTAINER = 240403;
	public static final int CLOUD_OBJECT_ALREADY_EXISTS = 240901;
	public static final int CLOUD_CONTAINER_ALREADY_EXISTS = 240902;
	public static final int CLOUD_INVALID_OBJECT_TYPE = 241201;
	public static final int CLOUD_INTERNAL_ERROR = 250001;
	public static final int CLOUD_SERVICE_UNAVAILABLE = 250301;
	
	public static final int CLOUD_NO_RESPONSE = 800001;
	public static final int CLOUD_UNKNOWN_CODE = 800002;
	
	// Result code for java exceptions
	public static final int INTERNAL_EXCEPTION = 300000;
	
	// Result code for failed jobs
	public static final int JOB_MISSING_TOO_MANY_TIMES = 400000;
	public static final int JOB_INVALID_DONKEY_COM = 400001;
	public static final int JOB_ALREADY_IN_DOWNLOADING = 400002;
	public static final int JOB_NO_PROGRESS = 400003;
	public static final int JOB_UNKNOWN_CODE = 400004;
	
	// Result code for the local file
	public static final int WRONG_FILE_SIZE = 500000;
	
	/** 获取SAP错误码
	 * @param sapCode SAP返回码
	 * @param desc 描述
	 * @return SAP错误码
	 */
	public static int getSapResultCode(String sapCode, String desc) {
		int code = SAP_UNKNOWN_CODE;
		if(desc.equals("No response")) {
			code = SAP_NO_RESPONSE;
		} else if(desc.equals("Wrong number of files")) {
			code = SAP_WRONG_NUM_OF_FILES;
		} else if(desc.equals("Null resultCode field")) {
			code = SAP_NULL_RESULT_CODE;
		} else if(sapCode != null) {
			try {
				code = Integer.parseInt(sapCode) + SAP_BASE;
			} catch (NumberFormatException e) {
				//e.printStackTrace();
			}
		}
		return code;
	}
	
	/** 获取云错误码
	 * @param cloudErrorCode 云服务器返回的错误码
	 * @return 云错误码
	 */
	public static int getCloudResultCode(String cloudErrorCode) {
		int code = CLOUD_UNKNOWN_CODE;
		if(cloudErrorCode.equals(CloudErrorCode.INVALID_PARAMETER)) {
			code = CLOUD_INVALID_PARAMETER;
		} else if(cloudErrorCode.equals(CloudErrorCode.INVALID_CONTAINER_NAME)) {
			code = CLOUD_INVALID_CONTAINER_NAME;
		} else if(cloudErrorCode.equals(CloudErrorCode.ERROR_PERMISSION_TYPE)) {
			code = CLOUD_ERROR_PERMISSION_TYPE;
		} else if(cloudErrorCode.equals(CloudErrorCode.NO_SUCH_TARGET_ACCESS_KEY)) {
			code = CLOUD_NO_SUCH_TARGET_ACCESS_KEY;
		} else if(cloudErrorCode.equals(CloudErrorCode.INVALID_METADATA)) {
			code = CLOUD_INVALID_METADATA;
		} else if(cloudErrorCode.equals(CloudErrorCode.INVALID_OBJECT_URI)) {
			code = CLOUD_INVALID_OBJECT_URI;
		} else if(cloudErrorCode.equals(CloudErrorCode.INVALID_OFFSET)) {
			code = CLOUD_INVALID_OFFSET;
		} else if(cloudErrorCode.equals(CloudErrorCode.OBJECT_TOO_LARGE)) {
			code = CLOUD_OBJECT_TOO_LARGE;
		} else if(cloudErrorCode.equals(CloudErrorCode.INCOMPLETE_BODY)) {
			code = CLOUD_INCOMPLETE_BODY;
		} else if(cloudErrorCode.equals(CloudErrorCode.MISSING_MANDATORY_PARAMETER)) {
			code = CLOUD_MISSING_MANDATORY_PARAMETER;
		} else if(cloudErrorCode.equals(CloudErrorCode.INVALID_ACL_BODY)) {
			code = CLOUD_INVALID_ACL_BODY;
		} else if(cloudErrorCode.equals(CloudErrorCode.NO_SOURCE_OBJECT)) {
			code = CLOUD_NO_SOURCE_OBJECT;
		} else if(cloudErrorCode.equals(CloudErrorCode.INVALID_REQUEST)) {
			code = CLOUD_INVALID_REQUEST;
		} else if(cloudErrorCode.equals(CloudErrorCode.NO_SUCH_ACCESS_KEY)) {
			code = CLOUD_NO_SUCH_ACCESS_KEY;
		} else if(cloudErrorCode.equals(CloudErrorCode.ERROR_SIGN)) {
			code = CLOUD_ERROR_SIGN;
		} else if(cloudErrorCode.equals(CloudErrorCode.NO_PERMISSIONS)) {
			code = CLOUD_NO_PERMISSIONS;
		} else if(cloudErrorCode.equals(CloudErrorCode.EXPIRED_URL)) {
			code = CLOUD_EXPIRED_URL;
		} else if(cloudErrorCode.equals(CloudErrorCode.NO_SUCH_OBJECT)) {
			code = CLOUD_NO_SUCH_OBJECT;
		} else if(cloudErrorCode.equals(CloudErrorCode.INVALID_URL)) {
			code = CLOUD_INVALID_URL;
		} else if(cloudErrorCode.equals(CloudErrorCode.NO_SUCH_CONTAINER)) {
			code = CLOUD_NO_SUCH_CONTAINER;
		} else if(cloudErrorCode.equals(CloudErrorCode.OBJECT_ALREADY_EXISTS)) {
			code = CLOUD_OBJECT_ALREADY_EXISTS;
		} else if(cloudErrorCode.equals(CloudErrorCode.CONTAINER_ALREADY_EXISTS)) {
			code = CLOUD_CONTAINER_ALREADY_EXISTS;
		} else if(cloudErrorCode.equals(CloudErrorCode.INVALID_OBJECT_TYPE)) {
			code = CLOUD_INVALID_OBJECT_TYPE;
		} else if(cloudErrorCode.equals(CloudErrorCode.INTERNAL_ERROR)) {
			code = CLOUD_INTERNAL_ERROR;
		} else if(cloudErrorCode.equals(CloudErrorCode.SERVICE_UNAVAILABLE)) {
			code = CLOUD_SERVICE_UNAVAILABLE;
		} else if(cloudErrorCode.equals(CloudErrorCode.NO_RESPONSE)) {
			code = CLOUD_NO_RESPONSE;
		}
		return code;
	}
}
