package com.moraustin;

import org.junit.Test;
import org.openqa.grid.internal.Registry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.StringTokenizer;

import static com.moraustin.StatusServlet.ACCESS_CONTROL_ALLOW_ORIGIN;
import static com.moraustin.StatusServlet.CONTENT_TYPE_HTML;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class StatusServletTest {

    @Test
    public void shouldHandleBasicGetRequest() throws ServletException, IOException {
        StringWriter writer = new StringWriter();

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);

        when(req.getHeaders("Accept")).thenReturn(new StringTokenizer(CONTENT_TYPE_HTML));
        when(req.getPathInfo()).thenReturn("/echo/hello");
        when(res.getWriter()).thenReturn(new PrintWriter(writer));

        StatusServlet servlet = new StatusServlet();
        servlet.doGet(req, res);

        verify(res).setStatus(200);
        verify(res).setContentType(CONTENT_TYPE_HTML);
        verify(res).setHeader(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        assertTrue(writer.toString().equals("hello"));
    }

}