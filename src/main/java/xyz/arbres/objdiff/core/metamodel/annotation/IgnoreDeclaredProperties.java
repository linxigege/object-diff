package xyz.arbres.objdiff.core.metamodel.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Use IgnoreDeclaredProperties annotation to mark
 * all properties <b>declared</b> in a given class as ignored by ObjDiff.
 * <br/><br/>
 * <p>
 * ObjDiff still tracks instances of a given class and tracks changes done on properties of
 * its superclass
 * (by contrast, if a class is annotated with {@link DiffIgnore}, ObjDiff completely ignores instances of
 * that class).
 * <p>
 * <br/><br/>
 * For example, when you want to ignore all properties declared in a subclass B but
 * still track changes in properties declared in a superclass A:
 * <pre>
 * class A {
 *     &#64;Id
 *     private Long id;
 *     private String name;
 * }
 * </pre>
 * <p>
 * this mapping:
 * <pre>
 * &#64;IgnoreDeclaredProperties
 * class B extends A {
 *     private String foo;
 *     private String bar;
 * }
 * </pre>
 * <p>
 * is equivalent to:
 * <pre>
 * class B extends A {
 *     &#64;DiffIgnore
 *     private String foo;
 *     &#64;DiffIgnore
 *     private String bar;
 * }
 * </pre>
 *
 * @author Edward Mallia
 * @see DiffIgnore
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface IgnoreDeclaredProperties {
}
