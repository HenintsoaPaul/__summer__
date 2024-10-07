package src.summer.beans;

import src.summer.exception.SummerMappingException;

import java.lang.reflect.Method;

public class VerbAction {
    String verb;
    Method action;

    public VerbAction( String verb, Method action )
            throws SummerMappingException {
        this.setVerb( verb );
        this.setAction( action );
    }

    public String getVerb() {
        return verb;
    }

    public void setVerb( String verb )
            throws SummerMappingException {
        if ( verb == null || verb.isEmpty() ) {
            throw new SummerMappingException( "Verb cannot be null or empty." );
        }
        this.verb = verb;
    }

    public Method getAction() {
        return action;
    }

    public void setAction( Method action )
            throws SummerMappingException {
        if ( action == null ) {
            throw new SummerMappingException( "Method cannot be null." );
        }
        this.action = action;
    }
}
