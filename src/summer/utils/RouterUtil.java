package src.summer.utils;

import java.util.Arrays;
import java.util.List;

public class RouterUtil {
    public static String getRoute(String url) {
        // Split url to a List<string> using "/" as a delimiter
        List<String> urlSegments = Arrays.asList(url.split("/"));
        // Remove the first element which is an empty string, and the second element which is the application name
        urlSegments = urlSegments.subList(2, urlSegments.size());
        // Join the remaining elements to get the route
        return String.join("/", urlSegments);
    }
}
