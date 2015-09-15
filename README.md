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
    <version>0.5.1</version>
</dependency>
 ```

##Examples
 
### Connection setup

 ```java
Actor<String> actor = new Actor<String>() {
	public void handle(String msg) {
		System.out.println("Received: " + msg);
	}
};

Broker.send(actor.endpoint(), "Hello");
 ```

##License
[Apache License Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)