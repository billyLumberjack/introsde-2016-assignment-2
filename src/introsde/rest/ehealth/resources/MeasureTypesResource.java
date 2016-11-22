package introsde.rest.ehealth.resources;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import introsde.rest.ehealth.model.MeasureTypes;

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