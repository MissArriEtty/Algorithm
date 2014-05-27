package test;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Connection conn = null;
		
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			do {
				System.out.println("ready to connect");
				conn = DriverManager.getConnection("jdbc:mysql://10.100.216.83:3306/image", "root", "zwj1989");	
			} while (conn == null);
			
			System.out.println("connect ok");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}
