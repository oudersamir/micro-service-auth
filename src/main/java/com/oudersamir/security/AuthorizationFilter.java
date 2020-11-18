package com.oudersamir.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;

public class AuthorizationFilter  extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest req,
			HttpServletResponse res, FilterChain filter)
			throws ServletException, IOException {
		

		res.addHeader("Access-Control-Allow-Origin", "*");
		res.addHeader("Access-Control-Allow-Headers", "Origin, Accept, "
				+ "X-Requested-With,Content-Type, Access-Control-Request-Method, Access-Control-RequestHeaders,authorization");
		res.addHeader("Access-Control-Expose-Headers", "Access-Control-Allow-Origin,"
				+ "Access-Control-Allow-Credentials, authorization");
		res.addHeader("Access-Control-Allow-Methods", "GET,POST,DELETE,PUT,PATCH");
		
		if(req.getMethod().equals("OPTIONS")){
		res.setStatus(HttpServletResponse.SC_OK);
		}else if(req.getRequestURI().equals(SecurityConstants.SIGN_UP_URL+"/login")){
			filter.doFilter(req, res);
			return ;
		}
		else {
			
			String jwt=req.getHeader(SecurityConstants.HEADER_STRING);
			
			if(jwt==null || !jwt.startsWith(SecurityConstants.TOKEN_PREFIX)){
                System.out.println(jwt);
				filter.doFilter(req, res);
				return;
			}
			DecodedJWT decodedJWT=JWT.decode(jwt.substring(SecurityConstants.TOKEN_PREFIX.length(), jwt.length()));
			String username=decodedJWT.getSubject();
			List<String> roles=decodedJWT.getClaims().get("roles").asList(String.class);
			Collection<GrantedAuthority> authorities=new ArrayList<GrantedAuthority> ();
			roles.forEach(role->{
				authorities.add(new SimpleGrantedAuthority(role));
			});
			UsernamePasswordAuthenticationToken user=new UsernamePasswordAuthenticationToken(username,null,authorities);
			SecurityContextHolder.getContext().setAuthentication(user);
			filter.doFilter(req, res);
			
		}
		
	}

}
