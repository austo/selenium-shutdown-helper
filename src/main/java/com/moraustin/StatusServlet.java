package com.moraustin;

import org.openqa.grid.internal.Registry;
import org.openqa.grid.web.servlet.RegistryBasedServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;


public class StatusServlet extends RegistryBasedServlet {

    private static final Logger logger = Logger.getLogger(NodeShutdownServlet.class.getName());

    public static final String CONTENT_TYPE_HTML = "text/html";
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_XML_TEXT = "text/xml";
    public static final String CONTENT_TYPE_XML_APPLICATION = "application/xml";

    public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";

    public StatusServlet(Registry registry) {
        super(registry);
    }

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

    @SuppressWarnings("unchecked")
    private String getContentType(HttpServletRequest req) {
        ArrayList acceptHeaders = Collections.list(req.getHeaders("Accept"));
        if (acceptHeaders.isEmpty()) {
            return CONTENT_TYPE_HTML;
        }
        String acceptHeader = (String) acceptHeaders.get(0);
        return acceptHeader.split(",")[0];
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

    public enum Status {
        SUCCESS(HttpServletResponse.SC_OK, "The application is running.", "black"),
        LOADING(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "The application is loading.", "black"),//TODO make loading
        ERROR(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error occurred connecting to the database.", "red");
        private int responseCode;
        private String responseMessage;
        private String color;

        private Status(int responseCode, String responseMessage, String color) {
            this.responseCode = responseCode;
            this.responseMessage = responseMessage;
            this.color = color;
        }

        public int getResponseCode() {
            return responseCode;
        }

        public String getResponseMessage() {
            return responseMessage;
        }

        public String getColor() {
            return color;
        }
    }

    protected enum MediaType {
        HTML {
            @Override
            public String getStatusPage(String appName, Status status, boolean getVersion) {
                StringBuilder html = new StringBuilder();
                String title = appName + " Status";
                String styles = "style=\"color:" + status.getColor() + ";\"";

                html.append("<html>");
                html.append("<head>");
                html.append("<title>" + title + "</title>");
                html.append("</head>");
                html.append("<body>");
                html.append("<h4>" + title + "</h4>");
                html.append("<p " + styles + ">" + status.getResponseMessage() + "</p>");
//                if (getVersion) {
//                    html.append("<h4>Version</h4>");
//                    html.append("<p id=\"version\">" + WebAppVersion.getVersion() + "</p>");
//                }
                html.append("<h4>Hostname</h4>");
                html.append("<p id=\"hostname\">").append(HostnameHelper.getHostname()).append("</p>");
                html.append("</body>");
                html.append("</html>");

                return html.toString();
            }

            @Override
            public String getContentType() {
                return CONTENT_TYPE_HTML;
            }
        },
        JSON {
            @Override
            public String getStatusPage(String appName, Status status, boolean getVersion) {
                StringBuilder json = new StringBuilder();

                json.append("{");
                json.append("\"applicationName\": \"").append(appName).append("\",");
                json.append("\"statusMessage\": \"").append(status.getResponseMessage()).append("\",");
//                if (getVersion) {
//                    json.append("\"version\": \"").append(WebAppVersion.getVersion()).append("\",");
//                }
                json.append("\"hostname\": \"").append(HostnameHelper.getHostname()).append("\"");
                json.append("}");

                return json.toString();
            }

            @Override
            public String getContentType() {
                return CONTENT_TYPE_JSON;
            }

        },
        XML {
            @Override
            public String getStatusPage(String appName, Status status, boolean getVersion) {
                StringBuilder xml = new StringBuilder();

                xml.append("<status>");
                xml.append("<applicationName>" + appName + "</applicationName>");
                xml.append("<statusMessage>" + status.getResponseMessage() + "</statusMessage>");
//                if (getVersion) {
//                    xml.append("<version>" + WebAppVersion.getVersion() + "</version>");
//                }
                xml.append("<hostname>").append(HostnameHelper.getHostname()).append("</hostname>");
                xml.append("</status>");

                return xml.toString();
            }

            @Override
            public String getContentType() {
                return CONTENT_TYPE_XML_TEXT;
            }

        };

        public abstract String getStatusPage(String appName, Status status, boolean getVersion);

        public abstract String getContentType();
    }
}