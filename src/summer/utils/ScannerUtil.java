package src.summer.utils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Objects;

import javax.servlet.ServletException;

import src.summer.annotations.controller.verb.Post;
import src.summer.beans.Mapping;
import src.summer.annotations.controller.Controller;
import src.summer.annotations.controller.UrlMapping;
import src.summer.beans.VerbAction;
import src.summer.exception.scan.file.EmptyDirectoryException;
import src.summer.exception.scan.ReturnTypeException;
import src.summer.exception.scan.file.EmptyPackageException;
import src.summer.exception.scan.file.PackageNotFoundException;
import src.summer.exception.scan.mapping.DuplicateMappingException;
import src.summer.exception.scan.mapping.NullVerbActionException;

public abstract class ScannerUtil {
    /**
     * Scan all controllers in the `packageName` folder.
     *
     * @param packageName Path to the folder containing the controllers.
     */
    public static HashMap<String, Mapping> scanControllers( String packageName )
            throws ClassNotFoundException, UnsupportedEncodingException, ServletException {
        HashMap<String, Mapping> URLMappings = new HashMap<>();
        String f = getURLPackage( packageName ).getFile();
        File file = new File( URLDecoder.decode( f, String.valueOf( StandardCharsets.UTF_8 ) ) );

        if ( file.isDirectory() ) {
            if ( Objects.requireNonNull( file.listFiles() ).length == 0 ) {
                throw new EmptyDirectoryException( file );
            }
            scanDirectory( file, packageName, URLMappings );
        } else scanFile( file, packageName, URLMappings );

        return URLMappings;
    }

    private static URL getURLPackage( String packageName )
            throws EmptyPackageException, PackageNotFoundException {
        // The user did not specify the controller package.
        if ( packageName == null || packageName.isEmpty() )
            throw new EmptyPackageException( packageName );

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource( packageName.replace( ".", "/" ) );

        if ( resource == null ) throw new PackageNotFoundException( packageName );
        return resource;
    }

    private static void scanDirectory( File directory, String packageName, HashMap<String, Mapping> URLMappings )
            throws ClassNotFoundException, ServletException {
        File[] files = directory.listFiles();
        assert files != null;
        for ( File file : files ) {
            if ( file.isDirectory() ) scanDirectory( file, packageName + "." + file.getName(), URLMappings );
            else scanFile( file, packageName, URLMappings );
        }
    }

    /**
     * Scan a file. If it is a java class file annotated with {@code @Controller}, we loop its methods.
     * We add methods annotated {@code @UrlMapping} to URLMapping hashMap.
     *
     * @throws DuplicateMappingException When there are two or more methods listing on the same URL.
     * @throws ReturnTypeException    When the return type of @GetMapping method is neither String nor ModelView.
     */
    private static void scanFile( File file, String packageName, HashMap<String, Mapping> URLMappings )
            throws ClassNotFoundException, ReturnTypeException, NullVerbActionException, DuplicateMappingException {
        if ( file.getName().endsWith( ".class" ) ) {
            String className = packageName + "." + file.getName().replace( ".class", "" );
            Class<?> clazz = Class.forName( className );

            if ( clazz.isAnnotationPresent( Controller.class ) ) { // Verify if the class is annotated with @Controller
                for ( Method method : clazz.getDeclaredMethods() ) {
                    scanMethod( method, className, URLMappings );
                }
            }
        }
    }

    /**
     * Scan a method.
     *
     * @throws DuplicateMappingException When there are two or more methods listing on the same URL for the same verb.
     * @throws ReturnTypeException    When the return type of @GetMapping method is neither String nor ModelView.
     */
    private static void scanMethod( Method method, String className, HashMap<String, Mapping> URLMappings )
            throws NullVerbActionException, ReturnTypeException, DuplicateMappingException {
        if ( method.isAnnotationPresent( UrlMapping.class ) ) { // Verify if the method is annotated with @GetMapping
            String url = method.getAnnotation( UrlMapping.class ).url(),
                    methodName = method.getName(),
                    urlVerb = getUrlVerb( method ),
                    returnTypeName = method.getReturnType().getName();

            ReturnTypeUtil.verify( returnTypeName, className, methodName );

            // If the URL is already in the HashMap -> if new verb, add new VerbAction; else, DuplicateException
            if ( URLMappings.containsKey( url ) ) {
                Mapping mapping = URLMappings.get( url );
                VerbAction verbAction = mapping.getVerbAction( urlVerb );

                if ( verbAction == null ) {
                    mapping.addVerbAction( new VerbAction( urlVerb, method ) );
                } else {
                    throw new DuplicateMappingException( url, urlVerb, mapping, className, methodName );
                }
            } else {
                // Create Mapping and add first VerbAction
                VerbAction va = new VerbAction( urlVerb, method );
                URLMappings.put( url, new Mapping( className, va ) );
            }
        }
    }

    /**
     * Get the urlVerb that is listened to by the method by looking for {@code controller.verb} annotations.
     *
     * @return the annotation's verb, or "GET" as default value for no annotation.
     */
    private static String getUrlVerb( Method method ) {
        String urlVerb = "GET";
        if ( method.isAnnotationPresent( Post.class ) ) urlVerb = "POST";
        return urlVerb;
    }

    /**
     * Look for a @Controller class named controllerName
     */
    public static Class<?> getClass( String controllerName )
            throws ClassNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return classLoader.loadClass( controllerName );
    }
}
