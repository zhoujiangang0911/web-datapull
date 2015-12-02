package com.mobile.future.util;


import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public final class CustomBeanFactory {

	public static ApplicationContext getContext() {
		final String[] applicationXML = { "mobile-future-Service_applicationContext.xml" };
		ApplicationContext context = getSpringContext(applicationXML);
		return context;
	}

	public static ApplicationContext getSpringContext(String[] paths) {
		return new ClassPathXmlApplicationContext(paths);

	}
}
