package com.ithee.iluggage.core.database;

import com.ithee.iluggage.ILuggageApplication;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javafx.scene.control.Alert;

/**
 * Een helper-class die het eenvoudiger maakt om met een Database te werken.
 * Deze class is verantwoordelijk voor het aanmaken van de connecties, het
 * uitvoeren van meegegeven queries, en het vertalen van resultaten naar Java
 * objecten.
 *
 * @author iThee
 */
public class DatabaseConnection {

    /**
     * De database driver classnaam die gebruikt word om te verbinden.
     */
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

    /**
     * De database connectie URL om verbinding te maken met de database.
     */
//    private static final String DB_URL = "jdbc:mysql://oege.ie.hva.nl/zvisserr026";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/iluggage";
    //private static final String DB_URL = "jdbc:mysql://192.168.178.26:3306/iluggage";

    /**
     * De gebruikersnaam waarmee SQL verbinding maakt met de database.
     */
//    private static final String DB_USER = "visserr026";
    private static final String DB_USER = "root";

    /**
     * De gebruikersnaam waarmee SQL verbinding maakt met de database.
     */
//    private static final String DB_PASS = "/Q1a5le$8agKUw";
    private static final String DB_PASS = "evil112";

    /**
     * Een referentie naar de applicatie.
     */
    private ILuggageApplication app;

    /**
     * De laatste error die onstaan is bij het uitvoeren van een query.
     */
    public String lastError = "";

