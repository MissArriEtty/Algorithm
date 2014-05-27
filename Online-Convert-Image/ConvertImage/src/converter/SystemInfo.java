//////////////////////////////////////////////////////////////////////////  
///        COPYRIGHT NOTICE  
///        Copyright (c) 2011, 上海电信 
///        All rights reserved.  
///  
/// @file SystemInfo.java  
/// @brief 本文件包含获取系统硬件信息的一些方法。
///  
///     主要包含获取内存信息、CPU占用率等转码节点的硬件信息的方法。
///  
/// @version 1.0
/// @author 易源
/// @date 2011年10月
///  
///  
///        修订说明：最初版本  
//////////////////////////////////////////////////////////////////////////

package converter;

import java.io.IOException;
import java.lang.management.ManagementFactory;
/*
import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import jpcap.NetworkInterfaceAddress;
import jpcap.PacketReceiver;
import jpcap.packet.Packet;
*/
import com.sun.management.OperatingSystemMXBean;

/** 本类的功能：提供系统的硬件信息
*
* 包括CPU占用率、内存使用等
*/
public class SystemInfo {
	/** 获取硬件信息的JAVA对象 */
	private OperatingSystemMXBean osmb;
	/** 用于CPU占用率计算，表示开始运行 */
	private boolean firstRun;
	/** 上次系统时间 */
	private long lastSystemTime;
	/** 上次CPU时间 */
	private long lastProcessCpuTime;

	/** 构造函数
	 */
	public SystemInfo() {
		osmb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
		firstRun = true;
	}

	/** 获取总物理内存
	 * @return 总物理内存
	 */
	public long getTotalPhysicalMemorySize() {
		return osmb.getTotalPhysicalMemorySize();
	}

	/** 获取剩余物理内存
	 * @return 剩余物理内存
	 */
	public long getFreePhysicalMemorySize() {
		return osmb.getFreePhysicalMemorySize();
	}

	/** 计算CPU占用率
	 * @return CPU占用率
	 */
	public double getCpuRatio() {
		double cpuRatio = 0;
		long currentSystemTime;
		long currentProcessCpuTime;

		if (firstRun) {
			firstRun = false;
			lastSystemTime = System.nanoTime();
			lastProcessCpuTime = osmb.getProcessCpuTime();
			return 0.0;
		}
		currentSystemTime = System.nanoTime();
		currentProcessCpuTime = osmb.getProcessCpuTime();

		cpuRatio = (double) (currentProcessCpuTime - lastProcessCpuTime) / (double) (currentSystemTime - lastSystemTime);

		lastSystemTime = currentSystemTime;
		lastProcessCpuTime = currentProcessCpuTime;

		return cpuRatio / (double) osmb.getAvailableProcessors();
	}
	/*
	// used for obtaining bandwidth
	private int packetNum;
	private long dataSize;
	private static final int CAPTURE_TIME = 100;
	public class PacketHandler implements PacketReceiver {
		
		public void receivePacket(Packet packet) {
			packetNum++;
			dataSize += packet.len;
			//System.out.println(packet);
			//System.out.println(packetNum + ":" + packet.len + " | total: " + dataSize);
		}
	}
	
	public double getBandwidth(String ip) throws IOException {
		JpcapCaptor captor = null;
		NetworkInterface[] nis = JpcapCaptor.getDeviceList();
		
		label:
		for(NetworkInterface ni : nis) {
			for(NetworkInterfaceAddress addr : ni.addresses) {
				if(addr.address.getHostAddress().equals(ip)) {
					captor = JpcapCaptor.openDevice(ni, 65535, false, CAPTURE_TIME);
					break label;
				}
			}
		}
		captor.setNonBlockingMode(false);
		long time1 = System.currentTimeMillis();
		captor.processPacket(-1, new PacketHandler());
		long time2 = System.currentTimeMillis();
		//captor.updateStat();
		//System.out.println(captor.received_packets);
		captor.close();
		
		return (double) dataSize / ((double) (time2 - time1) / 1000.0);
	}*/
}
