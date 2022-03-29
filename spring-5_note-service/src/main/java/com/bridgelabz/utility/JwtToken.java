package com.bridgelabz.utility;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Verification;

import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * this class is used to generate jwt token
 *
 * @author Shubham Verma
 */
@Component
public class JwtToken {

	Instant now = Instant.now();
	static String TOKEN_SECRET = "javaDemo123";

	public String generateToken(String emailId) {

		Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
		return JWT.create().withClaim("email", emailId).sign(algorithm);
	}

	public String decodeToken(String token) {
		String email;
		// for verification algorithm
		Verification verification = JWT.require(Algorithm.HMAC256(TOKEN_SECRET));
		JWTVerifier jwtverifier = verification.build();
		// to decode token
		DecodedJWT decodedjwt = jwtverifier.verify(token);
		// retrive data
		Claim claim = decodedjwt.getClaim("email");
		email = claim.asString();
		return email;
	}
}
