0blivi0n REST API client for JAVA applications
==============================================

##Introduction

  0blivi0n REST API is the official JAVA client for 0blivi0n-cache.
  
##Instalation

Maven dependency:
 
 ```xml
<dependency>
    <groupId>org.oblivion-cache</groupId>
    <artifactId>oblivion-rest-client</artifactId>
    <version>0.5.2</version>
</dependency>
 ```

##Examples
 
### Connection setup

 ```java
Oblivion oblivion = Oblivion.builder()
		.server("uiqui.net")
		.build();
 ```
 
### Cache setup

 ```java
// Cache PERSON stores json representations of the java class Person
CacheContext<Person> personCache = oblivion.newCacheContext("PERSON", Person.class);

// Cache RAW_CACHE stores json but no automatic conversion from object to json
// and json to object will be provided for this cache
CacheContext<String> rawCache = oblivion.newCacheContext("RAW_CACHE");
 ``` 
 
### Store data

 ```java
personCache.put(1, new Person(1, "Joaquim"));

rawCache.put(1, "{\"id\": 1, \"name\": \"Joaquim\"}");
 ```  
 
### Retrieve data

 ```java
Person person2 = personCache.get(2);

// Retrieving the value (Person object) and the version 
Value<Person> value3 = personCache.getValue(3);
Person person3 = value3.getValue();
long version3 = value3.getVersion();

String json = rawCache.get(2);
 ```  
 
### Delete data

 ```java
personCache.delete(2);
personCache.delete(3, version3);

rawCache.delete(3);
 ```   

##License
[Apache License Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)