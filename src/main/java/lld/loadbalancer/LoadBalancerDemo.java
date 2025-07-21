package lld.loadbalancer;

import lombok.Data;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class LoadBalancerDemo {
    public static void main(String[] args) throws InterruptedException {
        // Create servers
        Server server1 = new Server("server-1", "http://localhost:8081", 10);
        Server server2 = new Server("server-2", "http://localhost:8082", 5);
        Server server3 = new Server("server-3", "http://localhost:8083", 8);
        
        // Create different types of load balancers
        LoadBalancer roundRobinLB = LoadBalancerFactory.createLoadBalancer("Round Robin Load Balancer", LoadBalancerType.ROUND_ROBIN);
        LoadBalancer weightedRoundRobinLB = LoadBalancerFactory.createLoadBalancer("Weighted Round Robin Load Balancer", LoadBalancerType.WEIGHTED_ROUND_ROBIN);
        LoadBalancer leastConnectionsLB = LoadBalancerFactory.createLoadBalancer("Least Connections Load Balancer", LoadBalancerType.LEAST_CONNECTIONS);
        LoadBalancer ipHashLB = LoadBalancerFactory.createLoadBalancer("IP Hash Load Balancer", LoadBalancerType.IP_HASH);
        
        // Register servers with load balancers
        roundRobinLB.registerServer(server1);
        roundRobinLB.registerServer(server2);
        roundRobinLB.registerServer(server3);
        
        weightedRoundRobinLB.registerServer(server1);
        weightedRoundRobinLB.registerServer(server2);
        weightedRoundRobinLB.registerServer(server3);
        
        leastConnectionsLB.registerServer(server1);
        leastConnectionsLB.registerServer(server2);
        leastConnectionsLB.registerServer(server3);
        
        ipHashLB.registerServer(server1);
        ipHashLB.registerServer(server2);
        ipHashLB.registerServer(server3);
        
        // Start health check for all load balancers
        roundRobinLB.startHealthCheck();
        weightedRoundRobinLB.startHealthCheck();
        leastConnectionsLB.startHealthCheck();
        ipHashLB.startHealthCheck();
        
        // Simulate client requests
        System.out.println("\n=== Round Robin Load Balancer ===\n");
        simulateRequests(roundRobinLB, 10);
        
        System.out.println("\n=== Weighted Round Robin Load Balancer ===\n");
        simulateRequests(weightedRoundRobinLB, 10);
        
        System.out.println("\n=== Least Connections Load Balancer ===\n");
        simulateRequests(leastConnectionsLB, 10);
        
        System.out.println("\n=== IP Hash Load Balancer ===\n");
        simulateIPHashRequests(ipHashLB, 5);
        
        // Simulate server failure and recovery
        System.out.println("\n=== Server Failure Simulation ===\n");
        server2.setHealthy(false);
        Thread.sleep(1000); // Wait for health check to detect failure
        
        System.out.println("\nAfter server2 failure, requests are routed to healthy servers:\n");
        simulateRequests(roundRobinLB, 5);
        
        // Server recovery
        server2.setHealthy(true);
        Thread.sleep(1000); // Wait for health check to detect recovery
        
        System.out.println("\nAfter server2 recovery, requests are routed to all servers:\n");
        simulateRequests(roundRobinLB, 5);
        
        // Stop health checks
        roundRobinLB.stopHealthCheck();
        weightedRoundRobinLB.stopHealthCheck();
        leastConnectionsLB.stopHealthCheck();
        ipHashLB.stopHealthCheck();
        
        // Print metrics
        System.out.println("\n=== Load Balancer Metrics ===\n");
        printMetrics(roundRobinLB);
        printMetrics(weightedRoundRobinLB);
        printMetrics(leastConnectionsLB);
        printMetrics(ipHashLB);
    }
    
    private static void simulateRequests(LoadBalancer loadBalancer, int count) {
        for (int i = 0; i < count; i++) {
            Request request = new Request("client-" + (i % 3), "/api/resource/" + i);
            Server server = loadBalancer.getServer(request);
            if (server != null) {
                System.out.println("Request " + i + " routed to " + server.getId());
                server.handleRequest(request);
            } else {
                System.out.println("No server available for request " + i);
            }
        }
    }
    
    private static void simulateIPHashRequests(LoadBalancer loadBalancer, int count) {
        String[] clients = {"192.168.1.1", "192.168.1.2", "192.168.1.3"};
        
        for (int i = 0; i < count; i++) {
            for (String clientIP : clients) {
                Request request = new Request(clientIP, "/api/resource/" + i);
                Server server = loadBalancer.getServer(request);
                if (server != null) {
                    System.out.println("Request from " + clientIP + " routed to " + server.getId());
                    server.handleRequest(request);
                } else {
                    System.out.println("No server available for request from " + clientIP);
                }
            }
        }
    }
    
    private static void printMetrics(LoadBalancer loadBalancer) {
        System.out.println(loadBalancer.getName() + " Metrics:");
        Map<String, Integer> requestCounts = loadBalancer.getRequestCountByServer();
        requestCounts.forEach((serverId, count) -> {
            System.out.println("  " + serverId + ": " + count + " requests");
        });
        System.out.println("  Total Requests: " + loadBalancer.getTotalRequests());
        System.out.println();
    }
}

