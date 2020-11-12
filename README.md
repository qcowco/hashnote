# hashnote
### REST API for pastebin-style message encryption
Hashnote is an application that serves as a backend for message encryption. Its functionality can be briefly summed up as pastebin with encryption. The primary design concern is to be able to provide service from the get go, which makes account registration totally optional (save for a few benefits such as note folders).

## Table of contents
* [Technologies](#technologies)
* [Encryption](#encryption)
* [Database](#database)
* [Setup](#setup)
* [API Documentation](#api-documentation)

## Technologies
* Java 11
* Spring Boot
* JPA/Hibernate
* MongoDB
* Spring Security 5 with JWT
* JUnit5 5 + Mockito
* Springfox Swagger
* HATEOAS

## Encryption
The notes are encrypted with a private symmetric key that grants anyone access to its contents. Encryption is achieved thanks to the javax.crypto package, which also allows for seamless integration with projects such as Bouncy Castle. Additionally, the architecture is designed in such a way that makes expansion of supported algorithms very easy. Declare a component that extends the abstract AlgorithmDetails class, and it will be automatically picked up and integrated.

## Security
Bearer tokens are used to authenticate and authorize the user if needed.

## Database 
The application is configured for use with a MongoDB database.

## Setup
First of all, a [working database server](#database) is required to use the application. 

#### Building the application
```
$ gradlew.bat build
```

#### Required environment variables:
```
MONGODB_URI             URI of Your database
DB_NAME                 name of Your database
JWT_SECRET              base64 encoded jwt secret key
JWT_EXPIRE              jwt expiration in milliseconds
DELETE_SCHEDULE_CRON    CRON schedule for removal of expired notes
```

## API Documentation
Springfox Swagger is fully integrated, and serves as an interactive documentation that allows the user to test the API through a simple UI. It is set as the default homepage users are greeted with.
