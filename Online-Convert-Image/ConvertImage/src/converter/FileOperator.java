//////////////////////////////////////////////////////////////////////////  
///        COPYRIGHT NOTICE  
///        Copyright (c) 2011, 上海电信 
///        All rights reserved.  
///  
/// @file FileOperator.java  
/// @brief 本文件为文件操作类，包括一些文件操作方法删除文件、打包文件等功能。
///  
///     文件操作类，主要包括删除文件、获取文件和目录大小等功能，为转码节点的其
/// 他模块提供服务。
///  
/// @version 1.0
/// @author 易源
/// @date 2011年10月
///  
///  
///        修订说明：最初版本  
//////////////////////////////////////////////////////////////////////////

package converter;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

/** 本类的功能：提供一些文件操作功能
 * 
 * 文件操作功能包括删除文件、获取文件大小等
 */
public class FileOperator {

	/** 删除文件
	 * @param path 文件路径
	 * @throws IOException
	 */
	public static void deleteFile(String path) throws IOException {
		/*Runtime rt = Runtime.getRuntime();
		rt.exec("rm -rf \"" + path + "\"");*/
		File file = new File(path);
		FileUtils.forceDelete(file);
	}
	
	/** 获取文件大小
	 * @param filePath 文件路径
	 * @return 文件大小
	 */
	public static long getFileSize(String filePath) {
		return new File(filePath).length();
	}
	
	/** 获取目录下所有文件大小总和
	 * @param folderPath 目录路径
	 * @return 目录下所有文件总大小
	 * @throws IOException
	 */
	public static long getFolderSize(String folderPath) throws IOException {
		long size = 0;
		File item;
		File folder = new File(folderPath);
		if(!folder.isDirectory()) {
			return folder.length();
		}
		String files[] = folder.list();
		for(int i = 0; i < files.length; i++) {
			item = new File(folder.getCanonicalPath() + File.separator + files[i]);
			if(item.isDirectory()) {
				size += getFolderSize(folderPath + File.separator + files[i]);
			} else {
				size += item.length();
			}
		}
		return size;
	}
}
