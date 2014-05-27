package monitor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import config.configuration;
import converter.FileOperator;

import logPackage.cloudLog;

import dbio.dbQuery;
import dbio.dbUpdate;

public class CachePoller implements Runnable {
	/**日志记录*/
	private static cloudLog log = new cloudLog(CachePoller.class.getName());
	/** 数据库地址 */
	private String dbAddr;
	/** 数据库用户名 */
	private String userName;
	/** 数据库密码 */
	private String passwd;
	/** 数据库驱动 */
	private String driver = "com.mysql.jdbc.Driver";
	
	/** 压缩文件缓存天数*/
	private long cacheDay = 3;
	
	/** 负责数据库更新操作 */
	private dbUpdate dbUpdater;
	/** 负责数据库查询操作 */
	private dbQuery dbQuerier;
	/** 用于数据库更新操作的连接 */
	private Connection updateConn;
	/** 用于数据库查询操作的连接 */
	private Connection queryConn;
	/** 用于数据库更新操作的statement */
	private Statement ustmt;
	/** 用于数据库查询操作的statement */
	private Statement qstmt;
	
	private boolean shouldRun = true;
	
	public CachePoller(configuration conf) {
		//initialize db connection parameters.
		dbAddr = conf.getConf("mysqlAddr");
		userName = conf.getConf("mysqlUserName");
		passwd = conf.getConf("mysqlPasswd");
		driver = conf.getConf("mysqlDriver");
		try {
			cacheDay = Integer.parseInt(conf.getConf("cacheDay"));
		} catch (NumberFormatException ex) {
			log.writeLog("initialize: " + ex.toString());
			ex.printStackTrace();
		}
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
	
	public void setShouldRun(boolean shouldRun){
		this.shouldRun = shouldRun;
	}
	
	public boolean getShouldRun() {
		return shouldRun;
	}

	@Override
	public void run() {
		connectToDB();
		
		while (shouldRun) {
			ResultSet rs = dbQuerier.executeQuery(qstmt, "Select * From requestTable Where status = 'done' and finishAddrs != '' and finishTime <= '" 
					+ new Timestamp(System.currentTimeMillis() - cacheDay * 86400000) + "'");
			try {
				while (rs.next()) {
					String filePos = rs.getString("finishAddrs");
					int lastDot = filePos.lastIndexOf(".");
					String format = filePos.substring(lastDot);
					filePos = filePos.substring(0, lastDot);
					for (int index = 0; index < 3; ++index)
						try {
							FileOperator.deleteFile(filePos + "_" + index + format);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							log.writeLog("error when delete file : " + filePos + "_" + index + format);
						}
					String sql = "Update requestTable Set finishAddrs = '' Where requestId = '" + rs.getString("requestId") + "'";
					dbUpdater.executeUpdate(ustmt, sql);
				}
				rs.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				Thread.sleep(100000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
