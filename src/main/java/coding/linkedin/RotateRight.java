package coding.linkedin;

public class RotateRight {

    static class ListNode {
        int val;
        ListNode next;
        ListNode() {}
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    public ListNode rotateRight(ListNode head, int k) {
        // base cases
        if (head == null) return null;
        if (head.next == null) return head;

        // close the linked list into the ring
        ListNode old_tail = head;
        int n;
        for (n = 1; old_tail.next != null; n++) old_tail = old_tail.next;
        old_tail.next = head;

        // find new tail : (n - k % n - 1)th node
        // and new head : (n - k % n)th node
        ListNode new_tail = head;
        for (int i = 0; i < n - (k % n) - 1; i++) new_tail = new_tail.next;
        ListNode new_head = new_tail.next;

        // break the ring
        new_tail.next = null;

        return new_head;
    }

    public ListNode rotate(ListNode head, int k) {
        ListNode old_tail = head;

        int n;

        for (n = 1; old_tail.next != null; n++) {
            old_tail = old_tail.next;
        }

        old_tail.next = head;

        ListNode new_tail = head;
        for (int i = 0; i < (n - (k % n) - 1); i++) {
            new_tail = new_tail.next;
        }

        ListNode new_head = new_tail.next;

        new_tail.next = null;

        return new_head;
    }

    public static void main(String[] args) {
        var sol = new RotateRight();
        var head = new ListNode(1);
        head.next = new ListNode(2);
        head.next.next = new ListNode(3);
        head.next.next.next = new ListNode(4);
        head.next.next.next.next = new ListNode(5);

//        var res = sol.rotateRight(
//            head, 2
//        );
//
//        System.out.println(res);

        var res = sol.rotate(
                head, 2
        );

        System.out.println(res);
    }
}
