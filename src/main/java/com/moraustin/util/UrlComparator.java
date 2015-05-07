package com.moraustin.util;

import org.openqa.grid.internal.RemoteProxy;

import java.net.URL;
import java.util.Comparator;

public class UrlComparator implements Comparator<URL> {
    @Override
    public int compare(URL first, URL second) {

        if (first.equals(second)) {
            return 0;
        }
        String firstProtocol = first.getProtocol();
        String secondProtocol = second.getProtocol();
        if (!firstProtocol.equals(secondProtocol)) {
            return firstProtocol.compareTo(secondProtocol);
        }
        String firstHost = first.getHost();
        String secondHost = second.getHost();
        if (!firstHost.equals(secondHost)) {
            return firstHost.compareTo(secondHost);
        }
        int firstPort = first.getPort();
        int secondPort = second.getPort();
        if (firstPort == secondPort) {
            return 0;
        }
        if (firstPort < secondPort) {
            return -1;
        }
        return 1;
    }
}


