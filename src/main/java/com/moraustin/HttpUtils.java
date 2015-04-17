package com.moraustin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

public class HttpUtils {

    private HttpUtils() {

    }

    public static String readResponse(HttpURLConnection connection) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder builder = new StringBuilder();

        while ((inputLine = reader.readLine()) != null) {
            builder.append(inputLine);
        }
        reader.close();
        return builder.toString();
    }
}
