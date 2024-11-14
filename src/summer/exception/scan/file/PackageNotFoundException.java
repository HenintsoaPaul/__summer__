package src.summer.exception.scan.file;

import src.summer.exception.scan.SummerInitException;

public class PackageNotFoundException extends SummerInitException {
    String packageName;

    public PackageNotFoundException( String packageName ) {
        super();
        this.packageName = packageName;
    }

    @Override
    public String getMessage() {
        return "Package \"" + packageName + "\" not found. Make sure it does exist.";
    }
}
