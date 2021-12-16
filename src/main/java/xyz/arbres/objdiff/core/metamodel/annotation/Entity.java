package xyz.arbres.objdiff.core.metamodel.annotation;


import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author bartosz walacik
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface Entity {
}
