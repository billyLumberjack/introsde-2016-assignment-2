package introsde.rest.ehealth.client;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.glassfish.jersey.client.ClientConfig;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

import introsde.rest.ehealth.model.Measure;
import introsde.rest.ehealth.model.MeasureTypes;
import introsde.rest.ehealth.model.Measures;
import introsde.rest.ehealth.model.People;
import introsde.rest.ehealth.model.Person;

@SuppressWarnings("unchecked")
public class Client {
	
	private static final String ENDPOINT = "https://introsde-sm-assignment2.herokuapp.com/rest/";

    private static final URI SERVER_URI = UriBuilder.fromUri(ENDPOINT).build();
    private static final WebTarget SERVER = ClientBuilder.newClient(new ClientConfig()).target(SERVER_URI);	
    private static String mt;
    // log variables
    private static int requestNumber;
    private static String httpMethodStr, endpoint, acceptStr, contentTypeStr, result;
    static PrintWriter writer;
	
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
    	String log;
    	String [] types = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML}; 
    	for(int manna=0; manna<types.length; manna++){
			mt = types[manna];
			if(manna==0)
				writer = new PrintWriter("client-server-json.log", "UTF-8");
			else
				writer = new PrintWriter("client-server-xml.log", "UTF-8");
			//get the entire list of people in the db
			//step 3.1
			List<Person> pp = retrievePeople();
			//check whether the list of people in the db contains more than 2 objects
			if(pp.size() > 2)
				result = "OK";
			else
				result ="ERROR";
			// assign to the relative variables the first and last id found
			int first_person_id = pp.get(0).getId();
			int last_person_id = pp.get(pp.size()-1).getId();
			//step 3.2
			Person p = retrievePersonById(first_person_id);
			//step 3.3
			p.setName("Samuele");
			p = updatePerson(p, first_person_id);
			//step 3.4
			//creating person to insert
			p = new Person();
			p.setName("Chuck");
			p.setSurname("Norris");
			p.setBirthdate(-788918400);
			List<Measure> cnMeasures = new ArrayList<Measure>(
					Arrays.asList(new Measure("weight", 78.9, 000),new Measure("height", 172.0, 000))
					);
			p.setMeasure(cnMeasures);
			p = insertPerson(p);
			//step 3.5
			removePersonById(p.getId());
			if(retrievePersonById(p.getId()) == null){
				result = "OK";
				// print the log
				log = createLogStr("", 404);
				System.out.println(log);
				writer.println(log);
			}
			//step 3.6
			List<String> measureTypes = retrieveMeasureTypes().getMeasureType();
			//step 3.7
			List<Measure> firstPersonMeasures = new ArrayList<Measure>();
			List<Measure> lastPersonMeasures = new ArrayList<Measure>();
			for(String type : measureTypes){
				firstPersonMeasures.addAll(retrieveMeasureByPersonIdByType(first_person_id,type));
				lastPersonMeasures.addAll(retrieveMeasureByPersonIdByType(last_person_id,type));
			}
			if(firstPersonMeasures.size() > 0 && lastPersonMeasures.size()>0){
				result = "OK";
				// print the log
				log = createLogStr("", 404);
				System.out.println(log);
				writer.println(log);
			}
			
			int measure_id = firstPersonMeasures.get(0).getId();
			String measureType = firstPersonMeasures.get(0).getType();
			//step 3.8
			Measure m = retrieveMeasureByPersonIdByTypeById(first_person_id, measureType, measure_id);
			//step 3.9
			int firt_person_mm_size_old = retrieveMeasureByPersonIdByType(first_person_id, measureType).size();
			m = new Measure();
			m.setDate(1315785600);
			m.setValue(72.0);
			//R#8
			m = insertMeasureByPersonIdByType(m, first_person_id, measureType);
			//R#6
			int firt_person_mm_size_new = retrieveMeasureByPersonIdByType(first_person_id, measureType).size();
			if(firt_person_mm_size_new - firt_person_mm_size_old == 1)
				result = "OK";
			else
				result = "ERROR";
			
			// print the log
			log = createLogStr("", 200);
			System.out.println(log);
			writer.println(log);
			
			//step 3.10
			m.setValue(31.4);
			m = updateMeasureByPersonIdByTypeById(m, m.getPersonId(), m.getType(), m.getId());
			Measure mNew = retrieveMeasureByPersonIdByTypeById(m.getPersonId(), m.getType(), m.getId());
			if(mNew.getValue() == 31.4)
				result = "OK";
			else
				result = "ERROR";
	
			// print the log
			log = createLogStr("", 200);
			System.out.println(log);
			writer.println(log);
			//step 3.11
			retrieveMeasuresByTimestamp(mNew.getPersonId(), mNew.getType(), mNew.getDate()-100, mNew.getDate()+100);
			//step 3.12
			retrievePersonByMeasureTypeAndValue(mNew.getType(), mNew.getValue()-10, mNew.getValue()+10);	
			writer.close();
    	}
	}
	
	
	private static List<Person> retrievePeople(){
		requestNumber = 1;
		try{
			//prepare and make the call
			endpoint = "/person";
			httpMethodStr = "GET";
			
			Response response = SERVER.path(endpoint).request().accept(mt).get();
			String data = response.readEntity(String.class);
			
			if(response.getStatus() == 200)
				result="OK";
			else
				result="ERROR";
			
			// print the log
			String log = createLogStr(data, response.getStatus());
			System.out.println(log);
			writer.println(log);
			
			People people;
			if(mt.equals(MediaType.APPLICATION_JSON))
				people = (People) unmarshallJSON(data,People.class);
			else
				people = (People) unmarshallXML(data,People.class);
	        
	        return  	people.getPerson();			
			
			//return people.getPerson();
			
		}catch(Exception e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	private static Person retrievePersonById(int id){
		requestNumber = 2;
		try{
			//prepare and make the call
			endpoint = "/person/"+id;
			httpMethodStr = "GET";
			
			Response response = SERVER.path(endpoint).request().accept(mt).get();
			String data = response.readEntity(String.class);
			
			if(response.getStatus() == 200 || response.getStatus() == 202)
				result = "OK";
			else
				result = "ERROR";
			
			
			if(response.getStatus() == 404)
				return null;
			
			// print the log
			String log = createLogStr(data, response.getStatus());
			System.out.println(log);
			writer.println(log);
			
			Person p;
			if(mt.equals(MediaType.APPLICATION_JSON))
				p = (Person) unmarshallJSON(data,Person.class);
			else
				p= (Person) unmarshallXML(data,Person.class);
			return p;
			
		}catch(Exception e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return null;
	}

	private static List<Person> retrievePersonByMeasureTypeAndValue(String type, double d, double f){
		requestNumber = 12;
		try{
			//prepare and make the call
			endpoint = "/person";
			httpMethodStr = "GET";
			
			Response response = SERVER.path(endpoint)
					.queryParam("measureType", type)
					.queryParam("min", d)
					.queryParam("max", f)
					.request()
					.accept(mt).get();
			String data = response.readEntity(String.class);
			
			People ppl;
			if(mt.equals(MediaType.APPLICATION_JSON))
				ppl = (People) unmarshallJSON(data,People.class);
			else
				ppl= (People) unmarshallXML(data,People.class);
			
			if(ppl.getPerson().size() > 0 && response.getStatus()==200)
				result = "OK";
			else
				result = "ERROR";
			
			// print the log
			String log = createLogStr(data, response.getStatus());
			System.out.println(log);
			writer.println(log);	
			
			return ppl.getPerson();
		}catch(Exception e){
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	private static MeasureTypes retrieveMeasureTypes(){
		requestNumber = 9;
		try{
			//prepare and make the call
			endpoint = "/measureTypes";
			httpMethodStr = "GET";
			// create the request
			Builder builder = SERVER.path(endpoint).request().accept(mt);
			Response response;
			if(mt == MediaType.APPLICATION_JSON)
				response = builder.get();
			else
				response = builder.get();
			
			//read the request
			String data = response.readEntity(String.class);
						
			// init the marshaller and unmarshall from the given string
			MeasureTypes mtt;
			if(mt.equals(MediaType.APPLICATION_JSON))
				mtt = (MeasureTypes) unmarshallJSON(data,MeasureTypes.class);
			else
				mtt= (MeasureTypes) unmarshallXML(data,MeasureTypes.class);
			
			if(mtt.getMeasureType().size() > 2)
				result = "OK";
			else
				result = "ERROR";
			
			// print the log
			String log = createLogStr(data, response.getStatus());
			System.out.println(log);
			writer.println(log);			
			
			return mtt;
			
		}catch(Exception e){
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	private List<Measure> retrieveMeasures(){
		
		return null;
	}
	private static List<Measure> retrieveMeasuresByTimestamp(int pId, String t, int a, int b){
		requestNumber = 11;
		try{
			//prepare and make the call
			//	GET /person/{id}/{measureType}?before={beforeDate}&after={afterDate}
			endpoint = "/person/"+pId+"/"+t;
			httpMethodStr = "GET";
			// create the request
			Builder builder = SERVER.path(endpoint)
					.queryParam("after", a)
					.queryParam("before", b)
					.request()
					.accept(mt);
			Response response;
			if(mt == MediaType.APPLICATION_JSON)
				response = builder.get();
			else
				response = builder.get();
			
			//read the request
			String data = response.readEntity(String.class);
						
			// init the marshaller and unmarshall from the given string
			Measures mm;
			if(mt.equals(MediaType.APPLICATION_JSON))
				mm = (Measures) unmarshallJSON(data,Measures.class);
			else
				mm= (Measures) unmarshallXML(data,Measures.class);
			
			if(response.getStatus()==200 && mm.getMeasure().size()>0)
				result="OK";
			else
				result="ERROR";
			
			// print the log
			String log = createLogStr(data, response.getStatus());
			System.out.println(log);
			writer.println(log);
			
			return mm.getMeasure();
			
		}catch(Exception e){
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}			
	}
	private static List<Measure> retrieveMeasureByPersonIdByType(int pId, String t){
		requestNumber = 6;
		try{
			//prepare and make the call
			endpoint = "/person/"+pId+"/"+t;
			httpMethodStr = "GET";
			// create the request
			Builder builder = SERVER.path(endpoint).request().accept(mt);
			Response response;
			if(mt == MediaType.APPLICATION_JSON)
				response = builder.get();
			else
				response = builder.get();
			
			//read the request
			String data = response.readEntity(String.class);
			
			if(response.getStatus()==200)
				result="OK";
			else
				result="ERROR";			
			
			// print the log
			String log = createLogStr(data, response.getStatus());
			System.out.println(log);
			writer.println(log);
						
			// init the marshaller and unmarshall from the given string
			Measures mm;
			if(mt.equals(MediaType.APPLICATION_JSON))
				mm = (Measures) unmarshallJSON(data,Measures.class);
			else
				mm= (Measures) unmarshallXML(data,Measures.class);
			return mm.getMeasure();
			
		}catch(Exception e){
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}		
	}	
	private static Measure retrieveMeasureByPersonIdByTypeById(int pId, String t, int id){
		requestNumber = 7;
		try{
			//prepare and make the call
			endpoint = "/person/"+pId+"/"+t+"/"+id;
			httpMethodStr = "GET";
			
			Response response = SERVER.path(endpoint).request().accept(mt).get();
			String data = response.readEntity(String.class);
			
			if(response.getStatus()==200)
				result="OK";
			else
				result="ERROR";
			
			// print the log
			String log = createLogStr(data, response.getStatus());
			System.out.println(log);
			writer.println(log);
			
			// init the marshaller and unmarshall from the given string
			Measure m;
			if(mt.equals(MediaType.APPLICATION_JSON))
				m = (Measure) unmarshallJSON(data,Measure.class);
			else
				m= (Measure) unmarshallXML(data,Measure.class);
			return m;
			
		}catch(Exception e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return null;
	}
	private static Measure insertMeasureByPersonIdByType(Measure m, int pId, String t){
		requestNumber = 8;
		try{
			//prepare and make the call
			endpoint = "/person/"+pId+"/"+t;
			httpMethodStr = "POST";
			// create the request
			Builder builder = SERVER.path(endpoint).request().accept(mt);
			Response response;
			if(mt == MediaType.APPLICATION_JSON)
				response = builder.post(Entity.json(m));
			else
				response = builder.post(Entity.xml(m));
			
			//read the request
			String data = response.readEntity(String.class);
						
			// init the marshaller and unmarshall from the given string
			Measure res;
			if(mt.equals(MediaType.APPLICATION_JSON))
				res = (Measure) unmarshallJSON(data,Measure.class);
			else
				res= (Measure) unmarshallXML(data,Measure.class);
			
			// check the result
			if(response.getStatus() == 200 || response.getStatus() == 201 || response.getStatus() == 202)
				result = "OK";
			else
				result = "ERROR";
			
			// print the log
			String log = createLogStr(data, response.getStatus());
			System.out.println(log);
			writer.println(log);			
			
			return res;
			
		}catch(Exception e){
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}			

	}
	private static Person insertPerson(Person p){
		requestNumber = 4;
		try{
			//prepare and make the call
			endpoint = "/person";
			httpMethodStr = "POST";
			// create the request
			Builder builder = SERVER.path(endpoint).request().accept(mt);
			Response response;
			if(mt == MediaType.APPLICATION_JSON)
				response = builder.post(Entity.json(p));
			else
				response = builder.post(Entity.xml(p));
			
			//read the request
			String data = response.readEntity(String.class);
						
			// init the marshaller and unmarshall from the given string
			Person res;
			if(mt.equals(MediaType.APPLICATION_JSON))
				res = (Person) unmarshallJSON(data,Person.class);
			else
				res= (Person) unmarshallXML(data,Person.class);
			
			// check the result
			if(res.getId() != 0 && (response.getStatus() == 200 || response.getStatus() == 201 || response.getStatus() == 202))
				result = "OK";
			else
				result = "ERROR";
			
			// print the log
			String log = createLogStr(data, response.getStatus());
			System.out.println(log);
			writer.println(log);			
			
			return res;
			
		}catch(Exception e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return null;		
	}
	private static Person updatePerson(Person p, int id){
		requestNumber = 3;
		try{
			//prepare and make the call
			endpoint = "/person/"+id;
			httpMethodStr = "PUT";
			// create the request
			Builder builder = SERVER.path(endpoint).request().accept(mt);
			Response response;
			if(mt == MediaType.APPLICATION_JSON)
				response = builder.put(Entity.json(p));
			else
				response = builder.put(Entity.xml(p));
			
			//read the request
			String data = response.readEntity(String.class);
						
			// init the marshaller and unmarshall from the given string
			Person res;
			if(mt.equals(MediaType.APPLICATION_JSON))
				res = (Person) unmarshallJSON(data,Person.class);
			else
				res= (Person) unmarshallXML(data,Person.class);
			// check the result
			if(p.getName().equals(res.getName()))
				result = "OK";
			else
				result = "ERROR";
			
			// print the log
			String log = createLogStr(data, response.getStatus());
			System.out.println(log);
			writer.println(log);			
			
			return res;
			
		}catch(Exception e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return null;
	}
	private static Measure updateMeasureByPersonIdByTypeById(Measure m, int pId, String t, int id){
		requestNumber = 10;
		try{
			//prepare and make the call
			endpoint = "/person/"+pId+"/"+t+"/"+id;
			httpMethodStr = "PUT";
			// create the request
			Builder builder = SERVER.path(endpoint).request().accept(mt);
			Response response;
			if(mt == MediaType.APPLICATION_JSON)
				response = builder.put(Entity.json(m));
			else
				response = builder.put(Entity.xml(m));
			
			//read the request
			String data = response.readEntity(String.class);
						
			// init the marshaller and unmarshall from the given string
			Measure res;
			if(mt.equals(MediaType.APPLICATION_JSON))
				res = (Measure) unmarshallJSON(data,Measure.class);
			else
				res= (Measure) unmarshallXML(data,Measure.class);
			
			if(response.getStatus()==200)
				result="OK";
			else
				result="ERROR";
			
			// print the log
			String log = createLogStr(data, response.getStatus());
			System.out.println(log);
			writer.println(log);			
			
			return res;
			
		}catch(Exception e){
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	private static boolean removePersonById(int id){
		requestNumber = 5;
		try{
			//prepare and make the call
			endpoint = "/person/"+id;
			httpMethodStr = "DELETE";
			// create the request
			Response response = SERVER.path(endpoint).request().accept(mt).delete();
			
			if(response.getStatus()==200)
				result="OK";
			else
				result="ERROR";	
			// print the log
			String log = createLogStr("", response.getStatus());
			System.out.println(log);
			writer.println(log);
			
			if(response.getStatus() == 200 || response.getStatus()==201 || response.getStatus()==202)
				return true;
			else
				return false;
		}catch(Exception e){
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}		
	}

	private static String createLogStr(String body, int code){
		if(mt.equals(MediaType.APPLICATION_XML) && body != "")
			body = toPrettyString(body);
		
		
		String output = "Request #"+requestNumber+": "
				+ httpMethodStr+" "+endpoint+" Accept: "
				+ mt + " Content-type: "+mt + "\n";
		
		output += "\t=> Result: "+result+"\n";
		output += "\t=> HTTP Status: "+code+"\n";
		output += body +"\n";
		
		return output;
	}

	public static String toPrettyString(String xml) {
	    try {
	        // Turn xml string into a document
	        Document document = DocumentBuilderFactory.newInstance()
	                .newDocumentBuilder()
	                .parse(new InputSource(new ByteArrayInputStream(xml.getBytes("utf-8"))));
	
	        // Remove whitespaces outside tags
	        XPath xPath = XPathFactory.newInstance().newXPath();
	        NodeList nodeList = (NodeList) xPath.evaluate("//text()[normalize-space()='']",
	                                                      document,
	                                                      XPathConstants.NODESET);
	
	        for (int i = 0; i < nodeList.getLength(); ++i) {
	            Node node = nodeList.item(i);
	            node.getParentNode().removeChild(node);
	        }
	
	        // Setup pretty print options
	        TransformerFactory transformerFactory = TransformerFactory.newInstance();
	        transformerFactory.setAttribute("indent-number", 4);
	        Transformer transformer = transformerFactory.newTransformer();
	        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	
	        // Return pretty print xml string
	        StringWriter stringWriter = new StringWriter();
	        transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
	        return stringWriter.toString();
	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    }
	}
	private static Object unmarshallJSON(String d, Class c){
        try {
    		// Jackson Object Mapper 
    		ObjectMapper mapper = new ObjectMapper();
            JaxbAnnotationModule module = new JaxbAnnotationModule();
    		mapper.registerModule(module);
    		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);        	
			return mapper.readValue(d, c);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}		
	}
	private static Object unmarshallXML(String d, Class c){
        try {
			// init the marshaller and unmarshall from the given string
			JAXBContext jc = JAXBContext.newInstance(c);
			Unmarshaller um = jc.createUnmarshaller();
			Source s = new StreamSource(new StringReader(d));
			return um.unmarshal(s);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}		
	}	
}
