package result;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Converted")
public class convertedResult {
	
	@XmlElement(name = "item")
	private List<convertedTaskInfo> list;
	
	public convertedResult(){
		list = new ArrayList<convertedTaskInfo>();
	}
	
	public List<convertedTaskInfo> getList(){
		return list;
	}
	
	public void putTask(convertedTaskInfo cti){
		list.add(cti);
	}

}
