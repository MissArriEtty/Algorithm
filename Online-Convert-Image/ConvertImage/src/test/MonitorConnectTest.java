package test;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MonitorConnectTest {
	public static void main(String args[]) {
		try {
			System.out.println("ready to listen");
			ServerSocket ss = new ServerSocket(4321);
			Socket socket = ss.accept();
			DataInputStream di = new DataInputStream(socket.getInputStream());
			System.out.println(di.readUTF());
			di.close();
			System.out.println("receive from " + socket.getInetAddress());
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
