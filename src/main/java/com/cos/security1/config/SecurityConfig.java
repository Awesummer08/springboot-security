package com.cos.security1.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.cos.security1.config.oauth.PrincipalOauth2UserService;


@Configuration // 일단 얘가 메모리에 떠야하니까 붙여준다.
@EnableWebSecurity // 활성화 시키려고 스프링 시큐리티 필터가 스프링 필터체인에 등록이 된다.
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private PrincipalOauth2UserService principalOauth2UserService;
	
	@Bean //빈으로 등록하면 해당 메서드의 리턴되는 오브젝트를 IoC로 등록해준다. 그럼 어디서든 사용할 수 있다. 다시 Controller로 가자! Autowired하자
	public BCryptPasswordEncoder encodePwd() {
		return new BCryptPasswordEncoder();
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		http.authorizeRequests()
			.antMatchers("/user/**").authenticated() //user 경로로 오면 인증이 필요하다. 인증만 되면 들어갈 수 있는 주주
			.antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
			.antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")		
			.anyRequest().permitAll()     // 위 3개 경로 이외에는 전부 잘 가진다. 위 3개 경로에는 403이 뜬다. Security config 파일이 작동안해서 login 해도 그냥 login으로 간다. 
			.and()
			.formLogin()
			.loginPage("/loginForm") //이걸 해주기 때문에 user manager admin 상관없이 login 페이지로 이동 그래서 login 페이지를 예쁘게 꾸며주면 좋다. 
			.loginProcessingUrl("/login")  // /login 주소가 호출이 되면 시큐리티가 낚아채서 대신 로그인을 진행해준다. 컨트롤러에 /login을 안 만들어줘도 된다.
			.defaultSuccessUrl("/")  //로그인이 완료되면 메인페이지로 가게할거다. 그래서 /  => Auth.PrincipalDetails로 가기 
			.and()
			.oauth2Login()
			.loginPage("/loginForm")
			.userInfoEndpoint()
			.userService(principalOauth2UserService);  //구글 로그인 완료된 후 후처리가 필요함.
			
	}
	
}
