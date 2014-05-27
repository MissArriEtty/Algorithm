package test;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

public class RabbitmqTest {
	static String host = "10.20.2.44"; 
//	static String localhost = "127.0.0.1";
	static boolean durable = true;
	
	public static void main(String args[]) {

		try
		  {

			ConnectionFactory factory = new ConnectionFactory();
		    factory.setHost(host);
		    factory.setVirtualHost("/");
		    factory.setUsername("zwj");
		    factory.setPassword("zwj");
		    Connection connection = factory.newConnection();
		    Channel channel = connection.createChannel();
		    channel.queueDelete("watingQueue");
		    channel.queueDeclare("watingQueue", durable, false, false, null);
		    channel.close();
		    connection.close();
		}catch(java.io.IOException e){
			  e.printStackTrace();
		  }catch (Exception e){
			  e.printStackTrace();
		  }
	}
}
