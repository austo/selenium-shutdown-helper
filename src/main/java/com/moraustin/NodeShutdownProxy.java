package com.moraustin;

import org.openqa.grid.common.RegistrationRequest;
import org.openqa.grid.common.exception.RemoteUnregisterException;
import org.openqa.grid.internal.Registry;
import org.openqa.grid.internal.TestSession;
import org.openqa.grid.selenium.proxy.DefaultRemoteProxy;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NodeShutdownProxy extends DefaultRemoteProxy {

    private static final Logger logger = Logger.getLogger(NodeShutdownProxy.class.getName());

    private volatile int remainingSessions;
    private final int totalAllowedSessions;
    private NodePoller poller;

    public NodeShutdownProxy(RegistrationRequest request, Registry registry) throws IOException {
        super(request, registry);
        logger.info("New proxy instantiated for " + getRemoteHost().getHost());
        logger.info("Attaching node " + this.getId());
        logger.info("Remote host is " + this.getRemoteHost());

        Properties props = getProxyProperties();
        totalAllowedSessions = Integer.parseInt((String) props.get(Constants.UNIQUE_SESSION_COUNT));
        remainingSessions = totalAllowedSessions;
    }

    @Override
    public void startPolling() {
        super.startPolling();
        poller = new NodePoller(this);
        poller.start();
    }

    @Override
    public void stopPolling() {
        super.stopPolling();
        poller.interrupt();
    }

    @Override
    public void beforeSession(TestSession session) {
        String ip = getRemoteHost().getHost();
        if (decrementedCounterIsNotZero()) {
            super.beforeSession(session);
            return;
        }
        logger.warning("Cannot forward any more tests to " + ip);
    }

    public int getRemainingSessions() {
        return remainingSessions;
    }

    public int getTotalAllowedSessions() {
        return totalAllowedSessions;
    }

    private synchronized boolean decrementedCounterIsNotZero() {
        if (this.remainingSessions == 0) {
            return false;
        }
        --this.remainingSessions;
        return true;
    }

    private synchronized boolean canReleaseNode() {
        final String ip = this.getRemoteHost().toString().replaceAll("^.*?://", "");
        if (this.isBusy()) {
            logger.info(ip + " is busy and cannot be released");
            return false;
        }
        if (this.remainingSessions == 0) {
            logger.info(ip + " has no sessions remaining and can be released");
            return true;
        }
        logger.info(ip + " has " + remainingSessions + " sessions remaining and will not be released");
        return false;
    }

    static Properties getProxyProperties() throws IOException {
        Properties props = new Properties();
        String propertiesFilePath = System.getProperty(Constants.PROXY_PROPERTIES_PATH);

        if (propertiesFilePath == null) {
            String propFileName = NodeShutdownProxy.class.getSimpleName() + ".properties";
            logger.info(String.format("System property \"%s\" is not set. Reading properties file from %s",
                    Constants.PROXY_PROPERTIES_PATH, propFileName));
            InputStream stream = NodeShutdownProxy.class.getResourceAsStream(propFileName);
            props.load(stream);
            stream.close();
            return props;
        }
        logger.info("Reading properties file from " + propertiesFilePath);
        File propFile = new File(propertiesFilePath);
        FileInputStream stream = new FileInputStream(propFile);
        props.load(stream);
        stream.close();
        return props;
    }

    /**
     * Polls continuously to decide if the current node can be cleaned up. If so,
     * releases node from the grid and issues a shutdown request to the node.
     */
    static class NodePoller extends Thread {
        private NodeShutdownProxy proxy = null;

        public NodePoller(NodeShutdownProxy proxy) {
            this.proxy = proxy;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    if (proxy.canReleaseNode()) {
                        shutdownNode();
                        logger.info(proxy.getRemoteHost().getHost() + " has been released successfully from the hub");
                        Thread.sleep(Constants.NODE_SHUTDOWN_INTERVAL);
                        proxy.addNewEvent(new RemoteUnregisterException(String.format("taking proxy %s offline", this.getId())));
                        return;
                    }
                    Thread.sleep(Constants.NODE_POLLING_INTERVAL);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }

        private void shutdownNode() {
            try {
                URL shutdownUrl = new URL(String.format(
                        "%s/extra/%s", proxy.getRemoteHost(), NodeShutdownServlet.class.getSimpleName()));
                logger.info(String.format("sending shutdown request to %s", shutdownUrl));
                HttpURLConnection connection = (HttpURLConnection) shutdownUrl.openConnection();
                connection.setRequestMethod("POST");
                int responseCode = connection.getResponseCode();
                logger.info(String.format(
                        "received %d response from node: %s\n",
                        responseCode,
                        HttpUtils.readResponse(connection)));
            } catch (IOException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
                return;
            }
            logger.info("Node " + proxy.getRemoteHost().getHost() + " has shut down successfully");
        }
    }
}
