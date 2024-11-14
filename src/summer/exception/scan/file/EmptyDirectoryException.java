package src.summer.exception.scan.file;

import src.summer.exception.scan.SummerInitException;

import java.io.File;

public class EmptyDirectoryException extends SummerInitException {
    File directory;

    public EmptyDirectoryException( File directory ) {
        super();
        this.directory = directory;
    }

    @Override
    public String getMessage() {
        return "Directory \"" + directory.getName() + "\" is empty.";
    }
}
