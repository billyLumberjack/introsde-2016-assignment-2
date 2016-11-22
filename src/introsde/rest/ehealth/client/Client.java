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

import introsde.rest.ehealth.model.Measure;
import introsde.rest.ehealth.model.People;
import introsde.rest.ehealth.model.Person;

@SuppressWarnings("unchecked")
public class Client {
	
	private static final String ENDPOINT = "http://localhost:8080/introsde-assignment2/rest/";

    private static final URI SERVER_URI = UriBuilder.fromUri(ENDPOINT).build();
    private static final WebTarget SERVER = ClientBuilder.newClient(new ClientConfig()).target(SERVER_URI);	
    private static String mt;
    // log variables
    private static int requestNumber;
    private static String httpMethodStr, endpoint, acceptStr, contentTypeStr, result;
    static PrintWriter writer;
	
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
    
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		mt = MediaType.APPLICATION_XML;
		writer = new PrintWriter("client-server-xml.log", "UTF-8");
		//get the entire list of people in the db
		List<Person> pp = retrievePeople();
		//check whether the list of people in the db contains more than 2 objects
		if(pp.size() > 2)
			result = "OK";
		else
			result ="ERROR";
		// assign to the relative variables the first and last id found
		int first_person_id = pp.get(0).getId();
		int last_person_id = pp.get(pp.size()-1).getId();
		//send R#2
		Person p = retrievePersonById(first_person_id);
		// preparing R#3
		p.setName("Samuele");
		p = updatePerson(p, first_person_id);
		// R#4
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
		//R#5
		removePersonById(p.getId());
		if(retrievePersonById(p.getId()) == null){
			result = "OK";
			// print the log
			String log = createLogStr("", 404);
			System.out.println(log);
			writer.println(log);
		}
		
		
		
		
		writer.close();
	}
	
	
	private static List<Person> retrievePeople(){
		requestNumber = 1;
		try{
			//prepare and make the call
			endpoint = "/person";
			httpMethodStr = "GET";
			
			Response response = SERVER.path(endpoint).request().accept(mt).get();
			String data = response.readEntity(String.class);
			
			// print the log
			String log = createLogStr(data, response.getStatus());
			System.out.println(log);
			writer.println(log);
			
			// init the marshaller and unmarshall from the given string
			JAXBContext jc = JAXBContext.newInstance(People.class);
			Unmarshaller um = jc.createUnmarshaller();
			Source s = new StreamSource(new StringReader(data));
			// return person list
			return ((People) um.unmarshal(s)).getPerson();
			
		}catch(Exception e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	private List<Measure> retrieveMeasures(){
		
		return null;
	}
	private List<Measure> retrieveMeasuresByTimestamp(int t){
		requestNumber = 11;
		return null;
	}
	private Measure retrieveMeasureByPersonIdByType(int pId, String t){
		requestNumber = 6;
		return null;
	}	
	private Measure retrieveMeasureByPersonIdByTypeById(int pId, String t, int id){
		requestNumber = 7;
		return null;
	}
	private Measure retrieveMeasureTypes(){
		requestNumber = 9;
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
			
			
			// init the marshaller and unmarshall from the given string
			JAXBContext jc = JAXBContext.newInstance(Person.class);
			Unmarshaller um = jc.createUnmarshaller();
			Source s = new StreamSource(new StringReader(data));
			// return person list
			return (Person) um.unmarshal(s);
			
		}catch(Exception e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return null;
	}
	private static Person retrievePersonByMeasureTypeAndValue(String type, int min, int max){
		requestNumber = 12;
		return null;
	}
	private boolean insertMeasureByPersonIdByType(Measure m, int pId, String t){
		requestNumber = 8;
		return false;
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
			JAXBContext jc = JAXBContext.newInstance(Person.class);
			Unmarshaller um = jc.createUnmarshaller();
			Source s = new StreamSource(new StringReader(data));
			Person res = (Person) um.unmarshal(s);
			
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
			JAXBContext jc = JAXBContext.newInstance(Person.class);
			Unmarshaller um = jc.createUnmarshaller();
			Source s = new StreamSource(new StringReader(data));
			Person res = (Person) um.unmarshal(s);
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
	private boolean updateMeasureByPersonIdByTypeById(Measure m, int pId, String t){
		requestNumber = 10;
		return false;
	}
	private static boolean removePersonById(int id){
		requestNumber = 5;
		try{
			//prepare and make the call
			endpoint = "/person/"+id;
			httpMethodStr = "DELETE";
			// create the request
			Response response = SERVER.path(endpoint).request().accept(mt).delete();
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
}
