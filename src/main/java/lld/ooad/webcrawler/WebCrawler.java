// package lld.ooad.webcrawler;

// import java.util.Set;
// import java.util.concurrent.*;
// import java.util.concurrent.atomic.AtomicInteger;

// public class WebCrawler {
//     private final int maxThreads;
//     private final int maxPages;
//     private final Set<String> visitedUrls;
//     private final BlockingQueue<String> urlQueue;
//     private final ExecutorService executorService;
//     private final Set<String> results;
//     private final AtomicInteger pagesCrawled;
//     private final URLValidator urlValidator;
//     private final HtmlParser htmlParser;
//     private final Object lock = new Object();

//     public WebCrawler(int maxThreads, int maxPages, HtmlParser htmlParser) {
//         this.maxThreads = maxThreads;
//         this.maxPages = maxPages;
//         this.visitedUrls = ConcurrentHashMap.newKeySet();
//         this.urlQueue = new LinkedBlockingQueue<>();
//         this.executorService = Executors.newFixedThreadPool(maxThreads);
//         this.results = ConcurrentHashMap.newKeySet();
//         this.pagesCrawled = new AtomicInteger(0);
//         this.urlValidator = new URLValidator();
//         this.htmlParser = htmlParser;
//     }

//     public void startCrawling(String startUrl) {
//         if (!urlValidator.isValid(startUrl)) {
//             throw new IllegalArgumentException("Invalid start URL: " + startUrl);
//         }

//         urlQueue.add(startUrl);
//         visitedUrls.add(startUrl);

//         // Start producer and consumer threads
//         for (int i = 0; i < maxThreads / 2; i++) {
//             executorService.submit(new UrlProducer());
//             executorService.submit(new UrlConsumer());
//         }

//         // Wait for completion
//         executorService.shutdown();
//         try {
//             executorService.awaitTermination(1, TimeUnit.HOURS);
//         } catch (InterruptedException e) {
//             Thread.currentThread().interrupt();
//             executorService.shutdownNow();
//         }
//     }

//     private class UrlProducer implements Runnable {
//         @Override
//         public void run() {
//             while (!Thread.currentThread().isInterrupted() && pagesCrawled.get() < maxPages) {
//                 try {
//                     String url = urlQueue.poll(1, TimeUnit.SECONDS);
//                     if (url == null) {
//                         if (urlQueue.isEmpty()) {
//                             break;
//                         }
//                         continue;
//                     }

//                     // Rate limiting
//                     Thread.sleep(1000);

//                     String content = fetchContent(url);
//                     if (content == null) {
//                         continue;
//                     }

//                     // Extract and add new URLs
//                     Set<String> newUrls = htmlParser.parseUrls(content, url);
//                     for (String newUrl : newUrls) {
//                         if (visitedUrls.add(newUrl)) {
//                             urlQueue.offer(newUrl);
//                         }
//                     }
//                 } catch (InterruptedException e) {
//                     Thread.currentThread().interrupt();
//                     break;
//                 }
//             }
//         }

//         private String fetchContent(String url) {
//             try {
//                 java.net.URL urlObj = new java.net.URL(url);
//                 try (java.io.BufferedReader reader = new java.io.BufferedReader(
//                         new java.io.InputStreamReader(urlObj.openStream()))) {
//                     StringBuilder content = new StringBuilder();
//                     String line;
//                     while ((line = reader.readLine()) != null) {
//                         content.append(line);
//                     }
//                     return content.toString();
//                 }
//             } catch (Exception e) {
//                 System.err.println("Error fetching content from " + url + ": " + e.getMessage());
//                 return null;
//             }
//         }
//     }

//     private class UrlConsumer implements Runnable {
//         @Override
//         public void run() {
//             while (!Thread.currentThread().isInterrupted() && pagesCrawled.get() < maxPages) {
//                 try {
//                     String url = urlQueue.poll(1, TimeUnit.SECONDS);
//                     if (url == null) {
//                         if (urlQueue.isEmpty()) {
//                             break;
//                         }
//                         continue;
//                     }

//                     // Add URL to results
//                     synchronized (lock) {
//                         if (pagesCrawled.get() < maxPages) {
//                             results.add(url);
//                             pagesCrawled.incrementAndGet();
//                             System.out.println("Crawled: " + url + " (Total: " + pagesCrawled.get() + ")");
//                         }
//                     }
//                 } catch (InterruptedException e) {
//                     Thread.currentThread().interrupt();
//                     break;
//                 }
//             }
//         }
//     }

//     public Set<String> getResults() {
//         return new HashSet<>(results);
//     }

//     public int getPagesCrawled() {
//         return pagesCrawled.get();
//     }

//     public void shutdown() {
//         executorService.shutdownNow();
//     }
// } 