package src.summer.beans;

import src.summer.exception.SummerSessionException;
import java.util.HashMap;

public class SummerSession {
    private HashMap<String, Object> values = new HashMap<>();

    public SummerSession(HashMap<String, Object> values) {
        this.setAll(values);
    }

    // CREATE
    public void setAll( HashMap<String, Object> values ) {
        this.values = values;
    }

    public void set( String key, Object value )
            throws SummerSessionException {
        if ( this.values.containsKey( key ) )
            throw new SummerSessionException( "Cannot duplicate keys in the Session." );
        this.values.put( key, value );
    }

    // READ
    public HashMap<String, Object> getAll() {
        return this.values;
    }

    public Object get( String key )
            throws SummerSessionException {
        if ( !this.values.containsKey( key ) )
            throw new SummerSessionException( "Key \""+ key +"\" does not exist in Session." );
        return this.values.get( key );
    }

    // UPDATE
    public void update( String key, Object value ) throws SummerSessionException {
        if ( !this.values.containsKey( key ) )
            throw new SummerSessionException( "Key \""+ key +"\" does not exist in Session." );
        Object oldValue = this.values.get( key );

    }

    // DELETE
    public void delete( String key ) {
        this.values.remove( key );
    }
}
