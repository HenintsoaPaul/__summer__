package src.summer.exception.scan;

public class ReturnTypeException extends SummerInitException {
    String returnTypeName, className, methodName;

    public ReturnTypeException( String returnTypeName, String className, String methodName ) {
        this.returnTypeName = returnTypeName;
        this.className = className;
        this.methodName = methodName;
    }

    @Override
    public String getMessage() {
        return "Unsupported return type \"" + returnTypeName + "\" for method \"" + className + "." + methodName + "()\"";
    }
}
