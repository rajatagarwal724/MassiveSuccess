package lld.ooad.webcrawler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleWebCrawler {

    Set<String> visitedUrls;
    Queue<String> urlsToVisit;
    int maxCount = 0;

    public SimpleWebCrawler() {
        this.visitedUrls = new HashSet<>();
        this.urlsToVisit = new LinkedList<>();
        maxCount = 100;
    }

    public void crawl(String url) throws MalformedURLException {
        int currentCount = 0;
        urlsToVisit.add(url);
        var host = new URL(url).getHost();
        visitedUrls.add(host);
        System.out.println("host: " + host);
        while (!urlsToVisit.isEmpty()) {
            String currentUrl = urlsToVisit.poll();
            try(var stream = new BufferedReader(new InputStreamReader(new URL(currentUrl).openStream()))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = stream.readLine()) != null) {
                    sb.append(line);
                }
                String content = sb.toString();
                // System.out.println("Line: " + line);
                Pattern pattern = Pattern.compile("href=\"(https?://[^\"]+)\"");
                // Pattern.compile("(https?//\\S+)");
                Matcher matcher = pattern.matcher(content);

                while (matcher.find() && currentCount <= maxCount) {
                    String newUrl = matcher.group(1);
                    URL urlObj = new URL(newUrl);
                    if (visitedUrls.contains(urlObj.getHost())) {
                        continue;
                    }
                    urlsToVisit.add(newUrl);
                    visitedUrls.add(urlObj.getHost());
                    System.out.println("new url: " + newUrl + " current count: " + currentCount + " urlObj: " + urlObj.getHost());
                    currentCount++; 
                }

                if (currentCount >= maxCount) {
                    break;
                }
            } catch (Exception e) {
                System.out.println("Exception while processing url: " + url + " Exception: " + e);
            }
        }
    }

    public static void main(String[] args) throws MalformedURLException {
        SimpleWebCrawler crawler = new SimpleWebCrawler();
        crawler.crawl("https://google.com");
    }
    
}
