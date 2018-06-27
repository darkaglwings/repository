package org.frame.repository.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Foreign {
	
	public static String DELETE = "delete";
	
	public static String INSERT = "insert";
	
	public static String SELECT = "select";
	
	public static String UPDATE = "update";
	
	public static boolean TRUE = true;
	
	public static boolean FALSE = false;
	
	String[] id() default "";
	
	String[] reference() default "";
	
	String name() default "";
	
	String scheme() default "";
	
	String user() default "";
	
	String[] sync() default "";
	
	boolean lazy() default true;
}
