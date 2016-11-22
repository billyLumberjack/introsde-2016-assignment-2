package introsde.rest.ehealth.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "measures")
@XmlAccessorType(XmlAccessType.FIELD)
public class Measures {
	
	private List<Measure> measure;

	public List<Measure> getMeasure() {
		return measure;
	}

	public void setMeasure(List<Measure> measure) {
		this.measure = measure;
	}


}
