package companies.splunk;

public class AddElemAtMiddleOfLL {

    static class Node {
        int val;
        Node next;

        public Node(int val) {
            this.val = val;
        }
    }

    public static void main(String[] args)
    {
        // Creating the list 1->2->4->5
        Node head = new Node(1);
        head.next = new Node(2);
        head.next.next = new Node(4);
        head.next.next.next = new Node(5);

        int x = 3;
        head = insertInMiddle(head, x);
        printList(head);
    }

    private static Node insertInMiddle(Node head, int x) {
        Node slow = head;
        Node fast = head.next;
        while (null != fast && null != fast.next) {
            slow = slow.next;
            fast = fast.next.next;
        }

        Node middle = new Node(x);
        middle.next = slow.next;
        slow.next = middle;
        return head;
    }

    private static void printList(Node head) {
        Node node = head;
        while (null != node) {
            System.out.print(node.val + " ");
            node = node.next;
        }
    }
}
