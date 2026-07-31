package com.hospital_management_system.demo;

import io.jsonwebtoken.Jwts;


import javax.crypto.SecretKey;
import java.util.Base64;

public class KeyGenerator {

    public static void main(String[] args) {

        SecretKey key = Jwts.SIG.HS256.key().build();
        String base54Key = Base64.getEncoder()
                .encodeToString(key.getEncoded());

        System.out.println(base54Key);
    }
}
