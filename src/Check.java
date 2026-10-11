import java.util.*;
import java.util.function.Supplier;

public class Check {

    static void verify(String name, Supplier<IntMap> factory) {
        Random rnd = new Random(7);
        IntMap map = factory.get();
        Map<Integer, Integer> ref = new HashMap<>();

        for (int i = 0; i < 200_000; i++) {
            int key = rnd.nextInt(2000);
            int op = rnd.nextInt(3);
            if (op == 0) {
                map.insert(key, i);
                ref.put(key, i);
            } else if (op == 1) {
                if (!Objects.equals(map.search(key), ref.get(key)))
                    throw new IllegalStateException(name + ": search mismatch, key " + key);
            } else {
                if (map.delete(key) != (ref.remove(key) != null))
                    throw new IllegalStateException(name + ": delete mismatch, key " + key);
            }
            if (map.size() != ref.size())
                throw new IllegalStateException(name + ": size mismatch");
        }
        System.out.println(name + ": OK");
    }

    public static void main(String[] args) {
        verify("BST", BST::new);
        verify("HashTable", HashTable::new);
    }
}