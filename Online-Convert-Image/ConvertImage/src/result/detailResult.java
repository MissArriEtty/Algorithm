package result;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;



@XmlRootElement(name="details")
public class detailResult {
	
    @XmlElement(name = "item")
    private List<detailResultInfo> list;
	
	public detailResult(){
		list = new ArrayList<detailResultInfo>();
	}
	
	public List<detailResultInfo> getList(){
		return list;
	}
	
	public void putTask(detailResultInfo dri){
		list.add(dri);
	}
}
