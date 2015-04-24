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
        res.setStatus(HttpServletResponse.SC_OK);
        if (req.getPathInfo().equals("/capacity")) {
            sendCapacityResponse(res);
            return;
        }
        res.setContentType("text/plain");
        res.getWriter().write(getReport());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    private String getReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("Status for hub: ").append(this.getRegistry().getHub().getUrl()).append("\n\n");

        ProxySet proxySet = getRegistry().getAllProxies();
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

            sb.append("Max concurrent sessions: ").append(proxy.getMaxNumberOfConcurrentTestSessions()).append('\n');
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

    private void sendCapacityResponse(HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write(String.format("{ \"maxConcurrentSessions\": %d }", calculateCapacity()));
    }

    private int calculateCapacity() {
        int counter = 0;
        for (RemoteProxy p : getRegistry().getAllProxies()) {
            counter += p.getMaxNumberOfConcurrentTestSessions();
        }
        return counter;
    }
}
