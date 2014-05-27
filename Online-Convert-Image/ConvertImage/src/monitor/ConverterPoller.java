package monitor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import converter.Converter;

public class ConverterPoller implements Runnable {
	private Monitor monitor;
	private boolean shouldRun = true;
	
	public ConverterPoller(Monitor monitor) {
		this.monitor = monitor;
	}
	
	public void setShouldRun(boolean shouldRun){
		this.shouldRun = shouldRun;
	}
	
	public boolean getShouldRun() {
		return shouldRun;
	}
	
	@Override
	public void run() {
		while (shouldRun) {
			synchronized(monitor.converterList) {
				Iterator<Object[]> converterIt = monitor.converterList.iterator();
				ArrayList<Object[]> deadConverterList = new ArrayList<Object[]>();
				while (converterIt.hasNext()) {
					Object[] node = converterIt.next();
					long heartbeatTime = (long)node[0];
					String converterId = (String)node[1];
					if (System.currentTimeMillis() - heartbeatTime >= Monitor.HEARTBEAT_INTERVAL) {
						System.out.println("Converter " + converterId + " has been dead");
						monitor.dbUpdater.executeUpdate(monitor.ustmt, "Update ConverterInfo Set status = 'dead' Where nodeId = '" + converterId + "'");
						ResultSet rs = monitor.dbQuerier.executeQuery(monitor.qstmt, "Select * from requestTable where status = 'running' and runningConverterId = '" + converterId + "'");
						try {
							while (rs.next()) {
								String sql = "Update requestTable Set status = 'waiting', runningConverterId = '', restartCount = 0 Where requestId = '" + rs.getString("requestId") + "'";
								monitor.dbUpdater.executeUpdate(monitor.ustmt, sql);
							}
							rs.close();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						deadConverterList.add(node);
					}
				}
				
				Iterator<Object[]> deadIt = deadConverterList.iterator();
				while (deadIt.hasNext()) 
					monitor.converterList.remove(deadIt.next());
			}
			
			try {
				Thread.sleep(20000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
