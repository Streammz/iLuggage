package com.ithee.iluggage.core.database.classes;

/**
 * Een bagagemerk dat binnen de applicatie gebruikt word.
 *
 * @author iThee
 */
public class LuggageBrand {

    public int id;
    public String name;

    @Override
    public String toString() {
        return name;
    }

}
