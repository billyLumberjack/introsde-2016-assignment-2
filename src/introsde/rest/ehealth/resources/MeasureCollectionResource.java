package introsde.rest.ehealth.resources;
import introsde.rest.ehealth.model.Measure;
import introsde.rest.ehealth.model.Person;

import java.util.List;
import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.PersistenceUnit;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

@Stateless // will work only inside a Java EE application
@LocalBean // will work only inside a Java EE application
@Path("/measure")
public class MeasureCollectionResource {

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
    public List<Measure> getMeasuresBrowser() {
        System.out.println("Getting list of Measure...");
        List<Measure> measures = Measure.getAll();
        System.out.println(measures);
        return measures;
    }

    // Defines that the next path parameter after the base url is
    // treated as a parameter and passed to the PersonResources
    // Allows to type http://localhost:599/measure/1
    // 1 will be treaded as parameter todo and passed to PersonResource
    @GET
    @Path("{measureId}")
    @Produces({MediaType.TEXT_XML,  MediaType.APPLICATION_JSON ,  MediaType.APPLICATION_XML })
    public Measure getMeasure(@PathParam("measureId") int id) {
        return Measure.getOne(id);
    }
/*
    @POST
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    public Person newPerson(Person measure) throws IOException {
        System.out.println("Creating new measure...");            
        return Person.savePerson(measure);
    }

    // Defines that the next path parameter after the base url is
    // treated as a parameter and passed to the PersonResources
    // Allows to type http://localhost:599/base_url/1
    // 1 will be treaded as parameter todo and passed to PersonResource
    @Path("{measureId}")
    public PersonResource getPerson(@PathParam("measureId") int id) {
        return new PersonResource(uriInfo, request, id);
    }
   */
}