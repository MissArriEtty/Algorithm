package logPackage;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class cloudLog {
	
	private String logName;
	private Logger log;
	private FileHandler fileHandler;
	
	public cloudLog(String logName){
		
		this.logName = logName;
		log = Logger.getLogger(logName);
		
		String path = System.getProperty("user.dir");
		File logDir = new File(path,"log");
		if(!logDir.exists())
			logDir.mkdir();
	    System.out.println("log目录地址" + logDir.getPath());
		
		//在log目录下新建文件
		File logFile = new File(logDir.getPath(), logName);
		
		try{
			fileHandler = new FileHandler(logFile.getPath(),true);
			
		}catch(SecurityException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
		fileHandler.setFormatter(new myLogHander());
		log.addHandler(fileHandler);
		
	}
	
	public void writeLog(String logInfo){
		
		log.info(new Date() + ": " + logInfo + "\r\n");
	}

	private class myLogHander extends Formatter{
	
		@Override
		public String format(LogRecord logRecord){
			return logRecord.getLevel() + ":" + logRecord.getMessage()+"\n"; 
		}
	}
}
