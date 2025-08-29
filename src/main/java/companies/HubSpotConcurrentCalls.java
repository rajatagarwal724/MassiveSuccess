package companies;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class HubSpotConcurrentCalls {
    
    private static final String USER_KEY = "5524d8682632e78024cf794ea10a";
    private static final String DATA_URL = "https://candidate.hubteam.com/candidateTest/v3/problem/dataset?userKey=" + USER_KEY;
    private static final String RESULT_URL = "https://candidate.hubteam.com/candidateTest/v3/problem/result?userKey=" + USER_KEY;
    private static final String TEST_DATA_URL = "https://candidate.hubteam.com/candidateTest/v3/problem/test-dataset?userKey=" + USER_KEY;
    private static final String TEST_RESULT_URL = "https://candidate.hubteam.com/candidateTest/v3/problem/test-result?userKey=" + USER_KEY;
    
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    public HubSpotConcurrentCalls() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }
    
    // Data models
    static class Call {
        @JsonProperty("customerId")
        public int customerId;
        
        @JsonProperty("callId")
        public String callId;
        
        @JsonProperty("startTimestamp")
        public long startTimestamp;
        
        @JsonProperty("endTimestamp")
        public long endTimestamp;
        
        @Override
        public String toString() {
            return String.format("Call{customerId=%d, callId='%s', start=%d, end=%d}", 
                customerId, callId, startTimestamp, endTimestamp);
        }
    }
    
    static class Result {
        @JsonProperty("customerId")
        public int customerId;
        
        @JsonProperty("date")
        public String date;
        
        @JsonProperty("maxConcurrentCalls")
        public int maxConcurrentCalls;
        
        @JsonProperty("timestamp")
        public long timestamp;
        
        @JsonProperty("callIds")
        public List<String> callIds;
        
        public Result(int customerId, String date, int maxConcurrentCalls, long timestamp, List<String> callIds) {
            this.customerId = customerId;
            this.date = date;
            this.maxConcurrentCalls = maxConcurrentCalls;
            this.timestamp = timestamp;
            this.callIds = callIds;
        }
    }
    
    static class ResultsWrapper {
        @JsonProperty("results")
        public List<Result> results;
        
        public ResultsWrapper(List<Result> results) {
            this.results = results;
        }
    }
    
    // Event for sweep line algorithm
    static class Event implements Comparable<Event> {
        long timestamp;
        int type; // 1 for start, -1 for end
        String callId;
        
        Event(long timestamp, int type, String callId) {
            this.timestamp = timestamp;
            this.type = type;
            this.callId = callId;
        }
        
        @Override
        public int compareTo(Event other) {
            if (this.timestamp != other.timestamp) {
                return Long.compare(this.timestamp, other.timestamp);
            }
            // Process end events before start events at same timestamp
            return Integer.compare(other.type, this.type);
        }
    }
    
    /**
     * Fetch call data from the API
     */
    public List<Call> fetchCallData(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to fetch data: " + response.statusCode() + " - " + response.body());
        }
        
        return objectMapper.readValue(response.body(), new TypeReference<List<Call>>() {});
    }
    
    /**
     * Convert UNIX timestamp to UTC date string
     */
    private String timestampToDate(long timestamp) {
        return Instant.ofEpochMilli(timestamp)
                .atZone(ZoneOffset.UTC)
                .toLocalDate()
                .format(DateTimeFormatter.ISO_LOCAL_DATE);
    }
    
    /**
     * Get start and end of day in UTC for a given date
     */
    private long[] getDayBounds(String dateStr) {
        LocalDate date = LocalDate.parse(dateStr);
        long startOfDay = date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
        long endOfDay = date.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
        return new long[]{startOfDay, endOfDay};
    }
    
    /**
     * Calculate maximum concurrent calls for a customer on a specific date
     */
    public Result calculateMaxConcurrentCalls(int customerId, String date, List<Call> customerCalls) {
        long[] dayBounds = getDayBounds(date);
        long startOfDay = dayBounds[0];
        long endOfDay = dayBounds[1];
        
        // Filter calls that overlap with this date
        List<Call> relevantCalls = customerCalls.stream()
                .filter(call -> call.startTimestamp < endOfDay && call.endTimestamp > startOfDay)
                .collect(Collectors.toList());
        
        if (relevantCalls.isEmpty()) {
            return null; // No calls on this date
        }
        
        // Create events for sweep line algorithm
        List<Event> events = new ArrayList<>();
        for (Call call : relevantCalls) {
            // Clamp timestamps to the day bounds
            long effectiveStart = Math.max(call.startTimestamp, startOfDay);
            long effectiveEnd = Math.min(call.endTimestamp, endOfDay);
            
            events.add(new Event(effectiveStart, 1, call.callId));
            events.add(new Event(effectiveEnd, -1, call.callId));
        }
        
        Collections.sort(events);
        
        int currentConcurrent = 0;
        int maxConcurrent = 0;
        long maxTimestamp = startOfDay;
        Set<String> currentCalls = new HashSet<>();
        Set<String> maxCalls = new HashSet<>();
        
        for (Event event : events) {
            if (event.type == 1) { // Start event
                currentCalls.add(event.callId);
                currentConcurrent++;
                
                if (currentConcurrent > maxConcurrent) {
                    maxConcurrent = currentConcurrent;
                    maxTimestamp = event.timestamp;
                    maxCalls = new HashSet<>(currentCalls);
                }
            } else { // End event
                currentCalls.remove(event.callId);
                currentConcurrent--;
            }
        }
        
        return new Result(customerId, date, maxConcurrent, maxTimestamp, new ArrayList<>(maxCalls));
    }
    
    /**
     * Process all calls and generate results
     */
    public List<Result> processCallData(List<Call> calls) {
        List<Result> results = new ArrayList<>();
        
        // Group calls by customer
        Map<Integer, List<Call>> callsByCustomer = calls.stream()
                .collect(Collectors.groupingBy(call -> call.customerId));
        
        for (Map.Entry<Integer, List<Call>> entry : callsByCustomer.entrySet()) {
            int customerId = entry.getKey();
            List<Call> customerCalls = entry.getValue();
            
            // Get all dates that have calls for this customer
            Set<String> dates = new HashSet<>();
            for (Call call : customerCalls) {
                // A call might span multiple days
                long currentDay = call.startTimestamp;
                long endDay = call.endTimestamp;
                
                while (currentDay < endDay) {
                    dates.add(timestampToDate(currentDay));
                    // Move to next day
                    LocalDate date = Instant.ofEpochMilli(currentDay)
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate()
                            .plusDays(1);
                    currentDay = date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
                }
            }
            
            // Calculate max concurrent calls for each date
            for (String date : dates) {
                Result result = calculateMaxConcurrentCalls(customerId, date, customerCalls);
                if (result != null) {
                    results.add(result);
                }
            }
        }
        
        return results;
    }
    
    /**
     * Submit results to the API
     */
    public void submitResults(List<Result> results, String url) throws IOException, InterruptedException {
        ResultsWrapper wrapper = new ResultsWrapper(results);
        String jsonBody = objectMapper.writeValueAsString(wrapper);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        System.out.println("Response Status: " + response.statusCode());
        System.out.println("Response Body: " + response.body());
        
        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to submit results: " + response.statusCode() + " - " + response.body());
        }
    }
    
    /**
     * Test the solution with test dataset
     */
    public void testSolution() throws IOException, InterruptedException {
        System.out.println("Testing solution with test dataset...");
        
        List<Call> testCalls = fetchCallData(TEST_DATA_URL);
        System.out.println("Fetched " + testCalls.size() + " test calls");
        
        List<Result> testResults = processCallData(testCalls);
        System.out.println("Generated " + testResults.size() + " test results");
        
        submitResults(testResults, TEST_RESULT_URL);
        System.out.println("Test completed successfully!");
    }
    
    /**
     * Run the full solution
     */
    public void runSolution() throws IOException, InterruptedException {
        System.out.println("Running full solution...");
        
        List<Call> calls = fetchCallData(DATA_URL);
        System.out.println("Fetched " + calls.size() + " calls");
        
        List<Result> results = processCallData(calls);
        System.out.println("Generated " + results.size() + " results");
        
        submitResults(results, RESULT_URL);
        System.out.println("Solution submitted successfully!");
    }
    
    public static void main(String[] args) {
        HubSpotConcurrentCalls solution = new HubSpotConcurrentCalls();
        
        try {
            // First test with small dataset
            solution.testSolution();
            
            // Then run the full solution
            solution.runSolution();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
