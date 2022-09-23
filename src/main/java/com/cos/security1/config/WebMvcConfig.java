package com.cos.security1.config;

import org.springframework.boot.web.servlet.view.MustacheViewResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration // 자바 파일로 등록하기위해서    
public class WebMvcConfig implements WebMvcConfigurer{
	// WebMvcConfigurer을 임플리먼츠
	
	@Override    //이걸 오버라이딩해서. 내부구성을 resolver 4개로 바꿈 => 해당 viewresolver mustache를 재설정할 수 있다. utf-8
	public void configureViewResolvers(ViewResolverRegistry registry) {
		MustacheViewResolver resolver = new MustacheViewResolver();
		resolver.setCharset("UTF-8");
		resolver.setContentType("text/html; charset=UTF-8");
		resolver.setPrefix("classpath:/templates/");
		resolver.setSuffix(".html");  // .html 파일을 만들어도 mustache로 인식한다.
		
		registry.viewResolver(resolver); //마지막으로 registry로 viewresolver를 등록해준다. 
		
	}
}