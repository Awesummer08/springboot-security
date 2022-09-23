package com.cos.security1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;

@Controller // view를 리턴하겠다.
public class IndexController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@GetMapping("/test/login")
	public @ResponseBody String testLogin(
			Authentication authentication,
			@AuthenticationPrincipal PrincipalDetails userDetails) { //DI 의존성주입
		System.out.println("/test/login===================");
		PrincipalDetails principalDetails = (PrincipalDetails)authentication.getPrincipal();
		System.out.println("authentication:"+principalDetails.getUser());
		
		System.out.println("userDetails: "+userDetails.getUser());
		return "세션 정보 확인하기";
	}
	
	@GetMapping("/test/oauth/login")
	public @ResponseBody String testOAuthLogin(
			Authentication authentication,
			@AuthenticationPrincipal OAuth2User oauth) { //DI 의존성주입
		System.out.println("/test/oauth/login===================");
		OAuth2User oauth2User = (OAuth2User)authentication.getPrincipal();
		System.out.println("authentication: "+oauth2User.getAttributes());
		System.out.println("oauth2User: "+oauth.getAttributes());
		
		return "oauth 세션 정보 확인하기";
	}

	@GetMapping({"", "/"})
	public String index() {
		// 머스테치 기본폴더 src/main/resources/
		// 뷰리졸버 설정:templates(prefix), mustache(suffix) 생략가능
		return "index";   //src/main/resources/templates/index.mustache 경로로 찾는다. 
	}
	
	
	// OAuth 로그인을 해도 PrincipalDetails로 받고 일반로그인을 해도 PrincipalDetails로 받을 수 있다. 
	@GetMapping("/user")
	public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
		System.out.println("principalDetails:"+principalDetails.getUser());
		return "user";
	}
	
	@GetMapping("/manager")
	public @ResponseBody String manager() {
		return "manager";
	}
	
	@GetMapping("/admin")
	public @ResponseBody String admin() {
		return "admin";
	}
	
	@GetMapping("/loginForm")  //security가 이걸 낚아챈다. 다른건 responsebody를 달아놔서 그대로 user 이런식으로 리턴되는데!!
	public String loginForm() {
		return "loginForm";
	}
	
	@GetMapping("/joinForm")
	public String joinForm() {
		return "joinForm";
	}
	
	@PostMapping("/join")
	public String join(User user) {
		//회원가입을 시킬때 user의 Role이 없다. 그래서 user의 Role은 ROLE_USER로 강제로 넣어줘라.
		System.out.println("회원가입 진행 : " + user);
		user.setRole("ROLE_USER");
		String rawPassword = user.getPassword();
		String encPassword = bCryptPasswordEncoder.encode(rawPassword);
		user.setPassword(encPassword);   //인코딩된 패스워드를 넣고 save해줘라.
		
		userRepository.save(user); //createtime도 자동으로 된다. 그래서 회원가입이 잘된다. 하지만 이렇게 되면 비밀번호가 1234. 시큐리티로 로그인 불가능. 이유는 패스워드가 암호화 안 되서. 패스워드 암호화
		//하자. SECURITYCONFIg로 들어가서 하면 된다. override 하면된다. => Bean으로 BCryptPasswordEncoder IoC에 등록 => SecurityConfig 등록하고 상단 Autowired하고 위에 적기
		
		return "redirect:/loginForm";  //회원가입이 정상적으로 되면 redirect 로그인 폼. redirect를 붙이면 /loginForm이라는 함수를 붙인다.
	}
	
	@Secured("ROLE_ADMIN")
	@GetMapping("/info")
	public @ResponseBody String info() {
		return "개인정보";
	}
	
	@PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
	@GetMapping("/data")
	public @ResponseBody String data() {
		return "데이터정보";
	}
}
