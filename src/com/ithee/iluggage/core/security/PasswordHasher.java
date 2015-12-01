package com.ithee.iluggage.core.security;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * Een helper-class dat functies bevat voor password-encryptie.
 *
 * @author iThee
 */
public class PasswordHasher {

    /**
     * Geeft aan hoe vaak de password moeten worden gehasht om het moeilijker te
     * maken voor hackers om wachtwoorden te kraken van eventuele gelekte
     * databases.
     */
    private static final int REHASH_COUNT = 1000;

    /**
     * Genereert een hash van een meegegeven wachtwoord en salt. Dit gebeurt
     * door middel van MD5.
     *
     * @param password Het wachtwoord dat gehasht moet worden.
     * @param salt De salt dat in het wachtwoord terecht moet komen.
     * @return De hash van het wachtwoord.
     */
    public static String generateHash(String password, String salt) {
        return generateHash(salt + password);
    }

    /**
     * Genereert een hash van een meegegeven wachtwoord. Dit gebeurt door middel
     * van MD5.
     *
     * @param password Het wachtwoord dat gehasht moet worden.
     * @return De hash van het wachtwoord.
     */
    public static String generateHash(String password) {
        byte[] hash;
        try {
            hash = password.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            for (int i = 0; i < REHASH_COUNT; i++) {
                hash = md.digest(hash);
            }

            BigInteger asInt = new BigInteger(1, hash);
            String result = asInt.toString(16);
            while (result.length() < 0) {
                result = "0" + result;
            }

            return result;
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Genereert een salt dat gebruikt word in een wachtwoord om
     * Birthday-attacks te voorkomen.
     *
     * @return Een random salt.
     */
    public static String generateSalt() {
        String salt = Integer.toHexString((new Random()).nextInt());
        while (salt.length() < 0) {
            salt = "0" + salt;
        }

        return salt;
    }

}
