package com.moraustin;

import org.openqa.grid.internal.ProxySet;
import org.openqa.grid.internal.Registry;
import org.openqa.grid.internal.RemoteProxy;
import org.openqa.grid.web.servlet.RegistryBasedServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class HubStatusServlet extends RegistryBasedServlet {
    public HubStatusServlet(Registry registry) {
        super(registry);
    }

    public HubStatusServlet() {
        super(null);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/plain");
        res.setStatus(HttpServletResponse.SC_OK);
        res.getWriter().write(getReport());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    private String getReport() {
        ProxySet proxySet = getRegistry().getAllProxies();
        StringBuilder sb = new StringBuilder();
        for (RemoteProxy proxy : proxySet) {
            sb.append("Node host URL: ")
                    .append(proxy.getRemoteHost().toString()).append('\n')
                    .append("Proxy type: ")
                    .append(proxy.getClass().getSimpleName())
                    .append('\n');

            if (proxy instanceof NodeShutdownProxy) {
                NodeShutdownProxy shutdownProxy = (NodeShutdownProxy) proxy;
                sb.append("Total allowed Sessions: ").append(shutdownProxy.getTotalAllowedSessions()).append('\n');
                sb.append("Remaining sessions: ").append(shutdownProxy.getRemainingSessions()).append('\n');
            }

            sb.append("\nConfiguration:\n");

            for (Map.Entry<String, Object> entry : proxy.getConfig().entrySet()) {
                sb.append('\t')
                        .append(entry.getKey())
                        .append(": ");
                Object value = entry.getValue();
                sb.append(value == null ? "null" : value.toString())
                        .append('\n');
            }
            sb.append("\n\n");
        }
        return sb.toString();
    }

}
