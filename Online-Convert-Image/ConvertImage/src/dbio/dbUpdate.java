package dbio;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class dbUpdate {
	
	private String url;
	private String userName;
	private String passwd;
	private Connection conn = null;
	private Statement stmt = null;
	
	public dbUpdate(String url, String userName, String passwd){
		
    	this.url = url;
    	this.userName = userName;
    	this.passwd = passwd;
    }
	
	public Statement createStatement(Connection conn) {
    	Statement stmt = null;
		try {
			stmt = conn.createStatement();
			if(this.stmt == null){
				this.stmt = stmt;
			}
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
    	return stmt;
    }
	
	public boolean executeUpdate(Statement stmt, String sql){
		
    	boolean shouldReturn = false;
    	boolean res = false;
    	
    	while(!shouldReturn){
    		try {
        		res = this.stmt.execute(sql);
        		shouldReturn = true;
    		} catch (Exception e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    			tryConnetDB();
           	}
    	}
		return res;
    }
	
	public void tryConnetDB()
	{
		try {
			
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
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
    			
    		}while(this.conn == null);
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
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
    
    public Connection connect(){
    	
    	boolean shouldReturn = false;
    	Connection conn = null;
    	
		while(!shouldReturn){
			
			try {
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				do {
	        		conn = DriverManager.getConnection(url, userName, passwd);
	        		
	        	}while (conn == null);
				
	        	this.conn = conn;
	        	shouldReturn = true;
	        	
			} catch(Exception e){
	    		e.printStackTrace();
	    	}
			
		}
		try {
			if(!conn.isClosed()){
				System.out.println("connect to db successfully");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conn;
    }
	

}
