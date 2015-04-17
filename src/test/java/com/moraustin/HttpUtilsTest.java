package com.moraustin;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import static org.junit.Assert.*;

public class HttpUtilsTest {


    @Test
    public void ShouldReadInputStream() throws IOException {
        final String body = "I am a robot";
        HttpURLConnectionSpy spy = new HttpURLConnectionSpy(body);
        assertTrue(HttpUtils.readResponse(spy).equals(body));
        assertEquals(1, spy.inputStreamCallCount);
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