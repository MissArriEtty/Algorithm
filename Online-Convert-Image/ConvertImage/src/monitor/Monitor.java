package monitor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.TreeMap;

import logPackage.cloudLog;

import config.configuration;
import converter.Converter;

import dbio.dbQuery;
import dbio.dbUpdate;

public class Monitor implements Runnable {
	/**日志记录*/
	protected static cloudLog log = new cloudLog(Monitor.class.getName());
	
	/** 数据库地址 */
	protected String dbAddr;
	/** 数据库用户名 */
	protected String userName;
	/** 数据库密码 */
	protected String passwd;
	/** 数据库驱动 */
	protected String driver = "com.mysql.jdbc.Driver";
	
	/**服务器端套接字端口号*/
	private int socketPort;
	/**服务器端套接字*/
	private ServerSocket ss;
	
	/** 负责数据库更新操作 */
	protected dbUpdate dbUpdater;
	/** 负责数据库查询操作 */
	protected dbQuery dbQuerier;
	/** 用于数据库更新操作的连接 */
	protected Connection updateConn;
	/** 用于数据库查询操作的连接 */
	protected Connection queryConn;
	/** 用于数据库更新操作的statement */
	protected Statement ustmt;
	/** 用于数据库查询操作的statement */
	protected Statement qstmt;
	
	/**活动的Converter的列表*/
	protected ArrayList<Object[]> converterList;
	
	/**Monitor的心跳时间*/
	protected static long HEARTBEAT_INTERVAL;
	
	/**是否运行的标记 */
	private boolean shouldRun = true;
	
	public Monitor() {
		configuration conf = new configuration("monitor-conf.xml");
		
		//initialize db connection parameters.
		dbAddr = conf.getConf("mysqlAddr");
		userName = conf.getConf("mysqlUserName");
		passwd = conf.getConf("mysqlPasswd");
		driver = conf.getConf("mysqlDriver");
		
		try {
			socketPort = Integer.parseInt(conf.getConf("monitorPort"));
			HEARTBEAT_INTERVAL = Integer.parseInt(conf.getConf("heartbeatInterval"));
		} catch (NumberFormatException ex) {
			log.writeLog(ex.toString());
			ex.printStackTrace();
		}
		System.out.println("port : " + socketPort);
		try {
			ss = new ServerSocket(socketPort);
		} catch (IOException e) {
			log.writeLog("fail to open serversocket at " + socketPort);
			e.printStackTrace();
		}
		
		connectToDB();
		
		converterList = new ArrayList<Object[]>();
	}
	
	public void setShouldRun(boolean shouldRun){
		this.shouldRun = shouldRun;
	}
	
	public boolean getShouldRun() {
		return shouldRun;
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
		ConverterPoller poller = new ConverterPoller(this);
		new Thread(poller).start();
		CachePoller cleaner = new CachePoller(new configuration("monitor-conf.xml"));
		new Thread(cleaner).start();
		while (shouldRun) 
			try {
				System.out.println("Monitor is waiting for new connections from downloaders.....");
				if(ss == null)
					System.out.println("ss is null");
				Socket socket = ss.accept();
				System.out.println("accpet new connection");
				ConverterReceiver receiver = new ConverterReceiver(socket, this);
				String nodeId = socket.getInetAddress().toString();
				
				log.writeLog("accept socket request from : " + nodeId);
				new Thread(receiver).start();
			} catch (Exception e) {
				System.out.println("catch exception when accepting new connections");
				e.printStackTrace();
			}
	}
	
	public static void main(String args[]) {
		Monitor mon = new Monitor();
		new Thread(mon).start();
	}
}
