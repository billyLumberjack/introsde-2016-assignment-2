package introsde.rest.ehealth.resources;
import introsde.rest.ehealth.model.MeasureTypes;

import java.util.List;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.PersistenceUnit;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

@Stateless // will work only inside a Java EE application
@LocalBean // will work only inside a Java EE application
@Path("/measureTypes")
public class MeasureTypesResource {    
	
	/***************************************************************
	 * 		GET REQUESTS
	 ***************************************************************/	
	
    // Return the list of measurements types into the db
    @GET
    @Produces({MediaType.TEXT_XML,  MediaType.APPLICATION_JSON ,  MediaType.APPLICATION_XML })
    public MeasureTypes getMeasureTypes() {
        return MeasureTypes.getDistinct();
    }
}