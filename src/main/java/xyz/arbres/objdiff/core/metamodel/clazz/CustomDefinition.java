package xyz.arbres.objdiff.core.metamodel.clazz;


import xyz.arbres.objdiff.common.validation.Validate;
import xyz.arbres.objdiff.core.diff.customer.CustomPropertyComparator;

/**
 *  Recipe for {@link CustomType}
 *
 * @author bartosz walacik
 */
public class CustomDefinition<T> extends ClientsClassDefinition {
    private CustomPropertyComparator<T, ?> comparator;

    public CustomDefinition(Class<T> clazz, CustomPropertyComparator<T, ?> comparator) {
        super(clazz);
        Validate.argumentIsNotNull(comparator);
        this.comparator = comparator;
    }

    public CustomPropertyComparator<T, ?> getComparator() {
        return comparator;
    }
}
