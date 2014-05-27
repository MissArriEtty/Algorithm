package converter;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.net.NetUtils;

import poller.Request;
import rabbitmq.requestDequeue;
import monitor.Commands;
import logPackage.cloudLog;
import config.configuration;
import dbio.dbQuery;
import dbio.dbUpdate;
import cloud.SapInterface;

/**本类功能：转码节点的主程序
 * 
 * @author Eric
 * 
 */
public class Converter implements Runnable {

	/**日志记录*/
	private static cloudLog log = new cloudLog(Converter.class.getName());

	/**是否运行的标记 */
	private boolean shouldRun = true;
	
	/**最大运行任务数目*/
	private int MAX_RUNNING_REQUEST = 10;
	/**任务队列名*/
	private String REQUESTQUEUE_NAME;
	/**任务最大重启次数*/
	private int MAX_RESTART_COUNT = 10;
	/**心跳间隔时间*/
	private long HEARTBEAT_INTERVAL;
	/** 指定此转码节点是否会删除失败的任务 */
	private static boolean deleteFailJob = true;
	
	/**最后一次发送心跳的时间*/
	private long lastHeartbeatTime;
	
	/** Socket，用于与Monitor通讯 */
	private Socket socket;
	/** 从socket中读取数据流 */
	private DataInputStream in;
	/** 向socket发送数据流 */
	private DataOutputStream out;
	
	/** 本机地址 */
	private String localAddr;
	//private InetAddress localAddr;
	/** Monitor地址 */
	private String monitorAddr;
	/** Monitor端口 */
	private int monitorPort;
	
	/** 数据库地址 */
	public static String dbAddr;
	/** 数据库用户名 */
	public static String userName;
	/** 数据库密码 */
	public static String passwd;
	/** 数据库驱动 */
	public static String driver = "com.mysql.jdbc.Driver";
	
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
	
	/** 压缩图像存放在本地的位置*/
	private String compressPos;
	/** 压缩图像后的宽度*/
	private int width[] = new int[3];
	
	private ArrayList<PicCompress> compressJobList;
	
	private Queue<Request> requestQueue;
	
	private FetchThread fetcher;
	
	/** 用于获取系统硬件信息 */
	private SystemInfo sysinfo; // computer information
	
	/**初始化转码实例
	 * 
	 * @return 是否成功
	 */
	private boolean initialize() {

		configuration conf = new configuration("converter-conf.xml");

		System.out.println("Initializing");
		log.writeLog("Initializing");
		
		compressPos = conf.getConf("compressPos");
		
		try {
			width[0] = Integer.parseInt(conf.getConf("width0"));
			width[1] = Integer.parseInt(conf.getConf("width1"));
			width[2] = Integer.parseInt(conf.getConf("width2"));
			MAX_RUNNING_REQUEST = Integer.parseInt(conf.getConf("maxRunningRequest"));
			MAX_RESTART_COUNT =  Integer.parseInt(conf.getConf("maxRestartCount"));
			HEARTBEAT_INTERVAL = Integer.parseInt(conf.getConf("heartbeatInterval"));
		} catch(NumberFormatException e) {
			log.writeLog("initialize(): " + e.toString());
			e.printStackTrace();
		}
		if(conf.getConf("deleteFailJob").equals("no")) 
			deleteFailJob = false;
		else 
			deleteFailJob = true;
		REQUESTQUEUE_NAME = conf.getConf("rabbitmqRequestQueueName");
		compressJobList = new ArrayList<PicCompress>(MAX_RUNNING_REQUEST);
		requestQueue = new LinkedList<Request>();
		
		/*no platform
		Upload.defSapAddr = conf.getConf("sapAddr");
		if(!Upload.defSapAddr.startsWith("http://")) {
			Upload.defSapAddr = "http://" + Upload.defSapAddr;
		}
		Upload.defAppTokenId = conf.getConf("appToken");*/
		
		//initialize db connection parameters.
		dbAddr = conf.getConf("mysqlAddr");
		userName = conf.getConf("mysqlUserName");
		passwd = conf.getConf("mysqlPasswd");
		driver = conf.getConf("mysqlDriver");
		
		localAddr = conf.getConf("localHost");
		/*try {
			localAddr = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			System.out.println("Cannot connect to localhost: " + e.toString());
			log.writeLog("Cannot connect to localhost: " + e.toString());
			return false;
		}*/
		
		monitorAddr = conf.getConf("masterMonitorIP");
		monitorPort = Integer.parseInt(conf.getConf("masterMonitorPort"));
		
		try {
			connectToMonitor(monitorAddr, monitorPort);
		} catch (UnknownHostException e) {
			log.writeLog("Connot connect to the monitor: " + e.toString());
			System.out.println("Connot connect to the monitor: " + e.toString());
			return false;
		} catch (IOException e) {
			log.writeLog("Connot connect to the monitor: " + e.toString());
			System.out.println("Connot connect to the monitor: " + e.toString());
			return false;
		}
		
		return true;
	}

