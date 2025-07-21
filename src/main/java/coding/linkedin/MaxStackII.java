//package coding.linkedin;
//
//import java.util.TreeSet;
//
//public class MaxStackII {
//
////    class Node {
////        int value;
////        int index;
////
////        public Node(int value, int index) {
////            this.value = value;
////            this.index = index;
////        }
////    }
//
//    record Node(int value, int index) {}
//
//    private TreeSet<Node> stack, maxStack;
//    int currIndex = 0;
//
//    public MaxStackII() {
//        this.stack = new TreeSet<>((n1, n2) -> n1.index - n2.index);
//        this.maxStack = new TreeSet<>((n1, n2) -> {
//            if(n1.value == n2.value) {
//                return n1.index - n2.index;
//            }
//            return n1.value - n2.value;
//        });
//    }
//
//    public void push(int x) {
//        var node = new Node(x, currIndex++);
//        stack.add(node);
//        maxStack.add(node);
//    }
//
//    public int pop() {
//        var node = stack.removeLast();
//        maxStack.remove(node);
//        return node.value;
//    }
//
//    public int top() {
//        return stack.last().value;
//    }
//
//    public int peekMax() {
//        return maxStack.last().value;
//    }
//
//    public int popMax() {
//        var node = maxStack.removeLast();
//        stack.remove(node);
//        return node.value;
//    }
//}
