package companies.coinbase;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class TimeMap {

    private Map<String, TreeMap<Integer, String>> map;
    public TimeMap() {
        this.map = new HashMap<>();
    }

    public void set(String key, String value, int timestamp) {
        map.computeIfAbsent(key, s -> new TreeMap<>()).put(timestamp, value);
    }

    public String get(String key, int timestamp) {
        if (!map.containsKey(key)) {
            return "";
        }

        Map.Entry<Integer, String> entry = map.get(key).floorEntry(timestamp);
        if (entry == null) {
            return "";
        }
        return entry.getValue();
    }

    public static void main(String[] args) {
        var sol = new TimeMap();
        sol.set("foo", "bar", 1);
        System.out.println(sol.get("foo", 1));
        System.out.println(sol.get("foo", 3));
        sol.set("foo", "bar2", 4);
        System.out.println(sol.get("foo", 4));
        System.out.println(sol.get("foo", 5));
    }
}
