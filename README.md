netty-livereload
================

[Livereload](http://livereload.com/) protocol implementation based on Netty websocket implementation.  
The goal of this project is to provide an as lighweight as possible Java implementation of a livereload server.  
It is strobgly inspired by [livereload-jvm](https://github.com/davidB/livereload-jvm) which is based on Jetty websocket implementation.  

Livereload in a nutshell
----------------
Livereload is a productivity tool  for web developers. During web development, it refreshs the browser each time a file has been changed on the server side.  
Under the cover, livereload relies on a websocket connection between the browser and the server. On the browser side, javascript code (from livereload.js), opens the connection with the server and handle commands comming from the server. On the server, a process watchs filesystem changes and generates a refresh command when a file (html, css, js, ...) from the web UI has been modified.

How to enable reload in the browser
--------------
On the browser side the easiest way to include livereload.js into your application is to use a [browser plugin](http://feedback.livereload.com/knowledgebase/articles/86242-how-do-i-install-and-use-the-browser-extensions-).
However it is not always possible to do so. If your application runs on a mobile device, on internet explorer or anywhere else where adding a browser extention is not an option, the fastest and more reliable way to include livereload.js is to use the following code fragment:

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

