package malloc;

import java.util.ArrayList;
//false == unused
public class heapTree {
    private static  int calc(int n){

        int i = 0;
        while (true){
            if(n<=Math.pow(2,i)){
                return (int)Math.pow(2,i);
            }
            i++;
        }
    }
    private class Node{
        public int left;
        public int size;//每个内存块的大小
        public boolean mark;
        public int index;
        public int bro;//0代表是只有一个节点，-1是没有兄弟节点
        public Node(int left,int size,int index,boolean mark,int bro){
            //mark==true代表使用了该内存空间
//            this.startChar = startChar;
            this.mark = mark;
            this.index = index;
            this.left = left;
            this.size = size;
            this.bro = bro;
        }
    }


    public ArrayList<Node> list;
    private chars1 chars;

    public heapTree(int n){
        int flag = 0;//判断有没有超过
        this.chars = new chars1(n);
        this.list = new ArrayList<Node>();
        Node head = new Node(0,n,0,false,-1);
        list.add(head);
//        int k = 0;
//        Node node = head;
//        int len = list.size()-1;
//        while (node.used<n){
//            //树的形状可以考虑用队列实行层序遍历
//            HeapTree(node,k,0,len);
//            HeapTree(node,k+1,1,len+1);
//            k++;
//            len = list.size()-1;
//            node = list.get(len);
//            if(node.used+node.size*2>n){
//                break;
//            }
//        }

    }

    public void heap_malloc(int one_size){
        //分配函数和释放函数都要在执行后break；
       int need = this.calc(one_size);//实际需要的空间
        boolean flag = true;
        boolean flag1 = false;
        int k = -1;
        for (int i = 0; i < list.size(); i++) {
            if(need==list.get(i).size&&list.get(i).mark==false){
                flag = false;
                list.get(i).mark=true;
                this.chars.fill(list.get(i).left,need);
                return;
            }
            if(list.get(i).size>need&&list.get(i).mark==false){
                k = i;
                flag1 = true;
            }
        }
        if(flag&&flag1){
            //没有合适的,并且有更大的空间空闲
            divide(k,need);
            list.get(k).mark = true;
            this.chars.fill(list.get(k).left,need);
            return;
        }
        else {
            System.out.println("空间不足");
            return;
        }
    }
    private void divide(int k,int need){
        //将node分开
        int size = list.get(k).size;
        while (size!=need){
            Node node = list.get(k);
            if(node.bro!=-1){
               list.get(node.bro).bro = -1;
            }
            list.remove(k);
            Node node1 = new Node(node.left,size/2,k,false,k+1);
            Node node2 = new Node(node.left+size/2,size/2,k+1,false,k);
            list.add(k,node1);
            list.add(k+1,node2);

            size = size/2;
        }
    }

    private void merge(int i,int k){
        Node n1 = list.get(i);
        Node n2 = list.get(k);
        int b = -1;
        for (int j = 0; j < list.size(); j++) {
            if(n1.size*2==list.get(j).size){
                b = j;
                break;
            }
        }
        b--;
        if(n1.index>n2.index) {
            n1 = n2;
            i = k;
        }
        list.remove(i);
        list.remove(i);
        //应该用n1,n2改变

        if(b!=-2) {Node newNode = new Node(n1.left,n1.size*2,n1.index,false,b);
        list.add(n1.index,newNode);}
        else {
            Node newNode = new Node(n1.left,n1.size*2,n1.index,false,-1);
            list.add(n1.index,newNode);
        }
        if(b!=-2){
//            if(list.size())
            if(b!=-1&&n1.bro==b) merge(n1.index,b);
            else merge(0,n1.index);
        }
        else return;
    }
    public void free(int left){
        int size = 0;
        int n = 0;
        for (int i = 0; i < list.size(); i++) {
            if(left==list.get(i).left){
                n = list.get(i).index;
                list.get(i).mark = false;
                size = list.get(i).size;
                int k = list.get(i).bro;
                if(list.get(k).mark==false){
                    merge(i,k);
                    for(int l = n+1;l<list.size();l++) {
                        list.get(l).bro--;
                        list.get(l).index--;
                    }
                }
                break;
            }
        }

        chars.free(left,size);
    }

    public void show(){
        for (int i = 0; i < this.list.size(); i++) {
            System.out.println("["+list.get(i).left+"--"+(list.get(i).size+list.get(i).left-1)+"]"+list.get(i).bro+list.get(i).mark);
        }
        chars.show();
        System.out.println();
    }

}


class chars1{

    public static char[] ch;
    public chars1(int n){
        //初始化数组全为n
        this.ch = new char[n];
        for (int i = 0; i < n; i++) {
            ch[i] = 'n';
        }
    }
    public void fill(int n,int size){
        for (int i = n; i < n+size; i++) {
            ch[i] = 'i';
        }
    }
    public void free(int l,int size){
        for (int i = l; i < l+size; i++) {
            ch[i] = 'n';
        }
    }
    public static void show(){
        for (char c:ch) {
            System.out.print(c);
        }
    }
}