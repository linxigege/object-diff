package xyz.arbres.objdiff.core.diff.appenders;



import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import xyz.arbres.objdiff.core.diff.NodePair;
import xyz.arbres.objdiff.core.diff.changetype.PropertyChange;
import xyz.arbres.objdiff.core.metamodel.type.ObjDiffProperty;
import xyz.arbres.objdiff.core.metamodel.type.ObjDiffType;

import java.lang.reflect.Type;


/**
 * @author bartosz walacik
 */
public abstract class CorePropertyChangeAppender<T extends PropertyChange> implements PropertyChangeAppender<T> {


    /**
     * ObjDiff needs to know actual Class of elements stored in your Collections and Maps. <br/>
     * Wildcards (e.g. Set&lt;?&gt;), unbounded type parameters (e.g. Set&lt;T&gt;) <br/>
     * or missing parameters (e.g. Set) are defaulted to Object.class.
     * <br/><br/>
     * For Collections of Values it's a reasonable guess <br/>
     * but for Collections of Entities or ValueObjects you should use fully parametrized types (e.g. Set&lt;Person&gt;).
     */
    public static final String GENERIC_TYPE_NOT_PARAMETRIZED = "GENERIC_TYPE_NOT_PARAMETRIZED";

    public static void renderNotParametrizedWarningIfNeeded(Type parameterType, String parameterName, String colType, ObjDiffProperty property){
        if (parameterType == ObjDiffType.DEFAULT_TYPE_PARAMETER){

        }
    }

    @Override
    final public T calculateChanges(NodePair pair, ObjDiffProperty property) {
        Object leftValue =  pair.getLeftDehydratedPropertyValueAndSanitize(property);
        Object rightValue = pair.getRightDehydratedPropertyValueAndSanitize(property);
        return calculateChanges(leftValue, rightValue, pair, property);
    }

    protected abstract T calculateChanges(Object leftValue, Object rightValue, NodePair pair, ObjDiffProperty property);
}
