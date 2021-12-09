package xyz.arbres.objdiff.core.metamodel.scanner;

import java.util.Set;

interface AnnotationsNameSpace {

    Set<String> getEntityAliases();

    Set<String> getValueObjectAliases();

    Set<String> getValueAliases();

    Set<String> getTransientPropertyAliases();

    Set<String> getTypeNameAliases();

    Set<String> getPropertyNameAliases();
}