package com.github.alexvictoor.livereload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class FileReader {

    public static final Logger logger = LoggerFactory.getLogger(FileReader.class);

    public String readFileFromClassPath(String classPath) {


            return new Scanner(getClass().getResourceAsStream(classPath)).useDelimiter("\\Z").next();
            //byte[] encoded = Files.readAllBytes(Paths.get(fileURI));
            //return new String(encoded, Charset.defaultCharset());

    }
}
