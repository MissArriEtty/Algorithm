/**
 * 
 * 轮询类 负责从db中取出waiting状态request并且插入到queue
 * @author arrietty
 * @date 2013.8
 * 
 */
package poller;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import rabbitmq.requestEnqueue;
import config.configuration;
import dbio.dbQuery;
import dbio.dbUpdate;
import logPackage.cloudLog;

public class poller implements Runnable {
	
	private final static configuration conf = new configuration("poller-conf.xml");
	private final static int INTERVAL = Integer.parseInt(conf.getConf("pollerInterval"));
	private final static String REQUESTQUEUE_NAME = conf.getConf("rabbitmqRequestQueueName");
	private final static String requestTableName = "requestTable";
	private final static String downloaderInfoTableName = "downloaderInfo";
	
	//DB
	public final static  String dbAddress = conf.getConf("mysqlAddr");
	public final static  String userName = conf.getConf("mysqlUserName");
	public final static  String passwd = conf.getConf("mysqlPasswd");
	public final static  String driver = conf.getConf("mysqlDriver");
	
	private final static dbUpdate dbUpdate = new dbio.dbUpdate(dbAddress, userName, passwd);
	private static Connection updateConn = dbUpdate.connect();
	private final static dbQuery dbQuery = new dbio.dbQuery(dbAddress, userName, passwd);
	private static Connection queryConn = dbQuery.connect();
	private static Statement qstmt = dbQuery.createStatement(queryConn);
	private static Statement ustmt = dbUpdate.createStatement(updateConn);
	
	public static cloudLog log = new cloudLog("poller.log");
	
	public poller(){
		
		//connect to db
		try {
			if(queryConn == null || queryConn.isClosed()){
				queryConn = dbQuery.connect();
			}
		} catch (SQLException e) {
			log.writeLog("[poller.poller]: Error " + e.getMessage());
		}
		
		try {
			if(updateConn == null || queryConn.isClosed()){
				updateConn = dbUpdate.connect();
			}
		} catch (SQLException e) {
			log.writeLog("[poller.poller]: Error " + e.getMessage());
		}
		
		try {
			if(qstmt == null || qstmt.isClosed()){
				qstmt = dbQuery.createStatement(queryConn);
			}
		} catch (SQLException e) {
			log.writeLog("[poller.poller]: Error " + e.getMessage());
		}
		
		try {
			if(ustmt == null || ustmt.isClosed()){
				ustmt = dbUpdate.createStatement(updateConn);
			}
		} catch (SQLException e) {
			log.writeLog("[poller.poller]: Error " + e.getMessage());
		}
	}

	
	@Override
	public void run() {
		
		String sql = "", userId;
		int num;
		ResultSet rs = null;
		HashMap<String, Integer> userRunningMap = new HashMap<String, Integer>(),
				userQuotaMap = new HashMap<String, Integer>();
		
		while(true){
			
			try {
				Thread.sleep(INTERVAL);
			} catch (InterruptedException e) {
				
				log.writeLog("[poller.poller]: Error " + e.getMessage());
			}
			
			try {
				
				//save the number of current waiting&running requests of each user in requestTable
				userRunningMap.clear();
				sql = "select userId, count(requestId) as num from " + requestTableName
						+ " where status in ('running', 'enqueue') group by userId";
				rs = dbQuery.executeQuery(qstmt, sql);
				
				while (rs.next()) {
					userId = rs.getString("userId");
					num = rs.getInt("num");
					userRunningMap.put(userId, num);
				}
				rs.close();
				
				//save quota info of each user
				userQuotaMap.clear();
				sql = "select userId, maxRunningRequest from " + downloaderInfoTableName;
				rs = dbQuery.executeQuery(qstmt, sql);
				
				while (rs.next()) {				
					userId = rs.getString("userId");
					num = rs.getInt("maxRunningRequest");
					userQuotaMap.put(userId, num);
				}
				rs.close();
				
				//fetch requestTable item insert into rabbitmq
				sql = "select * from " + requestTableName 
					  + " where status = 'waiting' order by commitTime";
				rs = dbQuery.executeQuery(qstmt, sql);
				
				while(rs.next()){
//					System.out.println("test... sequence = " + no +" enQueue userId = " + rs.getString("userId") 
//							+ " requestId = " + rs.getString("requestId"));
					
					
					//check user whether in downloaderInfoTable
					userId = rs.getString("userId");
					if(!userQuotaMap.containsKey(userId)){
						log.writeLog("[poller.poller]: Error userId in requestTable"
								+ " but not in downloaderInfo. ");
						continue;
					}
					
					if (userRunningMap.get(userId) == null)
						userRunningMap.put(userId, 0);
					int userRunning = userRunningMap.get(userId);
					//check user's quota whether exceed
					if(userRunning > userQuotaMap.get(userId)){
						log.writeLog("[poller.poller]: Error user's quota has used up !");
						continue;
					}
					
					//create new queue item and enqueue
					Request request = new Request(rs.getString("requestId"), 
							                      rs.getString("userId"), 
							                      rs.getString("contents"), 
							                      rs.getString("MD5"),
							                      rs.getString("format"));
					log.writeLog("[poller.poller](requestId)" + rs.getString("requestId") 
							+ ": inserting into queue");
					
					publishToQueue(request);
					sql = "Update " + requestTableName + " Set status = 'enqueue' Where requestId = " + rs.getString("requestId");
					dbUpdate.executeUpdate(ustmt, sql);
					userRunningMap.put(userId, userRunning + 1);
					
					log.writeLog("[poller.poller](requestId)" + rs.getString("requestId") 
							+ ": succeed to insert into queue");
					
					//update status of db ? i think no
					
					//update userRuninngMap ? i think no
					
				}
				rs.close();

				
			} catch (SQLException e) {
				log.writeLog("[poller.poller]: Error " + e.getMessage());
			} catch(Exception e){
				try {
					while (updateConn == null || updateConn.isClosed())
						updateConn = dbUpdate.connect();				
					while (queryConn == null || queryConn.isClosed())
						queryConn = dbQuery.connect();				
					if (ustmt == null || ustmt.isClosed())
						ustmt = dbUpdate.createStatement(updateConn);
					if (qstmt == null || qstmt.isClosed())
						qstmt = dbQuery.createStatement(queryConn);
				} catch (SQLException e1) {
					log.writeLog("[poller.poller]: Error " + e1.getMessage());
				}
			}
			
			
		}
		
		
	}

	
	private static void publishToQueue(Request request) {
		boolean flag = true;
		while(flag){
			
			requestEnqueue.enQueue(request, REQUESTQUEUE_NAME);
			flag = false;
			
			try {
				if(flag){
					Thread.sleep(INTERVAL);
				}
			} catch (InterruptedException e) {
				log.writeLog("[poller.poller](requestId)" + request.getRequestId() 
						+ ": Error 1007 fail to insert into queue. " + e.getMessage());
			}
		}
		
	}
	
	public static void main(String[] args){
		Thread pt = new Thread(new poller());
		pt.start();
		Thread lt = new Thread(new listener());
		lt.start();

	}
}
