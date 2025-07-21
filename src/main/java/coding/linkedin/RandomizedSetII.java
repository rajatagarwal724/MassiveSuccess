//package coding.linkedin;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Random;
//
//public class RandomizedSetII {
//    private Map<Integer, Integer> map;
//    private List<Integer> list;
//
//    public RandomizedSetII() {
//        map = new HashMap<>();
//        list = new ArrayList<>();
//    }
//
//    public boolean insert(int val) {
//        if (map.containsKey(val)) {
//            return false;
//        }
//        list.add(val);
//        map.put(val, list.size() - 1);
//        return true;
//    }
//
//    public boolean remove(int val) {
//        if (!map.containsKey(val)) {
//            return false;
//        }
//
//        int valueAtLastIndex = list.get(list.size() - 1);
//
//        int indexToSwap = map.get(val);
//        list.removeLast();
//        list.set(indexToSwap, valueAtLastIndex);
//
//        map.put(valueAtLastIndex, indexToSwap);
//        map.remove(val);
//        return true;
//    }
//
//    public int getRandom() {
//        var random = new Random();
//        int nextInt = random.nextInt(list.size());
//        return list.get(nextInt);
//    }
//
//    public static void main(String[] args) {
//        var sol = new RandomizedSetII();
//        System.out.println(sol.insert(1));
//        System.out.println(sol.remove(2));
//        System.out.println(sol.insert(2));
//        System.out.println(sol.getRandom());
//        System.out.println(sol.remove(1));
//        System.out.println(sol.insert(2));
//        System.out.println(sol.getRandom());
//    }
//}
