
package com.ithee.iluggage.core.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 *
 * @author robby
 */
public class StatementHelper {
    
    private final PreparedStatement statement;
    private int currentIndex = 1;
    
    public StatementHelper(PreparedStatement statement) {
        this.statement = statement;
    }
    
    public boolean add(String value) {
        try {
            if (value == null || value.length() == 0) statement.setNull(currentIndex, Types.VARCHAR);
            else statement.setString(currentIndex, value);
            currentIndex++;
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }
    
    public boolean add(Integer value) {
        try {
            if (value == null) statement.setNull(currentIndex, Types.INTEGER);
            else statement.setInt(currentIndex, value);
            currentIndex++;
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }
    
    public boolean add(Boolean value) {
        try {
            if (value == null) statement.setNull(currentIndex, Types.BOOLEAN);
            else statement.setBoolean(currentIndex, value);
            currentIndex++;
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }
    
    public boolean add(java.util.Date value) {
        try {
            if (value == null) statement.setNull(currentIndex, Types.TIMESTAMP);
            else statement.setTimestamp(currentIndex, new java.sql.Timestamp(value.getTime()));
            currentIndex++;
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }
}
