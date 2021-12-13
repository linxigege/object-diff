package xyz.arbres.objdiff.test.algorithm;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import java.util.*;

/**
 * EditDistanceTest
 *
 * @author carlos
 * @date 2021-12-11
 */
public class EditDistanceTest {


    @ParameterizedTest
    @CsvSource({
            "dlashfasf,dasdadsad,8",
            "angle,angel,2",
            "a,b,1",
            "aa,b,2",
            "a,bb,2"
    })
    void editDistanceTest(String source,String target,int expectedResult){
        Assertions.assertEquals(expectedResult,getLevenshteinDistance(source,target),()->
            source + " -> " + target + " need " + expectedResult + "steps"
        );
    }

    public static int getLevenshteinDistance(String source, String target) {
        int m = source.length(), n = target.length(), temp;
        if (m == 0) {
            return n;
        }
        if (n == 0) {
            return m;
        }

        Node[][] d = new Node[m + 1][n + 1];
        // init d[?][0]
        for (int i = 0; i < m + 1; i++) {
            d[i][0] = new Node(i, Status.UNSET);
        }
        for (int j = 0; j < n + 1; j++) {
            d[0][j] = new Node(j, Status.UNSET);
        }

        for (int i = 1; i < m + 1; i++) {
            for (int j = 1; j < n + 1; j++) {
                temp = source.charAt(i - 1) == target.charAt(j - 1) ? 0 : 1;
                d[i][j] = getMinNode(d[i][j - 1], d[i - 1][j], d[i - 1][j - 1], temp);
            }
        }

        buildNodeOptList(d, source, target, m, n);
        return d[m][n].weight;
    }

    private static void buildNodeOptList(Node[][] nodeTable, String source, String target, int sourceLen, int targetLen) {
        Deque<PettyPrintNode> pettyPrintNodeList = new LinkedList<>();
        String trace = target;
        char[] sourceChars = source.toCharArray();
        char[] targetChars = target.toCharArray();
        for (int i = sourceLen; i > 0; ) {
            for (int j = targetLen; j > 0; ) {
                PettyPrintNode pettyPrintNode = new PettyPrintNode();
                pettyPrintNode.setStatus(nodeTable[i][j].status);
                switch (nodeTable[i][j].status) {
                    case INSERTED:
                        pettyPrintNode.setNewChar(targetChars[j - 1]);
                        pettyPrintNode.setStepTarget(trace);
                        int traceLen = trace.length();
                        trace = trace.substring(0, j - 1) + trace.substring(j, traceLen);
                        pettyPrintNode.setStepSource(trace);
                        pettyPrintNodeList.push(pettyPrintNode);
                        j = j - 1;
                        break;
                    case REMOVED:
                        pettyPrintNode.setOldChar(sourceChars[i - 1]);
                        pettyPrintNode.setStepTarget(trace);
                        trace = trace + sourceChars[i - 1];
                        pettyPrintNode.setStepSource(trace);
                        pettyPrintNodeList.push(pettyPrintNode);
                        i = i - 1;
                        break;
                    case RETAINED:
                        i = i - 1;
                        j = j - 1;
                        break;
                    case REPLACED:
                        pettyPrintNode.setOldChar(sourceChars[i - 1]);
                        pettyPrintNode.setNewChar(targetChars[j - 1]);
                        pettyPrintNode.setStepTarget(trace);
                        trace = new StringBuilder(trace).replace(j - 1, j, String.valueOf(sourceChars[i - 1])).toString();
                        pettyPrintNode.setStepSource(trace);
                        pettyPrintNodeList.push(pettyPrintNode);
                        i = i - 1;
                        j = j - 1;
                        break;
                }
            }
        }
        while (pettyPrintNodeList.size() > 0) {
            PettyPrintNode pop = pettyPrintNodeList.pop();
            System.out.println(pop);
        }
    }


    private static Node getMinNode(Node insertNode, Node removeNode, Node replaceNode, int temp) {
        Node tempNode = new Node();
        if (insertNode.weight > removeNode.weight) {
            tempNode.weight = removeNode.weight + 1;
            tempNode.status = Status.REMOVED;
        } else {
            tempNode.weight = insertNode.weight + 1;
            tempNode.status = Status.INSERTED;
        }
        if (tempNode.weight > replaceNode.weight + 1) {
            tempNode.weight = replaceNode.weight + temp;
            if (temp == 1) {
                tempNode.status = Status.REPLACED;
            } else {
                tempNode.status = Status.RETAINED;
            }
        }
        return tempNode;
    }

    static class PettyPrintNode {

        private Character newChar;
        private Character oldChar;
        private String stepTarget;
        private String stepSource;
        private Status status;

        public void setNewChar(Character newChar) {
            this.newChar = newChar;
        }

        public void setOldChar(Character oldChar) {
            this.oldChar = oldChar;
        }

        public void setStepTarget(String stepTarget) {
            this.stepTarget = stepTarget;
        }

        public void setStepSource(String stepSource) {
            this.stepSource = stepSource;
        }

        public void setStatus(Status status) {
            this.status = status;
        }

        @Override
        public String toString() {
            String string = "";
            switch (status) {
                case INSERTED:
                    string = stepSource + " -> " + stepTarget + " ( " + status.getName() + newChar + " ) ";
                    break;
                case REMOVED:
                    string = stepSource + " -> " + stepTarget + " ( " + status.getName() + oldChar + " ) ";
                    break;
                case REPLACED:
                    string = stepSource + " -> " + stepTarget + " ( " + newChar + status.getName() + oldChar + " ) ";
                    break;
            }
            return string;
        }
    }

    static class Node {
        private int weight;
        private Status status;

        private Node() {
        }

        public Node(int val, Status status) {
            this.weight = val;
            this.status = status;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "val=" + weight +
                    ", status=" + status +
                    '}';
        }
    }

    enum Status {
        UNSET("未设置"),
        RETAINED("保留"),
        INSERTED("插入"),
        REMOVED("删除"),
        REPLACED("替换");

        private final String name;

        Status(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
