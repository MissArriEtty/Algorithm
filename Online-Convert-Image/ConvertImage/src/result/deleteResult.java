package result;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="deleteTask")
public class deleteResult {
	
	//删除成功flag=1, msg=success;否则flag=0，msg为错误码
	private int flag;
	private String msg;
	
	public deleteResult(int flag, String msg){
		this.flag = flag;
		this.msg = msg;
	}
	
	public deleteResult(){
		flag = -1;
		msg = "";
	}
	
	public void setFlag(int flag){
		this.flag = flag;
	}
	public int getFlag(){
		return flag;
	}
	
	public void setMsg(String msg){
		this.msg = msg;
	}
	public String getMsg(){
		return msg;
	}
	

}
