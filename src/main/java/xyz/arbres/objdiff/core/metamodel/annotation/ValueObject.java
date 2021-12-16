package xyz.arbres.objdiff.core.metamodel.annotation;


import xyz.arbres.objdiff.core.metamodel.type.ValueObjectType;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Maps a user's class to {@link ValueObjectType}
 *
 * @author bartosz walacik
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface ValueObject {
}
