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
import src.summer.beans.ModelView;
import src.summer.annotations.controller.Controller;
import src.summer.annotations.controller.UrlMapping;
import src.summer.beans.VerbAction;
import src.summer.exception.SummerInitException;
import src.summer.exception.SummerMappingException;

public abstract class ScannerUtil {
    /**
     * Scan all controllers in the `packageName` folder.
     *
     * @param packageName Path to the folder containing the controllers.
     */
    public static HashMap<String, Mapping> scanControllers( String packageName )
            throws ServletException, ClassNotFoundException, UnsupportedEncodingException {
        HashMap<String, Mapping> URLMappings = new HashMap<>();
        String f = getURLPackage( packageName ).getFile();
        File file = new File( URLDecoder.decode( f, String.valueOf( StandardCharsets.UTF_8 ) ) );

        if ( file.isDirectory() ) {
            if ( Objects.requireNonNull( file.listFiles() ).length == 0 ) { // The package is empty.
                throw new SummerInitException( "Directory \"" + file.getName() + "\" is empty." );
            }
            scanDirectory( file, packageName, URLMappings );
        } else scanFile( file, packageName, URLMappings );

        return URLMappings;
    }

    private static URL getURLPackage( String packageName )
            throws SummerInitException {
        if ( packageName == null || packageName.isEmpty() ) // The user did not specify the controller package.
            throw new SummerInitException( "packageName is null or empty. Please check your \"web.xml\" file." );

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource( packageName.replace( ".", "/" ) );

        if ( resource == null ) // The package does not exist.
            throw new SummerInitException( "Package \"" + packageName + "\" does not exist." );
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
     * Scan a file. If annotated with {@code @Controller}, we loop its methods. We add methods annotated
     * {@code @UrlMapping} to URLMapping hashMap.
     *
     * @throws SummerMappingException When there are two or more methods listing on the same URL.
     * @throws SummerInitException    When the return type of @GetMapping method is neither String nor ModelView.
     */
    private static void scanFile( File file, String packageName, HashMap<String, Mapping> URLMappings )
            throws ClassNotFoundException, SummerInitException {
        if ( file.getName().endsWith( ".class" ) ) {
            String className = packageName + "." + file.getName().replace( ".class", "" );
            Class<?> clazz = Class.forName( className );

            if ( clazz.isAnnotationPresent( Controller.class ) ) { // Verify if the class is annotated with @Controller
                for ( Method method : clazz.getDeclaredMethods() ) {
                    if ( method.isAnnotationPresent( UrlMapping.class ) ) { // Verify if the method is annotated with @GetMapping
                        String url = method.getAnnotation( UrlMapping.class ).url(),
                                methodName = method.getName(),
                                urlVerb = getUrlVerb( method ),
                                returnTypeName = method.getReturnType().getName();

                        handleReturnType( returnTypeName, className, methodName );

                        // If the URL is already in the HashMap -> if new verb, add new VerbAction; else, throw Exception
                        if ( URLMappings.containsKey( url ) ) {
                            Mapping mapping = URLMappings.get( url );
                            VerbAction verbAction = mapping.getVerbAction( urlVerb );

                            if ( verbAction == null ) {
                                mapping.addVerbAction( new VerbAction( urlVerb, method ) );
                            } else {
                                throw new SummerMappingException( "\nURL \"" + url + "\" already registered for the verb \"" + urlVerb + "\".\n"
                                        + "Existing Mapping -> {\n \tclass: " + mapping.getControllerName() + " \n \tmethod: " + verbAction.getAction().getName() + " \n \tverb:" + verbAction.getVerb() + " }\n"
                                        + "New Mapping -> {\n \tclass: " + className + " \n \tmethod: " + methodName + " \n \tverb: " + urlVerb + " }\n" );
                            }
                        } else {
                            // Create Mapping and add first VerbAction
                            VerbAction va = new VerbAction( urlVerb, method );
                            URLMappings.put( url, new Mapping( className, va ) );
                        }
                    }
                }
            }
        }
    }

    /**
     * returnType must be of type String or ModelView
     */
    private static boolean isCorrectReturnType( String returnTypeName ) {
        return returnTypeName.equals( String.class.getName() ) || returnTypeName.equals( ModelView.class.getName() );
    }

    /**
     * Verifier le type de retour. Si la verification echoue, on lance une exception.
     */
    private static void handleReturnType( String returnTypeName, String className, String methodName )
            throws SummerInitException {
        if ( !isCorrectReturnType( returnTypeName ) ) {
            throw new SummerInitException( "Unsupported return type \"" + returnTypeName + "\" for method \"" + className + "." + methodName + "()\"" );
        }
    }

    /**
     * Get whether it is POST or GET
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
