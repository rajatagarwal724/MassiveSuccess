package lld.ooad.webcrawler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLProcessor {
    private final URLValidator urlValidator;
    private final Pattern urlPattern;
    private final Pattern relativeUrlPattern;

    public URLProcessor() {
        this.urlValidator = new URLValidator();
        this.urlPattern = Pattern.compile("href=\"(https?://[^\"]+)\"");
        this.relativeUrlPattern = Pattern.compile("href=\"(/[^\"]+)\"");
    }

    public String fetchContent(String url) {
        try {
            URL urlObj = new URL(url);
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(urlObj.openStream()))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }
                return content.toString();
            }
        } catch (Exception e) {
            System.err.println("Error fetching content from " + url + ": " + e.getMessage());
            return null;
        }
    }

    public Set<String> extractUrls(String content, String baseUrl) {
        Set<String> urls = new HashSet<>();
        
        // Extract absolute URLs
        Matcher matcher = urlPattern.matcher(content);
        while (matcher.find()) {
            String url = matcher.group(1);
            if (urlValidator.isValid(url)) {
                urls.add(url);
            }
        }

        // Extract relative URLs
        matcher = relativeUrlPattern.matcher(content);
        while (matcher.find()) {
            String relativePath = matcher.group(1);
            try {
                URL base = new URL(baseUrl);
                URL absoluteUrl = new URL(base, relativePath);
                urls.add(absoluteUrl.toString());
            } catch (Exception e) {
                // Skip invalid relative URLs
            }
        }

        return urls;
    }
} 