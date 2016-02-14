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

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.JdbcTemplate;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.sql.DataSource;

/**
 * 载入spring插件
 */
public class SpringKit implements ApplicationContextAware {
	private static ApplicationContext appContext = null;
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		appContext = applicationContext;
	}
	public static ApplicationContext getApplicationContext() {
		return appContext;
	}
	public static JdbcTemplate getJdbcTemplateByDataSource(String dataSourceName) {
		ComboPooledDataSource dataSource= (ComboPooledDataSource)appContext.getBean(dataSourceName);
		return  new JdbcTemplate(dataSource);
	}
	public static JedisPool getJedisPool(String jedisPool) {
		return(JedisPool)appContext.getBean(jedisPool);
	}
}
