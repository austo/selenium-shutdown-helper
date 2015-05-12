package com.moraustin;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A simple servlet which shuts down its own JVM when invoked.
 * Needs to be injected into node (not hub) to terminate node's JVM.
 */
public class NodeShutdownServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(NodeShutdownServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // TODO: get status of node
        res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        res.getWriter().print("vous n'avez pas la chance, mon vieux\n");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        logger.info("received shutdown request");
        res.setStatus(200);
        res.setContentType("application/json");
        res.getWriter().print("{ \"message\": \"shutting down\" }");
        new Terminator().start();
        logger.info("shutdown thread launched");
    }

    static class Terminator extends Thread {

        @Override
        public void run() {
            try {
                Thread.sleep(Constants.NODE_SHUTDOWN_INTERVAL); // shutdown after response is sent
                logger.warning("Shutting down the node's JVM");
                System.exit(0);
            } catch (InterruptedException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }
}