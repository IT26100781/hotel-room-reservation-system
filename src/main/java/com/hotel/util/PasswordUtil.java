package com.hotel.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public final class PasswordUtil {
    private PasswordUtil() { }
    public static String hash(String password) {
        byte[] salt = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);

        try {
            byte[] hash = sha256(salt, password);
            String hashString = Base64.getEncoder().encodeToString(hash);
            String saltString = Base64.getEncoder().encodeToString(salt);
            return saltString +":"+ hashString;
        }catch(NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available", e);
        }

    }
    public static boolean matchesPassword(String password, String stored)  {
        if(stored == null || !stored.contains(":") || password == null){
            return false;
        }else{

            String[] saltHash = stored.split(":",2);
            try {
                byte[] saltStored = Base64.getDecoder().decode(saltHash[0]);
                byte[] hashStored = Base64.getDecoder().decode(saltHash[1]);
                byte[] hash = sha256(saltStored, password);
                return MessageDigest.isEqual(hash, hashStored);

            } catch (NoSuchAlgorithmException | IllegalArgumentException e) {
                return false;
            }
        }
    }
    private static byte[] sha256(byte[] salt, String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(salt);
        byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
        return hash;
    }
}
