package monitor;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Iterator;

import logPackage.cloudLog;

import org.apache.hadoop.net.NetUtils;

public class ConverterReceiver implements Runnable {

	private cloudLog log;
	private Monitor monitor;
	private Socket socket;
	private String converterId;
	private boolean shouldRun = true;
	
	public ConverterReceiver(Socket socket, Monitor monitor) {
		this.monitor = monitor;
		this.socket = socket;
		
		converterId = socket.getInetAddress().toString();
		int index = converterId.lastIndexOf(".");
		log = new cloudLog(ConverterReceiver.class.getName() + "_" + converterId.substring(index + 1));
	}
	
	public void setShouldRun(boolean shouldRun){
		this.shouldRun = shouldRun;
	}
	
	public boolean getShouldRun() {
		return shouldRun;
	}
	
	@Override
	public void run() {
		DataOutputStream out = null;
		DataInputStream in = null;
		
		try {
			out = new DataOutputStream(new BufferedOutputStream(NetUtils.getOutputStream(socket), 4096));
			in = new DataInputStream(new BufferedInputStream(NetUtils.getInputStream(socket), 4096));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		System.out.println("downloadreceiver for " + converterId + " is running");
		log.writeLog("downloadreceiver for " + converterId + " is running");
		while (shouldRun) 
			try {
				int command = in.readInt();
				System.out.println("command is " + command);
				long current = System.currentTimeMillis();
				switch (command) {
				case Commands.REGISTER:
					synchronized(monitor.converterList) {
						Iterator<Object[]> converterIt = monitor.converterList.iterator();
						while (converterIt.hasNext()) {
							Object[] node = converterIt.next();
							if (((String)node[1]).equals(socket.getInetAddress().toString())) { 
								monitor.converterList.remove(node);
								break;
							};
						}
						monitor.converterList.add(new Object[]{current, socket.getInetAddress().toString()});
					}
					out.writeInt(Commands.REGISTER_OK);
					out.flush();
					break;
				case Commands.HEART_BEAT_REPORT:
					synchronized(monitor.converterList) {
						Iterator<Object[]> converterIt = monitor.converterList.iterator();
						while (converterIt.hasNext()) {
							Object[] node = converterIt.next();
							if (((String)node[1]).equals(socket.getInetAddress().toString())) { 
								monitor.converterList.remove(node);
								break;
							}
						}
						monitor.converterList.add(new Object[]{current, socket.getInetAddress().toString()});
					}
					break;
				}
			} catch(SocketException ex){
				System.out.println(ex.toString());
			    try {
			    	log.writeLog("closing socket for" + converterId + " because of socketexception");
					socket.close();
					//monitor.removeDownloadrecevier(node);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
			    break;
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				break;
			}
		
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
