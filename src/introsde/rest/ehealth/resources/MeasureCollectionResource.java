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
	/***************************************************************
	 * 		GET REQUESTS
	 ***************************************************************/
	//returns the entire list of measures present in the db
    @GET
    @Produces({MediaType.TEXT_XML,  MediaType.APPLICATION_JSON ,  MediaType.APPLICATION_XML })
    public List<Measure> getMeasuresBrowser() {
        System.out.println("Getting list of Measure...");
        List<Measure> measures = Measure.getAll();
        return measures;
    }

    //return one measure by its id
    @GET
    @Path("{measureId}")
    @Produces({MediaType.TEXT_XML,  MediaType.APPLICATION_JSON ,  MediaType.APPLICATION_XML })
    public Measure getMeasureById(@PathParam("measureId") int id) {
        return Measure.getOne(id);
    }
}