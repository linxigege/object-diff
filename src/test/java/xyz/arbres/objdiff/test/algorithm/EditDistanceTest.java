package xyz.arbres.objdiff.test.algorithm;

/**
 * EditDistanceTest
 *
 * @author carlos
 * @date 2021-12-11
 */
public class EditDistanceTest {

    public static void main(String[] args) {
        System.out.println(getLevenshteinDistance("angle","angel"));
    }

    public static int getLevenshteinDistance(String source,String target){
        int m = source.length() , n = target.length() ,temp;
        if ( m == 0 ){
            return n ;
        }
        if ( n == 0 ){
            return m ;
        }
        Node[][] d = new Node[m+1][n+1];
        // init d[?][0]
        for (int i = 0 ; i< m+1 ; i ++){
            d[i][0]=new Node(i,Status.UNSET);
        }
        for (int j = 0 ; j< n+1 ; j ++){
            d[0][j]=new Node(j,Status.UNSET);
        }

        for( int i = 1 ; i < m+1  ; i ++ ){
            for ( int j = 1 ; j < n+1 ; j ++ ){
                temp = source.charAt(i-1) == target.charAt(j-1) ? 0 : 1;
                d[i][j] = getMinNode(d[i-1][j],d[i][j-1],d[i-1][j-1],temp);
            }
        }
        return d[m][n].val;
    }

   private static Node getMinNode(Node insertNode, Node removeNode, Node replaceNode, int temp){
        Node tempNode = new Node();
        if( insertNode.val > removeNode.val ){
            tempNode.val = removeNode.val+1;
            tempNode.status=Status.REMOVED;
        }else{
            tempNode.val = insertNode.val+1;
            tempNode.status=Status.INSERTED;
        }
        if( tempNode.val>replaceNode.val  ){
            tempNode.val = replaceNode.val+temp;
            if (temp == 1){
                tempNode.status = Status.REPLACED;
            }else{
                tempNode.status=Status.RETAINED;
            }

        }
        return tempNode;
    }



    static class Node {
        private int val;
        private Status status;

        private Node() {
        }

        public Node(int val, Status status) {
            this.val = val;
            this.status = status;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "val=" + val +
                    ", status=" + status +
                    '}';
        }
    }
    enum Status{
        UNSET,
        RETAINED,
        INSERTED,
        REMOVED,
        REPLACED;
    }
}
