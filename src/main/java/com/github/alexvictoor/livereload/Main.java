package com.github.alexvictoor.livereload;


public class Main {

    public static void main(String[] args) throws Exception {
        final String rootFolder;
        if (args.length == 0) {
            rootFolder = ".";
        } else {
            rootFolder = args[0];
        }
        new WebSocketServer(rootFolder).start();
    }
}