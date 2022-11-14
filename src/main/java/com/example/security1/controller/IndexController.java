package com.example.security1.controller;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.security1.config.auth.PrincipalDetails;
import com.example.security1.model.User;
import com.example.security1.repository.UserRepository;

@Controller
public class IndexController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@GetMapping("/test/login")
	public @ResponseBody String testLogin(Authentication authentication,
			@AuthenticationPrincipal PrincipalDetails userDetails) { 
		//DI(의존성 주입) Authentication에 PrincipalDetail에 넣었던 User객체를 다시 조회
		// 두가지 방법이 있는데
		// 첫번째는 Authentication에서 DownCasting 하는 방법
		System.out.println("/test/login ==================");
		PrincipalDetails principalDetails =(PrincipalDetails)authentication.getPrincipal();
		System.out.println("getPrincipal: " + principalDetails);
		System.out.println("authentication:"+principalDetails.getUser());
		// 두번째는 @AuthenticationPrincipal에서 바로 getUser
		System.out.println("UserDetails:"+userDetails);
		System.out.println("UserDetails:"+userDetails.getUser());
		return "세션 정보 확인하기";
	}
	
	@GetMapping("/test/oauth/login")
	public @ResponseBody String testOauthLogin(Authentication authentication,
			@AuthenticationPrincipal OAuth2User oauth) { 
		
		System.out.println("/test/login ==================");
		OAuth2User oAuth2User =(OAuth2User)authentication.getPrincipal();
		System.out.println("authentication:"+oAuth2User.getAttributes());
		
		System.out.println("oauth:"+oauth.getAttributes());
		
		return "OAuth 세션 정보 확인하기";
	}
	@GetMapping({"","/"})
	public String index() {
		// 머스태치 기본폴더 src/main/resources/
		// 뷰리졸버 설정 : templates (prefix), .mustache(suffix)
		return "index"; // src/main/resources/templates/index.mustache 를 찾게돼있음
	}
	
	// OAuth 로그인을 해도 PrincipalDetails
	// 일반 로그인해도 PrincipalDetails
	@GetMapping("/user")
	public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
		System.out.println("principalDetails: "+principalDetails.getUser());
		return "user";
	}	
	
	@GetMapping("/admin")
	public @ResponseBody String admin() {
		return "admin";
	}	
	
	@GetMapping("/manager")
	public @ResponseBody String manager() {
		return "manager";
	}	

	@GetMapping("/loginForm")
	public String loginForm() {
		return "loginForm";
	}

	@GetMapping("/joinForm")
	public String joinForm() {
		return "joinForm";
	}
	
	@PostMapping("/join")
	public String join(User user) {
		System.out.println(user);
		user.setRole("ROLE_USER");
		String rawPassword = user.getPassword();
		String encPassowrd = bCryptPasswordEncoder.encode(rawPassword);
		user.setPassword(encPassowrd);
		user.setCreateDate(new Timestamp(System.currentTimeMillis()));
		userRepository.save(user);
		return "redirect:/loginForm";
	}
	
	@PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
	@GetMapping("/data")
	public @ResponseBody String data() {
		return "데이터 정보";
	}
	
}
