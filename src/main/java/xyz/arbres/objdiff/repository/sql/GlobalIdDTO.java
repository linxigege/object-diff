package xyz.arbres.objdiff.repository.sql;

/**
 * @author bartosz walacik
 */
public abstract class GlobalIdDTO {
    public abstract String value();

    @Override
    public String toString() {
        return "Dto(" + value() + ")";
    }
}
