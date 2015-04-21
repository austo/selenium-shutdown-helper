package com.moraustin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.logging.Logger;

public class HostnameHelper {

    private static final Logger logger = Logger.getLogger(HostnameHelper.class.getName());

    private static class Holder {

        public static final String hostname = fetchHostnameFromProcess();

        private static String fetchHostnameFromProcess() {
            logger.info("fetching hostname from subprocess...");
            try {
                Runtime runtime = Runtime.getRuntime();
                Process process = runtime.exec("hostname");
                String output = "";
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String s;
                    while ((s = reader.readLine()) != null) {
                        output += s;
                    }
                }
                return output.replace("\n", "");
            } catch (Throwable t) {
                logger.severe(String.format("unable to fetch hostname from subprocess: %s", t.toString()));
                return "";
            }
        }
    }

    private HostnameHelper() {
    }

    public static String getHostname() {
        return Holder.hostname;
    }
}