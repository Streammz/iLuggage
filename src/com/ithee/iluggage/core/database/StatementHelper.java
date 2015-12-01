package com.ithee.iluggage.core.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Een helper-class wat het eenvoudiger maakt om een PreparedStatement te vullen
 * met parameters die null kunnen zijn.
 *
 * @author iThee
 */
public class StatementHelper {

    /**
     * De statement waar aan de parameters moeten worden bijgevuld.
     */
    private final PreparedStatement statement;

    /**
     * De huidige parameter index. Deze start standaard bij 1, en moet bij elk
     * toegevoegde parameter achteraf worden verhoogd.
     */
    private int currentIndex = 1;

    /**
     * Maakt een StatementHelper aan die parameters vult aan de meegegeven
     * PreparedStatement.
     *
     * @param statement De statement waar aan de parameters moeten worden
     * bijgevuld.
     */
    public StatementHelper(PreparedStatement statement) {
        this.statement = statement;
    }

    /**
     * Voegt een String toe aan de PreparedStatement. Indien de String null is,
     * dan word de parameter gezet naar een NULL varchar.
     *
     * @param value De String die toegevoegd moet worden.
     * @return True indien de parameter is toegevoegd, anders false.
     */
    public boolean add(String value) {
        try {
            if (value == null || value.length() == 0) {
                statement.setNull(currentIndex, Types.VARCHAR);
            } else {
                statement.setString(currentIndex, value);
            }
            currentIndex++;
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }

    /**
     * Voegt een Integer toe aan de PreparedStatement. Indien de Integer null
     * is, dan word de parameter gezet naar een NULL integer.
     *
     * @param value De Integer die toegevoegd moet worden.
     * @return True indien de parameter is toegevoegd, anders false.
     */
    public boolean add(Integer value) {
        try {
            if (value == null) {
                statement.setNull(currentIndex, Types.INTEGER);
            } else {
                statement.setInt(currentIndex, value);
            }
            currentIndex++;
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }

    /**
     * Voegt een Boolean toe aan de PreparedStatement. Indien de Boolean null
     * is, dan word de parameter gezet naar een NULL boolean.
     *
     * @param value De Boolean die toegevoegd moet worden.
     * @return True indien de parameter is toegevoegd, anders false.
     */
    public boolean add(Boolean value) {
        try {
            if (value == null) {
                statement.setNull(currentIndex, Types.BOOLEAN);
            } else {
                statement.setBoolean(currentIndex, value);
            }
            currentIndex++;
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }

    /**
     * Voegt een Date toe aan de PreparedStatement. Indien de Date null is, dan
     * word de parameter gezet naar een NULL timestamp.
     *
     * @param value De Date die toegevoegd moet worden.
     * @return True indien de parameter is toegevoegd, anders false.
     */
    public boolean add(java.util.Date value) {
        try {
            if (value == null) {
                statement.setNull(currentIndex, Types.TIMESTAMP);
            } else {
                statement.setTimestamp(currentIndex, new java.sql.Timestamp(value.getTime()));
            }
            currentIndex++;
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }
}
