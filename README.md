# introsde-2016-assignment-2
## Samuele Malavasi | mat.182551
The second assignment for the course of introduction of service design (a.a. 2016-2017) aims to implement a service on a heroku server and a client able to consume the api both via json and xml.
I used Ivy to manage dependencies and ant to automate the ivy installation, dependencies retrieving, java compiling and execution (of the client). The server is already deployed on heroku.

### About the code
The main classes are

**MyFilter** Is called (in the web.xml) on each request, it verifies if the database is populated. In case it is not, provides the population taking the queries from two .txt files.

**Client** Executes all the requests in the asked order, both via json and xml. It prints the log in two different files and on the console.
  * `rest api methods` Used to interface to the server side: here we can perform CRUD operations
  * `createLogStr(String, int)` Returns the string to print on each request's execution
  * `toPrettyString(String)` Prettifies the given xml type string
  * `unmarshallJSON(String, Class)` Given a `json` type string and its associated class returns a the generic java object obtained by the parser
  * `unmarshallXML(String, Class)` Given a `xml` type string and its associated class returns a generic java object obtained by the parser 

**Measure**
  * `id:int` Identifies the measure
  * `date:int` The timestamp of a specific measure
  * `type:String` Which kind of measure is stored in the field value
  * `value:Double` The measured value
  * `personId:int` Keeps the identifier of the person whose the measure
  * `person:Person` The instance of the associated person: useful to JPA
  
  
  * `saveMeasure()` Inserts in the database the given instance of a measure 
  * `updateMeasure()` Changes the value of an already presents measure to the given one 
  * `getAll()` Retrieve all the instances of measure in the database
  * `getOne()` Retrieve just the measure which owns a certain Id
  * `getByDate()` Retrieve the measures of a certain person made in a specific time range  

**Measures** Is the model used to hold a group of 'measure' instances  
  
**MeasureTypes** Keep the strings list of unique measure types inside the table

**Person**:
  * `birthdate : int` The timestamp of the birthdate
  * `id : int` The unique identifier of a person instance
  * `name : String` Name of a person
  * `surname : String` Surname of a person
  * `measure : List<Measure>` Group of measures belonging to a person
  
   
  * `cleanMeasures()` Keeps just the last measure foreach measure type
  * `getAll()` Retrieves all the instances of person in the database
  * `getOne(int)` Retrieves just the person which owns a certain Id
  * `updatePerson(Person)` Changes the value of an already present person to the given one
  * `savePerson(Person)` Inserts in the database the given instance of a person
  * `removePerson(Person)` Deletes from the db the person which matches a specific Id
  * `getByRangeMeasure(Double, Double, String)` Gets a group of people whose measure value falls in a specific range
  * `getByMinMeasure(Double, String)` Gets a group of people whose measure value is greater than...
  * `getByMaxMeasure(Double, String)` Gets a group of people whose measure value is lower than...

**People** Is the model used to hold a group of 'person' instances   

**MeasureCollectionResource** Exposes the endpoints for *http://<host>/rest/measure*
  * `getMeasuresBrowser()` Makes available all the measures stored
  * `getMeasureById(int)` Provide a measure with a specific Id
  
**MeasureTypesResource** Exposes the endpoints for *http://<host>/rest/measureTypes*
  * `getMeasureTypes()` Exposes the unique values which 'Measure.type' field assumes among the stored values
  
**PersonCollectionResource** Exposes the endpoints for *http://<host>/rest/person*
  * `getPeople(UriInfo)` Returns to the client an instance of People
  * `getMeasures(UriInfo, int, String, int, int)` Gets all the measures which belong to the person with a certain id and whose type is matched, if other parameters are specified (after and before) retrieve just the measures falling in the interval.
  * `getMeasureById(int, String, int)` Returns just a measure with certain PersonId, Type and Id
  * `getPerson(int)` Exposes the person resource whose id matches the given one
  * `newMeasure(Measure, int, String)` Inserts in the db a new measure for the person whose id is matched, the new measure has type equal to the given one
  * `newPerson(Person)` Inserts in the database the person parsed from the client's request body
  * `putPerson(int, Person)` Edits the person resource whose id matches the given one, takes the data from the user input
  * `putMeasure(int, String, int, Measure)` Updates the measure matching personId,Type and Id
  * `deletePerson(int)` Removes from the db the person whose id matches the given one

### About the tasks
Everything written in `build.xml` is useful to run **execute.client** target.
Before running **execute.client** ant will,
* download & install ivy to manage dependencies
* make ivy handle dependencies
* compile the classes including dependencies
* finally run `Client`

It is also implemented a task to clean the folder and delete the generated files with the target **clean**

### How to run
It is possible to make the code working by just running:
> git clone https://github.com/billyLumberjack/introsde-2016-assignment-2

> cd introsde-2016-assignment-2

> ant execute.client

Eventually it is possible to clean the folder and restore it as after the clone with
> ant clean
