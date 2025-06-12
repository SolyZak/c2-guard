package com.eden.eden_crm_sec_crm_back.utils.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;

public class JwtUtil {
    public static String getClaimValue(String token, String claimName) {
        DecodedJWT decodedJWT = JWT.decode(token);
        String issuer = decodedJWT.getIssuer();
        if (decodedJWT.getClaim(claimName).isNull()) {
            return null;
        }
        return decodedJWT.getClaim(claimName).asString();
    }
}
