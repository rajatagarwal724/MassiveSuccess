//package coding.linkedin;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Random;
//
//public class RandomizedSet {
//
//    private final Map<Integer, Integer> map;
//    private final List<Integer> list;
//
//    public RandomizedSet() {
//        this.map = new HashMap<>();
//        this.list = new ArrayList<>();
//    }
//
//    public boolean insert(int val) {
//        if (map.containsKey(val)) {
//            return false;
//        }
//        map.put(val, list.size());
//        list.add(map.get(val), val);
//        return true;
//    }
//
//    public boolean remove(int val) {
//        if (!map.containsKey(val)) {
//            return false;
//        }
//
//        int lastElement = list.get(list.size() - 1);
//        int idx = map.get(val);
//        list.set(idx, lastElement);
//        map.put(lastElement, idx);
//
//        map.remove(val);
//        list.removeLast();
//        return true;
//    }
//
//    public int getRandom() {
//        var random = new Random();
//        return list.get(random.nextInt(list.size()));
//    }
//
//    public static void main(String[] args) {
//        var sol = new RandomizedSet();
//        System.out.println(sol.insert(1));
//        System.out.println(sol.remove(2));
//        System.out.println(sol.insert(2));
//        System.out.println(sol.getRandom());
//        System.out.println(sol.remove(1));
//        System.out.println(sol.insert(2));
//        System.out.println(sol.getRandom());
//    }
//}
