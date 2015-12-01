package com.ithee.iluggage.core.database.classes;

/**
 * Een bagagesoort dat binnen de applicatie gebruikt word.
 *
 * @author iThee
 */
public class LuggageKind {

    public int id;
    public String name;

    @Override
    public String toString() {
        return name;
    }

}
