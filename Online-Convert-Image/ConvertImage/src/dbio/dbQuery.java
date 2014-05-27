package dbio;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class dbQuery {
	
	private String url;
	private String userName;
	private String passwd;
	private Connection conn = null;
	private Statement stmt = null;
	
	public dbQuery(String url, String userName, String passwd){
		
    	this.url = url;
    	this.userName = userName;
    	this.passwd = passwd;
    	
    }
	/**
	 * 
	 * 创建数据库statement
	 * @param conn 
	 * @return statement
	 */
	
	public Statement createStatement(Connection conn) {
		
    	Statement stmt = null;
		try {
			
			stmt = conn.createStatement();
			if(this.stmt == null){
				this.stmt = stmt;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return stmt;
    	
    }
	
	/**
	 * 
	 * 执行数据库查询语句sql
	 * @param stmt 数据库statement
	 * @param sql  查询语句
	 * @return resultset 查询结果
	 */
	public ResultSet executeQuery(Statement stmt, String sql){
		
		boolean shouldReturn = false;
		ResultSet res = null;
		
		while(!shouldReturn)
		{
			try{
				
				res = this.stmt.executeQuery(sql);
				shouldReturn = true;
			}catch(Exception e)
			{
				System.out.println(e.getMessage());
				tryConnectDB();
				
			}
		}
		
		return res;
		
	}
	
	/**
	 * 
	 * 尝试多次链接数据库
	 */
	
	public void tryConnectDB()
	{
		try{
			
			if(this.conn != null){
				
				if(!this.conn.isClosed()){
					this.conn.close();
					this.stmt.close();
			    }else{
			    	this.stmt.close();
			    }
				this.conn = null;
			}
			
			do{
    			try {
    				
					Thread.sleep(2000);
					this.conn = connect();
					if(this.conn != null){
						this.stmt = createStatement(this.conn);
					}
					
				} catch (Exception e) {
					
					System.out.println(e.getMessage());

				}
    			
    		}while(this.conn == null);
			
		} catch (Exception e) {
			
			System.out.println(e.getMessage());
		}
	}
	
	public Connection connect() {
		
		boolean shouldReturn = false;
		Connection conn = null;
		
		while(!shouldReturn){
			try{
				
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				do {
	        		conn = DriverManager.getConnection(url, userName, passwd);
	        	}while (conn == null);
	        	this.conn = conn;
	        	shouldReturn = true;
	        	
			}catch(Exception e)
			{
				System.out.println(e.getMessage());
			}
			
		}
		
		try {
			
			if(!conn.isClosed()){
				System.out.println("connect to db successfully");
			}
			
		} catch (SQLException e) {
			
			System.out.println(e.getMessage());
		}
		
		return conn;
	}
	
    public void closeStatement(Statement stmt){
    	try {
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void disConnect(Connection conn){
    	try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
	
}
