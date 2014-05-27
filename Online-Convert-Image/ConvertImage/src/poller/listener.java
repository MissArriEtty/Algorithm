package poller;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.hadoop.net.NetUtils;

import logPackage.cloudLog;


public class listener implements Runnable {

	private cloudLog log = new cloudLog("poller.log");
	
	@Override
	public void run() {
		
		ServerSocket ss = null;
		try {
			ss = new ServerSocket(8803);
			Socket socket = null;
			while(true){
				
				socket = ss.accept();
				DataOutputStream dos = new DataOutputStream(
						new BufferedOutputStream(NetUtils.getOutputStream(socket),4096));
				dos.writeChars("converter poller ok!");
				dos.flush();
				dos.close();
			}
			
			
			
		} catch (IOException e) {
			log.writeLog("[poller.listener]: Error " + e.getMessage() );
		}
		
	}

}
