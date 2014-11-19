[![Build Status](https://travis-ci.org/alexvictoor/netty-livereload.svg?branch=master)](https://travis-ci.org/alexvictoor/netty-livereload)

netty-livereload
================

[Livereload](http://livereload.com/) protocol implementation based on Netty websocket implementation.  
The goal of this project is to provide a livereload Java implementation as lighweight as possible .  
It is strongly inspired by [livereload-jvm](https://github.com/davidB/livereload-jvm) which is based on Jetty websockets.  

Livereload in a nutshell
----------------
Livereload is a productivity tool  for web developers. During web development, it refreshs the browser each time a file has been changed on the server side such as a CSS stylesheet or an HTML document.  
Under the cover, livereload relies on a websocket connection between the browser and the server. On the browser side, a javascript code (from livereload.js) opens a connection with a livereload server. Once the connection is established, commands can be exchanged between the server and the browser. On the server, a process watchs the filesystem for changes on web files (i.e. html, css, js, ...). When a file has been changed on the server, a command is send to the browser. This command once processed on the browser side generates a refresh of the current page.

How to enable reload in the browser
--------------
On the browser side the easiest way to include livereload.js into your application is to use a [browser plugin](http://feedback.livereload.com/knowledgebase/articles/86242-how-do-i-install-and-use-the-browser-extensions-).
However it is not always possible to do so. If your application runs on a mobile device, on internet explorer or anywhere else where adding a browser extention is not an option, the fastest and more reliable way to include livereload.js is to use the following code fragment:

    <script>document.write('<script src="http://'
        + (location.host || 'localhost').split(':')[0]
        + ':35729/livereload.js"></'
        + 'script>')</script>

*Snipset taken from the [livereload-js](https://github.com/livereload/livereload-js) website*


How to start netty-livereload from the CLI
------------------
The simplest way to start a livereload server from the command line is to use the "all in one" jar as below:

    java -jar netty-livereload-0.1-allinone.jar PATH
  
*PATH* being the path to the folder containing your web files.

How to embed the livereload server in your application
--------------------------
First you need to add a netty-livereload dependency to your project. Below a maven XML fragment to add in the dependencies section:

    <dependency>
      <groupId>com.github.alexvictoor</groupId>
      <artifactId>netty-livereload</artifactId>
      <version>0.1</version>
    </dependency>

Then you just need to create a WebSocketServer instance providing the path to your web files as a *String*. Port is optionnal, default value being 35729, which is the value specified in the livereload protocol specification.  
Then you just need to start the server. Below a code example:

    String rootFolder = "...";
    WebSocketServer server = new WebSocketServer(rootFolder);
    server.start();
    