	/** 设置是否运行
	 * 
	 * @param shouldRun 是否运行
	 */
	public void setShouldRun(boolean shouldRun) {
		this.shouldRun = shouldRun;
	}
	
	/** 获取是否运行
	 * 
	 * @return shouldRun
	 */
	public boolean getShouldRun() {
		return shouldRun;
	}
	
	/** 连接Monitor
	 * @param addr Monitor地址
	 * @param port Monitor端口
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	private void connectToMonitor(String addr, int port) throws UnknownHostException, IOException {
		socket = new Socket(addr, port);
		out = new DataOutputStream(new BufferedOutputStream(NetUtils.getOutputStream(socket), 4096));
		in = new DataInputStream(new BufferedInputStream(NetUtils.getInputStream(socket), 4096));
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
	
	private void clearRequest() {
		ResultSet rs = dbQuerier.executeQuery(qstmt, "select * from requestTable where status = 'running' and runningConverterId = '" + localAddr + "'");
		try {
			while (rs.next()) {
				String sql = "Update requestTable Set status = 'waiting' Where requestId = '" + rs.getString("requestId") + "'";
				dbUpdater.executeUpdate(ustmt, sql);
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private Request fetchRequest() {
		Request newRequest = null;
		synchronized (requestQueue) {
			try {
				newRequest = requestQueue.remove();
			} catch (NoSuchElementException ex) {
			}
		}
		return newRequest;
	}
	
	private void startRequest(Request newRequest) {
		PicCompress newCompress = new PicCompress(newRequest, width, compressPos, log);
		new Thread(newCompress).start();
		compressJobList.add(newCompress);
	}
	
	private void finishRequest() {
		int jobIndex = 0;
		while (jobIndex < compressJobList.size()) {
			PicCompress compressJob = compressJobList.get(jobIndex);
			if (compressJob.isFinished()) {
				Request request = compressJob.getRequest();
				if (compressJob.isSucceed()) {
					for (int index = 0; index < width.length; ++index) {
						String filePath = compressPos + request.getRequestId() + "_" + index + "." + request.getFormat();
						System.out.println("upload name " + filePath);
						new Thread(new Upload(request, filePath, new File(filePath).length())).start();
					}
					//modify request tables
					dbUpdater.executeUpdate(ustmt, "update requestTable set finishTime = '" + (new Timestamp(System.currentTimeMillis())) + "', finishAddrs = 'D:\\\\compressPic\\\\" + request.getRequestId() + "." + request.getFormat() + "', status = 'done' where requestId = '" + request.getRequestId() + "'");
				}
				else {
					ResultSet rs = dbQuerier.executeQuery(qstmt, "Select * from requestTable where requestId = " + request.getRequestId());
					try {
						if (rs.next()) {
							int restartCount = rs.getInt("restartCount");
							String sql = "Update requestTable Set status = ";
							if (restartCount > MAX_RESTART_COUNT && deleteFailJob) 
								sql += "'error', errorCode = " + ResultCode.CVT_FAILURE + ", errorMsg = '" + compressJob.getFailMsg()  + "'";
							else
								sql += "'waiting', restartCount = " + (restartCount + 1) + " ";
							sql += "Where requestId = '" + rs.getString("requestId") + "'";
							dbUpdater.executeUpdate(ustmt, sql);
						}
						rs.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				compressJobList.remove(jobIndex);
			}
			else
				++jobIndex;
		}
	}

	/** 向Monitor注册
	 * @return 是否成功
	 * @throws IOException
	 */
	private boolean register() throws IOException {
		out.writeInt(Commands.REGISTER);
		out.flush();
		
		int command = in.readInt();
		System.out.println("get command : " + command);
		if (command == Commands.REGISTER_OK) 
			return true;
		else 
			return false;
	}
	
	/** 向Monitor发送心跳
	 */
	private void sendHeartBeat() throws IOException {
		updateLastHeartbeatTime();
		System.out.println("before write");
		out.writeInt(Commands.HEART_BEAT_REPORT);
		out.flush();
		System.out.println("after write");
	}
	
