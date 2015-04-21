package com.moraustin;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;


public class StatusServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(NodeShutdownServlet.class.getName());

    public static final String CONTENT_TYPE_HTML = "text/html";
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_XML_TEXT = "text/xml";
    public static final String CONTENT_TYPE_XML_APPLICATION = "application/xml";

    public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType(getContentType(req));
        res.setStatus(200);
        res.setHeader(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        String pathInfo = req.getPathInfo();
        if (pathInfo.startsWith("/echo")) {
            res.getWriter().write(pathInfo.substring(pathInfo.lastIndexOf('/') + 1));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

    }

    private String getContentType(HttpServletRequest req) {
        ArrayList acceptHeaders = Collections.list(req.getHeaders("Accept"));
        if (acceptHeaders.isEmpty()) {
            return CONTENT_TYPE_HTML;
        }
        String acceptHeader = (String) acceptHeaders.get(0);
        return acceptHeader.split(",")[0];
    }
}