package de.gematik.octopussi.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.jose4j.jwt.JwtClaims;

@AllArgsConstructor
@Builder(toBuilder = true)
@Data
public class UserInformation {

    private String username;
    private String passwordHash;
    private Long id;

    public JwtClaims toClaims() {
        JwtClaims claims = new JwtClaims();
        claims.setClaim("name", username);
        claims.setClaim("userId", id);
        return claims;
    }
}
