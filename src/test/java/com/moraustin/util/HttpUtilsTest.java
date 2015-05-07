package com.moraustin.util;

import com.moraustin.util.HttpUtils;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import static org.junit.Assert.*;

public class HttpUtilsTest {


    @Test
    public void ShouldReadUrlConnectionInputStream() throws IOException {
        final String body = "I am a robot";
        HttpURLConnectionSpy spy = new HttpURLConnectionSpy(body);
        assertTrue(HttpUtils.readResponse(spy).equals(body));
        assertEquals(1, spy.inputStreamCallCount);
    }

    @Test
    public void ShouldReadNakedInputStream() throws IOException {
        final String body = "let's get serious";
        InputStream stream = new ByteArrayInputStream(body.getBytes());
        String result = HttpUtils.readInputStream(stream);
        assertTrue(body.equals(result));
    }


    private static class HttpURLConnectionSpy extends HttpURLConnection {

        private final String body;
        int inputStreamCallCount = 0;

        HttpURLConnectionSpy(String body) {
            super(null);
            this.body = body;
        }

        @Override
        public InputStream getInputStream() {
            ++inputStreamCallCount;
            return new ByteArrayInputStream(body.getBytes());
        }

        @Override
        public void disconnect() {

        }

        @Override
        public boolean usingProxy() {
            return false;
        }

        @Override
        public void connect() throws IOException {

        }
    }

}