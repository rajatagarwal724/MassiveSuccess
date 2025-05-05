package lld.ooad.webcrawler;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface HtmlParser {
    // Return a list of all URLs from a webpage of the given URL
    // This is a blocking call that simulates HTTP request
    public List<String> getUrls(String url) throws IOException;
}