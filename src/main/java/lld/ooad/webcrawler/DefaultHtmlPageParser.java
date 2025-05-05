package lld.ooad.webcrawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLStreamHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class DefaultHtmlPageParser implements HtmlParser {
    
    @Override
    public List<String> getUrls(String url) throws IOException {
        if (StringUtils.isBlank(url)) {
            return Collections.emptyList();
        }
        Set<String> urls = new HashSet<>();
        var urlObj = new URL(url);
        try(var stream = new BufferedReader(new InputStreamReader(urlObj.openStream()))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = stream.readLine()) != null) {
                sb.append(line);
            }
            String content = sb.toString();
            Pattern pattern = Pattern.compile("href=\"(https?://[^\"]+)\"");
            Matcher matcher = pattern.matcher(content);
            while (matcher.find()) {
                String newUrl = matcher.group(1);
                urls.add(newUrl);
            }
        }
        return new ArrayList<>(urls);
    }
}