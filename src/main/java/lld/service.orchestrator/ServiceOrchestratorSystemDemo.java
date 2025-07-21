package lld.service.orchestrator;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Supplier;

// Service interface representing a microservice
interface Service {
    String getId();
    String getName();
    ServiceStatus getStatus();
    CompletableFuture<ServiceResponse> execute(ServiceRequest request);
    void start();
    void stop();
}

// Service status enum
enum ServiceStatus {
    STARTING,
    RUNNING,
    DEGRADED,
    STOPPED,
    FAILED
}

// Service request and response classes
class ServiceRequest {
    private String requestId;
    private Map<String, Object> parameters;
    private Map<String, String> headers;

    public ServiceRequest(String requestId, Map<String, Object> parameters, Map<String, String> headers) {
        this.requestId = requestId;
        this.parameters = parameters;
        this.headers = headers;
    }

    public String getRequestId() { return requestId; }
    public Map<String, Object> getParameters() { return parameters; }
    public Map<String, String> getHeaders() { return headers; }
}

class ServiceResponse {
    private String requestId;
    private boolean success;
    private Object data;
    private String errorMessage;

    public ServiceResponse(String requestId, boolean success, Object data, String errorMessage) {
        this.requestId = requestId;
        this.success = success;
        this.data = data;
        this.errorMessage = errorMessage;
    }

    public String getRequestId() { return requestId; }
    public boolean isSuccess() { return success; }
    public Object getData() { return data; }
    public String getErrorMessage() { return errorMessage; }
}

// Circuit Breaker implementation
class CircuitBreaker {
    private final int failureThreshold;
    private final long resetTimeout;
    private int failureCount;
    private long lastFailureTime;
    private CircuitState state;

    public CircuitBreaker(int failureThreshold, long resetTimeout) {
        this.failureThreshold = failureThreshold;
        this.resetTimeout = resetTimeout;
        this.failureCount = 0;
        this.state = CircuitState.CLOSED;
    }

    public boolean allowRequest() {
        if (state == CircuitState.OPEN) {
            if (System.currentTimeMillis() - lastFailureTime > resetTimeout) {
                state = CircuitState.HALF_OPEN;
                return true;
            }
            return false;
        }
        return true;
    }

    public void recordSuccess() {
        if (state == CircuitState.HALF_OPEN) {
            state = CircuitState.CLOSED;
            failureCount = 0;
        }
    }

    public void recordFailure() {
        failureCount++;
        lastFailureTime = System.currentTimeMillis();
        if (failureCount >= failureThreshold) {
            state = CircuitState.OPEN;
        }
    }

    private enum CircuitState {
        CLOSED, OPEN, HALF_OPEN
    }
}

// Service Registry for service discovery
class ServiceRegistry {
    private final Map<String, Service> services;
    private final Map<String, Set<String>> dependencies;

    public ServiceRegistry() {
        this.services = new ConcurrentHashMap<>();
        this.dependencies = new ConcurrentHashMap<>();
    }

    public void registerService(Service service) {
        services.put(service.getId(), service);
        dependencies.put(service.getId(), new HashSet<>());
    }

    public void addDependency(String serviceId, String dependencyId) {
        dependencies.computeIfAbsent(serviceId, k -> new HashSet<>()).add(dependencyId);
    }

    public Set<String> getDependencies(String serviceId) {
        return dependencies.getOrDefault(serviceId, Collections.emptySet());
    }

    public Service getService(String serviceId) {
        return services.get(serviceId);
    }

    public List<Service> getAllServices() {
        return new ArrayList<>(services.values());
    }
}

// Service Orchestrator implementation
class ServiceOrchestrator {
    private final ServiceRegistry registry;
    private final Map<String, CircuitBreaker> circuitBreakers;
    private final ScheduledExecutorService healthCheckExecutor;
    private final ExecutorService serviceExecutor;

    public ServiceOrchestrator() {
        this.registry = new ServiceRegistry();
        this.circuitBreakers = new ConcurrentHashMap<>();
        this.healthCheckExecutor = Executors.newScheduledThreadPool(1);
        this.serviceExecutor = Executors.newFixedThreadPool(10);
        startHealthChecks();
    }

    private void startHealthChecks() {
        healthCheckExecutor.scheduleAtFixedRate(() -> {
            for (Service service : registry.getAllServices()) {
                checkServiceHealth(service);
            }
        }, 0, 30, TimeUnit.SECONDS);
    }

    private void checkServiceHealth(Service service) {
        try {
            ServiceRequest healthCheckRequest = new ServiceRequest(
                UUID.randomUUID().toString(),
                Collections.emptyMap(),
                Collections.singletonMap("type", "health_check")
            );
            
            CompletableFuture<ServiceResponse> future = service.execute(healthCheckRequest);
            future.get(5, TimeUnit.SECONDS);
            
            if (service.getStatus() != ServiceStatus.RUNNING) {
                service.start();
            }
        } catch (Exception e) {
            handleServiceFailure(service);
        }
    }

