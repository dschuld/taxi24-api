# Taxi24 API

This is the exemplary Taxi24 API application for review by BK Digital Factory. It is implemented in Spring Boot 2, using the [JHipster generator](https://www.jhipster.tech/).

It implements the data model and all use cases required in the task. 


## Building and running the application

After cloning the repository, use Maven to build and run the application. Maven does not have to be installed locally, just use the mvnw wrapper in the root directory. To start your application in the dev profile, simply run:

    ./mvnw
    
The application is compiled, unit and integration tests are run, and the server is automatically launched on port 8081. The API base path then is http://localhost:8081/taxi24api.<br>
There are 2 Maven profiles: dev (default) for the development mode, and prod for the production mode. For a simple demonstration, the dev profile should be used. This launches an embedded H2 in-memory database. On every launch, an empty H2 database is created and all necessary tables are created automatically. The database console can be accesses via http://localhost:8081/taxi24api/h2-console/.<br>
The production profile is configured to use a Postgres DB. The DB properties have to be configured in the src/main/resources/config/application-prod.yml file, section spring.datasource. 



## Data Model

The following 3 CREATE TABLE statements show the data model for the Driver, Rider and Trip entities. They are given here for information, however the tables are created automatically in the DB when the application connects, so there is no need to execute them manually.    

    CREATE TABLE public.driver
    (
        id bigint SERIAL NOT NULL PRIMARY KEY,
        name character varying(255),
        latitude real,
        longitude real,
        status integer NOT NULL
    );

    CREATE TABLE public.rider
    (
        id bigint SERIAL  NOT NULL PRIMARY KEY,
        name character varying(255),
        amount_rides integer,
        latitude real NOT NULL,
        longitude real NOT NULL
    );

    CREATE TABLE public.trip
    (
        id bigint SERIAL  NOT NULL PRIMARY KEY,
        driver_id integer NOT NULL REFERENCES driver(id),
        rider_id integer NOT NULL REFERENCES rider(id),
        trip_status integer NOT NULL,
        START_DATE TIMESTAMP, 
        END_DATE TIMESTAMP 
    );


    CREATE TABLE public.invoice
    ( 
        id bigint SERIAL  NOT NULL PRIMARY KEY,
        trip_id bigint REFERENCES trip(id), 
        amount float4, 
        paid boolean 
    );

    
    
    CREATE SEQUENCE PUBLIC.HIBERNATE_SEQUENCE START WITH 1000 INCREMENT BY 50;


## API

The application provides 3 domain endpoints:

- taxi24api/api/driver
    - GET /driver request to get a list of drivers. This request can contain following query parameters:
        - <i>status</i>    Currently the only allowed value is "available". Retrieves a list of all available drivers.
        - <i>longitude/latitude</i>     Specifies a location in coordinats. Both values must be given. All AVAILABLE drivers in a certain radius around this location are returned. Since the status is AVAILABLE by default, it can be omitted here.
        - <i>radius</i>     Specifies the radius in km around the previously mentioned location. If not given, defaults to 3km.
        - <i>riderId</i> Retrieves a list of the 3 drivers closest to the position of the rider with the given ID.
    - GET /driver/{id} to get a specific driver
    - POST /driver to create a driver. The body must have a payload in the following format and the status must be either AVAILABLE, OCCUPIED or UNAVAILABLE (note the all caps letters):
    
    .

        {
            "name": "DriverName",
            "latitude" : -1.9365425785635548,
            "longitude": 30.077939211280636,
            "status" : "AVAILABLE"
        }
        
    - DELETE /driver/{id} deletes a driver
    - PUT /driver/{id} updates an existing driver.

- taxi24api/api/rider
    - GET /riders request to get a list of riders
    - GET /riders/{id} to get a specific rider
    - POST /riders to create a rider. The body must have a payload in the following format:
    
    .

        {
            "name": "riderName",
            "latitude": -1.9365425785635548,
            "longitude": 30.077939211280636
        }
        
    - DELETE /riders/{id} deletes a rider
    - PUT /riders/{id} updates an existing rider.
    
- taxi24api/api/trips
    - GET /trips request to get a list of trip. Can be used with query parameter status (values "requested", "active", "cancelled", "completed") to get a list of all trips with the respective status.
    - GET /trips/{id} to get a specific trip
    - POST /trips to create a trip. The rider and driver specified by the request must exist in the database. The body must have a payload in the following format:
    
    .

        {
            "riderId" : 1001,
            "driverId": 951
        }

    - PATCH /trips/{id} to update the status of a trip. The new status must be contained as JSON in the body. Valid values are "requested", "active", "cancelled" and "completed". Valid state transitions are only requested -> active, requested -> cancelled or active -> completed.
   
    .

        {
            "newStatus" : "completed"
        }

    - DELETE /trips/{id} deletes a trip


- taxi24api/api/invoices
    - GET /invoices request to get a list of invoices.
    - GET /invoices/{id} to get a specific invoice

Additionally, JHipster applications provide non-domain endpoints for management and information:

- taxi24api/management/info
- taxi24api/management/health
- taxi24api/swagger-resources/

## Testing

To launch the application's tests, run:

    ./mvnw clean test

Unit and integration tests are implemented in src/test/java. Additionally, performance test can be implemented with [Gatling](Gatling.io), they have been skipped here due to time constraints.



