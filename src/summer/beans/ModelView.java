package src.summer.beans;

import com.google.gson.Gson;

import java.util.HashMap;


public class ModelView {
    /**
     * Path to the view.
     */
    String url;
    /**
     * Data to dispatch the view.
     */
    HashMap<String, Object> data;

    public ModelView( String url ) {
        this.setUrl( url );
        this.setData( null );
    }

    public ModelView( String url, HashMap<String, Object> data ) {
        this.setUrl( url );
        this.setData( data );
    }

    // Getters n Setters
    public String getUrl() {
        return url;
    }

    public void setUrl( String url ) {
        this.url = url;
    }

    public HashMap<String, Object> getData() {
        return data;
    }

    public Object getObject( String key ) {
        return data.get(key);
    }

    public void setData( HashMap<String, Object> data ) {
        if ( data == null ) data = new HashMap<>();
        this.data = data;
    }

    public void addObject( String key, Object value ) {
        this.data.put( key, value );
    }

    public String getDataToJson() {
        return new Gson().toJson( this.getData() );
    }
}
