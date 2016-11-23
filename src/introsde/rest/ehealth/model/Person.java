package introsde.rest.ehealth.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnore;

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
	@OneToMany(cascade = CascadeType.ALL, mappedBy="person")
	@XmlElementWrapper(name="measure")
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
	
	public void cleanMeasures(){
		/*
	     Person p=new Person();
	     p.setBirthdate(this.birthdate);
	     p.setId(this.id);
	     p.setName(this.name);
	     p.setSurname(this.surname);
	     */
	     
	     //now I iterate and I report only the more recent for each type
	     List<String>  matchString = new ArrayList<String>();
	     List<Measure> newMeasure = new ArrayList<Measure>();
	     newMeasure.clear();
	     
	     for(int i=0; i< this.measure.size();i++){
	    	 if(!matchString.contains(this.measure.get(i).getType())){
	    		 matchString.add(this.measure.get(i).getType());
	    		 newMeasure.add(this.measure.get(i));
	    		 }
	    	 else
	    	 {
	    		 if(this.measure.get(i).getDate()>newMeasure.get(matchString.indexOf(this.measure.get(i).getType())).getDate())
	    			 newMeasure.set(matchString.indexOf(this.measure.get(i).getType()), this.measure.get(i));
	    		 }
	    	}
	     
	     this.measure = newMeasure;
	     
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
    
    
    public static Person updatePerson(Person p) {
        EntityManager em = MyDao.instance.createEntityManager(); 
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        p=em.merge(p);
        tx.commit();
        MyDao.instance.closeConnections(em);
        return p;
    }    
    


    public static Person savePerson(Person p) {
        EntityManager em = MyDao.instance.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.persist(p);
        
        for(Measure m : p.getMeasure()){
        	m.setPersonId(p.getId());
        	em.merge(m);
        }
        p = em.merge(p);
        
        
        
        tx.commit();
        MyDao.instance.closeConnections(em);
        return p;
    } 
    
    public static void removePerson(Person p) {
        EntityManager em = MyDao.instance.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        p=em.merge(p);
        em.remove(p);
        tx.commit();
        MyDao.instance.closeConnections(em);
    }
    //select b.fname, b.lname from Users b JOIN b.groups c where c.groupName = :groupName 
    public static List<Person> getByRangeMeasure(Double min,Double max,String t){
    	EntityManager em = MyDao.instance.createEntityManager();
    	
    	String queryStr = "select * from Person where Id in ("
    			+ "select PersonId from ("
    			+ "select PersonId,Date,Value from Measure where Type like ?"
    			+ ") group by PersonId having Date like max(Date) and Value between ? and ?)";
    	Query q = em.createNativeQuery(queryStr, Person.class)
    			.setParameter(1, t)
    			.setParameter(2, min)
    			.setParameter(3, max);
    	
    	@SuppressWarnings("unchecked")
		List<Person> pp = q.getResultList();
    	
    	for(int c=0; c<pp.size(); c++){
    		pp.get(c).cleanMeasures();
    	}
    	
    	MyDao.instance.closeConnections(em);
    	return pp;
    }
    public static List<Person> getByMinMeasure(Double min,String t){
    	EntityManager em = MyDao.instance.createEntityManager();
    	
    	String queryStr = "select * from Person where Id in ("
    			+ "select PersonId from ("
    			+ "select PersonId,Date,Value from Measure where Type like ?"
    			+ ") group by PersonId having Date like max(Date) and Value >= ?)";
    	Query q = em.createNativeQuery(queryStr, Person.class)
    			.setParameter(1, t)
    			.setParameter(2, min);
    	
    	@SuppressWarnings("unchecked")
		List<Person> pp = q.getResultList();
    	
    	for(int c=0; c<pp.size(); c++){
    		pp.get(c).cleanMeasures();
    	}
    	
    	MyDao.instance.closeConnections(em);
    	return pp;
    }
    public static List<Person> getByMaxMeasure(Double max,String t){
    	EntityManager em = MyDao.instance.createEntityManager();
    	
    	String queryStr = "select * from Person where Id in ("
    			+ "select PersonId from ("
    			+ "select PersonId,Date,Value from Measure where Type like ?"
    			+ ") group by PersonId having Date like max(Date) and Value <= ?)";
    	Query q = em.createNativeQuery(queryStr, Person.class)
    			.setParameter(1, t)
    			.setParameter(2, max);
    	
    	@SuppressWarnings("unchecked")
		List<Person> pp = q.getResultList();
    	
    	for(int c=0; c<pp.size(); c++){
    		pp.get(c).cleanMeasures();
    	}
    	
    	MyDao.instance.closeConnections(em);
    	return pp;
    }    
}