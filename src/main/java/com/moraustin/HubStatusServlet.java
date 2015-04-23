package com.moraustin;

import org.openqa.grid.internal.ProxySet;
import org.openqa.grid.internal.Registry;
import org.openqa.grid.internal.RemoteProxy;
import org.openqa.grid.web.servlet.RegistryBasedServlet;
import org.openqa.selenium.Proxy;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Iterator;
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
        StringBuilder sb = new StringBuilder();
        Registry registry = getRegistry();
        final ProxySet proxySet = registry.getAllProxies();
        for (RemoteProxy proxy : proxySet) {
            sb.append(proxy.getRemoteHost().toString())
                    .append('\n')
                    .append("Configuration:\n");

            for (Map.Entry<String, Object> entry : proxy.getConfig().entrySet()) {
                sb.append('\t')
                        .append(entry.getKey())
                        .append(": ");
                Object value = entry.getValue();
                sb.append(value == null ? "null" : value.toString())
                        .append(",\n");
            }
        }
        res.getWriter().write(sb.toString());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

}
