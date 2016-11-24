# introsde-2016-assignment-2
## Samuele Malavasi | mat.182551
The second assignment for the course of introduction of service design (a.a. 2016-2017) aim to implement a service on a heroku server and a client able to consume the api both via json and xml.
I used Ivy to manage dependencies and ant to automate the ivy installation, dependencies retrieving, java compiling and execution (of the client). The server is already deployed on heroku.

### About the code
The main classes are

**MyFilter** Is called (in the web.xml) on each request, it verifies if the database is populated. In case it is not, provides to the population taking the queries from two .txt files.

**Client** Executes all the requests in the asked order, both via json and xml. It prints the log in two different files and on the console.
  * `rest api methods` Used to interface to the server side: here we can perform CRUD operations
  * `createLogStr(String, int)` Returns the string to print on each request's execution
  * `toPrettyString(String)` Prettify the xml type string given
  * `unmarshallJSON(String, Class)` Given a `json` type string and its associated class returns a the generic java object obtained parsing the string
  * `unmarshallXML(String, Class)` Given a `xml` type string and its associated class returns a the generic java object obtained parsing the string 

**Measure**
  * `id:int` Identifies the measure
  * `date:int` The timestamp of a specific measure
  * `type:String` Which kind of measure is stored in the value field
  * `value:Double` The measured value
  * `personId:int` Keeps the identifier of the person whose the measure
  * `person:Person` The istance of the associated person: useful to JPA
  
  
  * `saveMeasure()` Insert in the database the given istance of a measure 
  * `updateMeasure()` Change the value of an already present measure to the given one 
  * `getAll()` Retrieve all the istances of measure in the database
  * `getOne()` Retrieve just the measure which owns a certain Id
  * `getByDate()` Retrieve the measures of a certain person made in a specific time range  

**Measures** Is the model used to hold a group of 'measure' instances  
  
**MeasureTypes** Keep the string list of unique measure types inside the table

**Person**:
  * `birthdate : int` The timestamp of the birthdate
  * `id : int` The unique identifier of a person istance
  * `name : String` Name of a person
  * `surname : String` Surname of a person
  * `measure : List<Measure>` Group of measures belonging to a person
  
   
  * `cleanMeasures()` Keep just the last measure foreach measure type
  * `getAll()` Retrieve all the istances of person in the database
  * `getOne(int)` Retrieve just the person which owns a certain Id
  * `updatePerson(Person)` Change the value of an already present person to the given one
  * `savePerson(Person)` Insert in the database the given istance of a person
  * `removePerson(Person)` Delete from the db the person which matches a specific Id
  * `getByRangeMeasure(Double, Double, String)` Get a group of people whose measure value falls in a specific range
  * `getByMinMeasure(Double, String)` Get a group of people whose measure value is greater than
  * `getByMaxMeasure(Double, String)` Get a group of people whose measure value is lower than

**People** Is the model used to hold a group of 'person' instances   

**MeasureCollectionResource** Exposes the endpoints for *http://<host>/rest/measure*
  * `getMeasuresBrowser()` Makes available all the measures stored
  * `getMeasureById(int)` Provide a measure with a specific Id
  
**MeasureTypesResource** Exposes the enpoints for *http://<host>/rest/measureTypes*
  * `getMeasureTypes()` Exposes the unique values which 'Measure.type' field assumes among the stored values
  
**PersonCollectionResource** Exposes the enpoints for *http://<host>/rest/person*
  * `getPeople(UriInfo)` Returns to the client an istance of People
  * `getMeasures(UriInfo, int, String, int, int)` Get all the measures which belong to the person with a certain id and whose type matched, if other parameters are specified (after and before) retrieve just the measures falling in the time interval
  * `getMeasureById(int, String, int)` Returns just a measure with certain PersonId, Type and Id
  * `getPerson(int)` Expose the person resource whose id matches the given one
  * `newMeasure(Measure, int, String)` Insert in the db a new measure for the person whose id is matched
    // the new measure has type equal to {measureType}
  * `newPerson(Person)`
  * `putPerson(int, Person)`
  * `putMeasure(int, String, int, Measure)`
  * `deletePerson(int)  `
  
  * **generatePeople()** Generates 3 random people inside a new istance of a **People** object and returns it
  * **marshall(People, String)** Marshall in XML format the People istance passed inside the indicated file
  * **marshallJSON(People, String)** Marshall in JSON format the People istance passed inside the indicated file
  * **unmarshall(String)** Unmarshall from the XML file indicated a People istance and returns it
  * **unmarshallJSON(String)** Unmarshall from the JSON file indicated a People istance and returns it
  * **main(String[])** Marshalls and Unmarshalls both from XML and from JSON, before each marshalling it generates 3 new **Person** istances inside a **People** object, at each step it cleans the variable in which data are stored:
    * Generates 3 random people and marshall them to people_new.xml
    * Cleans local variable
    * Unmarshalls the previous created people from people_new.xml
    * Generates other 3 random people and marshall them to people_new.json
    * Cleans local variable
    * Unmarshalls the previous created people from people_new.json
* `people.xsd`:The XML schema usefull to validate people.xml and to generate through **XJC** the classes to model data

### About the tasks
Everything written in `build.xml` is necessary to run **execute.evaluation** target, with the exception of the target 'generate' whose scope is to generate models automatically. I edited such model to fit some implementation needs.
Before running **execute.evaluation** ant will,
* download & install ivy to manage dependencies
* make ivy handle dependencies
* compile the 3 main classes including the dependencies
* finally run `HealthProfileWriter`, `HealthProfileReader`, `DataMapper`

It is also implemented a task to clean the folder and delete the generated files with the target **clean**

### How to run
Is it possible to make the code working by just running:
> git clone https://github.com/billyLumberjack/introsde-2016-assignment-1

> cd introsde-2016-assignment-1

> ant execute.evaluation

Eventually it is possile to clean the folder and restore it as after the clone with
> ant clean
