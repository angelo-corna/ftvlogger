package com.software.trentanove.ftvRESTful.config;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class WebInitializer extends AbstractAnnotationConfigDispatcherServletInitializer  {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    protected Class[] getRootConfigClasses() {
        return new Class[] { WebConfig.class};
    }
  
	@SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected Class[] getServletConfigClasses() {
        return null;
    }
  
    @Override
    protected String[] getServletMappings() {
        return new String[] { "/" };
    }
}