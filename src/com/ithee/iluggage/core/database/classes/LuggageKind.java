package com.ithee.iluggage.core.database.classes;

import com.ithee.iluggage.ILuggageApplication;

/**
 * Een bagagesoort dat binnen de applicatie gebruikt word.
 *
 * @author iThee
 */
public class LuggageKind {

    public int id;
    public ILuggageApplication app;
    
    public LuggageKind(ILuggageApplication app, int id) {
        this.id = id;
        this.app = app;
    }

    @Override
    public String toString() {
        return app.getString("kind_" + id);
    }

}
