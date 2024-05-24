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
        File[] files = directory.listFiles();
        assert files != null;
        for ( File file : files ) {
            if ( file.isDirectory() ) {
                scanDirectory( file, packageName + "." + file.getName(), URLMappings );
            } else {
                scanFile( file, packageName, URLMappings );
            }
        }
    }

    public static void scanFile( File file, String packageName, HashMap<String, Mapping> URLMappings )
            throws Exception {
        if ( file.getName().endsWith( ".class" ) ) {
            String className = packageName + "." + file.getName().replace( ".class", "" );
            Class<?> clazz = Class.forName( className );

            if ( clazz.isAnnotationPresent( Controller.class ) ) { // Verify if the class is a Controller
                for ( Method method : clazz.getDeclaredMethods() ) {
                    if ( method.isAnnotationPresent( GetMapping.class ) ) { // Verify if the method is a GetMapping

                        String url = method.getAnnotation( GetMapping.class ).urlMapping(),
                                methodName = method.getName();

                        // Verify if the URL is already in the HashMap
                        if ( URLMappings.containsKey( url ) ) {
                            Mapping mapping = URLMappings.get( url );
                            throw new Exception( "URL \"" + url + "\" already exists in the URLMappings.\n" +
                                    "Mapping1 -> {\n \tclass: " + mapping.getControllerName() + " \n \tmethod: " + mapping.getMethodName() + " \n}\n" +
                                    "Mapping2 -> {\n \tclass: " + className + " \n \tmethod: " + methodName + " \n}\n" );
                        }

                        URLMappings.put( url, new Mapping( className, methodName ) );
                    }
                }
            }
        }
    }
}
