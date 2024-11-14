package src.summer.exception.scan.file;

import src.summer.exception.scan.SummerInitException;

import java.io.File;

public class EmptyPackageException extends SummerInitException {
    String packageName;

    public EmptyPackageException( String packageName ) {
        super();
        this.packageName = packageName;
    }

    @Override
    public String getMessage() {
        return "Package \"" + packageName + "\" is null or empty. Please check your \"web.xml\" file.";
    }
}
