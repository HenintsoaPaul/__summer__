package src.summer.beans;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Mapping {
    String controllerName;
    List<VerbAction> verbActionList;

//    TODO: Ajouter une liste verbe-aciton -- sprint10
//    TODO: Add class VerbAction. Ajouter la class comme atrb de Mapping

    public Mapping( String controllerName, VerbAction firstVa ) {
        this.setControllerName( controllerName );

        this.setVerbActionList( new ArrayList<>() );
        this.getVerbActionList().add( firstVa );
    }

    public String getControllerName() {
        return controllerName;
    }

    public void setControllerName( String controllerName ) {
        this.controllerName = controllerName;
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
