package test;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ConverterConnectTest {
	public static void main(String[] args) {
		try {
			Socket socket = new Socket("10.222.77.51", 4321);
			System.out.println("connect monitor ok");
			DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
			dout.writeUTF("hello");
			dout.close();
			socket.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
