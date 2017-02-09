## HTTP Server ##
Author: Anqi Wu

### HTTP Server###
* Support GET and HEAD requests
* Accept one command-line flag (--serverPort), indicating which TCP port to bind to.

### File List ###

* WebServer.java
* Handler.java
* Request.java

### Compile ### 

* javac *.java

### Run ###

* java WebServer --serverPort=8000

### Test ###

* curl -i <URL>/curl --include <URL>     GET Method which incldues the response message
* curl --head <URL>     HEAD Method
* curl -i -X POST http://127.0.0.1:8000/index.html    POST method
* curl -i -X DELETE http://127.0.0.1:8000/hello.txt          DELETE method
