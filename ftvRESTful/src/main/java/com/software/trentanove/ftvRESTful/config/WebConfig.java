package com.software.trentanove.ftvRESTful.config;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jndi.JndiTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { "com.software.trentanove.ftvRESTful" })
public class WebConfig implements WebMvcConfigurer {
 
	@Autowired
	DataSource dataSource;
	 
	@Bean
	public NamedParameterJdbcTemplate geNamedParameterJdbcTemplate(){
		return new NamedParameterJdbcTemplate(dataSource);
	}
	 
	@Bean
	public DataSource getDataSource() throws NamingException{
		JndiTemplate jndiTemplate = new JndiTemplate();
		DataSource dataSource = (DataSource) jndiTemplate.lookup("java:comp/env/jdbc/ftvdb");
		return dataSource;
	}
 
}