package malloc;

import java.util.ArrayList;


public class heap {
    private class Node{
        public int left;
        public int right;
        public boolean mark;
        public char[] ch;
        public Node(int left,int right,boolean mark,int n){
            //mark==true代表使用了该内存空间
            this.ch = new char[n];
            for (int i = 0; i < n; i++) {
                ch[i] = (char)i;
            }
            this.mark = mark;
            this.left = left;
            this.right = right;
        }
    }
    private int size;
    private ArrayList<Node> heapList;
    public heap(int n){
        //对内存进行初始化，并且申请一块大的空间
        this.size = n;
        this.heapList = new ArrayList<Node>();
        Node node =new  Node(0,n-1,false,0);
        heapList.add(node);
    }
    private static void fill(int l,int r){
        for(int i = 0;i < r-l+1;i++){

        }
    }
    public void heap_malloc(int one_size){
        boolean flag = true;
        if(this.size<one_size){
            System.out.println("内存不足不能申请新的空间");
        }
        for (int i = 0; i < heapList.size(); i++) {
            if(heapList.get(i).mark==false&&heapList.get(i).right-heapList.get(i).left>=one_size){
                flag = false;
                int l = heapList.get(i).left;
                int r = heapList.get(i).right;
                Node nodeAdd1 = new Node(l,l+one_size-1,true,one_size);
                Node nodeAdd2 = new Node(l+one_size,r,false,r-one_size);
                heapList.remove(i);
                heapList.add(i,nodeAdd1);
                heapList.add(i+1,nodeAdd2);
                break;
            }
        }
        if(flag){
            System.out.println("内存中碎片过多无法分配内存");
        }
    }

    public void free(int left){
        for (int i = 0; i < heapList.size(); i++) {
            if(heapList.get(i).left==left){
                free(left,heapList.get(i).right);
            }
        }
    }
    private void free(int left,int right){
        for (int i = 0; i < heapList.size(); i++) {
            if(heapList.get(i).left==left&&heapList.get(i).right==right){
                heapList.get(i).mark = false;//左右都没有空闲的情况
                if(i==0&&i+1<heapList.size()&&heapList.get(i+1).mark==false){
                    int l =left;
                    int r = heapList.get(i+1).right;
                    Node newNode = new Node(l,r,false,0);
                    heapList.remove(i);
                    heapList.remove(i);
                    heapList.add(i,newNode);
                }
                else if(i==heapList.size()-1&&i-1>=0&&heapList.get(i-1).mark==false){
                    //右边界并且左边空闲
                    int l = heapList.get(i-1).left;
                    int r = right;
                    Node newNode = new Node(l,r,false,0);
                    heapList.remove(i);
                    heapList.remove(i);
                    heapList.add(i,newNode);
                }
                 else if(i>0&&heapList.get(i-1).mark==false&&heapList.get(i+1).mark==true){
                    //左边有空闲，右边没有
                    int l = heapList.get(i-1).left;
                    int r = right;
                    Node newNode = new Node(l,r,false,0);
                    heapList.remove(i-1);
                    heapList.remove(i-1);
                    heapList.add(i-1,newNode);
                }
                else if(i>0&&heapList.get(i-1).mark==true&&heapList.get(i+1).mark==false){
                    int l = left;
                    int r = heapList.get(i+1).right;
                    Node newNode = new Node(l,r,false,0);
                    heapList.remove(i);
                    heapList.remove(i);
                    heapList.add(i,newNode);
                }
                else if(i>0&&i<heapList.size()-1&&heapList.get(i-1).mark==false&&heapList.get(i+1).mark==false){
                    System.out.println("ddd");
                    int l = heapList.get(i-1).left;
                    int r = heapList.get(i+1).right;
                    Node newNode = new Node(l,r,false,0);
                    heapList.remove(i-1);
                    heapList.remove(i-1);
                    heapList.remove(i-1);
                    heapList.add(i-1,newNode);
                }
            }
        }
    }

    public void show(){
        for (int i = 0; i < heapList.size(); i++) {
            System.out.println("内存段【"+heapList.get(i).left+","+heapList.get(i).right+"】"
            +"状态："+heapList.get(i).mark);
        }
    }

}

