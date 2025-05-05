package lld.ooad.webcrawler;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiThreadedWebCrawler {
    private final HtmlParser htmlParser;
    private final AtomicInteger maxCount;
    private final AtomicInteger processedCount;
    private volatile boolean shouldStop;

    public MultiThreadedWebCrawler(HtmlParser htmlParser, int maxCount) {
        this.htmlParser = htmlParser;
        this.maxCount = new AtomicInteger(maxCount);
        this.processedCount = new AtomicInteger(0);
        this.shouldStop = false;
    }

    public List<String> crawl(String url) throws MalformedURLException {
        String host = new URL(url).getHost();
        
        Set<String> visitedUrls = ConcurrentHashMap.newKeySet();
        visitedUrls.add(host);

        BlockingQueue<String> urlsToVisit = new LinkedBlockingQueue<>(20);
        urlsToVisit.add(url);

        Set<String> results = ConcurrentHashMap.newKeySet();
        results.add(url);

        int maxThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(maxThreads);

        CountDownLatch latch = new CountDownLatch(1);
        
        for (int i = 0; i < maxThreads; i++) {
            executorService.submit(() -> {
                try {
                    while (!shouldStop) {
                        String currentUrl = urlsToVisit.poll(10, TimeUnit.SECONDS);
                        if (currentUrl == null) {
                            if (latch.getCount() == 0) {
                                break;
                            }
                            continue;
                        }

                        // Check if we've reached the maximum count
                        if (processedCount.get() >= maxCount.get()) {
                            shouldStop = true;
                            break;
                        }

                        System.out.println("Crawling " + currentUrl);
                        List<String> nextUrls = htmlParser.getUrls(currentUrl);
                        
                        for (String nextUrl : nextUrls) {
                            if (shouldStop) break;
                            
                            URL nextUrlObj = new URL(nextUrl);
                            if (visitedUrls.add(nextUrlObj.getHost())) {
                                System.out.println("Adding " + nextUrl);
                                urlsToVisit.add(nextUrl);
                                results.add(nextUrl);
                                
                                // Increment processed count and check limit
                                if (processedCount.incrementAndGet() >= maxCount.get()) {
                                    shouldStop = true;
                                    break;
                                }
                                
                                latch.countUp();
                            }
                        }
                        latch.countDown();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            executorService.shutdown();
            try {
                executorService.awaitTermination(1, TimeUnit.HOURS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        return new ArrayList<>(results);
    }

    private class CountDownLatch {
        private AtomicInteger count;

        public CountDownLatch(int count) {
            this.count = new AtomicInteger(count);
        }

        public void countDown() {
            if (count.decrementAndGet() == 0) {
                synchronized (this) {
                    notifyAll();
                }
            }
        }

        public void countUp() {
            count.incrementAndGet();
        }

        public int getCount() {
            return count.get();
        }

        public synchronized void await() throws InterruptedException {
            while (count.get() > 0) {
                wait();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        HtmlParser parser = new DefaultHtmlPageParser();
        MultiThreadedWebCrawler crawler = new MultiThreadedWebCrawler(parser, 10);
        List<String> result = crawler.crawl("http://google.com");
        System.out.println("Crawled URLs: " + result);
        System.out.println("Total pages processed: " + crawler.processedCount.get());
    }
}