	/** 设置最后一次心跳时间
	 * 
	 * @param heartbeatTime 心跳时间
	 */
	public void updateLastHeartbeatTime() {
		lastHeartbeatTime = System.currentTimeMillis();
	}
	
	/** 获取本节点Id
	 * 
	 * @return 本节点Id
	 */
	public String getId() {
		return localAddr.toString();
	}
	
	public boolean requestNotFull() {
		synchronized (requestQueue) {
			if (requestQueue.size() + compressJobList.size() < MAX_RUNNING_REQUEST)
				return true;
		}
		return false;
	}
	
	/** 处理Monitor连接错误
	 * @param addr Monitor地址
	 * @param port Monitor端口
	 */
	private void errHandlerForConnToMonitor() {
		int i = 1;
		System.out.println("Error in communication with the monitor");
		log.writeLog("Error in communication with the monitor");
		System.out.println("Retry to connect to the monitor... " + i);
		while (true) {
			try {
				connectToMonitor(monitorAddr, monitorPort);
				break;
			} catch (UnknownHostException e) {
				//e.printStackTrace();
			} catch (IOException e) {
				//e.printStackTrace();
			}
			System.out.println("Retry to connect to the monitor... " + (++i));
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				log.writeLog("errHandlerForConnToMon(): " + e.toString());
				e.printStackTrace();
			}
		}
		System.out.println("Re-connect OK!");
		while(true) {
			System.out.println("Registering...");
			try {
				if (this.register()) {
					System.out.println("Register OK!");
					break;
				} else {
					System.out.println("Register failed!");
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Register failed!");
			}
		}

		log.writeLog("Connection to the monitor is OK");
	}
	
	/** 获取节点硬件信息
	 */
	private void checkNodeStatus() {
		// update node table
		double cpuRatio = sysinfo.getCpuRatio();
		System.out.println("cpu : " + cpuRatio);
		
		//long totalMemory = sysinfo.getTotalPhysicalMemorySize();
		long freeMemory = sysinfo.getFreePhysicalMemorySize();
		
		dbUpdater.executeUpdate(ustmt, "Update ConverterInfo Set cpu = " + cpuRatio + ", memory = " + freeMemory + " Where nodeId = '" + localAddr.toString() + "'");
		
		if (cpuRatio >= 0.9)
			fetcher.pause();
		else if (cpuRatio < 0.8)
			fetcher.resume();
	}

	@Override
	public void run() {
		log.writeLog("Convertor thread is running.");
		System.out.println("Convertor thread is running.");
		
		sysinfo = new SystemInfo();
		
		connectToDB();
		// insert this node to nodeInfo table
		dbUpdater.executeUpdate(ustmt, "delete from converterInfo where nodeId = '" + localAddr + "'");
		dbUpdater.executeUpdate(ustmt, "insert into converterInfo(nodeId, status) values('" + localAddr + "', 'alive')");
		
		clearRequest();
		
		fetcher = new FetchThread(requestQueue, this);
		new Thread(fetcher).start();
		
		long time1 = System.currentTimeMillis(), time2;
		while (shouldRun) {
			/* judge whether should fetch new jobs
			   if so, fetch a new job*/
			Request newRequest = fetchRequest();
			if (newRequest != null) 
					startRequest(newRequest);
			finishRequest();
			
			time2 = System.currentTimeMillis();
			if (time2 - time1 > HEARTBEAT_INTERVAL) {
				try {
					System.out.println("ready to send hearbeat");
					sendHeartBeat();
					checkNodeStatus();
				} catch (IOException ex) {
					errHandlerForConnToMonitor();
				}
				time1 = System.currentTimeMillis();
			}
			
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				log.writeLog("run(): " + e.toString());
			}
		}
	}
	
	public static void main(String[] args) {
		Converter cvt = new Converter();
		cvt.initialize();
		
		System.out.println("Registering...");
		log.writeLog("Registering...");
		try {
			if (cvt.register()) {
				System.out.println("Register OK!");
				log.writeLog("Register OK!");
				
				// start download main thread
				new Thread(cvt).start();
			} else {
				System.out.println("Register failed!");
				log.writeLog("Register failed!");
			}
		} catch (IOException e) {
			System.out.println("Cannot connect to the monitor.");
			log.writeLog("Cannot connect to the monitor.");
			// e.printStackTrace();
		}
	}
}
