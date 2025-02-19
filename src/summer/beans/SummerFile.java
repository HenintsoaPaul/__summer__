package src.summer.beans;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

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
     * @param request Request contenant les champs du formulaire
     * @param fileInpName Nom du champ formulaire contenant le fichier que l'on envoi
     *
     * @return  a {@code SummerFile} object containing the name and the byte[]
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
                    file = new SummerFile( getSubmittedFileName(part), bytes );
                }
            }
        }
        return file;
    }

    private static String getSubmittedFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        if (contentDisp == null) {
            return null;
        }

        for (String content : contentDisp.split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(content.indexOf("=") + 1).trim().replace("\"", "");
            }
        }
        return null;
    }

    /**
     * Save the file bytes to a physical file in the given directory.
     *
     * @param directoryPath The path to the directory where the file should be saved.
     * @return The full path of the saved file.
     * @throws IOException If an error occurs during file writing.
     */
    public String saveToFile(String directoryPath) throws IOException {
        if (directoryPath == null || directoryPath.isEmpty()) {
            throw new IllegalArgumentException("Directory path cannot be null or empty.");
        }

        // Ensure directory exists
        Path dirPath = Paths.get(directoryPath);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        // Create file path
        Path filePath = dirPath.resolve(fileName);

        // Write bytes to file
        Files.write(filePath, fileBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        return filePath.toString();
    }
}
