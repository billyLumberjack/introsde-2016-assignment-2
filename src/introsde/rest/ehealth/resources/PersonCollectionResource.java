package introsde.rest.ehealth.resources;
import introsde.rest.ehealth.model.Measure;
import introsde.rest.ehealth.model.Person;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.PersistenceUnit;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Stateless // will work only inside a Java EE application
@LocalBean // will work only inside a Java EE application
@Path("/person")
public class PersonCollectionResource {

    // Allows to insert contextual objects into the class,
    // e.g. ServletContext, Request, Response, UriInfo
    @Context
    UriInfo uriInfo;
    @Context
    Request request;

    // will work only inside a Java EE application
    @PersistenceUnit(unitName="introsde-assignment2")
    EntityManager entityManager;

    // will work only inside a Java EE application
    @PersistenceContext(unitName = "introsde-assignment2",type=PersistenceContextType.TRANSACTION)
    private EntityManagerFactory entityManagerFactory;    
    
    // Return the list of people to the user in the browser
    @GET
    @Produces({MediaType.TEXT_XML,  MediaType.APPLICATION_JSON ,  MediaType.APPLICATION_XML })
    public List<Person> getPersonsBrowser() {
        System.out.println("Getting list of people...");
        List<Person> people = Person.getAll();
        System.out.println(people);
        return people;
    }

    @POST
    @Consumes({MediaType.TEXT_XML,  MediaType.APPLICATION_JSON ,  MediaType.APPLICATION_XML })
    @Produces({MediaType.TEXT_XML,  MediaType.APPLICATION_JSON ,  MediaType.APPLICATION_XML })
    public Person newPerson(Person p){
    	System.out.println("Creating new person...");
    	p = Person.savePerson(p);
    	System.out.println(p + "\n SAVED! \n");
    	return p;
    }        
    
    // Defines that the next path parameter after the base url is
    // treated as a parameter and passed to the PersonResources
    // Allows to type http://localhost:599/person/1
    // 1 will be treaded as parameter todo and passed to PersonResource
    @GET
    @Path("{personId}")
    public Person getPerson(@PathParam("personId") int id) {
    	return Person.getOne(id);//.cleanMeasures();
    }
    
    @PUT
    @Path("{personId}")
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Response putPerson(@PathParam("personId") int id, Person person) {
        System.out.println("--> Updating Person... " +id);
        System.out.println("--> "+person.toString());
        Person.updatePerson(person);
        Response res;
        Person existing = Person.getOne(id);

        if (existing == null) {
            res = Response.noContent().build();
        } else {
            res = Response.created(uriInfo.getAbsolutePath()).build();
            person.setId(id);
            Person.updatePerson(person);
        }
        return res;
    } 
    
    @DELETE
    @Path("{personId}")
    public void deletePerson(@PathParam("personId") int id) {
        Person c = Person.getOne(id);
        if (c == null)
            throw new RuntimeException("Delete: Person with " + id + " not found");
        Person.removePerson(c);
    }
    
    @GET
    @Path("{personId}/{measureType}")
    public List<Measure> getPerson(@PathParam("personId") int id,@PathParam("measureType") String type) {
    	List<Measure> result = new ArrayList<Measure>();
    	for(Measure m : Person.getOne(id).getMeasure()){
    		if(m.getType().equals(type))
    			result.add(m);
    	}
    	return result;
    }   
    @GET
    @Path("{personId}/{measureType}/{measureId}")
    public Measure getPerson(@PathParam("personId") int personId,@PathParam("measureType") String type,@PathParam("measureId") int measureId) {
    	for(Measure m : Person.getOne(personId).getMeasure()){
    		if(m.getType().equals(type) && m.getId()==measureId)
    			return m;
    	}
		return null;
    }  
    
    @POST
    @Path("{personId}/{measureType}")
    @Consumes({MediaType.TEXT_XML,  MediaType.APPLICATION_JSON ,  MediaType.APPLICATION_XML })
    @Produces({MediaType.TEXT_XML,  MediaType.APPLICATION_JSON ,  MediaType.APPLICATION_XML })
    public Measure newMeasure(Measure m, @PathParam("personId") int personId, @PathParam("measureType") String type){
    	System.out.println("Creating new measure...");
    	//preparing measure
    	m.setPersonId(personId);
    	m.setType(type);
    	//updating corrispondent person
    	Person p = Person.getOne(personId);
    	p.getMeasure().add(m);
    	p = Person.updatePerson(p);
    	//getting response
    	System.out.println(m + "\n SAVED! \n");
    	return p.getMeasure().get(p.getMeasure().size()-1);
    }        
    
    // Defines that the next path parameter after the base url is
    // treated as a parameter and passed to the PersonResources
    // Allows to type http://localhost:599/person/1
    // 1 will be treaded as parameter todo and passed to PersonResource
    /*
    @GET
    @Path("{personId}")
    public Person getPerson(@PathParam("personId") int id) {
    	return Person.getOne(id).cleanMeasures();
    } 
    */   
}