//////////////////////////////////////////////////////////////////////////  
///        COPYRIGHT NOTICE  
///        Copyright (c) 2011, 上海电信 
///        All rights reserved.  
///  
/// @file CloudErrorCode.java  
/// @brief 本文件为云服务器的错误代码信息。
///  
///     云服务器返回的错误代码信息。
///  
/// @version 1.0
/// @author 易源
/// @date 2011年10月
///  
///  
///        修订说明：最初版本  
//////////////////////////////////////////////////////////////////////////

package cloud;

/** 本类的功能：云错误码
*
* 云服务器返回的错误码
*/
public class CloudErrorCode {
	// No http response from the cloud
	public static final String NO_RESPONSE = "No response";
	// 400 Bad Request
	public static final String INVALID_PARAMETER = "InvalidParameter";
	public static final String INVALID_CONTAINER_NAME = "InvalidContainerName";
	public static final String ERROR_PERMISSION_TYPE = "ErrorPermissionType";
	public static final String NO_SUCH_TARGET_ACCESS_KEY = "NoSuchTargetAccessKey";
	public static final String INVALID_METADATA = "InvalidMetadata";
	public static final String INVALID_OBJECT_URI = "InvalidObjectURI";
	public static final String INVALID_OFFSET = "InvalidOffset";
	public static final String OBJECT_TOO_LARGE = "ObjectTooLarge";
	public static final String INCOMPLETE_BODY = "IncompleteBody";
	public static final String MISSING_MANDATORY_PARAMETER = "MissingMandatoryParameter";
	public static final String INVALID_ACL_BODY = "InvalidACLBody";
	public static final String NO_SOURCE_OBJECT = "NoSourceObject";
	public static final String INVALID_REQUEST = "InvalidRequest";
	// 401 Unauthorized
	public static final String NO_SUCH_ACCESS_KEY = "NoSuchAccessKey";
	public static final String ERROR_SIGN = "ErrorSign";
	public static final String NO_PERMISSIONS = "NoPermissions";
	// 403 Forbidden
	public static final String EXPIRED_URL = "ExpiredURL";
	// 404 Not Found
	public static final String NO_SUCH_OBJECT = "NoSuchObject";
	public static final String INVALID_URL = "InvalidURL";
	public static final String NO_SUCH_CONTAINER = "NoSuchContainer";
	// 409 Conflict
	public static final String OBJECT_ALREADY_EXISTS = "ObjectAlreadyExists";
	public static final String CONTAINER_ALREADY_EXISTS = "ContainerAlreadyExists";
	// 412 Precondition	Failed
	public static final String INVALID_OBJECT_TYPE = "InvalidObjectType";
	// 500 Internal Server Error
	public static final String INTERNAL_ERROR = "InternalError";
	// 503
	public static final String SERVICE_UNAVAILABLE = "ServiceUnavailable";
}
