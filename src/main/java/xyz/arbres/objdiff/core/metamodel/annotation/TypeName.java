package xyz.arbres.objdiff.core.metamodel.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Gives a persistent and human-readable <b>type name</b> for Entity or ValueObject.
 * This name is used everywhere by ObjDiff, instead of a fragile, fully-qualified class name.
 * <br/><br/>
 *
 * Naming types is <b>recommended</b> if you are
 * It fosters refactoring of package names and class names.
 * <br/><br/>
 *
 * Usage example:
 * <pre>
 *{@literal @}Entity
 *{@literal @}TypeName("Person")
 * class Person {
 *    {@literal @}Id
 *     private int id;
 *     private String name;
 * }
 * </pre>
 *
 * {@literal @}TypeName works similarly to <code>org.springframework.data.annotation.TypeAlias</code>
 * in Spring Data.
 *
 * <br/><br/>
 *
 * <b>Important</b><br/>
 * All classes with {@literal @}TypeName should be registered using {@link ObjDiffBuilder#withPackagesToScan(String)}<br/>
 * or <code>ObjDiff.packagesToScan</code> Spring Boot starter property.
 *
 * @see org.ObjDiff.core.metamodel.annotation.PropertyName
 * @see org.ObjDiff.core.metamodel.annotation.Entity
 * @see org.ObjDiff.core.metamodel.annotation.ValueObject
 * @since 1.4
 * @author bartosz.walacik
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface TypeName {
    /**
     * The type name to be used when comparing and persisting
     */
    String value();
}
