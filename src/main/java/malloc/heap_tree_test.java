package malloc;

public class heap_tree_test {
    public static void main(String[] args) {
        heapTree heapTree = new heapTree(8);
        heapTree.heap_malloc(2);
        heapTree.show();
        heapTree.heap_malloc(2);
        heapTree.heap_malloc(2);
        heapTree.heap_malloc(2);
        heapTree.free(2);
        heapTree.show();
        heapTree.free(4);
        heapTree.show();
        heapTree.free(0);
        heapTree.show();
        heapTree.free(6);
        heapTree.show();
    }

}
