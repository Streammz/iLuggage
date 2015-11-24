
package com.ithee.iluggage.core.database.classes;

import com.ithee.iluggage.ILuggageApplication;

/**
 * @author iThee
 */
public class Luggage {
    public int id;
    public int customerId;
    public String flightCode;
    public int kind;
    public int brand;
    public int color;
    public String size;
    public boolean stickers;
    public String miscellaneous;
    public String date;
    public int status;
    
    
    public void setSize(double length, double width, double height) {
        this.size = length + "x" + width + "x" + height;
    }
    
    public double[] getSize() {
        if (size == null || "".equals(size)) {
            return null;
        }
        String[] splits = size.split("x");
        if (splits.length != 3) return null;
        
        double[] result = new double[3];
        for (int i=0; i<3; i++) {
            result[i] = Double.parseDouble(splits[i]);
        }
        
        return result;
    }
    
    public LuggageKind getKind(ILuggageApplication app) {
        return app.dbKinds.getValue((o) -> { return o.id == kind; });
    }
    
    public void setKind(LuggageKind b) {
        this.kind = b.id;
    }
    
    public void setKind(ILuggageApplication app, String name) {
        setKind(app.dbKinds.getValue((o) -> { return o.name.equals(name); }));
    }
    
    public LuggageBrand getBrand(ILuggageApplication app) {
        return app.dbBrands.getValue((o) -> { return o.id == brand; });
    }
    
    public void setBrand(LuggageBrand b) {
        this.brand = b.id;
    }
    
    public void setBrand(ILuggageApplication app, String name) {
        setBrand(app.dbBrands.getValue((o) -> { return o.name.equals(name); }));
    }
    
    public LuggageColor getColor(ILuggageApplication app) {
        return app.dbColors.getValue((o) -> { return o.id == color; });
    }
    
    public void setColor(LuggageColor b) {
        this.color = b.id;
    }
    
    public void setColor(ILuggageApplication app, String name) {
        setColor(app.dbColors.getValue((o) -> { return o.name.equals(name); }));
    }
}
