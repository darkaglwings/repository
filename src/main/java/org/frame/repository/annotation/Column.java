package org.frame.repository.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
	
	public static boolean TRUE = true;
	
	public static boolean FALSE = false;
	
	String value() default "";
	
	String desc() default "";
	
	String name() default "";
	
	String type() default "";
	
	boolean primary() default false;
	
	boolean nullable() default true;
	
	int length() default 4000;
	
	int precision() default 0;
	
	int scale() default 0;
	
}
