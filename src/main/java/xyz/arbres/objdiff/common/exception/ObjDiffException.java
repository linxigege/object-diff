package xyz.arbres.objdiff.common.exception;

import static xyz.arbres.objdiff.common.exception.ObjDiffExceptionCode.RUNTIME_EXCEPTION;

/**
 * ObjDiffException
 *
 * @author carlos
 * @date 2021-12-07
 */
@SuppressWarnings("serial")
public class ObjDiffException extends RuntimeException{
    public static final String BOOTSTRAP_ERROR = "ObjDiff bootstrap error - ";

    private final ObjDiffExceptionCode code;

    public ObjDiffException(Throwable throwable){
        super(String.format(RUNTIME_EXCEPTION.getMessage(),
                "Cause: " + throwable.getClass().getName() + " - " + throwable.getMessage()), throwable);
        this.code = RUNTIME_EXCEPTION;
    }

    public ObjDiffException(ObjDiffExceptionCode code,Object... arguments){
        super(code.name() + ": " + String.format(code.getMessage(), arguments));
        this.code = code;
    }

    public ObjDiffExceptionCode getCode(){
        return code;
    }

    @Override
    public String toString() {
        return "ObjDiffException{" +
                "code=" + code +
                '}';
    }
}
