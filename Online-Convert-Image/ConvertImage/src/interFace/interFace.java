
/***********************************
 * 
 *    File Notice
 *    
 *    @file    Interface.java
 *    @brief   接口功能实现
 *    @author  cuixin
 *    @date    2013年7月
 * 
 * 
 ***********************************/


package interFace;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.Security;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import logPackage.cloudLog;
import result.addResult;
import result.convertedResult;
import result.convertedTaskInfo;
import result.convertingResult;
import result.convertingTaskInfo;
import result.deleteResult;
import result.detailResult;
import result.detailResultInfo;
import config.configuration;


@Path("/Image")
public class interFace{
	

	private final static configuration conf = new configuration("convert-interfacer-conf.xml");
	
	//3DES 密钥
	private final static String key = conf.getConf("decryptionKey"); //"nJ54PFb6a7F1ls94bDwJIl96"
	private final static String vector = conf.getConf("decryptionVector"); //"12345678"
	
	//DB 连接信息
	public final static  String dbAddress = conf.getConf("mysqlAddr");//"jdbc:mysql://10.200.11.237:3306/mydb";
	public final static  String userName = conf.getConf("mysqlUserName");//"come";
	public final static  String passwd = conf.getConf("mysqlPasswd");//"123456";
	public final static  String driver = conf.getConf("mysqlDriver");//"com.mysql.jdbc.Driver";
	private final static int maxRequest = Integer.parseInt(conf.getConf("maxWaitingRequest"));//1000000;

	
	//DB 表名
	public final static String downloaderInfoTableName = "downloaderInfo";
	public final static String requestTableName = "requestTable";
	
	//DB 操作接口
	private static dbio.dbUpdate dbUpdate = new dbio.dbUpdate(dbAddress, userName, passwd);
	private static dbio.dbQuery dbQuery = new dbio.dbQuery(dbAddress, userName, passwd);
	
	//DB 数据库操作
	private static Connection queryConn, updateConn;
	private Statement qstmt, ustmt;
	private String sql;
	private ResultSet rs;
	public static logPackage.cloudLog log = new cloudLog("converter.log");

	
	/**
	 * 构造函数 连接数据库
	 */
	public interFace(){
		
		try{
			
			if(queryConn == null || queryConn.isClosed())
				queryConn = dbQuery.connect();
			
		}catch(SQLException e){
			System.out.println(e.getMessage());
			log.writeLog("[inferface]Error: " + e.getMessage());
			
		}
		
		try{
			
			if(updateConn == null || updateConn.isClosed())
				updateConn = dbUpdate.connect();
		}catch(SQLException e){
			System.out.println(e.getMessage());
			log.writeLog("[interface]Error: " + e.getMessage());
		}
	}
	
