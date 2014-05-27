package test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;

import poller.Request;
import rabbitmq.requestEnqueue;

import config.configuration;
import dbio.dbQuery;
import dbio.dbUpdate;

public class ConverterTest {

	private final static configuration conf = new configuration("poller-conf.xml");
	private final static String REQUESTQUEUE_NAME = conf.getConf("rabbitmqRequestQueueName");
	private final static int INTERVAL = Integer.parseInt(conf.getConf("pollerInterval"));
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
	
	private static String host = "10.20.2.44";
	
	private static void initializeDB() {
		String sql = "Delete From " + requestTableName;
		try {
			ustmt.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		sql = "Insert into " + requestTableName + "(requestId, userId, taskName, contents, commitTime, appTokenId, userDirId, sapAddrs, MD5, status) " 
				+ "values(?, '0', ?, ?, ?, '0', '0', '0', '0', 'waiting')";
		long current = System.currentTimeMillis();
		final String taskInfo[][] = {{"1", "0", cloudPicPos + "pic1.jpg", new Timestamp(current).toString()}, 
									{"2", "1", cloudPicPos + "pic2.jpg", new Timestamp(current + 10).toString()},
									{"3", "2", cloudPicPos + "pic3.jpg", new Timestamp(current + 20).toString()},
									{"4", "3", cloudPicPos + "pic4.jpg", new Timestamp(current + 30).toString()}};
		try {
			PreparedStatement ps = updateConn.prepareStatement(sql);
			for (int index = 0; index < 12; ++index) {
				ps.setString(1, Integer.toString(index));
				ps.setString(2, Integer.toString(index));
				ps.setString(3, cloudPicPos + "pic" + (index % 4 + 1) + ".jpg");
				ps.setString(4, new Timestamp(current + index * 10).toString());
				ps.executeUpdate();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]) {
		initializeDB();
		initializeQueue();
	}

	private static void initializeQueue() {
		
		try {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost(host);
			factory.setVirtualHost("/");
			factory.setUsername("zwj");
			factory.setPassword("zwj");
			
			com.rabbitmq.client.Connection conn = factory.newConnection();
			Channel channel = conn.createChannel();
			channel.queueDelete("watingQueue");
			
			channel.close();
			conn.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
