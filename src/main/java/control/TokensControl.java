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

	public String criarToken(String issuer, String audience, long timeToExpire, Usuario user) {

		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
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
	
		
		Date DataDeHoje = new Date();
		Date DataDeAmanha = new Date(DataDeHoje.getTime() + (timeToExpire));
		builder.setIssuedAt(DataDeHoje);
		builder.setExpiration(DataDeAmanha);

		return builder.compact();
	}
	
	public Usuario getUsuarioWithToken(String token) {

		try {
				Claims claims = Jwts.parser()         
		       .setSigningKey(DatatypeConverter.parseBase64Binary(apiKey.getSecret()))
		       .parseClaimsJws(token).getBody();
		    
				Usuario user = new Usuario()
				.setidUsuario(Long.parseLong(claims.get("Id_usuario").toString()))
				.setNome(claims.get("Nome").toString())
				.setLogin(claims.get("Login").toString())
				.setUsuarioAdmin(Integer.parseInt(claims.get("UsuarioAdmin").toString()));
					    
				 return user;
		}catch(JwtException exception) {
			return null;
		}
	    
	}
	
	public String getToken(long timeToExpire, Usuario user) {
		 
		return this.criarToken("Garten Automação","Webcalmedumi",timeToExpire, user);
		
	}

	public boolean validateToken(String Token) {
		
		try{
			@SuppressWarnings("unused")
			Jws<Claims> jws = Jwts.parser()
			.setSigningKey(apiKey.getSecret())
			.parseClaimsJws(Token);
			
			return true;

		}catch(JwtException exception){
			System.out.println(exception.getMessage());
			return false;
		}
	}
	
	
	
}