	/**
	 * 添加任务 首先解析前端参数信息
	 * @param fileId
	 * @param apptoken
	 * @param sapaddr
	 * @param appinfo
	 * @param taskname 
	 * @return
	 */
	@POST
	@Path("/addTask")
	@Produces("application/xml")
	@Consumes("application/xml")
	public addResult addTask(@HeaderParam("fileId") String fileId,
			@HeaderParam("apptoken") String apptoken,
			@HeaderParam("sapaddr") String sapaddr,
			@HeaderParam("appinfo") String appinfo,
			@HeaderParam("taskname") String taskname){
		
		
		//decode taskname
		String taskName = "";
		try{
			
			taskName = new String(URLDecoder.decode(taskname,"utf8").getBytes(),"utf8");
//			log.writeLog("receive addTask request: (taskname)" + taskName);
		}catch(UnsupportedEncodingException e){
			log.writeLog("[Converter](taskname)" + taskName + ": ERROR 1001 url encoding error " + e.getMessage());
			return new addResult(0, "1001");
		}
		
		//decode appinfo 3DES解密出appInfo串 解析出信息
		Security.addProvider(new com.sun.crypto.provider.SunJCE());
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		String appTokenId, userDir = "", userId, enddate = "", format = "";
		int maxRequest = -1, maxRunningRequest = -1;
		try{
			
			String appInfo  = "", orderinfo = "";
			
			//test
			appInfo = appinfo;
//			try{
//				
//				appInfo = util.TripleDES.desDecodeOnly(appinfo, key, vector);
//			}catch(java.lang.ArrayIndexOutOfBoundsException e){
//				
//				log.writeLog("[Converter](taskname)" + taskName + ": ERROR 1002 decode appinfo error " + e.getMessage());
//				return new addResult(0, "1002");
//			}
			
			
			//解析出 appTokenId userDir userId maxRequest maxRunningRequest
			try{
				
				appTokenId = apptoken;
				String[] vals = appInfo.split(";");
				orderinfo = vals[1];
				enddate = vals[2];  //用户套餐结束日期
				userId = vals[3];
				format = vals[4];
				
				System.out.println("orderinfo : " + orderinfo);
				System.out.println("enddate : " + enddate);
				System.out.println("userId : " + userId);
				System.out.println("format : " + format);
				
			}catch(java.lang.ArrayIndexOutOfBoundsException e){
				log.writeLog("[Converter](taskname)" + taskName + ": ERROR 1003 parse appinfo error " + e.getMessage());
				return new addResult(0, "1003");
			}
			try{
				
				String[] orderInfo = orderinfo.split("\\|\\|");
				String[] item;
				for(int i = 0; i < orderInfo.length; i++){
					item = orderInfo[i].split(":");
					if (item[0].equals("maxJobNum"))    //套餐限制下载数
						maxRequest = Integer.parseInt(item[1]);
					else if (item[0].equals("maxConcurrentJobs"))    //套餐限制最大同时下载数
						maxRunningRequest = Integer.parseInt(item[1]);
					else if (item[0].equals("folderId"))
						userDir = item[1];
					else if (item[0].equals(""))
						break;
				}
			}catch(java.lang.ArrayIndexOutOfBoundsException e){
				
				log.writeLog("[Converter]taskname)" + taskName + ": ERROR 1004 parse order info error " + e.getMessage());
				return new addResult(0, "1004");
			}
			
			//if order info or userDir info error, return error
			if (maxRequest == -1 || maxRunningRequest == -1 || userDir.equals("")) {
				log.writeLog("[Converter](taskname)" + taskName + ": ERROR 1005 incomplete orderinfo error");
				return new addResult(0, "1005");
			}
		}catch(java.lang.ArrayIndexOutOfBoundsException e){
			
			log.writeLog("[Converter](taskname)" + taskName + ": ERROR 1002 decode appinfo error " + e.getMessage());
			return new addResult(0, "1002");
			
		}catch(Exception e){
			
			log.writeLog("[Converter](taskname)" + taskName + ": ERROR 1002 decode appinfo error " + e.getMessage());
			return new addResult(0, "1002");
		}
		
		try{
			//update downloaderInfo
			sql = "select * from " + downloaderInfoTableName + " where userId = '" + userId + "'";
			qstmt = dbQuery.createStatement(queryConn);
			ustmt = dbUpdate.createStatement(updateConn);
			rs = qstmt.executeQuery(sql);
			
			//如果 userId 不存在， 插入 userId; 否则 更新 maxRequest maxRunningRequest
			if(!rs.next()){
				sql = "insert into " + downloaderInfoTableName + " values('" + userId + "','"
			           + maxRequest + "','" + maxRunningRequest +"')";
				ustmt.execute(sql);
			}else{
				
				int maxRequestOfDB = rs.getInt("maxRequest");
				int maxRunningRequestOfDB = rs.getInt("maxRunningRequest");
				
				if( maxRequest != maxRequestOfDB || maxRunningRequestOfDB != maxRunningRequestOfDB){
					sql = "update " + downloaderInfoTableName + " set maxRequest = '" + maxRequest 
							+ "', maxRunningRequest = '" + maxRunningRequest +"' where userId = '" 
							+ userId + "'";
					ustmt.execute(sql);
				}
			}
			rs.close();
			
			//获取系统当前时间 时间格式 "yyyy-MM-dd HH:mm:ss" 24时制
			String startTime = "";
			Date now = new Date(System.currentTimeMillis());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			startTime = sdf.format(now);
			
			//check the total number of the user's posts
			sql = "select count(requestId) as num from " + requestTableName
					+ " where userId = '" + userId +"' and commitTime >= '" + startTime
					+ "' and status <> 'error' ";
			rs = qstmt.executeQuery(sql);
			if(rs.next() && rs.getInt("num") > maxRequest){
				rs.close();
				log.writeLog("[Converter](taskname)" + taskName + ": ERROR 2001 requests use up");
				return new addResult(0,"2001");
			}
			rs.close();
			
			//check the total waiting request 
			sql = "select count(requestId) as totalNum from " + requestTableName
					+ " where status = 'waiting' ";
			rs = qstmt.executeQuery(sql);
			if(rs.next() && rs.getInt("totalNum")>interFace.maxRequest){
				log.writeLog("[Converter](taskname)" + taskName + ": ERROR 2002 waiting request table is full");
				return new addResult(0,"2002");
			}
			rs.close();
			
			//re-encode taskname with UTF-8
		   try {
			   taskName = URLEncoder.encode(taskName, "utf8");
			   
		   } catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			   e.printStackTrace();
		   }
			
		   //insert task into requestTable
		   String requestId = userId + "_" + UUID.randomUUID();
		   if(taskName.length() > 1000)
			   return new addResult(0,"error: task name too long");
		   
		   String commitTime = "";
		   now.setTime(System.currentTimeMillis()); 
			commitTime = sdf.format(now);
		   
		   sql = "insert into " + requestTableName + " (requestId, userId, taskName, contents, "
				   + "commitTime,  appTokenId, userDirId, sapAddrs, format, status) values ('" + requestId
				   + "', '" + userId + "', '" + taskName + "', '" + fileId + "', '" + commitTime + "', '"
				   + appTokenId + "', '" + userDir + "', '" + sapaddr + "', '" + format + "', 'waiting')";
		   
		   ustmt.execute(sql);
		   log.writeLog("[Converter](taskname)" + taskName + ": succeed to add request " + requestId );
		   qstmt.close();
		   ustmt.close();
		   return new addResult(1, requestId);
			
		}catch(SQLException e){
			log.writeLog("[Converter](taskname)" + taskName + ": ERROR 1006 db operation error " + e.getMessage());
			log.writeLog(sql);
			return new addResult(0, "1006");
			
		}finally{
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(qstmt != null){
				try {
					qstmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(ustmt != null){
				try {
					ustmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}

	/**
	 * 
	 * @param appinfo
	 * @return
	 */
	@GET
	@Path("showConverting")
	@Produces("application/xml")
	public convertingResult showConverting(
			@HeaderParam("appinfo") String appinfo){
		
		//decode appinfo
		Security.addProvider(new com.sun.crypto.provider.SunJCE());
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		String appInfo = "", orderInfo = "";
		String userDir = "", userId = "";
		int maxRequest = -1, maxRunningRequest = -1;
		
		//test
		appInfo = appinfo;
//		try{
//			appInfo = util.TripleDES.desDecodeOnly(appinfo, key, vector);
//			
//		}catch(java.lang.ArrayIndexOutOfBoundsException e){
//			log.writeLog("[showConverting](appinfo)" + appinfo + ": ERROR 1002 decode appinfo error " + e.getMessage());
//			return new convertingResult();
//		}
		
		
		//parse userDir, userId, maxRequest, maxRunningRequest
		try {
			String[] vals = appInfo.split(";");
			orderInfo = vals[1];
			userId = vals[3];
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.writeLog("[showConverting](appinfo)" + appinfo + ": ERROR 1003 parse appinfo error " + e.getMessage());
			return new convertingResult();
		}
		
		try {
			String[] orderInfo_ = orderInfo.split("\\|\\|");
			String[] item;
			for(int i = 0; i < orderInfo_.length; i++){
				
				item = orderInfo_[i].split(":");
				
				if(item[0].equals("maxJobNum")){
					maxRequest = Integer.parseInt(item[1]);
				}
				else if(item[0].equals("maxConcurrentJobs")){
					maxRunningRequest = Integer.parseInt(item[1]);
				}
				else if(item[0].equals("folderId"))
					userDir = item[1];
				else if(item[0].equals(""))
					break;
			}
			
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			log.writeLog("[showConverting](appinfo)" + appinfo + ": ERROR 1004 parse order info error " + e.getMessage());
			return new convertingResult();
		}
		
		//check order info and userDir order
		if(maxRequest == -1 || maxRunningRequest == -1 || userDir.equals("")){
			log.writeLog("[showConverting](appinfo)" + appinfo + ": ERROR 1005 incomplete orderinfo error");
			return new convertingResult();
		}
		
		try{
			//update downloaderInfo
			sql = "select * from " + downloaderInfoTableName + " where userId='" + userId + "'";
			qstmt = dbQuery.createStatement(queryConn);
			ustmt = dbUpdate.createStatement(updateConn);
			rs = qstmt.executeQuery(sql);
			
			//if the user doesn't exist, create the user
			if(!rs.next()){
				sql = "insert into " + downloaderInfoTableName + " values ('" + userId + "', '"
						+ maxRequest + "', '" + maxRunningRequest + "')";
				ustmt.executeUpdate(sql);
				
			}
			//if the user exist, update the downloadinfo
			else{
				int maxRequestOfDB = rs.getInt("maxRequest");
				int maxRunningRequestOfDB = rs.getInt("maxRunningRequest");
				if(maxRequest != maxRequestOfDB || maxRunningRequest != maxRunningRequestOfDB){
					sql = "update " + downloaderInfoTableName + " set maxRequest='" + maxRequest
							+ "', maxRunningRequest='" + maxRunningRequest + "' where userId='"
							+ userId + "'";
					ustmt.executeUpdate(sql);
				}
			}
			ustmt.close();
			
			
			convertingResult cr = new convertingResult();
			
			sql = "select * from " + requestTableName + " where userId='" + userId
					+ "' and status in ('waiting','running')";
			rs = qstmt.executeQuery(sql);
			
			while(rs.next()){
				try{
					
					convertingTaskInfo cti = new convertingTaskInfo();
					
					cti.setRequestId(URLDecoder.decode(rs.getString("requestId"), "utf8"));
					cti.setTaskName(URLDecoder.decode(rs.getString("taskName"), "utf8"));
					cti.setCommitTime(URLDecoder.decode(rs.getString("commitTime"), "utf8"));
					cti.setFinishTime(URLDecoder.decode("finishTime", "utf8"));
					cti.setStatus(URLDecoder.decode(rs.getString("status"), "utf8"));
					cti.setErrorCode(rs.getInt("errorCode"));
	
					if(rs.getInt("errorCode") == 0)
						cti.setErrorMsg(URLDecoder.decode("No error occurs!", "utf8"));
					else
						cti.setErrorMsg(URLDecoder.decode(rs.getString("errorMsg"), "utf8"));
					
					cr.putTask(cti);
					
				}catch(Exception e){
					log.writeLog("[showConverting](userId)" + userId  + ": ERROR " + e.getMessage());
					continue;
				}
				
		   }
		   rs.close();
		   qstmt.close();
		   return cr;
			
		}catch(SQLException e){
			log.writeLog("[showConverting](userId)" + userId + ": ERROR 1006 db operation error " + e.getMessage());
			return new convertingResult();
		}finally{
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(qstmt != null){
				try {
					qstmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(ustmt != null){
				try {
					ustmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		
	}

	/**
	 * 
	 * @param appinfo
	 * @return
	 */
	@GET
	@Path("showConverted")
	@Produces("application/xml")
	public convertedResult showConverted(
			@HeaderParam("appinfo") String appinfo){
		
		//decode appinfo
		Security.addProvider(new com.sun.crypto.provider.SunJCE());
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());	
		String appInfo = "", orderInfo = "";
		String userDir = "", userId;
		int maxRequest = -1, maxRunningRequest = -1;
		
		appInfo = appinfo;
//		try {
//			appInfo = util.TripleDES.desDecodeOnly(appinfo, key, vector);
//		} catch (Exception e) {
//			log.writeLog("[showConverted](appinfo)" + appinfo + ": ERROR 1002 decode appinfo error " + e.getMessage());
//			return new convertedResult();
//		}
		
		//parse userDir, userId, maxRequest, maxRunningRequest
		try {
			String[] vals = appInfo.split(";");
			orderInfo = vals[1];
			userId = vals[3];
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.writeLog("[showConverted](appinfo)" + appinfo + ": ERROR 1003 parse appinfo error " + e.getMessage());
			return new convertedResult();
		}
		try {
			String[] orderInfo_ = orderInfo.split("\\|\\|");
			String[] item;
			for(int i = 0; i < orderInfo_.length; i++){
				
				item = orderInfo_[i].split(":");
				
				if(item[0].equals("maxJobNum")){
					maxRequest = Integer.parseInt(item[1]);
				}
				else if(item[0].equals("maxConcurrentJobs")){
					maxRunningRequest = Integer.parseInt(item[1]);
				}
				else if(item[0].equals("folderId"))
					userDir = item[1];
				else if(item[0].equals(""))
					break;
			}
			
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			log.writeLog("[showConverted](appinfo)" + appinfo + ": ERROR 1004 parse order info error " + e.getMessage());
			return new convertedResult();
		}
		
		//check order info and userDir order
		if(maxRequest == -1 || maxRunningRequest == -1 || userDir.equals("")){
			log.writeLog("[showConverted](appinfo)" + appinfo + ": ERROR 1005 incomplete orderinfo error");
			return new convertedResult();
		}
		
		try{
			//update downloaderInfo
			sql = "select * from " + downloaderInfoTableName + " where userId='" + userId + "'";
			qstmt = dbQuery.createStatement(queryConn);
			ustmt = dbUpdate.createStatement(updateConn);
			rs = qstmt.executeQuery(sql);
			
			//if the user doesn't exist, create the user
			if(!rs.next()){
				sql = "insert into " + downloaderInfoTableName + " values ('" + userId + "', '"
						+ maxRequest + "', '" + maxRunningRequest + "')";
				ustmt.executeUpdate(sql);
				
			}
			//if the user exist, update the downloadinfo
			else{
				
				int maxRequestOfDB = rs.getInt("maxRequest");
				int maxRunningRequestOfDB = rs.getInt("maxRunningRequest");
				
				if(maxRequest != maxRequestOfDB || maxRunningRequest != maxRunningRequestOfDB){
					sql = "update " + downloaderInfoTableName + " set maxRequest='" + maxRequest
							+ "', maxRunningRequest='" + maxRunningRequest + "' where userId='"
							+ userId + "'";
					ustmt.executeUpdate(sql);
				}
			}
			ustmt.close();
			
			
			convertedResult cr = new convertedResult();
			
			sql = "select * from " + requestTableName + " where userId='" + userId
					+ "' and status='done'";
			rs = qstmt.executeQuery(sql);
			
			while(rs.next()){
				try{
					
					convertedTaskInfo cti = new convertedTaskInfo();
					
					cti.setRequestId(URLDecoder.decode(rs.getString("requestId"), "utf8"));
					cti.setTaskName(URLDecoder.decode(rs.getString("taskName"), "utf8"));
					cti.setStatus(URLDecoder.decode(rs.getString("status"), "utf8"));
					cti.setFilePath(URLDecoder.decode("contents", "utf8"));
					
					cr.putTask(cti);
					
					
				}catch(Exception e){
					
					log.writeLog("[showConverted](userId)" + userId  + ": ERROR " + e.getMessage());
					continue;
				}
				
		   }
		   rs.close();
		   qstmt.close();
		   return cr;
			
		}catch(SQLException e){
			
			log.writeLog("[showConverted](userId)" + userId + ": ERROR 1006 db operation error " + e.getMessage());
			return new convertedResult();
			
		}finally{
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					log.writeLog("[showConverted](userId)" + userId + ": ERROR 1006 db operation error " + e.getMessage());
				}
			}
			if(qstmt != null){
				try {
					qstmt.close();
				} catch (SQLException e) {
					log.writeLog("[showConverted](userId)" + userId + ": ERROR 1006 db operation error " + e.getMessage());
				}
			}
			if(ustmt != null){
				try {
					ustmt.close();
				} catch (SQLException e) {
					log.writeLog("[showConverted](userId)" + userId + ": ERROR 1006 db operation error " + e.getMessage());
				}
			}
		}
		
		
	}
	
	/**
	 * 显示指定 requestId 的当前状态信息
	 * @param requestId
	 * @return
	 */
	@GET
	@Path("showDetails")
	@Produces("application/xml")
	public detailResult showDetails(
			@HeaderParam("requestId") String requestId){
		
		try {
			
			detailResult dr = new detailResult();
			
			qstmt = dbQuery.createStatement(queryConn);
			sql = "select * from " + requestTableName + " where requestId='" 
			      + requestId + "'";
			rs = qstmt.executeQuery(sql);
			
			while(rs.next()){
				
				detailResultInfo dri = new detailResultInfo();
				
				dri.setRequestId(URLDecoder.decode(rs.getString("requestId"), "utf8"));
				dri.setTaskName(URLDecoder.decode(rs.getString("taskName"), "utf8"));
				dri.setFilePath(URLDecoder.decode(rs.getString("contents"), "utf8"));
				dri.setStatus(URLDecoder.decode(rs.getString("status"), "utf8"));
				
				if(rs.getString("status").equals("error")){
					dri.setErrorCode(rs.getInt("errorCode"));
					dri.setErrorMsg(URLDecoder.decode(rs.getString("errorMsg"), "utf8"));
				}
				
				dr.putTask(dri);
				
			}
			rs.close();
			qstmt.close();
			return dr;
			
		} catch (SQLException e){
			
			log.writeLog("[showDetails](requestId)" + requestId + ": ERROR 1006 db operation error " + e.getMessage());
		} catch (UnsupportedEncodingException e) {
			
			log.writeLog("[showDetails](requestId)" + requestId + ": ERROR 1001 url encoding error " + e.getMessage());
		}finally{
			
			try {
				if(rs != null)
					rs.close();
			} catch (SQLException e) {
				log.writeLog("[showDetails](requestId)" + requestId + ": ERROR 1006 db operation error " + e.getMessage());
			}
			
			try {
				if(qstmt != null)
					qstmt.close();
			} catch (SQLException e) {
				log.writeLog("[showDetails](requestId)" + requestId + ": ERROR 1006 db operation error " + e.getMessage());
			}
			
			
		}
		return new detailResult();
	}

	/**
	 * 删除数据库requestTable指定 requestId 对应的元组  
	 * @param requestId
	 * @return
	 */
	@POST
	@Path("deleteTask")
	@Produces("application/xml")
	@Consumes("application/xml")
	public deleteResult deleteTask(
			@HeaderParam("requestId") String requestId){
		
		log.writeLog("[deleteTask] Receive deleteTask request: " + requestId);
		
		//获取系统当前时间 时间格式 "yyyy-MM-dd HH:mm:ss" 24时制
		String nowTime;
		try {
			nowTime = "";
			Date now = new Date(System.currentTimeMillis());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			nowTime = sdf.format(now);
		} catch (Exception e) {
			log.writeLog("[deleteTask](requestId)" + requestId + ": ERROR in grep system time " + e.getMessage());
			return new deleteResult();
		}
		
		try {
			
			sql = "select * from " + requestTableName + " where requestId='" 
			      + requestId + "' and commitTime <= '" + nowTime + "'";
			qstmt = dbQuery.createStatement(queryConn);
			rs = qstmt.executeQuery(sql);
			
			
			if(rs.next()){
				
				sql = "delete from " + requestTableName + " where requestId = '" + requestId + "'";
				ustmt = dbUpdate.createStatement(updateConn);
				ustmt.executeUpdate(sql);
				rs.close();
				ustmt.close();
				return new deleteResult(1,"Success");
			}else{
				rs.close();
				ustmt.close();
				log.writeLog("[deleteTask](requestId)" +  requestId + ": ERROR not found or commit time wrong");
				return new deleteResult(0,"3001");
			}
			
		} catch (SQLException e) {
			
			log.writeLog("[deleteTask](requestId)" + requestId + ": ERROR 1006 db operation error " + e.getMessage());
			try {
				if (!updateConn.getAutoCommit()) {
					updateConn.rollback();
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			} finally {
				try {
					updateConn.setAutoCommit(true);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			return new deleteResult(0,"1006");
		}finally{
			
			try {
				if(rs != null)
					rs.close();
				if(qstmt != null)
					qstmt.close();
				if(ustmt != null)
					ustmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	
	//test
	@GET
	@Path("/hello")
	@Produces(MediaType.TEXT_PLAIN)
	public String sayPlainTextHello(){
		return "Hello Jersey by cuixin";
	}
	
//	//test
//	public static void main(String[] args)
//	{
//		System.out.println("Test interface...");
//		String fileId = "/home/arrietty";
//		String apptoken = "appTokenId";
//		String sapaddr = "sapaddr";
//		
//		String appinfo = "appinfo;maxJobNum:100||maxConcurrentJobs:10||"
//				+ "folderId:userDir||;20200101;cuixin";
//		String taskname = "test_task";
//		
//		interFace test_case = new interFace();
//		
//		test_case.addTask(fileId, apptoken, sapaddr, appinfo, taskname);
//		
//		convertingResult convertingr = new convertingResult();
//		convertingr = test_case.showConverting(appinfo);
//		
//		
//
//	}
	

}
