package org.example.authJwt.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;
    /*
   Extract username from the token
    */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);// the subject of the token  : email or user name of the user
    }
    /*
    Genreate token from userDetail

     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        return buildToken(claims, userDetails, jwtExpiration);
    }

   /*
   Generate refresh token
    */
   public String generateRefreshToken(
           UserDetails userDetails
   ) {
       return buildToken(new HashMap<>(), userDetails, refreshExpiration);
   }


    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                // setIssuAt() used to know when the clain was create and to calculate expiration and check if the token is valid or not
                .setIssuedAt(new Date(System.currentTimeMillis()))
                //
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }










    /*
    To validate the token
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }
    /*
        To know if the token exipred or not
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date()); // to make sure that the token we have is before the current date

    }
    /*
    To get the expiration date of the token
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /*
    Extract one claim from the token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    /*
    Extract all claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey()) //signin key : use to create the signature part of the jwt which is used to verify that the sender of the jwt
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    public void validateTokenn(String token) {
        Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token);
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }


}
