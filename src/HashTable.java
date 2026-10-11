public class HashTable implements IntMap {

    private static final byte EMPTY = 0, USED = 1, DELETED = 2;

    private int[] keys, vals;
    private byte[] state;
    private int bits, cap, size, used;
    private long steps;

    public HashTable() {
        init(4);
    }

    private void init(int newBits) {
        bits = newBits;
        cap = 1 << bits;
        keys = new int[cap];
        vals = new int[cap];
        state = new byte[cap];
        size = 0;
        used = 0;
    }

    private int index(int key) {
        return (key * 0x9E3779B9) >>> (32 - bits);
    }

    private void resize() {
        int[] oldKeys = keys, oldVals = vals;
        byte[] oldState = state;
        init(bits + 1);
        for (int i = 0; i < oldKeys.length; i++) {
            if (oldState[i] == USED) insert(oldKeys[i], oldVals[i]);
        }
    }

    @Override
    public void insert(int key, int value) {
        if ((used + 1) * 10L > cap * 6L) resize();
        int i = index(key), firstFree = -1;
        while (state[i] != EMPTY) {
            steps++;
            if (state[i] == DELETED) {
                if (firstFree < 0) firstFree = i;
            } else if (keys[i] == key) {
                vals[i] = value;
                return;
            }
            i = (i + 1) & (cap - 1);
        }
        steps++;
        int slot = firstFree >= 0 ? firstFree : i;
        if (firstFree < 0) used++;
        keys[slot] = key;
        vals[slot] = value;
        state[slot] = USED;
        size++;
    }

    private int find(int key) {
        int i = index(key);
        while (state[i] != EMPTY) {
            steps++;
            if (state[i] == USED && keys[i] == key) return i;
            i = (i + 1) & (cap - 1);
        }
        steps++;
        return -1;
    }

    @Override
    public Integer search(int key) {
        int i = find(key);
        return i < 0 ? null : vals[i];
    }

    @Override
    public boolean delete(int key) {
        int i = find(key);
        if (i < 0) return false;
        state[i] = DELETED;
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
}