
package com.ithee.iluggage.core.database;

import com.ithee.iluggage.ILuggageApplication;
import java.util.Collections;
import java.util.List;

/**
 * @author robby
 */
public class DatabaseCache<T> {
    private final ILuggageApplication app;
    private final String selectAllQuery;
    
    private List<T> cache;
    private final Class<T> theClass;
    
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
    
    private void loadValues() {
        this.cache = app.db.executeAndReadList(theClass, selectAllQuery);
    }
    
    public void clear() {
        this.cache = null;
    }
}
