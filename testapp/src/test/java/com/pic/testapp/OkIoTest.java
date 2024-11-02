package com.pic.testapp;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import okio.Buffer;
import okio.BufferedSource;
import okio.Okio;
import okio.Source;

/**
 * @author Lu
 * @date 2024/11/2 11:10
 * @description
 */
public class OkIoTest {

    /**
     * okio copy
     */
    @Test
    public void testOkIoCopy() {
        try {
            File file = new File(new File("./").getAbsoluteFile().getParentFile().getParentFile(), "README.md");
            InputStream fileStream = new FileInputStream(file);
            // 创建一个 Source
            Source source = Okio.source(fileStream);
            // 创建缓冲source
            BufferedSource bufferedSource = Okio.buffer(source);
            Buffer buffer = bufferedSource.getBuffer();
            byte[] bytes = bufferedSource.readByteArray();
            buffer.write(bytes);

            System.out.println("buffer:" + buffer.readString(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
