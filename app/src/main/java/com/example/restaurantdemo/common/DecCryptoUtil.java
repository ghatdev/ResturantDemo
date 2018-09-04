package com.example.restaurantdemo.common;

import android.util.Base64;

import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class DecCryptoUtil {
    private static final String DES_KEY = "myschoolkey12345";
    private static final SecureRandom secRandom = new SecureRandom();

    public DecCryptoUtil() {}

    private static Key getKey() throws Exception {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        DESKeySpec desKeySpec = new DESKeySpec( DES_KEY.getBytes() );
        return keyFactory.generateSecret( desKeySpec );
    }

    public static String ENCRYPT(String input) throws Exception {
        if(input == null || input.length() == 0) return "";
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init( Cipher.ENCRYPT_MODE, getKey(), secRandom);
        byte [] inputBytes = input.getBytes("UTF8");
        byte [] outputBytes = cipher.doFinal( inputBytes );

//        return new sun.misc.BASE64Encoder().encode( outputBytes );
        return Base64.encodeToString(outputBytes, Base64.DEFAULT);
    }

    public static String DECRYPT(String input) throws Exception {
        if(input == null || input.length() == 0) return "";
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init( Cipher.DECRYPT_MODE, getKey(), secRandom);
//        byte [] inputBytes = new sun.misc.BASE64Decoder().decodeBuffer( input );
        byte [] inputBytes = Base64.decode(input, Base64.DEFAULT);
        byte [] outputBytes = cipher.doFinal( inputBytes );
        return new String( outputBytes, "UTF8" );
    }

    public static void main(String[] ar) throws Exception {
        String str = "10";
        String enc = ENCRYPT(str);
        String dec = DECRYPT(enc);
        System.out.println(enc);
        System.out.println(dec);
    }
}
