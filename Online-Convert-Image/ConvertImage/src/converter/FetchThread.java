package converter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Queue;

import logPackage.cloudLog;

import config.configuration;
import dbio.dbQuery;
import dbio.dbUpdate;

import poller.Request;
import rabbitmq.requestDequeue;

public class FetchThread implements Runnable {
	/** 日志文件操作 */
	private static cloudLog log = new cloudLog(FetchThread.class.getName());
	
	private Queue<Request> requestQueue;
	private Converter cvt;
	
	private boolean shouldRun = true;
	
	private configuration conf = new configuration("converter-conf.xml");
	
	//DB
	private String dbAddr = conf.getConf("mysqlAddr");
	private String userName = conf.getConf("mysqlUserName");
	private String passwd = conf.getConf("mysqlPasswd");
	private String driver = conf.getConf("mysqlDriver");
	
	private dbUpdate dbUpdater = new dbio.dbUpdate(dbAddr, userName, passwd);
	private Connection updateConn = dbUpdater.connect();
	private dbQuery dbQuerier = new dbio.dbQuery(dbAddr, userName, passwd);
	private Connection queryConn = dbQuerier.connect();
	private Statement qstmt = dbQuerier.createStatement(queryConn);
	private Statement ustmt = dbUpdater.createStatement(updateConn);
	
	private final String REQUESTQUEUE_NAME = conf.getConf("rabbitmqRequestQueueName");
	
	private boolean pauseFlag = false;
	
	public FetchThread(Queue<Request> requestQueue, Converter cvt) {
		this.requestQueue = requestQueue;
		this.cvt = cvt;
	}
	
	public void pause() {
		pauseFlag = true;
	}
	
	public synchronized void resume() {
		pauseFlag = false;
		this.notify();
	}
	
	/** 连接数据库
	 */
	private void connectToDB() {
		dbUpdater = new dbUpdate(dbAddr, userName, passwd);
		updateConn = dbUpdater.connect();
		dbQuerier = new dbQuery(dbAddr, userName, passwd);
		queryConn = dbQuerier.connect();
		ustmt = dbUpdater.createStatement(this.updateConn);
		qstmt = dbQuerier.createStatement(this.queryConn);
		
		log.writeLog("Connection to database is OK");
	}
	
	@Override
	public void run() {
		connectToDB();
		
		while (shouldRun) {
			if (cvt.requestNotFull()) {
				Request newRequest = requestDequeue.deQueue(REQUESTQUEUE_NAME);
				System.out.println("fetch a new request " + newRequest.getRequestId());
				synchronized (requestQueue) {
					requestQueue.add(newRequest);
				}
				ResultSet rs = dbQuerier.executeQuery(qstmt, "select * from requestTable where requestId = " + newRequest.getRequestId());
				try {
					if (rs.next()) {
						String sql = "Update requestTable Set status = 'running', runningConverterId = '" + cvt.getId() + "' Where requestId = '" + rs.getString("requestId") + "'";
						dbUpdater.executeUpdate(ustmt, sql);
					}
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					log.writeLog("fetching(): " + e.toString());
				}
			}
			else
				System.out.println("full request");
			
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				log.writeLog("run(): " + e.toString());
			}
			
			if (pauseFlag) {
				System.out.println("fetch thread is paused");
				log.writeLog("fetch thread is paused");
				try {
					synchronized(this) {
						this.wait();
					}
				} catch (InterruptedException e) {
					log.writeLog(e.toString());
					e.printStackTrace();
				}
				System.out.println("Job fetcher is resumed.");
				log.writeLog("Job fetcher is resumed.");
			}
		}

	}

}
