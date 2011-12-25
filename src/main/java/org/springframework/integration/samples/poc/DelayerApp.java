package org.springframework.integration.samples.poc;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DelayerApp {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		new ClassPathXmlApplicationContext("META-INF/spring/integration/delay.xml");
		System.in.read();
	}

}
