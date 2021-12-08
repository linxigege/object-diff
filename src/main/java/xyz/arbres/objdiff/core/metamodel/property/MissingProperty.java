package xyz.arbres.objdiff.core.metamodel.property;

/**
 * MissingProperty
 *
 * @author carlos
 * @date 2021-12-07
 */
public class MissingProperty {
    public static final MissingProperty INSTANCE = new MissingProperty();
    private MissingProperty() {
    }

    @Override
    public String toString() {
        return "MISSING_PROPERTY";
    }
}
