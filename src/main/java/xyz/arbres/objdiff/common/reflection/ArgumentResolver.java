package xyz.arbres.objdiff.common.reflection;

/**
 * @author bartosz walacik
 */
public interface ArgumentResolver {
    Object resolve(Class argType);
}
