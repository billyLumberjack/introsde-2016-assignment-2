package introsde.rest.ehealth.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import introsde.rest.ehealth.dao.MyDao;


/**
 * The persistent class for the "Person" database table.
 * 
 */
@Entity
@Table(name="\"Person\"")
@NamedQuery(name="Person.findAll", query="SELECT p FROM Person p")

@XmlRootElement(name = "person")
@XmlAccessorType(XmlAccessType.FIELD)
public class Person implements Serializable {
	private static final long serialVersionUID = 1L;

	@XmlElement
	@Column(name="\"Birthdate\"")
	private int birthdate;

	@GeneratedValue(generator="sqlite_person")
	@TableGenerator(name="sqlite_person", table="sqlite_sequence",
	    pkColumnName="name", valueColumnName="seq", pkColumnValue="Person")
	@XmlElement
	@Column(name="\"id\"")	
	@Id private int id;

	@XmlElement
	@Column(name="\"Name\"")
	private String name;

	@XmlElement
	@Column(name="\"Surname\"")
	private String surname;
	
	
	// mappedBy must be equal to the name of the attribute in LifeStatus that maps this relation
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
	
	
	

	

	public Person() {
	}

	public int getBirthdate() {
		return this.birthdate;
	}

	public void setBirthdate(int birthdate) {
		this.birthdate = birthdate;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return this.surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

    public static List<Person> getAll() {
        EntityManager em = MyDao.instance.createEntityManager();
        List<Person> list = em.createNamedQuery("Person.findAll", Person.class).getResultList();
        MyDao.instance.closeConnections(em);
        return list;
    }
    
    public static Person getOne(int id){
        EntityManager em = MyDao.instance.createEntityManager();
        Person p = em.find(Person.class, id);
        MyDao.instance.closeConnections(em);
        return p;
    }
}