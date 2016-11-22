package introsde.rest.ehealth.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
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
	
	public Measure(String t, Double v, int d) {
		this.type = t;
		this.value = v;
		this.date = d;
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
	
    public static Measure saveMeasure(Measure m) {
        EntityManager em = MyDao.instance.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.persist(m);
        tx.commit();
        MyDao.instance.closeConnections(em);
        return m;
    } 	
    
    public static Measure updateMeasure(Measure m) {
        EntityManager em = MyDao.instance.createEntityManager(); 
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        m=em.merge(m);
        tx.commit();
        MyDao.instance.closeConnections(em);
        return m;
    }    

    public static List<Measure> getAll() {
        EntityManager em = MyDao.instance.createEntityManager();
        List<Measure> list = em.createQuery("SELECT m FROM Measure m", Measure.class).getResultList();
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
	
	public static List<Measure> getByDate(int id, String type,int a, int b) {
    	EntityManager em = MyDao.instance.createEntityManager();
    	List<Measure> mm =em.createQuery(
    	        "SELECT m FROM Measure m WHERE m.personId LIKE :identifier AND m.type LIKE :type AND m.date BETWEEN :init AND :end",Measure.class)
    	        .setParameter("init",a)
    	        .setParameter("end",b)
    	        .setParameter("identifier",id)
    	        .setParameter("type",type)
    	        .getResultList();
    	MyDao.instance.closeConnections(em);
    	return mm;
	}	

}