    private void handleServiceFailure(Service service) {
        CircuitBreaker breaker = circuitBreakers.computeIfAbsent(
            service.getId(),
            k -> new CircuitBreaker(3, 30000)
        );
        breaker.recordFailure();
        
        if (breaker.allowRequest()) {
            service.start();
        } else {
            service.stop();
        }
    }

    public void registerService(Service service) {
        registry.registerService(service);
        circuitBreakers.put(service.getId(), new CircuitBreaker(3, 30000));
    }

    public void addDependency(String serviceId, String dependencyId) {
        registry.addDependency(serviceId, dependencyId);
    }

    public CompletableFuture<ServiceResponse> executeService(
        String serviceId,
        ServiceRequest request
    ) {
        Service service = registry.getService(serviceId);
        if (service == null) {
            return CompletableFuture.failedFuture(
                new RuntimeException("Service not found: " + serviceId)
            );
        }

        CircuitBreaker breaker = circuitBreakers.get(serviceId);
        if (!breaker.allowRequest()) {
            return CompletableFuture.failedFuture(
                new RuntimeException("Circuit breaker is open for service: " + serviceId)
            );
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                ServiceResponse response = service.execute(request).get();
                if (response.isSuccess()) {
                    breaker.recordSuccess();
                } else {
                    breaker.recordFailure();
                }
                return response;
            } catch (Exception e) {
                breaker.recordFailure();
                throw new CompletionException(e);
            }
        }, serviceExecutor);
    }

    public void shutdown() {
        healthCheckExecutor.shutdown();
        serviceExecutor.shutdown();
        try {
            if (!healthCheckExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                healthCheckExecutor.shutdownNow();
            }
            if (!serviceExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                serviceExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

// Example Service Implementation
class ExampleService implements Service {
    private final String id;
    private final String name;
    private volatile ServiceStatus status;
    private final Random random;

    public ExampleService(String id, String name) {
        this.id = id;
        this.name = name;
        this.status = ServiceStatus.STOPPED;
        this.random = new Random();
    }

    @Override
    public String getId() { return id; }

    @Override
    public String getName() { return name; }

    @Override
    public ServiceStatus getStatus() { return status; }

    @Override
    public CompletableFuture<ServiceResponse> execute(ServiceRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Simulate processing time
                Thread.sleep(random.nextInt(1000));
                
                // Simulate occasional failures
                if (random.nextDouble() < 0.2) {
                    throw new RuntimeException("Simulated failure");
                }

                return new ServiceResponse(
                    request.getRequestId(),
                    true,
                    "Processed by " + name,
                    null
                );
            } catch (Exception e) {
                return new ServiceResponse(
                    request.getRequestId(),
                    false,
                    null,
                    e.getMessage()
                );
            }
        });
    }

    @Override
    public void start() {
        status = ServiceStatus.RUNNING;
        System.out.println(name + " started");
    }

    @Override
    public void stop() {
        status = ServiceStatus.STOPPED;
        System.out.println(name + " stopped");
    }
}

public class ServiceOrchestratorSystemDemo {
    public static void main(String[] args) {
        // Create orchestrator
        ServiceOrchestrator orchestrator = new ServiceOrchestrator();

        // Create and register services
        Service authService = new ExampleService("auth", "Authentication Service");
        Service userService = new ExampleService("user", "User Service");
        Service paymentService = new ExampleService("payment", "Payment Service");

        orchestrator.registerService(authService);
        orchestrator.registerService(userService);
        orchestrator.registerService(paymentService);

        // Define service dependencies
        orchestrator.addDependency("user", "auth");
        orchestrator.addDependency("payment", "user");

        // Start services
        authService.start();
        userService.start();
        paymentService.start();

        // Execute some requests
        for (int i = 0; i < 10; i++) {
            ServiceRequest request = new ServiceRequest(
                UUID.randomUUID().toString(),
                Collections.singletonMap("amount", 100.0),
                Collections.singletonMap("user", "test_user")
            );

            orchestrator.executeService("payment", request)
                .thenAccept(response -> {
                    if (response.isSuccess()) {
                        System.out.println("Payment processed: " + response.getData());
                    } else {
                        System.out.println("Payment failed: " + response.getErrorMessage());
                    }
                })
                .exceptionally(e -> {
                    System.out.println("Error processing payment: " + e.getMessage());
                    return null;
                });
        }

        // Wait for requests to complete
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Shutdown orchestrator
        orchestrator.shutdown();
    }
}
