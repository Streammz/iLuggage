
package com.ithee.iluggage.core.database;

import com.ithee.iluggage.ILuggageApplication;

/**
 *
 * @author iThee
 */
public abstract class AddableDatabaseCache<T> extends DatabaseCache<T>{

    protected final String updateQuery;
    
    public AddableDatabaseCache(ILuggageApplication app, String selectAllQuery, String updateQuery, Class<T> theClass) {
        super(app, selectAllQuery, theClass);
        this.updateQuery = updateQuery;
    }

    
    public final void addValue(T value) {
        Integer result = app.db.executeStatement(updateQuery, getDbParams(value));
        try {
            value.getClass().getField("id").set(value, result);
        } catch (NoSuchFieldException | SecurityException | IllegalAccessException ignored) {
        }
    }
    
    public abstract Object[] getDbParams(T obj);
}
