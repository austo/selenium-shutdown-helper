package com.moraustin;

import com.moraustin.util.HttpUtils;
import org.openqa.grid.common.RegistrationRequest;
import org.openqa.grid.internal.Registry;
import org.openqa.grid.internal.TestSession;
import org.openqa.grid.internal.TestSlot;
import org.openqa.grid.selenium.proxy.DefaultRemoteProxy;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
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
        logger.info("New proxy instantiated for " + this);
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
        synchronized (this) {
            if (this.remainingSessions == 0) {
                logger.warning("Cannot forward any more tests to node " + this);
                return;
            }
            super.beforeSession(session);
        }
    }

    @Override
    public TestSession getNewSession(Map<String, Object> requestedCapability) {
        logger.info("Trying to create a new session on node " + this);

        if (!hasCapability(requestedCapability)) {
            logger.info("Node " + this + " has no matching capability");
            return null;
        }

        // don't accept the new session request if we've exceeded our session budget
        if (decrementedCounterHasSessionRemaining()) {
            // any slot left at all?
            if (getTotalUsed() >= getMaxNumberOfConcurrentTestSessions()) {
                logger.info("Node " + this + " has no free slots");
                return null;
            }
            // any slot left for the given app ?
            for (TestSlot testslot : getTestSlots()) {
                TestSession session = testslot.getNewSession(requestedCapability);

                if (session != null) {
                    return session;
                }
            }
            logger.warning("Node " + this + " has no test slots remaining");
        } else {
            logger.warning("Node " + this + " cannot create any more sessions before refresh");
        }
        return null;
    }

    public int getRemainingSessions() {
        return remainingSessions;
    }

    public int getTotalAllowedSessions() {
        return totalAllowedSessions;
    }

    private synchronized boolean decrementedCounterHasSessionRemaining() {
        if (this.remainingSessions == 0) {
            return false;
        }
        --this.remainingSessions;
        return true;
    }

    private synchronized boolean canReleaseNode() {
        if (this.isBusy()) {
            logger.info("Node " + this + " is busy and cannot be released");
            return false;
        }
        if (this.remainingSessions == 0) {
            logger.info("Node " + this + " has no sessions remaining and can be released");
            return true;
        }
        logger.info("Node " + this + " has " + remainingSessions + " sessions remaining and will not be released");
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

    @Override
    public String toString() {
        return "host: " + getRemoteHost();
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
                        proxy.teardown();
                        logger.info("Node " + proxy + " has been released successfully from the hub");
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
            logger.info("Node " + this + " has shut down successfully");
        }
    }
}
