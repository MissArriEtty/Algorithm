package result;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="item")
public class convertingTaskInfo {
    private String requestId;
    private String taskName;
    private String commitTime;
    private String finishTime;
    private String status;
    private int errorCode;
    private String errorMsg;
    
    public convertingTaskInfo(){
    	
    	requestId = "";
    	taskName = "";
    	commitTime = "";
    	finishTime = "";
    	status = "";
    	errorCode = 0;
    	errorMsg = "";
    	
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
    
    public void setCommitTime(String commitTime){
    	this.commitTime = commitTime;
    }
    public String getCommitTime(){
    	return commitTime;
    }
    
    public void setFinishTime(String finishTime){
    	this.finishTime = finishTime;
    }
    public String getFinishTime(){
    	return finishTime;
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
