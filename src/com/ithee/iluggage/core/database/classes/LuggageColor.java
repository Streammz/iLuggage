package com.ithee.iluggage.core.database.classes;

/**
 * Een kleur voor een bagagestuk dat binnen de applicatie gebruikt word.
 *
 * @author iThee
 */
public class LuggageColor {

    public int id;
    public String name;

    @Override
    public String toString() {
        return name;
    }

}
