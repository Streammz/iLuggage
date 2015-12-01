package com.ithee.iluggage.core.database.classes;

import java.util.Date;

/**
 * Een account dat binnen de applicatie gebruikt word om mee in te loggen en
 * rechten te bepalen.
 *
 * @author iThee
 */
public class Account {

    public int id;
    public String username;
    public String password;
    public String salt;
    public String name;
    public String phone;
    public int permissionLevel;
    public Date lastLogin;
}
