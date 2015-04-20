package com.moraustin;

import java.io.*;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;

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

    public static String readInputStream(InputStream stream) throws IOException {
        byte[] buf = new byte[100];
        int read;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        while((read = stream.read(buf)) != -1) {
            out.write(buf, 0, read);
        }
        return new String(out.toByteArray(), Charset.forName("UTF-8"));
    }
}
