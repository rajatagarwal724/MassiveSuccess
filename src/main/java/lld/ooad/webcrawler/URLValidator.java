package lld.ooad.webcrawler;

import java.net.URL;
import java.net.MalformedURLException;

public class URLValidator {
    public boolean isValid(String url) {
        try {
            new URL(url);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    public boolean isSameDomain(String url1, String url2) {
        try {
            URL u1 = new URL(url1);
            URL u2 = new URL(url2);
            return u1.getHost().equals(u2.getHost());
        } catch (MalformedURLException e) {
            return false;
        }
    }
} 