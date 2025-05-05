package companies.splunk;

import java.util.LinkedList;
import java.util.Stack;

public class BrowserHistory {

    private Stack<String> history, future;
    private String currentUrl;

    public BrowserHistory(String homepage) {
        history = new Stack<>();
        future = new Stack<>();
        currentUrl = homepage;
    }

    public void visit(String url) {
        history.push(currentUrl);
        currentUrl = url;
        future = new Stack<>();
    }

    public String back(int steps) {
        while (!history.isEmpty() && steps > 0) {
            future.push(currentUrl);
            currentUrl = history.pop();
            steps--;
        }
        return currentUrl;
    }

    public String forward(int steps) {
        while (!future.isEmpty() && steps > 0) {
            history.push(currentUrl);
            currentUrl = future.pop();
            steps--;
        }
        return currentUrl;
    }

    public static void main(String[] args) {
        BrowserHistory browserHistory = new BrowserHistory("leetcode.com");
        browserHistory.visit("google.com");       // You are in "leetcode.com". Visit "google.com"
        browserHistory.visit("facebook.com");     // You are in "google.com". Visit "facebook.com"
        browserHistory.visit("youtube.com");      // You are in "facebook.com". Visit "youtube.com"
        browserHistory.back(1);                 // You are in "youtube.com", move back to "facebook.com" return "facebook.com"
        System.out.println(browserHistory.currentUrl);
        browserHistory.back(1);                   // You are in "facebook.com", move back to "google.com" return "google.com"
        System.out.println(browserHistory.currentUrl);

        browserHistory.forward(1);                // You are in "google.com", move forward to "facebook.com" return "facebook.com"
        System.out.println(browserHistory.currentUrl);

        browserHistory.visit("linkedin.com");     // You are in "facebook.com". Visit "linkedin.com"
        System.out.println(browserHistory.currentUrl);

        browserHistory.forward(2);                // You are in "linkedin.com", you cannot move forward any steps.
        System.out.println(browserHistory.currentUrl);

        browserHistory.back(2);                   // You are in "linkedin.com", move back two steps to "facebook.com" then to "google.com". return "google.com"
        System.out.println(browserHistory.currentUrl);

        browserHistory.back(7);                   // You are in "google.com", you can move back only one step to "leetcode.com". return "leetcode.com"
        System.out.println(browserHistory.currentUrl);
    }
}
