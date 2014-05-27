package result;


public class detailResultInfo {
	
	private String requestId;
	private String taskName;
	private String filePath;
	private String status;
	private int errorCode;
	private String errorMsg;
	
	public void setRequestId(String requestId){
		this.requestId = requestId;
	}
	public String getRequestId(){
		return requestId;
	}
	
	public void setTaskName(String taskName){
		this.taskName = taskName;
	}
	public String getTaskName(){
		return taskName;
	}
	
	public void setFilePath(String filePath){
		this.filePath = filePath;
	}
	public String getFilePath(){
		return filePath;
	}
	
	public void setStatus(String status){
		this.status = status;
	}
	public String getStatus(){
		return status;
	}
	
	public void setErrorCode(int errorCode){
		this.errorCode = errorCode;
	}
	public int getErrorCode(){
		return errorCode;
	}
	
	public void setErrorMsg(String errorMsg){
		this.errorMsg = errorMsg;
	}
	public String getErrorMsg(){
		return errorMsg;
	}
}
