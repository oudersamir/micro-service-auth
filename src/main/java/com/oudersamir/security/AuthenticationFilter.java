package com.oudersamir.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oudersamir.requests.UserRequest;

public class AuthenticationFilter  extends UsernamePasswordAuthenticationFilter{
	private  AuthenticationManager authenticationManager;
	public AuthenticationFilter(AuthenticationManager authenticationManager){
	this.authenticationManager=authenticationManager;
	}
	
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request,
			HttpServletResponse response) throws AuthenticationException {
		UserRequest userRequest;	
		try {
			userRequest= new ObjectMapper().readValue(request.getInputStream(),UserRequest.class);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
				userRequest.getUserName(),
				userRequest.getPassword()
				));
	}
	
	@Override
	protected void successfulAuthentication(HttpServletRequest request,
			HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		super.successfulAuthentication(request, response, chain, authResult);
		
		User user=(User)authResult.getPrincipal();
		List<String> roles=new ArrayList<String>();
		user.getAuthorities().forEach(role->{
			roles.add(role.getAuthority());
		});
		
		String jwt=JWT.create()
				.withIssuer(request.getRequestURI())
				.withSubject(user.getUsername())
				.withArrayClaim("roles",roles.toArray(new String[roles.size()]))
				.withExpiresAt(new Date(System.currentTimeMillis()+SecurityConstants.EXPIRATION_TIME))
				.sign(Algorithm.HMAC256(SecurityConstants.TOKEN_SECRET));
		
		response.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX+jwt);
		System.out.println(response.getHeader(SecurityConstants.HEADER_STRING));

	}
}
