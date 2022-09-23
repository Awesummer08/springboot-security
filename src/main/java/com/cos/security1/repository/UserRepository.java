package com.cos.security1.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cos.security1.model.User;


//JpaRepository가 기본적으로 CRUD를 들고 있다. @Repository라는 어노테이션이 없어도 IoC가 된다. 이유는 JpaRepository를 상속했기 때문에 Bean으로 등록되고
// 필요한곳에서 AutoWised를 어노테이션해주면 된다.
// 타입은 User고 User의 Primary Key = code가 Integer니까 Integer해주면 된다.
public interface UserRepository extends JpaRepository<User, Integer>{
	//findBy까지는 규칙이다. 그리고 -> Username은 문법이다.
	// select * from user where username = 1?  이 물음표에는 파라미터가 들어온다.
	public User findByUsername(String username);
	
	// select * from user where email = ?  이런식으로 호출되는거다. 예시.
	// public User findByEmail();
	// 이게 궁금하면 Jpa query method 검색하기 
}
