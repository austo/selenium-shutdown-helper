package com.moraustin;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.openqa.grid.common.RegistrationRequest;
import org.openqa.grid.internal.Registry;
import org.openqa.grid.internal.TestSession;
import org.openqa.grid.selenium.proxy.DefaultRemoteProxy;
import org.apache.http.client.HttpClient;

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
        System.out.println("New proxy instantiated for the machine :" + getRemoteHost().getHost());
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
        System.out.println("Cannot forward any more tests to this proxy " + ip);
    }

    /**
     * Invoke this method to decide if the node has reached its max. test execution value and if the node should be
     * picked up for recycling.
     *
     * @return - <code>true</code> if the node can be released and shutdown as well.
     */
    public synchronized boolean shouldNodeBeReleased() {
        if (this.counter == 0) {
            System.out.println("The node " + getRemoteHost().getHost() + "can be released now");
            return true;
        }
        return false;
    }

    private synchronized boolean decrementedCounterIsNotZero() {
        if (this.counter == 0) {
            return false;
        }
        --this.counter;
        return true;
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
                boolean isBusy = proxy.isBusy();
                boolean canRelease = proxy.shouldNodeBeReleased();
                if (!isBusy && canRelease) {
                    proxy.getRegistry().removeIfPresent(proxy);
                    System.out.println(proxy.getRemoteHost().getHost() + " has been released successfully from the hub");
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
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://" + proxy.getRemoteHost().getHost() +
                    ":" + proxy.getRemoteHost().getPort() + "/extra/" +
                    NodeShutdownServlet.class.getSimpleName());
            try {
                client.execute(post);
            } catch (IOException e) {
                log.log(Level.SEVERE, e.getMessage(), e);
            }
            System.out.println("Node " + proxy.getRemoteHost().getHost() + " shut-down successfully.");
        }
    }
}
