package result;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="addTask")
public class addResult {
	
	private int flag;
	private String message;
	
	public addResult(int flag, String message){
		
		this.flag = flag;
		this.message = message;
	}
	
	public addResult(){
		
		this.flag = 0;
		this.message = "";
	}
	
	public int getFlag(){
		
		return flag;
	}
	
	public String getMessage(){
		
		return message;
	}
	
	public void setFlag(int flag){
		
		this.flag = flag;
	}
	
	public void setMessage(String message){
		
		this.message = message;
	}

}
