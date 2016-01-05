package com.ithee.iluggage.core.database;

import com.ithee.iluggage.ILuggageApplication;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * Deze class zorgt voor het cachen van specifieke objecten uit de database,
 * zodat deze niet meerdere keren opgehaald hoeft te worden.
 *
 * @author iThee
 */
public class DatabaseCache<T> {

    /**
     * Een referentie naar de applicatie.
     */
    protected final ILuggageApplication app;

    /**
     * Een query die moet worden uitgevoerd indien alle gegevens moeten worden
     * opgehaald uit de database.
     */
    protected final String selectAllQuery;

    /**
     * De huidige cache die gebruikt word.
     */
    protected List<T> cache;

    /**
     * De class dat gebruikt word om nieuwe objecten te initialiseren.
     */
    protected final Class<T> theClass;

    /**
     * Maakt een database cache aan met de meegegeven parameters. De gegevens
     * worden pas opgehaald zodra deze benodigd zijn.
     *
     * @param app Een referentie naar de applicatie.
     * @param selectAllQuery Een query die moet worden uitgevoerd indien alle
     * gegevens moeten worden opgehaald uit de database.
     * @param theClass De class van het object waarvoor de cache bestaat, zodat
     * de query gelijk naar objecten word omgezet.
     */
    public DatabaseCache(ILuggageApplication app, String selectAllQuery, Class<T> theClass) {
        this.app = app;
        this.selectAllQuery = selectAllQuery;
        this.theClass = theClass;
    }

    /**
     * Geeft de gecachte waardes terug. Indien deze nog niet gecached zijn, dan
     * worden deze waardes eerst opgehaald.
     *
     * @return Een lijst met de gecachte waardes.
     */
    public List<T> getValues() {
        if (this.cache == null) {
            loadValues();
        }

        // Geef een niet-aanpasbare lijst terug zodat niet met de cache 
        // gefrommeld word.
        return Collections.unmodifiableList(this.cache);
    }

    /**
     * Geeft een enkele waarde terug op basis van een filter-functie. Deze geeft
     * het eerste resultaat terug wat gevonden word, dus zonder specifieke
     * volgorde.
     *
     * @param pred De filter-functie waarmee de waarde er uit word gefiltert.
     * @return Het eerste gefilterde waarde.
     */
    public T getValue(Predicate<T> pred) {
        for (T val : getValues()) {
            if (pred.test(val)) {
                return val;
            }
        }
        return null;
    }

    /**
     * Laadt de waardes uit de database en stopt deze in de cache.
     */
    private void loadValues() {
        this.cache = app.db.executeAndReadList(theClass, selectAllQuery);
    }

    /**
     * Gooit de cache leeg, zodat deze later weer opnieuw word
     * opgehaald/ververst.
     */
    public void clear() {
        this.cache = null;
    }
}
