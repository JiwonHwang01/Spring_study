package com.example.security1.config.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.security1.model.User;
import com.example.security1.repository.UserRepository;

// /login 요청이 오면 자동으로 UserDetailService를 찾는다
// PrincipalDetailsService를 찾아서 바로 loadUserByUsername을 호출함
// (parameter에 있는 username은 loginForm에 uesrname과 맞춰줘야함)

@Service
public class PrincipalDetailsService implements UserDetailsService{

	@Autowired
	private UserRepository userReposirty;
	
	
	// return하면 시큐리티 session(내부 Authentication(내부 UserDetails)) 됨
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		User userEntity = userReposirty.findByUsername(username);
		if(userEntity != null) {
			return new PrincipalDetails(userEntity);
		}
		return null;
	}
	

}
