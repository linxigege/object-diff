package xyz.arbres.objdiff.common.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import static xyz.arbres.objdiff.common.reflection.ReflectionUtil.isNotStatic;

/**
 * ObjDiffFactory
 *
 * @author carlos
 * @date 2021-12-08
 */
public class ObjDiffFieldFactory {

    private final Class methodSource;

    public ObjDiffFieldFactory(Class methodSource) {
        this.methodSource = methodSource;
    }

    public List<ObjDiffField> getAllFields(){
        List<ObjDiffField> fields = new ArrayList<>();
        TypeResolvingContext context = new TypeResolvingContext();

        Class clazz = methodSource;
        while (clazz != null && clazz != Object.class)  {
            context.addTypeSubstitutions(clazz);

            for (Field f : clazz.getDeclaredFields()){
                if (isNotStatic(f)) {
                    fields.add(createJField(f, context));
                }
            }

            clazz = clazz.getSuperclass();
        }

        return fields;
    }

    private ObjDiffField createJField(Field rawField, TypeResolvingContext context){
        Type actualType = context.getSubstitution(rawField.getGenericType());
        return new ObjDiffField(rawField, actualType);
    }
}
