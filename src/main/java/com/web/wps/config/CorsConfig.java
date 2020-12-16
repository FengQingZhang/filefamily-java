package com.web.wps.config;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class CorsConfig extends WebMvcConfigurerAdapter {
	
	private CorsConfiguration buildconfig(){
		CorsConfiguration corsConfig= new CorsConfiguration();
		corsConfig.addAllowedOrigin("*");
		corsConfig.addAllowedHeader("*");
		corsConfig.addAllowedMethod("*");
		corsConfig.addAllowedHeader("Authorization");
		return corsConfig;
	}
	
	@Bean
	public CorsFilter corsFilter(){
		return new CorsFilter(new CorsConfigurationSource() {
			
			@Override
			public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
				// TODO Auto-generated method stub
				CorsConfiguration corsConfig= new CorsConfiguration();
				corsConfig.addAllowedOrigin("http://localhost:8080");
				corsConfig.addAllowedHeader("*");
				corsConfig.addAllowedMethod("*");
				corsConfig.addAllowedHeader("Authorization");
				return corsConfig;
			}
		});
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		// TODO Auto-generated method stub
		registry.addMapping("/**")
		        .allowedOrigins("*")
		        .allowCredentials(true)
		        .allowedMethods("GET","POST","DELETE","PUT")
		        .maxAge(3600);
	}
	
}
