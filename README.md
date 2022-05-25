# githubproxy

Simple [Spring Boot](http://projects.spring.io/spring-boot/) app for listing non-fork repositories and their branches
for given
GitHub user.

## Requirements

For building and running the application you need:

- [JDK 1.8](https://www.oracle.com/java/technologies/javase/javase8-archive-downloads.html)
- [Maven](https://maven.apache.org/)

## Running the application locally

To run application locally execute following commands in project directory

```shell
mvn clean install
mvn spring-boot:run
```

To run application in docker container execute following commands in project directory

```shell
docker build -t githubproxy .
docker run -p 8080:8080 githubproxy
```

## Swagger documentation

Swagger documentation can be accessed on:

```shell
http://localhost:8080/swagger-ui
```
