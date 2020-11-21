package com.oudersamir.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oudersamir.SpringApplicationContext;
import com.oudersamir.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.oudersamir.entities.UserEntity;
import com.oudersamir.requests.UserRequest;

public class AuthenticationFilter  extends UsernamePasswordAuthenticationFilter{
	private  AuthenticationManager authenticationManager;


	public AuthenticationFilter(AuthenticationManager authenticationManager){
	this.authenticationManager=authenticationManager;
	}
	
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request,
			HttpServletResponse response) throws AuthenticationException {
		UserEntity user;	
		try {
			user= new ObjectMapper().readValue(request.getInputStream(),UserEntity.class);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
				user.getUserName(),
				user.getPassword()
				));
	}
	
	@Override
	protected void successfulAuthentication(HttpServletRequest request,
			HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		
		User user=(User)authResult.getPrincipal();
		List<String> roles=new ArrayList<String>();
		user.getAuthorities().forEach(role->{
			roles.add(role.getAuthority());
		});
		UserService userService =(UserService) SpringApplicationContext.getBean("userServiceImpl");

		UserEntity userEntity = userService.findByUserName(user.getUsername()).get();
		String jwt=JWT.create()
				.withIssuer(request.getRequestURI())
				.withSubject(user.getUsername())
				.withClaim("id",userEntity.getUserId())
				.withArrayClaim("roles",roles.toArray(new String[roles.size()]))
				.withExpiresAt(new Date(System.currentTimeMillis()+SecurityConstants.EXPIRATION_TIME))
				.sign(Algorithm.HMAC256(SecurityConstants.TOKEN_SECRET));
		response.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX+jwt);
		String rolesString=new ObjectMapper().writeValueAsString(roles);
		response.getWriter().write("{\"token\": \"" + jwt + "\"" +
				", \"id\": \""+ userEntity.getUserId() + "\"," +
				" \"roles\": "+ rolesString + "}");



	}
}
