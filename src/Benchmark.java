import java.io.PrintWriter;
import java.util.*;
import java.util.function.Supplier;

public class Benchmark {

    static final int[] SIZES = {100, 1_000, 10_000};
    static final int REPEATS = 7;
    static long sink;

    static double[] runOnce(Supplier<IntMap> factory, int[] keys) {
        IntMap map = factory.get();
        int n = keys.length;
        double[] r = new double[6];

        map.resetSteps();
        long t = System.nanoTime();
        for (int k : keys) map.insert(k, k);
        r[0] = (System.nanoTime() - t) / 1e6;
        r[3] = (double) map.steps() / n;

        map.resetSteps();
        t = System.nanoTime();
        for (int k : keys) {
            Integer v = map.search(k);
            sink += v == null ? 0 : v;
        }
        r[1] = (System.nanoTime() - t) / 1e6;
        r[4] = (double) map.steps() / n;

        map.resetSteps();
        t = System.nanoTime();
        for (int k : keys) if (map.delete(k)) sink++;
        r[2] = (System.nanoTime() - t) / 1e6;
        r[5] = (double) map.steps() / n;

        return r;
    }

    static double median(double[] values) {
        double[] copy = values.clone();
        Arrays.sort(copy);
        return copy[copy.length / 2];
    }

    static int[] makeKeys(int n, boolean sorted, Random rnd) {
        List<Integer> pool = new ArrayList<>();
        for (int i = 0; i < n * 10; i++) pool.add(i);
        Collections.shuffle(pool, rnd);
        int[] keys = new int[n];
        for (int i = 0; i < n; i++) keys[i] = pool.get(i);
        if (sorted) Arrays.sort(keys);
        return keys;
    }

    public static void main(String[] args) throws Exception {
        Random rnd = new Random(42);
        String[] names = {"BST", "Hash"};
        List<Supplier<IntMap>> factories = List.of(BST::new, HashTable::new);

        int[] warm = makeKeys(10_000, false, rnd);
        for (int i = 0; i < 5; i++) {
            runOnce(BST::new, warm);
            runOnce(HashTable::new, warm);
        }

        String header = "| data | n | structure | insert ms | search ms | delete ms | insert steps | search steps | delete steps |";
        String line = "|---|---|---|---|---|---|---|---|---|";
        System.out.println(header);
        System.out.println(line);

        try (PrintWriter csv = new PrintWriter("results.csv");
             PrintWriter md = new PrintWriter("results.md")) {

            csv.println("dataset,n,structure,insert_ms,search_ms,delete_ms,insert_steps,search_steps,delete_steps");
            md.println(header);
            md.println(line);

            for (String dataset : new String[]{"random", "sorted"}) {
                for (int n : SIZES) {
                    int[] keys = makeKeys(n, dataset.equals("sorted"), rnd);
                    for (int s = 0; s < 2; s++) {
                        double[][] runs = new double[6][REPEATS];
                        for (int rep = 0; rep < REPEATS; rep++) {
                            double[] res = runOnce(factories.get(s), keys);
                            for (int c = 0; c < 6; c++) runs[c][rep] = res[c];
                        }
                        double[] m = new double[6];
                        for (int c = 0; c < 6; c++) m[c] = median(runs[c]);

                        String row = String.format(Locale.US,
                                "| %s | %d | %s | %.3f | %.3f | %.3f | %.1f | %.1f | %.1f |",
                                dataset, n, names[s], m[0], m[1], m[2], m[3], m[4], m[5]);
                        System.out.println(row);
                        md.println(row);
                        csv.printf(Locale.US, "%s,%d,%s,%.3f,%.3f,%.3f,%.1f,%.1f,%.1f%n",
                                dataset, n, names[s], m[0], m[1], m[2], m[3], m[4], m[5]);
                    }
                }
            }

            for (String dataset : new String[]{"random", "sorted"}) {
                BST tree = new BST();
                for (int k : makeKeys(1000, dataset.equals("sorted"), rnd)) tree.insert(k, k);
                String h = "BST height, n=1000, " + dataset + ": " + tree.height();
                System.out.println(h);
                md.println();
                md.println(h);
            }
        }

        if (sink == 42) System.out.println();
    }
}