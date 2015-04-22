package com.moraustin;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.StringTokenizer;

import static com.moraustin.NodeStatusServlet.*;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class NodeStatusServletTest {

    private static Map<String, String> EXPECTED_RESPONSES = new ImmutableMap.Builder<String, String>()
            .put(CONTENT_TYPE_HTML, "<html><head><title>selenium-shutdown-helper Status</title></head>" +
                    "<body><h4>selenium-shutdown-helper Status</h4>" +
                    "<p style=\"color: black;\">The application is running.</p>" +
                    "<h4>Hostname</h4>" +
                    "<p id=\"hostname\">SHE-MB579.surveysampling.com</p></body></html>")
            .put(CONTENT_TYPE_JSON, "{" +
                    "\"applicationName\": \"selenium-shutdown-helper\"," +
                    "\"statusMessage\": \"" + STATUS_MESSAGE + "\"," +
                    "\"hostname\": \"" + HostnameHelper.getHostname() + "\"" +
                    "}")
            .put(CONTENT_TYPE_XML_TEXT, "<status>" +
                    "<applicationName>selenium-shutdown-helper</applicationName>" +
                    "<statusMessage>" + STATUS_MESSAGE + "</statusMessage>" +
                    "<hostname>" + HostnameHelper.getHostname() + "</hostname>" +
                    "</status>")
            .build();


    @Test
    public void shouldHandleEchoGetRequest() throws ServletException, IOException {
        StringWriter writer = new StringWriter();

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);

        when(req.getHeaders("Accept")).thenReturn(new StringTokenizer(CONTENT_TYPE_HTML));
        when(req.getPathInfo()).thenReturn("/echo/hello");
        when(res.getWriter()).thenReturn(new PrintWriter(writer));

        NodeStatusServlet servlet = new NodeStatusServlet();
        servlet.doGet(req, res);

        verify(res).setStatus(200);
        verify(res).setContentType(CONTENT_TYPE_HTML);
        verify(res).setHeader(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        assertTrue(writer.toString().equals("hello"));
    }

    @Test
    public void shouldGetCorrectStatusMessageForEachContentType() throws ServletException, IOException {

        NodeStatusServlet servlet = new NodeStatusServlet();

        for(String key : EXPECTED_RESPONSES.keySet()) {
            validateRequest(servlet, key);
        }
    }

    private void validateRequest(NodeStatusServlet servlet, String contentType) throws IOException, ServletException {
        StringWriter writer = new StringWriter();

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);

        when(req.getHeaders("Accept")).thenReturn(new StringTokenizer(contentType));
        when(req.getPathInfo()).thenReturn("");
        when(res.getWriter()).thenReturn(new PrintWriter(writer));

        servlet.doGet(req, res);

        verify(res).setStatus(200);
        verify(res).setContentType(contentType);
        verify(res).setHeader(ACCESS_CONTROL_ALLOW_ORIGIN, "*");

        assertTrue(writer.toString().equals(EXPECTED_RESPONSES.get(contentType)));
    }
}