package malloc;

/**
 * 测试方法
 * #define POOL_NUM 100
 * #define MIN_POOL_SIZE 10
 * #define MAX_POOL_SIZE 1000
 */
public class heap_test {
    public static void main(String[] args) {
        heap Heap = new heap(10);
        Heap.show();
        System.out.println("-----------------------");
        Heap.heap_malloc(2);
        Heap.show();
        System.out.println("-----------------------");
        Heap.heap_malloc(3);
        Heap.show();
        System.out.println("-----------------------");
        Heap.free(0);
        Heap.show();
        System.out.println("-----------------------");
        Heap.heap_malloc(4);
        Heap.show();
        System.out.println("-----------------------");
        Heap.free(5);
        Heap.show();
    }
}
