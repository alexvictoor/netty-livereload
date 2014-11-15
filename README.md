netty-livereload
================

[Livereload](http://livereload.com/) protocol implementation based on Netty websocket implementation.  
The goal of this project is to provide an as lighweight as possible Java implementation of a livereload server.  
It is strobgly inspired by [livereload-jvm](https://github.com/davidB/livereload-jvm) which is based on Jetty websocket implementation.  

Livereload in a nutshell
----------------
Livereload is a productivity tool  for web developers. During web development, it refreshs the browser each time a file has been changed on the server side.  
Under the cover, livereload relies on a websocket connection between the browser and the server. On the browser side, a javascript file, livereload.js, need to be loaded. Check out next section for details on this.


How to enable reload in the browser
--------------
TBD

    <script>document.write('<script src="http://'
        + (location.host || 'localhost').split(':')[0]
        + ':35729/livereload.js"></'
        + 'script>')</script>

*Snipset taken from the [livereload-js](https://github.com/livereload/livereload-js) website*


How to start the livereload from the CLI
------------------
TBD

How to embed the livereload server in your application
--------------------------
TBD

