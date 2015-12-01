package com.ithee.iluggage.core.database;

import com.ithee.iluggage.ILuggageApplication;

/**
 * Uitbreiding op de reguliere DatabaseCache, zodat er ook gegevens toegevoegd
 * kunnen worden aan de database. Deze class is abstract omdat er een functie
 * aan toegevoegd moet worden om de parameters uit een object te halen.
 *
 * @author iThee
 */
public abstract class AddableDatabaseCache<T> extends DatabaseCache<T> {

    /**
     * Een query die moet worden uitgevoerd indien er een item moet worden
     * toegevoegd aan de database.
     */
    protected final String updateQuery;

    /**
     * Maakt een database cache aan met de meegegeven parameters. De gegevens
     * worden pas opgehaald zodra deze benodigd zijn.
     *
     * @param app Een referentie naar de applicatie.
     * @param selectAllQuery Een query die moet worden uitgevoerd indien alle
     * gegevens moeten worden opgehaald uit de database.
     * @param updateQuery Een query die moet worden uitgevoerd indien er een
     * item moet worden toegevoegd aan de database.
     * @param theClass De class van het object waarvoor de cache bestaat, zodat
     * de query gelijk naar objecten word omgezet.
     */
    public AddableDatabaseCache(ILuggageApplication app, String selectAllQuery, String updateQuery, Class<T> theClass) {
        super(app, selectAllQuery, theClass);
        this.updateQuery = updateQuery;
    }

    /**
     * Voegt een waarde toe aan de cache en de database.
     *
     * @param value De waarde die toegevoegd moet worden.
     */
    public final void addValue(T value) {
        // Voegt de waarde toe aan de database.
        Integer result = app.db.executeStatement(updateQuery, getDbParams(value));

        // Voegt de waarde toe aan de cache.
        this.cache.add(value);

        // Probeert het "id" veld te zetten naar de aangemaakte ID, maar indien
        // dit niet mogelijk is, word dit simpelweg overgeslagen.
        try {
            value.getClass().getField("id").set(value, result);
        } catch (NoSuchFieldException | SecurityException | IllegalAccessException ignored) {
        }

    }

    /**
     * Functie die overschreven moet worden die een lijst met objecten meegeeft
     * om in de query te vullen. Vaak zijn dit meerdere velden uit het
     * meegegeven object.
     *
     * @param obj Het object dat toegevoegd moet worden in de database, om de
     * parameters uit te filteren.
     * @return Een object array met de gefilterde parameters.
     */
    public abstract Object[] getDbParams(T obj);
}
