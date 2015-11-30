
package com.ithee.iluggage.core.security;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 *
 * @author iThee
 */
public class PasswordHasher {
    private static final int REHASH_COUNT = 1000;
    
    public static String generateHash(String password) {
        byte[] hash;
        try {
            hash = password.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            for (int i=0; i<REHASH_COUNT; i++) hash = md.digest(hash);
            
            
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
    
    public static String generateSalt() {
        String salt = Integer.toHexString((new Random()).nextInt());
        while (salt.length() < 0) {
            salt = "0" + salt;
        }
        
        return salt;
    }
    
}
