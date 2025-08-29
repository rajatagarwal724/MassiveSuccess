package companies;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * HubSpot Concurrent Calls Solution
 * 
 * Problem: Calculate maximum concurrent calls per customer per day for billing purposes.
 * 
 * Algorithm:
 * 1. Fetch call data from API
 * 2. Group calls by customer
 * 3. For each customer and date, use sweep line algorithm to find max concurrent calls
 * 4. Submit results back to API
 * 
 * Time Complexity: O(N log N) where N is number of calls
 * Space Complexity: O(N) for storing calls and events
 */
public class HubSpotConcurrentCallsSimple {
    
    private static final String USER_KEY = "5524d8682632e78024cf794ea10a";
    private static final String DATA_URL = "https://candidate.hubteam.com/candidateTest/v3/problem/dataset?userKey=" + USER_KEY;
    private static final String RESULT_URL = "https://candidate.hubteam.com/candidateTest/v3/problem/result?userKey=" + USER_KEY;
    private static final String TEST_DATA_URL = "https://candidate.hubteam.com/candidateTest/v3/problem/test-dataset?userKey=" + USER_KEY;
    private static final String TEST_RESULT_URL = "https://candidate.hubteam.com/candidateTest/v3/problem/test-result?userKey=" + USER_KEY;
    
    private final HttpClient httpClient;
    
    public HubSpotConcurrentCallsSimple() {
        this.httpClient = HttpClient.newHttpClient();
    }
    
    // Data models
    static class Call {
        int customerId;
        String callId;
        long startTimestamp;
        long endTimestamp;
        
        Call(int customerId, String callId, long startTimestamp, long endTimestamp) {
            this.customerId = customerId;
            this.callId = callId;
            this.startTimestamp = startTimestamp;
            this.endTimestamp = endTimestamp;
        }
        
        @Override
        public String toString() {
            return String.format("Call{customerId=%d, callId='%s', start=%d, end=%d}", 
                customerId, callId, startTimestamp, endTimestamp);
        }
    }
    
    static class Result {
        int customerId;
        String date;
        int maxConcurrentCalls;
        long timestamp;
        List<String> callIds;
        
        Result(int customerId, String date, int maxConcurrentCalls, long timestamp, List<String> callIds) {
            this.customerId = customerId;
            this.date = date;
            this.maxConcurrentCalls = maxConcurrentCalls;
            this.timestamp = timestamp;
            this.callIds = callIds;
        }
        
        // Convert to JSON string
        public String toJson() {
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            sb.append("\"customerId\":").append(customerId).append(",");
            sb.append("\"date\":\"").append(date).append("\",");
            sb.append("\"maxConcurrentCalls\":").append(maxConcurrentCalls).append(",");
            sb.append("\"timestamp\":").append(timestamp).append(",");
            sb.append("\"callIds\":[");
            for (int i = 0; i < callIds.size(); i++) {
                if (i > 0) sb.append(",");
                sb.append("\"").append(callIds.get(i)).append("\"");
            }
            sb.append("]}");
            return sb.toString();
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
            // Process end events before start events at same timestamp (exclusive end)
            return Integer.compare(other.type, this.type);
        }
    }
    
    /**
     * Simple JSON parser for the call data
     */
    public List<Call> parseCallData(String jsonResponse) {
        List<Call> calls = new ArrayList<>();
        
        // Patterns to extract call data
        Pattern callPattern = Pattern.compile("\\{[^}]+\\}");
        Pattern customerIdPattern = Pattern.compile("\"customerId\"\\s*:\\s*(\\d+)");
        Pattern callIdPattern = Pattern.compile("\"callId\"\\s*:\\s*\"([^\"]+)\"");
        Pattern startPattern = Pattern.compile("\"startTimestamp\"\\s*:\\s*(\\d+)");
        Pattern endPattern = Pattern.compile("\"endTimestamp\"\\s*:\\s*(\\d+)");
        
        Matcher callMatcher = callPattern.matcher(jsonResponse);
        
        while (callMatcher.find()) {
            String callJson = callMatcher.group();
            
            Matcher customerMatcher = customerIdPattern.matcher(callJson);
            Matcher idMatcher = callIdPattern.matcher(callJson);
            Matcher startMatcher = startPattern.matcher(callJson);
            Matcher endMatcher = endPattern.matcher(callJson);
            
            if (customerMatcher.find() && idMatcher.find() && 
                startMatcher.find() && endMatcher.find()) {
                
                int customerId = Integer.parseInt(customerMatcher.group(1));
                String callId = idMatcher.group(1);
                long startTimestamp = Long.parseLong(startMatcher.group(1));
                long endTimestamp = Long.parseLong(endMatcher.group(1));
                
                calls.add(new Call(customerId, callId, startTimestamp, endTimestamp));
            }
        }
        
        return calls;
    }
    
    /**
     * Convert results to JSON format
     */
    public String resultsToJson(List<Result> results) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"results\":[");
        for (int i = 0; i < results.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(results.get(i).toJson());
        }
        sb.append("]}");
        return sb.toString();
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
        
        return parseCallData(response.body());
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
     * Calculate maximum concurrent calls for a customer on a specific date using sweep line algorithm
     * 
     * The algorithm:
     * 1. Create start/end events for all calls that overlap with the given date
     * 2. Sort events by timestamp (end events before start events for same timestamp)
     * 3. Sweep through events, tracking current concurrent calls
     * 4. Record maximum concurrent calls and the calls active at that time
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
            // Clamp timestamps to the day bounds for proper date handling
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
        
        // Sweep line algorithm
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
     * Process all calls and generate results for each customer and date
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
                // A call might span multiple days, so we need to check each day it covers
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
        String jsonBody = resultsToJson(results);
        
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
        
        // Debug: Print some test calls
        for (int i = 0; i < Math.min(3, testCalls.size()); i++) {
            System.out.println("Test call " + i + ": " + testCalls.get(i));
        }
        
        List<Result> testResults = processCallData(testCalls);
        System.out.println("Generated " + testResults.size() + " test results");
        
        // Debug: Print some test results
        for (int i = 0; i < Math.min(3, testResults.size()); i++) {
            Result r = testResults.get(i);
            System.out.println("Test result " + i + ": Customer " + r.customerId + 
                ", Date " + r.date + ", Max " + r.maxConcurrentCalls + 
                ", Calls " + r.callIds.size());
        }
        
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
        HubSpotConcurrentCallsSimple solution = new HubSpotConcurrentCallsSimple();
        
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