// Request class
@Data
class Request {
    private final String clientId;
    private final String path;
    private final long timestamp;
    
    public Request(String clientId, String path) {
        this.clientId = clientId;
        this.path = path;
        this.timestamp = System.currentTimeMillis();
    }
}

// Server class
@Data
class Server {
    private final String id;
    private final String address;
    private final int weight;
    private volatile boolean healthy;
    private final AtomicInteger activeConnections;
    private final AtomicInteger totalRequests;
    
    public Server(String id, String address, int weight) {
        this.id = id;
        this.address = address;
        this.weight = weight;
        this.healthy = true;
        this.activeConnections = new AtomicInteger(0);
        this.totalRequests = new AtomicInteger(0);
    }
    
    public void setHealthy(boolean healthy) {
        this.healthy = healthy;
        System.out.println("Server " + id + " health status changed to: " + (healthy ? "healthy" : "unhealthy"));
    }
    
    public int getActiveConnections() {
        return activeConnections.get();
    }
    
    public int getTotalRequests() {
        return totalRequests.get();
    }
    
    public void handleRequest(Request request) {
        activeConnections.incrementAndGet();
        try {
            // Simulate processing time
            Thread.sleep(50);
            totalRequests.incrementAndGet();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            activeConnections.decrementAndGet();
        }
    }
    
    public boolean ping() {
        // In a real implementation, this would attempt to connect to the server
        // Here we just return the current health status
        return healthy;
    }
}

// LoadBalancer interface
interface LoadBalancer {
    String getName();
    void registerServer(Server server);
    void deregisterServer(String serverId);
    Server getServer(Request request);
    void startHealthCheck();
    void stopHealthCheck();
    Map<String, Integer> getRequestCountByServer();
    int getTotalRequests();
}

// AbstractLoadBalancer class
abstract class AbstractLoadBalancer implements LoadBalancer {
    protected final String name;
    protected final Map<String, Server> servers;
    protected final Lock serversLock;
    protected final Map<String, Integer> requestCounts;
    protected ScheduledExecutorService healthCheckExecutor;
    
    public AbstractLoadBalancer(String name) {
        this.name = name;
        this.servers = new ConcurrentHashMap<>();
        this.serversLock = new ReentrantLock();
        this.requestCounts = new ConcurrentHashMap<>();
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public void registerServer(Server server) {
        serversLock.lock();
        try {
            servers.put(server.getId(), server);
            requestCounts.put(server.getId(), 0);
            System.out.println("Server " + server.getId() + " registered with " + name);
        } finally {
            serversLock.unlock();
        }
    }
    
    @Override
    public void deregisterServer(String serverId) {
        serversLock.lock();
        try {
            servers.remove(serverId);
            System.out.println("Server " + serverId + " deregistered from " + name);
        } finally {
            serversLock.unlock();
        }
    }
    
    @Override
    public void startHealthCheck() {
        healthCheckExecutor = Executors.newSingleThreadScheduledExecutor();
        healthCheckExecutor.scheduleAtFixedRate(this::checkServersHealth, 0, 5, TimeUnit.SECONDS);
        System.out.println("Health check started for " + name);
    }
    
    @Override
    public void stopHealthCheck() {
        if (healthCheckExecutor != null) {
            healthCheckExecutor.shutdown();
            System.out.println("Health check stopped for " + name);
        }
    }
    
    @Override
    public Map<String, Integer> getRequestCountByServer() {
        return new HashMap<>(requestCounts);
    }
    
    @Override
    public int getTotalRequests() {
        return requestCounts.values().stream().mapToInt(Integer::intValue).sum();
    }
    
    protected void updateRequestCount(Server server) {
        if (server != null) {
            requestCounts.compute(server.getId(), (k, v) -> (v == null) ? 1 : v + 1);
        }
    }
    
    protected List<Server> getHealthyServers() {
        return servers.values().stream()
                .filter(Server::isHealthy)
                .collect(Collectors.toList());
    }
    
    private void checkServersHealth() {
        for (Server server : servers.values()) {
            boolean wasHealthy = server.isHealthy();
            boolean isHealthy = server.ping();
            
            if (wasHealthy && !isHealthy) {
                System.out.println("Server " + server.getId() + " is now unhealthy");
            } else if (!wasHealthy && isHealthy) {
                System.out.println("Server " + server.getId() + " has recovered and is healthy");
            }
        }
    }
}

// Round Robin Load Balancer
class RoundRobinLoadBalancer extends AbstractLoadBalancer {
    private AtomicInteger nextServerIndex;
    
    public RoundRobinLoadBalancer(String name) {
        super(name);
        this.nextServerIndex = new AtomicInteger(0);
    }
    
