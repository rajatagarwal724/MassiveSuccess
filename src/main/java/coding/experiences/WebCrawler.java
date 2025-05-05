package coding.experiences;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebCrawler {
    private final int maxThreads;
    private final int maxPages;
    private final Set<String> visitedUrls;
    private final BlockingQueue<String> urlQueue;
    private final ExecutorService executorService;
    private final Pattern urlPattern;
    private final Set<String> results;
    private final Object lock = new Object();

    public WebCrawler(int maxThreads, int maxPages) {
        this.maxThreads = maxThreads;
        this.maxPages = maxPages;
        this.visitedUrls = ConcurrentHashMap.newKeySet();
        this.urlQueue = new LinkedBlockingQueue<>();
        this.executorService = Executors.newFixedThreadPool(maxThreads);
        this.urlPattern = Pattern.compile("href=\"(https?://[^\"]+)\"");
        this.results = ConcurrentHashMap.newKeySet();
    }

    public void startCrawling(String startUrl) {
        urlQueue.add(startUrl);
        visitedUrls.add(startUrl);

        for (int i = 0; i < maxThreads; i++) {
            executorService.submit(new CrawlerTask());
        }

        // Wait for all tasks to complete
        executorService.shutdown();
        try {
            executorService.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private class CrawlerTask implements Runnable {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted() && results.size() < maxPages) {
                try {
                    String url = urlQueue.poll(1, TimeUnit.SECONDS);
                    if (url == null) {
                        if (urlQueue.isEmpty()) {
                            break;
                        }
                        continue;
                    }

                    crawlUrl(url);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        private void crawlUrl(String url) {
            try {
                URL urlObj = new URL(url);
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(urlObj.openStream()))) {
                    StringBuilder content = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line);
                    }

                    // Add URL to results
                    synchronized (lock) {
                        if (results.size() < maxPages) {
                            results.add(url);
                            System.out.println("Crawled: " + url);
                        }
                    }

                    // Find new URLs
                    Matcher matcher = urlPattern.matcher(content);
                    while (matcher.find()) {
                        String newUrl = matcher.group(1);
                        if (visitedUrls.add(newUrl)) {
                            urlQueue.offer(newUrl);
                        }
                    }

                    // Rate limiting
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                System.err.println("Error crawling " + url + ": " + e.getMessage());
            }
        }
    }

    public Set<String> getResults() {
        return new HashSet<>(results);
    }

    public static void main(String[] args) {
        WebCrawler crawler = new WebCrawler(5, 10); // 5 threads, max 10 pages
        crawler.startCrawling("https://example.com");
        
        System.out.println("\nCrawled URLs:");
        crawler.getResults().forEach(System.out::println);
    }
} 