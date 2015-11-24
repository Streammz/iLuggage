
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
 * @author iThee
 */
public class DatabaseConnection {
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
    private static final String DB_URL = "jdbc:mysql://oege.ie.hva.nl/zvisserr026";
    private static final String DB_USER = "visserr026";
    private static final String DB_PASS = "/Q1a5le$8agKUw";
    
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
            ex.printStackTrace();
            try {
                if (conn != null) conn.close();
            } catch (SQLException ignored) {
            }
        }
        
        return result;
    }
    
    public ResultSet executeQuery(String sql, Object... params) {
        Connection conn = null;
        ResultSet result = null;
        try {
            conn = getConnection();
            PreparedStatement statement = conn.prepareStatement(sql);
            
            addParams(statement, params);
            
            result = statement.executeQuery();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            try {
                if (conn != null) conn.close();
            } catch (SQLException ignored) {
            }
        }
        
        return result;
    }
    
    public boolean executeStatement(String sql, Object... params) {
        Connection conn = null;
        try {
            conn = getConnection();
            PreparedStatement statement = conn.prepareStatement(sql);
            
            addParams(statement, params);
            
            return statement.execute();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            try {
                if (conn != null) conn.close();
            } catch (SQLException ignored) {
            }
        }
        
        return false;
    }
    
    private void addParams(PreparedStatement statement, Object[] params) throws SQLException {
        for (int i=0; i<params.length; i++) {
            if (params[i] == null) continue;
            else if (params[i].getClass().equals(Integer.class)) statement.setInt(i+1, (int)params[i]);
            else if (params[i].getClass().equals(Double.class)) statement.setDouble(i+1, (double)params[i]);
            else if (params[i].getClass().equals(Long.class)) statement.setLong(i+1, (long)params[i]);
            else if (params[i].getClass().equals(Boolean.class)) statement.setBoolean(i+1, (boolean)params[i]);
            else if (params[i].getClass().equals(java.util.Date.class)) statement.setDate(i+1, new java.sql.Date(((java.util.Date)params[i]).getTime()));
            else statement.setString(i+1, params[i].toString());
        }
    }
    
    public <T> T executeAndReadSingle(Class<T> c, String sql) {
        return executeAndReadSingle(c, executeQuery(sql));
    }
    
    public <T> T executeAndReadSingle(Class<T> c, String sql, Object... params) {
        return executeAndReadSingle(c, executeQuery(sql, params));
    }
    
    private <T> T executeAndReadSingle(Class<T> c, ResultSet rs) {
        List<T> list = executeAndReadList(c, rs);
        if (list.isEmpty()) return null;
        return list.get(0);
    }
    
    public <T> List<T> executeAndReadList(Class<T> c, String sql) {
        return executeAndReadList(c, executeQuery(sql));
    }
    
    public <T> List<T> executeAndReadList(Class<T> c, String sql, Object... params) {
        return executeAndReadList(c, executeQuery(sql, params));
    }
    
    private <T> List<T> executeAndReadList(Class<T> c, ResultSet rs) {
        if (rs == null) {
            System.out.println("Trying to read from null (DatabaseConnection)");
            return null;
        }
        
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
