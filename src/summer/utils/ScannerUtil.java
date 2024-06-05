package src.summer.utils;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;

import src.summer.Mapping;
import src.summer.annotations.Controller;
import src.summer.annotations.GetMapping;

public class ScannerUtil {
    public static HashMap<String, Mapping> scanControllers( String packageName )
            throws Exception {
        HashMap<String, Mapping> URLMappings = new HashMap<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = classLoader.getResources( packageName.replace( ".", "/" ) );
        while ( resources.hasMoreElements() ) {
            URL resource = resources.nextElement();
            File file = new File( resource.getFile() );
            if ( file.isDirectory() ) {
                scanDirectory( file, packageName, URLMappings );
            } else {
                scanFile( file, packageName, URLMappings );
            }
        }
        return URLMappings;
    }

    public static void scanDirectory( File directory, String packageName, HashMap<String, Mapping> URLMappings )
            throws Exception {

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
                        String url = method.getAnnotation( GetMapping.class ).urlMapping(), methodName = method.getName();

                        // Verify if the URL is already in the HashMap
                        if ( URLMappings.containsKey( url ) ) {
                            Mapping mapping = URLMappings.get( url );
                            throw new SummerInitException( "\nURL \"" + url + "\" already exists in the URLMappings.\n"
                                    + "Existing Mapping -> {\n \tclass: " + mapping.getControllerName() + " \n \tmethod: " + mapping.getMethodName() + " \n}\n"
                                    + "New Mapping -> {\n \tclass: " + className + " \n \tmethod: " + methodName + " \n}\n" );
                        }

                        // Verify the return type of the method
                        String returnTypeName = method.getReturnType().getName();
                        if ( returnTypeName.equals( String.class.getName() ) || returnTypeName.equals( ModelView.class.getName() ) ) {
                            URLMappings.put( url, new Mapping( className, methodName ) );
                        } else
                            throw new SummerInitException( "Unsupported return type \"" + returnTypeName + "\" for method \"" + className + "." + methodName + "()\"" );
                    }
                }
            }
        }
    }

    public static Class<?> getControllerClass( String controllerName )
            throws ClassNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return classLoader.loadClass( controllerName );
    }
}
