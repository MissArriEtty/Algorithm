/**
 *  对rabbitmq消息队列进行request插入操作
 *  rabbitmq 默认占用端口 5672
 *  需要引入 rabbitmq libcommon jar包
 *  @author arrietty
 */
package rabbitmq;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import logPackage.cloudLog;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import poller.Request;


public class requestEnqueue {
	
	public static final boolean durable=true;
	public static String host = "10.20.2.44";
//	public static String host = (new Configuration("monitor-conf.xml")).getConf("rabbitmqIP");
	
	public static logPackage.cloudLog log = new cloudLog("rabbitmq.log");
	
	public static void enQueue(Request request, String queueName){
		
		try {
			
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost(host);
			factory.setVirtualHost("/");
			factory.setUsername("zwj");
			factory.setPassword("zwj");
			
			Connection conn = factory.newConnection();
			Channel channel = conn.createChannel();
			channel.queueDeclare(queueName, durable, false, false, null);
			channel.basicPublish("", queueName, MessageProperties.PERSISTENT_TEXT_PLAIN, ObjectToByte(request));
				
			channel.close();
			conn.close();
			
			
		} catch (Exception e) {
			
			log.writeLog("[requestEnqueue](enQueue): Error " + e.getMessage());
			
		}
		
	}

	private static byte[] ObjectToByte(Request request) {
		byte[] bytes = new byte[1024];
		
		try {
			
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			ObjectOutputStream oo = new ObjectOutputStream(bo);
			oo.writeObject(request);
			bytes = bo.toByteArray();
			
			bo.close();
			oo.close();
			
		} catch (IOException e) {
			log.writeLog("[requestEnqueue](enQueue): Error " + e.getMessage());
		}
		return bytes;

	}
	

}
