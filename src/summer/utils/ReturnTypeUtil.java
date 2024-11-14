package src.summer.utils;

import src.summer.beans.ModelView;
import src.summer.exception.scan.ReturnTypeException;

public abstract class ReturnTypeUtil {
    private static boolean isCorrectReturnType( String returnTypeName ) {
        return returnTypeName.equals( String.class.getName() ) || returnTypeName.equals( ModelView.class.getName() );
    }

    /**
     * Check the return type.
     *
     * @throws ReturnTypeException Whether the returnType is neither {@code String} nor {@code ModelView}
     */
    public static void verify( String returnTypeName, String className, String methodName )
            throws ReturnTypeException {
        if ( !isCorrectReturnType( returnTypeName ) ) {
            throw new ReturnTypeException( returnTypeName, className, methodName );
        }
    }
}
