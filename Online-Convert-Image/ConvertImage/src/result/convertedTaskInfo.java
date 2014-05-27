package result;

public class convertedTaskInfo {
	private String requestId;
	private String taskName;
	private String status;
	private String filePath;
	
	public convertedTaskInfo(){
		
		requestId = "";
		taskName = "";
		status = "";
		filePath = "";	
	}

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
	
	public void setStatus(String status){
		this.status = status;
	}
	public String getStatus(){
		return status;
	}
	
	public void setFilePath(String filePath){
		this.filePath = filePath;
	}
	public String getFilePath(){
		return filePath;
	}

}
