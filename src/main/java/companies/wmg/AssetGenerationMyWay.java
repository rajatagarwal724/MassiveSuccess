package companies.wmg;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class AssetGenerationMyWay {

    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    private final AtomicBoolean running = new AtomicBoolean(false);
    private static final String baseUrl = "http://localhost:8080";
    private static final Duration HTTP_TIMEOUT = Duration.ofSeconds(30);
    private static final Integer MAX_RETRIES = 3;
    private static final Duration RETRY_DELAY = Duration.ofSeconds(10);
    private static final Duration POLLING_INTERVAL = Duration.ofSeconds(5);
    private static final HttpClient httpClient = HttpClient
            .newBuilder()
            .connectTimeout(HTTP_TIMEOUT)
            .build();
    private static final ObjectMapper objectMapper = new ObjectMapper();


    public static void main(String[] args) {
        var client = new AssetGenerationMyWay();
        client.start();
    }

    public void start() {
        if (running.compareAndSet(false, true)) {
            executorService.submit(() -> {
                processOrders();
            });
        }
    }

    public void stop() {
        if (running.compareAndSet(true, false)) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    private void processOrders() {
        while (running.get()) {
            // 1. get Order from queue
            // 2. queue asset generation
            // 3. wait for asset generation to complete
            // 4. retrieve generated assets and their details
            // 5. queue Metadata generation
            // 6. wait for metadata generation to complete
            // 7. retrieve generated metadata
            // 8. submit completed order

            var order = fetchOrder();
            if (null != order) {
                processOrder(order);
            } else {
                // No orders available, wait before checking again
                try {
                    Thread.sleep(POLLING_INTERVAL.toMillis());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

        }
    }

    private void processOrder(Object order) {
        log.info("Processing order", "order", order);
        try {
            List<Object> assets = new ArrayList<>();
            List<Object> assetDetails = new ArrayList<>();
            var assetGenerationSuccess = generateAssets(order, assets, assetDetails);
            if (!assetGenerationSuccess) {
                submitFailedOrder(order);
                return;
            }
//            assets.setAssetDetails(assetDetails);
//            order.setAssets(assets);
            var metadata = generateMetadata(order);
            if (null == metadata) {
                submitFailedOrder(order);
                return;
            }
            submitShippableOrder(order, assets, assetDetails, metadata);

        } catch (Exception e) {
            log.error("Error occurred while processing order", "error", e);
            submitFailedOrder(order);
        }
    }

    private void submitShippableOrder(Object order, List<Object> assets, List<Object> assetDetails, Object metadata) {
        var shippableOrder = new Object();
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/shippable-order"))
                    .timeout(HTTP_TIMEOUT)
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(shippableOrder)))
                    .build();
            executeWithRetry(httpRequest);
        } catch (Exception e) {
            log.error("Error occurred while processing order", "error", e);
        }
    }

    private void submitFailedOrder(Object order) {
        var failedOrder = new Object();
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/failed-order"))
                    .timeout(HTTP_TIMEOUT)
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(failedOrder)))
                    .build();
            executeWithRetry(httpRequest);
        } catch (Exception e) {
            log.error("Error occurred while processing order", "error", e);
        }
    }

    private Object generateMetadata(Object order) {
        try {
            var metadataGenerationRequest = new Object();

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/metadata-generation"))
                    .timeout(HTTP_TIMEOUT)
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(metadataGenerationRequest)))
                    .build();
            var response = executeWithRetry(httpRequest);
            if (response.statusCode() != 200) {
                return null;
            }
            var metadataGenerationResponse = objectMapper.readValue(response.body(), Object.class);
            return waitForJobCompletion(metadataGenerationResponse, "METADATA");
        } catch (Exception e) {
            log.error("Error occurred while processing order", "error", e);
            return null;
        }
    }

    private boolean generateAssets(Object order, List<Object> assets, List<Object> assetDetails) {
        try {
            var assetGenerationRequest = new Object();

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/asset-generation"))
                    .timeout(HTTP_TIMEOUT)
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(assetGenerationRequest)))
                    .build();
            var response = executeWithRetry(httpRequest);
            if (response.statusCode() != 200) {
                return false;
            }
            var assetGenerationResponse = objectMapper.readValue(response.body(), Object.class);
            var assetGenerationStatus = waitForJobCompletion(assetGenerationResponse, "ASSET");
            if (null == assetGenerationStatus) {
                return false;
            }
            assets.add(new Object());
            assetDetails.add(new Object());
        } catch (Exception e) {
            log.error("Error occurred while processing order", "error", e);
        }
        return true;
    }

    private Object waitForJobCompletion(Object assetGenerationResponse, String asset) {
        int maxRetries = 10;
        int retryDelay = 1000; // in milliseconds
        int attempts = 0;
        while (attempts < maxRetries) {
            try {

                // Check the status of the job
                HttpRequest httpRequest = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/asset-generation/status/" + assetGenerationResponse))
                        .timeout(HTTP_TIMEOUT)
                        .GET()
                        .build();
                var response = executeWithRetry(httpRequest);
                if (response.statusCode() == 200) {
                    var assetGenerationStatus = objectMapper.readValue(response.body(), Object.class);
                    if ("COMPLETED".equals(assetGenerationStatus)) {
                        log.info("Asset generation completed", "asset", asset);
                        return new Object();
                    }
                }
                Thread.sleep(retryDelay);
                attempts++;
            } catch (Exception e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        return null;
    }

    public Object fetchOrder() {
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/queue/take"))
                    .timeout(HTTP_TIMEOUT)
                    .GET()
                    .build();

            var response = executeWithRetry(httpRequest);
            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), Object.class);
            }
        } catch (Exception e) {
            log.error("Error occurred while processing order", "error", e);
        }
        return null;
    }

    private HttpResponse<String> executeWithRetry(HttpRequest httpRequest) throws InterruptedException {
        Exception lastException = null;
        for (int retry = 0; retry < MAX_RETRIES; retry++) {
            try {
                var response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() >= 500 && retry < MAX_RETRIES) {
                    Thread.sleep(RETRY_DELAY.toMillis() * (retry + 1));
                    continue;
                }
                return response;
            } catch (Exception e) {
                // Handle exceptions and retry
                if (retry < MAX_RETRIES) {
                    Thread.sleep(RETRY_DELAY.toMillis() * (retry + 1));
                }
                log.error("Error occurred while processing order, attempt: " + (retry + 1), "error", e);
                lastException = e;
            }
        }
        throw new RuntimeException("Failed to process order after " + MAX_RETRIES + " attempts", lastException);
    }
}
