/**
 *  对rabbitmq消息队列进行request出队操作
 *  rabbitmq 默认占用端口 5672
 *  需要引入 rabbitmq libcommon jar包
 *  @author arrietty
 */
package rabbitmq;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import logPackage.cloudLog;
import poller.Request;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;

public class requestDequeue {
	
	public static final boolean durable=true;
	public static String host = "10.20.2.44";
//	public static String host = (new Configuration("monitor-conf.xml")).getConf("rabbitmqIP");
	
	public static logPackage.cloudLog log = new cloudLog("rabbitmq.log");
	
	public static Request deQueue(String queueName){
		
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(host);
		factory.setVirtualHost("/");
		factory.setUsername("zwj");
		factory.setPassword("zwj");
		
		Connection conn = null;
		Channel channel = null;
		GetResponse gr = null;
		
		try {
			conn = factory.newConnection();
			channel = conn.createChannel();
			int prefetchCount = 1;
			
			do{
				channel.basicQos(prefetchCount);
				channel.queueDeclare(queueName, durable, false, false, null);
				gr = channel.basicGet(queueName, true);
				
				if(gr == null){
					Thread.sleep(5000);
				}
				
			}while(gr == null);
			
		} catch (IOException e) {
			
			log.writeLog("[requestDequeue](deQueue): Error " + e.getMessage());
		} catch (InterruptedException e) {
			
			log.writeLog("[requestDequeue](deQueue): Error " + e.getMessage());
		}finally{
			try {
				
				channel.close();
				conn.close();
				
			} catch (IOException e) {
				log.writeLog("[requestDequeue](deQueue): Error " + e.getMessage());
			}
		}
		
		if(gr != null){
			return ByteToObject(gr.getBody());
		}else{
			return null;
		}
		
	}

	private static Request ByteToObject(byte[] body) {
		
		Request request = null;
		
		try {
			ByteArrayInputStream bi = new ByteArrayInputStream(body);
			ObjectInputStream oi = new ObjectInputStream(bi);
			request = (Request) oi.readObject();
			
			bi.close();
			oi.close();
			
		} catch (IOException e) {
			
			log.writeLog("[requestDequeue](deQueue): Error " + e.getMessage());
		} catch (ClassNotFoundException e) {
			
			log.writeLog("[requestDequeue](deQueue): Error " + e.getMessage());
		}
		
		return request;
	}

}
