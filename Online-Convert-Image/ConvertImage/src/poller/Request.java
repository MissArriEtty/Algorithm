package poller;

import java.io.Serializable;

public class Request implements Serializable{
	private String requestId;
	private String MD5;
	private String userId;
	private String fileId;
	private String format;
	
	public Request(String requestId, String userId, String fileId, String MD5, String format){
		this.requestId = requestId;
		this.MD5 = MD5;
		this.userId = userId;
		this.fileId = fileId;
		this.format = format;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserId() {
		return userId;
	}
	
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	
	public String getRequestId(){
		return requestId;
	}
	
	public void setMD5(String MD5){
		this.MD5 = MD5;
	}
	
	public String getMd5(){
		return MD5;
	}
		
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	
	public String getFileId() {
		return fileId;
	}
	
	public void setFormat(String format) {
		this.format = format;
	}
	
	public String getFormat() {
		return format;
	}
}
