package src.summer.beans;

import src.summer.exception.scan.mapping.NullVerbActionException;

import java.lang.reflect.Method;

public class VerbAction {
    String verb;
    Method action;

    public VerbAction( String verb, Method action )
            throws NullVerbActionException {
        this.setVerb( verb );
        this.setAction( action );
    }

    public String getVerb() {
        return verb;
    }

    public void setVerb( String verb )
            throws NullVerbActionException {
        if ( verb == null || verb.isEmpty() ) {
            throw new NullVerbActionException( "Verb cannot be null or empty." );
        }
        this.verb = verb;
    }

    public Method getAction() {
        return action;
    }

    public void setAction( Method action )
            throws NullVerbActionException {
        if ( action == null ) {
            throw new NullVerbActionException( "Method cannot be null." );
        }
        this.action = action;
    }
}
