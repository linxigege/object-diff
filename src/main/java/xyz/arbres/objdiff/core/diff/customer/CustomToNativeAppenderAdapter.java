package xyz.arbres.objdiff.core.diff.customer;


import xyz.arbres.objdiff.core.diff.NodePair;
import xyz.arbres.objdiff.core.diff.appenders.PropertyChangeAppender;
import xyz.arbres.objdiff.core.diff.changetype.PropertyChange;
import xyz.arbres.objdiff.core.metamodel.type.ObjDiffProperty;
import xyz.arbres.objdiff.core.metamodel.type.ObjDiffType;

/**
 * @author bartosz walacik
 */
public class CustomToNativeAppenderAdapter<T, C extends PropertyChange> implements PropertyChangeAppender<C> {
    private final CustomPropertyComparator<T, C> delegate;
    private final Class<T> propertyJavaClass;

    public CustomToNativeAppenderAdapter(CustomPropertyComparator<T, C> delegate, Class<T> propertyJavaClass) {
        this.delegate = delegate;
        this.propertyJavaClass = propertyJavaClass;
    }

    @Override
    public boolean supports(ObjDiffType propertyType) {
        return propertyType.getBaseJavaType().equals(propertyJavaClass);
    }

    @Override
    public C calculateChanges(NodePair pair, ObjDiffProperty property) {
        T leftValue = (T)pair.getLeftPropertyValue(property);
        T rightValue = (T)pair.getRightPropertyValue(property);

        return delegate.compare(leftValue, rightValue, pair.createPropertyChangeMetadata(property), property).orElse(null);
    }

    @Override
    public int priority() {
        return HIGH_PRIORITY;
    }
}
