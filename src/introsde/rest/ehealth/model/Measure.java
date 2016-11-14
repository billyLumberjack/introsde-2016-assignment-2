package introsde.rest.ehealth.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import introsde.rest.ehealth.dao.MyDao;


/**
 * The persistent class for the "Measure" database table.
 * 
 */
@XmlRootElement(name="measure")
@XmlAccessorType(XmlAccessType.FIELD)

@Entity
@Table(name="\"Measure\"")
@NamedQuery(name="Measure.findAll", query="SELECT m FROM Measure m")
public class Measure implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@GeneratedValue(generator="sqlite_measure")
	@TableGenerator(name="sqlite_measure", table="sqlite_sequence",
	    pkColumnName="name", valueColumnName="seq", pkColumnValue="Measure")
	@Column(name="\"id\"")
	@XmlElement
	@Id private int id;	

	@XmlElement
	@Column(name="\"Date\"")
	private int date;

	@XmlElement
	@Column(name="\"Type\"")
	private String type;

	@XmlElement
	@Column(name="\"Value\"")
	private Double value;
	
	@XmlElement
	@Column(name="\"PersonId\"")
	private int personId;	
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="\"PersonId\"" , insertable=false, updatable=false)
	@XmlTransient
	@JsonIgnore
	private Person person;
	
	public Person getPerson() {
	    return person;
	}
	
	public void setPerson(Person person) {
		this.person = person;
	}
	
	
	 

	public Measure() {
	}

	public int getDate() {
		return this.date;
	}

	public void setDate(int date) {
		this.date = date;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Double getValue() {
		return this.value;
	}

	public void setValue(Double value) {
		this.value = value;
	}


	public int getPersonId() {
		return personId;
	}


	public void setPersonId(int personId) {
		this.personId = personId;
	}

    public static List<Measure> getAll() {
        EntityManager em = MyDao.instance.createEntityManager();
        List<Measure> list = (List<Measure>) em.createQuery("SELECT m FROM Measure m").getResultList();
        MyDao.instance.closeConnections(em);
        return list;
    }
	
	public static Measure getOne(int id) {
    	EntityManager em = MyDao.instance.createEntityManager();
    	Measure m = (Measure) em.createQuery(
    	        "SELECT m FROM Measure m WHERE m.id LIKE :identifier")
    	        .setParameter("identifier",id).getSingleResult();
    	MyDao.instance.closeConnections(em);
    	return m;
	}

}