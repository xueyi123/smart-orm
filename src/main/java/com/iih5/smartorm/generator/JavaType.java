package com.iih5.smartorm.generator;

import java.util.HashMap;
import java.util.Map;

public class JavaType {

	private Map<String, String> strToType = new HashMap<String, String>() {{
		
		// varchar, char, enum, set, text, tinytext, mediumtext, longtext
		put("java.lang.String", String.class.getName());

		// int, integer, tinyint, smallint, mediumint
		put("java.lang.Integer", Integer.class.getName());

		// bigint
		put("java.lang.Long", Long.class.getName());

		// date, year
		put("java.sql.Date", java.sql.Date.class.getName());

		// real, double
		put("java.lang.Double", Double.class.getName());

		// float
		put("java.lang.Float", Float.class.getName());

		// bit
		put("java.lang.Boolean", Boolean.class.getName());
		
		// time
		put("java.sql.Time", java.sql.Time.class.getName());
		
		// timestamp, datetime
		put("java.sql.Timestamp", java.sql.Timestamp.class.getName());
		
		// decimal, numeric
		put("java.math.BigDecimal", java.math.BigDecimal.class.getName());
		
		// unsigned bigint
		put("java.math.BigInteger", java.math.BigInteger.class.getName());
		
		// binary, varbinary, tinyblob, blob, mediumblob, longblob
		// qjd project: print_info.content varbinary(61800);
		put("[B", byte[].class.getName());
	}};
	
	public String getType(String typeString) {
		return strToType.get(typeString);
	}
}


