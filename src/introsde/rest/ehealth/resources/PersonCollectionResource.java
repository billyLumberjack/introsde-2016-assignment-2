package introsde.rest.ehealth.resources;
import introsde.rest.ehealth.model.Measure;
import introsde.rest.ehealth.model.Person;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.*;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Stateless // will work only inside a Java EE application
@LocalBean // will work only inside a Java EE application
@Path("/person")
public class PersonCollectionResource {
	
	/***************************************************************
	 * 		GET REQUESTS
	 ***************************************************************/
    
    // Return the list of people to the user in the browser
    // in case measureType, min and max are specified retrieve people whose measures match the query
    @GET
    @Produces({MediaType.TEXT_XML,  MediaType.APPLICATION_JSON ,  MediaType.APPLICATION_XML })
    public List<Person> getPeople(@Context UriInfo info) {
    	//retrieving the parameter
    	String typeStr = info.getQueryParameters().getFirst("measureType");
    	String minStr = info.getQueryParameters().getFirst("min");
    	String maxStr = info.getQueryParameters().getFirst("max");
    	
    	if(typeStr != null){
    		//measureType set
    		if(minStr != null){
    			//min set
    			if(maxStr != null){
    				// both min and max set 
    				System.out.println("getting from range");
        			return Person.getByRangeMeasure(
        					Integer.parseInt(minStr),
        					Integer.parseInt(maxStr),
        					typeStr
        					);
    			}
    			else{
    				//only min set
    				System.out.println("getting from min");
        			return Person.getByMinMeasure(Integer.parseInt(minStr),typeStr);    				
    			}
    		}else if(maxStr != null){
    			//min NOT set
    			//max set
    			System.out.println("getting from max");
    			return Person.getByMaxMeasure(Integer.parseInt(maxStr),typeStr);    			
    		}
    		// ok, there's something strange
    	}
        List<Person> people = Person.getAll();
        return people;
    }
    
    // get all the measures which belong to the person with a certain id and whose type is {measureType}
    // if other parameters are specified (after and before) retrieve just the measures falling in sych time interval 
    @GET
    @Path("{personId}/{measureType}")
    public List<Measure> getMeasures(
    		@Context UriInfo info,
    		@PathParam("personId") int personId,
    		@PathParam("measureType") String type,
    		@QueryParam("after") int init,
    		@QueryParam("before") int end) {
    	//check weather after and before are both set
    	if(info.getQueryParameters().getFirst("after") != null && info.getQueryParameters().getFirst("before") != null){
    		//both parameters set
    		return Measure.getByDate(personId, type, init, end);
    	}
    	else{
    		//retrieve just the measures list from that person
    		List<Measure> result = new ArrayList<Measure>();
    		for(Measure m : Person.getOne(personId).getMeasure()){
    			if(m.getType().equals(type))
    				result.add(m);
    			}
    		return result;    		
    	}
    }   
    
    //retrieve just a measure with certain PersonId, Type and Id 
    @GET
    @Path("{personId}/{measureType}/{measureId}")
    public Measure getMeasureById(@PathParam("personId") int personId,@PathParam("measureType") String type,@PathParam("measureId") int measureId) {
    	for(Measure m : Person.getOne(personId).getMeasure()){
    		if(m.getType().equals(type) && m.getId()==measureId)
    			return m;
    	}
		return null;
    }  
    
    // retrieve the person resource whose id matches the given one
    @GET
    @Path("{personId}")
    public Response getPerson(@PathParam("personId") int id) {
    	Person p = Person.getOne(id);
        if (p != null)
        {
        	//to return just the last measure for each type
        	p.cleanMeasures();
            return Response.status(Response.Status.OK).entity(p).build();
        }
        else
        {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

    }    
    
	/***************************************************************
	 * 		POST REQUESTS
	 ***************************************************************/    
    
    // insert in the db a new measure for the person whose id is {personId}
    // the new measure has type equal to {measureType}
    @POST
    @Path("{personId}/{measureType}")
    @Consumes({MediaType.TEXT_XML,  MediaType.APPLICATION_JSON ,  MediaType.APPLICATION_XML })
    @Produces({MediaType.TEXT_XML,  MediaType.APPLICATION_JSON ,  MediaType.APPLICATION_XML })
    public Measure newMeasure(Measure m, @PathParam("personId") int personId, @PathParam("measureType") String type){
    	System.out.println("Creating new measure...");
    	//preparing measure
    	m.setPersonId(personId);
    	m.setType(type);
    	//updating correspondent person
    	Person p = Person.getOne(personId);
    	p.getMeasure().add(m);
    	p = Person.updatePerson(p);
    	//getting response
    	System.out.println(m + "\n SAVED! \n");
    	return p.getMeasure().get(p.getMeasure().size()-1);
    }     
    
    // insert inside the database the person coming form the client's request
    @POST
    @Consumes({MediaType.TEXT_XML,  MediaType.APPLICATION_JSON ,  MediaType.APPLICATION_XML })
    @Produces({MediaType.TEXT_XML,  MediaType.APPLICATION_JSON ,  MediaType.APPLICATION_XML })
    public Person newPerson(Person p){
    	System.out.println("Creating new person...");
    	p = Person.savePerson(p);
    	return p;
    }   
    
	/***************************************************************
	 * 		PUT REQUESTS
	 ***************************************************************/      
    
    // edit the person resource whose if matches the given one
    // take the data fro the user input
    @PUT
    @Path("{personId}")
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Person putPerson(@PathParam("personId") int id, Person person) {
        System.out.println("--> Updating Person... " +id);
        return Person.updatePerson(person);
    } 
    
    // update the measure matching personId,Type and Id
    @PUT
    @Path("{personId}/{measureType}/{mid}")
    @Consumes({MediaType.TEXT_XML,  MediaType.APPLICATION_JSON ,  MediaType.APPLICATION_XML })
    @Produces({MediaType.TEXT_XML,  MediaType.APPLICATION_JSON ,  MediaType.APPLICATION_XML })
    public Measure putMeasure(@PathParam("personId") int personId,
    		@PathParam("measureType") String type,
    		@PathParam("mid") int measureId,
    		Measure m) {
        System.out.println("--> Updating Measure... " +measureId);
        //preparing m
        m.setPersonId(personId);
        m.setType(type);
        m.setId(measureId);
        //updating measure
        return Measure.updateMeasure(m);
    }     
    
	/***************************************************************
	 * 		DELETE REQUESTS
	 ***************************************************************/      
    
    // remove from the db the person whose id matches the given one
    @DELETE
    @Path("{personId}")
    public void deletePerson(@PathParam("personId") int id) {
        Person c = Person.getOne(id);
        if (c == null)
            throw new RuntimeException("Delete: Person with " + id + " not found");
        Person.removePerson(c);
    }      
       
}