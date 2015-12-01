package com.ithee.iluggage.core.database.classes;

/**
 * Een klant dat binnen de applicatie gebruikt word om bagage aan te koppelen,
 * en gegevens zoals adres van klanten in op te slaan.
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

    /**
     * Geeft een string-representatie van dit object terug. Dit bevat de naam,
     * en optioneel de woonplaats van de klant.
     *
     * @return Een string-representatie van dit object.
     */
    @Override
    public String toString() {
        String result = name;
        if (address != null) {
            result += " {" + address + ")";
        }
        return result;
    }

}
