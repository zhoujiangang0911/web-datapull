package com.mobile.future.util;

import java.util.Timer;
import java.util.TimerTask;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

import com.mobile.future.action.data.StatisticsAction;
import com.mobile.future.service.data.IDataService;

/*
 *这个类只有一个作用，统计五秒钟更新一次的数据到内存中 
 */
public class FiveSecData implements Runnable {

	static String configFileName = "mobile-future-Service_applicationContext.xml";
	static BeanFactory factory = new XmlBeanFactory(new ClassPathResource(configFileName));
	static Object service = factory.getBean("dataServiceImpl");
	StatisticsAction sa = new StatisticsAction();
	private IDataService dataServiceImpl;

	/*
	 * 多线程类中的run方法
	 */
	public void run() {
		if (dataServiceImpl == null) {
			if (service != null) {
				dataServiceImpl = (IDataService) service;
				sa.setDataServiceImpl(dataServiceImpl);
			}
		}
		sa.checkFiveSecData();
	}
	
	protected static void startRun() {
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			public void run() {
				//启一个线程
				Thread thread = new Thread(new FiveSecData());
				thread.start();
			}
		};
		timer.scheduleAtFixedRate(task, 0, 5000);
	}


}
