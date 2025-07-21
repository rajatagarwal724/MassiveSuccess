package lld.bitly;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class TinyUrlSystemDemonstration {

    public static void main(String[] args) {
//        // Demo for MD5-based code generation
//        System.out.println("===== TinyURL System Demonstration (MD5 Strategy) =====");
//        demoShortenerService(new Md5ShortCodeGenerator());
        
        System.out.println("\n\n===== TinyURL System Demonstration (Sequential Strategy) =====");
        demoShortenerService(new SequentialShortCodeGenerator());
    }
    
    private static void demoShortenerService(ShortCodeGenerator codeGenerator) {
        // Create the URL shortener service with a default expiration of 30 days
        UrlShortenerService urlShortener = new UrlShortenerServiceImpl(
                new InMemoryUrlRepository(),
                codeGenerator,
                new BasicUrlValidator(),
                30);

        // Example usage
        try {
            // Shorten some URLs
            String url1 = "https://www.example.com/very/long/path/to/some/resource?param1=value1&param2=value2";
            String url2 = "https://www.google.com/search?q=java+programming";
            String url3 = "https://github.com/features/actions";
            
            System.out.println("\nShortening URLs:\n");
            String shortUrl1 = urlShortener.shortenUrl(url1);
            System.out.println("Original: " + url1);
            System.out.println("Shortened: " + shortUrl1);
            
            String shortUrl2 = urlShortener.shortenUrl(url2);
            System.out.println("\nOriginal: " + url2);
            System.out.println("Shortened: " + shortUrl2);

            // Create a custom short URL
            String customCode = "google";
            String shortUrl3 = urlShortener.shortenUrlWithCustomCode(url3, customCode);
            System.out.println("\nOriginal: " + url3);
            System.out.println("Shortened with custom code: " + shortUrl3);

            // Expand the URLs
            System.out.println("\nExpanding URLs:\n");
            String expandedUrl1 = urlShortener.expandUrl(shortUrl1);
            System.out.println("Short URL: " + shortUrl1);
            System.out.println("Expanded: " + expandedUrl1);

            String expandedUrl2 = urlShortener.expandUrl(shortUrl2);
            System.out.println("\nShort URL: " + shortUrl2);
            System.out.println("Expanded: " + expandedUrl2);

            String expandedUrl3 = urlShortener.expandUrl(shortUrl3);
            System.out.println("\nShort URL: " + shortUrl3);
            System.out.println("Expanded: " + expandedUrl3);

            // Get statistics
            System.out.println("\nURL Statistics:\n");
            UrlStatistics stats1 = urlShortener.getUrlStatistics(shortUrl1);
            System.out.println("Statistics for " + shortUrl1 + ":");
            System.out.println("Creation Time: " + stats1.getCreationTime());
            System.out.println("Expiration Time: " + stats1.getExpirationTime());
            System.out.println("Access Count: " + stats1.getAccessCount());

            // Simulate multiple accesses
            for (int i = 0; i < 5; i++) {
                urlShortener.expandUrl(shortUrl2);
            }

            UrlStatistics stats2 = urlShortener.getUrlStatistics(shortUrl2);
            System.out.println("\nStatistics for " + shortUrl2 + " after multiple accesses:");
            System.out.println("Creation Time: " + stats2.getCreationTime());
            System.out.println("Expiration Time: " + stats2.getExpirationTime());
            System.out.println("Access Count: " + stats2.getAccessCount());

            // List all URLs
            System.out.println("\nAll URLs in the system:\n");
            List<UrlMapping> allUrls = urlShortener.getAllUrls();
            allUrls.forEach(mapping -> {
                System.out.println("Short Code: " + mapping.getShortCode());
                System.out.println("Original URL: " + mapping.getLongUrl());
                System.out.println("Created: " + mapping.getCreationTime());
                System.out.println("Expires: " + mapping.getExpirationTime());
                System.out.println("Access Count: " + mapping.getAccessCount());
                System.out.println();
            });

            // Demonstrate URL expiration
            System.out.println("\nDemonstrating URL expiration:\n");
            String tempUrl = "https://www.example.org/temporary";
            String tempShortUrl = urlShortener.shortenUrlWithCustomExpiration(tempUrl, 0); // Expire immediately
            System.out.println("Original: " + tempUrl);
            System.out.println("Shortened with immediate expiration: " + tempShortUrl);

            try {
                // Small delay to ensure expiration
                TimeUnit.MILLISECONDS.sleep(100);
                
                String expanded = urlShortener.expandUrl(tempShortUrl);
                System.out.println("Expanded URL: " + expanded); // Should not reach here
            } catch (Exception e) {
                System.out.println("As expected, could not expand expired URL: " + e.getMessage());
            }

        } catch (Exception e) {
            System.err.println("Error in demonstration: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

/**
 * Interface for URL shortening service
 */
interface UrlShortenerService {
    String shortenUrl(String longUrl) throws MalformedURLException;
    String shortenUrlWithCustomCode(String longUrl, String customCode) throws MalformedURLException;
    String shortenUrlWithCustomExpiration(String longUrl, int expirationDays) throws MalformedURLException;
    String expandUrl(String shortUrl) throws UrlNotFoundException, UrlExpiredException;
    UrlStatistics getUrlStatistics(String shortUrl) throws UrlNotFoundException;
    List<UrlMapping> getAllUrls();
    boolean deleteUrl(String shortUrl);
}

/**
 * Implementation of the URL shortener service
 */
class UrlShortenerServiceImpl implements UrlShortenerService {
    private static final String BASE_URL = "http://tiny.url/";
    private final UrlRepository repository;
    private final ShortCodeGenerator codeGenerator;
    private final UrlValidator urlValidator;
    private final int defaultExpirationDays;

    public UrlShortenerServiceImpl(UrlRepository repository, 
                                ShortCodeGenerator codeGenerator, 
                                UrlValidator urlValidator, 
                                int defaultExpirationDays) {
        this.repository = repository;
        this.codeGenerator = codeGenerator;
        this.urlValidator = urlValidator;
        this.defaultExpirationDays = defaultExpirationDays;
    }

    @Override
    public String shortenUrl(String longUrl) throws MalformedURLException {
        return shortenUrlWithCustomExpiration(longUrl, defaultExpirationDays);
    }

    @Override
    public String shortenUrlWithCustomCode(String longUrl, String customCode) throws MalformedURLException {
        if (!urlValidator.isValidUrl(longUrl)) {
            throw new MalformedURLException("Invalid URL format: " + longUrl);
        }
        
        if (repository.existsByShortCode(customCode)) {
            throw new IllegalArgumentException("Custom code already in use: " + customCode);
        }
        
        LocalDateTime creationTime = LocalDateTime.now();
        LocalDateTime expirationTime = creationTime.plusDays(defaultExpirationDays);
        
        UrlMapping mapping = new UrlMapping(customCode, longUrl, creationTime, expirationTime);
        repository.save(mapping);
        
        return BASE_URL + customCode;
    }

    @Override
    public String shortenUrlWithCustomExpiration(String longUrl, int expirationDays) throws MalformedURLException {
        if (!urlValidator.isValidUrl(longUrl)) {
            throw new MalformedURLException("Invalid URL format: " + longUrl);
        }
        
        // Check if URL already exists in the system
        Optional<UrlMapping> existingMapping = repository.findByLongUrl(longUrl);
        if (existingMapping.isPresent() && 
            !existingMapping.get().isExpired()) {
            return BASE_URL + existingMapping.get().getShortCode();
        }
        
        // Generate a new short code
        String shortCode = codeGenerator.generateShortCode(longUrl);
        while (repository.existsByShortCode(shortCode)) {
            // Ensure uniqueness by regenerating if collision occurs
            shortCode = codeGenerator.generateShortCode(longUrl + UUID.randomUUID());
        }
        
        LocalDateTime creationTime = LocalDateTime.now();
        LocalDateTime expirationTime = expirationDays > 0 ? 
                creationTime.plusDays(expirationDays) : creationTime.plusSeconds(1);
        
        UrlMapping mapping = new UrlMapping(shortCode, longUrl, creationTime, expirationTime);
        repository.save(mapping);
        
        return BASE_URL + shortCode;
    }

    @Override
    public String expandUrl(String shortUrl) throws UrlNotFoundException, UrlExpiredException {
        String shortCode = extractShortCode(shortUrl);
        
        Optional<UrlMapping> mappingOpt = repository.findByShortCode(shortCode);
        if (!mappingOpt.isPresent()) {
            throw new UrlNotFoundException("Short URL not found: " + shortUrl);
        }
        
        UrlMapping mapping = mappingOpt.get();
        
        if (mapping.isExpired()) {
            throw new UrlExpiredException("URL has expired: " + shortUrl);
        }
        
        // Increment access count
        mapping.incrementAccessCount();
        repository.update(mapping);
        
        return mapping.getLongUrl();
    }

    @Override
    public UrlStatistics getUrlStatistics(String shortUrl) throws UrlNotFoundException {
        String shortCode = extractShortCode(shortUrl);
        
        Optional<UrlMapping> mappingOpt = repository.findByShortCode(shortCode);
        if (!mappingOpt.isPresent()) {
            throw new UrlNotFoundException("Short URL not found: " + shortUrl);
        }
        
        UrlMapping mapping = mappingOpt.get();
        return new UrlStatistics(
            mapping.getCreationTime(),
            mapping.getExpirationTime(),
            mapping.getAccessCount()
        );
    }

    @Override
    public List<UrlMapping> getAllUrls() {
        return repository.findAll();
    }

    @Override
    public boolean deleteUrl(String shortUrl) {
        String shortCode = extractShortCode(shortUrl);
        return repository.deleteByShortCode(shortCode);
    }
    
    private String extractShortCode(String shortUrl) {
        if (shortUrl == null || shortUrl.isEmpty()) {
            throw new IllegalArgumentException("Short URL cannot be null or empty");
        }
        
        if (shortUrl.startsWith(BASE_URL)) {
            return shortUrl.substring(BASE_URL.length());
        }
        
        return shortUrl; // Assume it's just the code
    }
}

/**
 * Interface for URL repository
 */
interface UrlRepository {
    void save(UrlMapping mapping);
    void update(UrlMapping mapping);
    Optional<UrlMapping> findByShortCode(String shortCode);
    Optional<UrlMapping> findByLongUrl(String longUrl);
    boolean existsByShortCode(String shortCode);
    boolean deleteByShortCode(String shortCode);
    List<UrlMapping> findAll();
}

/**
 * In-memory implementation of UrlRepository
 */
class InMemoryUrlRepository implements UrlRepository {
    private final Map<String, UrlMapping> urlMappings = new ConcurrentHashMap<>();
    private final Map<String, String> longToShortMapping = new ConcurrentHashMap<>();

    @Override
    public void save(UrlMapping mapping) {
        urlMappings.put(mapping.getShortCode(), mapping);
        longToShortMapping.put(mapping.getLongUrl(), mapping.getShortCode());
    }

    @Override
    public void update(UrlMapping mapping) {
        if (urlMappings.containsKey(mapping.getShortCode())) {
            urlMappings.put(mapping.getShortCode(), mapping);
        }
    }

    @Override
    public Optional<UrlMapping> findByShortCode(String shortCode) {
        return Optional.ofNullable(urlMappings.get(shortCode));
    }

    @Override
    public Optional<UrlMapping> findByLongUrl(String longUrl) {
        String shortCode = longToShortMapping.get(longUrl);
        if (shortCode == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(urlMappings.get(shortCode));
    }

    @Override
    public boolean existsByShortCode(String shortCode) {
        return urlMappings.containsKey(shortCode);
    }

    @Override
    public boolean deleteByShortCode(String shortCode) {
        UrlMapping mapping = urlMappings.remove(shortCode);
        if (mapping != null) {
            longToShortMapping.remove(mapping.getLongUrl());
            return true;
        }
        return false;
    }

    @Override
    public List<UrlMapping> findAll() {
        return new ArrayList<>(urlMappings.values());
    }
}

/**
 * URL mapping entity
 */
class UrlMapping {
    private final String shortCode;
    private final String longUrl;
    private final LocalDateTime creationTime;
    private final LocalDateTime expirationTime;
    private int accessCount;

    public UrlMapping(String shortCode, String longUrl, LocalDateTime creationTime, LocalDateTime expirationTime) {
        this.shortCode = shortCode;
        this.longUrl = longUrl;
        this.creationTime = creationTime;
        this.expirationTime = expirationTime;
        this.accessCount = 0;
    }

    public String getShortCode() {
        return shortCode;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public LocalDateTime getExpirationTime() {
        return expirationTime;
    }

    public int getAccessCount() {
        return accessCount;
    }

    public void incrementAccessCount() {
        this.accessCount++;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expirationTime);
    }
}

/**
 * URL statistics class
 */
class UrlStatistics {
    private final LocalDateTime creationTime;
    private final LocalDateTime expirationTime;
    private final int accessCount;

    public UrlStatistics(LocalDateTime creationTime, LocalDateTime expirationTime, int accessCount) {
        this.creationTime = creationTime;
        this.expirationTime = expirationTime;
        this.accessCount = accessCount;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public LocalDateTime getExpirationTime() {
        return expirationTime;
    }

    public int getAccessCount() {
        return accessCount;
    }
}

/**
 * Interface for short code generation
 */
interface ShortCodeGenerator {
    String generateShortCode(String longUrl);
}

/**
 * Sequential implementation of ShortCodeGenerator using AtomicLong
 */
class SequentialShortCodeGenerator implements ShortCodeGenerator {
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int BASE = ALPHABET.length(); // 62
    private static final int CODE_LENGTH = 7;
    
    // Start counter at 62^6 to ensure 7-character output
    // This is the smallest value that will generate a 7-character code in base62
    private static final long INITIAL_VALUE = (long) Math.pow(BASE, CODE_LENGTH - 1) - 1;
    
    private final AtomicLong counter;

    public SequentialShortCodeGenerator() {
        this.counter = new AtomicLong(INITIAL_VALUE);
    }

    @Override
    public String generateShortCode(String longUrl) {
        // Get the next value from the counter (thread-safe)
        long number = counter.incrementAndGet();
        return toBase62(number);
    }

    /**
     * Convert a decimal number to base62 (a-zA-Z0-9) representation
     * @param number The number to convert
     * @return The base62 string representation
     */
    private String toBase62(long number) {
        StringBuilder sb = new StringBuilder();
        do {
            int remainder = (int) (number % BASE);
            sb.append(ALPHABET.charAt(remainder));
            number /= BASE;
        } while (number > 0);
        
        return sb.reverse().toString();
    }
}

/**
 * Interface for URL validation
 */
interface UrlValidator {
    boolean isValidUrl(String url);
}

/**
 * Basic implementation of UrlValidator
 */
class BasicUrlValidator implements UrlValidator {
    @Override
    public boolean isValidUrl(String url) {
        try {
            new URL(url);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }
}

/**
 * Custom exception for URL not found
 */
class UrlNotFoundException extends Exception {
    public UrlNotFoundException(String message) {
        super(message);
    }
}

/**
 * Custom exception for expired URLs
 */
class UrlExpiredException extends Exception {
    public UrlExpiredException(String message) {
        super(message);
    }
}
