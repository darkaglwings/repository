package org.frame.repository.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Reference {

	public static String DELETE = "delete";
	
	public static String INSERT = "insert";
	
	public static String UPDATE = "update";
	
	public static boolean TRUE = true;
	
	public static boolean FALSE = false;
	
	String[] field() default "";
	
	String[] reference() default "";
	
	String[] features() default "";
	
	String name() default "";
	
	String scheme() default "";
	
	String user() default "";
	
	String wrapper() default "";
	
	String[] sync() default "";
	
	String value() default "";
	
	boolean lazy() default true;
	
}
