package control;

import model.ApiSchema;
import model.Usuario;
import java.security.Key;
import java.util.Date;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter; 
import io.jsonwebtoken.*;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;


public class TokensControl {
	
	ApiSchema apiKey = new ApiSchema();
	
	public TokensControl() {

	}

	public String criarToken(String issuer, String audience, long dateTimeOfExpiration,Usuario user) {

		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
		long dateTimeFromNowInMillis = System.currentTimeMillis();
		Date now = new Date(dateTimeFromNowInMillis);
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(apiKey.getSecret());
		Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

		JwtBuilder builder = Jwts.builder()
				.setIssuer(issuer)
				.setAudience(audience)
				.claim("Id_usuario", user.getidUsuario())
				.claim("Nome", user.getNome())
				.claim("Login", user.getLogin())
				.claim("UsuarioAdmin", user.getUsuarioAdmin())
				.signWith(signatureAlgorithm, signingKey);
			
		if(dateTimeOfExpiration >= 0 ) {
			long expirationInMillis = dateTimeFromNowInMillis + dateTimeFromNowInMillis;
			Date sumOfNowTimeWithExpirationTime = new Date(expirationInMillis );
			builder.setIssuedAt(now);
			builder.setExpiration(sumOfNowTimeWithExpirationTime);
		}
		return builder.compact();
	}
	
	public String parseTokenToString(String token) {

		try {
				Claims claims = Jwts.parser()         
		       .setSigningKey(DatatypeConverter.parseBase64Binary(apiKey.getSecret()))
		       .parseClaimsJws(token).getBody();
		    
				return "Issuer: " + claims.getIssuer()
					    +" Audience: " + claims.getAudience()
					    +" Id_usuario: " + claims.get("Id_usuario")
					    +" Nome: " + claims.get("Nome")
					    +" Login: " + claims.get("Login")
					    +" UsuarioAdmin: " + claims.get("UsuarioAdmin");

		}catch(JwtException exception) {
			return exception.getMessage();
		}
	    
	}
	
	public String getToken(long EXPIRATIONTIME,  Usuario user) {
		 
		return this.criarToken("Garten Automação","Webcalmedumi", EXPIRATIONTIME,user);
		
	}
}
