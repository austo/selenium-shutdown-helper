package com.moraustin;

import org.apache.http.client.fluent.Request;
import org.openqa.grid.common.RegistrationRequest;
import org.openqa.grid.internal.Registry;
import org.openqa.grid.internal.TestSession;
import org.openqa.grid.selenium.proxy.DefaultRemoteProxy;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NodeShutdownProxy extends DefaultRemoteProxy {

    private static final Logger log = Logger.getLogger(DefaultRemoteProxy.class.getName());

    private volatile int counter;
    private NodePoller poller;

    public NodeShutdownProxy(RegistrationRequest request, Registry registry) throws IOException {
        super(request, registry);
        System.out.printf("New proxy instantiated for %s\n", getRemoteHost().getHost());
        System.out.printf("Attaching node %s\n", this.getId());
        System.out.printf("Remote host is %s\n", this.getRemoteHost());
        InputStream stream = NodeShutdownProxy.class.getResourceAsStream(NodeShutdownProxy.class.getSimpleName() + ".properties");
        Properties props = new Properties();
        props.load(stream);
        counter = Integer.parseInt((String) props.get("UniqueSessionCount"));
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
        System.out.printf("Cannot forward any more tests to %s\n", ip);
    }

    private synchronized boolean decrementedCounterIsNotZero() {
        if (this.counter == 0) {
            return false;
        }
        --this.counter;
        return true;
    }

    private synchronized boolean canReleaseNode() {
        final String ip = this.getRemoteHost().getHost();
        if (this.isBusy()) {
            System.out.printf("%s is busy and cannot be released\n", ip);
            return false;
        }
        if (this.counter == 0) {
            System.out.printf("%s has no sessions remaining and can be released\n", ip);
            return true;
        }
        System.out.printf("%s has %d sessions remaining and will not be released\n", ip, counter);
        return false;
    }

    /**
     * This class is used to poll continuously to decide if the current node can be cleaned up. If it can be cleaned up,
     * this class helps in un-hooking the node from the grid and also issuing a shutdown request to the node.
     */
    static class NodePoller extends Thread {
        private NodeShutdownProxy proxy = null;

        public NodePoller(NodeShutdownProxy proxy) {
            this.proxy = proxy;
        }

        @Override
        public void run() {
            while (true) {
                if (proxy.canReleaseNode()) {
                    proxy.getRegistry().removeIfPresent(proxy);
                    System.out.printf("%s has been released successfully from the hub\n",
                            proxy.getRemoteHost().getHost());
                    shutdownNode();
                    return;
                }
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }

        private void shutdownNode() {
            try {
                Request.Post("http://" + proxy.getRemoteHost().getHost() +
                        ":" + proxy.getRemoteHost().getPort() + "/extra/" +
                        NodeShutdownServlet.class.getSimpleName()).execute();
            } catch (IOException e) {
                log.log(Level.SEVERE, e.getMessage(), e);
                return;
            }
            System.out.printf("Node %s has shut down successfully\n", proxy.getRemoteHost().getHost());
        }
    }
}
