package org.frame.repository.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
	
	public static boolean TRUE = true;
	
	public static boolean FALSE = false;
	
	String name() default "";
	
	String scheme() default "";
	
	String user() default "";
	
	boolean simple() default true;

}
