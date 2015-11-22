
package com.ithee.iluggage.core.database;

import com.ithee.iluggage.ILuggageApplication;

/**
 *
 * @author robby
 */
public abstract class AddableDatabaseCache<T> extends DatabaseCache<T>{

    protected final String updateQuery;
    
    public AddableDatabaseCache(ILuggageApplication app, String selectAllQuery, String updateQuery, Class<T> theClass) {
        super(app, selectAllQuery, theClass);
        this.updateQuery = updateQuery;
    }

    
    public final void addValue(T value) {
        app.db.executeQuery(updateQuery, getDbParams(value));
    }
    
    public abstract Object[] getDbParams(T obj);
}
