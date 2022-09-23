package com.cos.security1.config.auth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.cos.security1.model.User;

import lombok.Data;

// 시큐리티가 /login 주소 요청이 오면 낚아채서 로그인을 진행시키는데, 로그인을 진행이 완료가 되면 시큐리티가 session을 만들어준다.
// 세션 공간은 똑같은데 시큐리티가 자신만의 세션 공간을 가진다.  (Security ContextHolder)라는 키 값에다가 세션 정보를 저장해준다.
// 세션에 들어갈 수 있는 어떤 정보는 시큐리티가 가질 수 있는 세션에 들어갈 수 있는 오브젝트가 정해져잇다.
// 오브젝트는 Authentication 타입의 객체여야한다. 이 Authentication 안에는 User정보가 있어야한다.
// 이것도 클래스가 정해져 있다. User오브젝트타입 => UserDetails 타입의 객체다.

// Security Session 에 세션 정보를 저장 여기 들어갈 수 있는 객체가 => Authentication 객체다. 왜냐 그냥 이게 규칙이다. => 이 User정보는 UserDetails 타입이어야한다. (UserDetails 객체)
// 어떻게 꺼낼 수 있을가?
// implements UserDetials하면 PrincipalDetails가 UserDetails와 같은 객체가 된다. 

@Data
public class PrincipalDetails implements UserDetails, OAuth2User {
	
	private User user; //콤포지션. 우리 유저 객체는 User가 가지고 있으니까 이걸 먼저 선언.
	private Map<String, Object> attributes;
	
	//일반 로그인
	public PrincipalDetails(User user) {
		this.user = user;
	}
	
	//OAuth 로그인 
	public PrincipalDetails(User user, Map<String,Object> attributes) {
		this.user = user;
		this.attributes = attributes;
	}
	
	
	//해당 User의 권한을 리턴하는 곳이다!! 해당 User의 권한은 user.getRole => 이거의 리턴타입은 String이다. 그래서 타입을 만들어줘야한다.
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> collect = new ArrayList<>(); 
		collect.add(new GrantedAuthority() {  

			@Override
			public String getAuthority() {
				return user.getRole();       
				
	// 권한이 String타입이다. ArrayList는 Collection의 자식이다. collect안에 add를 해서 grantedAuthority 타입을 넣는다. 그래서 new GrantedAuthority 타입을 엔터쳐서 만든다.
	// 그리고 String을 리턴해준다. 그리고 collect를 리턴
			}
		});
		return collect;
	}
	

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;  // 계정 만료됐니? 아니니까 트루
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;  // 계정 잠김?
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true; // 계정 만료됐니?
	}

	@Override
	public boolean isEnabled() {
		// 우리 사이트에서 1년동안 회원이 로그인을 안하면 휴먼 계정으로 하기로했다면, User.java에 이런게 있어야한다.
		// private Timestamp loginDate; => 로그인 할 때마다 날짜를 보여준다. 그럼 
		// 여기서 user.getLoginDate(); 를 가져온다. 이 날짜를 들고와서 
		// 현재시간 - 로그인시간 => 1년을 초과하면 return을 false;로 설정하면된다.
		return true; // 계정 사용중이니?
	}


	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}


	@Override
	public String getName() {
		return null;
	}

	
	//=>PrincipalDetails를 여기까지 해서, Authentication객체에 넣을 수 있다. 여기 넣을 수 있는 방법은 Auth에서 PrincipalDetailsService 만들기
}
