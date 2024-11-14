package src.summer.exception.scan.mapping;

import src.summer.beans.Mapping;
import src.summer.beans.VerbAction;

public class DuplicateMappingException extends SummerMappingException {
    private final String url, urlVerb;
    private final String newController, newAction;
    private final Mapping mapping;

    public DuplicateMappingException( String url, String urlVerb, Mapping mapping, String className, String methodName ) {
        this.url = url;
        this.urlVerb = urlVerb;
        this.mapping = mapping;
        this.newController = className;
        this.newAction = methodName;
    }

    @Override
    public String getMessage() {
        VerbAction oldVa = mapping.getVerbAction( urlVerb );
        String oldController = mapping.getControllerName(),
                oldAction = oldVa.getAction().getName(),
                oldVerb = oldVa.getVerb();

        String msg = "Duplicate URL: " + urlVerb + ":" + url + ".\n";
        String alt = "\tOld Mapping: " + oldController + "." + oldAction + "():" + oldVerb + "\n";
        String neu = "\tNew Mapping: " + newController + "." + newAction + "():" + urlVerb + "\n";

        return msg + alt + neu;
    }
}
