package com.moraustin;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;


public class NodeStatusServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(NodeShutdownServlet.class.getName());

    public static final String CONTENT_TYPE_HTML = "text/html";
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_XML_TEXT = "text/xml";
    public static final String CONTENT_TYPE_XML_APPLICATION = "application/xml";

    public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";

    static final String STATUS_MESSAGE = "The application is running.";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        MediaType mediaType = getMediaType(req);
        res.setContentType(mediaType.getContentType());
        res.setStatus(200);
        res.setHeader(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        String pathInfo = req.getPathInfo();

        // Having a little fun
        if (pathInfo.startsWith("/echo/")) {
            res.getWriter().write(pathInfo.substring(pathInfo.lastIndexOf('/') + 1));
            return;
        }
        res.getWriter().write(mediaType.getStatusPage(getAppName()));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    String getAppName() {
        // TODO: get from MANIFEST.MF
        return "selenium-shutdown-helper";
    }

    @SuppressWarnings("unchecked")
    protected MediaType getMediaType(HttpServletRequest request) {
        ArrayList<String> mediaTypesList = Collections.list(request.getHeaders("Accept"));

        if (mediaTypesList.isEmpty()) {
            return MediaType.HTML;
        }

        String mediaType = mediaTypesList.get(0).split(",")[0];

        if (mediaType.equals(CONTENT_TYPE_JSON)) {
            return MediaType.JSON;
        } else if (mediaType.equals(CONTENT_TYPE_XML_TEXT) || mediaType.equals(CONTENT_TYPE_XML_APPLICATION)) {
            return MediaType.XML;
        }
        return MediaType.HTML;
    }

    protected enum MediaType {
        HTML {
            @Override
            public String getStatusPage(String appName) {
                String styles = "style=\"color: black;\"";

                return "<html>" + "<head>" + "<title>" + appName + " Status</title></head><body><h4>" + appName + " Status</h4>" + "<p " + styles + ">" + STATUS_MESSAGE + "</p>" + "<h4>Hostname</h4>" + "<p id=\"hostname\">" + HostnameHelper.getHostname() + "</p>" + "</body>" + "</html>";
            }

            @Override
            public String getContentType() {
                return CONTENT_TYPE_HTML;
            }
        },
        JSON {
            @Override
            public String getStatusPage(String appName) {
                return "{" +
                        "\"applicationName\": \"" + appName + "\"," +
                        "\"statusMessage\": \"" + STATUS_MESSAGE + "\"," +
                        "\"hostname\": \"" + HostnameHelper.getHostname() + "\"" +
                        "}";
            }

            @Override
            public String getContentType() {
                return CONTENT_TYPE_JSON;
            }

        },
        XML {
            @Override
            public String getStatusPage(String appName) {
                return "<status>" +
                        "<applicationName>" + appName + "</applicationName>" +
                        "<statusMessage>" + STATUS_MESSAGE + "</statusMessage>" +
                        "<hostname>" + HostnameHelper.getHostname() + "</hostname>" +
                        "</status>";
            }

            @Override
            public String getContentType() {
                return CONTENT_TYPE_XML_TEXT;
            }

        };

        public abstract String getStatusPage(String appName);

        public abstract String getContentType();
    }
}