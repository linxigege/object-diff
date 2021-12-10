package xyz.arbres.objdiff.core.metamodel.scanner;


import xyz.arbres.objdiff.common.reflection.ObjDiffMember;
import xyz.arbres.objdiff.common.reflection.ReflectionUtil;

import java.util.List;

/**
 * @author pawel szymczyk
 */
class FieldBasedPropertyScanner extends PropertyScanner {

    FieldBasedPropertyScanner(AnnotationNamesProvider annotationNamesProvider) {
        super(annotationNamesProvider);
    }

    @Override
    List<ObjDiffMember> getMembers(Class<?> managedClass) {
        return (List) ReflectionUtil.getAllPersistentFields(managedClass);
    }
}
