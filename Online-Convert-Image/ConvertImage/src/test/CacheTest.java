package test;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import config.configuration;
import dbio.dbQuery;
import dbio.dbUpdate;

public class CacheTest {

	private final static configuration conf = new configuration("poller-conf.xml");
	private final static String requestTableName = "requesttable";
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
	private static String cloudPicPos = "D:\\cloudPic\\";
	private static String compressPicPos = "D:\\compressPic\\";
	
	private static void initializeDB() {
		String sql = "Delete From " + requestTableName;
		try {
			ustmt.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		sql = "Insert into " + requestTableName + "(requestId, userId, taskName, contents, commitTime, appTokenId, userDirId, sapAddrs, MD5, status, finishTime, finishAddrs) " 
				+ "values(?, '0', ?, ?, ?, '0', '0', '0', '0', 'done', ?, ?)";
		long current = System.currentTimeMillis();
		final int taskInfo[] = {0, 5000, 5020, 10000, 10010, 10020, 10030, 14000};
		try {
			PreparedStatement ps = updateConn.prepareStatement(sql);
			for (int index = 0; index < taskInfo.length; ++index) {
				ps.setString(1, Integer.toString(index + 1));
				ps.setString(2, Integer.toString(index + 1));
				ps.setString(3, cloudPicPos + "pic" + Integer.toString(index + 1) + ".jpg");
				ps.setString(4, new Timestamp(current + index * 10).toString());
				ps.setString(5, new Timestamp(current - 86400000 * 3 + 10000 + taskInfo[index]).toString());
				ps.setString(6, compressPicPos + Integer.toString(index + 1) + ".jpg");
				ps.executeUpdate();
				
				for (int suffix = 0; suffix < 3; ++suffix)
					try {
						new File(compressPicPos + Integer.toString(index + 1) + "_" + Integer.toString(suffix) + ".jpg").createNewFile();
					} catch (IOException e) {
						
					}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		initializeDB();
	}

}
