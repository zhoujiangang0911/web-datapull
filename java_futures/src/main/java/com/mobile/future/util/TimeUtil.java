package com.mobile.future.util;

import java.util.Timer;
import java.util.TimerTask;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

import com.mobile.future.action.data.DataAction;
import com.mobile.future.service.data.IDataService;

/**
 * 定时器类，用来每秒取数据
 */
public class TimeUtil implements Runnable {

	static String configFileName = "mobile-future-Service_applicationContext.xml";
	static BeanFactory factory = new XmlBeanFactory(new ClassPathResource(configFileName));
	static Object service = factory.getBean("dataServiceImpl");
	DataAction da = new DataAction();
	private IDataService dataServiceImpl;

	/*
	 * 多线程类中的run方法
	 */
	public void run() {
		if (dataServiceImpl == null) {
			if (service != null) {
				dataServiceImpl = (IDataService) service;
				da.setDataServiceImpl(dataServiceImpl);
			}
		}
		da.addData();
	}
	
	protected static void startRun() {
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			public void run() {
				//启一个线程
				Thread thread = new Thread(new TimeUtil());
				thread.start();
			}
		};
		// timer.scheduleAtFixedRate(task, new Date(),500);
		timer.scheduleAtFixedRate(task, 0, 500); // 这句话的含义是0秒后启动 每次间隔0.9秒再启动
	}


}
