package src.summer.utils;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.Objects;

import jakarta.servlet.ServletException;
import src.summer.beans.Mapping;
import src.summer.beans.ModelView;
import src.summer.annotations.Controller;
import src.summer.annotations.GetMapping;
import src.summer.exception.SummerInitException;

public abstract class ScannerUtil {
    public static HashMap<String, Mapping> scanControllers( String packageName )
            throws ServletException, ClassNotFoundException {
        HashMap<String, Mapping> URLMappings = new HashMap<>();
        File file = new File( getURLPackage( packageName ).getFile() );
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

    private static void scanFile( File file, String packageName, HashMap<String, Mapping> URLMappings )
            throws ClassNotFoundException, SummerInitException {
        if ( file.getName().endsWith( ".class" ) ) {
            String className = packageName + "." + file.getName().replace( ".class", "" );
            Class<?> clazz = Class.forName( className );

            if ( clazz.isAnnotationPresent( Controller.class ) ) { // Verify if the class is annotated with @Controller
                for ( Method method : clazz.getDeclaredMethods() ) {
                    if ( method.isAnnotationPresent( GetMapping.class ) ) { // Verify if the method is annotated with @GetMapping
                        String url = method.getAnnotation( GetMapping.class ).urlMapping(),
                                methodName = method.getName();

                        // Verify if the URL is already in the HashMap
                        if ( URLMappings.containsKey( url ) ) {
                            Mapping mapping = URLMappings.get( url );
                            throw new SummerInitException( "\nURL \"" + url + "\" already exists in the URLMappings.\n"
                                    + "Existing Mapping -> {\n \tclass: " + mapping.getControllerName() + " \n \tmethod: " + mapping.getMethod().getName() + " \n}\n"
                                    + "New Mapping -> {\n \tclass: " + className + " \n \tmethod: " + methodName + " \n}\n" );
                        }

                        // Verify the return type of the method
                        String returnTypeName = method.getReturnType().getName();
                        if ( returnTypeName.equals( String.class.getName() ) || returnTypeName.equals( ModelView.class.getName() ) ) {
                            URLMappings.put( url, new Mapping( className, method ) );
                        } else
                            throw new SummerInitException( "Unsupported return type \"" + returnTypeName + "\" for method \"" + className + "." + methodName + "()\"" );
                    }
                }
            }
        }
    }

    public static Class<?> getClass( String controllerName )
            throws ClassNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return classLoader.loadClass( controllerName );
    }
}
