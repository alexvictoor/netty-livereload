package com.github.alexvictoor.livereload;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileReader {

    public String readFileFromClassPath(String classPath) {
        try {
            URL resource = getClass().getResource(classPath);
            if (resource==null) {
                throw new IllegalArgumentException("Incorrect path file");
            }
            URI fileURI = resource.toURI();
            byte[] encoded = Files.readAllBytes(Paths.get(fileURI));
            return new String(encoded, Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
