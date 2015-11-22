
package com.ithee.iluggage.core.database;

import com.ithee.iluggage.ILuggageApplication;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author robby
 */
public class DatabaseCache<T> {
    protected final ILuggageApplication app;
    protected final String selectAllQuery;
    
    protected List<T> cache;
    protected final Class<T> theClass;
    
    public DatabaseCache(ILuggageApplication app, String selectAllQuery, Class<T> theClass) {
        this.app = app;
        this.selectAllQuery = selectAllQuery;
        this.theClass = theClass;
    }
    
    public List<T> getValues() {
        if (this.cache == null) {
            loadValues();
        }
        
        return Collections.unmodifiableList(this.cache);
    }
    
    public T getValue(Predicate<T> pred) {
        for (T val : getValues()) {
            if (pred.test(val)) return val;
        }
        return null;
    }
    
    private void loadValues() {
        this.cache = app.db.executeAndReadList(theClass, selectAllQuery);
    }
    
    public void clear() {
        this.cache = null;
    }
}
