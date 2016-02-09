package com.iih5.smartorm.model;
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
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringContext {
	private static SpringContext install =new SpringContext();
	public static SpringContext getInstace() {
		return install;
	}
	
	private ApplicationContext ctx = null;
	private SpringContext() {
		String path1="spring.xml";
		this.ctx = new ClassPathXmlApplicationContext(new String[]{path1});
	}

	@SuppressWarnings("unchecked")
	public <T> T getBean(String arg) {
		return (T)this.ctx.getBean(arg);
	}
	public <T> T getBean(Class<T> t) {
		return (T)this.ctx.getBean(t);
	}
	public ApplicationContext getCtx() {
		return this.ctx;
	}
	public static <T> T getTBean(Class<T> t) {	
		return SpringContext.getInstace().getBean(t);
	}
}
