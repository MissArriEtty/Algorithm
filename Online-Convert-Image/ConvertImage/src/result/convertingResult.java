package result;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="showConverting")
public class convertingResult {
	
	@XmlElement(name="item")
	private List<convertingTaskInfo> list;
	
	public convertingResult(){
		list = new ArrayList<convertingTaskInfo>();
	}
	
	public List<convertingTaskInfo> getList(){
		return list;
	}
	
	public void putTask(convertingTaskInfo cti){
		list.add(cti);
	}
	

}
