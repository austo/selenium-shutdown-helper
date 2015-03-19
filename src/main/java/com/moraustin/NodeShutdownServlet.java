package com.moraustin;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A simple servlet which basically issues a System.exit() when invoked.
 * Needs to be injected into node (not grid) to terminate node JVM.
 */
public class NodeShutdownServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // TODO: get status of node
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        shutdownNode();
    }

    protected void shutdownNode() {
        System.out.println("Shutting down the node");
        // TODO: check for API/SPI for selenium node shutdown
        System.exit(0);
    }
}