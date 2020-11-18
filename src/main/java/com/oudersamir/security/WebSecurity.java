package com.oudersamir.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {
	@Autowired
    UserDetailsService  userDetailsService;
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth)
			throws Exception {	
		auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
	}
	protected AuthenticationFilter getAuthenticationFilter() throws Exception{
		final AuthenticationFilter filter=new AuthenticationFilter(authenticationManager());
		filter.setFilterProcessesUrl(SecurityConstants.SIGN_UP_URL+"/login");
		return filter;
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
	http.csrf().disable();
    http.authorizeRequests().antMatchers(HttpMethod.GET,"/").permitAll();
    http.authorizeRequests().antMatchers(HttpMethod.GET,SecurityConstants.SIGN_UP_URL+"/users").hasAuthority("ADMIN");
    http.authorizeRequests().antMatchers(SecurityConstants.SIGN_UP_URL+"/login/**").permitAll();
    http.authorizeRequests().antMatchers(HttpMethod.POST,SecurityConstants.SIGN_UP_URL+"/register").permitAll();
    http.authorizeRequests().antMatchers(HttpMethod.PUT,SecurityConstants.SIGN_UP_URL+"/users").hasAnyRole("USER","ADMIN");
    http.authorizeRequests().anyRequest().authenticated();
    http.addFilter(new AuthenticationFilter(authenticationManager()))
	.addFilterBefore(new AuthorizationFilter(), UsernamePasswordAuthenticationFilter.class).
	sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);


	}
//	@Override
//    public void configure(org.springframework.security.config.annotation.web.builders.WebSecurity web) throws Exception {
//        web.ignoring().antMatchers("/**");
//    }
}
