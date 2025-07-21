package lld.rate.limiter;


import lombok.Data;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

interface RateLimiter {
    boolean allowRequest(String clientId);

    void updateConfig(int maxRequests, long windowSizeInMillis);
}

class FixedWindowRateLimiter implements RateLimiter {
    private int maxRequests;
    private long windowSizeInMillis;
    private Map<String, AtomicInteger> requestCounts;
    private Map<String, Long> windowStartTimes;

    public FixedWindowRateLimiter(int maxRequests, long windowSizeInMillis) {
        this.maxRequests = maxRequests;
        this.windowSizeInMillis = windowSizeInMillis;
        this.requestCounts = new ConcurrentHashMap<>();
        this.windowStartTimes = new ConcurrentHashMap<>();
    }

    @Override
    public boolean allowRequest(String clientId) {
        long currentTime = System.currentTimeMillis();
        requestCounts.putIfAbsent(clientId, new AtomicInteger(0));
        windowStartTimes.putIfAbsent(clientId, currentTime);

        long windowStartTime = windowStartTimes.getOrDefault(clientId, currentTime);

        if (currentTime - windowStartTime >= windowSizeInMillis) {
            requestCounts.put(clientId, new AtomicInteger(0));
            windowStartTimes.put(clientId, currentTime);
        }

        if ((currentTime - windowStartTime) < windowSizeInMillis
                && requestCounts.get(clientId).get() < maxRequests) {
            requestCounts.get(clientId).incrementAndGet();
            return true;
        }

        return false;
    }

    @Override
    public void updateConfig(int maxRequests, long windowSizeInMillis) {
        this.maxRequests = maxRequests;
        this.windowSizeInMillis = windowSizeInMillis;
    }
}

class SlidingWindowRateLimiter implements RateLimiter {
    private int maxRequests;
    private long windowSizeInMillis;
    private Map<String, Queue<Long>> requestsTimestamps;

    public SlidingWindowRateLimiter(int maxRequests, long windowSizeInMillis) {
        this.maxRequests = maxRequests;
        this.windowSizeInMillis = windowSizeInMillis;
    }

    @Override
    public boolean allowRequest(String clientId) {
        long currentTime = System.currentTimeMillis();
        requestsTimestamps.putIfAbsent(clientId, new LinkedList<>());
        var clientRequestTimestamps = requestsTimestamps.get(clientId);

        while (!clientRequestTimestamps.isEmpty() && (currentTime - clientRequestTimestamps.peek()) >= windowSizeInMillis) {
            clientRequestTimestamps.poll();
        }

        if (clientRequestTimestamps.size() < maxRequests) {
            clientRequestTimestamps.offer(currentTime);
            return true;
        }

        return false;
    }

    @Override
    public void updateConfig(int maxRequests, long windowSizeInMillis) {
        this.maxRequests = maxRequests;
        this.windowSizeInMillis = windowSizeInMillis;
    }
}

enum RateLimiterType {
    FIXED, SLIDING
}

class TokenBucketRateLimiter implements RateLimiter {
    private int maxRequests;
    private long windowSizeInMillis;
    private Map<String, Long> lastRefillTimestamp;
    private Map<String, Integer> tokens;

    public TokenBucketRateLimiter(int maxRequests, long windowSizeInMillis) {
        this.maxRequests = maxRequests;
        this.windowSizeInMillis = windowSizeInMillis;
        this.lastRefillTimestamp = new HashMap<>();
        this.tokens = new HashMap<>();
    }

    @Override
    public boolean allowRequest(String clientId) {
        long currentTimestamp = System.currentTimeMillis();
        lastRefillTimestamp.putIfAbsent(clientId, currentTimestamp);
        tokens.putIfAbsent(clientId, maxRequests);

        long lastRefill = lastRefillTimestamp.get(clientId);
        int currentTokens = tokens.get(clientId);

        long elapsed = currentTimestamp - lastRefill;
        int tokensToAdd = (int) ((elapsed * maxRequests) / windowSizeInMillis); // tokens per ms

        if (tokensToAdd > 0) {
            currentTokens = Math.min(maxRequests, currentTokens + tokensToAdd);
            lastRefillTimestamp.put(clientId, currentTimestamp);
            tokens.put(clientId, currentTokens);
        }

        if (currentTokens > 0) {
            tokens.put(clientId, currentTokens - 1);
            return true;
        }
        return false;
    }

