package com.ithee.iluggage.core.database.classes;

/**
 *
 * @author iThee
 */
public class Customer {
    public int id;
    public String name;
    public String email;
    public String phone;
    public String address;
    public String housenumber;
    public String postalcode;
    public String addition;

    @Override
    public String toString() {
        String result = name;
        if (address != null) {
            result += " {" + address + ")";
        }
        return result;
    }
    
    
}
