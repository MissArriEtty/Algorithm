
/***************************************************
 *     
 *    File Notice
 *     
 *    将conf目录下指定的xml文件中属性hash到内存 
 *  
 *******************************************************/

package config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class configuration {
	
	private HashMap<String,String> property_map;
	
	/**
	 * 
	 * Config 将xml中的 <name> <value> 存入 private HashMap<String,String> property_map中
	 * 
	 * @param fileName xml文件名
	 * @throws JDOMException 
	 */
	public configuration(String fileName){
		
		property_map = new HashMap<String,String>();
		
		//取程序当前目录
		String path=System.getProperty("user.dir");
		System.out.println(path);
		
		//读取conf文件目录
		File confDir = new File(path,"conf");
		File confFile = new File(confDir.getName(), fileName);
//		System.out.println(confFile.getName());
		
		SAXBuilder sb = new SAXBuilder();
		try{
			
			String confFileStr = confFile.getAbsolutePath();
//			System.out.println(confFileStr);
			
			File file = new File(confFileStr);
			InputStream in = new FileInputStream(file);
			Document doc = sb.build(in);
			Element root = doc.getRootElement();
//			System.out.println("root = " + root.toString());
			List build = root.getChildren();
			Element firstLevel = null;
			
//			System.out.println("before loop...");
			for (int i = 0; i < build.size(); i++)
			{
				firstLevel = (Element)build.get(i);
				String propertyName = firstLevel.getChildText("name").toLowerCase();
				String propertyValue = firstLevel.getChildText("value");
				property_map.put(propertyName, propertyValue);
				
			}
			
		}catch(IOException e){
			System.out.println(e.getMessage());
			
		}
		catch(JDOMException e)
		{
			System.out.println(e.getMessage());
		}
		
		
		
		
	}

	/**
	 * 
	 * hash查找key对应的<value>项
	 * @param key xml配置文件中属性的键值
	 * @return xml中key对应的alue
	 * 
	 */
	public String getConf(String key) {
		// TODO Auto-generated method stub
		return property_map.get(key.toLowerCase());
	}
	
	
	//test
	public static void main(String[] args)
	{
		String filename = "test-conf";
	    try
	    {
		    configuration conf = new configuration(filename);
		    
	    }catch(Exception e)
	    {
	    	e.printStackTrace();
	    }
	}
	

}




