package com.oudersamir.security;

import java.util.ArrayList;
import java.util.Collection;
import org.springframework.security.core.userdetails.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.oudersamir.entities.UserEntity;
import com.oudersamir.service.UserService;
@Service
public class UserDetailsServiceImpl  implements UserDetailsService{
	@Autowired
     UserService userService;
	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
			
		UserEntity user=userService.findByUserName(username).get();
		if(user==null)  throw new UsernameNotFoundException("User Not Found");
		Collection<GrantedAuthority>  authorities=new ArrayList<GrantedAuthority>();
		user.getRoles().forEach(role->{
			authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
		});
		return new User(user.getUserName(),user.getPassword(),authorities);
	}

}
