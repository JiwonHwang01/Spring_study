package com.example.security1.config.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.example.security1.config.auth.PrincipalDetails;
import com.example.security1.model.User;
import com.example.security1.repository.UserRepository;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService{

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	// 구글로부터 받은 userRequest 데이터에 대한 후처리함수
	// 함수 종료시 @AuthentiationPrincipal 어노테이션이 만들어진다.
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		/*
 		System.out.println("getClientRegistration:"+userRequest.getClientRegistration()); //어떤 OAuth로 로그인했는지 확인 가능
		System.out.println("UserRequest:"+userRequest.getClientRegistration()); //어떤 OAuth로 로그인했는지 확인 가능
		System.out.println("TokenValue:"+userRequest.getAccessToken().getTokenValue());
		*/

		
		OAuth2User oAuth2User = super.loadUser(userRequest);
		// 구글 로그인 버튼 -> 구글 로그인 창 -> 로그인을 완료 -> code를 리턴(OAuth-client라이브러리) -> AccessTok 요청
		// userRequest 정보 -> loadUser함수 -> 회원 프로필 받아줌
		// System.out.println("OAuth2User:"+oAuth2User.getAttributes());
		
		String provider = userRequest.getClientRegistration().getClientId(); //google
		String providerId = oAuth2User.getAttribute("sub");
		String username = provider+"_"+providerId;
		String password = bCryptPasswordEncoder.encode("JiBat");
		String email = oAuth2User.getAttribute("email");
		String role = "ROLE_USER";
		
		User userEntity = userRepository.findByUsername(username);
		
		if(userEntity == null) {
			userEntity = User.builder()
					.username(username)
					.password(password)
					.email(email)
					.role(role)
					.provider(provider)
					.providerId(providerId)
					.build();
			userRepository.save(userEntity);
				
		}
		// oAuth로그인을  하면 user와 attribute를 가지고 Authentication에 들어감
		return new PrincipalDetails(userEntity,oAuth2User.getAttributes());
	}
}
