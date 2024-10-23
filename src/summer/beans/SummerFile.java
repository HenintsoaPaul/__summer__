package src.summer.beans;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024, // 1 MB
        maxFileSize = 1024 * 1024 * 5,   // 5 MB
        maxRequestSize = 1024 * 1024 * 5 * 5 // 25 MB
)
public class SummerFile {

    String fileName;
    byte[] fileBytes;

    public SummerFile( String fileName, byte[] fileBytes ) {
        this.setFileName( fileName );
        this.setFileBytes( fileBytes );
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName( String fileName ) {
        if ( fileName == null ) throw new IllegalArgumentException( "File name is null" );
        if ( fileName.isEmpty() ) throw new IllegalArgumentException( "File name is empty" );
        this.fileName = fileName;
    }

    public byte[] getFileBytes() {
        return fileBytes;
    }

    public void setFileBytes( byte[] fileBytes ) {
        if ( fileBytes == null ) throw new IllegalArgumentException( "fileBytes is null" );
        if ( fileBytes.length == 0 ) throw new IllegalArgumentException( "fileBytes is empty" );
        this.fileBytes = fileBytes;
    }

    /**
     * Return a {@code SummerFile} object containing the name and the byte[]
     * of the file sent as {@code fileInpName} from a multipart/form-data.
     */
    public static SummerFile getFileFromRequest( HttpServletRequest request, String fileInpName )
            throws ServletException, IOException {
        SummerFile file = null;
        for ( Part part : request.getParts() ) {
            if ( part.getName().equals( fileInpName ) ) {
                try ( InputStream inputStream = part.getInputStream() ) {
                    byte[] bytes = new byte[ inputStream.available() ];
                    inputStream.read( bytes );
                    file = new SummerFile( fileInpName, bytes );
                }
            }
        }
        return file;
    }
}
