package introsde.rest.ehealth.model;

public class HealthProfile {
	// mappedBy must be equal to the name of the attribute in LifeStatus that maps this relation
	/*
	@OneToMany(mappedBy="person")
	@XmlElementWrapper(name="measures")
	@JsonIgnore
	private List<Measure> measure;
	
	@XmlTransient
	public List<Measure> getMeasure() {
	    return measure;
	}
	
	public void setMeasure(List<Measure> lm){
		this.measure = lm;
	}
	*/
}
