package com.cos.security1.config.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;

//PrincipalDetails는 나중에 Service를 강제로 new로 띄울거니까 걱정안해도 된다. 얘만 일단 Service 어노테이션달아라. 이게 IoC에 등록되서 loadUserByName이 호출된다. 
// 시큐리티 설정에서 loginProcessingUrl("/login");을 걸어놨다. /login 요청이 오면 자동으로 UserDetailsService 타입으로 IoC되어있는 loadUserByUsername 함수가 실행된다. =>규칙
// Autowired 해서 UserRepository 등록하고. 이 이름으로 User가 있는지를 확인한다.
@Service 
public class PrincipalDetailsService implements UserDetailsService{

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User userEntity = userRepository.findByUsername(username);
		if(userEntity != null) {
			return new PrincipalDetails(userEntity); // 리턴 값이 session에 쏙 들어간다. 이럼 로그인이 완료가 된다.
		}
		//username이 null이 아니면 username이 있다는 것 그래서 있다면 userEntity내용을 리턴.
		//시큐리티 session = Authentication 타입 = UserDetails . 지금 리턴할때 Authentication내부에 쏙 들어간다. 그리고 이 값은 또 session내부에 속 들어가.
		return null;
	}

}
