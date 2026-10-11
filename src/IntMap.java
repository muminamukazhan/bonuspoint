public interface IntMap {
    void insert(int key, int value);
    Integer search(int key);
    boolean delete(int key);
    int size();
    long steps();
    void resetSteps();
}