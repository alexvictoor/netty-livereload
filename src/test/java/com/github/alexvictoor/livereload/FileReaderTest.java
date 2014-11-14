package com.github.alexvictoor.livereload;

import org.junit.Test;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

public class FileReaderTest {

    @Test
    public void should_read_file_content() throws Exception {
        System.out.println(getClass().getResource("/test.file").getFile());
        // given
        FileReader reader = new FileReader();
        // when
        String content = reader.readFileFromClassPath("/test.file");
        // then
        assertThat(content).isEqualTo("Test Content");
    }
}