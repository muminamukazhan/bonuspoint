import java.util.ArrayDeque;

public class BST implements IntMap {

    private static final class Node {
        int key, value;
        Node left, right;

        Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    private Node root;
    private int size;
    private long steps;

    @Override
    public void insert(int key, int value) {
        if (root == null) {
            root = new Node(key, value);
            size = 1;
            return;
        }
        Node cur = root;
        while (true) {
            steps++;
            if (key == cur.key) {
                cur.value = value;
                return;
            }
            Node next = key < cur.key ? cur.left : cur.right;
            if (next == null) {
                Node created = new Node(key, value);
                if (key < cur.key) cur.left = created;
                else cur.right = created;
                size++;
                return;
            }
            cur = next;
        }
    }

    @Override
    public Integer search(int key) {
        Node cur = root;
        while (cur != null) {
            steps++;
            if (key == cur.key) return cur.value;
            cur = key < cur.key ? cur.left : cur.right;
        }
        return null;
    }

    @Override
    public boolean delete(int key) {
        Node parent = null, cur = root;
        while (cur != null && cur.key != key) {
            steps++;
            parent = cur;
            cur = key < cur.key ? cur.left : cur.right;
        }
        if (cur == null) return false;
        steps++;

        if (cur.left != null && cur.right != null) {
            parent = cur;
            Node next = cur.right;
            while (next.left != null) {
                steps++;
                parent = next;
                next = next.left;
            }
            cur.key = next.key;
            cur.value = next.value;
            cur = next;
        }

        Node child = cur.left != null ? cur.left : cur.right;
        if (parent == null) root = child;
        else if (parent.left == cur) parent.left = child;
        else parent.right = child;
        size--;
        return true;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public long steps() {
        return steps;
    }

    @Override
    public void resetSteps() {
        steps = 0;
    }

    public int height() {
        if (root == null) return 0;
        ArrayDeque<Node> queue = new ArrayDeque<>();
        queue.add(root);
        int levels = 0;
        while (!queue.isEmpty()) {
            levels++;
            for (int i = queue.size(); i > 0; i--) {
                Node n = queue.poll();
                if (n.left != null) queue.add(n.left);
                if (n.right != null) queue.add(n.right);
            }
        }
        return levels;
    }
}