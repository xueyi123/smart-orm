package com.iih5.smartorm.kit;
/*
 * Copyright 2016 xueyi (1581249005@qq.com)
 *
 * The SmartORM Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 载入spring插件
 */
public class SpringKit implements ApplicationContextAware {
	private static ApplicationContext appContext = null;
	public static void init(ApplicationContext applicationContext) {
		if (appContext==null){
			appContext=  applicationContext;
		}
		return ;
	}
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		appContext = applicationContext;
	}
	public static ApplicationContext getApplicationContext() {
		return appContext;
	}
	public static <T> T getBean(String arg) {
		return (T)appContext.getBean(arg);
	}
	public static <T> T getTBean(Class<T> t) {	
		return appContext.getBean(t);
	}



}
