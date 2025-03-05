package src.summer.beans;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MappingVaovao {
    List<VerbAction> verbActionList;

    public MappingVaovao(VerbAction firstVa ) {

        this.setVerbActionList( new ArrayList<>() );
        this.getVerbActionList().add( firstVa );
    }

    public List<VerbAction> getVerbActionList() {
        return verbActionList;
    }

    public void setVerbActionList( List<VerbAction> verbActionList ) {
        this.verbActionList = verbActionList;
    }

    public VerbAction getVerbAction( String verb ) {
        for ( VerbAction verbAction : verbActionList ) {
            if ( verbAction.getVerb().equals( verb ) ) return verbAction;
        }
        return null;
    }

    public void addVerbAction( VerbAction verbAction ) {
        this.getVerbActionList().add( verbAction );
    }

    public Method getMethod( String verb ) {
        return this.getVerbAction( verb ).getAction();
    }
}
