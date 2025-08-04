package companies.coinbase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class IncrementalMap {

    class DataSnapshot {
        Map<String, Map<String, TreeMap<Long, String>>> dataMap;
        Map<String, Map<String, Long>> ttlMap;

        public DataSnapshot(Map<String, Map<String, TreeMap<Long, String>>> dataMap, Map<String, Map<String, Long>> ttlMap) {
            this.dataMap = dataMap.entrySet().stream().collect(
                    Collectors.toMap(
                            Map.Entry::getKey,
                            innerEntry -> new TreeMap<>(innerEntry.getValue())
                    )
            );
            this.ttlMap = ttlMap.entrySet().stream().collect(
                    Collectors.toMap(
                            Map.Entry::getKey,
                            innerEntry -> new HashMap<>(innerEntry.getValue())
                    )
            );
        }
    }

    Map<String, Map<String, TreeMap<Long, String>>> dataMap;
    Map<String, Map<String, Long>> ttlMap;

    Stack<DataSnapshot> undoStack, redoStack;

    public IncrementalMap(Map<String, Map<String, TreeMap<Long, String>>> dataMap, Map<String, Map<String, Long>> ttlMap, Stack<DataSnapshot> undoStack, Stack<DataSnapshot> redoStack) {
        this.dataMap = dataMap;
        this.ttlMap = ttlMap;
        this.undoStack = undoStack;
        this.redoStack = redoStack;
    }

    public void saveState() {
        DataSnapshot snapshot = new DataSnapshot(dataMap, ttlMap);
        undoStack.push(snapshot);
        redoStack.clear();
    }

    public boolean undo() {
        if (undoStack.isEmpty()) {
            return false;
        }

        var currentSnapshot = new DataSnapshot(dataMap, ttlMap);
        redoStack.push(currentSnapshot);

        var prevSnapshot = undoStack.pop();
        restoreState(prevSnapshot);
        return true;
    }

    public boolean redo() {
        if (redoStack.isEmpty()) {
            return false;
        }

        var currentSnapshot = new DataSnapshot(dataMap, ttlMap);
        undoStack.push(currentSnapshot);

        var redoState = redoStack.pop();
        restoreState(redoState);
        return true;
    }

    public void restoreState(DataSnapshot state) {
        dataMap.clear();
        ttlMap.clear();

        dataMap.putAll(state.dataMap.entrySet().stream().collect(
                Collectors.toMap(
                        Map.Entry::getKey,
                        innerEntry -> new TreeMap<>(innerEntry.getValue())
                )
        ));
        ttlMap.putAll(state.ttlMap.entrySet().stream().collect(
                Collectors.toMap(
                        Map.Entry::getKey,
                        innerEntry -> new HashMap<>(innerEntry.getValue())
                )
        ));
    }

    public void set(String key, String subKey, String value, Long timestamp) {
        set(key, subKey, value, timestamp, -1, System.currentTimeMillis());
    }

    public void set(String key, String subKey, String value, Long timestamp, int ttlSeconds, long currentTimeMillis) {
        saveState();
        dataMap.computeIfAbsent(key, subMap -> new HashMap<>())
                .computeIfAbsent(subKey, timeMap -> new TreeMap<>())
                .put(timestamp, value);

        if (ttlSeconds > 0) {
            ttlMap.computeIfAbsent(key, subMap -> new HashMap<>())
                    .put(subKey, currentTimeMillis + (ttlSeconds * 1000));
        } else {
            ttlMap.getOrDefault(key, new TreeMap<>()).remove(subKey);
        }
    }

    public String get(String key, String subKey, Long currentTs) {
        if (!dataMap.containsKey(key) || !dataMap.getOrDefault(key, new HashMap<>()).containsKey(subKey)
        || dataMap.getOrDefault(key, new HashMap<>()).get(subKey).isEmpty()
        || isExpired(key, subKey, currentTs)) {
            return null;
        }

        return dataMap.get(key).get(subKey).lastEntry().getValue();
    }

    private boolean isExpired(String key, String subKey, Long currentTs) {
        if (!ttlMap.getOrDefault(key, new HashMap<>()).containsKey(subKey)) {
            return false;
        }

        long ts = ttlMap.get(key).get(subKey);
        return currentTs >= ts;
    }

    public String get(String key, String subKey) {
        if (!dataMap.containsKey(key) || !dataMap.getOrDefault(key, new HashMap<>()).containsKey(subKey)
                || dataMap.getOrDefault(key, new HashMap<>()).get(subKey).isEmpty()) {
            return null;
        }

        return dataMap.get(key).get(subKey).lastEntry().getValue();
    }

    public List<String> prefixSearch(String prefix, Long currentTs) {
        List<String> result = new ArrayList<>();
        dataMap.entrySet().forEach(entry -> {
            if (entry.getKey().startsWith(prefix)) {
                result.add(entry.getKey());
            }
        });

        return result;
    }

    public Map<String, Map<String, Map<Long, String>>> state(Long currentTs) {
        Map<String, Map<String, Map<Long, String>>> result = new HashMap<>();
        for (var outerEntry: dataMap.entrySet()) {
            for (var subEntry: dataMap.getOrDefault(outerEntry.getKey(), new HashMap<>()).entrySet()) {
                if (!isExpired(outerEntry.getKey(), subEntry.getKey(), currentTs)) {
                    result.computeIfAbsent(outerEntry.getKey(), sub -> new HashMap<>())
                            .computeIfAbsent(subEntry.getKey(), time -> new TreeMap<>())
                            .putAll(subEntry.getValue());
                }
            }
        }
        return result;
    }
}