    @Override
    public void updateConfig(int maxRequests, long windowSizeInMillis) {
        this.maxRequests = maxRequests;
        this.windowSizeInMillis = windowSizeInMillis;
    }
}

class LeakyBucketRateLimiter implements RateLimiter {

    private int maxRequests;
    private long windowSizeInMillis;
    private final Map<String, ConcurrentLinkedDeque<Long>> requestLogs;
    private final ScheduledExecutorService scheduledExecutorService;

    public LeakyBucketRateLimiter(int maxRequests, long windowSizeInMillis) {
        this.maxRequests = maxRequests;
        this.windowSizeInMillis = windowSizeInMillis;
        this.requestLogs = new ConcurrentHashMap<>();
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        this.scheduledExecutorService.scheduleAtFixedRate(this::leakRequests, 1, 1, TimeUnit.SECONDS);

    }

    public void leakRequests() {
        int numberOfRequestToLeakPerSec = (int) (maxRequests/windowSizeInMillis) * 1000;
        if (!requestLogs.isEmpty()) {
            requestLogs.entrySet()
                    .stream()
                    .filter(entry -> !entry.getValue().isEmpty())
                    .forEach(entry -> {
                        int n = numberOfRequestToLeakPerSec;
                        while (!entry.getValue().isEmpty() && n > 0) {
                            entry.getValue().poll();
                            n--;
                        }
                    });
        }
    }

    @Override
    public boolean allowRequest(String clientId) {
        long currentTime = System.currentTimeMillis();
        requestLogs.putIfAbsent(clientId, new ConcurrentLinkedDeque<>());

        if (requestLogs.get(clientId).size() < maxRequests) {
            requestLogs.get(clientId).offer(currentTime);
            return true;
        }
        return false;
    }

    @Override
    public void updateConfig(int maxRequests, long windowSizeInMillis) {
        this.maxRequests = maxRequests;
        this.windowSizeInMillis = windowSizeInMillis;
    }
}

record Config(int maxRequests, long windowSizeInMillis) {}

@Data
class RateLimiterConfig {
    private final Config config;

    public RateLimiterConfig(Config config) {
        this.config = config;
    }

    private List<RateLimiter> rateLimiters;

    public void register(RateLimiter rateLimiter) {
        rateLimiters.add(rateLimiter);
    }

    public void remove(RateLimiter rateLimiter) {
        rateLimiters.remove(rateLimiter);
    }

    public void update(Config config) {
        rateLimiters.forEach(rateLimiter -> rateLimiter.updateConfig(config.maxRequests(), config.windowSizeInMillis()));
    }
}


class RateLimiterFactory {
    public static RateLimiter createRateLimiter(RateLimiterType type, RateLimiterConfig rateLimiterConfig) {
        if (rateLimiterConfig == null || rateLimiterConfig.getConfig() == null) {
            throw new IllegalArgumentException("RateLimiterConfig and its config must not be null");
        }

        RateLimiter rateLimiter;
        int maxRequests = rateLimiterConfig.getConfig().maxRequests();
        long windowSizeInMillis = rateLimiterConfig.getConfig().windowSizeInMillis();

        switch (type) {
            case FIXED -> rateLimiter = new FixedWindowRateLimiter(maxRequests, windowSizeInMillis);
            case SLIDING -> rateLimiter = new SlidingWindowRateLimiter(maxRequests, windowSizeInMillis);
            default -> throw new IllegalArgumentException("Unsupported RateLimiterType: " + type);
        }

        rateLimiterConfig.register(rateLimiter);
        return rateLimiter;
    }
}

class RateLimiterManager {
    private static volatile RateLimiterManager instance;
    private RateLimiter rateLimiter;
    private RateLimiterConfig rateLimiterConfig;

    private RateLimiterManager(RateLimiterType type, Config config) {
        rateLimiterConfig = new RateLimiterConfig(config);
        rateLimiter = RateLimiterFactory.createRateLimiter(type, new RateLimiterConfig(config));
    }

    public static RateLimiterManager getInstance(RateLimiterType type, Config config) {
        if (null == instance) {
            synchronized (RateLimiterManager.class) {
                if (null == instance) {
                    instance = new RateLimiterManager(type, config);
                }
            }
        }
        return instance;
    }

    public void updateConfig(Config config) {
        rateLimiterConfig.update(config);
    }
}

public class RateLimiterDemo {

}
