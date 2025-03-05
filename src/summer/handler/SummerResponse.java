package src.summer.handler;

import src.summer.beans.ModelView;

public class SummerResponse {

    private final ModelView modelView;

    /**
     * Can be a simple String or a JSON String
     */
    private final String stringResponse;

    /**
     * Formatted stringResponse that will be shown
     */
    private String formattedResponse;

    public SummerResponse(ModelView modelView, String stringResponse, String formattedResponse) {
        this.modelView = modelView;
        this.stringResponse = stringResponse;
        this.formattedResponse = formattedResponse;
    }

    // get
    public ModelView getModelView() {
        return modelView;
    }

    public String getStringResponse() {
        return stringResponse;
    }

    public String getFormattedResponse() {
        return formattedResponse;
    }
}
