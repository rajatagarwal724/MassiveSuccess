package companies.wmg;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.time.Duration;

/**
 * WMG Artifact Generation Client
 * 
 * Orchestrates the production of Metadata and Assets for Orders from a queue,
 * handling failures and following strict orchestration rules.
 */
public class ArtifactGenerationClient {
    
    private static final Logger logger = Logger.getLogger(ArtifactGenerationClient.class.getName());
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;
    private final ExecutorService executorService;
    private final AtomicBoolean running = new AtomicBoolean(false);
    
    // Configuration
    private static final int MAX_RETRIES = 3;
    private static final Duration RETRY_DELAY = Duration.ofSeconds(2);
    private static final Duration HTTP_TIMEOUT = Duration.ofSeconds(30);
    private static final Duration POLLING_INTERVAL = Duration.ofSeconds(5);
    
    public ArtifactGenerationClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(HTTP_TIMEOUT)
            .build();
        this.objectMapper = new ObjectMapper();
        this.executorService = Executors.newFixedThreadPool(10);
    }
    
    // ==================== DATA MODELS ====================
    
    public static class Order {
        @JsonProperty("orderId")
        private String orderId;
        
        @JsonProperty("songTitle")
        private String songTitle;
        
        @JsonProperty("artist")
        private String artist;
        
        @JsonProperty("expectedAssets")
        private List<String> expectedAssets;
        
        // Constructors, getters, setters
        public Order() {}
        
        public Order(String orderId, String songTitle, String artist, List<String> expectedAssets) {
            this.orderId = orderId;
            this.songTitle = songTitle;
            this.artist = artist;
            this.expectedAssets = expectedAssets;
        }
        
        public String getOrderId() { return orderId; }
        public void setOrderId(String orderId) { this.orderId = orderId; }
        
        public String getSongTitle() { return songTitle; }
        public void setSongTitle(String songTitle) { this.songTitle = songTitle; }
        
        public String getArtist() { return artist; }
        public void setArtist(String artist) { this.artist = artist; }
        
        public List<String> getExpectedAssets() { return expectedAssets; }
        public void setExpectedAssets(List<String> expectedAssets) { this.expectedAssets = expectedAssets; }
    }
    
    public static class Asset {
        @JsonProperty("assetId")
        private String assetId;
        
        @JsonProperty("orderId")
        private String orderId;
        
        @JsonProperty("assetType")
        private String assetType;
        
        @JsonProperty("filePath")
        private String filePath;
        
        @JsonProperty("status")
        private String status;
        
        // Constructors, getters, setters
        public Asset() {}
        
        public Asset(String assetId, String orderId, String assetType, String filePath, String status) {
            this.assetId = assetId;
            this.orderId = orderId;
            this.assetType = assetType;
            this.filePath = filePath;
            this.status = status;
        }
        
        public String getAssetId() { return assetId; }
        public void setAssetId(String assetId) { this.assetId = assetId; }
        
        public String getOrderId() { return orderId; }
        public void setOrderId(String orderId) { this.orderId = orderId; }
        
        public String getAssetType() { return assetType; }
        public void setAssetType(String assetType) { this.assetType = assetType; }
        
        public String getFilePath() { return filePath; }
        public void setFilePath(String filePath) { this.filePath = filePath; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
    
    public static class AssetDetail {
        @JsonProperty("assetDetailId")
        private String assetDetailId;
        
        @JsonProperty("assetId")
        private String assetId;
        
        @JsonProperty("fileSize")
        private long fileSize;
        
        @JsonProperty("checksum")
        private String checksum;
        
        @JsonProperty("metadata")
        private Map<String, Object> metadata;
        
        // Constructors, getters, setters
        public AssetDetail() {}
        
        public AssetDetail(String assetDetailId, String assetId, long fileSize, String checksum, Map<String, Object> metadata) {
            this.assetDetailId = assetDetailId;
            this.assetId = assetId;
            this.fileSize = fileSize;
            this.checksum = checksum;
            this.metadata = metadata;
        }
        
        public String getAssetDetailId() { return assetDetailId; }
        public void setAssetDetailId(String assetDetailId) { this.assetDetailId = assetDetailId; }
        
        public String getAssetId() { return assetId; }
        public void setAssetId(String assetId) { this.assetId = assetId; }
        
        public long getFileSize() { return fileSize; }
        public void setFileSize(long fileSize) { this.fileSize = fileSize; }
        
        public String getChecksum() { return checksum; }
        public void setChecksum(String checksum) { this.checksum = checksum; }
        
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }
    
    public static class Metadata {
        @JsonProperty("metadataId")
        private String metadataId;
        
        @JsonProperty("orderId")
        private String orderId;
        
        @JsonProperty("xmlContent")
        private String xmlContent;
        
        @JsonProperty("status")
        private String status;
        
        // Constructors, getters, setters
        public Metadata() {}
        
        public Metadata(String metadataId, String orderId, String xmlContent, String status) {
            this.metadataId = metadataId;
            this.orderId = orderId;
            this.xmlContent = xmlContent;
            this.status = status;
        }
        
        public String getMetadataId() { return metadataId; }
        public void setMetadataId(String metadataId) { this.metadataId = metadataId; }
        
        public String getOrderId() { return orderId; }
        public void setOrderId(String orderId) { this.orderId = orderId; }
        
        public String getXmlContent() { return xmlContent; }
        public void setXmlContent(String xmlContent) { this.xmlContent = xmlContent; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
    
    public static class ShippableOrder {
        @JsonProperty("orderId")
        private String orderId;
        
        @JsonProperty("assets")
        private List<Asset> assets;
        
        @JsonProperty("assetDetails")
        private List<AssetDetail> assetDetails;
        
        @JsonProperty("metadata")
        private Metadata metadata;
        
        @JsonProperty("status")
        private String status = "READY_TO_SHIP";
        
        // Constructors, getters, setters
        public ShippableOrder() {}
        
        public ShippableOrder(String orderId, List<Asset> assets, List<AssetDetail> assetDetails, Metadata metadata) {
            this.orderId = orderId;
            this.assets = assets;
            this.assetDetails = assetDetails;
            this.metadata = metadata;
        }
        
        public String getOrderId() { return orderId; }
        public void setOrderId(String orderId) { this.orderId = orderId; }
        
        public List<Asset> getAssets() { return assets; }
        public void setAssets(List<Asset> assets) { this.assets = assets; }
        
        public List<AssetDetail> getAssetDetails() { return assetDetails; }
        public void setAssetDetails(List<AssetDetail> assetDetails) { this.assetDetails = assetDetails; }
        
        public Metadata getMetadata() { return metadata; }
        public void setMetadata(Metadata metadata) { this.metadata = metadata; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
    
    public static class FailedOrder {
        @JsonProperty("orderId")
        private String orderId;
        
        @JsonProperty("assets")
        private List<Asset> assets;
        
        @JsonProperty("assetDetails")
        private List<AssetDetail> assetDetails;
        
        @JsonProperty("metadata")
        private Metadata metadata;
        
        @JsonProperty("status")
        private String status = "FAILED";
        
        @JsonProperty("failureReason")
        private String failureReason;
        
        // Constructors, getters, setters
        public FailedOrder() {}
        
        public FailedOrder(String orderId, String failureReason) {
            this.orderId = orderId;
            this.failureReason = failureReason;
        }
        
        public FailedOrder(String orderId, List<Asset> assets, List<AssetDetail> assetDetails, String failureReason) {
            this.orderId = orderId;
            this.assets = assets;
            this.assetDetails = assetDetails;
            this.failureReason = failureReason;
        }
        
        public String getOrderId() { return orderId; }
        public void setOrderId(String orderId) { this.orderId = orderId; }
        
        public List<Asset> getAssets() { return assets; }
        public void setAssets(List<Asset> assets) { this.assets = assets; }
        
        public List<AssetDetail> getAssetDetails() { return assetDetails; }
        public void setAssetDetails(List<AssetDetail> assetDetails) { this.assetDetails = assetDetails; }
        
        public Metadata getMetadata() { return metadata; }
        public void setMetadata(Metadata metadata) { this.metadata = metadata; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getFailureReason() { return failureReason; }
        public void setFailureReason(String failureReason) { this.failureReason = failureReason; }
    }
    
    public static class GenerationRequest {
        @JsonProperty("orderId")
        private String orderId;
        
        @JsonProperty("requestType")
        private String requestType;
        
        public GenerationRequest() {}
        
        public GenerationRequest(String orderId, String requestType) {
            this.orderId = orderId;
            this.requestType = requestType;
        }
        
        public String getOrderId() { return orderId; }
        public void setOrderId(String orderId) { this.orderId = orderId; }
        
        public String getRequestType() { return requestType; }
        public void setRequestType(String requestType) { this.requestType = requestType; }
    }
    
    public static class GenerationResponse {
        @JsonProperty("jobId")
        private String jobId;
        
        @JsonProperty("status")
        private String status;
        
        @JsonProperty("message")
        private String message;
        
        public GenerationResponse() {}
        
        public String getJobId() { return jobId; }
        public void setJobId(String jobId) { this.jobId = jobId; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
    
    // ==================== MAIN ORCHESTRATION ====================
    
    public void start() {
        if (running.compareAndSet(false, true)) {
            logger.info("Starting Artifact Generation Client");
            executorService.submit(this::processOrders);
        }
    }
    
    public void stop() {
        if (running.compareAndSet(true, false)) {
            logger.info("Stopping Artifact Generation Client");
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
            try {
                Order order = takeOrderFromQueue();
                if (order != null) {
                    processOrder(order);
                } else {
                    // No orders available, wait before checking again
                    Thread.sleep(POLLING_INTERVAL.toMillis());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error in order processing loop", e);
            }
        }
    }
    
    private void processOrder(Order order) {
        logger.info("Processing order: " + order.getOrderId());
        
        try {
            // Step 1: Generate Assets
            List<Asset> assets = new ArrayList<>();
            List<AssetDetail> assetDetails = new ArrayList<>();
            
            boolean assetGenerationSuccess = generateAssets(order, assets, assetDetails);
            
            if (!assetGenerationSuccess) {
                // Rule 4: If Asset generation fails, submit FailedOrder without any data
                FailedOrder failedOrder = new FailedOrder(order.getOrderId(), "Asset generation failed");
                submitFailedOrder(failedOrder);
                return;
            }
            
            // Step 2: Generate Metadata (only if Asset generation succeeded)
            Metadata metadata = generateMetadata(order);
            
            if (metadata == null) {
                // Rule 5: If Metadata generation fails, submit FailedOrder with Asset data but without Metadata
                FailedOrder failedOrder = new FailedOrder(order.getOrderId(), assets, assetDetails, "Metadata generation failed");
                submitFailedOrder(failedOrder);
                return;
            }
            
            // Rule 6: If both succeed, submit ShippableOrder with all data
            ShippableOrder shippableOrder = new ShippableOrder(order.getOrderId(), assets, assetDetails, metadata);
            submitShippableOrder(shippableOrder);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error processing order: " + order.getOrderId(), e);
            FailedOrder failedOrder = new FailedOrder(order.getOrderId(), "Unexpected error: " + e.getMessage());
            submitFailedOrder(failedOrder);
        }
    }
    
    // ==================== API INTEGRATION ====================
    
    private Order takeOrderFromQueue() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/queue/take"))
                .timeout(HTTP_TIMEOUT)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
            
            HttpResponse<String> response = executeWithRetry(request);
            
            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), Order.class);
            } else if (response.statusCode() == 204) {
                // No orders available
                return null;
            } else {
                logger.warning("Failed to take order from queue. Status: " + response.statusCode());
                return null;
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error taking order from queue", e);
            return null;
        }
    }
    
    private boolean generateAssets(Order order, List<Asset> assets, List<AssetDetail> assetDetails) {
        try {
            // Queue asset generation
            GenerationRequest request = new GenerationRequest(order.getOrderId(), "ASSET");
            String requestBody = objectMapper.writeValueAsString(request);
            
            HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/generation/asset"))
                .timeout(HTTP_TIMEOUT)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
            
            HttpResponse<String> response = executeWithRetry(httpRequest);
            
            if (response.statusCode() != 200) {
                logger.warning("Failed to queue asset generation for order: " + order.getOrderId());
                return false;
            }
            
            GenerationResponse genResponse = objectMapper.readValue(response.body(), GenerationResponse.class);
            String jobId = genResponse.getJobId();
            
            // Wait for asset generation to complete
            if (!waitForJobCompletion(jobId, "ASSET")) {
                return false;
            }
            
            // Retrieve generated assets and their details
            return retrieveAssetsAndDetails(order.getOrderId(), assets, assetDetails);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error generating assets for order: " + order.getOrderId(), e);
            return false;
        }
    }
    
    private Metadata generateMetadata(Order order) {
        try {
            // Queue metadata generation
            GenerationRequest request = new GenerationRequest(order.getOrderId(), "METADATA");
            String requestBody = objectMapper.writeValueAsString(request);
            
            HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/generation/metadata"))
                .timeout(HTTP_TIMEOUT)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
            
            HttpResponse<String> response = executeWithRetry(httpRequest);
            
            if (response.statusCode() != 200) {
                logger.warning("Failed to queue metadata generation for order: " + order.getOrderId());
                return null;
            }
            
            GenerationResponse genResponse = objectMapper.readValue(response.body(), GenerationResponse.class);
            String jobId = genResponse.getJobId();
            
            // Wait for metadata generation to complete
            if (!waitForJobCompletion(jobId, "METADATA")) {
                return null;
            }
            
            // Retrieve generated metadata
            return retrieveMetadata(order.getOrderId());
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error generating metadata for order: " + order.getOrderId(), e);
            return null;
        }
    }
    
    private boolean waitForJobCompletion(String jobId, String jobType) {
        try {
            int maxAttempts = 60; // 5 minutes with 5-second intervals
            int attempts = 0;
            
            while (attempts < maxAttempts) {
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/job/status/" + jobId))
                    .timeout(HTTP_TIMEOUT)
                    .GET()
                    .build();
                
                HttpResponse<String> response = executeWithRetry(request);
                
                if (response.statusCode() == 200) {
                    GenerationResponse status = objectMapper.readValue(response.body(), GenerationResponse.class);
                    
                    if ("COMPLETED".equals(status.getStatus())) {
                        logger.info(jobType + " generation completed for job: " + jobId);
                        return true;
                    } else if ("FAILED".equals(status.getStatus())) {
                        logger.warning(jobType + " generation failed for job: " + jobId + ". Reason: " + status.getMessage());
                        return false;
                    }
                    // Still in progress, continue waiting
                }
                
                Thread.sleep(POLLING_INTERVAL.toMillis());
                attempts++;
            }
            
            logger.warning(jobType + " generation timed out for job: " + jobId);
            return false;
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error waiting for job completion: " + jobId, e);
            return false;
        }
    }
    
    private boolean retrieveAssetsAndDetails(String orderId, List<Asset> assets, List<AssetDetail> assetDetails) {
        try {
            // Retrieve assets
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/assets/" + orderId))
                .timeout(HTTP_TIMEOUT)
                .GET()
                .build();
            
            HttpResponse<String> response = executeWithRetry(request);
            
            if (response.statusCode() != 200) {
                logger.warning("Failed to retrieve assets for order: " + orderId);
                return false;
            }
            
            Asset[] assetArray = objectMapper.readValue(response.body(), Asset[].class);
            assets.addAll(Arrays.asList(assetArray));
            
            // Retrieve asset details for each asset
            for (Asset asset : assets) {
                AssetDetail detail = retrieveAssetDetail(asset.getAssetId());
                if (detail != null) {
                    assetDetails.add(detail);
                } else {
                    logger.warning("Failed to retrieve asset detail for asset: " + asset.getAssetId());
                    return false;
                }
            }
            
            return true;
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving assets and details for order: " + orderId, e);
            return false;
        }
    }
    
    private AssetDetail retrieveAssetDetail(String assetId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/asset-details/" + assetId))
                .timeout(HTTP_TIMEOUT)
                .GET()
                .build();
            
            HttpResponse<String> response = executeWithRetry(request);
            
            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), AssetDetail.class);
            } else {
                logger.warning("Failed to retrieve asset detail for asset: " + assetId);
                return null;
            }
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving asset detail for asset: " + assetId, e);
            return null;
        }
    }
    
    private Metadata retrieveMetadata(String orderId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/metadata/" + orderId))
                .timeout(HTTP_TIMEOUT)
                .GET()
                .build();
            
            HttpResponse<String> response = executeWithRetry(request);
            
            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), Metadata.class);
            } else {
                logger.warning("Failed to retrieve metadata for order: " + orderId);
                return null;
            }
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving metadata for order: " + orderId, e);
            return null;
        }
    }
    
    private void submitShippableOrder(ShippableOrder order) {
        try {
            String requestBody = objectMapper.writeValueAsString(order);
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/orders/shippable"))
                .timeout(HTTP_TIMEOUT)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
            
            HttpResponse<String> response = executeWithRetry(request);
            
            if (response.statusCode() == 200) {
                logger.info("Successfully submitted shippable order: " + order.getOrderId());
            } else {
                logger.severe("Failed to submit shippable order: " + order.getOrderId() + ". Status: " + response.statusCode());
            }
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error submitting shippable order: " + order.getOrderId(), e);
        }
    }
    
    private void submitFailedOrder(FailedOrder order) {
        try {
            String requestBody = objectMapper.writeValueAsString(order);
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/orders/failed"))
                .timeout(HTTP_TIMEOUT)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
            
            HttpResponse<String> response = executeWithRetry(request);
            
            if (response.statusCode() == 200) {
                logger.info("Successfully submitted failed order: " + order.getOrderId());
            } else {
                logger.severe("Failed to submit failed order: " + order.getOrderId() + ". Status: " + response.statusCode());
            }
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error submitting failed order: " + order.getOrderId(), e);
        }
    }
    
    // ==================== UTILITY METHODS ====================
    
    private HttpResponse<String> executeWithRetry(HttpRequest request) throws Exception {
        Exception lastException = null;
        
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                
                // Consider 5xx errors as retryable
                if (response.statusCode() >= 500 && attempt < MAX_RETRIES) {
                    logger.warning("Server error (attempt " + attempt + "/" + MAX_RETRIES + "): " + response.statusCode());
                    Thread.sleep(RETRY_DELAY.toMillis() * attempt); // Exponential backoff
                    continue;
                }
                
                return response;
                
            } catch (Exception e) {
                lastException = e;
                if (attempt < MAX_RETRIES) {
                    logger.warning("Request failed (attempt " + attempt + "/" + MAX_RETRIES + "): " + e.getMessage());
                    Thread.sleep(RETRY_DELAY.toMillis() * attempt);
                }
            }
        }
        
        throw new RuntimeException("Request failed after " + MAX_RETRIES + " attempts", lastException);
    }
    
    // ==================== MAIN METHOD FOR TESTING ====================
    
    public static void main(String[] args) {
        String baseUrl = args.length > 0 ? args[0] : "http://localhost:8080";
        
        ArtifactGenerationClient client = new ArtifactGenerationClient(baseUrl);
        
        // Add shutdown hook for graceful shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(client::stop));
        
        client.start();
        
        // Keep the main thread alive
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
