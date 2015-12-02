package com.mobile.future.util;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * 这个类在tomcat启动的时候自动运行，因此可以自动调用线程池
 */
public class MyServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5143680511056923397L;

	/*
	 *该方法调用线程池，用来启动定时器
	 */
	 public void init() throws ServletException {
		  //把要做的事写到这里 
//		 System.out.println("自动启动的方法");
		 TimeUtil.startRun();
		 try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		 FiveSecData.startRun();
		  }
}
