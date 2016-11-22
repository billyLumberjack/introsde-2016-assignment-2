package introsde.rest.ehealth.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import introsde.rest.ehealth.dao.MyDao;


@XmlRootElement(name="measureTypes")
@XmlAccessorType(XmlAccessType.FIELD)

public class MeasureTypes implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//@XmlElementWrapper(name="measureType")
	private List<String> measureType;
	
	public MeasureTypes() {}

	public List<String> getMeasureType() {
		return measureType;
	}

	public void setMeasureType(List<String> measureType) {
		this.measureType = measureType;
	}
	
	public static MeasureTypes getDistinct(){
        EntityManager em = MyDao.instance.createEntityManager();
        MeasureTypes mmtt = new MeasureTypes();
        
        mmtt.setMeasureType(em.createQuery("select DISTINCT(m.type) from Measure m").getResultList());
        
		return mmtt;
	}
	
	
}