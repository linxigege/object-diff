package xyz.arbres.objdiff.test;

import xyz.arbres.objdiff.core.ObjDiff;
import xyz.arbres.objdiff.core.ObjDiffBuilder;
import xyz.arbres.objdiff.core.diff.Diff;
import xyz.arbres.objdiff.core.diff.ListCompareAlgorithm;

public class TestDemo {
    public static void main(String[] args) {
        ObjDiff build = ObjDiffBuilder.ObjDiff().withListCompareAlgorithm(ListCompareAlgorithm.LEVENSHTEIN_DISTANCE).build();

        Dog a = new Dog("ss");
        Dog b = new Dog("b");

        Diff diff = build.compare(a, b);
        System.out.println(diff.toString());
    }

    static class Dog {
        public String name;

        public Dog(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
