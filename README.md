# Taxi24 API

This is the exemplary Taxi24 API application for review by BK Digital Factory. It is implemented in Spring Boot 2, using the [JHipster generator](https://www.jhipster.tech/).

It implements the data model and all use cases required in the task:

- Driver API + Entity
    - 


## Building and running the application

After cloning the repository, use Maven to build and run the application. Maven does not have to be installed locally, just use the mvnw wrapper in the root directory. To start your application in the dev profile, simply run:

    ./mvnw
    
The application is compiled, unit and integration tests are run, and the server is automatically launched on port 8081. The API base path then is http://localhost:8081/taxi24api.<br>
There are 2 Maven profiles: dev (default) for the development mode, and prod for the production mode. For a simple demonstration, the dev profile should be used. This launches an embedded H2 in-memory database. On every launch, an empty H2 database is created and all necessary tables are created automatically. The database console can be accesses via http://localhost:8081/taxi24api/h2-console/.<br>
The production profile is configured to use a Postgres DB. The DB properties have to be configured in the src/main/resources/config/application-prod.yml file, section spring.datasource. 



## Building for production



## Testing

To launch your application's tests, run:

    ./mvnw clean test