    @Override
    public Server getServer(Request request) {
        List<Server> healthyServers = getHealthyServers();
        if (healthyServers.isEmpty()) {
            return null;
        }
        
        int index = nextServerIndex.getAndIncrement() % healthyServers.size();
        Server server = healthyServers.get(index);
        updateRequestCount(server);
        return server;
    }
}

// Weighted Round Robin Load Balancer
class WeightedRoundRobinLoadBalancer extends AbstractLoadBalancer {
    private final List<ServerWeight> serverWeights;
    private int currentIndex;
    private int currentWeight;
    private int maxWeight;
    private int gcdWeight;
    private final Lock weightLock;
    
    public WeightedRoundRobinLoadBalancer(String name) {
        super(name);
        this.serverWeights = new ArrayList<>();
        this.currentIndex = -1;
        this.currentWeight = 0;
        this.maxWeight = 0;
        this.gcdWeight = 0;
        this.weightLock = new ReentrantLock();
    }
    
    @Override
    public void registerServer(Server server) {
        super.registerServer(server);
        weightLock.lock();
        try {
            serverWeights.add(new ServerWeight(server));
            recalculateWeights();
        } finally {
            weightLock.unlock();
        }
    }
    
    @Override
    public void deregisterServer(String serverId) {
        super.deregisterServer(serverId);
        weightLock.lock();
        try {
            serverWeights.removeIf(sw -> sw.getServer().getId().equals(serverId));
            recalculateWeights();
        } finally {
            weightLock.unlock();
        }
    }
    
    private void recalculateWeights() {
        if (serverWeights.isEmpty()) {
            maxWeight = 0;
            gcdWeight = 0;
            return;
        }
        
        maxWeight = serverWeights.stream()
                .mapToInt(sw -> sw.getServer().getWeight())
                .max()
                .orElse(0);
        
        gcdWeight = serverWeights.stream()
                .mapToInt(sw -> sw.getServer().getWeight())
                .reduce(this::gcd)
                .orElse(0);
    }
    
    private int gcd(int a, int b) {
        if (b == 0) return a;
        return gcd(b, a % b);
    }
    
    @Override
    public Server getServer(Request request) {
        weightLock.lock();
        try {
            // Filter out unhealthy servers
            List<ServerWeight> healthyServerWeights = serverWeights.stream()
                    .filter(sw -> sw.getServer().isHealthy())
                    .collect(Collectors.toList());
            
            if (healthyServerWeights.isEmpty()) {
                return null;
            }
            
            // Weighted round robin algorithm
            while (true) {
                currentIndex = (currentIndex + 1) % healthyServerWeights.size();
                if (currentIndex == 0) {
                    currentWeight = currentWeight - gcdWeight;
                    if (currentWeight <= 0) {
                        currentWeight = maxWeight;
                        if (currentWeight == 0) {
                            return healthyServerWeights.get(0).getServer();
                        }
                    }
                }
                
                ServerWeight sw = healthyServerWeights.get(currentIndex);
                if (sw.getServer().getWeight() >= currentWeight) {
                    Server server = sw.getServer();
                    updateRequestCount(server);
                    return server;
                }
            }
        } finally {
            weightLock.unlock();
        }
    }
    
    private static class ServerWeight {
        private final Server server;
        
        public ServerWeight(Server server) {
            this.server = server;
        }
        
        public Server getServer() {
            return server;
        }
    }
}

// Least Connections Load Balancer
class LeastConnectionsLoadBalancer extends AbstractLoadBalancer {
    public LeastConnectionsLoadBalancer(String name) {
        super(name);
    }
    
    @Override
    public Server getServer(Request request) {
        List<Server> healthyServers = getHealthyServers();
        if (healthyServers.isEmpty()) {
            return null;
        }
        
        Server leastConnectedServer = healthyServers.stream()
                .min(Comparator.comparingInt(Server::getActiveConnections))
                .orElse(null);
        
        updateRequestCount(leastConnectedServer);
        return leastConnectedServer;
    }
}

// IP Hash Load Balancer
class IPHashLoadBalancer extends AbstractLoadBalancer {
    public IPHashLoadBalancer(String name) {
        super(name);
    }
    
    @Override
    public Server getServer(Request request) {
        List<Server> healthyServers = getHealthyServers();
        if (healthyServers.isEmpty()) {
            return null;
        }
        
        // Use client ID (IP address) to determine the server
        String clientId = request.getClientId();
        int hash = clientId.hashCode();
        int index = Math.abs(hash % healthyServers.size());
        
        Server server = healthyServers.get(index);
        updateRequestCount(server);
        return server;
    }
}

// LoadBalancer Factory
class LoadBalancerFactory {
    public static LoadBalancer createLoadBalancer(String name, LoadBalancerType type) {
        switch (type) {
            case ROUND_ROBIN:
                return new RoundRobinLoadBalancer(name);
            case WEIGHTED_ROUND_ROBIN:
                return new WeightedRoundRobinLoadBalancer(name);
            case LEAST_CONNECTIONS:
                return new LeastConnectionsLoadBalancer(name);
            case IP_HASH:
                return new IPHashLoadBalancer(name);
            default:
                throw new IllegalArgumentException("Unsupported load balancer type: " + type);
        }
    }
}

// LoadBalancer Types
enum LoadBalancerType {
    ROUND_ROBIN, WEIGHTED_ROUND_ROBIN, LEAST_CONNECTIONS, IP_HASH
}
