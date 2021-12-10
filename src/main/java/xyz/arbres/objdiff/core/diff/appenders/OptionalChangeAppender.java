package xyz.arbres.objdiff.core.diff.appenders;



import xyz.arbres.objdiff.common.exception.ObjDiffException;
import xyz.arbres.objdiff.common.exception.ObjDiffExceptionCode;
import xyz.arbres.objdiff.core.diff.NodePair;
import xyz.arbres.objdiff.core.diff.changetype.PropertyChange;
import xyz.arbres.objdiff.core.diff.changetype.ReferenceChange;
import xyz.arbres.objdiff.core.diff.changetype.map.ValueChangeFactory;
import xyz.arbres.objdiff.core.metamodel.object.GlobalId;
import xyz.arbres.objdiff.core.metamodel.type.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;


/**
 * @author bartosz.walacik
 */
public class OptionalChangeAppender implements PropertyChangeAppender<PropertyChange> {

    @Override
    public boolean supports(ObjDiffType propertyType) {
        return propertyType instanceof OptionalType;
    }

    @Override
    public PropertyChange calculateChanges(NodePair pair, ObjDiffProperty property) {
        OptionalType optionalType = property.getType();
        ObjDiffType contentType = optionalType.getItemObjDiffType();

        Optional leftOptional =  normalize((Optional) pair.getLeftDehydratedPropertyValueAndSanitize(property));
        Optional rightOptional = normalize((Optional) pair.getRightDehydratedPropertyValueAndSanitize(property));

        if (Objects.equals(leftOptional, rightOptional)) {
            return null;
        }
        if (contentType instanceof ManagedType) {
            return new ReferenceChange(pair.createPropertyChangeMetadata(property),
                    first(pair.getLeftReferences(property)),
                    first(pair.getRightReferences(property)),
                    flat(pair.getLeftPropertyValue(property)),
                    flat(pair.getRightPropertyValue(property)));
        }
        if (contentType instanceof PrimitiveOrValueType) {
            return ValueChangeFactory.create(pair, property, leftOptional, rightOptional);
        }

        throw new ObjDiffException(ObjDiffExceptionCode.UNSUPPORTED_OPTIONAL_CONTENT_TYPE, contentType);
    }

    private GlobalId first(List<GlobalId> refs){
        if (refs != null && refs.size() > 0) {
            return refs.get(0);
        }
        return null;
    }

    private Object flat(Object optional){
        if (optional instanceof Optional) {
            return ((Optional) optional).orElse(null);
        }
        return optional;
    }

    private Optional normalize(Optional optional) {
        if (optional == null) {
            return Optional.empty();
        }
        return optional;
    }
}
