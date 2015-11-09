
package com.ithee.iluggage.core.database;

import com.ithee.iluggage.ILuggageApplication;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author robby
 */
public class DatabaseConnection {
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
    private static final String DB_URL = "jdbc:mysql://localhost:3306/iLuggage";
    private static final String DB_USER = "root";
    private static final String DB_PASS = null;
    
    private ILuggageApplication app;
    
    public DatabaseConnection(ILuggageApplication app) {
        this.app = app;
        
        // Initialize the DB driver
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException("MySQL database driver not found");
        }
    }
    
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }
    
    public ResultSet executeQuery(String sql) {
        Connection conn = null;
        ResultSet result = null;
        try {
            conn = getConnection();
            PreparedStatement statement = conn.prepareStatement(sql);
            
            result = statement.executeQuery();
        } catch (SQLException ex) {
            try {
                if (conn != null) conn.close();
            } catch (SQLException ignored) {
            }
        }
        
        return result;
    }
    
    public <T> List<T> executeAndReadList(Class<T> c, String sql) {
        ResultSet rs = executeQuery(sql);
        List<T> result = new ArrayList<>();
        
        try {
            while (rs.next()) {
                try {
                    T obj = c.newInstance();
                    for (Field f : c.getFields()) {

                        String fieldName = f.getName().substring(0,1).toUpperCase() + f.getName().substring(1);
                        if (f.getType().equals(String.class)) {
                            f.set(obj, rs.getString(fieldName));
                        } else if (f.getType().equals(int.class)) {
                            f.setInt(obj, rs.getInt(fieldName));
                        } else if (f.getType().equals(double.class)) {
                            f.setDouble(obj, rs.getDouble(fieldName));
                        } else if (f.getType().equals(boolean.class)) {
                            f.setBoolean(obj, rs.getBoolean(fieldName));
                        }
                    }

                    result.add(obj);
                } catch (InstantiationException | IllegalAccessException | SecurityException | SQLException | IllegalArgumentException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        
        return result;
    }
}
