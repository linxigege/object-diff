package xyz.arbres.objdiff.core.diff.appenders;



import xyz.arbres.objdiff.core.diff.NodePair;
import xyz.arbres.objdiff.core.diff.changetype.container.ListChange;
import xyz.arbres.objdiff.core.diff.changetype.container.SetChange;
import xyz.arbres.objdiff.core.metamodel.type.ListAsSetType;
import xyz.arbres.objdiff.core.metamodel.type.ObjDiffProperty;
import xyz.arbres.objdiff.core.metamodel.type.ObjDiffType;

import java.util.List;

/**
 * @author Sergey Kobyshev
 */
public class ListAsSetChangeAppender implements PropertyChangeAppender<ListChange> {

    private final SetChangeAppender setChangeAppender;

    ListAsSetChangeAppender(SetChangeAppender setChangeAppender) {
        this.setChangeAppender = setChangeAppender;
    }

    @Override
    public boolean supports(ObjDiffType propertyType) {
        return propertyType instanceof ListAsSetType;
    }

    @Override
    public ListChange calculateChanges(NodePair pair, ObjDiffProperty property) {
        SetChange setChange = setChangeAppender.calculateChanges(pair, property);

        if (setChange != null) {
            return new ListChange(pair.createPropertyChangeMetadata(property), setChange.getChanges(),
                    (List)pair.getLeftPropertyValueAndSanitize(property),
                    (List)pair.getRightPropertyValueAndSanitize(property));
        }
        return null;
    }

}
