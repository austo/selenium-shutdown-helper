package com.moraustin.util;

import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import static org.junit.Assert.*;

public class UrlComparatorTest {

    private static String[] URL_STRINGS = new String[] {
            "https://10.37.21.107:5556",
            "http://10.1.21.40:5556",
            "http://10.1.21.46:5556",
            "http://10.1.21.46:5555",
            "http://10.37.21.101:5556",
            "http://10.37.21.101:5555",
            "http://10.37.21.107:5555"
    };

    @Test
    public void differentHostsShouldSortCorrectly() throws MalformedURLException {
        Comparator<URL> comparator = new UrlComparator();
        URL first = new URL("http://10.1.21.40:5556");
        URL second = new URL("http://10.1.21.41:5556");

        assertEquals(-1, comparator.compare(first, second));
    }

    @Test
    public void differentPortsShouldSortCorrectly() throws MalformedURLException {
        Comparator<URL> comparator = new UrlComparator();
        URL first = new URL("http://10.1.21.40:5556");
        URL second = new URL("http://10.1.21.40:5555");

        assertEquals(1, comparator.compare(first, second));
    }

    @Test
    public void differentProtocolsShouldSortCorrectly() throws MalformedURLException {
        Comparator<URL> comparator = new UrlComparator();
        URL first = new URL("https://10.1.21.40:5556");
        URL second = new URL("http://10.1.21.40:5556");

        assertEquals(1, comparator.compare(first, second));
    }

    @Test
    public void shouldCorrectlySortList() throws MalformedURLException {
        Comparator<URL> comparator = new UrlComparator();
        List<URL> urls = buildUrlList();
        Collections.sort(urls, comparator);

        assertEquals(new URL("http://10.1.21.40:5556"), urls.get(0));
        assertEquals(urls.size() - 1, urls.indexOf(new URL("https://10.37.21.107:5556")));
    }

    private List<URL> buildUrlList() throws MalformedURLException {
        List<URL> urls = new LinkedList<>();
        for (String s : URL_STRINGS) {
            urls.add(new URL(s));
        }
        return urls;
    }

}