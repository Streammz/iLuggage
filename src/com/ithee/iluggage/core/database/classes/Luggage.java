package com.ithee.iluggage.core.database.classes;

import com.ithee.iluggage.ILuggageApplication;
import java.util.Date;

/**
 * Een bagagestuk dat binnen de applicatie gebruikt word.
 *
 * @author iThee
 */
public class Luggage {

    public int id;
    public Integer customerId;
    public String flightCode;
    public Integer kind;
    public Integer brand;
    public Integer color;
    public String size;
    public boolean stickers;
    public String miscellaneous;
    public Date date;
    public int status;

    /**
     * Veranderd het formaat van dit bagagestuk naar de meegegeven parameters.
     *
     * @param length De lengte van dit bagagestuk.
     * @param width De breedte van dit bagagestuk.
     * @param height De hoogte van dit bagagestuk.
     */
    public void setSize(double length, double width, double height) {
        this.size = (length == (int) length ? String.valueOf((int) length) : String.valueOf(length)) + "x"
                + (width == (int) width ? String.valueOf((int) width) : String.valueOf(width)) + "x"
                + (height == (int) height ? String.valueOf((int) height) : String.valueOf(height));
    }

    /**
     * Veranderd het formaat van dit bagagestuk naar de meegegeven parameters.
     *
     * @param sizes Een array met [lengte, breedte, hoogte] als formaat van dit
     * bagagestuk.
     */
    public void setSize(double[] sizes) {
        if (sizes == null) {
            this.size = null;
        } else {
            setSize(sizes[0], sizes[1], sizes[2]);
        }
    }

    /**
     * Geeft het formaat van dit bagagestuk terug als een Array met [lengte,
     * breedte, hoogte].
     *
     * @return Het formaat van dit bagagestuk.
     */
    public double[] getSize() {
        if (size == null || "".equals(size)) {
            return null;
        }
        String[] splits = size.split("x");
        if (splits.length != 3) {
            return null;
        }

        double[] result = new double[3];
        for (int i = 0; i < 3; i++) {
            result[i] = Double.parseDouble(splits[i]);
        }

        return result;
    }

    /**
     * Haalt het bagagesoort op uit de cache van de applicatie, en geeft dit
     * object terug.
     *
     * @param app Een referentie naar de applicatie.
     * @return Het bagagesoort.
     */
    public LuggageKind getKind(ILuggageApplication app) {
        if (kind == null) {
            return null;
        }
        return app.dbKinds.get(kind - 1);
    }

    /**
     * Veranderd het bagagesoort naar het meegegeven bagagesoort.
     *
     * @param b Het bagagesoort waar in dit veranderd moet worden.
     */
    public void setKind(LuggageKind b) {
        if (b == null) {
            this.kind = null;
        } else {
            this.kind = b.id;
        }
    }

    /**
     * Haalt het bagagemerk op uit de cache van de applicatie, en geeft dit
     * object terug.
     *
     * @param app Een referentie naar de applicatie.
     * @return Het bagagemerk.
     */
    public LuggageBrand getBrand(ILuggageApplication app) {
        if (brand == null) {
            return null;
        }
        return app.dbBrands.getValue((o) -> {
            return o.id == brand;
        });
    }

    /**
     * Veranderd het bagagemerk naar het meegegeven bagagemerk.
     *
     * @param b Het bagagemerk waar in dit veranderd moet worden.
     */
    public void setBrand(LuggageBrand b) {
        if (b == null) {
            this.brand = null;
        } else {
            this.brand = b.id;
        }
    }

    /**
     * Veranderd het bagagemerk naar het meegegeven bagagemerk. Deze haalt de
     * waarde op uit de cache, of voegt deze toe indien deze nog niet in de
     * database staat.
     *
     * @param app Een referentie naar de applicatie
     * @param name De naam van het bagagemerk.
     */
    public void setBrand(ILuggageApplication app, String name) {
        if (name == null || name.length() == 0) {
            this.brand = null;
            return;
        }
        LuggageBrand b = app.dbBrands.getValue((o) -> {
            return o.name.equals(name);
        });
        if (b == null) {
            b = new LuggageBrand();
            b.name = name;
            app.dbBrands.addValue(b);
        }
        setBrand(b);
    }

    /**
     * Haalt de kleur op uit de cache van de applicatie, en geeft dit object
     * terug.
     *
     * @param app Een referentie naar de applicatie.
     * @return De kleur.
     */
    public LuggageColor getColor(ILuggageApplication app) {
        if (color == null) {
            return null;
        }
        return app.dbColors.getValue((o) -> {
            return o.id == color;
        });
    }

    /**
     * Veranderd de kleur naar het meegegeven kleur.
     *
     * @param b De kleur waar in dit veranderd moet worden.
     */
    public void setColor(LuggageColor b) {
        if (b == null) {
            this.color = null;
        } else {
            this.color = b.id;
        }
    }

    /**
     * Veranderd de kleur naar de meegegeven kleur. Deze haalt de waarde op uit
     * de cache, of voegt deze toe indien deze nog niet in de database staat.
     *
     * @param app Een referentie naar de applicatie
     * @param name De naam van de kleur.
     */
    public void setColor(ILuggageApplication app, String name) {
        if (name == null || name.length() == 0) {
            this.color = null;
            return;
        }
        LuggageColor c = app.dbColors.getValue((o) -> {
            return o.name.equals(name);
        });
        if (c == null) {
            c = new LuggageColor();
            c.name = name;
            app.dbColors.addValue(c);
        }
        setColor(c);
    }
}