    /**
     * Maakt een nieuwe instantie van de class die verbonden is aan de
     * applicatie.
     *
     * @param app Een referentie naar de applicatie.
     */
    public DatabaseConnection(ILuggageApplication app) {
        this.app = app;

        // Initialiseert de database (MySQL) driver.
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException ex) {
            ILuggageApplication.showSimpleMessage(Alert.AlertType.ERROR, "Driver niet gevonden",
                    "De MySQL-driver is niet gevonden. "
                    + "Indien dit bestand word geopend vanuit een ZIP-bestand, graag uitpakken.");
            throw new RuntimeException("MySQL database driver not found");
        }
    }

    /**
     * Initialiseert een connectie en geeft deze terug.
     *
     * @return De aangemaakte connectie.
     * @throws SQLException Indien SQL een error geeft, dan word deze gethrowd.
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    /**
     * Voert een lees-query uit.
     *
     * @param sql De query die uitgevoerd moet worden.
     * @return Een ResultSet met het resultaat van de query.
     */
    public ResultSet executeQuery(String sql) {
        Connection conn = null;
        ResultSet result = null;
        try {
            // Vraag een nieuwe connectie aan
            conn = getConnection();

            // Initialiseert een statement
            PreparedStatement statement = conn.prepareStatement(sql);

            // Voert de query uit
            result = statement.executeQuery();
        } catch (SQLException ex) {
            // Indien er een error ontstaat, geef deze weer in de console en sla
            // deze foutmelding op.
            System.out.println(lastError = ex.getMessage());

            // Sluit de verbinding indien deze nog open staat.
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ignored) {
            }
        }

        return result;
    }

    /**
     * Voert een lees-query uit met de meegegeven parameters.
     *
     * @param sql De query die uitgevoerd moet worden.
     * @param params De parameters die in de query vervangen moeten worden
     * @return Een ResultSet met het resultaat van de query.
     */
    public ResultSet executeQuery(String sql, Object... params) {
        Connection conn = null;
        ResultSet result = null;
        try {
            // Vraag een nieuwe connectie aan
            conn = getConnection();

            // Initialiseert een statement
            PreparedStatement statement = conn.prepareStatement(sql);

            // Verwerk de parameters in de statement
            addParams(statement, params);

            // Voert de query uit
            result = statement.executeQuery();
        } catch (SQLException ex) {
            // Indien er een error ontstaat, geef deze weer in de console en sla
            // deze foutmelding op.
            System.out.println(lastError = ex.getMessage());

            // Sluit de verbinding indien deze nog open staat.
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ignored) {
            }
        }

        return result;
    }

    /**
     * Voert een insert, delete of update query uit.
     *
     * @param sql De query die uitgevoerd moet worden.
     * @param params De parameters die in de query verwerkt moeten worden.
     * @return Een aangemaakte key, of -1 indien er niets word aangemaakt.
     */
    public int executeStatement(String sql, Object... params) {
        Connection conn = null;
        ResultSet rs = null;
        try {
            // Vraag een nieuwe connectie aan
            conn = getConnection();

            // Initialiseert een statement
            PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            // Verwerk de parameters in de statement
            addParams(statement, params);

            // Voert de query uit
            if (statement.executeUpdate() > 0) {
                // Indien de query een key heeft aangemaakt, geef deze terug
                rs = statement.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            // Indien er een error ontstaat, geef deze weer in de console en sla
            // deze foutmelding op.
            System.out.println(lastError = ex.getMessage());
        } finally {
            // Sluit de verbinding indien deze nog open staat.
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ignored) {
            }
        }

        return -1;
    }

    /**
     * Voert een insert, delete of update query uit met de meegegeven
     * parameters.
     *
     * @param sql De query die uitgevoerd moet worden.
     * @param params Een meegegeven functie die de statement vult met
     * parameters.
     * @return Een aangemaakte key, of -1 indien er niets word aangemaakt.
     */
    public int executeStatement(String sql, Consumer<StatementHelper> params) {
        Connection conn = null;
        ResultSet rs = null;
        try {
            // Vraag een nieuwe connectie aan
            conn = getConnection();

            // Initialiseert een statement
            PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            // Verwerk de parameters in de statement
            params.accept(new StatementHelper(statement));

            // Voert de query uit
            if (statement.executeUpdate() > 0) {
                // Indien de query een key heeft aangemaakt, geef deze terug.
                rs = statement.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            // Indien er een error ontstaat, geef deze weer in de console en sla
            // deze foutmelding op.
            System.out.println(lastError = ex.getMessage());
        } finally {
            // Sluit de verbinding indien deze nog open staat.
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ignored) {
            }
        }

        return -1;
    }

    /**
     * Verwerk een array van parameters in een PreparedStatement.
     *
     * @param statement De statement waar de parameters in moeten worden
     * verwerkt.
     * @param params Een lijst met parameters die moeten worden verwerk in de
     * statement.
     * @throws SQLException Indien er een SQL-error ontstaat, dan word deze
     * gethrowd.
     */
    private void addParams(PreparedStatement statement, Object[] params) throws SQLException {
        // Gaat alle parameters af
        for (int i = 0; i < params.length; i++) {
            // Indien de parameter null is, dan word deze overgeslagen.
            // Anders word de class van de parameter vergeleken met 
            // veelgebruikte classes en aan de hand daarvan de parameters gezet.
            if (params[i] == null) {
                continue;
            } else if (params[i].getClass().equals(Integer.class)) {
                statement.setInt(i + 1, (int) params[i]);
            } else if (params[i].getClass().equals(Double.class)) {
                statement.setDouble(i + 1, (double) params[i]);
            } else if (params[i].getClass().equals(Long.class)) {
                statement.setLong(i + 1, (long) params[i]);
            } else if (params[i].getClass().equals(Boolean.class)) {
                statement.setBoolean(i + 1, (boolean) params[i]);
            } else if (params[i].getClass().equals(java.util.Date.class)) {
                statement.setTimestamp(i + 1, new java.sql.Timestamp(((java.util.Date) params[i]).getTime()));
            } else {
                statement.setString(i + 1, params[i].toString());
            }
        }
    }

    /**
     * Voert een lees-query uit, en vertaalt het resultaat naar een enkel
     * object.
     *
     * @param <T> Het type object dat gelezen moet worden.
     * @param c De class van het object dat gelezen moet worden.
     * @param sql De query die uitgevoerd moet worden.
     * @return Een object van het meegegeven class dat gelezen is uit de
     * resultaten.
     */
    public <T> T executeAndReadSingle(Class<T> c, String sql) {
        // Voert de lees-query uit en leest de resultaten hiervan door middel 
        // van een andere functie.
        return executeAndReadSingle(c, executeQuery(sql));
    }

    /**
     * Voert een lees-query uit met de meegegeven parameters, en vertaalt het
     * resultaat naar een enkel object.
     *
     * @param <T> Het type object dat gelezen moet worden.
     * @param c De class van het object dat gelezen moet worden.
     * @param sql De query die uitgevoerd moet worden.
     * @param params De parameters die in de query verwerkt moeten worden.
     * @return Een object van het meegegeven class dat gelezen is uit de
     * resultaten.
     */
    public <T> T executeAndReadSingle(Class<T> c, String sql, Object... params) {
        // Voert de lees-query uit met de meegegeven parameters en leest de 
        // resultaten hiervan door middel van een andere functie.
        return executeAndReadSingle(c, executeQuery(sql, params));
    }

    /**
     * Verwerkt de query resultaten, en vertaalt dit resultaat naar een enkel
     * object.
     *
     * @param <T> Het type object dat gelezen moet worden.
     * @param c De class van het object dat gelezen moet worden.
     * @param rs De query-resultaten waarvan gelezen moet worden.
     * @return Een object van het meegegeven class dat gelezen is uit de
     * resultaten.
     */
    private <T> T executeAndReadSingle(Class<T> c, ResultSet rs) {
        // Om herhaling te voorkomen, leest deze een lijst van objecten door 
        // middel van een andere functie, en pakt hierbij alleen het eerste
        // resultaat.
        List<T> list = executeAndReadList(c, rs);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    /**
     * Voert een lees-query uit, en vertaalt het resultaat naar een lijst van
     * object.
     *
     * @param <T> Het type object dat gelezen moet worden.
     * @param c De class van het object dat gelezen moet worden.
     * @param sql De query die uitgevoerd moet worden.
     * @return Een lijst met objecten van het meegegeven class dat gelezen is
     * uit de resultaten.
     */
    public <T> List<T> executeAndReadList(Class<T> c, String sql) {
        // Voert de lees-query uit en leest de resultaten hiervan door middel 
        // van een andere functie.
        return executeAndReadList(c, executeQuery(sql));
    }

    /**
     * Voert een lees-query uit met de meegegeven parameters, en vertaalt het
     * resultaat naar een lijst van object.
     *
     * @param <T> Het type object dat gelezen moet worden.
     * @param c De class van het object dat gelezen moet worden.
     * @param sql De query die uitgevoerd moet worden.
     * @param params De parameters die in de query verwerkt moeten worden.
     * @return Een lijst met objecten van het meegegeven class dat gelezen is
     * uit de resultaten.
     */
    public <T> List<T> executeAndReadList(Class<T> c, String sql, Object... params) {
        // Voert de lees-query uit met de meegegeven parameters en leest de 
        // resultaten hiervan door middel van een andere functie.
        return executeAndReadList(c, executeQuery(sql, params));
    }

    /**
     * Verwerkt de query resultaten, en vertaalt dit resultaat naar een lijst
     * met objecten
     *
     * @param <T> Het type object dat gelezen moet worden.
     * @param c De class van het object dat gelezen moet worden.
     * @param rs De query-resultaten waarvan gelezen moet worden.
     * @return Een lijst met objecten van het meegegeven class dat gelezen is
     * uit de resultaten.
     */
    private <T> List<T> executeAndReadList(Class<T> c, ResultSet rs) {
        // Maak een lege lijst aan voor de objecten.
        List<T> result = new ArrayList<>();

        // Indien het gelezen resultaat null is, geef dan enkel de lege lijst 
        // mee terug.
        if (rs == null) {
            return result;
        }

        try {
            // Blijft de resultaten lezen zolang er meerdere rows zijn om te 
            // lezen.
            while (rs.next()) {
                try {
                    // Maakt een nieuw object aan van de meegegeven class, en
                    // loopt over alle fields heen in de class dmv. reflectie.
                    T obj = c.newInstance();
                    for (Field f : c.getFields()) {
                        // Pakt de naam van een veld, en maakt de eerste letter 
                        // een hoofdletter om aan standaarden te houden.
                        String fieldName = f.getName().substring(0, 1).toUpperCase() + f.getName().substring(1);

                        // Leest, aan de hand van het type van het veld, de 
                        // waarde uit de query resultaten.
                        if (f.getType().equals(String.class)) {
                            f.set(obj, rs.getString(fieldName));
                        } else if (f.getType().equals(int.class)) {
                            f.setInt(obj, rs.getInt(fieldName));
                        } else if (f.getType().equals(Integer.class)) {
                            f.set(obj, rs.getInt(fieldName) == 0 ? null : rs.getInt(fieldName));
                        } else if (f.getType().equals(double.class)) {
                            f.setDouble(obj, rs.getDouble(fieldName));
                        } else if (f.getType().equals(Double.class)) {
                            f.set(obj, rs.getDouble(fieldName) == 0 ? null : rs.getDouble(fieldName));
                        } else if (f.getType().equals(boolean.class)) {
                            f.setBoolean(obj, rs.getBoolean(fieldName));
                        } else if (f.getType().equals(java.util.Date.class)) {
                            f.set(obj, new java.util.Date(rs.getTimestamp(fieldName).getTime()));
                        }
                    }

                    result.add(obj);
                } catch (Exception e) {
                    // Indien er een exception hier ontstaat, dan is dit een 
                    // programmeerfout, en zou niet in productieontgeving 
                    // zomaar ontstaan.
                    throw new RuntimeException(e);
                }
            }
        } catch (SQLException ex) {
            // Indien er een error ontstaat, geef deze weer in de console en sla
            // deze foutmelding op.
            System.out.println(lastError = ex.getMessage());
        }

        return result;
    }
}
