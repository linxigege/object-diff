package xyz.arbres.objdiff.common.string;

import java.util.List;

/**
 * PetteryPrintBuilder
 *
 * @author carlos
 * @date 2021-12-07
 */
public class PrettyPrintBuilder {

    private StringBuilder out = new StringBuilder();

    public PrettyPrintBuilder(Object instance) {
        println(instance.getClass().getSimpleName() + "{");
    }

    public PrettyPrintBuilder addField(String fieldName, Object value) {
        println("  " + fieldName + ": " + value);
        return this;
    }

    public PrettyPrintBuilder addMultiField(String fieldName, List<?> values) {
        println("  " + fieldName + ":");
        for (Object v : values) {
            println("    " + v);
        }
        return this;
    }

    private void println(String text) {
        out.append(text + "\n");
    }

    private void print(String text) {
        out.append(text);
    }


    public String build() {
        print("}");
        return out.toString();
    }
}
